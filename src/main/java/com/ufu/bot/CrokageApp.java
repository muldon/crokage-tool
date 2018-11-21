package com.ufu.bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.code.stackexchange.client.query.StackExchangeApiQueryFactory;
import com.google.code.stackexchange.common.PagedList;
import com.google.code.stackexchange.schema.Paging;
import com.google.code.stackexchange.schema.Question;
import com.google.code.stackexchange.schema.StackExchangeSite;
import com.google.code.stackexchange.schema.User.QuestionSortOrder;
import com.google.common.base.Charsets;
import com.ufu.bot.googleSearch.GoogleWebSearch;
import com.ufu.bot.googleSearch.SearchQuery;
import com.ufu.bot.googleSearch.SearchResult;
import com.ufu.bot.service.CrokageService;
import com.ufu.bot.tfidf.TFIDFCalculator;
import com.ufu.bot.to.AnswerParentPair;
import com.ufu.bot.to.Bucket;
import com.ufu.bot.to.BucketOld;
import com.ufu.bot.to.Post;
import com.ufu.bot.util.BingWebSearch;
import com.ufu.bot.util.BotComposer;
import com.ufu.bot.util.Matrix;
import com.ufu.crokage.to.MetricResult;
import com.ufu.crokage.to.SearchResults;
import com.ufu.crokage.to.UserEvaluation;
import com.ufu.crokage.util.CrokageUtils;
import com.ufu.crokage.util.IDFCalc;
import com.ufu.crokage.util.IndexLucene;
import com.ufu.crokage.util.LuceneSearcherBM25;

@Component
@SuppressWarnings("unused")
public class CrokageApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public CrokageService crokageService;

	@Autowired
	public CrokageUtils crokageUtils;

	@Autowired
	private GoogleWebSearch googleWebSearch;
	
	@Autowired
	private TFIDFCalculator tfidfCalculator;
	
	@Autowired
	private BotComposer botComposer;
	
	@Autowired
	LuceneSearcherBM25 luceneSearcherBM25;
	
	//app variables

	@Value("${useProxy}")
	public Boolean useProxy;
	
	@Value("${environment}")
	public String environment;
	
	@Value("${BIKER_HOME}")
	public String bikerHome;
	
	@Value("${CROKAGE_HOME}")
	public String crokageHome;
	
	@Value("${TMP_DIR}")
	public String tmpDir;
	
	@Value("${virutalPythonEnv}")
	public String virutalPythonEnv;
	
	@Value("${BIKER_HOME}")
	public String BIKER_HOME;
	
	@Value("${CROKAGE_HOME}")
	public String CROKAGE_HOME;
	
	@Value("${TMP_DIR}")
	public String TMP_DIR;
	
	@Value("${FAST_TEXT_INSTALLATION_DIR}")
	public String FAST_TEXT_INSTALLATION_DIR;
	
	@Value("${BIKER_RUNNER_PATH}")
	public String BIKER_RUNNER_PATH;
	
	
	
	@Value("${BIG_MAP_INVERTED_INDEX_APIS_FILE_PATH}")
	public String BIG_MAP_INVERTED_INDEX_APIS_FILE_PATH;
	
	@Value("${REDUCED_MAP_INVERTED_INDEX_APIS_FILE_PATH}")
	public String REDUCED_MAP_INVERTED_INDEX_APIS_FILE_PATH;
	
	@Value("${SO_UPVOTED_POSTS_WITH_CODE_APIS_FILE}")
	public String SO_UPVOTED_POSTS_WITH_CODE_APIS_FILE;
	
	@Value("${SO_ANSWERS_IDS_PARENT_IDS_MAP}")
	public String SO_ANSWERS_IDS_PARENT_IDS_MAP;
	
	@Value("${SO_QUESTIONS_IDS_TITLES_MAP}")
	public String SO_QUESTIONS_IDS_TITLES_MAP;
	
	@Value("${SO_ANSWERS_IDS_CONTENTS_PARENT_CONTENTS_MAP}")
	public String SO_ANSWERS_IDS_CONTENTS_PARENT_CONTENTS_MAP;
	
	@Value("${SO_CONTENT_FILE}")
	public String SO_CONTENT_FILE;
	
	@Value("${SO_IDF_VOCABULARY}")
	public String SO_IDF_VOCABULARY;
	
	@Value("${SO_SET_OF_WORDS}")
	public String SO_SET_OF_WORDS;
	
	@Value("${SO_CONTENT_WORD_VECTORS}")
	public String SO_CONTENT_WORD_VECTORS;
	
	@Value("${SO_DIRECTORY_FILES}")
	public String SO_DIRECTORY_FILES;
	
	@Value("${SO_DIRECTORY_INDEX}")
	public String SO_DIRECTORY_INDEX;
	
	@Value("${GOOGLE_TOP_RESULTS_FOR_NLP2API}")
	public String GOOGLE_TOP_RESULTS_FOR_NLP2API;
	
	@Value("${GOOGLE_TOP_RESULTS_FOR_CROKAGE}")
	public String GOOGLE_TOP_RESULTS_FOR_CROKAGE;
	
	@Value("${SE_TOP_RESULTS_FOR_NLP2API}")
	public String SE_TOP_RESULTS_FOR_NLP2API;
	
	@Value("${SE_TOP_RESULTS_FOR_CROKAGE}")
	public String SE_TOP_RESULTS_FOR_CROKAGE;
	
	@Value("${BING_TOP_RESULTS_FOR_NLP2API_RAW_JSON}")
	public String BING_TOP_RESULTS_FOR_NLP2API_RAW_JSON;
	
	@Value("${BING_TOP_RESULTS_FOR_CROKAGE_RAW_JSON}")
	public String BING_TOP_RESULTS_FOR_CROKAGE_RAW_JSON;
	
	@Value("${BING_TOP_RESULTS_FOR_BIKER_RAW_JSON}")
	public String BING_TOP_RESULTS_FOR_BIKER_RAW_JSON;
		
	
	@Value("${BING_TOP_RESULTS_FOR_NLP2API}")
	public String BING_TOP_RESULTS_FOR_NLP2API;
	
	@Value("${BING_TOP_RESULTS_FOR_CROKAGE}")
	public String BING_TOP_RESULTS_FOR_CROKAGE;
	
	@Value("${BING_TOP_RESULTS_FOR_BIKER}")
	public String BING_TOP_RESULTS_FOR_BIKER;
	
	@Value("${ALL_QUESTIONS_EXCEPTIONS_FOR_NLP2API}")
	public String ALL_QUESTIONS_EXCEPTIONS_FOR_NLP2API;
	
	@Value("${QUERIES_AND_SO_ANSWERS_TO_EVALUATE}")
	public String QUERIES_AND_SO_ANSWERS_TO_EVALUATE;
	
	@Value("${QUERIES_AND_SO_ANSWERS_AGREEMENT}")
	public String QUERIES_AND_SO_ANSWERS_AGREEMENT;
		
	@Value("${NLP2API_GOLD_SET_FILE}")
	public String NLP2API_GOLD_SET_FILE;
	
	@Value("${INPUT_QUERIES_FILE_CROKAGE}")
	public String INPUT_QUERIES_FILE_CROKAGE;
	
	@Value("${INPUT_QUERIES_FILE_BIKER}")
	public String INPUT_QUERIES_FILE_BIKER;
	
	@Value("${INPUT_QUERIES_FILE_NLP2API}")
	public String INPUT_QUERIES_FILE_NLP2API;
	
	@Value("${INPUT_QUERIES_FILE_SELECTED_QUERIES}")
	public String INPUT_QUERIES_FILE_SELECTED_QUERIES;
	
	@Value("${BIKER_INPUT_QUERIES_FILE}")
	public String BIKER_INPUT_QUERIES_FILE;
	
	@Value("${BIKER_OUTPUT_QUERIES_FILE}")
	public String BIKER_OUTPUT_QUERIES_FILE;
	
	@Value("${BIKER_SCRIPT_FILE}")
	public String BIKER_SCRIPT_FILE;
	
	@Value("${RACK_INPUT_QUERIES_FILE}")
	public String RACK_INPUT_QUERIES_FILE;
	
	@Value("${RACK_OUTPUT_QUERIES_FILE}")
	public String RACK_OUTPUT_QUERIES_FILE;
	
	@Value("${NLP2API_INPUT_QUERIES_FILE}")
	public String NLP2API_INPUT_QUERIES_FILE;
	
	
	@Value("${NLP2API_OUTPUT_QUERIES_FILE}")
	public String NLP2API_OUTPUT_QUERIES_FILE;
	
	@Value("${MATRIX_KAPPA_BEFORE_AGREEMENT}")
	public String MATRIX_KAPPA_BEFORE_AGREEMENT;
	
	@Value("${MATRIX_KAPPA_AFTER_AGREEMENT}")
	public String MATRIX_KAPPA_AFTER_AGREEMENT;
	
	
	@Value("${action}")
	public String action;

	@Value("${subAction}")
	public String subAction;

	@Value("${obs}")
	public String obs;

	@Value("${numberOfAPIClasses}")
	public Integer numberOfAPIClasses;
	
	@Value("${topk}")
	public Integer topk;
	
	@Value("${limitQueries}")
	public Integer limitQueries;
	
	
	@Value("${callBIKERProcess}")
	public Boolean callBIKERProcess;
	
	@Value("${callNLP2ApiProcess}")
	public Boolean callNLP2ApiProcess;
	
	@Value("${callRACKApiProcess}")
	public Boolean callRACKApiProcess;
		
	@Value("${dataSet}")
	public String dataSet;

	
	@Value("${cutoff}")
	public Integer cutoff;
	
	@Value("${bm25TopNResults}")
	public Integer bm25TopNResults;
	
	@Value("${topApisScoredPairsPercent}")
	public Integer topApisScoredPairsPercent;
	
	@Value("${topSimilarTitlesPercent}")
	public Integer topSimilarTitlesPercent;
	
	@Value("${useCodeInSimCalculus}")
	public Boolean useCodeInSimCalculus;
	
	@Value("${iHaveALotOfMemory}")
	public Boolean iHaveALotOfMemory;
	
	@Value("${useGoogleSearch}")
	public Boolean useGoogleSearch;  
	
	
	@Value("${numberOfGoogleResults}")
	public Integer numberOfGoogleResults;  
	
	@Value("${productionEnvironment}")
	public Boolean productionEnvironment;  
	
	
	private long initTime;
	private long endTime;
	private Set<String> extractedAPIs;
	private List<String> queries;
	private List<String> processedQueries;
	private Map<Integer,Set<String>> rackQueriesApisMap;
	//private Map<String,Set<String>> rackReformulatedQueriesApis;  // because output from rack is reformulated. This map contains the real output.
	private Map<Integer,Set<String>> bikerQueriesApisClassesMap;
	private Map<Integer,Set<String>> bikerQueriesApisClassesAndMethodsMap;
	private Map<Integer,Set<String>> nlp2ApiQueriesApisMap;
	private Map<String,Set<Integer>> bigMapApisAnswersIds;
	private Map<String,Set<Integer>> filteredSortedMapAnswersIds;
	private Map<String,Set<Integer>> groundTruthSelectedQueriesAnswersIdsMap;
	private Map<String,double[]> soContentWordVectorsMap;
	private Map<String,Double> soIDFVocabularyMap;
	private Map<String,Double> queryIDFVectorsMap;
	private Map<Integer,String> allQuestionsIdsTitlesMap;
	private Map<Integer,String> allAnswersIdsContentsParentContentsMap;
	private Map<Integer,Integer> allAnswersWithUpvotesIdsParentIdsMap;
	private Map<Integer,Integer> topAnswersWithUpvotesIdsParentIdsMap;
	private Map<Integer,Bucket> allAnswersWithUpvotesAndCodeBucketsMap;
	private Set<Integer> topClassesRelevantAnswersIds;
	private Set<Integer> topApiScoredAnswersIds;
	private List<String> wordsAndVectorsLines;
	//private Map<Integer,Double> questionsIdsScores;
	private Map<Integer,Double> bucketsIdsScores;
	private Map<Integer,Double> answersIdsScores;
	private Set<String> allWordsSetForBuckets;
	private List<String> bikerTopMethods;
	private Set<String> bikerTopClasses;
	private Map<String,Integer> methodsCounterMap;
	private Map<String,Integer> classesCounterMap;
	private Map<Integer,Double> topAnswerParentPairsAnswerIdScoreMap; 
	
	private Map<String,Set<Integer>> topScoredApiAnswersIdsMap;
	private Map<String,Set<Integer>> luceneAnswersIdsMap;
	private Map<String,Set<Integer>> topThreadsAnswersIdsMap;
	private Map<Integer,Set<String>> upvotedPostsIdsWithCodeApisMap;
	
	@PostConstruct
	public void init() throws Exception {
		long initTime = System.currentTimeMillis();
		System.out.println("Initializing CrokageApp app...");
		// initializeVariables();
		// botUtils.initializeConfigs();
		//getPropertyValueFromLocalFile();
		
		subAction = subAction !=null ? subAction.toLowerCase().trim(): null;
		dataSet = dataSet !=null ? dataSet.toLowerCase().trim(): null;
		soIDFVocabularyMap = new HashMap<>();
		wordsAndVectorsLines = new ArrayList<>();
		soContentWordVectorsMap = new HashMap<>();
		allQuestionsIdsTitlesMap = new HashMap<>();
		allAnswersIdsContentsParentContentsMap= new HashMap<>();
		//questionsIdsScores = new HashMap<>();
		bucketsIdsScores = new HashMap<>();
		answersIdsScores = new HashMap<>();
		allWordsSetForBuckets = new HashSet<>();
		methodsCounterMap = new HashMap<>();
		classesCounterMap = new HashMap<>();
		topAnswersWithUpvotesIdsParentIdsMap = new HashMap<>(); 
		topAnswerParentPairsAnswerIdScoreMap = new HashMap<>(); 
		groundTruthSelectedQueriesAnswersIdsMap = new LinkedHashMap<>();
		allAnswersWithUpvotesAndCodeBucketsMap = new HashMap<>(); 
		topClassesRelevantAnswersIds = new HashSet<>();
		rackQueriesApisMap = new LinkedHashMap<>();
		bikerQueriesApisClassesMap = new LinkedHashMap<>();
		bikerQueriesApisClassesAndMethodsMap = new LinkedHashMap<>();
		nlp2ApiQueriesApisMap = new LinkedHashMap<>();
		topScoredApiAnswersIdsMap = new LinkedHashMap<>();
		topThreadsAnswersIdsMap = new LinkedHashMap<>();
		luceneAnswersIdsMap = new LinkedHashMap<>();
		
		upvotedPostsIdsWithCodeApisMap = new HashMap<>();
		
		processedQueries = new ArrayList<>();
		/*if(topk==null) {
			topk=5000000;
		}*/
		
		System.out.println("\nConsidering parameters: \n" 
				+ "\n BIKER_HOME: " + BIKER_HOME
				+ "\n CROKAGE_HOME: " + CROKAGE_HOME
				+ "\n TMP_DIR: " + TMP_DIR
				+ "\n FAST_TEXT_INSTALLATION_DIR: " + FAST_TEXT_INSTALLATION_DIR
				+ "\n useProxy: " + useProxy 
				+ "\n environment: " + environment
				+ "\n numberOfGoogleResults: " + numberOfGoogleResults
				+ "\n topk: " + topk
				+ "\n useGoogleSearch: " + useGoogleSearch
				+ "\n numberOfAPIClasses: " + numberOfAPIClasses 
				+ "\n limitQueries: " + limitQueries
				+ "\n cutoff: " + cutoff
				+ "\n useCodeInSimCalculus: " + useCodeInSimCalculus
				+ "\n bm25TopNResults: " + bm25TopNResults
				+ "\n topApisScoredPairsPercent: " + topApisScoredPairsPercent
				+ "\n dataSet: " + dataSet
				+ "\n iHaveALotOfMemory: " + iHaveALotOfMemory
				+ "\n productionEnvironment: " + productionEnvironment
				+ "\n callBIKERProcess: " + callBIKERProcess
				+ "\n callNLP2ApiProcess: " + callNLP2ApiProcess
				+ "\n callRACKApiProcess: " + callRACKApiProcess
				
				+ "\n obs: " + obs
				+ "\n action: " + action 
				+ "\n subAction: " + subAction 
				+ "\n");

		switch (action) {
		
		case "runApproach":
			runApproach();
			break;
				
		case "generateMetricsForApiExtractorsGroundTruth":
			generateMetricsForApiExtractorsGroundTruth();
			break;	
			
		case "compareCrokageWithGoogleThreads":
			compareCrokageWithGoogleThreads();
			break;
			
		case "generateMetricsForBaselineApproaches":
			generateMetricsForBaselineApproaches();
			break;
			
		case "loadGroundTruthSelectedQueries":
			loadGroundTruthSelectedQueries();
			break;	
			
		case "buildMatrixForKappaBeforeAgreement":
			buildMatrixForKappaBeforeAgreement();
			break;	
				
		case "buildFileForAgreementPhaseHighlightingDifferences":
			buildFileForAgreementPhaseHighlightingDifferences();
			break;		
			
		case "buildMatrixForKappaAfterAgreement":
			buildMatrixForKappaAfterAgreement();
			break;		
			
		case "processCrawledQuestionsIDsAndBuilExcelFileForEvaluation":
			processCrawledQuestionsIDsAndBuilExcelFileForEvaluation();
			break;
		
		case "crawlGoogleForRelatedQuestionsIdsDatasetNLP2Api":
			crawlGoogleForRelatedQuestionsIdsDatasetNLP2Api();
			break;
			
		case "crawlGoogleForRelatedQuestionsIdsDatasetCrokage":
			crawlGoogleForRelatedQuestionsIdsDatasetCrokage();
			break;	
			
		case "crawlBingForRelatedQuestionsIdsDatasetNLP2Api":
			crawlBingForRelatedQuestionsIdsDatasetNLP2Api();
			break;
			
		case "crawlBingForRelatedQuestionsIdsDatasetCrokage":
			crawlBingForRelatedQuestionsIdsDatasetCrokage();
			break;	
		
		case "crawlBingForRelatedQuestionsIdsDatasetBIKER":
			crawlBingForRelatedQuestionsIdsDatasetBIKER();
			break;	
			
				
		case "processBingResultsToStandardFormat":
			processBingResultsToStandardFormat();
			break;		
			
		case "crawlStackExchangeForRelatedQuestionsIdsDatasetNLP2Api":
			crawlStackExchangeForRelatedQuestionsIdsDatasetNLP2Api();
			break;	
			
		case "crawlStackExchangeForRelatedQuestionsIdsDatasetCrokage":
			crawlStackExchangeForRelatedQuestionsIdsDatasetCrokage();
			break;	
			
		case "readAnswersIdsParentsMap":
			readAnswersIdsParentsMap();
			break;	
		
		case "checkConditions":
			checkConditions();
			break;	
			
		case "generateAnswersIdsParentsMap":
			generateAnswersIdsParentsMap();
			break;	
			
		case "generateQuestionsIdsTitlesMap":
			generateQuestionsIdsTitlesMap();
			break;	
			
		case "generateQuestionsIdsContentMap":
			generateQuestionsIdsContentMap();
			break;		
				
		case "buildSOSetOfWords":
			buildSOSetOfWords();
			break;	
			
		case "buildSODirectoryFiles":
			buildSODirectoryFiles();
			break;	
		
		case "buildLuceneIndex":
			buildLuceneIndex();
			break;		
			
		case "readIDFVocabulary":
			crokageUtils.readIDFVocabulary(soIDFVocabularyMap);
			break;	
			
		case "readInputQueries":
			readInputQueries();
			break;	
			
			
		case "buildIDFVocabulary":
			buildIDFVocabulary();
			break;	
			
				
		case "readQuestionsIdsTitlesMap":
			readQuestionsIdsTitlesMap();
			break;
			
		case "readSoContentWordVectors":
			readSoContentWordVectorsForAllWords();
			break;		
			
		case "generateTrainingFileToFastText":
			generateTrainingFileToFastText();
			break;	
			
		case "reduceBigMapFileToMininumAPIsCount":
			reduceBigMapFileToMininumAPIsCount();
			break;	
			
		case "loadInvertedIndexFile":
			loadInvertedIndexFile();
			break;
		
		case "generateInvertedIndexFileFromSOPosts":
			generateInvertedIndexFileFromSOPosts();
			break;
		
		case "generatePostsApisMap":
			generatePostsApisMap();
			break;	
			
		case "loadPostsApisMap":
			loadUpvotedPostsWithCodeApisMap();
			break;	
			
		case "generateMetricsForApiExtractors":
			generateMetricsForApiExtractors(null);
			break;	
		
		case "generateInputQueriesForCrokageDataSetFromExcelGroudTruth":
			generateInputQueriesForCrokageDataSetFromExcelGroudTruth();
			break;
		
				
		case "getApisForApproaches":
			getApisForApproaches();
			break;
			
		case "extractAPIsFromRACK":
			extractAPIsFromRACK();
			break;
		
		case "extractAPIsFromBIKER":
			extractAPIsFromBIKER();
			break;
			
		case "extractAPIsFromNLP2Api":
			extractAPIsFromNLP2Api();
			break;	
		
		case "loadExcelGroundTruthQuestionsAndLikerts":
			loadExcelGroundTruthQuestionsAndLikerts();
			break;
			
		case "generateInputQueriesFromNLP2ApiGroudTruth":
			generateInputQueriesFromNLP2ApiGroudTruth();
			break;	
			
		case "tester":
			tester();
			break;	
		
		default:
			break;
		}
		
		System.out.println("Done running task.");
		crokageUtils.reportElapsedTime(initTime,action);

	}


	private void loadUpvotedPostsWithCodeApisMap() throws Exception {
		long initTime = System.currentTimeMillis();
		upvotedPostsIdsWithCodeApisMap.putAll(CrokageUtils.readPostsIdsApisMap(SO_UPVOTED_POSTS_WITH_CODE_APIS_FILE));
		crokageUtils.reportElapsedTime(initTime,"loadUpvotedPostsWithCodeApisMap");
	}


	private void compareCrokageWithGoogleThreads() throws Exception {
		Map<String,Set<Integer>> googleQueriesAndSOIdsMap = new LinkedHashMap<>();
		Map<String, Set<Integer>> googleRecommendedResults = new LinkedHashMap<>();
		
		googleQueriesAndSOIdsMap = CrokageUtils.readCrawledQuestionsIds(GOOGLE_TOP_RESULTS_FOR_NLP2API);
		googleQueriesAndSOIdsMap.putAll(CrokageUtils.readCrawledQuestionsIds(GOOGLE_TOP_RESULTS_FOR_CROKAGE));
		
		//load recommended 
		runApproach();
		
		//Map<String, Set<Integer>> groundTruthSelectedQueriesQuestionsIdsMap = getGroundTruthSelectedQueriesQuestionsIdsMap();
		
		MetricResult metricResult = new MetricResult();
		metricResult.setApproach("crokage stage1 x google");
		analyzeResults(topScoredApiAnswersIdsMap,googleQueriesAndSOIdsMap,metricResult, " google ");
		
	}





	private Map<String, Set<Integer>> getGroundTruthSelectedQueriesQuestionsIdsMap() {
		Map<String, Set<Integer>> groundTruthSelectedQueriesQuestionsIdsMap = new LinkedHashMap<>();
		
		for(String query: groundTruthSelectedQueriesAnswersIdsMap.keySet()) {
			//googleRecommendedResults.put(query, googleQueriesAndSOIdsMap.get(query));
			
			Set<Integer> answersIds = groundTruthSelectedQueriesAnswersIdsMap.get(query);
			Set<Integer> parentIds = new LinkedHashSet<>();
			List<Post> answers = crokageService.findPostsById(new ArrayList<>(answersIds));
			
			for(Post answer: answers) {
				Post parent = crokageService.findPostById(answer.getParentId());
				parentIds.add(parent.getId());
			}
			groundTruthSelectedQueriesQuestionsIdsMap.put(query, parentIds); 
			
		}
		return groundTruthSelectedQueriesQuestionsIdsMap;
	}




	private void generateMetricsForApiExtractorsGroundTruth() throws Exception {
		String processedQuery;
		String rawQuery;
		Set<Integer> answersWithTopFrequentAPIs=null;
		Set<Integer> luceneAnswersSearchResult=null;
		Set<Integer> topThreadsAnswersIds=null;
		Set<Integer> topAnswersForThreadsIds=null;
		Set<Integer> topKRelevantQuestionsIds=null;
		Map<String, Set<Integer>> recommendedResults = new LinkedHashMap<>();
		
		//load input queries considering dataset
		queries = readInputQueries();
		//load ground truth
		loadGroundTruthSelectedQueries();
		
		for(String query: queries) {
			processedQueries.add(crokageUtils.processQuery(query));
		}
		
		
		if(iHaveALotOfMemory) { //load all word vectors only once
			readSoContentWordVectorsForAllWords();
			wordsAndVectorsLines=null;
		}else {
			//load so content word vectors to a list of strings representing posts (each line is a post)
			readSOContentWordAndVectorsLines();
			crokageUtils.readVectorsFromSOMapForWords(soContentWordVectorsMap,new HashSet(processedQueries),wordsAndVectorsLines);
		}
		
		//load questions map (id,title)
		readQuestionsIdsTitlesMap();
		//readAllAnswersIdsContentsParentContentsMap();
		
		
		//load answers map (id,parentId)
		readAnswersIdsParentsMap();
		
		//load the inverted index (api, postsSet)
		loadInvertedIndexFile();
		
		//filter by cutoff and sort in descending order
		reduceBigMapFileToMininumAPIsCount();
		
		//read idf vocabulary map (word, idf)
		crokageUtils.readIDFVocabulary(soIDFVocabularyMap);
		
		resetAPIExtractors();
		
		//load apis considering approaches
		getApisForApproaches();
		
		List<Bucket> upvotedAnswersIdsWithCode = loadUpvotedAnswersIdsWithCodeContentsAndParentContents();
		luceneSearcherBM25.buildSearchManager(upvotedAnswersIdsWithCode);
		upvotedAnswersIdsWithCode=null;
		
		//load <postId,Set<Apis>> map
		loadUpvotedPostsWithCodeApisMap();
		
		//combine state of art approaches considering the order in parameters
		Map<Integer, Set<String>> recommendedApis = getRecommendedApis();
		
		Set<Integer> keys = recommendedApis.keySet();
		if(keys.size()!=queries.size()) {
			throw new Exception("Queries size are different from the ones recommended by the API extractors. Consider extracting them again. ");
		}
		
		/*int bm25TopNResultsArr[] = {1000,2500,5000,10000,12500,15000,20000,25000,30000,35000};*/
		int bm25TopNResultsArr[] = {10000};
		double simWeight = 1;
		double classFreqWeight = 0.0;
		double methodFreqWeight = 0.0;
		double repWeight = 0.0;
		double upWeight = 0.0;
		
		
		for(int bm25TopNResults: bm25TopNResultsArr) {
			int count = 1;
			int sum1=0;
			int sum2=0;
			int sum3=0;
			BotComposer.setSimWeight(simWeight);
			BotComposer.setClassFreqWeight(classFreqWeight);
			BotComposer.setMethodFreqWeight(methodFreqWeight);
			BotComposer.setRepWeight(repWeight);
			BotComposer.setUpWeight(upWeight);
			
			for(Integer key: keys) {  //for each query 
				long initTime = System.currentTimeMillis();
				long initTime2 = System.currentTimeMillis();
				
				rawQuery = queries.get(key-1);
				System.out.println("\n\nQuery: "+count+" "+rawQuery+ " - bm25TopNResults: "+bm25TopNResults);
				count++;
				processedQuery = processedQueries.get(key-1);
				System.out.println("Processed query: "+processedQuery);
				
				Set<String> topClasses = recommendedApis.get(key);
				System.out.println("topClasses: "+topClasses);
				//get vectors for query words
				double[][] matrix1 = CrokageUtils.getMatrixVectorsForQuery(processedQuery,soContentWordVectorsMap);
				
				//get idfs for query
				double[][] idf1 = CrokageUtils.getIDFMatrixForQuery(processedQuery, soIDFVocabularyMap);
				//crokageUtils.reportElapsedTime(initTime2,"for query 2");
				
				//add vectors for all retrieved answers
				if(!iHaveALotOfMemory) {
					addVectorsToSoContentWordVectorsMap(answersWithTopFrequentAPIs);
				}	
				
				
				//first filter, lucene
				luceneAnswersSearchResult = luceneSearch(processedQuery,bm25TopNResults);
				System.out.println("Size of luceneAnswersSearchResult: "+luceneAnswersSearchResult.size());
				luceneAnswersIdsMap.put(rawQuery, luceneAnswersSearchResult);
				sum1+=luceneAnswersSearchResult.size();
				//crokageUtils.reportElapsedTime(initTime2,"lucene");
				//initTime2 = System.currentTimeMillis();
				
				//second filter, api score
				answersWithTopFrequentAPIs = getAnswersForTopFrequentAPIs(topClasses,luceneAnswersSearchResult);
				System.out.println("Size of answersWithTopFrequentAPIs: "+answersWithTopFrequentAPIs.size());
				topScoredApiAnswersIdsMap.put(rawQuery, answersWithTopFrequentAPIs);
				sum2+=answersWithTopFrequentAPIs.size();
				//crokageUtils.reportElapsedTime(initTime2,"topScoredApiAnswersIdsMap");
				//initTime2 = System.currentTimeMillis();
				
				//third filter, threads
				topAnswersForThreadsIds = getAnswersForTopThreads(answersWithTopFrequentAPIs,matrix1,idf1);
				System.out.println("Size of topAnswersForThreadsIds: "+topAnswersForThreadsIds.size());
				topThreadsAnswersIdsMap.put(rawQuery, topAnswersForThreadsIds);
				sum3+=topAnswersForThreadsIds.size();
				//crokageUtils.reportElapsedTime(initTime2,"topAnswersForThreadsIds");
				//initTime2 = System.currentTimeMillis();
				
				//last filter: relevance similarity
				Set<Integer> topKRelevantAnswersIds = getTopKRelevantAnswers(topAnswersForThreadsIds,matrix1,idf1,key,processedQuery);
				System.out.println("Size of topKRelevantAnswersIds: "+topKRelevantAnswersIds.size());
				recommendedResults.put(rawQuery, topKRelevantAnswersIds);
				//crokageUtils.reportElapsedTime(initTime2,"topKRelevantAnswersIds");
				
				crokageUtils.reportElapsedTime(initTime,"for query");
				
			}
			
			System.out.println("\n");
			String obsComp = "luceneAnswersIdsMap filter (average): "+sum1/keys.size();
			MetricResult metricResult = new MetricResult("luceneAnswersIdsMap",bm25TopNResults,topApisScoredPairsPercent,topSimilarTitlesPercent,cutoff,null,obsComp);
			analyzeResults(luceneAnswersIdsMap,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"luceneAnswersIdsMap");
			System.out.println(obsComp);
			//crokageService.saveMetricResult(metricResult,useCodeInSimCalculus,obsComp,bm25TopNResults,cutoff,numberOfAPIClasses,topApisScoredPairsPercent);
		
			System.out.println("\n");
			obsComp = "topScoredApiAnswersIdsMap filter (average): "+sum2/keys.size();
			metricResult = new MetricResult("topScoredApiAnswersIdsMap",bm25TopNResults,topApisScoredPairsPercent,topSimilarTitlesPercent,cutoff,null,obsComp);
			analyzeResults(topScoredApiAnswersIdsMap,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"topScoredApiAnswersIdsMap");
			System.out.println(obsComp);
			//crokageService.saveMetricResult(metricResult,useCodeInSimCalculus,obsComp,bm25TopNResults,cutoff,numberOfAPIClasses,topApisScoredPairsPercent);
			
			System.out.println("\n");
			obsComp = "topThreadsAnswersIdsMap filter (average): "+sum3/keys.size();
			metricResult = new MetricResult("topThreadsAnswersIdsMap",bm25TopNResults,topApisScoredPairsPercent,topSimilarTitlesPercent,cutoff,null,obsComp);
			analyzeResults(topThreadsAnswersIdsMap,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"topThreadsAnswersIdsMap");
			System.out.println(obsComp);
			crokageService.saveMetricResult(metricResult,useCodeInSimCalculus,obsComp,bm25TopNResults,cutoff,numberOfAPIClasses,topApisScoredPairsPercent);
			
			System.out.println("\n");
			obsComp = "topKRelevantAnswersIds filter. Size: "+topk;
			metricResult = new MetricResult("final relevance",bm25TopNResults,topApisScoredPairsPercent,topSimilarTitlesPercent,cutoff,topk,obsComp);
			analyzeResults(recommendedResults,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"topKRelevantAnswersIds");
			System.out.println(obsComp);
			crokageService.saveMetricResult(metricResult,useCodeInSimCalculus,obsComp,bm25TopNResults,cutoff,numberOfAPIClasses,topApisScoredPairsPercent);
			
		}
	}


	
	private Set<Integer> getAnswersForTopThreads(Set<Integer> answersWithTopFrequentAPIs, double[][] matrix1, double[][] idf1) {
		Set<Bucket> candidateBuckets = new HashSet<>();
		for(Integer answerId: answersWithTopFrequentAPIs) {
			candidateBuckets.add(allAnswersWithUpvotesAndCodeBucketsMap.get(answerId));
		}
		
		
		String comparingTitle;
		bucketsIdsScores.clear();
		
		for(Bucket bucket:candidateBuckets) {
			
			//get the word vectors for each word of the query
			comparingTitle = bucket.getParentProcessedTitle();
			
			//get vectors for query2 words
			double[][] matrix2 = CrokageUtils.getMatrixVectorsForQuery(comparingTitle,soContentWordVectorsMap);
			
			//get idfs for query2
			double[][] idf2 = CrokageUtils.getIDFMatrixForQuery(comparingTitle, soIDFVocabularyMap);
			
			double simPair = CrokageUtils.round(Matrix.simDocPair(matrix1,matrix2,idf1,idf2),6);
			
			bucketsIdsScores.put(bucket.getId(), simPair);
			
			bucket.setTitleScore(simPair);
			
		}
				
		int topSimilarTitles = bucketsIdsScores.size()*topSimilarTitlesPercent/100;
		
		//sort scores in descending order and consider the first topSimilarQuestionsNumber parameter
		Map<Integer,Double> topKRelevantAnswersBucketsMap = bucketsIdsScores.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       .limit(topSimilarTitles)
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		
		Set<Integer> topKRelevantAnswersIds = topKRelevantAnswersBucketsMap.keySet();
		
		
		return topKRelevantAnswersIds;
		
		
	}
	
	
	private List<Bucket> loadUpvotedAnswersIdsWithCodeContentsAndParentContents() {
		long initTime2 = System.currentTimeMillis();
		List<Bucket> buckets = crokageService.getUpvotedAnswersIdsContentsAndParentContents();
		for(Bucket bucket:buckets) {
			allAnswersWithUpvotesAndCodeBucketsMap.put(bucket.getId(), bucket);
		}
		crokageUtils.reportElapsedTime(initTime2,"getUpvotedAnswersIdsContentsAndParentContents");
		return buckets;
	}

	
	


	private Set<Integer> luceneSearch(String processedQuery, int bm25TopNResults2) throws Exception {
		//List<Bucket> buckets = crokageService.getUpvotedAnswersIdsContentsAndParentContents();
		
		Set<Integer> answersIds = luceneSearcherBM25.search(processedQuery, bm25TopNResults2);
		
		
		return answersIds;
		
	}


	private void calculateApiScore(AnswerParentPair answerParentPair, Set<String> topClasses) {
		Set<String> allApis = new HashSet<>();
		ArrayList<String> arr = new ArrayList<>(topClasses);
		double rrank=0;
		
		Set<String> parentApis = upvotedPostsIdsWithCodeApisMap.get(answerParentPair.getParentId());
		Set<String> answerApis = upvotedPostsIdsWithCodeApisMap.get(answerParentPair.getAnswerId());
		if(parentApis!=null) {
			allApis.addAll(parentApis);
		}
		if(answerApis!=null) {
			allApis.addAll(answerApis);
		}
		
		/*
		for (int i = 0; i < rapis.size(); i++) {
			String api = rapis.get(i);
			if (isApiFound(api, gapis)) {
				rrank = 1.0 / (i + 1);
				break;
			}
		}*/
		
		for (int i = 0; i < arr.size(); i++) {
			String api = arr.get(i);
			if(allApis.contains(api)) {
				rrank += 1.0 / (i + 1);
			}
		}
		answerParentPair.setApiScore(rrank);
		arr= null;
		allApis=null;
	}



/*

	private Set<Integer> getCandidateQuestionsFromTopApisOld(Set<String> topClasses) throws Exception {
		//get ids from reducedMap for top classes
		topClassesRelevantAnswersIds.clear();
		Set<Integer> candidateQuestionsIds = new HashSet<>();
		List<Integer> answersWithNoUpvotes = new ArrayList<>();  //allAnswersWithUpvotesIdsParentIdsMap is filled with previou process that fetches answers with upvotes
		
		for(String topClass:topClasses) {
			Set<Integer> answersIdsFromBigMap = filteredSortedMapAnswersIds.get(topClass);
			if(answersIdsFromBigMap!=null) {
				topClassesRelevantAnswersIds.addAll(answersIdsFromBigMap);
			}else {
				if(!productionEnvironment) {
					System.out.println("*** Class not found in bigMap: "+topClass);
				}
			}
		}
		
		if(!productionEnvironment) {
			System.out.println("At least "+topClassesRelevantAnswersIds.size()+ " answers contain those classes. Filtering relevants...");
		}
		
		for(Integer answerId: topClassesRelevantAnswersIds) {
			if(allAnswersWithUpvotesIdsParentIdsMap.containsKey(answerId)) {
				int questionId = allAnswersWithUpvotesIdsParentIdsMap.get(answerId);
				candidateQuestionsIds.add(questionId);
			}else {
				answersWithNoUpvotes.add(answerId);
			}
		}
		
		topClassesRelevantAnswersIds.removeAll(answersWithNoUpvotes);
		
		int listSize = answersWithNoUpvotes.size();
		int k = listSize > 10? 10:listSize; 
		
		String answersWithNoUpvotesStr = StringUtils.join(answersWithNoUpvotes.subList(0, k), ',');
		
		if(!productionEnvironment) {
			System.out.println("Candidate questions to top classes relevant answers: "+candidateQuestionsIds.size()+ " - no upvotes: "+answersWithNoUpvotes.size()+ " - remaining: "+topClassesRelevantAnswersIds.size());
		}
		//System.out.println("Number of discarded answers because they have no upvotes: "+answersWithNoUpvotes.size()+ " showing "+k+": "+answersWithNoUpvotesStr);
		//System.out.println("Number of candidate answers left - stage 1: "+topClassesRelevantAnswersIds.size());
		answersWithNoUpvotes=null;
		
		
		return candidateQuestionsIds;
	}

*/

/*
	private Set<Integer> getCandidateAnswersIdsOld(Set<Integer> topKRelevantQuestionsIds) {
		//fetch again the answers related to those questions
		Set<Integer> candidateAnswersIds = new LinkedHashSet<>();
		
		for(Integer quesitonId: topKRelevantQuestionsIds) {
			if(allAnswersWithUpvotesIdsParentIdsMap.containsValue(quesitonId)) {
				Set<Integer> keys = CrokageUtils.getKeysByValue(allAnswersWithUpvotesIdsParentIdsMap, quesitonId);
				for(Integer id: keys) {
					if(topClassesRelevantAnswersIds.contains(id)) {
						candidateAnswersIds.add(id);
					}
				}
			}
		}
		
		if(!productionEnvironment) {
			reportSimilarRelatedPosts(candidateAnswersIds,"answers","End of Ranking phase 2: ");
		}
		return candidateAnswersIds;
	}
*/


	private void runApproach() throws Exception {
		String processedQuery;
		String rawQuery;
		Set<Integer> candidateQuestionsIds=null;
		Set<Integer> topKRelevantQuestionsIds=null;
		Map<String, Set<Integer>> recommendedResults = new LinkedHashMap<>();
		
		//load input queries considering dataset
		queries = readInputQueries();
		//load ground truth
		loadGroundTruthSelectedQueries();
		
		for(String query: queries) {
			processedQueries.add(crokageUtils.processQuery(query));
		}
		
		//load ground truth
		loadGroundTruthSelectedQueries();
		
		if(iHaveALotOfMemory) { //load all word vectors only once
			readSoContentWordVectorsForAllWords();
			wordsAndVectorsLines=null;
		}else {
			//load so content word vectors to a list of strings representing posts (each line is a post)
			readSOContentWordAndVectorsLines();
			crokageUtils.readVectorsFromSOMapForWords(soContentWordVectorsMap,new HashSet(processedQueries),wordsAndVectorsLines);
		}
		
		//load questions map (id,title)
		readQuestionsIdsTitlesMap();
		//readAllAnswersIdsContentsParentContentsMap();
		
		//load answers map (id,parentId)
		readAnswersIdsParentsMap();
		
		//load the inverted index (api, postsSet)
		loadInvertedIndexFile();
		
		//filter by cutoff and sort in descending order
		reduceBigMapFileToMininumAPIsCount();
				
		//read word vectors map (word, vectors[]) by demand
		if(!iHaveALotOfMemory) {
			crokageUtils.readVectorsFromSOMapForWords(soContentWordVectorsMap,new HashSet(processedQueries),wordsAndVectorsLines);
		}
		
		//read idf vocabulary map (word, idf)
		crokageUtils.readIDFVocabulary(soIDFVocabularyMap);
		
		/*String subActions[] = {
				//"rack",
				//"biker",
				//"nlp2api",
				//"rack|biker",
				//"rack|nlp2api",
				"biker|rack",
				"biker|nlp2api",
				"nlp2api|rack",
				"nlp2api|biker",
				"rack|biker|nlp2api",
				"rack|nlp2api|biker",
				"biker|rack|nlp2api",
				"biker|nlp2api|rack",
				"nlp2api|rack|biker",
				"nlp2api|biker|rack"
		}; */
		
		/*String subActions[] = {
				"rack|biker|nlp2api",
		}; */
		
		//for(String subActionTmp: subActions) {
			//this.subAction = subActionTmp;
		resetAPIExtractors();
		
		//load apis considering approaches
		getApisForApproaches();
		
		List<Bucket> upvotedScoredAnswers = loadUpvotedAnswersIdsWithCodeContentsAndParentContents();
		luceneSearcherBM25.buildSearchManager(upvotedScoredAnswers);
		upvotedScoredAnswers=null;
		
		//load <postId,Set<Apis>> map
		loadUpvotedPostsWithCodeApisMap();
		
		//combine state of art approaches considering the order in parameters
		Map<Integer, Set<String>> recommendedApis = getRecommendedApis();
		
		Set<Integer> keys = recommendedApis.keySet();
		
		/*double simWeights[] = {0.9, 1.0};
		double classFreqWeights[] = {0.7, 0.8, 0.9};
		double methodFreqWeights[] = {0.7, 0.8, 0.9};
		double repWeights[] = {0.4, 0.5, 0.6};
		double upWeights[] = {0.4, 0.5, 0.6};*/
		
		double simWeights[] = {0.9};
		double classFreqWeights[] = {0.9};
		double methodFreqWeights[] = {0.7};
		double repWeights[] = {0.3};
		//double repWeights[] = {0};
		double upWeights[] = {0.3};
		//double upWeights[] = {0};
		
		//boolean useCodeInSimCalculusArr[] = {true,false};
		String obsTmp;
		int count=0;
		
		
		for(double simWeight: simWeights) {
			for(double classFreqWeight: classFreqWeights) {
				for(double methodFreqWeight: methodFreqWeights) {
					for(double repWeight: repWeights) {
						for(double upWeight: upWeights) {
							BotComposer.setSimWeight(simWeight);
							BotComposer.setClassFreqWeight(classFreqWeight);
							BotComposer.setMethodFreqWeight(methodFreqWeight);
							BotComposer.setRepWeight(repWeight);
							BotComposer.setUpWeight(upWeight);
							
							count++;
							performSearch(recommendedApis, keys, recommendedResults);
							obsTmp = "topk:"+topk+" - whole approach lucene BM25 + TopFrequentAPIs: "+subAction+ " - "+obs;
							System.out.println(obsTmp);
							MetricResult metricResult = new MetricResult();
							metricResult.setApproach("crokage");
							analyzeResults(recommendedResults,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"whole approach");
							crokageService.saveMetricResult(metricResult,useCodeInSimCalculus,obsTmp,bm25TopNResults,cutoff,numberOfAPIClasses,topApisScoredPairsPercent);
							
						}
					}
				}
			}
		}
		
		/*BotComposer.setSimWeight(0.9);
		BotComposer.setClassFreqWeight(0.75);
		BotComposer.setMethodFreqWeight(0.75);
		BotComposer.setRepWeight(0.5);
		BotComposer.setUpWeight(0.5);
		
		performSearch(recommendedApis, keys, recommendedResults);
		MetricResult metricResult = new MetricResult();
		crokageUtils.reduceSetV2(recommendedResults, 10);
		analyzeResultsV2(recommendedResults,groundTruthSelectedQueriesAnswersIdsMap,metricResult);
		
		crokageService.saveMetricResult(metricResult,useCodeInSimCalculus,obs,topSimilarAnswersNumber,topSimilarQuestionsNumber,cutoff);*/
		
		System.out.println("Number of searches: "+count);
		
		//}
		
		//System.out.println(metricResult.toString());
	}


	private void performSearch(Map<Integer, Set<String>> recommendedApis, Set<Integer> keys, Map<String, Set<Integer>> recommendedResults) throws Exception, IOException {
		long initTime = System.currentTimeMillis();
		String processedQuery;
		String rawQuery;
		Set<Integer> answersWithTopFrequentAPIs=null;
		Set<Integer> candidateQuestionsIds;
		Set<Integer> topKRelevantQuestionsIds;
		Set<Integer> luceneAnswersSearchResult=null;
		int count = 1;
		for(Integer key: keys) {  //for each query 
			long initTime2 = System.currentTimeMillis();
			
			rawQuery = queries.get(key-1);
			System.out.println("\n\nQuery: "+count+" "+rawQuery);
			count++;
			
			//remove stop words, punctuations, etc. The same process applied to preprocess all SO titles.
			processedQuery = processedQueries.get(key-1);
			if(!productionEnvironment) {
				System.out.println("Processed query: "+processedQuery);
			}
			Set<String> topClasses = recommendedApis.get(key);
			if(!productionEnvironment) {
				System.out.println("Top classes: "+topClasses);
			}
			
			//first filter, lucene
			luceneAnswersSearchResult = luceneSearch(processedQuery,bm25TopNResults);
			System.out.println("Size of luceneAnswersSearchResult: "+luceneAnswersSearchResult.size());
			
			//second filter, api score
			answersWithTopFrequentAPIs = getAnswersForTopFrequentAPIs(topClasses,luceneAnswersSearchResult);
			System.out.println("Size of answersWithTopFrequentAPIs: "+answersWithTopFrequentAPIs.size());
			
			if(CollectionUtils.isEmpty(answersWithTopFrequentAPIs)) {
				System.out.println("Empty answersWithTopFrequentAPIs for query: "+rawQuery+ " - topClasses: "+topClasses);
				recommendedResults.put(rawQuery, new HashSet<>());
				continue;
			}
			
			
			//get vectors for query words
			double[][] matrix1 = CrokageUtils.getMatrixVectorsForQuery(processedQuery,soContentWordVectorsMap);
			
			//get idfs for query
			double[][] idf1 = CrokageUtils.getIDFMatrixForQuery(processedQuery, soIDFVocabularyMap);
			//crokageUtils.reportElapsedTime(initTime2,"for query 2");
			
			//add vectors for all retrieved answers
			if(!iHaveALotOfMemory) {
				addVectorsToSoContentWordVectorsMap(answersWithTopFrequentAPIs);
			}	
			
			
			//crokageUtils.reportElapsedTime(initTime,"for loop query 2");
			//reportTest("here 1",candidateQuestionsIds,key);
			//Set<Integer> topKRelevantAnswersIds = getTopKRelevantAnswers(answersWithTopFrequentAPIs,bikerTopMethods,matrix1,idf1,key,processedQuery,topClasses);
			//recommendedResults.put(rawQuery, topKRelevantAnswersIds);
			//crokageUtils.reportElapsedTime(initTime2," query 3");
			//crokageUtils.reportElapsedTime(initTime2," query "+key);
			
			/*topKRelevantQuestionsIds = getTopKRelevantQuestionsIds(candidateQuestionsIds,bikerTopMethods,matrix1,idf1,key,processedQuery);
			
			Set<Integer> candidateAnswersIds = getCandidateAnswersIdsOld(topKRelevantQuestionsIds);
			
			Set<Integer> topKRelevantAnswersIds = new LinkedHashSet<>();
			
			if(!CollectionUtils.isEmpty(candidateAnswersIds)) {
				topKRelevantAnswersIds = getTopKRelevantAnswers(candidateAnswersIds,bikerTopMethods,matrix1,idf1,key,processedQuery,topClasses);
			}else {
				System.out.println("Empty candidateAnswersIds for query: "+rawQuery+ " - processed: "+processedQuery
						+ " -\n topKRelevantQuestionsIds: "+topKRelevantQuestionsIds
						+ " -\n candidateQuestionsIds: "+candidateQuestionsIds
						+ " -\n topClasses: "+topClasses);
			}
			
			recommendedResults.put(rawQuery, topKRelevantAnswersIds);
			
			crokageUtils.reportElapsedTime(initTime2," query "+key);*/
			
			
		}
		crokageUtils.reportElapsedTime(initTime,"all "+keys.size() +" queries.");
		
	}

	private Set<Integer> getAnswersForTopFrequentAPIs(Set<String> topClasses, Set<Integer> luceneAnswersSearchResult) {
		//long initTime = System.currentTimeMillis();
		
		//Set<Integer> answersWithTopFrequentAPIs = new HashSet<>();
		Set<AnswerParentPair> answerParentThreads = new HashSet<>(); 
		//List<AnswerParentPair> sortedPairs = new ArrayList<>();
		List<Integer> topNAnswersIds = new ArrayList<>();
		//Set<Integer> topNAnswersIds = new LinkedHashSet<>();
		
		for(String topClass:topClasses) {
			Set<Integer> answersIdsFromBigMap = filteredSortedMapAnswersIds.get(topClass);
			if(answersIdsFromBigMap==null) {
				//System.out.println("Answers not found for topClass: "+topClass);
				continue;
			}	
			for(Integer answerId: answersIdsFromBigMap) {
				if(luceneAnswersSearchResult.contains(answerId) && allAnswersWithUpvotesAndCodeBucketsMap.containsKey(answerId)) { //filtered by lucene and is upvoted answer
					//answersWithTopFrequentAPIs.add(answerId);
					AnswerParentPair answerParentPair = new AnswerParentPair(answerId, allAnswersWithUpvotesAndCodeBucketsMap.get(answerId).getParentId());
					calculateApiScore(answerParentPair,topClasses);
					answerParentThreads.add(answerParentPair);
					topAnswerParentPairsAnswerIdScoreMap.put(answerId, answerParentPair.getApiScore());
				}
			}
		}
		
		if(!productionEnvironment) {
			System.out.println("Number of pairs(answer+parent) containing topClasses: "+answerParentThreads.size());
		}
		
		
		int topApisScoredPairs = answerParentThreads.size()*topApisScoredPairsPercent/100;
		
		//sort by apiScore desc
		topNAnswersIds= answerParentThreads.stream()
				.sorted(Comparator.reverseOrder())
				.limit(topApisScoredPairs)
				.map(x -> x.getAnswerId())
				.collect(Collectors.toList());
		
		/*sortedPairs= topAnswerParentPairs.stream()
				.sorted(Comparator.comparing(AnswerParentPair::getApiScore).reversed())
				.limit(topApisScoredPairsPercent)
				//.map(x -> x.getAnswerId())
				.collect(Collectors.toList());*/
		
		
		/*System.out.println(topNAnswersIds.contains(20904059)); 
		System.out.println(topNAnswersIds.contains(7074420));
		System.out.println(topNAnswersIds.contains(7384949));
		System.out.println(topNAnswersIds.contains(7384948));
		System.out.println(topNAnswersIds.contains(44483952));*/
		
		
		Set<Integer> topNAnswersIdsSet = new LinkedHashSet<>(topNAnswersIds);
		//topApiScoredAnswersIds.addAll(topNAnswersIdsSet);
		
		for(Integer topAnswerId: topNAnswersIdsSet) {
			topAnswersWithUpvotesIdsParentIdsMap.put(topAnswerId, allAnswersWithUpvotesIdsParentIdsMap.get(topAnswerId));
		}
		
		
		topNAnswersIds=null;
		answerParentThreads=null;
		//crokageUtils.reportElapsedTime(initTime,"getAnswersForTopFrequentAPIs");
		
		return topNAnswersIdsSet;
	}


	private void generateMetricsForBaselineApproaches() throws Exception {
		//google
		//load all queries and the ids found for google
		Map<String,Set<Integer>> googleQueriesAndSOIdsMap = new LinkedHashMap<>();
		Map<String,Set<Integer>> bingQueriesAndSOIdsMap = new LinkedHashMap<>();
		
		Map<String, Set<Integer>> recommendedResults = new LinkedHashMap<>();
		
		googleQueriesAndSOIdsMap = CrokageUtils.readCrawledQuestionsIds(GOOGLE_TOP_RESULTS_FOR_NLP2API);
		googleQueriesAndSOIdsMap.putAll(CrokageUtils.readCrawledQuestionsIds(GOOGLE_TOP_RESULTS_FOR_CROKAGE));
		bingQueriesAndSOIdsMap = CrokageUtils.readCrawledQuestionsIds(BING_TOP_RESULTS_FOR_NLP2API);
		bingQueriesAndSOIdsMap.putAll(CrokageUtils.readCrawledQuestionsIds(BING_TOP_RESULTS_FOR_CROKAGE));
		
		queries = readInputQueries();
		//load ground truth
		loadGroundTruthSelectedQueries();
		
		
		for(String query: queries) {  //for each query 
			Set<Integer> acceptedOrMostUpvotedAnswersIds = new LinkedHashSet<>();
			Set<Integer> googleSOQuestionsIds = googleQueriesAndSOIdsMap.get(query);
			if(googleSOQuestionsIds.size()>10) {
				throw new Exception("size should be 10");
			}
			
			for(Integer questionId: googleSOQuestionsIds) {
				Post question = crokageService.findPostById(questionId);
				if(question==null) {
					System.out.println("question not found. Not java or newer than the dataset or with no upvotes "+questionId);
					acceptedOrMostUpvotedAnswersIds.add(0); //very bad recommendation
					continue;
				}
				
				if(question.getAnswerCount()==null || question.getAnswerCount()==0) {
					System.out.println("answers is zero/null for query: "+query+ " and question: "+questionId);
					acceptedOrMostUpvotedAnswersIds.add(0); //very bad recommendation
				}else if(question.getAcceptedAnswerId()!=null) {
					acceptedOrMostUpvotedAnswersIds.add(question.getAcceptedAnswerId());
				}else { //get answer with most upvotes
					Integer mostUpvotedAnswer = crokageService.getMostUpvotedAnswerForQuestionId(questionId);
					acceptedOrMostUpvotedAnswersIds.add(mostUpvotedAnswer);
				}
				
			}
			
			recommendedResults.put(query,acceptedOrMostUpvotedAnswersIds);
		}
		
		CrokageUtils.writeMapToFile4(groundTruthSelectedQueriesAnswersIdsMap,"./data/googleSelectedQueriesGroundTruthAnswersIds.txt");
		CrokageUtils.writeMapToFile4(recommendedResults,"./data/googleSelectedQueriesRecommendedAcceptedOrMostUpvotedAnswersIds.txt");
		
		MetricResult metricResult = new MetricResult();
		metricResult.setApproach("google");
		analyzeResults(recommendedResults,groundTruthSelectedQueriesAnswersIdsMap,metricResult, "google ");
		crokageService.saveMetricResult(metricResult,useCodeInSimCalculus,"metrics for google with accepted answers or most upvoted answers",10,cutoff,numberOfAPIClasses,topApisScoredPairsPercent);
		
		
		
	}




	private void loadGroundTruthSelectedQueries() throws Exception {
		List<UserEvaluation> evaluationsWithBothUsersScales = new ArrayList<>();
		crokageUtils.readGroundTruthFile(evaluationsWithBothUsersScales,QUERIES_AND_SO_ANSWERS_AGREEMENT,4,5);
		if(!productionEnvironment) {
			System.out.println("Number of analyzed posts: "+evaluationsWithBothUsersScales.size());
		}
		
		
		List<UserEvaluation> validEvaluations = new ArrayList<>();
		for(UserEvaluation userEvaluation:evaluationsWithBothUsersScales) {
			int likert1 = userEvaluation.getLikertScaleUser1();
			int likert2 = userEvaluation.getLikertScaleUser2();
			
			int diff = Math.abs(likert1 - likert2);
			if(diff>1) {
				continue;
			}
			
			double meanFull = (likert1+likert2)/(double)2;
			double meanLikert=0d;
			meanLikert = CrokageUtils.round(meanFull,2);
			userEvaluation.setMeanLikert(meanLikert);
			
			if(meanLikert>=4) { 
				
				//verify if post contain APIs
				Post answer = crokageService.findPostById(userEvaluation.getPostId());
				Set<String> classes = crokageUtils.extractClassesFromProcessedCode(answer.getCode());
				if(!classes.isEmpty()) {
					validEvaluations.add(userEvaluation);
				}/*else {
					System.out.println("Discarding from ground truth: "+userEvaluation.getPostId());
				}*/
			}
		}
		
		if(!productionEnvironment) {
			System.out.println("Number of posts containing correct answers: "+validEvaluations.size());
		}
		
		if(queries==null || queries.isEmpty()) {
			queries = readInputQueries(); //selectedQueries
		}
				
		for(String query: queries) {
			/*if(query.contains("How to implement the hashCode and equals method using Apache Commons?")) {
				System.out.println("");
			}*/
			
			Map<String,Set<Integer>> strIdsMap = validEvaluations.stream()
					.filter(e -> Objects.equals(e.getQuery(), query))
					.collect(Collectors.groupingBy(e -> new String(e.getQuery()),
			         	     Collectors.mapping(UserEvaluation::getPostId, Collectors.toSet())));
					
			groundTruthSelectedQueriesAnswersIdsMap.putAll(strIdsMap);		
			
		}
		
		if(!productionEnvironment) {
			System.out.println("Number of queries whose mean likert is > 4: "+groundTruthSelectedQueriesAnswersIdsMap.size());
		}
		
		queries.clear();
		queries.addAll(groundTruthSelectedQueriesAnswersIdsMap.keySet());
		
		System.out.println("Size of ground truth considering only posts with api calls:"+groundTruthSelectedQueriesAnswersIdsMap.size()+ " \nNew queries list size: "+queries.size());
	}
	


	private void processBingResultsToStandardFormat() throws IOException {
		//List<String> allLinesCrokage = Files.readAllLines(Paths.get(BING_TOP_RESULTS_FOR_CROKAGE_RAW_JSON));
		//List<String> allLinesNLP2API = Files.readAllLines(Paths.get(BING_TOP_RESULTS_FOR_NLP2API_RAW_JSON));
		List<String> allLinesBiker = Files.readAllLines(Paths.get(BING_TOP_RESULTS_FOR_BIKER_RAW_JSON));
		
		//Map<String,Set<Integer>> queriesAndSOIdsMap = processBingResult(allLinesCrokage);
		//Map<String,Set<Integer>> queriesAndSOIdsMap2 = processBingResult(allLinesNLP2API);
		Map<String,Set<Integer>> queriesAndSOIdsMap3 = processBingResult(allLinesBiker);
		
		//CrokageUtils.printMapToFile(queriesAndSOIdsMap,BING_TOP_RESULTS_FOR_CROKAGE);
		//CrokageUtils.printMapToFile(queriesAndSOIdsMap2,BING_TOP_RESULTS_FOR_NLP2API);
		CrokageUtils.writeMapToFile4(queriesAndSOIdsMap3,BING_TOP_RESULTS_FOR_BIKER);
				
	}






	private Map<String, Set<Integer>> processBingResult(List<String> allLinesCrokage) {
		String[] parts;
		String query="";
		String questionId;
		Set<Integer> questionsIds = new LinkedHashSet<>();
		Map<String,Set<Integer>> queriesAndSOIdsMap = new LinkedHashMap<>();
		
		for(String line: allLinesCrokage) {
			if(line.trim().startsWith("\"originalQuery\":")) {
				if(!questionsIds.isEmpty()) {
					queriesAndSOIdsMap.put(query, questionsIds);
					questionsIds = new LinkedHashSet<>();
				}
				
				parts = line.trim().split("\"originalQuery\": \"");
				parts = parts[1].split(" site:stackoverflow.com\"");
				query = parts[0].replace("java ", "");
				
			}
			if(line.trim().startsWith("\"url\": \"https://stackoverflow.com/questions/")) {
				parts = line.trim().split("\"url\": \"https://stackoverflow.com/questions/");
				parts = parts[1].split("/");
				questionId = parts[0];
				if(crokageUtils.isNumeric(questionId)) {
					questionsIds.add(new Integer(questionId));
				}
				
			}
			
		}
		return queriesAndSOIdsMap;
	}







	private void processCrawledQuestionsIDsAndBuilExcelFileForEvaluation() throws Exception {
		Map<String,Set<Integer>> googleQueriesAndSOIdsMap = new LinkedHashMap<>();
		Map<String,Set<Integer>> seQueriesAndSOIdsMap = new LinkedHashMap<>();
		Map<String,Set<Integer>> bingQueriesAndSOIdsMap = new LinkedHashMap<>();
		
		Map<String,Set<Integer>> allQueriesAndSOIdsMap = new LinkedHashMap<>();
		Map<String, Set<Integer>> exceptionQueriesSOQuestionIds = new LinkedHashMap<>();
		
		Map<String,List<Post>> allQueriesAndUpVotedCodedAnswersMap = new LinkedHashMap<>();
		
		googleQueriesAndSOIdsMap = crokageUtils.readCrawledQuestionsIds(GOOGLE_TOP_RESULTS_FOR_NLP2API);
		googleQueriesAndSOIdsMap.putAll(crokageUtils.readCrawledQuestionsIds(GOOGLE_TOP_RESULTS_FOR_CROKAGE));
		seQueriesAndSOIdsMap = crokageUtils.readCrawledQuestionsIds(SE_TOP_RESULTS_FOR_NLP2API);
		seQueriesAndSOIdsMap.putAll(crokageUtils.readCrawledQuestionsIds(SE_TOP_RESULTS_FOR_CROKAGE));
		bingQueriesAndSOIdsMap = crokageUtils.readCrawledQuestionsIds(BING_TOP_RESULTS_FOR_NLP2API);
		bingQueriesAndSOIdsMap.putAll(crokageUtils.readCrawledQuestionsIds(BING_TOP_RESULTS_FOR_CROKAGE));
		
		queries = readInputQueries();
		
		for(String query: queries) {  //for each query 
			
			//merge lists
			Set<Integer> googleSOQuestionsIds = googleQueriesAndSOIdsMap.get(query);
			Set<Integer> seSOQuestionsIds = seQueriesAndSOIdsMap.get(query);
			Set<Integer> bingSOQuestionsIds = bingQueriesAndSOIdsMap.get(query);
			
			Set<Integer> allSOQuestionsIds = new LinkedHashSet<>();
			Set<Integer> soQuestionsExceptionsIds = new LinkedHashSet<>();
			
			if(googleSOQuestionsIds==null || googleSOQuestionsIds.isEmpty()) {
				throw new Exception("Query not found for google ?!: "+query);
				//continue;
			}
			if(seSOQuestionsIds==null || seSOQuestionsIds.isEmpty()) {
				System.out.println("Query not found for SE ?!: "+query);
			}
			if(bingSOQuestionsIds==null || bingSOQuestionsIds.isEmpty()) {
				throw new Exception("Query not found for bing ?!: "+query);
			}
			
			if(seSOQuestionsIds!=null) {
				allSOQuestionsIds.addAll(seSOQuestionsIds);
			}
			if(googleSOQuestionsIds!=null) {
				allSOQuestionsIds.addAll(googleSOQuestionsIds);
			}
			if(bingSOQuestionsIds!=null) {
				allSOQuestionsIds.addAll(bingSOQuestionsIds);
			}
			
			
			allQueriesAndSOIdsMap.put(query,allSOQuestionsIds);
			
			List<Post> soQuestionsForQuery = crokageService.findPostsById(new ArrayList(allSOQuestionsIds));
			List<Post> upVotedAnswersWithCode = new ArrayList<>();
			
			for(Post question:soQuestionsForQuery) {
				/*if(question.getId().equals(40439065)) {
					System.out.println();
				}*/
				if(question.getAnswerCount()!=null && question.getAnswerCount()>0 && question.getScore()>0) {
					List<Post> answers = crokageService.findUpVotedAnswersWithCodeByQuestionId(question.getId());
					for(Post answer:answers) {
						Post parent = crokageService.findPostById(answer.getParentId());
						answer.setParent(parent);
					}
					upVotedAnswersWithCode.addAll(answers);
				}else {
					soQuestionsExceptionsIds.add(question.getId());
				}
			}
			allQueriesAndUpVotedCodedAnswersMap.put(query, upVotedAnswersWithCode);	
			exceptionQueriesSOQuestionIds.put(query, soQuestionsExceptionsIds);
		}
		
		StringBuilder lines = new StringBuilder("");
		Set<String> queriesIdsSet = exceptionQueriesSOQuestionIds.keySet();
		for(String query: queriesIdsSet) {
			lines.append("\n"+query+" >> ");
			 Set<Integer> ids = exceptionQueriesSOQuestionIds.get(query);
			 for(Integer id: ids) {
				lines.append(id+" ");
			}
			allQueriesAndSOIdsMap.get(query).removeAll(ids);
			System.out.println("\n\nQuery: "+query);
			System.out.println("\nExceptions: "+ids);
			System.out.println("\nTotal ids after merge and without exceptions: "+allQueriesAndSOIdsMap.get(query).size()+ " - "+allQueriesAndSOIdsMap.get(query)); 
		}
		
		
		
		crokageUtils.writeStringContentToFile(lines.toString(), ALL_QUESTIONS_EXCEPTIONS_FOR_NLP2API);
	
		//build an excel file 
		crokageUtils.buildCsvQuestionsForEvaluation(QUERIES_AND_SO_ANSWERS_TO_EVALUATE+".csv",allQueriesAndUpVotedCodedAnswersMap);
		
		
	}




	private void crawlBingForRelatedQuestionsIdsDatasetBIKER() throws Exception {
		List<String> queries = Files.readAllLines(Paths.get(INPUT_QUERIES_FILE_BIKER));

		try (PrintWriter out = new PrintWriter(BING_TOP_RESULTS_FOR_BIKER_RAW_JSON)) {
			for (String query : queries) {
				SearchResults result = BingWebSearch.SearchWeb(prepareQueryForBing(query));
				out.println(BingWebSearch.prettify(result.jsonResponse));
			}
		}
		
	}


	
	private void crawlBingForRelatedQuestionsIdsDatasetCrokage() throws Exception {
		List<String> queries = Files.readAllLines(Paths.get(INPUT_QUERIES_FILE_CROKAGE));

		try (PrintWriter out = new PrintWriter(BING_TOP_RESULTS_FOR_CROKAGE_RAW_JSON)) {
			for (String query : queries) {
				SearchResults result = BingWebSearch.SearchWeb(prepareQueryForBing(query));
				out.println(BingWebSearch.prettify(result.jsonResponse));
			}
		}
		
	}



	
	private void crawlBingForRelatedQuestionsIdsDatasetNLP2Api() throws Exception {
		List<String> queries = Files.readAllLines(Paths.get(INPUT_QUERIES_FILE_NLP2API));

		try (PrintWriter out = new PrintWriter(BING_TOP_RESULTS_FOR_NLP2API_RAW_JSON)) {
			for (String query : queries) {
				SearchResults result = BingWebSearch.SearchWeb(prepareQueryForBing(query));
				out.println(BingWebSearch.prettify(result.jsonResponse));
			}
		}
	}
	

	private void crawlStackExchangeForRelatedQuestionsIdsDatasetNLP2Api() throws IOException {
		List<String> queries = Files.readAllLines(Paths.get(INPUT_QUERIES_FILE_NLP2API));
		
		try (PrintWriter out = new PrintWriter(SE_TOP_RESULTS_FOR_NLP2API)) {
			for(String query: queries) {
				Set<Integer> soQuestionsIds = executeStackExchangeSearch(crokageUtils.processQuery(query),10);
				out.print("\n"+query+ " >> ");
				for(Integer soQuestionId: soQuestionsIds) {
					out.print(soQuestionId+ " ");
				}
			}
		}
	}
		
	
	private void crawlStackExchangeForRelatedQuestionsIdsDatasetCrokage() throws IOException {
		List<String> queries = Files.readAllLines(Paths.get(INPUT_QUERIES_FILE_CROKAGE));
		
		try (PrintWriter out = new PrintWriter(SE_TOP_RESULTS_FOR_CROKAGE)) {
			for(String query: queries) {
				Set<Integer> soQuestionsIds = executeStackExchangeSearch(crokageUtils.processQuery(query),10);
				out.print("\n"+query+ " >> ");
				for(Integer soQuestionId: soQuestionsIds) {
					out.print(soQuestionId+ " ");
				}
			}
		}
		
	}



	private void crawlGoogleForRelatedQuestionsIdsDatasetCrokage() throws Exception {
		List<String> queries = Files.readAllLines(Paths.get(INPUT_QUERIES_FILE_CROKAGE));
		
		try (PrintWriter out = new PrintWriter(GOOGLE_TOP_RESULTS_FOR_CROKAGE)) {
			for(String query: queries) {
				Set<Integer> soQuestionsIds = executeGoogleSearch(prepareQueryForCrawler(query),10);
				out.print("\n"+query+ " >> ");
				for(Integer soQuestionId: soQuestionsIds) {
					out.print(soQuestionId+ " ");
				}
			}
		}
	}
	



	
	private void crawlGoogleForRelatedQuestionsIdsDatasetNLP2Api() throws Exception {
		List<String> queries = Files.readAllLines(Paths.get(CROKAGE_HOME+"/data/inputQueriesNlp2Api-201-300.txt"));
		
		try (PrintWriter out = new PrintWriter(CROKAGE_HOME+"/data/googleNLP2ApiResults-201-300.txt")) {
			for(String query: queries) {
				Set<Integer> soQuestionsIds = executeGoogleSearch(prepareQueryForCrawler(query),10);
				out.print("\n"+query+ " >> ");
				for(Integer soQuestionId: soQuestionsIds) {
					out.print(soQuestionId+ " ");
				}
			}
		}
	}
	
	
	
	
	
	public String prepareQueryForCrawler(String query) {
		String completeQuery = "";
		
		Boolean containJavaToken = Pattern.compile(".*\\bjava\\b.*").matcher(query.toLowerCase()).find();
		
		if(!containJavaToken) {
			completeQuery += "java ";
		}
		
		completeQuery += query;
		
		return completeQuery;
	}
	
	public String prepareQueryForBing(String query) {
		String completeQuery = "";
		
		Boolean containJavaToken = Pattern.compile(".*\\bjava\\b.*").matcher(query.toLowerCase()).find();
		
		if(!containJavaToken) {
			completeQuery += "java ";
		}
		
		completeQuery += query + " site:stackoverflow.com";
		
		return completeQuery;
	}


	
	/**
	 * Google Search
	 * @param googleQuery
	 * @return Set<Integer>
	 */
	public Set<Integer> executeGoogleSearch(String googleQuery, Integer numberOfGoogleResults) {
		System.out.println("Initiating Google Search... Using query: "+googleQuery);
		Set<Integer> soQuestionsIds = new LinkedHashSet<>();
		
		try {
			SearchQuery searchQuery = new SearchQuery(googleQuery, "stackoverflow.com", numberOfGoogleResults);
			        //.site("https://stackoverflow.com")
			SearchResult result = googleWebSearch.search(searchQuery,useProxy);
			List<String> urls = result.getUrls();
			CrokageUtils.identifyQuestionsIdsFromUrls(urls,soQuestionsIds);
						
		} catch (Exception e) {
			System.out.println("Error ... "+e);
			e.printStackTrace();
		}
		
		return soQuestionsIds;
		
	}
	
	private Set<Integer> executeStackExchangeSearch(String query, Integer numberOfGoogleResults2) {
		 StackExchangeApiQueryFactory queryFactory = StackExchangeApiQueryFactory.newInstance("gJMxDoCH9XhURvF7a3F4Kg((",StackExchangeSite.STACK_OVERFLOW);  
         Paging paging = new Paging(1, 20);  
         String filterName = "default";  
         List<String> tagged = new ArrayList<String>();  
         tagged.add("java");  
         List<String> nottagged = new ArrayList<String>();  
         PagedList<Question> questions = queryFactory  
                   .newAdvanceSearchApiQuery()
                   .withFilter(filterName)
                   .withSort(QuestionSortOrder.MOST_RELEVANT)
                   .withQuery(query)
                   .withMinAnswers(1)  
                   .withMinViews(100)
                   .withTags(tagged).list();  
         
         Set<Integer> soQuestionsIds = new LinkedHashSet<>();
		
         int i=0;
         for(Question question:questions) {
        	System.out.println(question.getQuestionId()+ " - "+question.getTitle());
        	soQuestionsIds.add(((Long)(question.getQuestionId())).intValue());
        	i++;
        	if(i==20) {
        		break;
        	}
         }
		return soQuestionsIds;
	}





	private void checkConditions() throws Exception {
		File file1 = new File(BIKER_OUTPUT_QUERIES_FILE);
		boolean exists1 = file1.exists();
		if(!exists1) {
			throw new Exception("File "+file1.getAbsolutePath()+ " must exist. Has it been provided ? ");
		}
		
		File file2 = new File(NLP2API_OUTPUT_QUERIES_FILE);
		boolean exists2 = file2.exists();
		if(!exists1) {
			throw new Exception("File "+file2.getAbsolutePath()+ " must exist. Has it been provided ? ");
		}
		
		File file3 = new File(RACK_OUTPUT_QUERIES_FILE);
		boolean exists3 = file3.exists();
		if(!exists1) {
			throw new Exception("File "+file3.getAbsolutePath()+ " must exist. Has it been provided ? ");
		}
		
	}




	private void readAnswersIdsParentsMap() throws IOException {
		//long initTime = System.currentTimeMillis();
		List<String> idsAndids = Files.readAllLines(Paths.get(SO_ANSWERS_IDS_PARENT_IDS_MAP));
		allAnswersWithUpvotesIdsParentIdsMap = new HashMap<>();
		crokageUtils.readWordsFromFileToMap2(allAnswersWithUpvotesIdsParentIdsMap, idsAndids);
		//crokageUtils.reportElapsedTime(initTime,"readAnswersIdsParentsMap");
	}




	private void generateAnswersIdsParentsMap() throws IOException {
		long initTime = System.currentTimeMillis();
		readAnswersIdsParentsMap();
		crokageUtils.writeMapToFile3(allAnswersWithUpvotesIdsParentIdsMap, SO_ANSWERS_IDS_PARENT_IDS_MAP);
		crokageUtils.reportElapsedTime(initTime,"generateAnswersIdsParentsMap");
	}




	private void readQuestionsIdsTitlesMap() throws IOException {
		long initTime = System.currentTimeMillis();
		List<String> idsAndWords = Files.readAllLines(Paths.get(SO_QUESTIONS_IDS_TITLES_MAP));
		crokageUtils.readWordsFromFileToMap(allQuestionsIdsTitlesMap,idsAndWords);
		crokageUtils.reportElapsedTime(initTime,"readQuestionsIdsTitlesMap");
	}

	
	private void readAllAnswersIdsContentsParentContentsMap() throws IOException {
		long initTime = System.currentTimeMillis();
		System.out.println("Reading all answers ids and contents and their parent contents from file...");
		List<String> idsAndContents = Files.readAllLines(Paths.get(SO_ANSWERS_IDS_CONTENTS_PARENT_CONTENTS_MAP));
		crokageUtils.readWordsFromFileToMap(allAnswersIdsContentsParentContentsMap,idsAndContents);
		/*String titleTest = allQuestionsIdsTitlesMap.get(43966301);
		System.out.println(titleTest);*/
		crokageUtils.reportElapsedTime(initTime,"readAllAnswersIdsContentsParentContentsMap");
	}

	private void generateQuestionsIdsContentMap() throws FileNotFoundException {
		long initTime = System.currentTimeMillis();
		//loadAllQuestionsIdsContentsAndAcceptedOrMostUpvotedAnswerContentsMap();
		loadAllAnswersIdsContentsAndParentContentsMap();
		crokageUtils.writeMapToFile2(allAnswersIdsContentsParentContentsMap, SO_ANSWERS_IDS_CONTENTS_PARENT_CONTENTS_MAP);
		crokageUtils.reportElapsedTime(initTime,"generateQuestionsIdsTitlesMap");
		
	}


	private void generateQuestionsIdsTitlesMap() throws FileNotFoundException {
		long initTime = System.currentTimeMillis();
		loadAllQuestionsIdsTitles();
		crokageUtils.writeMapToFile2(allQuestionsIdsTitlesMap, SO_QUESTIONS_IDS_TITLES_MAP);
		crokageUtils.reportElapsedTime(initTime,"generateQuestionsIdsTitlesMap");
	}

	
	private void loadAllAnswersIdsContentsAndParentContentsMap() {
		//load threads whose questions has score > 0 
		List<Bucket> buckets = crokageService.getUpvotedAnswersIdsContentsAndParentContents();
		System.out.println("buckets recuperados: "+buckets.size());
		int count=0;
		for(Bucket bucket: buckets) {
			count++;
			if(count%10000==0) {
				System.out.println(count+" buckets processed...");
			}
			String content = loadBucketContent(bucket);
			allAnswersIdsContentsParentContentsMap.put(bucket.getId(), content);
			
		}
		
	}


	private String loadBucketContent(Bucket bucket) {
		StringBuffer stringBuffer = new StringBuffer();
		if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
			stringBuffer.append(bucket.getParentProcessedTitle());
		}
		if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getParentProcessedBody());
		}
		if(!StringUtils.isBlank(bucket.getParentProcessedCode())) {
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getParentProcessedCode());
		}
		if(!StringUtils.isBlank(bucket.getProcessedBody())) {
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getProcessedBody());
		}
		if(!StringUtils.isBlank(bucket.getProcessedCode())) {
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getProcessedCode());
		}
		return stringBuffer.toString();
	}

	/*
	private void loadAllQuestionsIdsContentsAndAcceptedOrMostUpvotedAnswerContentsMap() {
		//load threads whose questions has score > 0 
		List<Bucket> buckets = crokageService.getQuestionsProcessedTitlesBodiesCodes();
		for(Bucket bucket: buckets) {
			StringBuffer stringBuffer = new StringBuffer();
			if(!StringUtils.isBlank(bucket.getProcessedTitle())) {
				stringBuffer.append(bucket.getProcessedTitle());
			}
			if(!StringUtils.isBlank(bucket.getProcessedBody())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBody());
			}
			if(!StringUtils.isBlank(bucket.getProcessedCode())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedCode());
			}
			
			Post answer=null;
			//append content of accepted answer or most upvoted answer
			if(bucket.getAcceptedAnswerId()!=null) {
				answer = crokageService.findPostById(bucket.getAcceptedAnswerId());
				if(answer!=null) {
					if(StringUtils.isBlank(answer.getProcessedCode())){
						answer=null;
					}
				}
			}	
			if(answer==null) {	
			    //get answer with most upvotes
				answer = crokageService.getMostUpvotedAnswerWithCodeForQuestion(bucket.getId());
			}
			if(answer==null) {
				System.out.println("no answer with code for question: "+bucket.getId());
				continue;
			}
			if(!StringUtils.isBlank(answer.getProcessedBody())) {
				stringBuffer.append(" ");
				stringBuffer.append(answer.getProcessedBody());
			}
			
			if(!StringUtils.isBlank(answer.getProcessedCode())) {
				stringBuffer.append(" ");
				stringBuffer.append(answer.getProcessedCode());
			}
			allAnswersIdsContentsParentContentsMap.put(bucket.getId(), stringBuffer.toString());
			
		}
		
	}*/


	private void buildLuceneIndex() {
		long start = System.currentTimeMillis();
		//String docs = "/home/rodrigo/tmp/sodirectory";
		//String index = "/home/rodrigo/tmp/sodirindex";
		IndexLucene indexer = new IndexLucene(SO_DIRECTORY_INDEX, SO_DIRECTORY_FILES);
		indexer.indexCorpusFiles();
		crokageUtils.reportElapsedTime(start, "buildLuceneIndex");
		
	}




	private void tester() throws Exception {
		queries = readInputQueries();
		for(String query: queries) {
			query = CrokageUtils.processQuery(query);
			System.out.println(query);
		}
		System.out.println("");
		
		logger.debug("debug");
		System.out.println("info");
		logger.warn("warn");
		logger.error("error");
	}



	/*
	 * Used to generate the vec model. After generate this file, use fastText command in command line to build the vectors in a txt file:
	 * ./fasttext print-word-vectors /home/rodrigo/tmp/fastTextModel.bin < /home/rodrigo/tmp/soSetOfWords.txt > /home/rodrigo/tmp/soContentWordVec.txt
	 */
	private void buildSOSetOfWords() throws IOException {
		long initTime = System.currentTimeMillis();
		System.out.println("Reading all words from IDFs file...");
		String[] parts;
		StringBuilder str = new StringBuilder();
		List<String> wordsAndIDFs = Files.readAllLines(Paths.get(SO_IDF_VOCABULARY));
		for(String line: wordsAndIDFs) {
			parts = line.split(" ");
			str.append(parts[0]);
			str.append("\n");
		}
		wordsAndIDFs = null;
		System.out.println("Done reading idf words. Now writing them to a file");
		CrokageUtils.writeStringContentToFile(str.toString(), SO_SET_OF_WORDS);
		crokageUtils.reportElapsedTime(initTime,"readSoContentWordVectors");
		
	}




	private void buildIDFVocabulary() throws IOException {
		//each post has been saved in each line
		Set<String> wordsSet = new HashSet<>();
		System.out.println("reading contents lines...");
		List<String> contentLines = Files.readAllLines(Paths.get(SO_CONTENT_FILE));
		System.out.println("lines:  "+contentLines.size());
		for(String line: contentLines) {
			wordsSet.addAll(Arrays.stream(line.split(" +")).collect(Collectors.toSet()));
		}
		contentLines=null;
		System.out.println("Wordset built. Now calculating idfs for "+wordsSet.size()+ " words");
		Map<String, Double> idfs = new IDFCalc(SO_DIRECTORY_INDEX, wordsSet).calculateIDFOnly();
		
		//all file into a String
		System.out.println("writing idfs to file...");
		crokageUtils.writeMapToFile(idfs,SO_IDF_VOCABULARY );
		
	}


	private void buildIDFVocabularyOld() throws IOException {
		//each post has been saved in each line
		List<List<String>> documents = new ArrayList<>();
		Set<String> wordsSet = new HashSet<>();
		System.out.println("reading contents lines...");
		
		List<String> contentLines = Files.readAllLines(Paths.get(SO_CONTENT_FILE));
		for(String line: contentLines) {
			wordsSet.addAll(Arrays.stream(line.split(" +")).collect(Collectors.toSet()));
		}
		
		StringBuilder idfContent= new StringBuilder();
		
		//all file into a String
		System.out.println("building idf. Total number of words: "+wordsSet.size());
		int i=0;
		for(String word: wordsSet) {
			double idf = tfidfCalculator.idf2(contentLines, word);
			idfContent.append(word);
			idfContent.append(" ");
			idfContent.append(idf);
			idfContent.append("\n");
			i++;
			if(i%100000==0) {
				System.out.println(i+ " idfs calculated...");
			}
		}
		System.out.println("writing idf...");
		crokageUtils.writeStringContentToFile(idfContent.toString(),SO_IDF_VOCABULARY );
		
	}




	private void readSoContentWordVectorsForAllWords() throws IOException {
		readSOContentWordAndVectorsLines();
		
		long initTime = System.currentTimeMillis();
		System.out.println("Parsing vectors...");
		CrokageUtils.getVectorsFromLines(wordsAndVectorsLines,soContentWordVectorsMap);
		crokageUtils.reportElapsedTime(initTime,"parsing vectors");
		
	}


	private void readSOContentWordAndVectorsLines() throws IOException {
		System.out.println("Reading SO word vectors file...");
		long initTime = System.currentTimeMillis();
		wordsAndVectorsLines.addAll(Files.readAllLines(Paths.get(SO_CONTENT_WORD_VECTORS)));
		crokageUtils.reportElapsedTime(initTime,"readSOContentWordAndVectorsLines. Total number of words: "+wordsAndVectorsLines.size());
		
		
	}




	private void buildSODirectoryFiles() throws FileNotFoundException {
		//load all SO questions
		List<Integer> allJavaPostsIds = crokageService.findAllPostsIds();
		List<Integer> somePostsIds=null; 
		int maxQuestions = 1000;
		
		int init=0;
		int end=maxQuestions;
		int remainingSize = allJavaPostsIds.size();
		System.out.println("all posts ids:  "+remainingSize);
		while(remainingSize>maxQuestions){ //this is for memory issues
			somePostsIds = allJavaPostsIds.subList(init, end);
			//preProcessService.processPosts(somePostsIds);
			processPosts(somePostsIds);
			remainingSize = remainingSize - maxQuestions;
			init+=maxQuestions;
			end+=maxQuestions;
			somePostsIds=null;
			if(init%100000==0) {
				System.out.println("Processed "+init);
			}
			
		}
		somePostsIds = allJavaPostsIds.subList(init, init+remainingSize);
		//remaining
		//preProcessService.processPosts(somePostsIds);
		processPosts(somePostsIds);
	
		
	}


	private void generateTrainingFileToFastText() throws FileNotFoundException {
		//load all SO questions
		List<Integer> allJavaPostsIds = crokageService.findAllPostsIds();
		try (PrintWriter out = new PrintWriter(SO_CONTENT_FILE)) {
			List<Integer> somePostsIds=null; 
			int maxQuestions = 1000;
			
			int init=0;
			int end=maxQuestions;
			int remainingSize = allJavaPostsIds.size();
			System.out.println("all posts ids:  "+remainingSize);
			while(remainingSize>maxQuestions){ //this is for memory issues
				somePostsIds = allJavaPostsIds.subList(init, end);
				//preProcessService.processPosts(somePostsIds);
				processPosts(somePostsIds, out);
				remainingSize = remainingSize - maxQuestions;
				init+=maxQuestions;
				end+=maxQuestions;
				somePostsIds=null;
				if(init%100000==0) {
					System.out.println("Processed "+init);
				}
				
			}
			somePostsIds = allJavaPostsIds.subList(init, init+remainingSize);
			//remaining
			//preProcessService.processPosts(somePostsIds);
			processPosts(somePostsIds, out);
		
		}
	}
	
	/*
	 * Each post in one line 
	 */
	public void processPosts(List<Integer> somePosts, PrintWriter out) throws FileNotFoundException {
		List<Post> some = crokageService.findPostsById(somePosts);
		for (Post post : some) {
			String postContent = getPostContent(post);
			if(!StringUtils.isBlank(postContent)) {
				out.println(postContent);
			}
			
		}
	}
	
	/*
	 * Each post in one file 
	 */
	public void processPosts(List<Integer> somePosts) throws FileNotFoundException {
		List<Post> some = crokageService.findPostsById(somePosts);
		for (Post post : some) {
			String postContent = getPostContent(post);
			if(!StringUtils.isBlank(postContent)) {
				try (PrintWriter out = new PrintWriter(SO_DIRECTORY_FILES+"/"+post.getId()+".txt")) {
					out.println(postContent);
				}
			}
		}
	}

	private String getPostContent(Post post) {
		StringBuilder postContent = new StringBuilder();
		if(post.getPostTypeId().equals(1) && !StringUtils.isBlank(post.getProcessedTitle())) { //question
			postContent.append(post.getProcessedTitle());
		}
		if(!StringUtils.isBlank(post.getProcessedBody())) {
			postContent.append(" ");
			postContent.append(post.getProcessedBody());
		}
		if(!StringUtils.isBlank(post.getProcessedCode())) {
			postContent.append(" ");
			postContent.append(post.getProcessedCode());
		}
		return StringUtils.normalizeSpace(postContent.toString());
	}




	private void reduceBigMapFileToMininumAPIsCount() throws Exception {
		//load the inverted index
		long initTime = System.currentTimeMillis();
		
		loadInvertedIndexFile();
		
		//filter by cutoff and sort in descending order
		filteredSortedMapAnswersIds = bigMapApisAnswersIds.entrySet().stream()
				.filter( e -> e.getValue().size() > cutoff)
		        .sorted( (e1,e2)->  Integer.compare(e2.getValue().size(), e1.getValue().size()))
		        .collect(Collectors.toMap(
		                Map.Entry::getKey,
		                Map.Entry::getValue,
		                (a,b) -> {throw new AssertionError();},
		                LinkedHashMap::new
		        )); 
		
		//remove String
		//filteredSortedMapAnswersIds.remove("String");
		
		//generate reduced map
		//CrokageUtils.printMapInfosIntoCVSFile(filteredSortedMapAnswersIds,REDUCED_MAP_INVERTED_INDEX_APIS_FILE_PATH);
		crokageUtils.reportElapsedTime(initTime,"reduceBigMapFileToMininumAPIsCount");
	}




	private void resetAPIExtractors() {
		rackQueriesApisMap.clear();
		bikerQueriesApisClassesMap.clear();
		nlp2ApiQueriesApisMap.clear();
	}




	
	
	private Set<Integer> getTopKRelevantAnswers(Set<Integer> candidateAnswersIds, double[][] matrix1, double[][] idf1, Integer key, String query) throws IOException {
		Set<Bucket> candidateBuckets = new HashSet<>();
		for(Integer answerId: candidateAnswersIds) {
			candidateBuckets.add(allAnswersWithUpvotesAndCodeBucketsMap.get(answerId));
		}
		
		
		String comparingTitle;
		bucketsIdsScores.clear();
		String parentTitle;
		String comparingContent;
		answersIdsScores.clear();
		double maxSimPair=0;
		double maxApiScore=0;
		methodsCounterMap.clear();
		classesCounterMap.clear();
		
		String rawQuery = queries.get(key-1);
		
		for(Bucket bucket:candidateBuckets) {
			//second ranking
			try {
				
				/*//parentTitle =  allQuestionsIdsTitlesMap.get(bucket.getParentId());
				comparingContent = loadBucketContent(bucket);		
				
				//get vectors for query2 words
				double[][] matrix2 = CrokageUtils.getMatrixVectorsForQuery(comparingContent,soContentWordVectorsMap);
				
				//get idfs for query2
				double[][] idf2 = CrokageUtils.getIDFMatrixForQuery(comparingContent, soIDFVocabularyMap);
				
				double simPair = CrokageUtils.round(Matrix.simDocPair(matrix1,matrix2,idf1,idf2),6);*/
				
				double simPair = bucket.getTitleScore();
				
				if(simPair>maxSimPair) {
					maxSimPair=simPair;
				}
				
				double apiAnswerPairScore = topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId());
				//double apiAnswerPairScore = 0;
				
				if(apiAnswerPairScore>maxApiScore) {
					maxApiScore=apiAnswerPairScore;
				}
				
				//countClasses(bucket.getCode()); //use recommended to score
				countMethods(bucket.getCode(),bucket);
				
				answersIdsScores.put(bucket.getId(), simPair);
				
			} catch (Exception e) {
				System.out.println("error here... post: "+bucket.getId());
				throw e;
			}
			
		}
		
		//crokageUtils.reportElapsedTime(initTime,"getTopKRelevantAnswers 1");
		//initTime = System.currentTimeMillis();
		
		if(!productionEnvironment) {
			reportCommonMethods();
		}
		
		//normalization and other relevance boosts
		for(Bucket bucket:candidateBuckets) {
				double simPair = answersIdsScores.get(bucket.getId());
				simPair = (simPair / maxSimPair); //normalization
				
				double apiAnswerPairScore = topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId());
				apiAnswerPairScore = (apiAnswerPairScore/maxApiScore); //normalization
				//double apiAnswerPairScore=0;
				
				double finalScore = BotComposer.calculateFinalScore(simPair,bucket,methodsCounterMap,apiAnswerPairScore);
				
				
				//**pontuar intercecao de substrings entrey a query e o body ou codigo da resposta
				//ex, query "Use Scanner to read a list of comma-separated values" Respsotas boas contem "comma" ou "scanner"
				
				
				
				//not good results yet...
				/*String processedCode = bucket.getProcessedCode();
				for(int i=0; i<numberOfAPIClasses;i++) {
					String bikerMethod = bikerTopMethods.get(i);
					if(processedCode.contains(bikerMethod) && codeContainAnyClass(processedCode)) {
						double methodBoost = (float)1/((bikerTopMethods.indexOf(bikerMethod)+1));
						simPair += crokageUtils.round(methodBoost,6);
						break; //only once
					}
				}*/
				
				
				answersIdsScores.put(bucket.getId(), finalScore);
				
				
				if(rawQuery.contains("How can I insert an element in array at a given position?")) {
					
					if(bucket.getId().equals(7074420)
						|| bucket.getId().equals(20904059)
						|| bucket.getId().equals(7074420)
						|| bucket.getId().equals(7384948)
						|| bucket.getId().equals(7384949)
						|| bucket.getId().equals(7384990)
						|| bucket.getId().equals(22785344)
						|| bucket.getId().equals(24887047)
						|| bucket.getId().equals(34756034)
						|| bucket.getId().equals(36966011)
						|| bucket.getId().equals(21054277)
						|| bucket.getId().equals(44483952)
						|| bucket.getId().equals(44483952)
						|| bucket.getId().equals(8514146)
						|| bucket.getId().equals(9823658)
						|| bucket.getId().equals(9823533)){
						
						System.err.println("answer: "+bucket.getId()+ " - score: "+finalScore);
						
					}
				}
				
				
				if(rawQuery.contains("How do I compress or zip a directory recursively?")) {
					
					if(bucket.getId().equals(740382)
						|| bucket.getId().equals(6472872)
						|| bucket.getId().equals(1399432)
						|| bucket.getId().equals(1402051)
						|| bucket.getId().equals(9257957)
						|| bucket.getId().equals(9437806)
						|| bucket.getId().equals(16646691)
						|| bucket.getId().equals(19683083)
						|| bucket.getId().equals(9325036)
						|| bucket.getId().equals(13912353)
						|| bucket.getId().equals(29116934)
						|| bucket.getId().equals(13476752)
						|| bucket.getId().equals(23524963)
						|| bucket.getId().equals(15969374)) {
							
						System.err.println("answer: "+bucket.getId()+ " - score: "+finalScore);
						
					}
				}
				
				
				
				
				//observar ex: https://stackoverflow.com/questions/1053467/how-do-i-save-a-string-to-a-text-file-using-java
				
				//score da pergunta : parentUpVotesScore
				
				//codigo deve ter chamada de método com ponto *.*
				
				//poucas linhas de codigo (desconsiderar imports . pontuar imports )
				
				
				//comentarios com sentimento positivo
		
			
		}
		
		//sort scores in descending order 
		Map<Integer,Double> topSimilarAnswers = answersIdsScores.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       //.limit(topSimilarAnswersNumber)
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		candidateAnswersIds = topSimilarAnswers.keySet();
		
		candidateBuckets=null;
		return candidateAnswersIds;
	}


	
	




	
	
	private Set<Integer> getTopKRelevantAnswersOld(Set<Integer> candidateAnswersIds, List<String> topMethods, double[][] matrix1, double[][] idf1, Integer key, String query, Set<String> topClasses) throws IOException {
		//fetch answers fields
		//long initTime = System.currentTimeMillis();
		List<Bucket> answerBuckets=new ArrayList<>();
		for(Integer answerId: candidateAnswersIds) {
			answerBuckets.add(allAnswersWithUpvotesAndCodeBucketsMap.get(answerId));
		}
		//crokageUtils.reportElapsedTime(initTime,"getBucketsByIds");
		/*List<Bucket> answerBuckets = crokageService.getBucketsByIds(new HashSet(candidateAnswersIds));
		crokageUtils.reportElapsedTime(initTime,"getBucketsByIds");
		initTime = System.currentTimeMillis();*/
		
		
		
		/*
		if(!iHaveALotOfMemory) {
			allWordsSetForBuckets = getAllWordsForBuckets(allAnswersWithUpvotesAndCodeBucketsMap.values());
			crokageUtils.readVectorsFromSOMapForWords(soContentWordVectorsMap,allWordsSetForBuckets,wordsAndVectorsLines);
		}*/
		
		String parentTitle;
		String comparingContent;
		answersIdsScores.clear();
		double maxSimPair=0;
		double maxApiScore=0;
		methodsCounterMap.clear();
		classesCounterMap.clear();
		//answers with code
		for(Bucket bucket:answerBuckets) {
			
			//second ranking
			try {
				
				//parentTitle =  allQuestionsIdsTitlesMap.get(bucket.getParentId());
				comparingContent = loadBucketContent(bucket);		
				
				//get vectors for query2 words
				double[][] matrix2 = CrokageUtils.getMatrixVectorsForQuery(comparingContent,soContentWordVectorsMap);
				
				//get idfs for query2
				double[][] idf2 = CrokageUtils.getIDFMatrixForQuery(comparingContent, soIDFVocabularyMap);
				
				double simPair = CrokageUtils.round(Matrix.simDocPair(matrix1,matrix2,idf1,idf2),6);
				
				if(simPair>maxSimPair) {
					maxSimPair=simPair;
				}
				
				double apiAnswerPairScore = topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId());
				//double apiAnswerPairScore = 0;
				
				if(apiAnswerPairScore>maxApiScore) {
					maxApiScore=apiAnswerPairScore;
				}
				
				//countClasses(bucket.getCode()); //use recommended to score
				countMethods(bucket.getCode(),bucket);
				
				answersIdsScores.put(bucket.getId(), simPair);
				
			} catch (Exception e) {
				System.out.println("error here... post: "+bucket.getId());
				throw e;
			}
			
		}
		
		//crokageUtils.reportElapsedTime(initTime,"getTopKRelevantAnswers 1");
		//initTime = System.currentTimeMillis();
		
		if(!productionEnvironment) {
			reportCommonMethods();
		}
		
		//normalization and other relevance boosts
		for(Bucket bucket:answerBuckets) {
				double simPair = answersIdsScores.get(bucket.getId());
				simPair = (simPair / maxSimPair); //normalization
				
				double apiAnswerPairScore = topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId());
				apiAnswerPairScore = (apiAnswerPairScore/maxApiScore); //normalization
				//double apiAnswerPairScore=0;
				
				double finalScore = BotComposer.calculateFinalScore(simPair,bucket,methodsCounterMap,apiAnswerPairScore);
				
				
				//**pontuar intercecao de substrings entrey a query e o body ou codigo da resposta
				//ex, query "Use Scanner to read a list of comma-separated values" Respsotas boas contem "comma" ou "scanner"
				
				
				
				//not good results yet...
				/*String processedCode = bucket.getProcessedCode();
				for(int i=0; i<numberOfAPIClasses;i++) {
					String bikerMethod = bikerTopMethods.get(i);
					if(processedCode.contains(bikerMethod) && codeContainAnyClass(processedCode)) {
						double methodBoost = (float)1/((bikerTopMethods.indexOf(bikerMethod)+1));
						simPair += crokageUtils.round(methodBoost,6);
						break; //only once
					}
				}*/
				
				
				answersIdsScores.put(bucket.getId(), finalScore);
				
				//observar ex: https://stackoverflow.com/questions/1053467/how-do-i-save-a-string-to-a-text-file-using-java
				
				//score da pergunta : parentUpVotesScore
				
				//codigo deve ter chamada de método com ponto *.*
				
				//poucas linhas de codigo (desconsiderar imports . pontuar imports )
				
				
				//comentarios com sentimento positivo
		
			
		}
		
		//crokageUtils.reportElapsedTime(initTime,"getTopKRelevantAnswers 2");
		//initTime = System.currentTimeMillis();
		//sort scores in descending order 
		Map<Integer,Double> topSimilarAnswers = answersIdsScores.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       //.limit(topSimilarAnswersNumber)
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		if(!productionEnvironment) {
			reportSimilarRelatedPosts(topSimilarAnswers.keySet(),"answers","End of Ranking phase 3: ");
		}
		candidateAnswersIds = topSimilarAnswers.keySet();
		if(!productionEnvironment) {
			reportAnswers(candidateAnswersIds,topSimilarAnswers);
		}
		
		//crokageUtils.reportElapsedTime(initTime,"getTopKRelevantAnswers 3");
		
		answerBuckets=null;
		return candidateAnswersIds;
	}


	



	private void reportCommonClasses() {
		Map<String,Integer> topClassesCounterMap = classesCounterMap.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		System.out.println("Top classes: ");
		int i=0;
		for(String className:topClassesCounterMap.keySet()) {
			System.out.println(className+" :"+topClassesCounterMap.get(className));
			i++;
			if(i==10) {
				break;
			}
		}
		
		classesCounterMap.clear();
		classesCounterMap.putAll(topClassesCounterMap);
		
	}


	private void reportCommonMethods() {
		Map<String,Integer> topMethodsCounterMap = methodsCounterMap.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		System.out.println("Top methods: ");
		int i=0;
		for(String method:topMethodsCounterMap.keySet()) {
			System.out.println(method+" :"+topMethodsCounterMap.get(method));
			i++;
			if(i==10) {
				break;
			}
		}
		
		methodsCounterMap.clear();
		methodsCounterMap.putAll(topMethodsCounterMap);
		
	}




	private void countClasses(String code) {
		Set<String> classes = crokageUtils.extractClassesFromProcessedCode(code);
		for(String className: classes) {
			/*if(className.equals(".println(") && code.contains("ystem.out.println")) {
				continue;
			}*/
			if(classesCounterMap.containsKey(className)) {
				Integer currentCount = classesCounterMap.get(className);
				currentCount++;
				classesCounterMap.put(className,currentCount);
			}else {
				classesCounterMap.put(className,1);
			}
		}
		classes=null;
		
	}

	private void countMethods(String code,Bucket bucket) {
		Set<String> codes = crokageUtils.getMethodCalls(code);
		/*if(CollectionUtils.isEmpty(codes)) {
			System.out.println("here: "+code+ " - "+bucket.getId());
		}*/
		for(String method: codes) {
			if(method.equals(".println(") && code.contains("ystem.out.println")) {
				continue;
			}
			if(methodsCounterMap.containsKey(method)) {
				Integer currentCount = methodsCounterMap.get(method);
				currentCount++;
				methodsCounterMap.put(method,currentCount);
			}else {
				methodsCounterMap.put(method,1);
			}
		}
		codes=null;
	}




	private boolean codeContainAnyClass(String processedCode) {
		for(String bikerClass: bikerTopClasses) {
			if(processedCode.contains(bikerClass.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}




	private Set<String> getAllWordsForBuckets(List<Bucket> answerBuckets) {
		Set<String> allWords = new HashSet<>();
		String line;
		String words[];
		for(Bucket bucket:answerBuckets) {
			line = bucket.getProcessedBody()+ " "+bucket.getProcessedCode(); 
			words = line.split("\\s+");
			allWords.addAll(Arrays.asList(words));
		}
		line=null;
		words=null;
		return allWords;
	}




	private void reportSimilarRelatedPosts(Set<Integer> topSimilarIds, String postType, String obs) {
		int listSize = topSimilarIds.size();
		int k = listSize > 10? 10:listSize;
		String topIdsStr= obs+ " - Number of related "+postType+":"+listSize +". Showing first ("+k+") ids: ";
		topIdsStr+= StringUtils.join(new ArrayList(topSimilarIds).subList(0, k), ',');
		System.out.println(topIdsStr.toString());
		
		
	}




	private void addVectorsToSoContentWordVectorsMap(Set<Integer> answersWithTopFrequentAPIsIds) throws Exception {
		Set<String> contents = new LinkedHashSet<>();
		String content;
		for(Integer answerId:answersWithTopFrequentAPIsIds) {
			content = allAnswersIdsContentsParentContentsMap.get(answerId);
			contents.add(content);
		}
		crokageUtils.readVectorsFromSOMapForWords(soContentWordVectorsMap,contents,wordsAndVectorsLines);
		
		contents=null;
				
	}
	

	private void addVectorsToSoContentWordVectorsMapOld(Set<Integer> answersWithTopFrequentAPIsIds) throws Exception {
		Set<String> contents = new LinkedHashSet<>();
		String content;
		Set<Integer> irrelevantQuestionsIds = new HashSet<>();
		for(Integer answerId:answersWithTopFrequentAPIsIds) {
			content = allAnswersIdsContentsParentContentsMap.get(answerId);
			if(!StringUtils.isBlank(content)) {
				contents.add(content);
			}else {
				irrelevantQuestionsIds.add(answerId);
			}
		}
		
		//System.out.println("Discarding irrelevant questions ids, size: "+irrelevantQuestionsIds.size());
		for(Integer irrelevantQuestionId: irrelevantQuestionsIds) {
			allQuestionsIdsTitlesMap.remove(irrelevantQuestionId);
		}
		answersWithTopFrequentAPIsIds.removeAll(irrelevantQuestionsIds);
		
		if(!iHaveALotOfMemory) {
			crokageUtils.readVectorsFromSOMapForWords(soContentWordVectorsMap,contents,wordsAndVectorsLines);
		}
		contents=null;
		irrelevantQuestionsIds=null;
		
	}



/*
	private void loadAllAnswersIdsParentIds() {
		long initTime = System.currentTimeMillis();
		allAnswersWithUpvotesIdsParentIdsMap = crokageService.getAnswersIdsParentIds();
		crokageUtils.reportElapsedTime(initTime,"loadAllAnswersIdsParentIds");
	}
*/



	private void loadAllQuestionsIdsTitles() {
		long initTime = System.currentTimeMillis();
		allQuestionsIdsTitlesMap.putAll(crokageService.getQuestionsIdsTitles());
		crokageUtils.reportElapsedTime(initTime,"loadAllQuestionsIdsTitles");
	}
	
	



	private List<String> getBikerTopMethods(Integer key) {
		if(bikerQueriesApisClassesAndMethodsMap==null) {
			return null;
		}
		List<String> topMethods = new ArrayList<>();
		Set<String> classesAndMethods = bikerQueriesApisClassesAndMethodsMap.get(key);
		for(String classAndMethod: classesAndMethods) {
			String parts[] = classAndMethod.split("\\."); 
			topMethods.add(parts[1]);
		}
		//System.out.println("Top methods from biker: "+topMethods);
		return topMethods;
	}



	


	private void loadInvertedIndexFile() throws IOException {
		if(bigMapApisAnswersIds==null) {
			long initTime = System.currentTimeMillis();
			//System.out.println("Reading big map inverted index file...");
			String[] parts;
			String[] ids;
			bigMapApisAnswersIds = new HashMap<>();
			List<String> apisAndSOIds = Files.readAllLines(Paths.get(BIG_MAP_INVERTED_INDEX_APIS_FILE_PATH), Charsets.UTF_8);
			for(String line: apisAndSOIds) {
				parts = line.split(":\t");
				ids = parts[1].split(" ");
				HashSet<Integer> idsSet = new HashSet<>();
				for(String id:ids) {
					idsSet.add(Integer.parseInt(id));
				}
				bigMapApisAnswersIds.put(parts[0], idsSet);
			}
			//System.out.println(bigMapApisAnswersIds);
			crokageUtils.reportElapsedTime(initTime,"loadInvertedIndexFile");
		}
		
	}

	



	private void generatePostsApisMap() throws IOException {
		//get posts containing code. Date parameter is optional for tests purpose.
		//String startDate = "2016-01-01"; 
		String startDate = null;
		long initTime = System.currentTimeMillis();
		System.out.println("Processing posts to generate index file...");
		List<Post> upvotedPostsWithCode =  crokageService.getUpvotedPostsWithCode(startDate);
		
		String postsWithoutAPICalls = "";
		int postsWithoutAPICallsCounter=0;
		
		StringBuilder content = new StringBuilder();
		
		int i=0;
		for(Post post:upvotedPostsWithCode) {
			i++;
			if(i%10000==0) {
				System.out.println("Processing post "+i);
			}
			
			//extract apis from answer. For each api, add the api in a map, together with the other references for that post
			Set<String> codeSet=null;
			try {
				codeSet = crokageUtils.extractClassesFromCode(post.getBody());
			} catch(StackOverflowError e){
				System.out.println("Error in post "+post.getId()+ " - disconsidering...");
				continue;
	        } catch (Exception e) {
				System.out.println("Exception in post "+post.getId()+ " - disconsidering...");
				continue;
			}
			
			//ArrayList<String> codeClasses = new ArrayList(codeSet);
			if(codeSet.isEmpty()) {
				continue;
			}
			
			content.append(post.getId()+" >> ");
			for(String api: codeSet) {
				content.append(api+" ");
			}
			content.append("\n");
			
		}
		content.append("end@");
		String contentStr = content.toString().replace("\nend@", "");
		
		System.out.println("Done processing posts to generate index file.");
		System.out.println("Number of processed posts containing API calls: "+i+ ". Now printing files...");
		CrokageUtils.writeStringContentToFile(contentStr,SO_UPVOTED_POSTS_WITH_CODE_APIS_FILE);
		System.out.println("Done printing files.");
		crokageUtils.reportElapsedTime(initTime,"generatePostsApisMap");
		
	}



	/*
	 * Set -Xss200m in VM parameters or java.lang.StackOverflowError: null is thrown 
	 */
	private void generateInvertedIndexFileFromSOPosts() throws IOException {
		//get posts containing code. Date parameter is optional for tests purpose.
		//String startDate = "2016-01-01"; 
		String startDate = null;
		long initTime = System.currentTimeMillis();
		System.out.println("Processing posts to generate inverted index file...");
		List<Post> answersWithPreCode =  crokageService.getAnswersWithCode(startDate);
		
		String postsWithoutAPICalls = "";
		int postsWithoutAPICallsCounter=0;
		
		Map<String,Set<Integer>> bigMapApisAnswersIds = new HashMap<>();
		int i=1;
		for(Post answer:answersWithPreCode) {
			
			/*if(answer.getId().equals(50662268)) {
				System.out.println();
			}*/
			
			if(i%10000==0) {
				System.out.println("Processing post "+i);
			}
			
			//extract apis from answer. For each api, add the api in a map, together with the other references for that post
			Set<String> codeSet=null;
			try {
				codeSet = crokageUtils.extractClassesFromCode(answer.getBody());
			} catch(StackOverflowError e){
				System.out.println("Error in post "+answer.getId()+ " - disconsidering...");
				continue;
	        } catch (Exception e) {
				System.out.println("Exception here: "+answer);
				e.printStackTrace();
			}
			
			//ArrayList<String> codeClasses = new ArrayList(codeSet);
			if(codeSet.isEmpty()) {
				i++;
				postsWithoutAPICallsCounter++;
				if(postsWithoutAPICallsCounter%10==0) {
					postsWithoutAPICalls+="\n";
				}
				postsWithoutAPICalls+=answer.getId()+",";
				continue;
			}
			
			for(String api: codeSet) {
				if(bigMapApisAnswersIds.get(api)==null){
					HashSet<Integer> idsSet = new LinkedHashSet<>();
					idsSet.add(answer.getId());
					bigMapApisAnswersIds.put(api, idsSet);
				
				}else {
					/*if(api.equals("MainActivity")) {
						System.out.println();
					}*/
					Set<Integer> currentIds = bigMapApisAnswersIds.get(api);
					currentIds.add(answer.getId());
				}
				
			}
			i++;
		}
		
		System.out.println("Done processing posts to generate inverted index file.");
		System.out.println("Number of posts containing API calls: "+bigMapApisAnswersIds.size()+ ". Now printing files...");
		CrokageUtils.printBigMapIntoFile(bigMapApisAnswersIds,BIG_MAP_INVERTED_INDEX_APIS_FILE_PATH);
		System.out.println("Done printing files.");
		crokageUtils.reportElapsedTime(initTime,"generateInvertedIndexFileFromSOPosts");
	}


	private void generateInputQueriesFromNLP2ApiGroudTruth() throws Exception {
		List<String> inputQueries = getQueriesFromFile(NLP2API_GOLD_SET_FILE);
		
		Path queriesFile = Paths.get(INPUT_QUERIES_FILE_NLP2API);
		Files.write(queriesFile, inputQueries, Charset.forName("UTF-8"));
		
	}



	private void extractAPIsFromNLP2Api() throws Exception {
		
		if(queries==null) {
			queries = readInputQueries();
		}
		
		if(callNLP2ApiProcess) {
			//queries = queries.subList(0, 5);
			//First generate inputQueries file in a format NLP2Api understand
			FileWriter fw = new FileWriter(NLP2API_INPUT_QUERIES_FILE);
			for (String query: queries) {
				fw.write(query+"\n--\n"); //specific format to NLP2API understand
				
			}
		 	fw.close();
			
			
	 		//call jar with parameters
		 	//java -jar /home/rodrigo/projects/bot/myNlp2Api.jar -K 10 -task reformulate -queryFile /home/rodrigo/projects/NLP2API-Replication-Package/NL-Query+GroundTruth.txt -outputFile /home/rodrigo/projects/NLP2API-Replication-Package/nlp2apiQueriesOutput.txt
			
		 	String jarPath = CROKAGE_HOME;
			List<String> command = new ArrayList<String>();
		    
		    command.add("java");
		    command.add("-jar");
		    command.add(jarPath+"/myNlp2Api.jar");
		    command.add("-K");
		    command.add(""+numberOfAPIClasses);
		    command.add("-task");
		    command.add("reformulate");
		    command.add("-queryFile");
		    command.add(NLP2API_INPUT_QUERIES_FILE);
		    command.add("-outputFile");
		    command.add(NLP2API_OUTPUT_QUERIES_FILE);
			
			ProcessBuilder pb = new ProcessBuilder(command);
			Process p = pb.start();
			p.waitFor();
			String output = CrokageUtils.loadStream(p.getInputStream());
			String error = CrokageUtils.loadStream(p.getErrorStream());
			/*if(error.contains("error")) {
				System.out.println(error);
				throw new Exception(error);
			}*/
			//int rc = p.waitFor();
			//System.out.println("Process ended with rc=" + rc);
			//System.out.println("\nStandard Output:\n");
			//System.out.println(output);
			//String apis[] = output.replaceAll("\n", " ").split(" ");
			//System.out.println(apis);
			//System.out.println("\nStandard Error:\n");
			//System.out.println(error);
	 	}
		
		
		
		getQueriesAndApisFromFileMayContainDupes(nlp2ApiQueriesApisMap,NLP2API_OUTPUT_QUERIES_FILE);
		
		
	}
	
	private void extractAPIsFromRACK() throws Exception {
		if(queries==null) {
			queries = readInputQueries();
		}
		
		
		
		if(callRACKApiProcess) {
			//First generate inputQueries file in a format NLP2Api understand
			FileWriter fw = new FileWriter(RACK_INPUT_QUERIES_FILE);
			for (String query: queries) {
				fw.write(query+"\n--\n"); //specific format to NLP2API understand
				
			}
		 	fw.close();
			
			
		 	//call jar with parameters
		 	
		 	String jarPath = CROKAGE_HOME;
		 	List<String> command = new ArrayList<String>();
		
		 	command.add("java");
		    command.add("-jar");
		    command.add(jarPath+"/rack-exec.jar");
		    command.add("-K");
		    command.add(""+numberOfAPIClasses);
		    command.add("-task");
		    command.add("suggestAPI");
		    command.add("-queryFile");
		    command.add(RACK_INPUT_QUERIES_FILE);
		    command.add("-resultFile");
		    command.add(RACK_OUTPUT_QUERIES_FILE);
			
			ProcessBuilder pb = new ProcessBuilder(command);
			Process p = pb.start();
			p.waitFor();
			String output = CrokageUtils.loadStream(p.getInputStream());
			String error = CrokageUtils.loadStream(p.getErrorStream());
			if(error.contains("error")) {
				System.out.println(error);
				throw new Exception(error);
			}
			//int rc = p.waitFor();
			//System.out.println("Process ended with rc=" + rc);
			//System.out.println("\nStandard Output:\n");
			//System.out.println(output);
			//String apis[] = output.replaceAll("\n", " ").split(" ");
			//System.out.println(apis);
			//System.out.println("\nStandard Error:\n");
			//System.out.println(error);
	 	}
		
		
		
		getQueriesAndApisFromFileMayContainDupes(rackQueriesApisMap,RACK_OUTPUT_QUERIES_FILE);
		
	}
	
	private void extractAPIsFromBIKER() throws Exception {
		// BIKER
		if(queries==null) {
			queries = readInputQueries();
		}
		
	
		if(callBIKERProcess) {
			// writing queries to be read by biker
			Path bikerQueriesFile = Paths.get(BIKER_INPUT_QUERIES_FILE);
			Files.write(bikerQueriesFile, queries, Charset.forName("UTF-8"));

			// writing script to be called
			Path scriptFile = Paths.get(BIKER_SCRIPT_FILE);
			List<String> lines=null;
			
			if(!StringUtils.isBlank(virutalPythonEnv)) { //specific env
				lines = Arrays.asList("#!/bin/bash","export PYTHONPATH=" + BIKER_HOME, "echo $PYTHONPATH", "cd $PYTHONPATH/../","virtualenv "+virutalPythonEnv,"source "+virutalPythonEnv+"/bin/activate","cd $PYTHONPATH/main","python " + BIKER_RUNNER_PATH);
				//lines = Arrays.asList("#!/bin/bash", "export PYTHONPATH=" + BIKER_HOME, "echo $PYTHONPATH","echo $PYTHONPATH","echo $PYTHONPATH", "cd $PYTHONPATH/../","virtualenv "+virutalPythonEnv,"source "+virutalPythonEnv+"/bin/activate","./test.sh");
			}else {
				lines = Arrays.asList("#!/bin/bash","export PYTHONPATH=" + BIKER_HOME, "echo $PYTHONPATH", "cd $PYTHONPATH/main", "python " + BIKER_RUNNER_PATH);
			}
						
			Files.write(scriptFile, lines, Charset.forName("UTF-8"));
			File file = new File(BIKER_SCRIPT_FILE);
			file.setExecutable(true);
			file.setReadable(true);
			file.setWritable(true);
			
		
			ProcessBuilder pb = new ProcessBuilder(BIKER_SCRIPT_FILE);
			Process p = pb.start();
			p.waitFor();
			String output = CrokageUtils.loadStream(p.getInputStream());
			String error = CrokageUtils.loadStream(p.getErrorStream());
			if(error.contains("error")) {
				System.out.println(error);
				throw new Exception(error);
			}
			//int rc = p.waitFor();
			//System.out.println("Process ended with rc=" + rc);
			//System.out.println("\nStandard Output:\n");
			//System.out.println(output);
			//System.out.println("\nStandard Error:\n");
			//System.out.println(error);
			
		}
		
		int key = 1;
		// reading output from BIKER
		List<String> queriesWithApis = Files.readAllLines(Paths.get(BIKER_OUTPUT_QUERIES_FILE), Charsets.UTF_8);
		if(limitQueries!=null) {
			queriesWithApis = queriesWithApis.subList(0, limitQueries);
		}
		for(String generatedLine: queriesWithApis) {
			String parts[] = generatedLine.split("=  ");
			List<String> rankedApis = Arrays.asList(parts[1].split("### ")).stream().map(String::trim).collect(Collectors.toList());
			rankedApis.remove("");
			
			int k = numberOfAPIClasses;
			if (rankedApis.size() < k) {
				//logger.warn("The number of retrieved APIs from BIKER is lower than the number of rack classes set as parameter. Ajusting the parameter to -->" + rankedApis.size() + " apis, returned by BIKER for this query.");
				k = rankedApis.size();
			}
			
			Set<String> rankedApisSetWithMethods = new LinkedHashSet<String>(rankedApis);
			bikerQueriesApisClassesAndMethodsMap.put(key, rankedApisSetWithMethods);
			
			Set<String> rankedApisSetClassesOnly = new LinkedHashSet<String>();
			for(String api: rankedApisSetWithMethods) {
				rankedApisSetClassesOnly.add(api.split("\\.")[0]);
			}
			
			CrokageUtils.setLimit(rankedApisSetClassesOnly,k);
			bikerQueriesApisClassesMap.put(key, rankedApisSetClassesOnly);
		
			//System.out.println("Biker - discovered classes for query: "+parts[0]+ " ->  " + bikerQueriesApisClassesMap.get(key));
			key++;
		}
		
		/*List<String> bikerClasses = new ArrayList<>();
		for(String api: list) {
			classes.add(api.split("\\.")[0]); 
		}*/
		
		//System.out.println("Biker finished... ");
		
	}

	
	private List<String> getQueriesFromFile(String fileName) throws IOException {
		List<String> queries = new ArrayList<>();
		// reading output from generated file
		List<String> queriesAndApis = Files.readAllLines(Paths.get(fileName), Charsets.UTF_8);
		Iterator<String> it = queriesAndApis.iterator();
		
		while(it.hasNext()) {
			String query = "";
			if(it.hasNext()) {
				query = it.next();
				queries.add(query);
				if(it.hasNext()) {
					it.next(); //APIs are in even lines
				}
			}
			
		}
		
		
		return queries;
	}
	
	
	private void reportAnswers(Set<Integer> topKRelevantAnswersIds, Map<Integer, Double> topSimilarAnswers) {
		List<Integer> listIds = new ArrayList<>(topKRelevantAnswersIds);
		int listSize = listIds.size();
		int k = listSize > 10? 10:listSize;
		listIds=listIds.subList(0, k);
		System.out.println("Top "+k+" similar answers to query");
		
		for(Integer id:listIds) {
			System.out.println(id.toString()+ " -score:"+topSimilarAnswers.get(id));
			
		}
		
	}


	
	/*
	 * Extended version of getQueriesAndApisFromFile where the generated queries can be equal
	 */
	private void getQueriesAndApisFromFileMayContainDupes(Map<Integer, Set<String>> queriesApis, String fileName) throws IOException {
		if(queriesApis==null) {
			queriesApis = new LinkedHashMap<>();
		}
		
		// reading output from generated file
		List<String> queriesAndApis = Files.readAllLines(Paths.get(fileName), Charsets.UTF_8);
		if(limitQueries!=null) {
			queriesAndApis = queriesAndApis.subList(0, 2*limitQueries);
		}
		Iterator<String> it = queriesAndApis.iterator();
		int key = 1;
		while(it.hasNext()) {
			String query = "";
			String queryApis = "";
			if(it.hasNext()) {
				query = it.next();
				if(it.hasNext()) {
					queryApis = it.next(); //APIs are in even lines
				}
			}
			
			queryApis = queryApis.replaceAll("\\s+"," ");
			
			List<String> rankedApis = Arrays.asList(queryApis.split(" ")).stream().map(String::trim).collect(Collectors.toList());
			int k = numberOfAPIClasses;
			if (rankedApis.size() < k) {
				//logger.warn("The number of retrieved APIs for query is lower than the number of rack classes set as parameter. Ajusting the parameter to -->" + rankedApis.size() + " apis");
				k = rankedApis.size();
			}
			
			Set<String> rankedApisSet = new LinkedHashSet<String>(rankedApis);
			CrokageUtils.setLimit(rankedApisSet, k);
			queriesApis.put(key, rankedApisSet);
			//System.out.println("discovered classes for query: "+query+ " ->  " + queriesApis.get(key));
			key++;
		}
			
	}


	private void generateMetricsForApiExtractors(Integer[] kArray) throws Exception {
		
		//needs to be before extraction, because annotated dataset is used to generate queries file in case of crokage
		Map<Integer, Set<String>> goldSetQueriesApis = getGoldSetQueriesApis();

		int numApproaches = getApisForApproaches();
		
		Map<Integer, Set<String>> recommendedApis = getRecommendedApis();
	
		
		if(subAction.contains("generatetableforapproaches")) {
			
			List<Integer> ks = new ArrayList<>();
			ks.add(10); ks.add(5); ks.add(1);
			if(numApproaches>1) {
				ks.remove(ks.size()-1);
			}
			MetricResult metricResult = new MetricResult();
			for(int k: ks) {
				numberOfAPIClasses = k;
				crokageUtils.reduceSet(goldSetQueriesApis,k);
				crokageUtils.reduceSet(recommendedApis, k);
				analyzeResultsForApiExtractors(recommendedApis,goldSetQueriesApis,metricResult);
			}
			System.out.println(metricResult.toString());
		}else {
			analyzeResultsForApiExtractors(recommendedApis,goldSetQueriesApis,null);
		}
		
		
	}


	private int getApisForApproaches() throws Exception {
		System.out.println("loading queries from approaches...");
		int numApproaches = 0;
		long initTime = System.currentTimeMillis();
		
		loadGroundTruthSelectedQueries();
		
		//subAction is transformed to lowercase
		if(subAction.contains("rack")) {
			extractAPIsFromRACK();
			numApproaches++;
			System.out.println("Metrics for RACK");
		}
		if(subAction.contains("biker")) {
			extractAPIsFromBIKER();
			numApproaches++;
			System.out.println("Metrics for BIKER");
		}
		if(subAction.contains("nlp2api")) {
			extractAPIsFromNLP2Api();
			numApproaches++;
			System.out.println("Metrics for nlp2api");
		}
		//System.out.println("Done loading queries from approaches.");
		crokageUtils.reportElapsedTime(initTime,"getApisForApproaches");
		return numApproaches;
	}



	private Map<Integer, Set<String>> getGoldSetQueriesApis() throws IOException {
		Map<Integer, Set<String>> goldSetQueriesApis = new LinkedHashMap<>();
		
		if(dataSet.equals("crokage")) {
			List<UserEvaluation> evaluationsList = loadExcelGroundTruthQuestionsAndLikerts();
			//get goldset and update input queries to be used by approaches
			getGoldSetByEvaluations(goldSetQueriesApis,evaluationsList,INPUT_QUERIES_FILE_CROKAGE);
	
		}else if(dataSet.equals("nlp2api")) {
			getQueriesAndApisFromFileMayContainDupes(goldSetQueriesApis,NLP2API_GOLD_SET_FILE);
		
		}else if(dataSet.equals("selectedqueries")) {
			List<UserEvaluation> evaluationsWithBothUsersScales = new ArrayList<>();
			crokageUtils.readGroundTruthFile(evaluationsWithBothUsersScales,QUERIES_AND_SO_ANSWERS_AGREEMENT,4,5);
			//get goldset 
			getGoldSetByEvaluations(goldSetQueriesApis,evaluationsWithBothUsersScales,"selectedQueriesHavingGoldSet.txt");
		
		}
		
		return goldSetQueriesApis;
	}



	private void generateInputQueriesForCrokageDataSetFromExcelGroudTruth() throws FileNotFoundException {
		String file1 = "questionsLot1and2withoutWeightsAdjuster.xlsx";
		Integer likertsResearcher1AfterAgreementColumn = 3;
		Integer likertsResearcher2AfterAgreementColumn = 4;
		
		List<UserEvaluation> evaluationsList = new ArrayList<>();
		List<String> inputQueries = new ArrayList<>();
		crokageUtils.readXlsxToEvaluationList(evaluationsList,file1,likertsResearcher1AfterAgreementColumn,likertsResearcher2AfterAgreementColumn,inputQueries);
		//System.out.println(inputQueries);
		
		try (PrintWriter out = new PrintWriter(INPUT_QUERIES_FILE_CROKAGE)) {
		    for(String query: inputQueries) {
		    	out.println(query);
		    }
			
		}
		
	}
	
	private List<UserEvaluation> loadExcelGroundTruthQuestionsAndLikerts() throws IOException {
		String file1 = "questionsLot1and2withoutWeightsAdjuster.xlsx";
		Integer likertsResearcher1AfterAgreementColumn = 3;
		Integer likertsResearcher2AfterAgreementColumn = 4;
		
		List<UserEvaluation> evaluationsList = new ArrayList<>();
		crokageUtils.readXlsxToEvaluationList(evaluationsList,file1,likertsResearcher1AfterAgreementColumn,likertsResearcher2AfterAgreementColumn,null);
		//generateMetricsForEvaluations(evaluationsList);
		return evaluationsList;
	}

	

	



	public List<String> readInputQueries() throws Exception {
		//long initTime = System.currentTimeMillis();
		String fileName = "";
		if(dataSet.equals("crokage")) {
			fileName = INPUT_QUERIES_FILE_CROKAGE;
	
		}else if(dataSet.equals("nlp2api")) {
			fileName = INPUT_QUERIES_FILE_NLP2API;
		
		}else if(dataSet.equals("selectedqueries")) {
			fileName = INPUT_QUERIES_FILE_SELECTED_QUERIES;
		}
		
		
		// read queries from file
		File inputQueriesFile = new File(fileName);
		if(!inputQueriesFile.exists()) { 
		    //generateInputQueriesForCrokageDataSetFromExcelGroudTruth();
			throw new Exception("Input queries file needed...");
		}
		
		List<String> queries = FileUtils.readLines(inputQueriesFile, "utf-8");
		if(limitQueries!=null) {
			queries = queries.subList(0, limitQueries);
		}
		//crokageUtils.reportElapsedTime(initTime,"readInputQueries");
		return queries;

	}

	/*private void getPropertyValueFromLocalFile() {
		Properties prop = new Properties();
		InputStream input = null;
		useProxy = false;
		try {

			input = new FileInputStream(pathFileEnvFlag);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			String useProxyStr = prop.getProperty("useProxy");
			if (!StringUtils.isBlank(useProxyStr)) {
				useProxy = new Boolean(useProxyStr);
			}
			bikerHome = prop.getProperty("BIKER_HOME");
			virutalPythonEnv= prop.getProperty("virutalPythonEnv");
			environment = prop.getProperty("environment");
			
			String msg = "\nEnvironment property (useProxy): ";
			if (useProxy) {
				msg += "with proxy";
			} else {
				msg += "no proxy";
			}

			System.out.println(msg+"\nnEnvironment: "+environment);

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
	}*/
	
	
	private Map<Integer, Set<String>> getGoldSetByEvaluations(Map<Integer, Set<String>> goldSetMap, List<UserEvaluation> evaluationsWithBothUsersScales,String fileName) throws FileNotFoundException {
		//long initTime = System.currentTimeMillis();
		if(goldSetMap==null) {
			goldSetMap = new LinkedHashMap<>();
		}
		Map<String,Set<String>> goldSetQueriesApis = new LinkedHashMap<>();
		int assessed = 0;
		String query = null;
		Integer postId=null;
		
		try {
			
			for(UserEvaluation evaluation:evaluationsWithBothUsersScales){
				int likert1 = evaluation.getLikertScaleUser1();
				int likert2 = evaluation.getLikertScaleUser2();
				query = evaluation.getQuery();
				int diff = Math.abs(likert1 - likert2);
				if(diff>1) {
					continue;
				}
				assessed++;
				
				double meanFull = (likert1+likert2)/(double)2;
				double meanLikert=0d;
				meanLikert = CrokageUtils.round(meanFull,2);
				
				if(meanLikert>=4) { 
					
					Post post = crokageService.findPostById(evaluation.getPostId());
					postId=post.getId();
					Set<String> codeSet = crokageUtils.extractClassesFromCode(post.getBody());
					if(codeSet.isEmpty()) {
						continue;
					}
					
					if(goldSetQueriesApis.get(query)==null){
						goldSetQueriesApis.put(query, codeSet);
					
					}else {
						Set<String> currentClasses = goldSetQueriesApis.get(query);
						currentClasses.addAll(codeSet);
					}
					
				}
			}
			
			System.out.println("\nTotal evaluations: "+evaluationsWithBothUsersScales.size() + "\nConsidered with likert difference <=1: "+assessed);
			
				
		} catch (Exception e) {
			System.out.println("Error in postId: "+postId);
			e.printStackTrace();
		} 
		
		//crokageUtils.reportElapsedTime(initTime,"generateMetricsForEvaluations");
		
		//transform from query
		Set<String> keySet = goldSetQueriesApis.keySet();
		
		int keyNumber =1;
		for(String keyQuery: keySet) {
			Set<String> apis = goldSetQueriesApis.get(keyQuery);
			goldSetMap.put(keyNumber, apis);
			keyNumber++;
		}
		
		
		//update input queries to be used by approaches
		try (PrintWriter out = new PrintWriter(fileName)) {
		    for(String query2: keySet) {
		    	out.println(query2);
		    }
			
		}
		
		
		return goldSetMap;
	}
	
	
	
	private Map<Integer, Set<String>> getRecommendedApis() {
		long initTime = System.currentTimeMillis();
		Map<Integer, Set<String>> recommendedApis = new LinkedHashMap<>();
		//are the same for all
		//Set<String> queriesSet = new LinkedHashSet<>(queries);  
		Set<Integer> keys = null; 
		String approaches = subAction;
		
		int numApproaches = 0;
		if(!MapUtils.isEmpty(rackQueriesApisMap)) {
			numApproaches++;
			keys = rackQueriesApisMap.keySet();
		}
		if(!MapUtils.isEmpty(bikerQueriesApisClassesMap)) {
			numApproaches++;
			keys = bikerQueriesApisClassesMap.keySet();
		}
		if(!MapUtils.isEmpty(nlp2ApiQueriesApisMap)) {
			numApproaches++;
			keys = nlp2ApiQueriesApisMap.keySet();
		}
				
		if(numApproaches>1){ 
			approaches = subAction.replace("|generatetableforapproaches", "");
			String apisArrOrder[] = approaches.split("\\|");
			//List<String> queriesList = new ArrayList<>(queriesSet);
			String bikerApi = null;
			String nlp2ApiApi = null;
			String rackApi = null;
			
			
			outer: for(Integer keyNum: keys) {
				Set<String> recommendedApisSet = new LinkedHashSet<>();
				Set<String> bikerApisSet = null;
				Set<String> rackApisSet = null;
				Set<String> nlpApisSet = null;
				
				Iterator<String> bikerIt = null;
				Iterator<String> rackIt = null;
				Iterator<String> nlpIt = null;
				
				
				if(!MapUtils.isEmpty(rackQueriesApisMap)) {
					rackApisSet = rackQueriesApisMap.get(keyNum);
					rackIt = rackApisSet.iterator();
				}
				if(!MapUtils.isEmpty(bikerQueriesApisClassesMap)) {
					bikerApisSet = bikerQueriesApisClassesMap.get(keyNum);
					bikerIt = bikerApisSet.iterator();
				}
				if(!MapUtils.isEmpty(nlp2ApiQueriesApisMap)) {
					nlpApisSet = nlp2ApiQueriesApisMap.get(keyNum);
					nlpIt = nlpApisSet.iterator();
				}
				
				bikerApi = "";
				nlp2ApiApi = "";
				rackApi = "";
				
				
				int i=1;
				String chosenApi = null;
				//int key =1;
				while(true) { 
					//collect the ith API from each approach and merge
					chosenApi = null;
					boolean stop = true;
					
					if(bikerIt!=null && bikerIt.hasNext()) {
						bikerApi = bikerIt.next();
						stop = false;
					}
					
					if(nlpIt!=null && nlpIt.hasNext()) {
						nlp2ApiApi= nlpIt.next();
						stop = false;
					}
					if(rackIt!=null && rackIt.hasNext()) {
						rackApi = rackIt.next();
						stop = false;
					}
					
					if(stop) { //recommenders were not able to produce enough apis
						break;
					}
					
					//uses the order of the apis to merge the recommendation
					if(numApproaches==3) {
						if(bikerApi.equals(nlp2ApiApi) || bikerApi.equals(rackApi)) { //if 2 are equal, choose it, else one of each until numberOfAPIClasses
							chosenApi = bikerApi;
							recommendedApisSet.add(chosenApi);
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
							i++;
							
						} else if(nlp2ApiApi.equals(rackApi)) {
							chosenApi = nlp2ApiApi;
							recommendedApisSet.add(chosenApi);
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
							i++;
						} else {  //all different, follows the order
							
							
						}
						
					} 
					//two approaches or three approaches: rack+biker(+nlp), rack+nlp(+biker), biker+rack(+nlp), biker+nlp(+rack), nlp+rack(+biker), nlp+biker(+rack)
					if(apisArrOrder[0].equals("rack") && apisArrOrder[1].equals("biker")) {
						
						recommendedApisSet.add(rackApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(bikerApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
							
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(nlp2ApiApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}else if(apisArrOrder[0].equals("rack") && apisArrOrder[1].equals("nlp2api")) {
						
						recommendedApisSet.add(rackApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(nlp2ApiApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(bikerApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}else if(apisArrOrder[0].equals("biker") && apisArrOrder[1].equals("rack")) {
						
						recommendedApisSet.add(bikerApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(rackApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						//nlp
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(nlp2ApiApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
						
					}else if(apisArrOrder[0].equals("biker") && apisArrOrder[1].equals("nlp2api")) {
						
						recommendedApisSet.add(bikerApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(nlp2ApiApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(rackApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}else if(apisArrOrder[0].equals("nlp2api") && apisArrOrder[1].equals("rack")) {
						
						recommendedApisSet.add(nlp2ApiApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(rackApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(bikerApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}else if(apisArrOrder[0].equals("nlp2api") && apisArrOrder[1].equals("biker")) {
						recommendedApisSet.add(nlp2ApiApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						recommendedApisSet.add(bikerApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(rackApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}
			
				}
				recommendedApis.put(keyNum, recommendedApisSet);
				//key++;
				
			}
			
		}else if(!MapUtils.isEmpty(rackQueriesApisMap)) {
			recommendedApis.putAll(rackQueriesApisMap);
		}else if(!MapUtils.isEmpty(bikerQueriesApisClassesMap)){
			recommendedApis.putAll(bikerQueriesApisClassesMap);
		}else {
			recommendedApis.putAll(nlp2ApiQueriesApisMap);
		}
		crokageUtils.reportElapsedTime(initTime,"getRecommendedApis");
		return recommendedApis;
		
	}


	public void analyzeResults(Map<String, Set<Integer>> recommended,Map<String, Set<Integer>> goldSet, MetricResult metricResult, String approach) {
		int hitK = 0;
		int correct_sum = 0;
		double rrank_sum = 0;
		double precision_sum = 0;
		double preck_sum = 0;
		double recall_sum = 0;
		double fmeasure_sum = 0;
		
		Integer topk = metricResult.getTopk();
		if(topk!=null && topk>0) {
			crokageUtils.reduceSetV2(recommended, topk);
		}else if(topk==null) {
			topk=10000; //save in top1 
		}
		//Integer topk = metricResult.getTopk();
		//System.out.println("analyzeResults - considering topk= "+topk);
		int maxSize = 0;
		
		String currentQuery="";
		try {
			
			for (String keyQuery : goldSet.keySet()) {
				currentQuery=keyQuery;
				ArrayList<Integer> rapis = new ArrayList<>(recommended.get(keyQuery));
				ArrayList<Integer> gapis = new ArrayList<>(goldSet.get(keyQuery));
			
				if(rapis.size()>maxSize) {
					maxSize=rapis.size();
				}
				
				hitK = hitK + isFound_K(rapis, gapis);
				rrank_sum = rrank_sum + getRRankV2(rapis, gapis);
				double preck = 0;
				preck = getAvgPrecisionKV2(rapis, gapis);
				preck_sum = preck_sum + preck;
				double recall = 0;
				recall = getRecallKV2(rapis, gapis);
				/*if(recall<1) {
					System.out.println("here: "+keyQuery);
				}*/
				recall_sum = recall_sum + recall;
				//System.out.println(hitK);
				/*if(keyQuery==308) {
					System.out.println();
				}*/
				//System.out.println("query="+keyQuery+" -hitk="+hitK+" -rrank_sum:"+rrank_sum+" -preck:"+preck+ " -recall:"+recall );
			}

			double hit_k= CrokageUtils.round((double) hitK / goldSet.size(),4)*100;
			double mrr = CrokageUtils.round(rrank_sum / goldSet.size(),4)*100;
			double map = CrokageUtils.round(preck_sum / goldSet.size(),4)*100;
			double mr = CrokageUtils.round(recall_sum / goldSet.size(),4)*100;
			
			metricResult.setTopk(maxSize);
			
			if(metricResult!=null) {
				if(topk==10) {
					metricResult.setHitK10(hit_k);
					metricResult.setMrrK10(mrr);
					metricResult.setMapK10(map);
					metricResult.setMrK10(mr);
				}else if(topk==5) {
					metricResult.setHitK5(hit_k);
					metricResult.setMrrK5(mrr);
					metricResult.setMapK5(map);
					metricResult.setMrK5(mr);
				}else {
					metricResult.setHitK1(hit_k);
					metricResult.setMrrK1(mrr);
					metricResult.setMapK1(map);
					metricResult.setMrK1(mr);
				}
			}
			
			
			System.out.println("Results for: "+approach+" "
					+" - Hit@" + maxSize + ": " + hit_k
					+" - MRR@" + maxSize + ": " + mrr
					+" - MAP@" + maxSize + ": " + map
					+" - MR@" + maxSize + ": " + mr
					+ "");
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in analyzeResultsV2 in query: "+currentQuery);
			throw e;
		}
		
	}


	



	public void analyzeResultsForApiExtractors(Map<Integer, Set<String>> recommendedApis,Map<Integer, Set<String>> goldSetQueriesApis, MetricResult metricResult) {
		int hitK = 0;
		int correct_sum = 0;
		double rrank_sum = 0;
		double precision_sum = 0;
		double preck_sum = 0;
		double recall_sum = 0;
		double fmeasure_sum = 0;
		
		int k = numberOfAPIClasses;
		
		try {
			
			for (Integer keyQuery : goldSetQueriesApis.keySet()) {
				
				List<String> rapis = new ArrayList<>(recommendedApis.get(keyQuery));
				/*int K = rapis.size() < numberOfAPIClasses ? rapis.size() : numberOfAPIClasses;
				rapis = rapis.subList(0, K);*/
				ArrayList<String> gapis = new ArrayList<>(goldSetQueriesApis.get(keyQuery));
			
				/*if(keyQuery.contains("Read JSON Array")) {
					System.out.println();
				}*/
				
				hitK = hitK + isApiFound_K(rapis, gapis);
				rrank_sum = rrank_sum + getRRank(rapis, gapis);
				double preck = 0;
				preck = getAvgPrecisionK(rapis, gapis);
				preck_sum = preck_sum + preck;
				double recall = 0;
				recall = getRecallK(rapis, gapis);
				recall_sum = recall_sum + recall;
				//System.out.println(hitK);
				/*if(keyQuery==308) {
					System.out.println();
				}*/
				//System.out.println("query="+keyQuery+" -hitk="+hitK+" -rrank_sum:"+rrank_sum+" -preck:"+preck+ " -recall:"+recall );
			}

			double hit_k= CrokageUtils.round((double) hitK / goldSetQueriesApis.size(),4);
			double mrr = CrokageUtils.round(rrank_sum / goldSetQueriesApis.size(),4);
			double map = CrokageUtils.round(preck_sum / goldSetQueriesApis.size(),4);
			double mr = CrokageUtils.round(recall_sum / goldSetQueriesApis.size(),4);
			
			if(metricResult!=null) {
				if(k==10) {
					metricResult.setHitK10(hit_k);
					metricResult.setMrrK10(mrr);
					metricResult.setMapK10(map);
					metricResult.setMrK10(mr);
				}else if(k==5) {
					metricResult.setHitK5(hit_k);
					metricResult.setMrrK5(mrr);
					metricResult.setMapK5(map);
					metricResult.setMrK5(mr);
				}else {
					metricResult.setHitK1(hit_k);
					metricResult.setMrrK1(mrr);
					metricResult.setMapK1(map);
					metricResult.setMrK1(mr);
				}
			}
			
			
			System.out.println("Results: \n"
					+"\nHit@" + k + ": " + hit_k
					+ "\nMRR@" + k + ": " + mrr
					+ "\nMAP@" + k + ": " + map
					+ "\nMR@" + k + ": " + mr
					+ "");
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	private double getAvgPrecisionK(List<BucketOld> trimmedRankedList, List<UserEvaluation> goldSetEvaluations, Integer externalQuestionId) {
		int count=0;
		double found = 0;
		double linePrec = 0;
		for(BucketOld bucketOld: trimmedRankedList) {
			count++;
			for(UserEvaluation evaluation: goldSetEvaluations) {
				if(evaluation.getExternalQuestionId().equals(externalQuestionId) && evaluation.getPostId().equals(bucketOld.getPostId())) {
					found++;
					linePrec += (found / count);
				}
			}
		}
		
		if (found == 0) {
			return 0;
		}
		
		return linePrec / found;
	}

	
	protected int isApiFound_K(List<String> rapis, ArrayList<String> gapis) {
		// check if correct API is found
		int found = 0;
		outer: for (int i = 0; i < rapis.size(); i++) {
			String api = rapis.get(i);
			for (String gapi : gapis) {
				//if (gapi.endsWith(api) || api.endsWith(gapi)) {
				if (gapi.equals(api)) {
					//System.out.println("Gold:"+gapi+"\t"+"Result:"+api);
					found = 1;
					break outer;
				}
			}
		}
		return found;
	}
	
	private int isFound_K(ArrayList<Integer> recommendedIds, ArrayList<Integer> goldSetIds) {
		int found = 0;
		outer: for (int i = 0; i < recommendedIds.size(); i++) {
			Integer id = recommendedIds.get(i);
			if(goldSetIds.contains(id)) {
				found = 1;
				break outer;
			}
		}
		return found;
	}

	
	protected double getRecallK(List<String> rapis, ArrayList<String> gapis, int K) {
		// getting recall at K
		K = rapis.size() < K ? rapis.size() : K;
		double found = 0;
		for (int index = 0; index < K; index++) {
			String api = rapis.get(index);
			if (isApiFound(api, gapis)) {
				found++;
			}
		}
		return found / gapis.size();
	}
	
	
	protected boolean isApiFound(String api, List<String> gapis) {
		// check if the API can be found
		for (String gapi : gapis) {
			//if (gapi.endsWith(api) || api.endsWith(gapi)) {
			if (gapi.equals(api)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean isFound(Integer id, List<Integer> goldSetIds) {
		return goldSetIds.contains(id);
	}
	
	
	protected double getRRank(List<String> rapis, ArrayList<String> gapis) {
		double rrank = 0;
		for (int i = 0; i < rapis.size(); i++) {
			String api = rapis.get(i);
			if (isApiFound(api, gapis)) {
				rrank = 1.0 / (i + 1);
				break;
			}
		}
		return rrank;
	}
	
	protected double getRRankV2(List<Integer> recommendedIds, ArrayList<Integer> goldSetIds) {
		double rrank = 0;
		for (int i = 0; i < recommendedIds.size(); i++) {
			Integer id = recommendedIds.get(i);
			if (goldSetIds.contains(id)) {
				rrank = 1.0 / (i + 1);
				break;
			}
		}
		return rrank;
	}
	
	
	
	protected double getAvgPrecisionK(List<String> rapis, ArrayList<String> gapis) {
		double linePrec = 0;
		double found = 0;
		for (int index = 0; index < rapis.size(); index++) {
			String api = rapis.get(index);
			if (isApiFound(api, gapis)) {
				found++;
				linePrec += (found / (index + 1));
			}
		}
		if (found == 0)
			return 0;

		return linePrec / found;
	}
	
	protected double getAvgPrecisionKV2(List<Integer> recommendedIds, ArrayList<Integer> goldSetIds) {
		double linePrec = 0;
		double found = 0;
		for (int index = 0; index < recommendedIds.size(); index++) {
			Integer id = recommendedIds.get(index);
			if (goldSetIds.contains(id)) {
				found++;
				linePrec += (found / (index + 1));
			}
		}
		if (found == 0)
			return 0;

		return linePrec / found;
	}

	protected double getRecallK(List<String> rapis, ArrayList<String> gapis) {
		// getting recall at K
		double found = 0;
		for (int index = 0; index < rapis.size(); index++) {
			String api = rapis.get(index);
			if (isApiFound(api, gapis)) {
				found++;
			}
		}
		return found / gapis.size();
	}
	
	protected double getRecallKV2(List<Integer> recommendedIds, ArrayList<Integer> goldSetIds) {
		// getting recall at K
		double found = 0;
		for (int index = 0; index < recommendedIds.size(); index++) {
			Integer id = recommendedIds.get(index);
			if (goldSetIds.contains(id)) {
				found++;
			}/*else {
				System.out.println("not found: "+id);
			}*/
		}
		return found / goldSetIds.size();
	}


	private void buildMatrixForKappaAfterAgreement() throws IOException {
		List<UserEvaluation> evaluationsWithBothUsersScales = new ArrayList<>();
		crokageUtils.readGroundTruthFile(evaluationsWithBothUsersScales,QUERIES_AND_SO_ANSWERS_AGREEMENT,4,5);
		crokageUtils.buildMatrixForKappa(evaluationsWithBothUsersScales, MATRIX_KAPPA_AFTER_AGREEMENT);
		
	}





	private void buildMatrixForKappaBeforeAgreement() throws IOException {
		List<UserEvaluation> evaluationsWithBothUsersScales = new ArrayList<>();
		crokageUtils.readGroundTruthFile(evaluationsWithBothUsersScales,QUERIES_AND_SO_ANSWERS_TO_EVALUATE+".xlsx",2,3);
		crokageUtils.buildMatrixForKappa(evaluationsWithBothUsersScales, MATRIX_KAPPA_BEFORE_AGREEMENT);
		
	}



	private void buildFileForAgreementPhaseHighlightingDifferences() {
		crokageUtils.buildFileForAgreementPhaseHighlightingDifferences(QUERIES_AND_SO_ANSWERS_TO_EVALUATE+".xlsx",QUERIES_AND_SO_ANSWERS_AGREEMENT);
		
	}


}
