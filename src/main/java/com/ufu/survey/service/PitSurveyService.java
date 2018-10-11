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
import com.ufu.bot.tfidf.TfIdf;
import com.ufu.bot.tfidf.ngram.NgramTfIdf;
import com.ufu.bot.to.BucketOld;
import com.ufu.bot.to.Comment;
import com.ufu.bot.to.Evaluation;
import com.ufu.bot.to.Experiment;
import com.ufu.bot.to.ExternalQuestion;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.Rank;
import com.ufu.bot.to.RelatedPost;
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
	
	@Value("${phaseNumber}")
	public Integer phaseNumber;  
	
	@Value("${section}")
	public Integer section;  
	
	@Value("${internalSurveyRankListSize}")
	public Integer internalSurveyRankListSize;  
	
	@Value("${externalSurveyRankListSize}")
	public Integer externalSurveyRankListSize;  
	
	@Value("${metricsGenerationRankListSize}")
	public Integer metricsGenerationRankListSize;  
		
	private Double avgScore;
	private Double avgReputation;
	int countRecoveredPostsFromLinks;
	
	@Autowired
	private BotComposer botComposer;
	
	private List<ExternalQuestion> externalQuestionsWithRack;
	private List<ExternalQuestion> externalQuestionsWithoutRack;
	private List<ExternalQuestion> allExternalQuestions;
	private Map<Integer,List<Integer>> withoutRackMap;
	private Map<Integer,List<Integer>> withRackMap;
	private Map<Integer,List<Integer>> allPostsIdsMap;
	private Map<Integer,List<ExternalQuestion>> nextQuestionUserMap;
	
	public PitSurveyService() {
		nextQuestionUserMap = new HashMap<>();
	}
	/*
	@Transactional(readOnly = true)
	public void loadQuestionsAndRelatedPostsToCache() {
		
		 // Build a cache with external questions and their related posts for each perspective.
		 // This cache will serve each request for nextQuestion in the survey 
		 
		externalQuestionsWithRack = findByUseRack(true);
		externalQuestionsWithoutRack = findByUseRack(false);
		allExternalQuestions = new ArrayList<>(externalQuestionsWithRack);
		allExternalQuestions.addAll(externalQuestionsWithoutRack);
		
		withoutRackMap = new HashMap<>();
		withRackMap = new HashMap<>();
		allPostsIdsMap = new HashMap<>();
		nextQuestionUserMap = new HashMap<>();
		
		for(ExternalQuestion externalQuestion: externalQuestionsWithRack) {
			List<Integer> relatedPosts = findRelatedPostsIds(externalQuestion.getId());
			withRackMap.put(externalQuestion.getFileReferenceId(), relatedPosts);
		}
		
		for(ExternalQuestion externalQuestion: externalQuestionsWithoutRack) {
			//externalQuestion.setRawQuery(externalQuestion.getId()+ " - "+externalQuestion.getRawQuery()); //presentation
			List<Integer> relatedPosts = findRelatedPostsIds(externalQuestion.getId());
			withoutRackMap.put(externalQuestion.getFileReferenceId(), relatedPosts);
		}
		allPostsIdsMap.putAll(withoutRackMap);
		allPostsIdsMap.putAll(withRackMap);
		
	}*/
	
	
	@Transactional(readOnly = true)
	public List<BucketOld> runSteps7and8(ExternalQuestion externalQuestion, Set<SoThread> allThreads) throws Exception {
		/*
		 * Step 7: Text Processing
		 * 
		 */
		BucketOld mainBucket = new BucketOld();
		long initTime; 
		
		initTime = System.currentTimeMillis();
		Set<BucketOld> bucketOlds = step7(externalQuestion,allThreads,mainBucket);
		botUtils.reportElapsedTime(initTime,"Step 7: Text Processing");
		
		/*
		 * Step 8: Relevance Calculation
		 * 
		 */
		initTime = System.currentTimeMillis();
		List<BucketOld> scoredBucketList = step8(bucketOlds,mainBucket);
		botUtils.reportElapsedTime(initTime,"Step 8: Relevance Calculation");
		
		return scoredBucketList;
		/*
		 * Step 9: Answer Generation
		 * 
		 
		initTime = System.currentTimeMillis();
		Integer maxRankSize=null;
		if(phaseNumber.equals(1) || phaseNumber.equals(2) || phaseNumber.equals(4)) {
			maxRankSize = internalSurveyRankListSize;
		}else if(phaseNumber.equals(6)) {
			maxRankSize = metricsGenerationRankListSize;
		}else if(phaseNumber.equals(7)) {
			maxRankSize = externalSurveyRankListSize;
		}
		
		List<BucketOld> trimmedRank = step9(rankedBuckets,externalQuestion.getGoogleQuery(),new ArrayList<String>(Arrays.asList(externalQuestion.getClasses().split(" "))),maxRankSize);
		botUtils.reportElapsedTime(initTime,"Step 9: Answer Generation");
		
		return trimmedRank;*/
		
		
	}
	
	
	/**
	 * Generate buckets structures representing posts
	 * @param isInternalSurveyUser 
	 * @param apis 
	 * @param googleQuery 
	 * @return A set of buckets
	 * @throws Exception 
	 */
	private Set<BucketOld> step7(ExternalQuestion nextQuestion, Set<SoThread> allThreads, BucketOld mainBucket) throws Exception {
		
		List<String> apis = new ArrayList<String>(Arrays.asList(nextQuestion.getClasses().split(" ")));
		apis.remove("");
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
		//logger.info("Main bucket: "+mainBucket.getProcessedBodyStemmedStopped()+ " - classes: "+mainBucket.getClassesNames());
		
		//Remaining buckets
		Set<BucketOld> bucketOlds = new LinkedHashSet<>();
		
		for(SoThread thread: allThreads) {
			
			List<Post> answers = thread.getAnswers();
			for(Post answer: answers) {
				boolean containCode = BotUtils.containCode(answer.getBody());
				if(!containCode) {
					//logger.info("Disconsidering in step 7 answer without code: "+answer.getId()+ " - "+answer.getBody());
					continue;
				}
				
				BucketOld bucketOld = buildAnswerPostBucket(answer);
				bucketOlds.add(bucketOld);
				
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
		
		
		return bucketOlds;
	}
	
	
	


	
	@Transactional(readOnly = true)
	public List<BucketOld> step8(Set<BucketOld> bucketOlds, BucketOld mainBucket) {
		List<BucketOld> bucketsList = new ArrayList<>(bucketOlds);
		
		/*
		 * Calculate tfidf for all terms
		 */
		List<String> bucketsTexts = new ArrayList<>();
		bucketsTexts.add(mainBucket.getProcessedBodyStemmedStopped());
		for(BucketOld bucketOld: bucketOlds){
			bucketsTexts.add(bucketOld.getProcessedBodyStemmedStopped());
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
			BucketOld postBucket = bucketsList.get(pos);
			botComposer.calculateScores(avgReputation, avgScore, tfIdfMainBucket, tfIdfOtherBucket, mainBucket, postBucket);
			pos++;
		}
		       
		
       return bucketsList;
	}

	
	/*
	 * Step 9: Rank List
	 * 
	*/ 
	@Transactional(readOnly = true)
	public List<BucketOld> step9(List<BucketOld> bucketsList, Integer maxRankSize) throws IOException {
		//showBucketsOrderByCosineDesc(bucketsList);
		//showRankedList(rankedBuckets);

	    botComposer.rankList(bucketsList);
	    
	    //now the buckets are ranked
		//List<BucketOld> trimmedRankedBuckets = new ArrayList<>();
		
		/*int pos=0;
		for(BucketOld bucket: bucketsList){
			//logger.info("Rank: "+(pos+1)+ " total Score: "+bucket.getComposedScore() +" - cosine: "+bucket.getCosSim()+ " - coverageScore: "+bucket.getCoverageScore()+ " - codeSizeScore: "+bucket.getCodeSizeScore() +" - repScore: "+bucket.getRepScore()+ " - upScore: "+bucket.getUpScore()+ " - id: "+bucket.getPostId()+ " \n "+bucket.getPresentingBody());
			//logger.info("Rank: "+(pos+1)+ " total Score: "+bucket.getComposedScore() +" - cosine: "+bucket.getCosSim()+ " - coverageScore: "+bucket.getCoverageScore()+ " - codeSizeScore: "+bucket.getCodeSizeScore() +" - repScore: "+bucket.getRepScore()+ " - upScore: "+bucket.getUpScore()+ " - id: "+bucket.getPostId());
			//botUtils.buildOutPutFile(bucket,pos+1,googleQuery,rackApis);
			trimmedRankedBuckets.add(bucket);
			pos++;
			if(pos==maxRankSize){
				break;
			}
		}
		bucketsList= null;*/
		if(maxRankSize>bucketsList.size()) {
			maxRankSize = bucketsList.size();
		}
		//trimmedRankedBuckets.addAll(bucketsList.subList(0, maxRankSize-1));
		//return trimmedRankedBuckets;
		return bucketsList.subList(0, maxRankSize);
		
	}

	private BucketOld buildAnswerPostBucket(Post post) throws Exception {
		//for tests --remove in production
		/*if(botUtils==null) {
			botUtils = new BotUtils();
		}*/
		
		BucketOld bucketOld = new BucketOld();
		bucketOld.setParentId(post.getParentId());
		bucketOld.setPostId(post.getId());
		bucketOld.setPostScore(post.getScore());
		if(post.getOwnerUserId()!=null) {
			User user = usersRepository.findOne(post.getOwnerUserId());
			bucketOld.setUserReputation(user.getReputation());
		}
				
		bucketOld.setRelationTypeId(post.getRelationTypeId());		
		
		Post parentPost = botUtils.getParentPostsCache().get(post.getParentId());
		if(parentPost==null) { 
			parentPost = postsRepository.findOne(post.getParentId());
		}
		
		
		String presentingBody = botUtils.buildPresentationBody(post.getBody(),false);
		bucketOld.setPresentingBody(presentingBody);
		presentingBody = botUtils.removeHtmlTagsExceptCode(presentingBody);
		List<String> preCodes = botUtils.getPreCodes(presentingBody);
		bucketOld.setCodes(preCodes);
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
		bucketOld.setClassesNames(classesNames);
		
		bucketOld.setProcessedBodyStemmedStopped(processedBodyStemmedStopped);
		
		return bucketOld;
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
	public List<Post> findUpVotedAnswersByQuestionId(Integer questionId) {
		//return postsRepository.findByParentIdAndPostTypeId(questionId,2,new Sort(Sort.Direction.ASC, "id"));
		return postsRepository.findByParentId(questionId,new Sort(Sort.Direction.ASC, "id"));
	}


	public User findUserById(Integer userId) {
		return usersRepository.findOne(userId);
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
		boolean isInternalSurveyUser = SurveyUser.isInternalSurveyUser(evaluation.getSurveyUserId());
		int phaseNum = 0;
		if(phaseNumber==1 || phaseNumber==2 || phaseNumber==4) {
			phaseNum = section;
		}
		
		for(int i=0; i<postsIds.size(); i++) { //lists have the same size
			RelatedPost relatedPost = relatedPostRepository.findByExternalQuestionIdAndPostId(evaluation.getExternalQuestionId(),postsIds.get(i));
			//Rank rank = rankRepository.findByRelatedPostIdAndInternalEvaluation(relatedPost.getId(),isInternalSurveyUser);
			Rank rank = rankRepository.findByRelatedPostIdAndPhase(relatedPost.getId(),phaseNum);
			Evaluation eval = new Evaluation(rank.getId(),evaluation.getSurveyUserId(),ratings.get(i),getCurrentDate());
			evaluationRepository.save(eval);
		}
		nextQuestionUserMap.get(evaluation.getSurveyUserId()).remove(0); 
		
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

	public void loadExternalQuestions() throws IOException {
		externalQuestionsWithoutRack = botUtils.readExternalQuestionsAndAnswers(false, "");
		
	}


	@Transactional(readOnly = true)
	public void loadQuestions(ToTransfer toTransfer, Boolean internalSurvey, Integer userId) {
		
		/*List<ExternalQuestion> list = externalQuestionsWithoutRack.stream()
			.peek(e -> e.setRawQuery(e.getFileReferenceId()+ " - "+e.getRawQuery()))
			.collect(Collectors.toList());*/
		/*List<ExternalQuestion> list = new ArrayList<>();
		for(ExternalQuestion externalQuestion: ex)*/
		int phaseNum = 0;
		if(phaseNumber==1 || phaseNumber==2 || phaseNumber==4) {
			phaseNum = section;
		}
		ExternalQuestion nextExternalQuestion = null;
		toTransfer.setList(externalQuestionsWithoutRack);
		
		List<ExternalQuestion> nextQuestinsUser = new ArrayList<>();
		
		for(ExternalQuestion externalQuestion: externalQuestionsWithoutRack) {
			//how many evaluations for this user ?
			externalQuestion = externalQuestionRepository.findByFileReferenceId(externalQuestion.getFileReferenceId());
			List<Evaluation> evaluations = evaluationRepository.getEvaluationsByUserExternalQuestionPhase(phaseNum,userId,externalQuestion.getId());
			if(evaluations.size()<internalSurveyRankListSize) {
				
				//checking if there is this number of posts to assess yet
				List<Post> postsForQuestion = genericRepository.findRankedPosts(externalQuestion.getId(),userId,phaseNum);
				List<Post> postsAlreadyEvaluated = genericRepository.findRankedEvaluatedPosts(externalQuestion.getId(),userId,phaseNum);
				List<Post> remaining = new ArrayList<>(postsForQuestion); 
				remaining.removeAll(postsAlreadyEvaluated);
				if(!remaining.isEmpty()) { 
					nextQuestinsUser.add(externalQuestion);
				}
				postsForQuestion = null;
				postsAlreadyEvaluated = null;
				remaining = null;
			}
		}
		
		logger.info("loadQuestions ok");
		nextQuestionUserMap.put(userId, nextQuestinsUser);
		
	}

	@Transactional(readOnly = true)
	public void loadNextQuestion(ToTransfer<ExternalQuestion> toTransfer, Integer userId) throws Exception {
		
		//for(Externa)
		
		/*List<ExternalQuestion> nextExternalQuestions;
		
		Integer previousPhase=0;
		if(phaseNumber==2) {
			previousPhase = section;
		}else if(phaseNumber==4) {
			previousPhase = 40;
		}
		
		boolean bringTwoQuestions = false;
		if(phaseNumber==8) {
			bringTwoQuestions = true;
		}*/
		ExternalQuestion nextQuestion=null;
		int phaseNum = 0;
		if(phaseNumber==1 || phaseNumber==2 || phaseNumber==4) {
			phaseNum = section;
		}
		
		boolean isInternalSurveyUser = SurveyUser.isInternalSurveyUser(userId);
		if(isInternalSurveyUser) {
			/*nextExternalQuestions = genericRepository.findNextExternalQuestionInternalSurveyUser(userId,previousPhase);
			if(!bringTwoQuestions && nextExternalQuestions.size()==2) {
				ExternalQuestion first = nextExternalQuestions.remove(0);
				nextExternalQuestions.clear();
				nextExternalQuestions.add(first);
			}
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
			nextQuestion = toTransfer.getTo();*/
			List<ExternalQuestion> nextQuestinsUser = nextQuestionUserMap.get(userId); 
			if(!nextQuestinsUser.isEmpty()) {
				nextQuestion = nextQuestinsUser.get(0);
			}
			
			  
			toTransfer.setTo(nextQuestion);
			if(nextQuestion==null) {
				toTransfer.setInfoMessage("Você completou o survey interno.");
			}
			
			if(nextQuestion!=null) {
				//List<Post> postsForQuestion = genericRepository.findRankedList(nextQuestion.getId(),userId,phaseNum);
				
				List<Post> postsForQuestion = genericRepository.findRankedPosts(nextQuestion.getId(),userId,phaseNum);
				List<Post> postsAlreadyEvaluated = genericRepository.findRankedEvaluatedPosts(nextQuestion.getId(),userId,phaseNum);
				
				postsForQuestion.removeAll(postsAlreadyEvaluated);
				toTransfer.setList4(postsForQuestion);
			}
			
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

	public List<ExternalQuestion> getAllExternalQuestions() {
		return allExternalQuestions;
	}

	/*
	public void runPhase3Old() throws Exception {
		long initTime; 
		long endTime;
						
		
		int count =1;
		for(ExternalQuestion externalQuestion: allExternalQuestions) {
			//initializeVariables();
			String questionStr = "Id: "+externalQuestion.getId()+ " - externalId: "+externalQuestion.getFileReferenceId()+ " - Gquery: "+externalQuestion.getGoogleQuery();
			logger.info("\n\n\n\n\n\n\n Processing question: "+questionStr);
			
			List<Post> answers = new ArrayList<>();
			
			List<RelatedPost> relatedPosts = relatedPostRepository.findByExternalQuestionId(externalQuestion.getId());
			for(RelatedPost relatedPost: relatedPosts) {
				Post answer = postsRepository.findOne(relatedPost.getPostId());
				answer.setRelationTypeId(relatedPost.getRelationTypeId());
				answers.add(answer);
			}
			
			SoThread thread = new SoThread(null, answers);
			Set<SoThread> threadListOneElement = new HashSet<>();
			threadListOneElement.add(thread);
			
			initTime = System.currentTimeMillis();
			List<BucketOld> rankedList = runSteps7toTheEnd(externalQuestion,threadListOneElement);
			botUtils.reportElapsedTime(initTime,"runSteps7toTheEnd for question: "+questionStr);
			
			
		}
		
	}
*/
	
	
	
	
	
}
