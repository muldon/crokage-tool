package com.ufu.crokage.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Charsets;
import com.ufu.crokage.service.CrokageService;
import com.ufu.crokage.tfidf.Document;
import com.ufu.crokage.tfidf.TFIDFCalculator;
import com.ufu.crokage.to.AnswerParentPair;
import com.ufu.crokage.to.Bucket;
import com.ufu.crokage.to.Post;
import com.ufu.crokage.to.UserEvaluation;
import com.ufu.crokage.util.BotComposer;
import com.ufu.crokage.util.CrokageUtils;
import com.ufu.crokage.util.LuceneSearcher;

public class AppAux {

	@Autowired
	public CrokageService crokageService;

	@Autowired
	public CrokageUtils crokageUtils;

	@Autowired
	protected TFIDFCalculator tfidfCalculator;
	
	@Autowired
	protected BotComposer botComposer;
	
	@Autowired
	protected LuceneSearcher luceneSearcherBM25;
	
	//app variables

	@Value("${TMP_DIR}")
	public String TMP_DIR;
	
	@Value("${BM25_K}")
	public Float bm25_k;
	
	@Value("${BM25_B}")
	public Float bm25_b;
	
	@Value("${CROKAGE_HOME}")
	public String CROKAGE_HOME;
	
	@Value("${BIKER_ANSWERS_SUMMARIES_TRAINING}")
	public String BIKER_ANSWERS_SUMMARIES_TRAINING;
	
	
	@Value("${RECOMMENDED_ANSWERS_QUERIES_CACHE}")
	public String RECOMMENDED_ANSWERS_QUERIES_CACHE;
	
	@Value("${BIG_MAP_INVERTED_INDEX_APIS_FILE_PATH}")
	public String BIG_MAP_INVERTED_INDEX_APIS_FILE_PATH;
	
	@Value("${SO_UPVOTED_POSTS_WITH_CODE_APIS_FILE}")
	public String SO_UPVOTED_POSTS_WITH_CODE_APIS_FILE;
	
	@Value("${MODEL_VECTOR_SIZE}")
	public Integer MODEL_VECTOR_SIZE;
	
	@Value("${SO_CONTENT_FILE}")
	public String SO_CONTENT_FILE;
	
	@Value("${SO_IDF_VOCABULARY}")
	public String SO_IDF_VOCABULARY;
	
	@Value("${SO_CONTENT_WORD_VECTORS}")
	public String SO_CONTENT_WORD_VECTORS;
	
	@Value("${SO_DIRECTORY_FILES}")
	public String SO_DIRECTORY_FILES;
	
	@Value("${SO_DIRECTORY_INDEX}")
	public String SO_DIRECTORY_INDEX;
	
	@Value("${QUERIES_AND_SO_ANSWERS_TO_EVALUATE}")
	public String QUERIES_AND_SO_ANSWERS_TO_EVALUATE;
	
	@Value("${QUERIES_AND_SO_ANSWERS_AGREEMENT}")
	public String QUERIES_AND_SO_ANSWERS_AGREEMENT;
		
	@Value("${NLP2API_GOLD_SET_FILE}")
	public String NLP2API_GOLD_SET_FILE;
	
	@Value("${INPUT_QUERIES_FILE_NLP2API}")
	public String INPUT_QUERIES_FILE_NLP2API;
	
	@Value("${INPUT_QUERIES_FILE_TRAINING}")
	public String INPUT_QUERIES_FILE_TRAINING;
	
	@Value("${INPUT_QUERIES_FILE_USER_STUDY}")
	public String INPUT_QUERIES_FILE_USER_STUDY;
	
	
	
	@Value("${CROKAGE_HOME_DATE_FOLDER}")
	public String CROKAGE_HOME_DATE_FOLDER;
		
			
	
	@Value("${RACK_OUTPUT_QUERIES_FILE}")
	public String RACK_OUTPUT_QUERIES_FILE;
		
	
	@Value("${NLP2API_OUTPUT_QUERIES_FILE}")
	public String NLP2API_OUTPUT_QUERIES_FILE;
	
	@Value("${MATRIX_KAPPA_BEFORE_AGREEMENT}")
	public String MATRIX_KAPPA_BEFORE_AGREEMENT;
	
	@Value("${MATRIX_KAPPA_AFTER_AGREEMENT}")
	public String MATRIX_KAPPA_AFTER_AGREEMENT;
	
	
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
	
	
		
	@Value("${dataSet}")
	public String dataSet;

	
	
	@Value("${bm25TopNBigLimit}")
	public Integer bm25TopNBigLimit;
	
	@Value("${bm25TopNSmallLimit}")
	public Integer bm25TopNSmallLimit;
	
	@Value("${topSimilarContentsAsymRelevanceNumber}")
	public Integer topSimilarContentsAsymRelevanceNumber;
	
	@Value("${semWeight}")
	public Double semWeight;
	
	@Value("${apiWeight}")
	public Double apiWeight;
	
	@Value("${methodWeight}")
	public Double methodWeight;
	
	@Value("${tfIdfWeight}")
	public Double tfIdfWeight;
	
	@Value("${bm25Weight}")
	public Double bm25Weight;
	
	@Value("${numberOfComposedAnswers}")
	public Integer numberOfComposedAnswers;
	
	@Value("${useExtractors}")
	public Boolean useExtractors;
	
	@Value("${numberOfPostsInfoToMatchTFIDF}")
	public Integer numberOfPostsInfoToMatchTFIDF; 
	
	@Value("${numberOfPostsInfoToMatchAsymmetricSimRelevance}")
	public Integer numberOfPostsInfoToMatchAsymmetricSimRelevance; 
	
	
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
	protected Map<Integer,Double> bucketsIdsSmallSetScores;
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
	protected Map<Integer,Float> bm25ScoreAnswerIdMap;
	public Integer contentTypeTFIDF;
	public Integer contentTypeSemanticSim; 
	public Integer cutoff;
	
	public Integer luceneMoreThreadsNumber; 
	protected String currentQuery;
	protected Set<Bucket> candidateBuckets;
	protected Map<Integer,Bucket> allBucketsWithUpvotesMap;
	
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
		bucketsIdsSmallSetScores= new HashMap<>();
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
		bm25ScoreAnswerIdMap = new HashMap<>();
		allBucketsWithUpvotesMap = new HashMap<>();
		
		numberOfAPIClasses=30;
		contentTypeTFIDF=3;
		contentTypeSemanticSim= 2;
		cutoff=5;
		
	}
	

	
	
	protected int isApiFound_K(List<String> rapis, ArrayList<String> gapis) {
		// check if correct API is found
		int found = 0;
		outer: for (int i = 0; i < rapis.size(); i++) {
			String api = rapis.get(i);
			for (String gapi : gapis) {
				if (gapi.equals(api)) {
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
			}
		}
		return found / goldSetIds.size();
	}
	
	
	protected double getRecallKV3(List<Integer> recommendedIds, ArrayList<Integer> goldSetIds) {
		// getting recall at K
		double found = 0;
		for (Integer id: goldSetIds) {
			if (recommendedIds.contains(id)) {
				found++;
			}
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
			}
		}
		
	
		answerParentPair.setApiScore(score);
		topClassesArray= null;
		allApis=null;
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


	

	
	protected List<UserEvaluation> loadExcelGroundTruthQuestionsAndLikerts() throws IOException {
		String file1 = "questionsLot1and2withoutWeightsAdjuster.xlsx";
		Integer likertsResearcher1AfterAgreementColumn = 3;
		Integer likertsResearcher2AfterAgreementColumn = 4;
		
		List<UserEvaluation> evaluationsList = new ArrayList<>();
		crokageUtils.readXlsxToEvaluationList(evaluationsList,file1,likertsResearcher1AfterAgreementColumn,likertsResearcher2AfterAgreementColumn,null);
		//generateMetricsForEvaluations(evaluationsList);
		return evaluationsList;
	}

	

	



	
	
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





	




	

	private void checkFiles() throws Exception {
		
		
		File file2 = new File(NLP2API_OUTPUT_QUERIES_FILE);
		boolean exists2 = file2.exists();
		if(!exists2) {
			throw new Exception("File "+file2.getAbsolutePath()+ " must exist. Has it been provided ? ");
		}
		
		File file3 = new File(RACK_OUTPUT_QUERIES_FILE);
		boolean exists3 = file3.exists();
		if(!exists3) {
			throw new Exception("File "+file3.getAbsolutePath()+ " must exist. Has it been provided ? ");
		}
		
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
		
		switch(numberOfPostsInfoToMatch) {
		
			case 1:
				stringBuffer.append(bucket.getProcessedTitle());
				break;
			
			case 2:
				if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
					stringBuffer.append(bucket.getParentProcessedTitle());
				}
				if(!StringUtils.isBlank(bucket.getProcessedBody())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedBody());
				}
				break;	
			
				
			
			case 3:
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
				break;
				
			
			
			case 4: 
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
				break;
				
			case 5:
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
				break;
			
			case 6:
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
				break;
				
			case 7:
				if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
					stringBuffer.append(bucket.getParentProcessedBody());
				}
				if(!StringUtils.isBlank(bucket.getParentProcessedCode())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getParentProcessedCode());
				}
				break;
				
			case 8: 
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
				break;
			
		
				
				
			
				
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
