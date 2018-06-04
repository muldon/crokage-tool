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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ufu.bot.googleSearch.GoogleWebSearch;
import com.ufu.bot.googleSearch.SearchQuery;
import com.ufu.bot.googleSearch.SearchResult;
import com.ufu.bot.service.PitBotService;
import com.ufu.bot.tfidf.TfIdf;
import com.ufu.bot.tfidf.VectorSpaceModel;
import com.ufu.bot.tfidf.ngram.NgramTfIdf;
import com.ufu.bot.to.Bucket;
import com.ufu.bot.to.Comment;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.ProcessedPostOld;
import com.ufu.bot.to.SoThread;
import com.ufu.bot.util.BotComposer;
import com.ufu.bot.util.BotUtils;

import core.CodeTokenProvider;


@Component
public class PitBotApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected Set<Integer> closedDuplicatedNonMastersIdsByTag;
	
	@Autowired
	private PitBotService pitBotService;
	
	@Autowired
	private BotUtils botUtils;
	
	@Autowired
	private BotComposer botComposer;
		
	
	/*@Value("${preProcess}")
	public Boolean preProcess;  //true - false
*/	
	@Value("${inputQueriesPath}")
	public String inputQueriesPath;  
	
	
	@Value("${numberOfRackClasses}")
	public Integer numberOfRackClasses;  
	
	
	@Value("${numberOfGoogleResults}")
	public Integer numberOfGoogleResults;  
	
	@Value("${shuffleListOfQueriesBeforeGoogleSearch}")
	public Boolean shuffleListOfQueriesBeforeGoogleSearch;
		
	@Value("${pickUpOnlyTheFirstQuery}")
	public Boolean pickUpOnlyTheFirstQuery;
	
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
	
	@Value("${runGoogleSearch}")
	public Boolean runGoogleSearch;
	
	
	private List<String> queriesList;
	
	private Set<Integer> allQuestionsIds;
	private Set<Integer> allAnwersIds;
	private Set<Integer> allCommentsIds;
	
	private long initTime; 
	private long endTime;
	
	
	protected Set<ProcessedPostOld> processedPostsByFilter;
	protected Set<Post> postsByFilter;
	private Bucket mainBucket;
	
	private Double avgScore;
	private Double avgReputation;
	
	@PostConstruct
	public void init() throws Exception {
		
		logger.info("Initializing app...");
		initializeVariables();
		botUtils.initializeConfigs();
		getPropertyValueFromLocalFile();
		
				
		logger.info("\nConsidering parameters: \n"
				//+ "\n preProcess: "+preProcess
				+ "\n inputQueriesPath: "+inputQueriesPath
				+ "\n numberOfRackClasses: "+numberOfRackClasses
				+ "\n numberOfGoogleResults: "+numberOfGoogleResults
				+ "\n pathFileEnvFlag: "+pathFileEnvFlag
				+ "\n useProxy: "+useProxy
				+ "\n shuffleListOfQueriesBeforeGoogleSearch: "+shuffleListOfQueriesBeforeGoogleSearch
				+ "\n minTokenSize: "+minTokenSize
				+ "\n pickUpOnlyTheFirstQuery: "+pickUpOnlyTheFirstQuery
				+ "\n");
		
		/*
		 * Do not proceed with rest of the app if the task is preprocess. 
		 */
		/*if(preProcess) {
			preProcess();
			return;
		}*/
		
		

		/*
		 * Step 1: Question in Natural Language
		 * Read queries from a text file and insert into a list
		 */	
		step1();
				
		if(pickUpOnlyTheFirstQuery) {  //for tests
			runSteps(queriesList.get(0));
		}else {
			//For each query 
			int i=1;
			for(String query: queriesList) {
				logger.info("Processing query "+i+ " -> "+query);
				runSteps(query);
				i++;
			}
		}
					
	}
	
	private void step1() throws Exception {
		File file = new File(inputQueriesPath);
		queriesList = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	logger.info(line);
		       queriesList.add(line);
		    }
		}
		
		
		if(queriesList.isEmpty()) {
			throw new Exception("No input query found in the input file. Aborting...");
		}
		

		if(shuffleListOfQueriesBeforeGoogleSearch) {
			Collections.shuffle(queriesList);
		}
		
	}

	
	private void runSteps(String query) throws Exception {
		
		/*
		 * Step 2: API Classes Extraction
		 * RACK  
		 *   *** Considering only the first query
		 */
		initTime = System.currentTimeMillis();
		List<String> apis = step2(query);
		botUtils.reportElapsedTime(initTime,"step2 - RACK ");
		
		
		
		/*
		 * Step 3: Query Preparation
		 * 
		 */
		String googleQuery = step3(apis,query);
		
		
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
		Set<SoThread> augmentedThreads = step6(threads);
		botUtils.reportElapsedTime(initTime,"step6 - Links Retrieval ");
		
		
		/*
		 * Step 7: Text Processing
		 * 
		 */
		initTime = System.currentTimeMillis();
		Set<Bucket> buckets = step7(threads, googleQuery, apis);
		botUtils.reportElapsedTime(initTime,"Step 7: Text Processing");
		
		/*
		 * Step 7: Relevance Calculation
		 * 
		 */
		initTime = System.currentTimeMillis();
		List<Bucket> rankedBuckets = step8(buckets);
		botUtils.reportElapsedTime(initTime,"Step 8: Relevance Calculation");
		
		
		//showBucketsOrderByCosineDesc(bucketsList);
		showRankedList(rankedBuckets);
		
		
	}

	
	



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





	/**
	 * Rack tool
	 * @param query
	 * @return List<String> representing the associated classes
	 */
	public List<String> step2(String query) {
		//list is not null. It has been already verified.
		logger.info("RACK: discovering related classes to query: "+query);
		CodeTokenProvider ctProvider = new CodeTokenProvider(query);
		List<String> apis = ctProvider.recommendRelevantAPIs();
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
		
		completeQuery = BotUtils.removeDuplicatedTokens(completeQuery," ");
		
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
			SearchQuery searchQuery = new SearchQuery.Builder(googleQuery)
			        //.site("https://stackoverflow.com")
					.site("stackoverflow.com")
			        .numResults(numberOfGoogleResults).build();
			SearchResult result = new GoogleWebSearch().search(searchQuery);
			//assertThat(result.getSize(), equalTo(10));
			List<String> urls = result.getUrls();
			//List<String> urls = new ArrayList<>();
			//urls.add("https://stackoverflow.com/questions/21961651/why-does-activesupport-add-method-forty-two-to-array/21962048");
			
			identifyQuestionsIdsFromUrls(urls,soQuestionsIds);
						
		} catch (IOException e) {
			System.out.println("Error ... "+e);
			e.printStackTrace();
		}
		
		for(Integer soQuestionId: soQuestionsIds) {
			logger.info("Id: "+soQuestionId);
		}
	
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
		logger.info("Number of allRelatedQuestionsIds: "+allRelatedQuestionsIds.size());
		
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
		logger.info("Number of questions ids identified inside links: "+soQuestionsIdsInsideTexts.size());
		
		Set<SoThread> newThreadsAssembledFromLinks = assembleListOfThreads(soQuestionsIdsInsideTexts);
		logger.info("Number of new Threads built from link's ids: "+newThreadsAssembledFromLinks.size());
		
		
		threads.addAll(newThreadsAssembledFromLinks);
		logger.info("Second total number of threads: "+threads.size()+" as a result of all discovered threads...");
		
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
		getClassesNamesForString(classesNames,presentingBody);
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

	

	private void showBucketsOrderByCosineDesc(List<Bucket> bucketsList) {
		Collections.sort(bucketsList, new Comparator<Bucket>() {
		    public int compare(Bucket o1, Bucket o2) {
		        return o2.getCosSim().compareTo(o1.getCosSim());
		    }
		});
		int pos=1;
		for(Bucket bucket: bucketsList){
			logger.info("Rank: "+(pos)+ " cosine: "+bucket.getCosSim()+" id: "+bucket.getPostId()+ " - "+bucket.getPresentingBody());
			pos++;
			if(pos>10){
				break;
			}
		}
		
	}
	

	private void showRankedList(List<Bucket> rankedBuckets) {
		int pos=0;
		for(Bucket bucket: rankedBuckets){
			logger.info("Rank: "+pos+ " total Score: "+bucket.getComposedScore() +" - cosine: "+bucket.getCosSim()+ " - coverageScore: "+bucket.getCoverageScore()+ " - codeScore: "+bucket.getCodeScore() +" - repScore: "+bucket.getRepScore()+ " - upScore: "+bucket.getUpScore()+ " - id: "+bucket.getPostId()+ " - "+bucket.getPresentingBody());
			pos++;
			if(pos==10){
				break;
			}
		}
	}

	private Bucket buildAnswerPostBucket(Post post) throws Exception {
		Bucket bucket = new Bucket();
		bucket.setParentId(post.getParentId());
		bucket.setPostId(post.getId());
		bucket.setPostScore(post.getScore());
		
		bucket.setUserReputation(post.getUser()!=null? post.getUser().getReputation():null);
		
		String presentingBody = botUtils.buildPresentationBody(post.getBody());
		bucket.setPresentingBody(presentingBody);
		
		List<String> codes = botUtils.getCodes(presentingBody);
		bucket.setCodes(codes);
		
		
		//extract classes names
		Set<String> classesNames = getClassesNames(codes);
		bucket.setClassesNames(classesNames);
		
		String processedBodyStemmedStopped = "";
		
		for(String className: classesNames){
			processedBodyStemmedStopped+= className+ " ";
		}
		processedBodyStemmedStopped+= presentingBody;
		processedBodyStemmedStopped = botUtils.buildProcessedBodyStemmedStopped(processedBodyStemmedStopped,true);
		
		
		bucket.setProcessedBodyStemmedStopped(processedBodyStemmedStopped);
		
		return bucket;
	}

	private Set<String> getClassesNames(List<String> codes) {

		Set<String> classesNames = new HashSet();
		for(String code: codes) {
			getClassesNamesForString(classesNames,code);
		}
		return classesNames;
				
	}

	private void getClassesNamesForString(Set<String> classesNames, String code) {
		//remove java keywords
		for(String keyword: BotUtils.keywords){
			code= code.replaceAll(keyword,"");
		}
		
		//remove double quotes contents
		code= code.replaceAll(BotUtils.DOUBLE_QUOTES_REGEX_EXPRESSION,"");
		
		//remove comments
		code = code.replaceAll( BotUtils.COMMENTS_REGEX_EXPRESSION, BotUtils.COMMENTS_REPLACEMENT_EXPRESSION );
		
		//Get classes in camel case
		Pattern pattern = Pattern.compile(BotUtils.CLASSES_CAMEL_CASE_REGEX_EXPRESSION);
		Matcher matcher = pattern.matcher(code);
		while (matcher.find()) {
			if(matcher.group(0)!=null && matcher.group(0).length()>3) {
				classesNames.add(matcher.group(0));
			}
			
		}
		
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



	private void initializeVariables() {
		allQuestionsIds = new HashSet<>();
		allAnwersIds = new HashSet<>();
		allCommentsIds = new HashSet<>();
		avgScore = 0d;
		avgReputation = 0d;
		
	}


	private Set<SoThread> assembleListOfThreads(Set<Integer> soPostsIds) {
		//allQuestionsIds.addAll(soQuestionsIds);
		Set<SoThread> threads = new LinkedHashSet<>();
		
		for(Integer questionId: soPostsIds) {
			Post post = pitBotService.findPostById(questionId);
			if(post==null) {
				logger.info("Post has been excluded or is not present in dataset because it is not a java post: "+questionId);
				continue; //could have been excluded 
			}
			
			if(post.getPostTypeId().equals(2)) { 
				logger.info("Post "+questionId+ " is an answer. Fetching its parent thread: "+post.getParentId());
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
			
			List<Post> answers = pitBotService.findAnswersByQuestionId(post.getId());
			
			for(Post answer: answers) {
				if(answer.getOwnerUserId()!=null) {
					answer.setUser(pitBotService.findUserById(answer.getOwnerUserId()));
				}
				List<Comment> answerComments = pitBotService.getCommentsByPostId(answer.getId());
				setCommentsUsers(answerComments);
				answer.setComments(answerComments);
				//allAnwersIds.add(answer.getId());
			}
			
			
			SoThread soThread = new SoThread(post,answers);
			threads.add(soThread);
			
		}
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
				logger.info("Recovering question from URL: "+url);
			}
			//System.out.println(url);
			
		}
		
	}    
	
	

	/*private void preProcess() throws Exception {
		
		
		 * a previous script filled postsmin table containing only java posts
		 
		postsByFilter = pitBotService.getAllPosts();
		processedPostsByFilter = getAllPostsFromSet();
		logger.info("Number of posts to perform stemming and remove stop words: "+processedPostsByFilter.size());
		performStemStop();
		
		processedPostsByFilter = null; 
		
	}
*/
	
	private Set<ProcessedPostOld> getAllPostsFromSet() {
		Set<ProcessedPostOld> processedPostsByFilter = new HashSet();
		
		logger.info("now creating new TOs...");
		for(Post post: postsByFilter) {
			ProcessedPostOld newPost = new ProcessedPostOld();
			BeanUtils.copyProperties(post,newPost);
			processedPostsByFilter.add(newPost);
		}
		logger.info("finished new TOs...");
		return processedPostsByFilter;
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

	
	
}
