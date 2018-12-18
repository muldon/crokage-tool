package com.ufu.bot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ufu.bot.config.AppAux;
import com.ufu.bot.tfidf.Corpus;
import com.ufu.bot.tfidf.Document;
import com.ufu.bot.tfidf.VectorSpaceModel;
import com.ufu.bot.to.AnswerParentPair;
import com.ufu.bot.to.Bucket;
import com.ufu.bot.to.Post;
import com.ufu.bot.util.BotComposer;
import com.ufu.bot.util.Matrix;
import com.ufu.crokage.to.MetricResult;
import com.ufu.crokage.to.UserEvaluation;
import com.ufu.crokage.util.CrokageUtils;
import com.ufu.crokage.util.IDFCalc;
import com.ufu.crokage.util.IndexLucene;

@Component
@SuppressWarnings("unused")
public class CrokageApp extends AppAux{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	
	@PostConstruct
	public void init() throws Exception {
		long initTime = System.currentTimeMillis();
		System.out.println("Initializing CrokageApp app...");
		initializeVariables();
		
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
				+ "\n numberOfComposedAnswers: " + numberOfComposedAnswers
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
		
		case "extractAnswers":
			extractAnswers();
			break;
		
		case "runApproach":
			runApproach();
			break;
		
		case "analyzeGroundTruthThreads":
			analyzeGroundTruthThreads();
			break;
				
		case "generateGroundTruthThreads":
			generateGroundTruthThreads();
			break;
			
		case "buildRelevantThreadsContents":
			buildRelevantThreadsContents();
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


	
	private void extractAnswers() throws Exception {
				
		Map<String, Set<Integer>> recommendedResults = null;
		if(subAction.contains("userecommendedcache")) {
			recommendedResults = crokageUtils.readFileToMap(RECOMMENDED_ANSWERS_QUERIES_CACHE);
		}else {
			recommendedResults = runApproach();
			if(subAction.contains("savecache")) {
				CrokageUtils.reduceSetV2(recommendedResults, topk);
				crokageUtils.printBigMapIntoFile(recommendedResults,RECOMMENDED_ANSWERS_QUERIES_CACHE);
			}
		}
		
		CrokageUtils.reduceSetV2(recommendedResults, topk);
		
		Map<String, Set<Post>> sortedBuckets =  processAnswers(recommendedResults);
		
		CrokageUtils.composeAnswers(ANSWERS_DIRECTORY,sortedBuckets,numberOfComposedAnswers);
		
	}

	
	private Map<String, Set<Post>> processAnswers(Map<String, Set<Integer>> recommendedResults) {
		Map<String, Set<Post>> answersPosts = new LinkedHashMap<>();
		Set<String> queries = recommendedResults.keySet();
		
		for(String query: queries) {
			
			//String processedQuery = crokageUtils.processQuery(query);
			
			Set<Integer> answersIds = recommendedResults.get(query);
			Set<Post> posts = new HashSet<>(crokageService.findPostsById(new ArrayList<>(answersIds)));
			ArrayList<Post> postsList = new ArrayList<>(posts);
			System.out.println("\n\n"+query+"\n");
			
			for(int i=0; (i<numberOfComposedAnswers && i<posts.size()); i++) {
				Post post = postsList.get(i);
				/*if(post.getId()==30281392) {
					System.out.println();
				}*/
				//if sentence has low similarity with code, next()
				boolean processed = crokageUtils.processSentences(post,query);
				if(!processed) {
					posts.remove(post);
				}
			}
			postsList=null;
			answersIds=null;
			answersPosts.put(query, posts);
		}
		
		return answersPosts;
	}


	protected Map<String, Set<Integer>> runApproach() throws Exception {
		String processedQuery;
		String rawQuery;
		Set<Integer> answersWithTopFrequentAPIs=null;
		Set<Integer> filteredAnswersWithAPIs=null;
		Set<Integer> luceneSmallSetIds=null;
		Set<Integer> luceneBigSetIds=null;
		Set<Integer> asymTopAnswersThreadsIds=null;
		Set<Integer> topAnswersForThreadsIds=null;
		Set<Integer> topKRelevantQuestionsIds=null;
		Set<Integer> tfIdfTopAnswersIds=null;
		Map<String, Set<Integer>> recommendedResults = new LinkedHashMap<>();
		
		//load input queries considering dataset
		queries = readInputQueries();
		//load ground truth answers
		loadGroundTruthSelectedQueries();
		
		for(String query: queries) {
			processedQueries.add(crokageUtils.processQuery(query));
		}
		
		//getThreadsIdsForUpvotedAnswersWithCode();
		//System.out.println("Size of threads: "+threadsForUpvotedAnswersWithCodeIdsTitlesMap.size());
		
		//loadAllThreadsIdsContentsMap();
		loadUpvotedAnswersIdsWithCodeContentsAndParentContents();
		luceneSearcherBM25.buildSearchManager(allAnswersWithUpvotesAndCodeBucketsMap);
		//luceneSearcherBM25.buildSearchManager(allAnswersWithUpvotesAndCodeBucketsMap,allThreadsIdsContentsMap);
		//allAnswersWithUpvotesAndCodeBucketsMap=null;
		//allThreadsIdsContentsMap=null;
		
		//load all word vectors only once
		readSoContentWordVectorsForAllWords();
				
		//load questions map (id,title)
		//readQuestionsIdsTitlesMap();
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
		
		//loadThreadsForUpvotedAnswersWithCodeIdsTitlesMap();
		loadUpvotedPostsWithCodeApisMap();
		
		//combine state of art approaches considering the order in parameters
		Map<Integer, Set<String>> recommendedApis = getRecommendedApis();
		
		Set<Integer> keys = recommendedApis.keySet();
		if(keys.size()!=queries.size()) {
			throw new Exception("Queries size are different from the ones recommended by the API extractors. Consider extracting them again. ");
		}
		
		/*
		 * best parameters so far
		int bm25TopNBigLimitArr[]   = {5000};
		int bm25TopNSmallLimitArr[] = {100};
		double simWeights[] 		= {1};
		double classFreqWeights[]   = {0.25};
		double methodFreqWeights[]  = {0.75};
		double tfIdfWeightArr[] 	= {0.5};
		double repWeights[] 		= {0.75};
		double upWeights[] 			= {0.5};
		int numberOfPostsInfoToMatchTFIDFArr[] = {3};
		int numberOfPostsInfoToMatchAsymmetricSimRelevanceArr[] = {2};
		int topSimilarContentAsymRelevanceNumberArr[] = {50};
		int numberOfAPIClassesArr[] = {20};
		int topkArr[] = {10,5,1};
		*/
		int bm25TopNBigLimitArr[]   = {5000};
		int bm25TopNSmallLimitArr[] = {100};
		double simWeights[] 		= {1};
		double classFreqWeights[]   = {0.25};
		double methodFreqWeights[]  = {0.75};
		double tfIdfWeightArr[] 	= {0.5};
		double repWeights[] 		= {0.75};
		double upWeights[] 			= {0.5};
		int numberOfPostsInfoToMatchTFIDFArr[] = {3};
		int numberOfPostsInfoToMatchAsymmetricSimRelevanceArr[] = {2};
		int topSimilarContentAsymRelevanceNumberArr[] = {100};
		int numberOfAPIClassesArr[] = {20};
		int topkArr[] = {10};
	
		int run = 0;
		int count=0;
		String obsTmp;
		String firstObs;
		
		for(int bm25TopNBigLimit: bm25TopNBigLimitArr) {
			for(int bm25TopNSmallLimit: bm25TopNSmallLimitArr) {
				for(double simWeight: simWeights) {
					for(double classFreqWeight: classFreqWeights) {
						for(double methodFreqWeight: methodFreqWeights) {
							for(double tfIdfWeight: tfIdfWeightArr) {
								for(double repWeight: repWeights) {
									for(double upWeight: upWeights) {
										for(int numberOfPostsInfoToMatchTFIDF: numberOfPostsInfoToMatchTFIDFArr){
											for(int numberOfPostsInfoToMatchAsymmetricSimRelevance: numberOfPostsInfoToMatchAsymmetricSimRelevanceArr){
												for(int topSimilarContentsAsymRelevanceNumber: topSimilarContentAsymRelevanceNumberArr){
													for(int numberOfAPIClasses: numberOfAPIClassesArr) {
														
		
			
			int sum1=0;
			int sum2=0;
			int sum3=0;
			int sum4=0;
			int sum5=0;
			BotComposer.setSimWeight(simWeight);
			BotComposer.setClassFreqWeight(classFreqWeight);
			BotComposer.setMethodFreqWeight(methodFreqWeight);
			BotComposer.setRepWeight(repWeight);
			BotComposer.setUpWeight(upWeight);
			BotComposer.setTfIdfWeight(tfIdfWeight);
			this.numberOfPostsInfoToMatchTFIDF=numberOfPostsInfoToMatchTFIDF;
			this.topSimilarContentsAsymRelevanceNumber=topSimilarContentsAsymRelevanceNumber;
			this.numberOfPostsInfoToMatchAsymmetricSimRelevance=numberOfPostsInfoToMatchAsymmetricSimRelevance;
			this.numberOfAPIClasses=numberOfAPIClasses;
			
			
			initTime = System.currentTimeMillis();
			firstObs = " - numberOfPostsInfoToMatchTFIDF: "+numberOfPostsInfoToMatchTFIDF+ " - topSimilarContentsAsymRelevanceNumber: "+topSimilarContentsAsymRelevanceNumber+" - bm25TopNSmallLimit: "+bm25TopNSmallLimit+  " - bm25TopNBigLimit: "+bm25TopNBigLimit+  " - numberOfPostsInfoToMatchAsymmetricSimRelevance:"+numberOfPostsInfoToMatchAsymmetricSimRelevance;
			System.out.println("\n\nRun: "+count+firstObs );
			run++;
			for(Integer key: keys) {  //for each query
				long initTime2 = System.currentTimeMillis();
				count++;
				rawQuery = queries.get(key-1);
				//System.out.println("\n\nQuery: "+count+" "+rawQuery+ " - bm25TopNResults: "+bm25TopNResults+ " - topSimilarContentsAsymRelevanceNumber: "+topSimilarContentsAsymRelevanceNumber+ " - ");
				
				processedQuery = processedQueries.get(key-1);
				//System.out.println("Processed query: "+processedQuery);
				
				Set<String> topClasses = new LinkedHashSet<>(recommendedApis.get(key));
				crokageUtils.setLimitV2(topClasses, numberOfAPIClasses);
				/*if(rawQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("topClasses number: "+topClasses.size()+ " >> "+topClasses);
				}*/
				
				//get vectors for query words
				double[][] matrix1 = CrokageUtils.getMatrixVectorsForQuery(processedQuery,soContentWordVectorsMap);
				
				//get idfs for query
				double[][] idf1 = CrokageUtils.getIDFMatrixForQuery(processedQuery, soIDFVocabularyMap);
				
				//first filter, lucene
				luceneSmallSetIds = luceneSearch(processedQuery,bm25TopNSmallLimit,bm25TopNBigLimit);
				luceneBigSetIds = luceneSearcherBM25.getBigSetAnswersIds();
				luceneTopIdsMap.put(rawQuery, luceneSmallSetIds);
				sum1+=luceneSmallSetIds.size();
				/*if(rawQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("Size of luceneSmallSetThreadsIds: "+sum1);
				}*/
			
				asymTopAnswersThreadsIds = getAnswersForTopThreads(luceneBigSetIds,matrix1,idf1);
				topAsymIdsMap.put(rawQuery, asymTopAnswersThreadsIds);
				sum2+=asymTopAnswersThreadsIds.size();
				/*if(rawQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("Size of asymTopThreadsIds: "+sum2);
				}*/
				
				
				Set<Integer> mergeIds=new HashSet<>(luceneSmallSetIds);
				mergeIds.addAll(asymTopAnswersThreadsIds);
				topMergeIdsMap.put(rawQuery, mergeIds);
				sum3+=mergeIds.size();
				/*if(rawQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("Size of mergeIds: "+sum3);
				}*/
				
				
				filteredAnswersWithAPIs = filterAnswersByAPIs(topClasses,mergeIds);
				filteredAnswersWithApisIdsMap.put(rawQuery, filteredAnswersWithAPIs);
				sum4+=filteredAnswersWithAPIs.size();
				/*if(rawQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("Size of filteredAnswersWithAPIs: "+sum4);
				}*/
				
				/*
				tfIdfTopAnswersIds = getAnswersForTFIDF(luceneBigSetIds, processedQuery);
				topTFIDFAnswersIdsMap.put(rawQuery, tfIdfTopAnswersIds);
				sum5+=tfIdfTopAnswersIds.size();
				if(rawQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("Size of topTFIDFAnswersIdsMap: "+sum5);
				}*/
				
				//last filter: relevance similarity
				Set<Integer> topKRelevantAnswersIds = getTopKRelevantAnswers(filteredAnswersWithAPIs,matrix1,idf1,key,processedQuery);
				/*if(rawQuery.contains("How do I copy a file in JDK 7")) {
					System.out.println("Size of topKRelevantAnswersIds: "+topKRelevantAnswersIds.size());
				}*/
				
				recommendedResults.put(rawQuery, topKRelevantAnswersIds);
				//crokageUtils.reportElapsedTime(initTime2,"topKRelevantAnswersIds");
				topClasses=null;
				mergeIds=null;
				//crokageUtils.reportElapsedTime(initTime2,"for query");
				
			}
			
			System.out.println("\nSaving baselines");
			String obsComp=null;
			MetricResult metricResult=null;
			for(int topk: topkArr) {
				obsComp = obs + " - "+firstObs+ " luceneTopIdsMap (avg): "+sum1/keys.size();
				metricResult = new MetricResult("luceneTopIdsMap",bm25TopNSmallLimit,null,null,null,cutoff,topk,obsComp,null);
				analyzeResults(luceneTopIdsMap,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"luceneTopIdsMap");
				crokageService.saveMetricResult(metricResult);
			}
			
			for(int topk: topkArr) {
				obsComp = obs + " - "+firstObs+ " topAsymIdsMap (avg): "+sum2/keys.size();
				metricResult = new MetricResult("topAsymIdsMap",bm25TopNSmallLimit,bm25TopNBigLimit,null,topSimilarContentsAsymRelevanceNumber,cutoff,topk,obsComp,null);
				analyzeResults(topAsymIdsMap,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"topAsymIdsMap");
				crokageService.saveMetricResult(metricResult);
			}
			
			
			for(int topk: topkArr) {
				obsComp = obs + " - "+firstObs+ " topMergeIdsMap (avg): "+sum3/keys.size();
				metricResult = new MetricResult("topMergeIdsMap",bm25TopNSmallLimit,bm25TopNBigLimit,null,topSimilarContentsAsymRelevanceNumber,cutoff,topk,obsComp,null);
				analyzeResults(topMergeIdsMap,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"topMergeIdsMap");
				crokageService.saveMetricResult(metricResult);
			}
			
			
			for(int topk: topkArr) {
				obsComp = obs + " filteredAnswersWithApisIdsMap filter (avg): "+sum4/keys.size();
				metricResult = new MetricResult("filteredAnswersWithApisIdsMap",bm25TopNSmallLimit,bm25TopNBigLimit,topApisScoredPairsPercent,topSimilarContentsAsymRelevanceNumber,cutoff,topk,obsComp,numberOfAPIClasses);
				analyzeResults(filteredAnswersWithApisIdsMap,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"filteredAnswersWithApisIdsMap");
				crokageService.saveMetricResult(metricResult);
			}
			
			/*for(int topk: topkArr) {
				obsComp = obs + " topTFIDFAnswersIdsMap filter (avg): "+sum5/keys.size();
				metricResult = new MetricResult("topTFIDFAnswersIdsMap",null,bm25TopNBigLimit,null,null,cutoff,topk,obsComp,null);
				analyzeResults(topTFIDFAnswersIdsMap,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"topTFIDFAnswersIdsMap");
				crokageService.saveMetricResult(metricResult);
			}*/
			
			
			for(int topk: topkArr) {
				Map<String, Set<Integer>> recommendedResultsTmp = crokageUtils.copy(recommendedResults);
				obsComp = firstObs+ " - final relevance. Size: "+topk;
				metricResult = new MetricResult("final relevance",bm25TopNSmallLimit,bm25TopNBigLimit,topApisScoredPairsPercent,topSimilarContentsAsymRelevanceNumber,cutoff,topk,obsComp,numberOfAPIClasses);
				analyzeResults(recommendedResultsTmp,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"final relevance");
				crokageService.saveMetricResult(metricResult);
				recommendedResultsTmp=null;
			}
			
			CrokageUtils.reportElapsedTime(initTime,"for run");
			
		}
		
		}}}}}}}}}}}
		
		return recommendedResults;
	}



	protected Set<Integer> getAnswersForTopThreads(Set<Integer> relevantAnswersIds, double[][] matrix1, double[][] idf1) {
		Set<Bucket> candidateBuckets = new HashSet<>();
		for(Integer answerId: relevantAnswersIds) {
			candidateBuckets.add(allAnswersWithUpvotesAndCodeBucketsMap.get(answerId));
		}
		
		
		String comparingContent;
		bucketsIdsScores.clear();
		
		for(Bucket bucket:candidateBuckets) {
			
			//get the word vectors for each word of the query
			comparingContent = loadBucketContent(bucket,numberOfPostsInfoToMatchAsymmetricSimRelevance);
			if(StringUtils.isBlank(comparingContent)) {
				continue;
			}
			
			//get vectors for query2 words
			double[][] matrix2 = CrokageUtils.getMatrixVectorsForQuery(comparingContent,soContentWordVectorsMap);
			
			//get idfs for query2
			double[][] idf2 = CrokageUtils.getIDFMatrixForQuery(comparingContent, soIDFVocabularyMap);
			
			double simPair = CrokageUtils.round(Matrix.simDocPair(matrix1,matrix2,idf1,idf2),6);
			
			bucketsIdsScores.put(bucket.getId(), simPair);
			
			//bucket.setTitleScore(simPair);
			
		}
				
		//int topSimilarContents = bucketsIdsScores.size()*topSimilarContentsAsymRelevanceNumber/100;
		
		//sort scores in descending order and consider the first topSimilarQuestionsNumber parameter
		Map<Integer,Double> topKRelevantAnswersBucketsMap = bucketsIdsScores.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       .limit(topSimilarContentsAsymRelevanceNumber)
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		
		Set<Integer> topKRelevantAnswersIds = topKRelevantAnswersBucketsMap.keySet();
		
		
		return topKRelevantAnswersIds;
		
		
	}
	
	
	
	


	protected Set<Integer> luceneSearch(String processedQuery, Integer smallLimit, Integer bigSetLimit) throws Exception {
		//List<Bucket> buckets = crokageService.getUpvotedAnswersIdsContentsAndParentContents();
		
		Set<Integer> answersIds = luceneSearcherBM25.search(processedQuery, smallLimit,bigSetLimit);
				
		return answersIds;
		
	}

	


	protected Set<Integer> filterAnswersByAPIs(Set<String> topClasses, Set<Integer> relevantAnswersIds) {
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
				//if(relevantAnswersIds.contains(answerId) && allAnswersWithUpvotesAndCodeBucketsMap.containsKey(answerId)) { //filtered by lucene and is upvoted answer
				if(relevantAnswersIds.contains(answerId) && allAnswersWithUpvotesAndCodeBucketsMap.containsKey(answerId)) { //filtered by lucene and is upvoted answer 
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
		/*
		sortedPairs= topAnswerParentPairs.stream()
				.sorted(Comparator.comparing(AnswerParentPair::getApiScore).reversed())
				.limit(topApisScoredPairsPercent)
				//.map(x -> x.getAnswerId())
				.collect(Collectors.toList());
		*/
		
		Set<Integer> topNAnswersIdsSet = new LinkedHashSet<>(topNAnswersIds);
		//topApiScoredAnswersIds.addAll(topNAnswersIdsSet);
		
		/*for(Integer topAnswerId: topNAnswersIdsSet) {
			topAnswersWithUpvotesIdsParentIdsMap.put(topAnswerId, allAnswersWithUpvotesIdsParentIdsMap.get(topAnswerId));
		}*/
		
		
		topNAnswersIds=null;
		answerParentThreads=null;
		//crokageUtils.reportElapsedTime(initTime,"getAnswersForTopFrequentAPIs");
		
		return topNAnswersIdsSet;
	}


	protected void generateMetricsForBaselineApproaches() throws Exception {
		//google
		//load all queries and the ids found for google
		Map<String,Set<Integer>> googleQueriesAndSOIdsMap = new LinkedHashMap<>();
		Map<String,Set<Integer>> bingQueriesAndSOIdsMap = new LinkedHashMap<>();
		Map<String,Set<Integer>> soQueriesAndIdsMap = new LinkedHashMap<>();
		
		Map<String,Set<Integer>> soAcceptedOrMostUpvotedSOIdsMapRecommended = new LinkedHashMap<>();
		Map<String,Set<Integer>> soMostUpvotedSOIdsMapRecommended = new LinkedHashMap<>();
		
		Map<String, Set<Integer>> recommendedResults = new LinkedHashMap<>();
		
		
		googleQueriesAndSOIdsMap = CrokageUtils.readCrawledQuestionsIds(GOOGLE_TOP_RESULTS_FOR_NLP2API);
		googleQueriesAndSOIdsMap.putAll(CrokageUtils.readCrawledQuestionsIds(GOOGLE_TOP_RESULTS_FOR_CROKAGE));
		bingQueriesAndSOIdsMap = CrokageUtils.readCrawledQuestionsIds(BING_TOP_RESULTS_FOR_NLP2API);
		bingQueriesAndSOIdsMap.putAll(CrokageUtils.readCrawledQuestionsIds(BING_TOP_RESULTS_FOR_CROKAGE));
		soQueriesAndIdsMap = CrokageUtils.readCrawledQuestionsIds(SE_TOP_RESULTS_FOR_NLP2API);
		soQueriesAndIdsMap.putAll(CrokageUtils.readCrawledQuestionsIds(SE_TOP_RESULTS_FOR_CROKAGE));
		
		
		// google, bing, so, all
		String approach = "all";
		
		
		queries = readInputQueries();
		//load ground truth
		loadGroundTruthSelectedQueries();
		
		//getGroundTruthSelectedQueriesQuestionsIdsMap();
		
		for(String query: queries) {  //for each query 
			Set<Integer> acceptedOrMostUpvotedAnswersIds = new LinkedHashSet<>();
			Set<Integer> approachQuestionsIds= new LinkedHashSet<>();
			
			if(approach.equals("google")) {
				approachQuestionsIds.addAll(googleQueriesAndSOIdsMap.get(query));
				if(approachQuestionsIds.size()>10) {
					throw new Exception("size should be 10");
				}
			}else if(approach.equals("bing")) {
				approachQuestionsIds.addAll(bingQueriesAndSOIdsMap.get(query));
				if(approachQuestionsIds.size()>20) {
					throw new Exception("size should be 20");
				}
			}else if(approach.equals("so")) {
				if(!CollectionUtils.isEmpty(soQueriesAndIdsMap.get(query))){
					approachQuestionsIds.addAll(soQueriesAndIdsMap.get(query));
				}
			
			}else if(approach.equals("all")) {
				approachQuestionsIds.addAll(googleQueriesAndSOIdsMap.get(query));
				approachQuestionsIds.addAll(bingQueriesAndSOIdsMap.get(query));
				if(!CollectionUtils.isEmpty(soQueriesAndIdsMap.get(query))){
					approachQuestionsIds.addAll(soQueriesAndIdsMap.get(query));
				}
			}
			
			
			//for SO
			/*Set<Integer> groundTruthAnswers = groundTruthSelectedQueriesAnswersIdsMap.get(query);
			Set<Integer> acceptedAnswersIdsOrMostUpvoted = new HashSet<>();
			for(Integer answerId: groundTruthAnswers) {
				Integer parentId = crokageService.findPostById(answerId).getParentId();
				Post parent = crokageService.findPostById(parentId);
				if(parent.getAcceptedAnswerId()!=null) {
					acceptedAnswersIdsOrMostUpvoted.add(parent.getAcceptedAnswerId());
				}else {
					Integer mostUpvotedAnswer = crokageService.getMostUpvotedAnswerForQuestionId(parentId);
					acceptedAnswersIdsOrMostUpvoted.add(mostUpvotedAnswer);
				}
			}
			soAcceptedOrMostUpvotedSOIdsMapRecommended.put(query,acceptedAnswersIdsOrMostUpvoted);*/
			
			if(!CollectionUtils.isEmpty(approachQuestionsIds)) {
				for(Integer questionId: approachQuestionsIds) {
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
			}
			recommendedResults.put(query,acceptedOrMostUpvotedAnswersIds);
			
			
		}
		
		//CrokageUtils.writeMapToFile4(groundTruthSelectedQueriesAnswersIdsMap,"./data/googleSelectedQueriesGroundTruthAnswersIds.txt");
		//CrokageUtils.writeMapToFile4(recommendedResults,"./data/googleSelectedQueriesRecommendedAcceptedOrMostUpvotedAnswersIds.txt");
		
		Integer topkArr[] = new Integer[]{10,5,1};
		
		for(Integer topk:topkArr) {
			MetricResult metricResult = new MetricResult(approach,0,0,0,0,0,topk,"approach "+approach+" for top10",0);
			analyzeResults(recommendedResults,groundTruthSelectedQueriesAnswersIdsMap,metricResult, approach);
			crokageService.saveMetricResult(metricResult);
		}
		
		
	}





	protected void buildLuceneIndex() {
		long start = System.currentTimeMillis();
		//String docs = "/home/rodrigo/tmp/sodirectory";
		//String index = "/home/rodrigo/tmp/sodirindex";
		IndexLucene indexer = new IndexLucene(SO_DIRECTORY_INDEX, SO_DIRECTORY_FILES);
		indexer.indexCorpusFiles();
		crokageUtils.reportElapsedTime(start, "buildLuceneIndex");
		
	}




	protected void tester() throws Exception {
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
	protected void buildSOSetOfWords() throws IOException {
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




	protected void buildIDFVocabulary() throws IOException {
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


	protected void buildIDFVocabularyOld() throws IOException {
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




	protected void readSoContentWordVectorsForAllWords() throws IOException {
		readSOContentWordAndVectorsLines();
		
		long initTime = System.currentTimeMillis();
		System.out.println("Parsing vectors...");
		CrokageUtils.getVectorsFromLines(wordsAndVectorsLines,soContentWordVectorsMap);
		wordsAndVectorsLines=null;
		crokageUtils.reportElapsedTime(initTime,"parsing vectors");
		
	}


	protected void readSOContentWordAndVectorsLines() throws IOException {
		System.out.println("Reading SO word vectors file...");
		long initTime = System.currentTimeMillis();
		wordsAndVectorsLines.addAll(Files.readAllLines(Paths.get(SO_CONTENT_WORD_VECTORS)));
		crokageUtils.reportElapsedTime(initTime,"readSOContentWordAndVectorsLines. Total number of words: "+wordsAndVectorsLines.size());
		
		
	}




	protected void buildSODirectoryFiles() throws FileNotFoundException {
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


	protected void generateTrainingFileToFastText() throws FileNotFoundException {
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

	protected String getPostContent(Post post) {
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




	protected void reduceBigMapFileToMininumAPIsCount() throws Exception {
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




	protected void resetAPIExtractors() {
		rackQueriesApisMap.clear();
		bikerQueriesApisClassesMap.clear();
		nlp2ApiQueriesApisMap.clear();
	}




	private Set<Integer> getAnswersForTFIDF(Set<Integer> candidateAnswersIds, String processedQuery) {
		Set<Bucket> candidateBuckets = new HashSet<>();
		for(Integer answerId: candidateAnswersIds) {
			candidateBuckets.add(allAnswersWithUpvotesAndCodeBucketsMap.get(answerId));
		}
		
		String comparingContent;
		Map<Integer, Double> answersIdsScores = new LinkedHashMap<>();
		
		documents.clear();
		Document queryDocument = new Document(processedQuery, 0);
		documents.add(queryDocument);
		
		for(Bucket bucket:candidateBuckets) {
			comparingContent = loadBucketContent(bucket,2);
			Document document = new Document(comparingContent, bucket.getId());
			documents.add(document);
			bucket.setDocument(document);
		}
		
		Corpus corpus = new Corpus(documents);
		VectorSpaceModel vectorSpace = new VectorSpaceModel(corpus);
		
		for(Bucket bucket:candidateBuckets) {
			double tfIdfCosineSimScore = vectorSpace.cosineSimilarity(queryDocument, bucket.getDocument());
			bucket.setTfIdfCosineSimScore(tfIdfCosineSimScore);
			answersIdsScores.put(bucket.getId(), tfIdfCosineSimScore);
			
		}
		
		//sort scores in descending order 
		Map<Integer,Double> topSimilarAnswers = answersIdsScores.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		candidateAnswersIds.clear();
		candidateAnswersIds.addAll(topSimilarAnswers.keySet());
		corpus=null;
		vectorSpace=null;
		candidateBuckets=null;
		queryDocument=null;
		answersIdsScores = null;
		return candidateAnswersIds;
	}
	
	
	protected Set<Integer> getTopKRelevantAnswers(Set<Integer> candidateAnswersIds, double[][] matrix1, double[][] idf1, Integer key, String processedQuery) throws IOException {
		Set<Bucket> candidateBuckets = new HashSet<>();
		for(Integer answerId: candidateAnswersIds) {
			candidateBuckets.add(allAnswersWithUpvotesAndCodeBucketsMap.get(answerId));
		}
		
		
		String comparingTitle;
		String parentTitle;
		String comparingContent;
		answersIdsScores.clear();
		double maxSimPair=0;
		double maxApiScore=0;
		double maxCosSimScore=0;
		methodsCounterMap.clear();
		classesCounterMap.clear();
		documents.clear();
		Document queryDocument = new Document(processedQuery, 0);
		documents.add(queryDocument);
		
		String rawQuery = queries.get(key-1);
		
		for(Bucket bucket:candidateBuckets) {
			comparingContent = loadBucketContent(bucket,numberOfPostsInfoToMatchTFIDF);
			Document document = new Document(comparingContent, bucket.getId());
			documents.add(document);
			bucket.setDocument(document);
		}

		Corpus corpus = new Corpus(documents);
		VectorSpaceModel vectorSpace = new VectorSpaceModel(corpus);
			
		for(Bucket bucket:candidateBuckets) {
			//second ranking
			try {
				
				double tfIdfCosineSimScore = vectorSpace.cosineSimilarity(queryDocument, bucket.getDocument());
				bucket.setTfIdfCosineSimScore(tfIdfCosineSimScore);
				
				if(tfIdfCosineSimScore>maxCosSimScore) {
					maxCosSimScore=tfIdfCosineSimScore;
				}
				
				double simPair = bucketsIdsScores.get(bucket.getId());
				bucket.setSimPair(simPair);
				if(simPair>maxSimPair) {
					maxSimPair=simPair;
				}
				
				if(!topAnswerParentPairsAnswerIdScoreMap.isEmpty() && topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId())!=null) {
					double apiAnswerPairScore = topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId());
					if(apiAnswerPairScore>maxApiScore) {
						maxApiScore=apiAnswerPairScore;
					}
				}
				
				countMethods(bucket.getCode(),bucket);
				
			} catch (Exception e) {
				System.out.println("error here... post: "+bucket.getId());
				throw e;
			}
			
		}
		
		if(!productionEnvironment) {
			reportCommonMethods();
		}
		
		//normalization and other relevance boosts
		for(Bucket bucket:candidateBuckets) {
				double simPair = bucket.getSimPair();
				simPair = (simPair / maxSimPair); //normalization
				
				double tfIdfCosineSimScore = bucket.getTfIdfCosineSimScore();
				tfIdfCosineSimScore = (tfIdfCosineSimScore / maxCosSimScore); //normalization
	
				double apiAnswerPairScore=0;
				if(!topAnswerParentPairsAnswerIdScoreMap.isEmpty() && topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId())!=null) {
					apiAnswerPairScore = topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId());
					apiAnswerPairScore = (apiAnswerPairScore/maxApiScore); //normalization
				}
				
				double finalScore = BotComposer.calculateRankingScore(simPair,bucket,methodsCounterMap,apiAnswerPairScore,tfIdfCosineSimScore);
				answersIdsScores.put(bucket.getId(), finalScore);
				
			
		}
		
		Map<Integer,Double> topSimilarAnswers = answersIdsScores.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       //.limit(topSimilarAnswersNumber)
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		corpus=null;
		vectorSpace=null;
		candidateBuckets=null;
		queryDocument=null;
		return topSimilarAnswers.keySet();
	}


	

	protected void reportCommonClasses() {
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


	protected void reportCommonMethods() {
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


	protected void countMethods(String code,Bucket bucket) {
		Set<String> codes = crokageUtils.getMethodCalls(code);
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


	protected boolean codeContainAnyClass(String processedCode) {
		for(String bikerClass: bikerTopClasses) {
			if(processedCode.contains(bikerClass.toLowerCase())) {
				return true;
			}
		}
		
		return false;
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




	protected void reportSimilarRelatedPosts(Set<Integer> topSimilarIds, String postType, String obs) {
		int listSize = topSimilarIds.size();
		int k = listSize > 10? 10:listSize;
		String topIdsStr= obs+ " - Number of related "+postType+":"+listSize +". Showing first ("+k+") ids: ";
		topIdsStr+= StringUtils.join(new ArrayList(topSimilarIds).subList(0, k), ',');
		System.out.println(topIdsStr.toString());
		
		
	}




	protected void addVectorsToSoContentWordVectorsMap(Set<Integer> answersWithTopFrequentAPIsIds) throws Exception {
		Set<String> contents = new LinkedHashSet<>();
		String content;
		for(Integer answerId:answersWithTopFrequentAPIsIds) {
			content = allAnswersIdsContentsParentContentsMap.get(answerId);
			contents.add(content);
		}
		crokageUtils.readVectorsFromSOMapForWords(soContentWordVectorsMap,contents,wordsAndVectorsLines);
		
		contents=null;
				
	}
	

	protected void addVectorsToSoContentWordVectorsMapOld(Set<Integer> answersWithTopFrequentAPIsIds) throws Exception {
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



	protected void generateMetricsForApiExtractors(Integer[] kArray) throws Exception {
		
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


	protected int getApisForApproaches() throws Exception {
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



	protected Map<Integer, Set<String>> getGoldSetQueriesApis() throws IOException {
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



	public void analyzeResults(Map<String, Set<Integer>> recommended,Map<String, Set<Integer>> goldSet, MetricResult metricResult, String approach) {
		int hitK = 0;
		int correct_sum = 0;
		double rrank_sum = 0;
		double precision_sum = 0;
		double preck_sum = 0;
		double recall_sum = 0;
		double fmeasure_sum = 0;

		Integer topk = metricResult.getTopk();
		String kvalue="";
		if(topk!=null && topk>0) {
			CrokageUtils.reduceSetV2(recommended, topk);
		}else if(topk==null) {
			topk=1; //save in top1 
		}
		int maxSize = 0;
		
		
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
				recall = getRecallKV3(rapis, gapis);
				recall_sum = recall_sum + recall;
			}

			double hit_k= CrokageUtils.round((double) hitK / goldSet.size(),2);
			double mrr = CrokageUtils.round(rrank_sum / goldSet.size(),2);
			double map = CrokageUtils.round(preck_sum / goldSet.size(),2);
			double mr = CrokageUtils.round(recall_sum / goldSet.size(),2);
			
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
			
			kvalue = maxSize+ "";
			if(approach.contains("topAsymIdsMap") || approach.contains("topAsymIdsMap")) {
				kvalue+="(maxValue)";
			}
			
			System.out.println("Results for: "+approach+" "
					+" - Hit@" + kvalue + ": " + hit_k
					+" - MRR@" + kvalue + ": " + mrr
					+" - MAP@" + kvalue + ": " + map
					+" - MR@" + kvalue + ": " + mr
					+ "");
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in analyzeResultsV2 in query: "+currentQuery);
			throw e;
		}
		
	}




	protected void compareCrokageWithGoogleThreads() throws Exception {
		Map<String,Set<Integer>> googleQueriesAndSOIdsMap = new LinkedHashMap<>();
		Map<String, Set<Integer>> googleRecommendedResults = new LinkedHashMap<>();
		
		googleQueriesAndSOIdsMap = CrokageUtils.readCrawledQuestionsIds(GOOGLE_TOP_RESULTS_FOR_NLP2API);
		googleQueriesAndSOIdsMap.putAll(CrokageUtils.readCrawledQuestionsIds(GOOGLE_TOP_RESULTS_FOR_CROKAGE));
		
		//load recommended 
		runApproach();
		
		MetricResult metricResult = new MetricResult();
		metricResult.setApproach("crokage stage1 x google");
		analyzeResults(filteredAnswersWithApisIdsMap,googleQueriesAndSOIdsMap,metricResult, " google ");
		
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
				ArrayList<String> gapis = new ArrayList<>(goldSetQueriesApis.get(keyQuery));
				
				hitK = hitK + isApiFound_K(rapis, gapis);
				rrank_sum = rrank_sum + getRRank(rapis, gapis);
				double preck = 0;
				preck = getAvgPrecisionK(rapis, gapis);
				preck_sum = preck_sum + preck;
				double recall = 0;
				recall = getRecallK(rapis, gapis);
				recall_sum = recall_sum + recall;
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
	

}
