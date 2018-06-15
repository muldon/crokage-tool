package com.ufu.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
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
import com.ufu.bot.to.SoThread;
import com.ufu.bot.to.Survey.SurveyEnum;
import com.ufu.bot.util.BotComposer;
import com.ufu.bot.util.BotUtils;

import core.CodeTokenProvider;


@Component
public class PitBotApp2 {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PitBotService pitBotService;
	
	@Autowired
	private BotUtils botUtils;
	
	@Autowired
	private BotComposer botComposer;
	
	@Autowired
	private GoogleWebSearch googleWebSearch;
		
	@Value("${phaseNumber}")
	public Integer phaseNumber;  
	
	@Value("${useAnswerBotQueries}")
	public Boolean useAnswerBotQueries;  
	
	@Value("${inputQueriesPath}")
	public String inputQueriesPath;  
	
	@Value("${pickUpOnlyTheFirstQuery}")
	public Boolean pickUpOnlyTheFirstQuery;
	
	@Value("${runRack}")
	public Boolean runRack;  
		
	@Value("${runGoogleSearch}")
	public Boolean runGoogleSearch;  
	
	
	@Value("${numberOfRackClasses}")
	public Integer numberOfRackClasses;  
	
	
	@Value("${numberOfGoogleResults}")
	public Integer numberOfGoogleResults;  
	
	
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
	private Bucket mainBucket;
	private List<ExternalQuestion> answerBotQuestionAnswers;
	private Set<SoThread> augmentedThreads;
	private String googleQuery;
	private List<String> rackApis;
	
	private Double avgScore;
	private Double avgReputation;
	
	private Map<Integer,Post> allRetrievedPostsCache;
	
	int countExcludedPosts;
	int countPostIsAnAnswer;
	int countRecoveredPostsFromLinks;
	
	
	@PostConstruct
	public void init() throws Exception {
		
		logger.info("Initializing app...");
		//initializeVariables();
		botUtils.initializeConfigs();
		getPropertyValueFromLocalFile();
		
				
		logger.info("\nConsidering parameters: \n"
				+ "\n phaseNumber: "+phaseNumber
				+ "\n pathFileEnvFlag: "+pathFileEnvFlag
				+ "\n useAnswerBotQueries: "+useAnswerBotQueries
				+ "\n inputQueriesPath: "+inputQueriesPath
				+ "\n pickUpOnlyTheFirstQuery: "+pickUpOnlyTheFirstQuery
				+ "\n shuffleListOfQueriesBeforeGoogleSearch: "+shuffleListOfQueriesBeforeGoogleSearch
				+ "\n runRack: "+runRack
				+ "\n numberOfRackClasses: "+numberOfRackClasses
				+ "\n runGoogleSearch: "+runGoogleSearch
				+ "\n useProxy: "+useProxy
				+ "\n numberOfGoogleResults: "+numberOfGoogleResults
				+ "\n minTokenSize: "+minTokenSize
				+ "\n");
		
		switch (phaseNumber) {
		case 1:
			initTime = System.currentTimeMillis();
			runPhase1();
			botUtils.reportElapsedTime(initTime," runPhase1 ");
			break;
		case 2:
			
			break;
			
		case 3:
			
			break;
		
		case 4:
			
			break;

		default:
			break;
		}
			

		
	}
	
	private void runPhase1() throws Exception {
		/*
		 * Step 1: Question in Natural Language
		 * Read queries from a text file and insert into a list
		 */	
		step1();
				
		
		if(pickUpOnlyTheFirstQuery) {
			ExternalQuestion answerBotQuestion = answerBotQuestionAnswers.get(0);
			answerBotQuestionAnswers = new ArrayList<>();
			answerBotQuestionAnswers.add(answerBotQuestion);
		}
		
		//RACK - retirar
		//answerBotQuestionAnswers.remove(0);
		
		for(ExternalQuestion answerBotQuestion: answerBotQuestionAnswers) {
			initializeVariables();
			logger.info("Processing query: "+answerBotQuestion);
			runSteps2to6(answerBotQuestion.getGoogleQuery()); //which is now only the raw query yet.
			
			answerBotQuestion.setGoogleQuery(googleQuery);
			answerBotQuestion.setUseRack(runRack);
			answerBotQuestion.setClasses(String.join(", ", rackApis));
			answerBotQuestion.setSurveyId(SurveyEnum.BUILDING_GROUND_TRUTH.getId());
			
			logger.info("saving external question and related ids...");
			pitBotService.saveExternalQuestionAndRelatedIds(answerBotQuestion,allRetrievedPostsCache);
		}
		
					
		
	}

	private void step1() throws Exception {
		
		answerBotQuestionAnswers = botUtils.readAnswerBotQuestionsAndAnswers();
		
		if(useAnswerBotQueries) {
			
			queriesList= answerBotQuestionAnswers.stream()
				.map(ExternalQuestion::getGoogleQuery)
				.collect(Collectors.toList());
			
		}else {
			File file = new File(inputQueriesPath);
			queriesList = new ArrayList<>();
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			    	logger.info(line);
			       queriesList.add(line);
			    }
			}
		}
		
		
		if(queriesList.isEmpty()) {
			throw new Exception("No input query found in the input file. Aborting...");
		}
		

		if(shuffleListOfQueriesBeforeGoogleSearch) {
			Collections.shuffle(queriesList);
		}
		
	}

	private void runSteps2to6(String query) throws Exception {
		
		rackApis = new ArrayList<>();
		if(runRack){
			/*
			 * Step 2: API Classes Extraction
			 * RACK  
			 *   *** Considering only the first query
			 */
			initTime = System.currentTimeMillis();
			rackApis = step2(query);
			botUtils.reportElapsedTime(initTime,"step2 - RACK ");
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
		initTime = System.currentTimeMillis();
		soPostsIds = step4(googleQuery);
		botUtils.reportElapsedTime(initTime,"step4 - Google Search ");
		
		}else { //static list for tests
			soPostsIds = getStaticIdsForTests();
		}
		
		
		/*
		 * Step 5: Fetch Questions Content in SO
		 * 
		 */
		initTime = System.currentTimeMillis();
		Set<SoThread> threads = step5(soPostsIds);
		botUtils.reportElapsedTime(initTime,"step5 - Fetch Questions Content in SO ");
		
		
		/*
		 * Step 6: Links Retrieval
		 * 
		 */
		initTime = System.currentTimeMillis();
		augmentedThreads = step6(threads);
		botUtils.reportElapsedTime(initTime,"step6 - Links Retrieval ");
		
		
		
		
	}

	
	private void runSteps7toTheEnd() throws Exception {
		/*
		 * Step 7: Text Processing
		 * 
		 */
		initTime = System.currentTimeMillis();
		Set<Bucket> buckets = step7(augmentedThreads, googleQuery, rackApis);
		botUtils.reportElapsedTime(initTime,"Step 7: Text Processing");
		
		/*
		 * Step 8: Relevance Calculation
		 * 
		 */
		initTime = System.currentTimeMillis();
		List<Bucket> rankedBuckets = step8(buckets);
		botUtils.reportElapsedTime(initTime,"Step 8: Relevance Calculation");
		
		
		/*
		 * Step 9: Answer Generation
		 * 
		 */
		initTime = System.currentTimeMillis();
		step9(rankedBuckets,googleQuery,rackApis);
		botUtils.reportElapsedTime(initTime,"Step 9: Answer Generation");
		
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
		
		for(int i=0; i<numberOfRackClasses;i++) {
			completeQuery += " "+ apis.get(i);
		}
		
		if(runRack) {
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
			identifyQuestionsIdsFromUrls(urls,soQuestionsIds);
						
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
		return assembleListOfThreads(soQuestionsIds);
		
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
		 */
		
		Set<Integer> allRelatedQuestionsIds = pitBotService.recoverRelatedQuestionsIds(allQuestionsIds);
		
		String relatedIdsStr = "";
		for(Integer relatedId: allRelatedQuestionsIds) {
			relatedIdsStr+= relatedId+ " - ";
		}
		logger.info("Number of allRelatedQuestionsIds: "+allRelatedQuestionsIds.size()+ " \n List: "+relatedIdsStr);
				
		allRelatedQuestionsIds.removeAll(allQuestionsIds);
		logger.info("Number of remaing threads to assemble: "+allRelatedQuestionsIds.size());
		
		Set<SoThread> relatedThreads = assembleListOfThreads(allRelatedQuestionsIds);
		
		logger.info("Number of new threads: "+relatedThreads.size());
		
		threads.addAll(relatedThreads);
		
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
			identifyQuestionsIdsFromUrls(links, soQuestionsIdsInsideTexts);
			
		}
		logger.info("Number of recovered questions from URLs: "+countRecoveredPostsFromLinks);
		
		logger.info("Number of questions ids identified inside links: "+soQuestionsIdsInsideTexts.size());
		
		Set<SoThread> newThreadsAssembledFromLinks = assembleListOfThreads(soQuestionsIdsInsideTexts);
		logger.info("Number of new Threads built from link's ids: "+newThreadsAssembledFromLinks.size());
		
		logger.info("Number of posts that are answers and had their parent thread fetched: "+countPostIsAnAnswer);
		
		threads.addAll(newThreadsAssembledFromLinks);
		logger.info("Second total number of threads: "+threads.size()+" as a result of all discovered threads...");
		
		logger.info("Total number of posts stored in cache: "+allRetrievedPostsCache.size());
		
		return threads;
	}



	/**
	 * Generate buckets structures representing posts
	 * @param threads
	 * @param apis 
	 * @param googleQuery 
	 * @return A set of buckets
	 * @throws Exception 
	 */
	public Set<Bucket> step7(Set<SoThread> threads, String googleQuery, List<String> apis) throws Exception {
		
		//Main bucket
		mainBucket = new Bucket();
		String presentingBody = botUtils.buildPresentationBody(googleQuery);
		
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
		
		for(SoThread thread: threads) {
			
			List<Post> answers = thread.getAnswers();
			for(Post answer: answers) {
				Bucket bucket = buildAnswerPostBucket(answer);
				buckets.add(bucket);
				
				if(bucket.getPostScore()!=null) {
					avgScore      += bucket.getPostScore();
				}
				if(bucket.getUserReputation()!=null) {
					avgReputation += bucket.getUserReputation();
				}
			}
		}
		
		if(buckets.size()>0) {
			avgScore = avgScore / buckets.size();
			avgReputation = avgReputation / buckets.size();
		}
		
		
		return buckets;
	}

	

	public List<Bucket> step8(Set<Bucket> buckets) {
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


	private void step9(List<Bucket> rankedBuckets,String googleQuery, List<String> rackApis) throws IOException {
		//showBucketsOrderByCosineDesc(bucketsList);
		//showRankedList(rankedBuckets);
		int pos=0;
		for(Bucket bucket: rankedBuckets){
			logger.info("Rank: "+(pos+1)+ " total Score: "+bucket.getComposedScore() +" - cosine: "+bucket.getCosSim()+ " - coverageScore: "+bucket.getCoverageScore()+ " - codeSizeScore: "+bucket.getCodeSizeScore() +" - repScore: "+bucket.getRepScore()+ " - upScore: "+bucket.getUpScore()+ " - id: "+bucket.getPostId()+ " \n "+bucket.getPresentingBody());
			buildOutPutFile(bucket,pos+1,googleQuery,rackApis);
			pos++;
			if(pos==20){
				break;
			}
		}
		
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

	

	public Bucket buildAnswerPostBucket(Post post) throws Exception {
		//for tests --remove in production
		/*if(botUtils==null) {
			botUtils = new BotUtils();
		}*/
		
		Bucket bucket = new Bucket();
		bucket.setParentId(post.getParentId());
		bucket.setPostId(post.getId());
		bucket.setPostScore(post.getScore());
		bucket.setUserReputation(post.getUser()!=null? post.getUser().getReputation():null);
				
		Post parentPost = allRetrievedPostsCache.get(post.getParentId());
		if(parentPost==null) {
			throw new PitBotException("Parent post not found... this does not make sense... ");
		}
		
		
		String presentingBody = botUtils.buildPresentationBody(post.getBody());
		bucket.setPresentingBody(presentingBody);
		
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
		String presentingTitle = botUtils.buildPresentationBody(parentPost.getTitle());
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
		
		
		String presentingBody = botUtils.buildPresentationBody(parentPost.getBody());
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

	

	/*private Set<Post> getPostsFromThreads(Set<SoThread> threads) {
		Set<Post> allPosts = new HashSet<>();
		for(SoThread thread: threads){
			
			
		}
		return allPosts;
	}*/



	private void setCommentsUsers(List<Comment> questionComments) {
		for(Comment comment: questionComments) {
			if(comment.getUserId()!=null) {
				comment.setUser(pitBotService.findUserById(comment.getUserId()));
			}
			//allCommentsIds.add(comment.getId());
		}
		
	}



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
		allRetrievedPostsCache = new HashMap<>();
	}


	private Set<SoThread> assembleListOfThreads(Set<Integer> soPostsIds) {
		//allQuestionsIds.addAll(soQuestionsIds);
		Set<SoThread> threads = new LinkedHashSet<>();
				
		for(Integer questionId: soPostsIds) {
			Post post = pitBotService.findPostById(questionId);
			if(post==null) {
				countExcludedPosts++;
				continue; //could have been excluded 
			}
			
			if(post.getPostTypeId().equals(2)) { 
				countPostIsAnAnswer++;
				//soPostsIds.remove(post.getParentId()); //fetch the parent only once
				if(!soPostsIds.contains(post.getParentId())) {
					post = pitBotService.findPostById(post.getParentId());
				}else {
					//parent is already present in the list and we be processed later
					continue;
				}
				
			}
			
			if(post.getOwnerUserId()!=null) {
				post.setUser(pitBotService.findUserById(post.getOwnerUserId()));
			}
			
			List<Comment> questionComments = pitBotService.getCommentsByPostId(questionId);
			setCommentsUsers(questionComments);
			post.setComments(questionComments);
			
			storeInCache(post);
			
			List<Post> answers = pitBotService.findAnswersByQuestionId(post.getId());
			
			for(Post answer: answers) {
				if(answer.getOwnerUserId()!=null) {
					answer.setUser(pitBotService.findUserById(answer.getOwnerUserId()));
				}
				List<Comment> answerComments = pitBotService.getCommentsByPostId(answer.getId());
				setCommentsUsers(answerComments);
				answer.setComments(answerComments);
				storeInCache(answer);
				//allAnwersIds.add(answer.getId());
			}
			
			
			SoThread soThread = new SoThread(post,answers);
			threads.add(soThread);
			
		}
		
		
		logger.info("Number of posts that has been excluded, or is not present in dataset because it is not a java post, or is newer than the dataset: "+countExcludedPosts);
		
		return threads;
		
	}


	
	private void identifyQuestionsIdsFromUrls(List<String> urls, Set<Integer> soQuestionsIds) {
		for(String url: urls){
			if(!url.contains("stackoverflow.com")) {
				//logger.info("Discarting URL because its is not a SO url: "+url);
				continue;
			}
			//String[] urlPart = url.split("\\/[[:digit:]].*\\/");
			Pattern pattern = Pattern.compile("\\/([\\d]+)");
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				soQuestionsIds.add(new Integer(matcher.group(1)));
				countRecoveredPostsFromLinks++;
			}
			//System.out.println(url);
			
		}
		
	}    
	
	
	public void storeInCache(Post post) {
		allRetrievedPostsCache.put(post.getId(), post);
	}

	

	public Bucket getMainBucket() {
		return mainBucket;
	}

	public void setMainBucket(Bucket mainBucket) {
		this.mainBucket = mainBucket;
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


	private void buildOutPutFile(Bucket bucket, int pos, String googleQuery, List<String> rackApis) {
		
		
	}
	
}
