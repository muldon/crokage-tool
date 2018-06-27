package com.ufu.survey.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ufu.bot.exception.PitBotException;
import com.ufu.bot.tfidf.TfIdf;
import com.ufu.bot.tfidf.ngram.NgramTfIdf;
import com.ufu.bot.to.Bucket;
import com.ufu.bot.to.Comment;
import com.ufu.bot.to.Evaluation;
import com.ufu.bot.to.Experiment;
import com.ufu.bot.to.ExternalQuestion;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.SoThread;
import com.ufu.bot.to.Survey;
import com.ufu.bot.to.SurveyUser;
import com.ufu.bot.to.User;
import com.ufu.bot.util.AbstractService;
import com.ufu.bot.util.BotComposer;
import com.ufu.bot.util.BotUtils;
import com.ufu.survey.transfer.ToTransfer;



@Service
@Transactional
public class PitSurveyService extends AbstractService{
		
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${runRack}")
	public Boolean runRack;  
	
	@Value("${internalSurveyRankListSize}")
	public Integer internalSurveyRankListSize;  
	
	@Value("${externalSurveyRankListSize}")
	public Integer externalSurveyRankListSize;  
	
		
	private Double avgScore;
	private Double avgReputation;
	int countRecoveredPostsFromLinks;
	
	@Autowired
	private BotComposer botComposer;
	
	private List<ExternalQuestion> externalQuestionsWithRack;
	private List<ExternalQuestion> externalQuestionsWithoutRack;
	private Map<Integer,List<Integer>> withoutRackMap;
	private Map<Integer,List<Integer>> withRackMap;
	
	public PitSurveyService() {
		
	}
	
	@Transactional(readOnly = true)
	public void loadQuestionsAndRelatedPostsToCache() {
		/*
		 * Build a cache with external questions and their related posts for each perspective.
		 * This cache will serve each request for nextQuestion in the survey 
		 */
		externalQuestionsWithRack = findByUseRack(true);
		externalQuestionsWithoutRack = findByUseRack(false);
		
		withoutRackMap = new HashMap<>();
		withRackMap = new HashMap<>();
		
		for(ExternalQuestion externalQuestion: externalQuestionsWithRack) {
			List<Integer> relatedPosts = findRelatedPostsIds(externalQuestion.getId());
			withRackMap.put(externalQuestion.getExternalId(), relatedPosts);
		}
		
		for(ExternalQuestion externalQuestion: externalQuestionsWithoutRack) {
			List<Integer> relatedPosts = findRelatedPostsIds(externalQuestion.getId());
			withoutRackMap.put(externalQuestion.getExternalId(), relatedPosts);
		}
		
	}
	
	
	
	private List<Bucket> runSteps7toTheEnd(ExternalQuestion nextQuestion, boolean isInternalSurveyUser) throws Exception {
		/*
		 * Step 7: Text Processing
		 * 
		 */
		Bucket mainBucket = new Bucket();
		long initTime; 
		
		initTime = System.currentTimeMillis();
		Set<Bucket> buckets = step7(nextQuestion,isInternalSurveyUser,mainBucket);
		botUtils.reportElapsedTime(initTime,"Step 7: Text Processing");
		
		/*
		 * Step 8: Relevance Calculation
		 * 
		 */
		initTime = System.currentTimeMillis();
		List<Bucket> rankedBuckets = step8(buckets,mainBucket);
		botUtils.reportElapsedTime(initTime,"Step 8: Relevance Calculation");
		
		
		/*
		 * Step 9: Answer Generation
		 * 
		 */
		initTime = System.currentTimeMillis();
		List<Bucket> trimmedRank = step9(rankedBuckets,nextQuestion.getGoogleQuery(),new ArrayList<String>(Arrays.asList(nextQuestion.getClasses().split(" "))));
		botUtils.reportElapsedTime(initTime,"Step 9: Answer Generation");
		
		return trimmedRank;
	}
	
	
	/**
	 * Generate buckets structures representing posts
	 * @param isInternalSurveyUser 
	 * @param apis 
	 * @param googleQuery 
	 * @return A set of buckets
	 * @throws Exception 
	 */
	private Set<Bucket> step7(ExternalQuestion nextQuestion, boolean isInternalSurveyUser, Bucket mainBucket) throws Exception {
		
		List<String> apis = new ArrayList<String>(Arrays.asList(nextQuestion.getClasses().split(" ")));
		
		Set<Integer> relatedPostsIdsSet;
		List<Integer> relatedPostsIdsList;
		
		if(isInternalSurveyUser) {
			if(nextQuestion.getUseRack()) {
				relatedPostsIdsList = withRackMap.get(nextQuestion.getExternalId());
			}else {
				relatedPostsIdsList = withoutRackMap.get(nextQuestion.getExternalId());
			}
		}else { //external survey
			if(runRack) {
				relatedPostsIdsList = withRackMap.get(nextQuestion.getExternalId());
			}else {
				relatedPostsIdsList = withoutRackMap.get(nextQuestion.getExternalId());
			}
		}
		relatedPostsIdsSet = new HashSet<>(relatedPostsIdsList);
		
		Set<SoThread> allThreads = assembleListOfThreads(relatedPostsIdsSet);
		
		logger.info("Number of posts that are answers and had their parent thread fetched: "+countPostIsAnAnswer);
		
		logger.info("Second total number of threads: "+allThreads.size()+" as a result of all discovered threads for this query: "+nextQuestion);
				
		
		//Main bucket
		String presentingBody = botUtils.buildPresentationBody(nextQuestion.getGoogleQuery(),true);
		
		Set<String> classesNames = new LinkedHashSet<>();
		/*
		 * Classes present in the query are suppose to be more relevant then the ones found by RACK.
		 * The false positives like the word "How" is adjusted in the relevance calculation process where it will not belong to 
		 * the intersection of codes present between the main bucket and the code section of the post being compared.  
		 */
		botUtils.getClassesNamesForString(classesNames,presentingBody);
		classesNames.addAll(apis);
		mainBucket.setClassesNames(classesNames);
		
		String processedBodyStopped = BotUtils.removeSpecialSymbolsTitles(presentingBody);
		
		//after stemming, add classes names
		for(String className: classesNames){
			processedBodyStopped+= " "+className;
		}
		
		//stemming and stop words
		processedBodyStopped = botUtils.tokenizeStopStem(processedBodyStopped);
		
		//remove unnecessary words, used in the question but not useful to match with the answers
		processedBodyStopped = botUtils.removeUnnecessaryWords(processedBodyStopped);
		
		//Remove duplicates
		processedBodyStopped = BotUtils.removeDuplicatedTokens(processedBodyStopped," ");
		
		processedBodyStopped = StringUtils.normalizeSpace(processedBodyStopped);
		mainBucket.setProcessedBodyStemmedStopped(processedBodyStopped);
		
		//logger.info("Main bucket: "+mainBucket);
		logger.info("Main bucket: "+mainBucket.getProcessedBodyStemmedStopped()+ " - classes: "+mainBucket.getClassesNames());
		
		//Remaining buckets
		Set<Bucket> buckets = new LinkedHashSet<>();
		
		for(SoThread thread: allThreads) {
			
			List<Post> answers = thread.getAnswers();
			for(Post answer: answers) {
				Bucket bucket = buildAnswerPostBucket(answer);
				buckets.add(bucket);
				
				/*if(bucket.getPostScore()!=null) {
					avgScore      += bucket.getPostScore();
				}
				if(bucket.getUserReputation()!=null) {
					avgReputation += bucket.getUserReputation();
				}*/
			}
		}
		
		/*if(buckets.size()>0) {
			avgScore = avgScore / buckets.size();
			avgReputation = avgReputation / buckets.size();
		}*/
		
		
		return buckets;
	}
	
	
	


	

	private List<Bucket> step8(Set<Bucket> buckets, Bucket mainBucket) {
		List<Bucket> bucketsList = new ArrayList<>(buckets);
		
		/*
		 * Calculate tfidf for all terms
		 */
		List<String> bucketsTexts = new ArrayList<>();
		bucketsTexts.add(mainBucket.getProcessedBodyStemmedStopped());
		for(Bucket bucket: buckets){
			bucketsTexts.add(bucket.getProcessedBodyStemmedStopped());
		}
		
		List<Collection<String>> documents =  Lists.newArrayList(NgramTfIdf.ngramDocumentTerms(Lists.newArrayList(1,2,3), bucketsTexts));
		List<Map<String, Double>> tfs = Lists.newArrayList(TfIdf.tfs(documents));
		Map<String, Double> idfAll = TfIdf.idfFromTfs(tfs);
		
		Map<String,Double> tfsMainBucket = tfs.remove(0);
		HashMap<String, Double> tfIdfMainBucket = (HashMap)TfIdf.tfIdf(tfsMainBucket, idfAll);
		//buckets.remove(buckets.iterator().next());
		HashMap<String, Double> tfIdfOtherBucket;
		
		int pos = 0;
		
		
		for(Map<String, Double> tfsMap: tfs){
			tfIdfOtherBucket = (HashMap)TfIdf.tfIdf(tfsMap, idfAll);
			Bucket postBucket = bucketsList.get(pos);
			botComposer.calculateScores(avgReputation, avgScore, tfIdfMainBucket, tfIdfOtherBucket, mainBucket, postBucket);
			pos++;
		}
		
		
       
       botComposer.rankList(bucketsList);
		
       return bucketsList;
	}


	private List<Bucket> step9(List<Bucket> rankedBuckets,String googleQuery, List<String> rackApis) throws IOException {
		//showBucketsOrderByCosineDesc(bucketsList);
		//showRankedList(rankedBuckets);
		List<Bucket> trimmedRankedBuckets = new ArrayList<>();
		
		int pos=0;
		for(Bucket bucket: rankedBuckets){
			//logger.info("Rank: "+(pos+1)+ " total Score: "+bucket.getComposedScore() +" - cosine: "+bucket.getCosSim()+ " - coverageScore: "+bucket.getCoverageScore()+ " - codeSizeScore: "+bucket.getCodeSizeScore() +" - repScore: "+bucket.getRepScore()+ " - upScore: "+bucket.getUpScore()+ " - id: "+bucket.getPostId()+ " \n "+bucket.getPresentingBody());
			logger.info("Rank: "+(pos+1)+ " total Score: "+bucket.getComposedScore() +" - cosine: "+bucket.getCosSim()+ " - coverageScore: "+bucket.getCoverageScore()+ " - codeSizeScore: "+bucket.getCodeSizeScore() +" - repScore: "+bucket.getRepScore()+ " - upScore: "+bucket.getUpScore()+ " - id: "+bucket.getPostId());
			//botUtils.buildOutPutFile(bucket,pos+1,googleQuery,rackApis);
			trimmedRankedBuckets.add(bucket);
			pos++;
			if(pos==internalSurveyRankListSize){
				break;
			}
		}
		return trimmedRankedBuckets;
		
	}

	private Bucket buildAnswerPostBucket(Post post) throws Exception {
		//for tests --remove in production
		/*if(botUtils==null) {
			botUtils = new BotUtils();
		}*/
		
		Bucket bucket = new Bucket();
		bucket.setParentId(post.getParentId());
		bucket.setPostId(post.getId());
		bucket.setPostScore(post.getScore());
		bucket.setUserReputation(post.getUser()!=null? post.getUser().getReputation():null);
				
		Post parentPost = botUtils.getAllRetrievedPostsCache().get(post.getParentId());
		if(parentPost==null) {
			throw new PitBotException("Parent post not found... this does not make sense... ");
		}
		
		
		String presentingBody = botUtils.buildPresentationBody(post.getBody(),false);
		bucket.setPresentingBody(presentingBody);
		presentingBody = botUtils.removeHtmlTagsExceptCode(presentingBody);
		List<String> preCodes = botUtils.getPreCodes(presentingBody);
		bucket.setCodes(preCodes);
		List<String> smallCodes = botUtils.getSimpleCodes(presentingBody);
		
		//classes names of parent post are set inside getParentProcessedContentStemmedStopped method
		String parentProcessedContentStemmedStopped = getParentProcessedContentStemmedStopped(parentPost);
				
		//extract classes names
		Set<String> classesNames = botUtils.getClassesNames(smallCodes);
		classesNames.addAll(botUtils.getClassesNames(preCodes));
		String processedBodyStemmedStopped = "";
		
		for(String className: classesNames){
			processedBodyStemmedStopped+= className+ " ";
		}
		processedBodyStemmedStopped+= presentingBody;
		processedBodyStemmedStopped = botUtils.buildProcessedBodyStemmedStopped(processedBodyStemmedStopped,true);
		processedBodyStemmedStopped+= " "+parentProcessedContentStemmedStopped;
		
		classesNames.addAll(parentPost.getClassesNames());   //reinforce classes set with parent post
		bucket.setClassesNames(classesNames);
		
		bucket.setProcessedBodyStemmedStopped(processedBodyStemmedStopped);
		
		return bucket;
	}


	private String getParentProcessedContentStemmedStopped(Post parentPost) throws Exception {
		
		/*
		 * First the title: extract classes and processed text
		 */
		Set<String> classesNamesParentPost = new LinkedHashSet<>();
		String presentingTitle = botUtils.buildPresentationBody(parentPost.getTitle(),true);
		botUtils.getClassesNamesForString(classesNamesParentPost,presentingTitle);
		
		String processedTitleStopped = BotUtils.removeSpecialSymbolsTitles(presentingTitle);
		
		//after stemming, add classes names
		for(String className: classesNamesParentPost){
			processedTitleStopped+= " "+className;
		}
		
		//stemming and stop words
		processedTitleStopped = botUtils.tokenizeStopStem(processedTitleStopped);
		
		//remove unnecessary words, used in the question but not useful to match with the answers
		processedTitleStopped = botUtils.removeUnnecessaryWords(processedTitleStopped);
		
		//Remove duplicates
		processedTitleStopped = BotUtils.removeDuplicatedTokens(processedTitleStopped," ");
				
		
		/*
		 * Now the classes and processed text from the body
		 */
		
		
		String presentingBody = botUtils.buildPresentationBody(parentPost.getBody(),true);
		//extract classes names
		List<String> preCodes = botUtils.getPreCodes(presentingBody);
		List<String> smallCodes = botUtils.getSimpleCodes(presentingBody);
		
		classesNamesParentPost.addAll(botUtils.getClassesNames(smallCodes));
		classesNamesParentPost.addAll(botUtils.getClassesNames(preCodes));
		
		Set<String> classesNamesOnlyBody = botUtils.getClassesNames(smallCodes);
		classesNamesOnlyBody.addAll(botUtils.getClassesNames(preCodes));
		
		String processedBodyStemmedStopped="";
		
		//extract classes names
		parentPost.setClassesNames(classesNamesParentPost);
		
		for(String className: classesNamesOnlyBody){
			processedBodyStemmedStopped+= className+ " ";
		}
		processedBodyStemmedStopped+= presentingBody;
		processedBodyStemmedStopped = botUtils.buildProcessedBodyStemmedStopped(processedBodyStemmedStopped,true);
		
		processedBodyStemmedStopped = processedTitleStopped + " "+processedBodyStemmedStopped;
		return processedBodyStemmedStopped;
	}

	@Transactional(readOnly = true)
	public List<Post> getSomePosts() {
		return genericRepository.getSomePosts();
	}
	
	/*@Transactional(readOnly = true)
	public Map<Post, List<Post>> getQuestionsByFilters(String tagFilter,Integer limit, String maxCreationDate) {
		return genericRepository.getQuestionsByFilters(tagFilter,limit,maxCreationDate);
	}*/
	
	

	@Transactional(readOnly = true)
	public Post findPostById(Integer id) {
		return postsRepository.findOne(id);
	}

	

	@Transactional(readOnly = true)
	public Map<Integer, Set<Integer>> getAllPostLinks() {
		return genericRepository.getAllPostLinks();
	}


	/*
	public void stemStop(List<ProcessedPostOld> somePosts) throws Exception {
		for (ProcessedPostOld processedPostOld : somePosts) {
			String body = processedPostOld.getBody();
			
			if(processedPostOld.getPostTypeId().equals(1)){ //pergunta
				String query = processedPostOld.getTitle();
				
				String[] titleContent = pitSurveyUtils.separateWordsCodePerformStemmingStopWords(query,"query");
				processedPostOld.setTitle(titleContent[0] + " "+ titleContent[1]);
				
				String tagssyn = processedPostOld.getTags().replaceAll("<","");
				tagssyn = tagssyn.replaceAll(">"," ");
				tagssyn = pitSurveyUtils.tagMastering(tagssyn);												
				processedPostOld.setTagsSyn(tagssyn);		
			}
			//respostas nao possuem tag nem query				
			
			String[] bodyContent = pitSurveyUtils.separateWordsCodePerformStemmingStopWords(body,"body");
			//processedPost.setBody(bodyContent[0] + " "+ bodyContent[1]+ " "+pitSurveyUtils.formatCode(bodyContent[2]));
			processedPostOld.setBody(bodyContent[0] + " "+ bodyContent[1]);
			if (!StringUtils.isBlank(bodyContent[2])) {
				processedPostOld.setCode(bodyContent[2]);
			}
				
				
			
			//question.setTags(tags);														
			processedPostsRepository.save(processedPostOld);
					
		}
		
	}
*/


	

	public void saveExperiment(Experiment experiment) {
		experimentRepository.save(experiment);
		
	}


	

	@Transactional(readOnly = true)
	public List<Comment> getCommentsByPostId(Integer postId) {
		return commentsRepository.findByPostId(postId,new Sort(Sort.Direction.ASC, "id"));
	}


	/*
	 * Answers have postTypeId = 2
	 */
	public List<Post> findAnswersByQuestionId(Integer questionId) {
		//return postsRepository.findByParentIdAndPostTypeId(questionId,2,new Sort(Sort.Direction.ASC, "id"));
		return postsRepository.findByParentId(questionId,new Sort(Sort.Direction.ASC, "id"));
	}


	public User findUserById(Integer userId) {
		return usersRepository.findOne(userId);
	}


	public Set<Integer> recoverRelatedQuestionsIds(Set<Integer> allQuestionsIds) {
		return genericRepository.findRelatedQuestionsIds(allQuestionsIds);
	}


	public Set<Post> getAllPosts() {
		Set<Post> set = Sets.newHashSet(postsRepository.findAll());
		return set;
		
	}

	@Transactional(readOnly = true)
	public ExternalQuestion findExternalQuestionById(Integer externalQuestionId) {
		return externalQuestionRepository.findOne(externalQuestionId);
	}


	@Transactional(readOnly = true)
	public List<ExternalQuestion> getAllExternalQuestionsForActiveSurvey(Integer userId) {
		List<ExternalQuestion> externalQuestions = externalQuestionRepository.findAllExternalQuestionsForActiveSurvey(); 
		for(ExternalQuestion externalQuestion: externalQuestions) {
			
		}
		
		
		
		return externalQuestions;
	}


	

	public Survey saveSurvey(Survey survey) {
		return surveyRepository.save(survey);
		
	}


	@Transactional(readOnly = true)
	public Survey getInternalSurvey() {
		return surveyRepository.findByInternalSurvey(true);
	}


	public ExternalQuestion saveExternalQuestion(ExternalQuestion externalQuestion) {
		return externalQuestionRepository.save(externalQuestion);
		
	}


	public void saveRatings(Evaluation evaluation) {
		List<Integer> postsIds = evaluation.getPostsIds();
		List<Integer> ratings = evaluation.getRatings();
		
		for(int i=0; i<postsIds.size(); i++) { //lists have the same size
			Evaluation eval = new Evaluation(
					evaluation.getExternalQuestionId(),
					postsIds.get(i),
					evaluation.getSurveyUserId(),
					ratings.get(i),
					getCurrentDate(),
					SurveyUser.isInternalSurveyUser(evaluation.getSurveyUserId())
					);
			evaluationRepository.save(eval);
		}
		
		
	}




	public SurveyUser authenticateUser(SurveyUser surveyUser) {
		return surveyUserRepository.findByLogin(surveyUser.getLogin());
		
	}




	public SurveyUser loadUser(Integer userId) {
		return surveyUserRepository.findOne(userId);
	}

/*

	@Transactional(readOnly = true)
	public void loadQuestions(ToTransfer toTransfer, Boolean internalSurvey) {
		int randomNum;
		if(internalSurvey) {
			List<ExternalQuestion> externalQuestionsWithRack = externalQuestionRepository.findByUseRack(true);
			List<ExternalQuestion> externalQuestionsWithoutRack = externalQuestionRepository.findByUseRack(false);
						
			toTransfer.setList(externalQuestionsWithRack);
					
			List<ExternalQuestion> randomList1= new ArrayList<>();
			List<ExternalQuestion> randomList2= new ArrayList<>();
			
			//para testes - retirar
			if(externalQuestionsWithoutRack.isEmpty()) {
				externalQuestionsWithoutRack.addAll(externalQuestionsWithRack);
			}
			
			for(int i=0; i<externalQuestionsWithoutRack.size(); i++) {//lists contain the same size
				randomNum = ThreadLocalRandom.current().nextInt(1, 3);
				if(randomNum==1) {
					randomList1.add(externalQuestionsWithRack.get(i));
					randomList2.add(externalQuestionsWithoutRack.get(i));
				}else {
					randomList2.add(externalQuestionsWithRack.get(i));
					randomList1.add(externalQuestionsWithoutRack.get(i));
				}
			}
			
			toTransfer.setList2(randomList1);
			toTransfer.setList3(randomList2);
		
		}else {
			//use the winner (parameterized) perspective to show questions
			List<ExternalQuestion> externalQuestionsWithOrWithoutRack = externalQuestionRepository.findByUseRack(runRack);
			
			
		}
	}*/


	@Transactional(readOnly = true)
	public void loadQuestions(ToTransfer toTransfer, Boolean internalSurvey) {
		toTransfer.setList(externalQuestionsWithRack);
		
	}


	public void loadNextQuestion(ToTransfer<ExternalQuestion> toTransfer, Integer userId) throws Exception {
		List<ExternalQuestion> nextExternalQuestions;
		ExternalQuestion nextQuestion;
		
		boolean isInternalSurveyUser = SurveyUser.isInternalSurveyUser(userId);
		if(isInternalSurveyUser) {
			nextExternalQuestions = externalQuestionRepository.findNextExternalQuestionInternalSurveyUser(userId);
			if(nextExternalQuestions.isEmpty()) {
				toTransfer.setInfoMessage("Você completou o survey interno.");
			}else if(nextExternalQuestions.size()==1) {
				toTransfer.setTo(nextExternalQuestions.get(0));
			}else {
				int randomNum = ThreadLocalRandom.current().nextInt(1, 3);
				if(randomNum==1) { //randomly choose between the 2 elements of the list
					toTransfer.setTo(nextExternalQuestions.get(0));
				}else {
					toTransfer.setTo(nextExternalQuestions.get(1));
				}
			}
			nextQuestion = toTransfer.getTo();
			List<Bucket> rankedBuckets = runSteps7toTheEnd(nextQuestion,isInternalSurveyUser);
			toTransfer.setList4(rankedBuckets);
		}
		
		
		
		/*List<Post> relatedPosts = relatedPostRepository.findRelatedPosts(nextQuestion.getId());
		for(Post post:relatedPosts) {
			Evaluation evaluation = evaluationRepository.findByExternalQuestionIdAndPostIdAndSurveyUserId(nextQuestion.getId(),post.getId(),userId);
			post.setEvaluation(evaluation);
		}
		nextQuestion.setAnswers(relatedPosts);*/
		
		
		
	}


	@Transactional(readOnly = true)
	private List<ExternalQuestion> findByUseRack(boolean useRack) {
		return externalQuestionRepository.findByUseRack(useRack);
	}

	@Transactional(readOnly = true)
	private List<Post> findRelatedPosts(Integer externalQuestionId) {
		return relatedPostRepository.findRelatedPosts(externalQuestionId);
	}

	@Transactional(readOnly = true)
	private List<Integer> findRelatedPostsIds(Integer externalQuestionId) {
		return relatedPostRepository.findRelatedPostsIds(externalQuestionId);
	}
	
}
