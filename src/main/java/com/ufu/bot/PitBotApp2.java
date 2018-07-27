package com.ufu.bot;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ufu.bot.exception.PitBotException;
import com.ufu.bot.googleSearch.GoogleWebSearch;
import com.ufu.bot.googleSearch.SearchQuery;
import com.ufu.bot.googleSearch.SearchResult;
import com.ufu.bot.service.PitBotService;
import com.ufu.bot.tfidf.TfIdf;
import com.ufu.bot.tfidf.ngram.NgramTfIdf;
import com.ufu.bot.to.Bucket;
import com.ufu.bot.to.Comment;
import com.ufu.bot.to.ExternalQuestion;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.RelatedPost.RelationTypeEnum;
import com.ufu.bot.to.SoThread;
import com.ufu.bot.to.Survey.SurveyEnum;
import com.ufu.bot.util.BotComposer;
import com.ufu.bot.util.BotUtils;
import com.ufu.survey.service.PitSurveyService;

import core.CodeTokenProvider;


@Component
public class PitBotApp2 {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PitBotService pitBotService;
	
	@Autowired
	private PitSurveyService pitSurveyService;
	
	@Autowired
	private BotUtils botUtils;
	
		
	@Autowired
	private GoogleWebSearch googleWebSearch;
		
	@Value("${phaseNumber}")
	public Integer phaseNumber;  
	
	@Value("${obs}")
	public String obs;  
	
	
	@Value("${numberOfQueriesToTest}")
	public Integer numberOfQueriesToTest;
	
	@Value("${runRack}")
	public Boolean runRack;  
		
	@Value("${runGoogleSearch}")
	public Boolean runGoogleSearch;  
	
	
	@Value("${numberOfRackClasses}")
	public Integer numberOfRackClasses;  
	
	
	@Value("${numberOfGoogleResults}")
	public Integer numberOfGoogleResults;  
	
	@Value("${internalSurveyRankListSize}")
	public Integer internalSurveyRankListSize;  
	
	@Value("${externalSurveyRankListSize}")
	public Integer externalSurveyRankListSize;  
	
	/*@Value("${APIKEY}")
	public String apiKey;
	
	@Value("${CUSTOM_SEARCH_ID}")
	public String customSearchId; */ 
	
	@Value("${shuffleListOfQueriesBeforeGoogleSearch}")
	public Boolean shuffleListOfQueriesBeforeGoogleSearch;
		
	
	
	@Value("${minTokenSize}")
	public Integer minTokenSize;
	
	/*@Value("${tagFilter}")
	public String tagFilter;  //null for all
*/	
	
	@Value("${alphaCosSim}")
	public Double alphaCosSim; 
	
	@Value("${betaCoverageScore}")
	public Double betaCoverageScore; 
	
	@Value("${gamaCodeSizeScore}")
	public Double gamaCodeSizeScore; 
	
	@Value("${deltaRepScore}")
	public Double deltaRepScore; 
	
	@Value("${epsilonUpScore}")
	public Double epsilonUpScore; 
	
	@Value("${relationType_FROM_GOOGLE_QUESTION}")
	public Double relationTypeFromGoogleQuestion; 
	
	@Value("${relationType_FROM_GOOGLE_ANSWER}")
	public Double relationTypeFromGoogleAnswer; 
	
	@Value("${relationType_RELATED_NOT_DUPE}")
	public Double relationTypeRelatedNotDupe; 
	
	@Value("${relationType_LINKS_INSIDE_TEXTS}")
	public Double relationTypeLinksInsideTexts; 
	
	@Value("${relationType_RELATED_DUPE}")
	public Double relationTypeRelatedDupe; 
	
	/*
	 * Path to a file which contains a FLAG indicating if the environment is test or production. This file (environmentFlag.properties) contains only one line with a boolean value (useProxy = true|false)
	 * If useProxy = true, the proxy is not applied for the google search engine. Otherwise, proxy is set.   
	 */
	@Value("${pathFileEnvFlag}")
	public String pathFileEnvFlag;   
	
	/*
	 * Stores the value obtained from the pathFileEnvFlag file 
	 */
	private Boolean useProxy;
	
		
	
	private List<String> queriesList;
	
	private Set<Integer> allQuestionsIds;
	private Set<Integer> allAnwersIds;
	private Set<Integer> allCommentsIds;
	
	private long initTime; 
	private long endTime;
	
	
	//protected Set<Post> postsByFilter;
	//private Bucket mainBucket;
	private List<ExternalQuestion> externalQuestions;
	private Set<SoThread> augmentedThreads;
	private String googleQuery;
	private List<String> rackApis;
	
	private Double avgScore;
	private Double avgReputation;
	
	
	
	
	
	@PostConstruct
	public void init() throws Exception {
		
		logger.info("Initializing app...");
		//initializeVariables();
		botUtils.initializeConfigs();
		getPropertyValueFromLocalFile();
		
				
		logger.info("\nConsidering parameters: \n"
				+ "\n phaseNumber: "+phaseNumber
				+ "\n pathFileEnvFlag: "+pathFileEnvFlag
				+ "\n obs: "+obs
				+ "\n numberOfQueriesToTest: "+numberOfQueriesToTest
				+ "\n shuffleListOfQueriesBeforeGoogleSearch: "+shuffleListOfQueriesBeforeGoogleSearch
				+ "\n runRack: "+runRack
				+ "\n numberOfRackClasses: "+numberOfRackClasses
				+ "\n runGoogleSearch: "+runGoogleSearch
				+ "\n useProxy: "+useProxy
				+ "\n numberOfGoogleResults: "+numberOfGoogleResults
				+ "\n minTokenSize: "+minTokenSize
				+ "\n internalSurveyRankListSize: "+internalSurveyRankListSize
				+ "\n externalSurveyRankListSize: "+externalSurveyRankListSize
				+ "\n");
				
		
		switch (phaseNumber) {
		case 1:
			initTime = System.currentTimeMillis();
			runPhase1or4or7();
			botUtils.reportElapsedTime(initTime," runPhase1 ");
			break;
		case 2:
			runPhase2or5or8();
			break;
			
		case 3:
			initTime = System.currentTimeMillis();
			runPhase3();
			botUtils.reportElapsedTime(initTime," runPhase3 ");
			break;
		case 4:
			initTime = System.currentTimeMillis();
			runPhase1or4or7();
			botUtils.reportElapsedTime(initTime," runPhase1 ");
			break;
		case 5:
			runPhase2or5or8();
			break;
		case 6:
			initTime = System.currentTimeMillis();
			runPhase6();
			botUtils.reportElapsedTime(initTime," runPhase6 ");
			break;
		case 7:
			initTime = System.currentTimeMillis();
			runPhase1or4or7();
			botUtils.reportElapsedTime(initTime," runPhase1 ");
			break;
		case 8:
			runPhase2or5or8();
			break;
		default:
			break;
		}
			

		
	}
	
	



	private void runPhase1or4or7() throws Exception {
		logWeights();
		
		/*
		 * Step 1: Question in Natural Language
		 * Read queries from a text file and insert into a list. Only 20%.
		 */	
		step1();
				
		
		if(numberOfQueriesToTest!=null && numberOfQueriesToTest>0) {
			List<ExternalQuestion> newList = new ArrayList<ExternalQuestion>(externalQuestions.subList(0, numberOfQueriesToTest));
			externalQuestions = new ArrayList<>(newList);
			
		}
		
		
		int count =1;
		for(ExternalQuestion externalQuestion: externalQuestions) {
			/*
			 * Start with only the first 50s
			 */
			/*if(externalQuestion.getUrl().contains("kodejava")) {
				continue;
			}*/
			
			initializeVariables();
			logger.info("\n\n\n\n\n\n\nProcessing new question: "+externalQuestion);
			initTime = System.currentTimeMillis();
			runSteps2to6(externalQuestion.getRawQuery()); 
			botUtils.reportElapsedTime(initTime,"runSteps2to6");
			
			externalQuestion.setGoogleQuery(googleQuery);
			externalQuestion.setUseRack(runRack);
			externalQuestion.setClasses(String.join(", ", rackApis));
									
			logger.info("saving external question and related ids for external question: "+externalQuestion.getExternalId());
			pitBotService.saveExternalQuestionAndRelatedIds(externalQuestion,botUtils.getAnswerPostsCache());
			count++;
			if(count>99) {
				break;
			}
			
			initTime = System.currentTimeMillis();
			List<Bucket> rankedList = pitSurveyService.runSteps7toTheEnd(externalQuestion,augmentedThreads);
			botUtils.reportElapsedTime(initTime,"runSteps7toTheEnd");
			logger.info("list of ranks generated, now saving ranks for question "+externalQuestion.getExternalId());
			pitBotService.saveRanks(externalQuestion,rankedList,true,phaseNumber);
			
		}
	}
	
	
	private void logWeights() {
		logger.info("Ranking with weights: "+
				"\n alphaCosSim = "+alphaCosSim +
				"\n betaCoverageScore = "+betaCoverageScore +
				"\n gamaCodeSizeScore = "+gamaCodeSizeScore +
				"\n deltaRepScore = "+deltaRepScore +
				"\n epsilonUpScore = "+epsilonUpScore+
				//relation weights
				" \n and ajuster weights of: "+
				"\n relationTypeFromGoogleQuestion = "+relationTypeFromGoogleQuestion +
				"\n relationTypeFromGoogleAnswer = "+relationTypeFromGoogleAnswer +
				"\n relationTypeRelatedNotDupe = "+relationTypeRelatedNotDupe +
				"\n relationTypeLinksInsideTexts = "+relationTypeLinksInsideTexts +
				"\n relationTypeRelatedDupe = "+relationTypeRelatedDupe 
				);
	}





	private void runPhase2or5or8() throws IOException {
		//load external questions and their related posts and store in cache
		//pitSurveyService.loadQuestionsAndRelatedPostsToCache();
		pitSurveyService.loadExternalQuestions();
	}
	

	


	private void runPhase3Old() throws Exception {
		//load external questions and their related posts and store in cache
		pitSurveyService.loadQuestionsAndRelatedPostsToCache();
		pitSurveyService.runPhase3Old();
	}


	private void runPhase3() throws Exception {
		
		
	}



	private void runPhase5() {
		// TODO Auto-generated method stub
		
	}



	private void runPhase6() {
		// TODO Auto-generated method stub
		
	}
	
	private void runPhase7() {
		// TODO Auto-generated method stub
		
	}
	

	
	private void step1() throws Exception {
		
		externalQuestions = botUtils.readExternalQuestionsAndAnswers(runRack,obs);
	
	}

	private void runSteps2to6(String query) throws Exception {
		
		
		if(runRack){
			/*
			 * Step 2: API Classes Extraction
			 * RACK  
			 *   *** Considering only the first query
			 */
			//initTime = System.currentTimeMillis();
			rackApis = step2(query);
			//botUtils.reportElapsedTime(initTime,"step2 - RACK ");
		}
		
		
		/*
		 * Step 3: Query Preparation
		 * 
		 */
		googleQuery = step3(rackApis,query);
		
		
		Set<Integer> soPostsIds = null;
		if(runGoogleSearch) {
		/*
		 * Step 4: Query Serach
		 * 
		 */
		//initTime = System.currentTimeMillis();
		soPostsIds = step4(googleQuery);
		//botUtils.reportElapsedTime(initTime,"step4 - Google Search ");
		
		}else { //static list for tests
			soPostsIds = getStaticIdsForTests();
		}
		
		
		/*
		 * Step 5: Fetch Questions Content in SO
		 * 
		 */
		//initTime = System.currentTimeMillis();
		Set<SoThread> threads = step5(soPostsIds);
		//botUtils.reportElapsedTime(initTime,"step5 - Fetch Questions Content in SO ");
		
		
		/*
		 * Step 6: Links Retrieval
		 * 
		 */
		//initTime = System.currentTimeMillis();
		augmentedThreads = step6(threads);
		//botUtils.reportElapsedTime(initTime,"step6 - Links Retrieval ");
		
		
		
		
	}

	
	

	

	/**
	 * Rack tool
	 * @param query
	 * @return List<String> representing the associated classes
	 * @throws PitBotException 
	 */
	public List<String> step2(String query){
		//list is not null. It has been already verified.
		logger.info("RACK: discovering related classes to query: "+query);
		CodeTokenProvider ctProvider = new CodeTokenProvider(query);
		List<String> apis = ctProvider.recommendRelevantAPIs();
		if(apis.size()<numberOfRackClasses){
			logger.warn("The number of retrieved APIs from RACK is lower than the number of rack classes set as paramter. Ajusting the parameter to -->"+apis.size()+" apis, returned by RACK for this query.");
			numberOfRackClasses = apis.size();
		}
		
		apis = apis.subList(0, numberOfRackClasses);
		logger.info("Finished... discored classes:"+ apis.stream().limit(numberOfRackClasses).collect(Collectors.toList()));
		return apis;
	}

	
	
	
	public String step3(List<String> apis, String query) {
		String completeQuery = "";
		
		Boolean containJavaToken = Pattern.compile(".*\\bjava\\b.*").matcher(query.toLowerCase()).find();
		
		if(!containJavaToken) {
			completeQuery += "java ";
		}
		
		completeQuery += query;
						
		if(runRack) {
			for(int i=0; i<numberOfRackClasses;i++) {
				completeQuery += " "+ apis.get(i);
			}
			completeQuery = BotUtils.removeDuplicatedTokens(completeQuery," ");
		}
		
		
		return completeQuery;
	}



	/**
	 * Google Search
	 * @param googleQuery
	 * @return Set<Integer>
	 */
	public Set<Integer> step4(String googleQuery) {
		logger.info("Initiating Google Search... Using query: "+googleQuery);
		Set<Integer> soQuestionsIds = new LinkedHashSet<>();
		
		try {
			SearchQuery searchQuery = new SearchQuery(googleQuery, "stackoverflow.com", numberOfGoogleResults);
			        //.site("https://stackoverflow.com")
			SearchResult result = googleWebSearch.search(searchQuery,useProxy);
			List<String> urls = result.getUrls();
			BotUtils.identifyQuestionsIdsFromUrls(urls,soQuestionsIds);
						
		} catch (Exception e) {
			System.out.println("Error ... "+e);
			e.printStackTrace();
		}
		
		/*for(Integer soQuestionId: soQuestionsIds) {
			logger.info("Id: "+soQuestionId);
		}*/
	
		return soQuestionsIds;
		
	}


	



	/**
	 * Fetch Questions Content in SO ( the related SO question from SO together with their answers and comments).
	 * @param soQuestionsIds
	 * @return a list of threads, where each thread is a question with all their answers and comments.
	 */
	public Set<SoThread> step5(Set<Integer> soQuestionsIds) {
		return pitBotService.assembleListOfThreads(soQuestionsIds,RelationTypeEnum.FROM_GOOGLE_QUESTION_OR_ANSWER.getId());
		
	}



	/**
	 * Links Retrieval
	 * @param threads
	 * @return an augmented list of threads
	 */
	public Set<SoThread> step6(Set<SoThread> threads) {
		logger.info("Number of threads before: "+threads.size());
		
		for(SoThread thread: threads) {
			allQuestionsIds.add(thread.getQuestion().getId());
		}
		logger.info("Number of questions before: "+allQuestionsIds.size());
		
		/*
		 * Verify if questions ids are in PostLinks table. If so, retrieve the related posts.
		 * LinkTypeId - type of link (currently either 1, linked, or 3, dupe)
		 */
		Set<Integer> allRelatedDupesIds = pitBotService.recoverRelatedQuestionsIds(allQuestionsIds,3);
		Set<Integer> otherRelatedIds = pitBotService.recoverRelatedQuestionsIds(allQuestionsIds,1);
		
		String relatedIdsStr = "";
		String otherRelatedIdsStr = "";
		for(Integer relatedId: allRelatedDupesIds) {
			relatedIdsStr+= relatedId+ " - ";
		}
		for(Integer relatedId: otherRelatedIds) {
			otherRelatedIdsStr+= relatedId+ " - ";
		}
		logger.info("Number of allRelatedDupesIds: "+allRelatedDupesIds.size()+ " \n List: "+relatedIdsStr);
		logger.info("Number of otherRelatedIdsStr: "+otherRelatedIds.size()+ " \n List: "+otherRelatedIdsStr);
				
		allRelatedDupesIds.removeAll(allQuestionsIds);
		otherRelatedIds.removeAll(allQuestionsIds);
		logger.info("Number of remaing threads to assemble: dupes: "+allRelatedDupesIds.size() + " + others: "+otherRelatedIds.size());
		
		Set<SoThread> relatedDupeThreads = pitBotService.assembleListOfThreads(allRelatedDupesIds,RelationTypeEnum.RELATED_DUPE.getId());
		Set<SoThread> otherRelatedThreads = pitBotService.assembleListOfThreads(otherRelatedIds,RelationTypeEnum.RELATED_NOT_DUPE.getId());
		
		logger.info("Number of new threads: dupes: "+relatedDupeThreads.size()+ " + others: "+otherRelatedThreads.size());
		
		threads.addAll(relatedDupeThreads);
		threads.addAll(otherRelatedThreads);
		
		logger.info("First total number of threads: "+threads.size()+" obtained only by PostLinks table. Now fetching links inside texts to assemble new threads...");
		
		
		/*
		 * Now check for links inside the texts of all posts and comments
		 */
		Set<Post> allPostsFromAllThreads = new HashSet<>();
		Set<Comment> allCommentsFromAllThreads = new HashSet<>();
		Set<Integer> soQuestionsIdsInsideTexts = new HashSet<>();
		
		for(SoThread thread: threads){
			allPostsFromAllThreads.add(thread.getQuestion());
			allCommentsFromAllThreads.addAll(thread.getQuestion().getComments());
			
			List<Post> answers = thread.getAnswers();
			for(Post answer: answers) {
				allPostsFromAllThreads.add(answer);
				allCommentsFromAllThreads.addAll(answer.getComments());
				
			}
		}
		
		List<String> textsToVerify = new ArrayList<>();
		for(Post post: allPostsFromAllThreads) {
			/*if(post.getId().equals(10975386)){
				logger.info("Good post is here: "+post);
			}*/
			textsToVerify.add(post.getBody());
		}
		for(Comment comment: allCommentsFromAllThreads) {
			textsToVerify.add(comment.getText());
		}
		
		logger.info("Number of texts to extract links from: "+textsToVerify.size());
		for(String text: textsToVerify) {
			List<String> links = botUtils.getCodeValues(BotUtils.LINK_PATTERN, text);
			BotUtils.identifyQuestionsIdsFromUrls(links, soQuestionsIdsInsideTexts);
			
		}
		logger.info("Number of recovered questions from URLs: "+soQuestionsIdsInsideTexts.size());
		
		logger.info("Number of questions ids identified inside links: "+soQuestionsIdsInsideTexts.size());
		
		Set<SoThread> newThreadsAssembledFromLinks = pitBotService.assembleListOfThreads(soQuestionsIdsInsideTexts,RelationTypeEnum.LINKS_INSIDE_TEXTS.getId());
		logger.info("Number of new Threads built from link's ids: "+newThreadsAssembledFromLinks.size());
		
		logger.info("Number of posts that are answers and had their parent thread fetched: "+pitBotService.getCountPostIsAnAnswer());
		
		threads.addAll(newThreadsAssembledFromLinks);
		logger.info("Second total number of threads: "+threads.size()+" as a result of all discovered threads...");
		
		logger.info("Total number of parent posts stored in cache: "+botUtils.getParentPostsCache().size());
		logger.info("Total number of answer posts stored in cache: "+botUtils.getAnswerPostsCache().size());
		
		return threads;
	}



	
	

	

	private void showBucketsOrderByCosineDesc(List<Bucket> bucketsList) {
		Collections.sort(bucketsList, new Comparator<Bucket>() {
		    public int compare(Bucket o1, Bucket o2) {
		        return o2.getCosSim().compareTo(o1.getCosSim());
		    }
		});
		int pos=1;
		for(Bucket bucket: bucketsList){
			logger.info("Rank: "+(pos)+ " cosine: "+bucket.getCosSim()+" id: "+bucket.getPostId()+ " -\n "+bucket.getPresentingBody());
			pos++;
			if(pos>10){
				break;
			}
		}
		
	}
	

	/*private void showRankedList(List<Bucket> rankedBuckets) {
		int pos=0;
		for(Bucket bucket: rankedBuckets){
			logger.info("Rank: "+(pos+1)+ " total Score: "+bucket.getComposedScore() +" - cosine: "+bucket.getCosSim()+ " - coverageScore: "+bucket.getCoverageScore()+ " - codeSizeScore: "+bucket.getCodeSizeScore() +" - repScore: "+bucket.getRepScore()+ " - upScore: "+bucket.getUpScore()+ " - id: "+bucket.getPostId()+ " \n "+bucket.getPresentingBody());
			buildOutPutFile(bucket,pos+1);
			pos++;
			if(pos==20){
				break;
			}
		}
	}*/

	


	

	

	/*private Set<Post> getPostsFromThreads(Set<SoThread> threads) {
		Set<Post> allPosts = new HashSet<>();
		for(SoThread thread: threads){
			
			
		}
		return allPosts;
	}*/



	


	private void getPropertyValueFromLocalFile() {
		Properties prop = new Properties();
		InputStream input = null;
		useProxy = false;
		try {

			input = new FileInputStream(pathFileEnvFlag);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			String useProxyStr = prop.getProperty("useProxy");
			if(!StringUtils.isBlank(useProxyStr)) {
				useProxy = new Boolean(useProxyStr);
			}
			String msg = "\nEnvironment property is (useProxy): ";
			if(useProxy) {
				msg+= "with proxy";
			}else {
				msg+= "no proxy";
			}
			
			logger.info(msg);
			

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	

	public void initializeVariables() {
		allQuestionsIds = new HashSet<>();
		allAnwersIds = new HashSet<>();
		allCommentsIds = new HashSet<>();
		avgScore = 0d;
		avgReputation = 0d;
		rackApis = new ArrayList<>();
		googleQuery="";
	}


	


	
	
	
	
	
	
	/*private void performStemStop() throws Exception {
		List<ProcessedPostOld> tmpList = new ArrayList<>();
		tmpList.addAll(processedPostsByFilter);
		
		List<ProcessedPostOld> somePosts = new ArrayList<>(); 
		
		int maxQuestions = 1000;
		int init=0;
		int end= maxQuestions;
		int remainingSize = tmpList.size();
		logger.info("all questions ids:  "+remainingSize);
		while(remainingSize>maxQuestions){ //this is for memory issues
			somePosts = tmpList.subList(init, end);
			pitBotService.stemStop(somePosts);
			remainingSize = remainingSize - maxQuestions;
			init+=maxQuestions;
			end+=maxQuestions;
			if(init%50000==0) {
				logger.info("stemming...:  "+init);
			}
		}
		somePosts = tmpList.subList(init, init+remainingSize);
		//remaining
		pitBotService.stemStop(somePosts);
				
		tmpList = null;
	}*/

	private Set<Integer> getStaticIdsForTests() {
		HashSet<Integer> soPostsIds = new LinkedHashSet<>();
		soPostsIds.add(10117026);
		soPostsIds.add(6416706);
		soPostsIds.add(10117051);
		soPostsIds.add(35593309);
		soPostsIds.add(11018325);
		soPostsIds.add(23329173);
		soPostsIds.add(40064173);
		soPostsIds.add(46107706);
		soPostsIds.add(8174964);
		soPostsIds.add(9740830);
		
		for(Integer id:soPostsIds) {
			logger.info("id: "+id);
		}
		
		return soPostsIds;
	}


	
	
}
