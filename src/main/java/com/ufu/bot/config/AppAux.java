package com.ufu.bot.config;

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

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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
import com.ufu.bot.tfidf.Document;
import com.ufu.bot.tfidf.TFIDFCalculator;
import com.ufu.bot.to.AnswerParentPair;
import com.ufu.bot.to.Bucket;
import com.ufu.bot.to.BucketOld;
import com.ufu.bot.to.Post;
import com.ufu.bot.util.BingWebSearch;
import com.ufu.bot.util.BotComposer;
import com.ufu.crokage.to.SearchResults;
import com.ufu.crokage.to.UserEvaluation;
import com.ufu.crokage.util.CrokageUtils;
import com.ufu.crokage.util.LuceneSearcherBM25;

public class AppAux {

	@Autowired
	public CrokageService crokageService;

	@Autowired
	public CrokageUtils crokageUtils;

	@Autowired
	protected GoogleWebSearch googleWebSearch;
	
	@Autowired
	protected TFIDFCalculator tfidfCalculator;
	
	@Autowired
	protected BotComposer botComposer;
	
	@Autowired
	protected LuceneSearcherBM25 luceneSearcherBM25;
	
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
	
	@Value("${BIKER_RUNNER_PATH_TRAINING}")
	public String BIKER_RUNNER_PATH_TRAINING;
	
	@Value("${BIKER_RUNNER_PATH_TEST}")
	public String BIKER_RUNNER_PATH_TEST;
	
	@Value("${BIKER_ANSWERS_SUMMARIES_TRAINING}")
	public String BIKER_ANSWERS_SUMMARIES_TRAINING;
	
	@Value("${BIKER_ANSWERS_SUMMARIES_TEST}")
	public String BIKER_ANSWERS_SUMMARIES_TEST;
	
	
	@Value("${RECOMMENDED_ANSWERS_QUERIES_CACHE}")
	public String RECOMMENDED_ANSWERS_QUERIES_CACHE;
	
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

	@Value("${SO_RELEVANT_THREADS_CONTENTS_MAP}")
	public String SO_RELEVANT_THREADS_CONTENTS_MAP;
		
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
	
	@Value("${INPUT_QUERIES_FILE_TEST_QUERIES}")
	public String INPUT_QUERIES_FILE_TEST_QUERIES;
	
	@Value("${INPUT_QUERIES_FILE_DUMMY_QUERIES}")
	public String INPUT_QUERIES_FILE_DUMMY_QUERIES;
	
	@Value("${INPUT_QUERIES_FILE_TEST_QUERIES_EVALUATION_DIR}")
	public String INPUT_QUERIES_FILE_TEST_QUERIES_EVALUATION_DIR;
	
	
	@Value("${BIKER_HOME_DATA_FOLDER}")
	public String BIKER_HOME_DATA_FOLDER;
	
	@Value("${CROKAGE_HOME_DATE_FOLDER}")
	public String CROKAGE_HOME_DATE_FOLDER;
		
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
	
	@Value("${GROUND_TRUTH_THREADS_FOR_QUERIES}")
	public String GROUND_TRUTH_THREADS_FOR_QUERIES;
	
	@Value("${ANSWERS_DIRECTORY}")
	public String ANSWERS_DIRECTORY;
	
	
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
	
	@Value("${topSimilarContentsAsymRelevanceNumber}")
	public Integer topSimilarContentsAsymRelevanceNumber;
	
	@Value("${numberOfComposedAnswers}")
	public Integer numberOfComposedAnswers;
	
	
	
	@Value("${useGoogleSearch}")
	public Boolean useGoogleSearch;  
	
	
	@Value("${numberOfGoogleResults}")
	public Integer numberOfGoogleResults;  
	
	@Value("${productionEnvironment}")
	public Boolean productionEnvironment;  
	
	
	protected long initTime;
	protected long endTime;
	protected Set<String> extractedAPIs;
	protected List<String> queries;
	protected List<String> processedQueries;
	protected Map<Integer,Set<String>> rackQueriesApisMap;
	//protected Map<String,Set<String>> rackReformulatedQueriesApis;  // because output from rack is reformulated. This map contains the real output.
	protected Map<Integer,Set<String>> bikerQueriesApisClassesMap;
	protected Map<Integer,Set<String>> bikerQueriesApisClassesAndMethodsMap;
	protected Map<Integer,Set<String>> nlp2ApiQueriesApisMap;
	protected Map<String,Set<Integer>> bigMapApisAnswersIds;
	protected Map<String,Set<Integer>> filteredSortedMapAnswersIds;
	protected Map<String,Set<Integer>> groundTruthSelectedQueriesAnswersIdsMap;
	protected Map<String, Set<Integer>> groundTruthSelectedQueriesQuestionsIdsMap;
	protected Map<String,double[]> soContentWordVectorsMap;
	protected Map<String,Double> soIDFVocabularyMap;
	protected Map<String,Double> queryIDFVectorsMap;
	protected Map<Integer,String> allQuestionsIdsTitlesMap;
	protected Map<Integer,String> allThreadsIdsContentsMap;
	protected Map<Integer,String> allAnswersIdsContentsParentContentsMap;
	protected Map<Integer,Integer> allAnswersWithUpvotesIdsParentIdsMap;
	protected Map<Integer,Integer> topAnswersWithUpvotesIdsParentIdsMap;
	protected Map<Integer,Bucket> allAnswersWithUpvotesAndCodeBucketsMap;
	protected Map<Integer,Bucket> allThreadsForAnswersWithUpvotesAndCodeBucketsMap;
	protected Set<Integer> topClassesRelevantAnswersIds;
	protected Set<Integer> topApiScoredAnswersIds;
	protected Map<Integer,String> threadsForUpvotedAnswersWithCodeIdsTitlesMap;
	protected List<String> wordsAndVectorsLines;
	//protected Map<Integer,Double> questionsIdsScores;
	protected Map<Integer,Double> bucketsIdsScores;
	protected Map<Integer,Double> answersIdsScores;
	protected Set<String> allWordsSetForBuckets;
	protected List<String> bikerTopMethods;
	protected Set<String> bikerTopClasses;
	protected Map<String,Integer> methodsCounterMap;
	protected Map<String,Integer> classesCounterMap;
	protected Map<Integer,Double> topAnswerParentPairsAnswerIdScoreMap; 
	
	protected Map<String,Set<Integer>> filteredAnswersWithApisIdsMap;
	protected Map<String,Set<Integer>> luceneTopIdsMap;
	protected Map<String,Set<Integer>> topThreadsAnswersIdsMap;
	protected Map<String,Set<Integer>> topAsymIdsMap;
	protected Map<String,Set<Integer>> topTFIDFAnswersIdsMap;
	protected Map<String,Set<Integer>> topMergeIdsMap1;
	protected Map<String,Set<Integer>> topMergeIdsMap2;
	protected Map<String,Set<Integer>> topAnswersIdsMap;
	protected Map<Integer,Set<String>> upvotedPostsIdsWithCodeApisMap;
	protected ArrayList<Document> documents;
	public Integer numberOfPostsInfoToMatchTFIDF;
	public Integer luceneMoreThreadsNumber; 
	public Integer numberOfPostsInfoToMatchAsymmetricSimRelevance;
	protected String currentQuery;
	protected Set<Bucket> candidateBuckets;
	
	protected void initializeVariables() {
		subAction = subAction !=null ? subAction.toLowerCase().trim(): null;
		dataSet = dataSet !=null ? dataSet.toLowerCase().trim(): null;
		soIDFVocabularyMap = new HashMap<>();
		wordsAndVectorsLines = new ArrayList<>();
		soContentWordVectorsMap = new HashMap<>();
		allQuestionsIdsTitlesMap = new HashMap<>();
		allThreadsIdsContentsMap = new HashMap<>();
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
		groundTruthSelectedQueriesQuestionsIdsMap = new LinkedHashMap<>();
		allAnswersWithUpvotesAndCodeBucketsMap = new HashMap<>(); 
		allThreadsForAnswersWithUpvotesAndCodeBucketsMap = new HashMap<>();
		topClassesRelevantAnswersIds = new HashSet<>();
		rackQueriesApisMap = new LinkedHashMap<>();
		bikerQueriesApisClassesMap = new LinkedHashMap<>();
		bikerQueriesApisClassesAndMethodsMap = new LinkedHashMap<>();
		nlp2ApiQueriesApisMap = new LinkedHashMap<>();
		filteredAnswersWithApisIdsMap = new LinkedHashMap<>();
		topThreadsAnswersIdsMap = new LinkedHashMap<>();
		topAsymIdsMap = new LinkedHashMap<>();
		topTFIDFAnswersIdsMap = new LinkedHashMap<>();
		topMergeIdsMap1 = new LinkedHashMap<>();
		topMergeIdsMap2 = new LinkedHashMap<>();
		topAnswersIdsMap = new LinkedHashMap<>();
		luceneTopIdsMap = new LinkedHashMap<>();
		documents = new ArrayList<>();
		threadsForUpvotedAnswersWithCodeIdsTitlesMap = new HashMap<>();
		upvotedPostsIdsWithCodeApisMap = new HashMap<>();
		processedQueries = new ArrayList<>();
		candidateBuckets = new HashSet<>();
		
	}
	

	protected double getAvgPrecisionK(List<BucketOld> trimmedRankedList, List<UserEvaluation> goldSetEvaluations, Integer externalQuestionId) {
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
	
	protected int isFound_K(ArrayList<Integer> recommendedIds, ArrayList<Integer> goldSetIds) {
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
				if(currentQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("not found: "+id);
				}
				
			}*/
		}
		return found / goldSetIds.size();
	}
	
	
	protected double getRecallKV3(List<Integer> recommendedIds, ArrayList<Integer> goldSetIds) {
		// getting recall at K
		double found = 0;
		for (Integer id: goldSetIds) {
			if (recommendedIds.contains(id)) {
				found++;
			}/*else {
				if(currentQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("not found: "+id);
				}
			}*/
		}
		return found / goldSetIds.size();
	}


	protected void buildMatrixForKappaAfterAgreement() throws IOException {
		List<UserEvaluation> evaluationsWithBothUsersScales = new ArrayList<>();
		crokageUtils.readGroundTruthFile(evaluationsWithBothUsersScales,QUERIES_AND_SO_ANSWERS_AGREEMENT,4,5);
		crokageUtils.buildMatrixForKappa(evaluationsWithBothUsersScales, MATRIX_KAPPA_AFTER_AGREEMENT);
		
	}





	protected void buildMatrixForKappaBeforeAgreement() throws IOException {
		List<UserEvaluation> evaluationsWithBothUsersScales = new ArrayList<>();
		crokageUtils.readGroundTruthFile(evaluationsWithBothUsersScales,QUERIES_AND_SO_ANSWERS_TO_EVALUATE+".xlsx",2,3);
		crokageUtils.buildMatrixForKappa(evaluationsWithBothUsersScales, MATRIX_KAPPA_BEFORE_AGREEMENT);
		
	}



	protected void buildFileForAgreementPhaseHighlightingDifferences() {
		crokageUtils.buildFileForAgreementPhaseHighlightingDifferences(QUERIES_AND_SO_ANSWERS_TO_EVALUATE+".xlsx",QUERIES_AND_SO_ANSWERS_AGREEMENT);
		
	}



	protected void buildRelevantThreadsContents() throws IOException {
		//get questions containing answers with upvotes
		List<Bucket> answersBucketsWithCode = crokageService.getUpvotedAnswersIdsContentsAndParentContents();
		
		System.out.println("number of buckets: "+answersBucketsWithCode.size());
		
		Set<Integer> parentIds = new HashSet<>();
		for(Bucket bucket: answersBucketsWithCode) {
			parentIds.add(bucket.getParentId());
		}
		
		String content = assembleContentsByThreads(parentIds,answersBucketsWithCode);
		
		crokageUtils.writeStringContentToFile(content,SO_RELEVANT_THREADS_CONTENTS_MAP);
		
	}


	public String assembleContentsByThreads(Set<Integer> parentIds, List<Bucket> answersBucketsWithCode) {
		int count=0;
		List<Post> parents = crokageService.findPostsById(new ArrayList(parentIds));
		System.out.println("number of threads(parents): "+parents.size());
		StringBuilder stringBuilder = new StringBuilder();
		for(Post parent: parents) {
			count++;
			if(count%10000==0) {
				System.out.println(count+" threads processed...");
			}
			stringBuilder.append("\n");
			stringBuilder.append(parent.getId()+" ");
			if(!StringUtils.isBlank(parent.getProcessedTitle())) {
				stringBuilder.append(parent.getProcessedTitle());
			}
			if(!StringUtils.isBlank(parent.getProcessedBody())) {
				stringBuilder.append(" ");
				stringBuilder.append(parent.getProcessedBody());
			}
			if(!StringUtils.isBlank(parent.getProcessedCode())) {
				stringBuilder.append(" ");
				stringBuilder.append(parent.getProcessedCode());
			}
			
			List<Bucket> answersBucketsWithCodeForParent = answersBucketsWithCode.stream()
					.filter(e-> e.getParentId().equals(parent.getId()))
					.collect(Collectors.toList());
			
			for(Bucket answer: answersBucketsWithCodeForParent) {
				if(!StringUtils.isBlank(answer.getProcessedBody())) {
					stringBuilder.append(" ");
					stringBuilder.append(answer.getProcessedBody());
				}
				if(!StringUtils.isBlank(answer.getProcessedCode())) {
					stringBuilder.append(" ");
					stringBuilder.append(answer.getProcessedCode());
				}
			}
		}
		return stringBuilder.toString();
	}


	protected void loadUpvotedAnswersIdsWithCodeContentsAndParentContents() {
		long initTime2 = System.currentTimeMillis();
		List<Bucket> buckets = crokageService.getUpvotedAnswersIdsContentsAndParentContents();
		
		Set<Integer> acceptedAnswersIds = buckets.stream()
		  .filter(b -> b.getAcceptedAnswerId()!=null)
		  .map(e -> e.getAcceptedAnswerId())
		  .collect(Collectors.toSet());
		
		List<Bucket> acceptedAnswersBuckets = crokageService.getBucketsByIds(acceptedAnswersIds);
		
		Map<Integer,String> acceptedAnswersBucketsMap = new HashMap<>();
		for(Bucket acceptedAnswersBucket:acceptedAnswersBuckets) {
			acceptedAnswersBucketsMap.put(acceptedAnswersBucket.getId(), acceptedAnswersBucket.getAcceptedAnswerBody());
		}
		
		Set<Integer> parentsIdsOfPostsWhoseThreadDoesNotHaveAcceptedAnswers = buckets.stream()
				  .filter(b -> b.getAcceptedAnswerId()==null)
				  .map(e -> e.getParentId())
				  .collect(Collectors.toSet());
		
		Map<Integer,Bucket> mostUpVotedBucketsMap = new HashMap<>();
		for(Integer parentId: parentsIdsOfPostsWhoseThreadDoesNotHaveAcceptedAnswers) {
			mostUpVotedBucketsMap.put(parentId,crokageService.getMostUpvotedAnswerForQuestionId2(parentId));
		}
		
				
		for(Bucket bucket:buckets) {
			if(bucket.getAcceptedAnswerId()!=null && !bucket.getId().equals(bucket.getAcceptedAnswerId())) {
				bucket.setAcceptedAnswerBody(acceptedAnswersBucketsMap.get(bucket.getAcceptedAnswerId()));
			}else if(bucket.getAcceptedAnswerId()==null ) {
				Bucket mostUpvoted = mostUpVotedBucketsMap.get(bucket.getParentId());
				if(!mostUpvoted.getId().equals(bucket.getId())) {
					bucket.setAcceptedAnswerBody(mostUpvoted.getProcessedBody());
				}
			}
				
			allAnswersWithUpvotesAndCodeBucketsMap.put(bucket.getId(), bucket);
		}
		crokageUtils.reportElapsedTime(initTime2,"getUpvotedAnswersIdsContentsAndParentContents");
		buckets=null;
		acceptedAnswersBuckets=null;
		acceptedAnswersBucketsMap=null;
		mostUpVotedBucketsMap=null;
	}
	
	
	protected void loadUpvotedAnswersIdsWithCodeContentsAndParentContents2() {
		long initTime2 = System.currentTimeMillis();
		List<Bucket> buckets = crokageService.getUpvotedAnswersIdsContentsAndParentContents();
		String threadContent=null;
		for(Bucket bucket:buckets) {
			threadContent = allThreadsIdsContentsMap.get(bucket.getParentId());
			bucket.setThreadContent(threadContent);
			//allThreadsForAnswersWithUpvotesAndCodeBucketsMap.put(bucket.getId(), bucket);
			
			allAnswersWithUpvotesAndCodeBucketsMap.put(bucket.getId(), bucket);
		}
		crokageUtils.reportElapsedTime(initTime2,"getUpvotedAnswersIdsContentsAndParentContents");
		buckets=null;
		
	}
	
	protected void loadUpvotedAnswersIdsWithCodeContentsAndParentContents3() {
		long initTime2 = System.currentTimeMillis();
		List<Bucket> buckets = crokageService.getUpvotedAnswersIdsContentsAndParentContents();
		for(Bucket bucket:buckets) {
			allAnswersWithUpvotesAndCodeBucketsMap.put(bucket.getId(), bucket);
		}
		crokageUtils.reportElapsedTime(initTime2,"getUpvotedAnswersIdsContentsAndParentContents");
		buckets=null;
		
	}
	
	protected Set<Integer> getAllThreadsForAnswersWithUpvotesAndCodeBucketsMap() {
		Set<Integer> keys = allAnswersWithUpvotesAndCodeBucketsMap.keySet();
		Set<Integer> threadsIds = new HashSet<>();
		for(Integer key: keys) {
			threadsIds.add(allAnswersWithUpvotesAndCodeBucketsMap.get(key).getParentId());
		}
		return threadsIds;	
	}


	
	protected void loadThreadsForUpvotedAnswersWithCodeIdsTitlesMap() {
		threadsForUpvotedAnswersWithCodeIdsTitlesMap = crokageService.getThreadsIdsTitlesForUpvotedAnswersWithCode();
		
	}


	protected void loadUpvotedPostsWithCodeApisMap() throws Exception {
		long initTime = System.currentTimeMillis();
		upvotedPostsIdsWithCodeApisMap.putAll(CrokageUtils.readPostsIdsApisMap(SO_UPVOTED_POSTS_WITH_CODE_APIS_FILE));
		crokageUtils.reportElapsedTime(initTime,"loadUpvotedPostsWithCodeApisMap");
	}
	
	

	protected void calculateApiScore(AnswerParentPair answerParentPair, Set<String> topClasses) {
		Set<String> allApis = new HashSet<>();
		ArrayList<String> topClassesArray = new ArrayList<>(topClasses);
		double score=0;
		double found = 0;
		double linePrec = 0;
		
		Set<String> parentApis = upvotedPostsIdsWithCodeApisMap.get(answerParentPair.getParentId());
		Set<String> answerApis = upvotedPostsIdsWithCodeApisMap.get(answerParentPair.getAnswerId());
		if(parentApis!=null) {
			allApis.addAll(parentApis);
		}
		if(answerApis!=null) {
			allApis.addAll(answerApis);
		}
		
		
		
		outer:for (int i = 0; i < topClassesArray.size(); i++) {
			String api = topClassesArray.get(i);
			if(allApis.contains(api)) {
				score += 1.0 / (i + 2);
				//break outer;
			}
		}
		
		/*outer:for (int i = 0; i < topClassesArray.size(); i++) {
			String api = topClassesArray.get(i);
			if(allApis.contains(api)) {
				found++;
				linePrec += (found / (i + 1));
			}
		}
		if (found == 0) {
			score=0;
		}else {
			score = linePrec / found;
		}*/
			
		answerParentPair.setApiScore(score);
		topClassesArray= null;
		allApis=null;
	}


	protected void loadGroundTruthSelectedQueries() throws Exception {
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
			
			if(userEvaluation.getPostId().equals(1305454)) {
				System.out.println();
			}
			
			
			if(meanLikert>=4) { 
				
				Post answer=null;
				try {
				
				//verify if post contain APIs
				answer = crokageService.findPostById(userEvaluation.getPostId());
				Set<String> classes = crokageUtils.extractClassesFromProcessedCode(answer.getCode());
				if(!classes.isEmpty()) {
					validEvaluations.add(userEvaluation);
				}/*else {
					System.out.println("Discarding from ground truth: "+userEvaluation.getPostId());
				}*/
				
				} catch (Throwable e2) {
					System.out.println("Error trying to extract classes from post: "+answer.getId());
				}
				
			}
		}
		
		if(!productionEnvironment) {
			System.out.println("Number of posts containing correct answers: "+validEvaluations.size());
		}
		
		if(queries==null || queries.isEmpty()) {
			queries = readInputQueries(); //selectedQueries
		}
				
		for(String query: queries) {
			
			Map<String,Set<Integer>> strIdsMap = validEvaluations.stream()
					.filter(e -> Objects.equals(e.getQuery(), query))
					.collect(Collectors.groupingBy(e -> new String(e.getQuery()),
			         	     Collectors.mapping(UserEvaluation::getPostId, Collectors.toSet())));
					
			groundTruthSelectedQueriesAnswersIdsMap.putAll(strIdsMap);		
			
		}
		
		if(!productionEnvironment) {
			System.out.println("Number of queries whose mean likert is > 4: "+groundTruthSelectedQueriesAnswersIdsMap.size());
		}
		
		List<String> originalQueriesList = new ArrayList<>(queries);
		
		queries.clear();
		queries.addAll(groundTruthSelectedQueriesAnswersIdsMap.keySet());
		
		System.out.println("Size of ground truth considering only posts with api calls:"+groundTruthSelectedQueriesAnswersIdsMap.size()+ " \nNew queries list size: "+queries.size());
		
		originalQueriesList.removeAll(groundTruthSelectedQueriesAnswersIdsMap.keySet());
		
		System.out.println("Excluded queries: "+originalQueriesList);
		
	}
	


	protected void processBingResultsToStandardFormat() throws IOException {
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



	protected List<String> getBikerTopMethods(Integer key) {
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



	


	protected void loadInvertedIndexFile() throws IOException {
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

	



	protected void generatePostsApisMap() throws IOException {
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
	protected void generateInvertedIndexFileFromSOPosts() throws IOException {
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


	protected void generateInputQueriesFromNLP2ApiGroudTruth() throws Exception {
		List<String> inputQueries = getQueriesFromFile(NLP2API_GOLD_SET_FILE);
		
		Path queriesFile = Paths.get(INPUT_QUERIES_FILE_NLP2API);
		Files.write(queriesFile, inputQueries, Charset.forName("UTF-8"));
		
	}



	protected List<String> getQueriesFromFile(String fileName) throws IOException {
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
	
	
	protected void reportAnswers(Set<Integer> topKRelevantAnswersIds, Map<Integer, Double> topSimilarAnswers) {
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
	protected void getQueriesAndApisFromFileMayContainDupes(Map<Integer, Set<String>> queriesApis, String fileName) throws IOException {
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



	protected void generateInputQueriesForCrokageDataSetFromExcelGroudTruth() throws FileNotFoundException {
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
	
	protected List<UserEvaluation> loadExcelGroundTruthQuestionsAndLikerts() throws IOException {
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
		
		}else if(dataSet.equals("selectedqueries-training")) {
			fileName = INPUT_QUERIES_FILE_SELECTED_QUERIES;
		
		}else if(dataSet.equals("selectedqueries-test")) {
			fileName = INPUT_QUERIES_FILE_TEST_QUERIES;
		
		}else if(dataSet.equals("selectedqueries-dummy")) {
			fileName = INPUT_QUERIES_FILE_DUMMY_QUERIES;
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

	/*protected void getPropertyValueFromLocalFile() {
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
	

	
	protected Map<Integer, Set<String>> getGoldSetByEvaluations(Map<Integer, Set<String>> goldSetMap, List<UserEvaluation> evaluationsWithBothUsersScales,String fileName) throws FileNotFoundException {
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
	
	
	
	protected Map<Integer, Set<String>> getRecommendedApis() {
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


	protected Map<String, Set<Integer>> processBingResult(List<String> allLinesCrokage) {
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







	protected void processCrawledQuestionsIDsAndBuilExcelFileForEvaluation() throws Exception {
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




	protected void crawlBingForRelatedQuestionsIdsDatasetBIKER() throws Exception {
		List<String> queries = Files.readAllLines(Paths.get(INPUT_QUERIES_FILE_BIKER));

		try (PrintWriter out = new PrintWriter(BING_TOP_RESULTS_FOR_BIKER_RAW_JSON)) {
			for (String query : queries) {
				SearchResults result = BingWebSearch.SearchWeb(prepareQueryForBing(query));
				out.println(BingWebSearch.prettify(result.jsonResponse));
			}
		}
		
	}


	
	protected void crawlBingForRelatedQuestionsIdsDatasetCrokage() throws Exception {
		List<String> queries = Files.readAllLines(Paths.get(INPUT_QUERIES_FILE_CROKAGE));

		try (PrintWriter out = new PrintWriter(BING_TOP_RESULTS_FOR_CROKAGE_RAW_JSON)) {
			for (String query : queries) {
				SearchResults result = BingWebSearch.SearchWeb(prepareQueryForBing(query));
				out.println(BingWebSearch.prettify(result.jsonResponse));
			}
		}
		
	}



	
	protected void crawlBingForRelatedQuestionsIdsDatasetNLP2Api() throws Exception {
		List<String> queries = Files.readAllLines(Paths.get(INPUT_QUERIES_FILE_NLP2API));

		try (PrintWriter out = new PrintWriter(BING_TOP_RESULTS_FOR_NLP2API_RAW_JSON)) {
			for (String query : queries) {
				SearchResults result = BingWebSearch.SearchWeb(prepareQueryForBing(query));
				out.println(BingWebSearch.prettify(result.jsonResponse));
			}
		}
	}
	

	protected void crawlStackExchangeForRelatedQuestionsIdsDatasetNLP2Api() throws IOException {
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
		
	
	protected void crawlStackExchangeForRelatedQuestionsIdsDatasetCrokage() throws IOException {
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



	protected void crawlGoogleForRelatedQuestionsIdsDatasetCrokage() throws Exception {
		List<String> queries = Files.readAllLines(Paths.get(INPUT_QUERIES_FILE_CROKAGE));
		
		try (PrintWriter out = new PrintWriter(GOOGLE_TOP_RESULTS_FOR_CROKAGE)) {
			for(String query: queries) {
				Set<Integer> soQuestionsIds = executeGoogleSearch(prepareQueryForGoogleCrawler(query),10);
				out.print("\n"+query+ " >> ");
				for(Integer soQuestionId: soQuestionsIds) {
					out.print(soQuestionId+ " ");
				}
			}
		}
	}
	



	
	protected void crawlGoogleForRelatedQuestionsIdsDatasetNLP2Api() throws Exception {
		//must be executed in files containing 100 queries each
		
		List<String> queries = Files.readAllLines(Paths.get(CROKAGE_HOME+"/data/inputQueriesNlp2Api-201-300.txt"));
		
		try (PrintWriter out = new PrintWriter(CROKAGE_HOME+"/data/googleNLP2ApiResults-201-300.txt")) {
			for(String query: queries) {
				Set<Integer> soQuestionsIds = executeGoogleSearch(prepareQueryForGoogleCrawler(query),10);
				out.print("\n"+query+ " >> ");
				for(Integer soQuestionId: soQuestionsIds) {
					out.print(soQuestionId+ " ");
				}
			}
		}
	}
	
	
	
	
	
	public String prepareQueryForGoogleCrawler(String query) {
		String completeQuery = "";
		
		Boolean containJavaToken = Pattern.compile(".*\\bjava\\b.*").matcher(query.toLowerCase()).find();
		
		if(!containJavaToken) {
			completeQuery += "java ";
		}
		//filter for SO posts is executed in another method: executeGoogleSearch
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
	
	protected Set<Integer> executeStackExchangeSearch(String query, Integer numberOfGoogleResults2) {
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





	protected void checkConditions() throws Exception {
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




	protected void readAnswersIdsParentsMap() throws IOException {
		//long initTime = System.currentTimeMillis();
		List<String> idsAndids = Files.readAllLines(Paths.get(SO_ANSWERS_IDS_PARENT_IDS_MAP));
		allAnswersWithUpvotesIdsParentIdsMap = new HashMap<>();
		crokageUtils.readWordsFromFileToMap2(allAnswersWithUpvotesIdsParentIdsMap, idsAndids);
		//crokageUtils.reportElapsedTime(initTime,"readAnswersIdsParentsMap");
	}




	protected void generateAnswersIdsParentsMap() throws IOException {
		long initTime = System.currentTimeMillis();
		readAnswersIdsParentsMap();
		crokageUtils.writeMapToFile3(allAnswersWithUpvotesIdsParentIdsMap, SO_ANSWERS_IDS_PARENT_IDS_MAP);
		crokageUtils.reportElapsedTime(initTime,"generateAnswersIdsParentsMap");
	}




	protected void readQuestionsIdsTitlesMap() throws IOException {
		long initTime = System.currentTimeMillis();
		List<String> idsAndWords = Files.readAllLines(Paths.get(SO_QUESTIONS_IDS_TITLES_MAP));
		crokageUtils.readWordsFromFileToMap(allQuestionsIdsTitlesMap,idsAndWords);
		crokageUtils.reportElapsedTime(initTime,"readQuestionsIdsTitlesMap");
	}

	
	protected void readAllAnswersIdsContentsParentContentsMap() throws IOException {
		long initTime = System.currentTimeMillis();
		System.out.println("Reading all answers ids and contents and their parent contents from file...");
		List<String> idsAndContents = Files.readAllLines(Paths.get(SO_ANSWERS_IDS_CONTENTS_PARENT_CONTENTS_MAP));
		crokageUtils.readWordsFromFileToMap(allAnswersIdsContentsParentContentsMap,idsAndContents);
		/*String titleTest = allQuestionsIdsTitlesMap.get(43966301);
		System.out.println(titleTest);*/
		crokageUtils.reportElapsedTime(initTime,"readAllAnswersIdsContentsParentContentsMap");
	}

	protected void generateQuestionsIdsContentMap() throws FileNotFoundException {
		long initTime = System.currentTimeMillis();
		//loadAllQuestionsIdsContentsAndAcceptedOrMostUpvotedAnswerContentsMap();
		loadAllAnswersIdsContentsAndParentContentsMap();
		crokageUtils.writeMapToFile2(allAnswersIdsContentsParentContentsMap, SO_ANSWERS_IDS_CONTENTS_PARENT_CONTENTS_MAP);
		crokageUtils.reportElapsedTime(initTime,"generateQuestionsIdsTitlesMap");
		
	}


	protected void generateQuestionsIdsTitlesMap() throws FileNotFoundException {
		long initTime = System.currentTimeMillis();
		loadAllQuestionsIdsTitles();
		crokageUtils.writeMapToFile2(allQuestionsIdsTitlesMap, SO_QUESTIONS_IDS_TITLES_MAP);
		crokageUtils.reportElapsedTime(initTime,"generateQuestionsIdsTitlesMap");
	}

	
	protected void loadAllAnswersIdsContentsAndParentContentsMap() {
		//load threads whose questions has score > 0 
		List<Bucket> buckets = crokageService.getUpvotedAnswersIdsContentsAndParentContents();
		System.out.println("buckets recuperados: "+buckets.size());
		int count=0;
		for(Bucket bucket: buckets) {
			count++;
			if(count%10000==0) {
				System.out.println(count+" buckets processed...");
			}
			String content = loadBucketContent(bucket,5);
			allAnswersIdsContentsParentContentsMap.put(bucket.getId(), content);
			
		}
		
	}


	protected String loadBucketContent(Bucket bucket,Integer numberOfPostsInfoToMatch) {
		StringBuffer stringBuffer = new StringBuffer();
		if(numberOfPostsInfoToMatch==1) {
			if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
				stringBuffer.append(bucket.getParentProcessedTitle());
			}
		}else if(numberOfPostsInfoToMatch==2) {
			if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
				stringBuffer.append(bucket.getParentProcessedTitle());
			}
			if(!StringUtils.isBlank(bucket.getProcessedBody())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBody());
			}
		}else if(numberOfPostsInfoToMatch==3) {
			if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
				stringBuffer.append(bucket.getParentProcessedTitle());
			}
			if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getParentProcessedBody());
			}
			if(!StringUtils.isBlank(bucket.getProcessedBody())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBody());
			}
			
		}else if(numberOfPostsInfoToMatch==4) {
			if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
				stringBuffer.append(bucket.getParentProcessedTitle());
			}
			if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getParentProcessedBody());
			}
			if(!StringUtils.isBlank(bucket.getProcessedBody())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBody());
			}
			if(!StringUtils.isBlank(bucket.getProcessedCode())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedCode());
			}
		}else if(numberOfPostsInfoToMatch==5) {
				if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
					stringBuffer.append(bucket.getParentProcessedTitle());
				}
				if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getParentProcessedBody());
				}
				if(!StringUtils.isBlank(bucket.getProcessedBody())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedBody());
				}
				if(!StringUtils.isBlank(bucket.getProcessedCode())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedCode());
				}
				if(!StringUtils.isBlank(bucket.getParentProcessedCode())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getParentProcessedCode());
				}
				
		}else if(numberOfPostsInfoToMatch==6) {
			if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
				stringBuffer.append(bucket.getParentProcessedTitle());
			}
			if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getParentProcessedBody());
			}
			if(!StringUtils.isBlank(bucket.getProcessedBody())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBody());
			}
			if(!StringUtils.isBlank(bucket.getParentProcessedCode())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getParentProcessedCode());
			}
		}else if(numberOfPostsInfoToMatch==6) {
			if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
				stringBuffer.append(bucket.getParentProcessedBody());
			}
		}else if(numberOfPostsInfoToMatch==7) {
			if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
				stringBuffer.append(bucket.getParentProcessedBody());
			}
			if(!StringUtils.isBlank(bucket.getParentProcessedCode())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getParentProcessedCode());
			}
		}else if(numberOfPostsInfoToMatch==8) {
			if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
				stringBuffer.append(bucket.getParentProcessedTitle());
			}
			if(!StringUtils.isBlank(bucket.getProcessedBody())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBody());
			}
			if(!StringUtils.isBlank(bucket.getProcessedCode())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedCode());
			}
			
		}
			
	
		return stringBuffer.toString();
	}
	
	/*protected String loadBucketContent(Bucket bucket) {
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
	}*/

	/*
	protected void loadAllQuestionsIdsContentsAndAcceptedOrMostUpvotedAnswerContentsMap() {
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


	protected void loadAllQuestionsIdsTitles() {
		long initTime = System.currentTimeMillis();
		allQuestionsIdsTitlesMap.putAll(crokageService.getQuestionsIdsTitles());
		crokageUtils.reportElapsedTime(initTime,"loadAllQuestionsIdsTitles");
	}
	
	
	protected void loadAllThreadsIdsContentsMap() throws IOException {
		long initTime = System.currentTimeMillis();
		List<String> idsAndWords = Files.readAllLines(Paths.get(SO_RELEVANT_THREADS_CONTENTS_MAP));
		crokageUtils.readWordsFromFileToMap(allThreadsIdsContentsMap,idsAndWords);
		idsAndWords=null;
		crokageUtils.reportElapsedTime(initTime,"loadAllThreadsIdsContentsMap");
		
	}




	protected void extractAPIsFromNLP2Api() throws Exception {
		
		if(queries==null) {
			queries = readInputQueries();
		}
		
		if(callNLP2ApiProcess) {
			//queries = queries.subList(0, 5);
			//First generate inputQueries file in a format NLP2Api understand
			FileWriter fw = new FileWriter(CROKAGE_HOME_DATE_FOLDER+dataSet+"-"+NLP2API_INPUT_QUERIES_FILE);
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
		    command.add(CROKAGE_HOME_DATE_FOLDER+dataSet+"-"+NLP2API_INPUT_QUERIES_FILE);
		    command.add("-outputFile");
		    command.add(CROKAGE_HOME_DATE_FOLDER+dataSet+"-"+NLP2API_OUTPUT_QUERIES_FILE);
			
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
		
		
		
		getQueriesAndApisFromFileMayContainDupes(nlp2ApiQueriesApisMap,CROKAGE_HOME_DATE_FOLDER+dataSet+"-"+NLP2API_OUTPUT_QUERIES_FILE);
		
		
	}
	
	protected void extractAPIsFromRACK() throws Exception {
		if(queries==null) {
			queries = readInputQueries();
		}
		
		
		
		if(callRACKApiProcess) {
			
			//First generate inputQueries file in a format NLP2Api understand
			FileWriter fw = new FileWriter(CROKAGE_HOME_DATE_FOLDER+dataSet+"-"+RACK_INPUT_QUERIES_FILE);
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
		    command.add(CROKAGE_HOME_DATE_FOLDER+dataSet+"-"+RACK_INPUT_QUERIES_FILE);
		    command.add("-resultFile");
		    command.add(CROKAGE_HOME_DATE_FOLDER+dataSet+"-"+RACK_OUTPUT_QUERIES_FILE);
			
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
		
		
		
		getQueriesAndApisFromFileMayContainDupes(rackQueriesApisMap,CROKAGE_HOME_DATE_FOLDER+dataSet+"-"+RACK_OUTPUT_QUERIES_FILE);
		
	}
	
	protected void extractAPIsFromBIKER() throws Exception {
		// BIKER
		if(queries==null) {
			queries = readInputQueries();
		}
		
		String runnerPath="";
		
		String inputQueriesFile="";
		String outputPath="";
		if(dataSet.equals("selectedqueries-test")) {
			runnerPath=BIKER_RUNNER_PATH_TEST;
			inputQueriesFile=BIKER_INPUT_QUERIES_FILE+"Test";
			outputPath=BIKER_HOME_DATA_FOLDER+BIKER_OUTPUT_QUERIES_FILE+"Test.txt";
		}else {
			runnerPath=BIKER_RUNNER_PATH_TRAINING;
			inputQueriesFile=BIKER_INPUT_QUERIES_FILE+"Training";
			outputPath=BIKER_HOME_DATA_FOLDER+BIKER_OUTPUT_QUERIES_FILE+"Training.txt";
		}
	
		if(callBIKERProcess) {
					
			// writing queries to be read by biker
			Path bikerQueriesFile = Paths.get(BIKER_HOME_DATA_FOLDER+inputQueriesFile);
			Files.write(bikerQueriesFile, queries, Charset.forName("UTF-8"));

			// writing script to be called
			Path scriptFile = Paths.get(BIKER_SCRIPT_FILE);
			List<String> lines=null;
			
			
			
			if(!StringUtils.isBlank(virutalPythonEnv)) { //specific env
				lines = Arrays.asList("#!/bin/bash","export PYTHONPATH=" + BIKER_HOME, "echo $PYTHONPATH", "cd $PYTHONPATH/../","virtualenv "+virutalPythonEnv,"source "+virutalPythonEnv+"/bin/activate","cd $PYTHONPATH/main","python " + BIKER_RUNNER_PATH_TRAINING);
				//lines = Arrays.asList("#!/bin/bash", "export PYTHONPATH=" + BIKER_HOME, "echo $PYTHONPATH","echo $PYTHONPATH","echo $PYTHONPATH", "cd $PYTHONPATH/../","virtualenv "+virutalPythonEnv,"source "+virutalPythonEnv+"/bin/activate","./test.sh");
			}else {
				lines = Arrays.asList("#!/bin/bash","export PYTHONPATH=" + BIKER_HOME, "echo $PYTHONPATH", "cd $PYTHONPATH/main", "python " + runnerPath);
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
		List<String> queriesWithApis = Files.readAllLines(Paths.get(outputPath), Charsets.UTF_8);
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



	protected void generateGroundTruthThreads() throws Exception {
		//load input queries considering dataset
		queries = readInputQueries();
		//load ground truth answers
		loadGroundTruthSelectedQueries();
		//load ground truth threads
		getGroundTruthSelectedQueriesQuestionsIdsMap();
		
		crokageUtils.printBigMapIntoFile(groundTruthSelectedQueriesQuestionsIdsMap,GROUND_TRUTH_THREADS_FOR_QUERIES);
	}


	protected void analyzeGroundTruthThreads() throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		Map<Integer,Integer> conterMap = new HashMap<>();
		Map<String,Set<Integer>> map = CrokageUtils.readFileToMap(GROUND_TRUTH_THREADS_FOR_QUERIES);
		Set<String> queries = map.keySet();
		int totalScore=0;
		int totalAns=0;
		int totalThreads=0;
		for(String query: queries) {
			stringBuilder.append("\n"+query+ " ");
			Set<Integer> questionsIds = map.get(query);
			for(Integer postId: questionsIds) {
				totalThreads++;
				Post post = crokageService.findPostById(postId);
				Integer ansCount = post.getAnswerCount()!=null? post.getAnswerCount():0;
				stringBuilder.append(" "+postId+ " up:"+post.getScore()+" ans:"+ansCount);
				
				if(conterMap.get(post.getScore())==null) {
					conterMap.put(post.getScore(), 1);
				}else {
					int current = conterMap.get(post.getScore());
					conterMap.put(post.getScore(), current+1);
				}
				
				totalScore+=post.getScore();
				totalAns+=post.getAnswerCount();
			}
		}
		
		Map<Integer,Integer> sorted = conterMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		System.out.println("Total threads: "+totalThreads);
		CrokageUtils.writeStringContentToFile(stringBuilder.toString(), "./data/groundTruthThreadsForQueries-analysis.txt");

	}

	protected Map<String, Set<Integer>> getGroundTruthSelectedQueriesQuestionsIdsMap() {
		//Map<String, Set<Integer>> groundTruthSelectedQueriesQuestionsIdsMap = new LinkedHashMap<>();
		
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


	public void mergeThreads(Map<String, Set<Integer>> mapMerge,Map<String, Set<Integer>> map1, Map<String, Set<Integer>> map2) {
		mapMerge.putAll(map1);
		Set<String> queries = map2.keySet();
		for(String query: queries) {
			/*Set<Integer> allIds = new HashSet<>();
			Set<Integer> map1Ids = map1.get(query);
			Set<Integer> map2Ids = map2.get(query);
			if(!CollectionUtils.isEmpty(map1Ids)) {
				mergeIds.addAll(otherIds);
			}
			mapMerge.put(query, mergeIds);*/
			if(map1.containsKey(query)) {
				mapMerge.get(query).addAll(map2.get(query));
			}else {
				mapMerge.put(query,map2.get(query));
			}
		}
		
	}

	
	protected Set<String> getAllWordsForBuckets(List<Bucket> answerBuckets) {
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


	
	
}
