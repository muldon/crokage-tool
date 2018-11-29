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
import com.ufu.bot.config.AppAux;
import com.ufu.bot.googleSearch.GoogleWebSearch;
import com.ufu.bot.googleSearch.SearchQuery;
import com.ufu.bot.googleSearch.SearchResult;
import com.ufu.bot.service.CrokageService;
import com.ufu.bot.tfidf.Corpus;
import com.ufu.bot.tfidf.Document;
import com.ufu.bot.tfidf.TFIDFCalculator;
import com.ufu.bot.tfidf.VectorSpaceModel;
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


	protected void compareCrokageWithGoogleThreads() throws Exception {
		Map<String,Set<Integer>> googleQueriesAndSOIdsMap = new LinkedHashMap<>();
		Map<String, Set<Integer>> googleRecommendedResults = new LinkedHashMap<>();
		
		googleQueriesAndSOIdsMap = CrokageUtils.readCrawledQuestionsIds(GOOGLE_TOP_RESULTS_FOR_NLP2API);
		googleQueriesAndSOIdsMap.putAll(CrokageUtils.readCrawledQuestionsIds(GOOGLE_TOP_RESULTS_FOR_CROKAGE));
		
		//load recommended 
		runApproach();
		
		//Map<String, Set<Integer>> groundTruthSelectedQueriesQuestionsIdsMap = getGroundTruthSelectedQueriesQuestionsIdsMap();
		
		MetricResult metricResult = new MetricResult();
		metricResult.setApproach("crokage stage1 x google");
		analyzeResults(filteredAnswersWithApisIdsMap,googleQueriesAndSOIdsMap,metricResult, " google ");
		
	}




	private void extractAnswers() throws Exception {
		
		Map<String, Set<Integer>> recommendedResults = runApproach(); 
		System.out.println();
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
		Map<String, Set<Integer>> recommendedResults = new LinkedHashMap<>();
		
		//load input queries considering dataset
		queries = readInputQueries();
		//load ground truth answers
		loadGroundTruthSelectedQueries();
		//load ground truth threads
		getGroundTruthSelectedQueriesQuestionsIdsMap();
		
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
		
		if(iHaveALotOfMemory) { //load all word vectors only once
			readSoContentWordVectorsForAllWords();
			wordsAndVectorsLines=null;
		}else {
			//load so content word vectors to a list of strings representing posts (each line is a post)
			readSOContentWordAndVectorsLines();
			crokageUtils.readVectorsFromSOMapForWords(soContentWordVectorsMap,new HashSet(processedQueries),wordsAndVectorsLines);
		}
		
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
		
		/*
		List<Bucket> upvotedAnswersIdsWithCode = loadUpvotedAnswersIdsWithCodeContentsAndParentContents();
		long initTime = System.currentTimeMillis();
		Post parent;
		Post acceptedOrMostUpvotedAnswer=null;
		for(Bucket bucket:upvotedAnswersIdsWithCode) {
			parent = crokageService.findPostById(bucket.getParentId());
			if(parent.getAcceptedAnswerId()!=null) {
				acceptedOrMostUpvotedAnswer = crokageService.findPostById(parent.getAcceptedAnswerId());
			}else {
				acceptedOrMostUpvotedAnswer = crokageService.getMostUpvotedAnswerForQuestion(bucket.getParentId(),bucket.getId());
			}
			if(acceptedOrMostUpvotedAnswer!=null) {
				bucket.setAcceptedOrMostUpvotedAnswerOfParentProcessedBody(acceptedOrMostUpvotedAnswer.getProcessedBody());
				bucket.setAcceptedOrMostUpvotedAnswerOfParentProcessedCode(acceptedOrMostUpvotedAnswer.getProcessedCode());
			}
			
		}
		crokageUtils.reportElapsedTime(initTime,"acceptedOrMostUpvotedAnswer info added");*/
		
		//loadThreadsForUpvotedAnswersWithCodeIdsTitlesMap();
		
		//load <postId,Set<Apis>> map
		loadUpvotedPostsWithCodeApisMap();
		
		//combine state of art approaches considering the order in parameters
		Map<Integer, Set<String>> recommendedApis = getRecommendedApis();
		
		Set<Integer> keys = recommendedApis.keySet();
		if(keys.size()!=queries.size()) {
			throw new Exception("Queries size are different from the ones recommended by the API extractors. Consider extracting them again. ");
		}
		
		/*int bm25TopNResultsArr[] = {1000,2500,5000,10000,12500,15000,20000,25000,30000,35000};*/
		
		/*
		 * best parameters so far
		int bm25TopNBigLimitArr[]   = {5000};
		int bm25TopNSmallLimitArr[] = {100};
		double simWeights[] 		= {1};
		double classFreqWeights[]   = {0.25};
		double methodFreqWeights[]  = {0.75};
		double cosSimWeights[] 		= {0.5};
		double repWeights[] 		= {0.75};
		double upWeights[] 			= {0.5};
		int numberOfPostsInfoToMatchTFIDFArr[] = {3};
		int numberOfPostsInfoToMatchAsymmetricSimRelevanceArr[] = {2};
		int topSimilarContentAsymRelevanceNumberArr[] = {100};
		*/
		int bm25TopNBigLimitArr[]   = {5000};
		int bm25TopNSmallLimitArr[] = {100};
		double simWeights[] 		= {1};
		double classFreqWeights[]   = {0.25};
		double methodFreqWeights[]  = {0.75};
		double cosSimWeights[] 		= {0.5};
		double repWeights[] 		= {0.75};
		double upWeights[] 			= {0.5};
		int numberOfPostsInfoToMatchTFIDFArr[] = {3};
		int numberOfPostsInfoToMatchAsymmetricSimRelevanceArr[] = {2};
		int topSimilarContentAsymRelevanceNumberArr[] = {50};
		int numberOfAPIClassesArr[] = {20};
		int topkArr[] = {20,10,5,1};
		
		
		//int numberOfPostsInfoToMatchArr[] = {5};
		/*double simWeights[] = {1};
		double classFreqWeights[] = {0.25};
		double methodFreqWeights[] = {0.75};
		double repWeights[] = {0.75};
		double upWeights[] = {0.5};
		double cosSimWeights[] = {0.25};*/
		int count = 1;
		//boolean useCodeInSimCalculusArr[] = {true,false};
		String obsTmp;
		String firstObs;
		
		for(int numberOfPostsInfoToMatchTFIDF: numberOfPostsInfoToMatchTFIDFArr){
			for(int numberOfPostsInfoToMatchAsymmetricSimRelevance: numberOfPostsInfoToMatchAsymmetricSimRelevanceArr){
				for(int topSimilarContentsAsymRelevanceNumber: topSimilarContentAsymRelevanceNumberArr){
					for(int bm25TopNSmallLimit: bm25TopNSmallLimitArr) {
						for(int bm25TopNBigLimit: bm25TopNBigLimitArr) {
							for(double simWeight: simWeights) {
								for(double classFreqWeight: classFreqWeights) {
									for(double methodFreqWeight: methodFreqWeights) {
										for(double cosSimWeight: cosSimWeights) {
											for(double repWeight: repWeights) {
												for(double upWeight: upWeights) {
													for(int numberOfAPIClasses: numberOfAPIClassesArr) {
														
			
														
		
			
			int sum1=0;
			int sum2=0;
			int sum3=0;
			int sum4=0;
			BotComposer.setSimWeight(simWeight);
			BotComposer.setClassFreqWeight(classFreqWeight);
			BotComposer.setMethodFreqWeight(methodFreqWeight);
			BotComposer.setRepWeight(repWeight);
			BotComposer.setUpWeight(upWeight);
			BotComposer.setCosSimWeight(cosSimWeight);
			this.numberOfPostsInfoToMatchTFIDF=numberOfPostsInfoToMatchTFIDF;
			this.topSimilarContentsAsymRelevanceNumber=topSimilarContentsAsymRelevanceNumber;
			this.numberOfPostsInfoToMatchAsymmetricSimRelevance=numberOfPostsInfoToMatchAsymmetricSimRelevance;
			this.numberOfAPIClasses=numberOfAPIClasses;
			
			//this.luceneMoreThreadsNumber=luceneMoreThreadsNumber;
			
			//Integer topNScoredTitles = 10;
			initTime = System.currentTimeMillis();
			firstObs = " - numberOfPostsInfoToMatchTFIDF: "+numberOfPostsInfoToMatchTFIDF+ " - topSimilarContentsAsymRelevanceNumber: "+topSimilarContentsAsymRelevanceNumber+" - bm25TopNSmallLimit: "+bm25TopNSmallLimit+  " - bm25TopNBigLimit: "+bm25TopNBigLimit+  " - numberOfPostsInfoToMatchAsymmetricSimRelevance:"+numberOfPostsInfoToMatchAsymmetricSimRelevance;
			System.out.println("\n\nRun: "+count+firstObs );
			count++;
			for(Integer key: keys) {  //for each query 
				//long initTime = System.currentTimeMillis();
				//long initTime2 = System.currentTimeMillis();
				
				rawQuery = queries.get(key-1);
				//System.out.println("\n\nQuery: "+count+" "+rawQuery+ " - bm25TopNResults: "+bm25TopNResults+ " - topSimilarContentsAsymRelevanceNumber: "+topSimilarContentsAsymRelevanceNumber+ " - ");
				
				processedQuery = processedQueries.get(key-1);
				//System.out.println("Processed query: "+processedQuery);
				
				Set<String> topClasses = new LinkedHashSet<>(recommendedApis.get(key));
				crokageUtils.setLimitV2(topClasses, numberOfAPIClasses);
				if(rawQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("topClasses number: "+topClasses.size()+ " >> "+topClasses);
				}
				
				//get vectors for query words
				double[][] matrix1 = CrokageUtils.getMatrixVectorsForQuery(processedQuery,soContentWordVectorsMap);
				
				//get idfs for query
				double[][] idf1 = CrokageUtils.getIDFMatrixForQuery(processedQuery, soIDFVocabularyMap);
				//crokageUtils.reportElapsedTime(initTime2,"for query 2");
				
				//add vectors for all retrieved answers
				/*if(!iHaveALotOfMemory) {
					addVectorsToSoContentWordVectorsMap(answersWithTopFrequentAPIs);
				}	*/
				
				
				//first filter, lucene
				luceneSmallSetIds = luceneSearch(processedQuery,bm25TopNSmallLimit,bm25TopNBigLimit);
				luceneBigSetIds = luceneSearcherBM25.getBigSetAnswersIds();
				luceneTopIdsMap.put(rawQuery, luceneSmallSetIds);
				sum1+=luceneSmallSetIds.size();
				if(rawQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("Size of luceneSmallSetThreadsIds: "+luceneSmallSetIds.size());
				}
			
				//crokageUtils.reportElapsedTime(initTime2,"lucene");
				//initTime2 = System.currentTimeMillis();
				
				//asymTopAnswersThreadsIds = getTopRelevantThreads(matrix1,idf1,key,processedQuery,luceneBigSetIds);
				//asymTopAnswersThreadsIds = getAnswersForTopThreads(luceneBigSetIds,matrix1,idf1);
				
				asymTopAnswersThreadsIds = getAnswersForTopThreads(luceneBigSetIds,matrix1,idf1);
				topAsymIdsMap.put(rawQuery, asymTopAnswersThreadsIds);
				sum2+=asymTopAnswersThreadsIds.size();
				if(rawQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("Size of asymTopThreadsIds: "+asymTopAnswersThreadsIds.size());
				}
				
				
				Set<Integer> mergeIds=new HashSet<>(luceneSmallSetIds);
				mergeIds.addAll(asymTopAnswersThreadsIds);
				topMergeIdsMap.put(rawQuery, mergeIds);
				sum3+=mergeIds.size();
				if(rawQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("Size of mergeIds: "+mergeIds.size());
				}
				
				
				filteredAnswersWithAPIs = filterAnswersByAPIs(topClasses,mergeIds);
				filteredAnswersWithApisIdsMap.put(rawQuery, filteredAnswersWithAPIs);
				if(rawQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("Size of filteredAnswersWithAPIs: "+filteredAnswersWithAPIs.size());
				}
				sum4+=filteredAnswersWithAPIs.size();
				
				//mergeThreads(topMergeIdsMap,luceneTopIdsMap,topAsymIdsMap);
				
				
				/*
				//third filter, threads
				topAnswersForThreadsIds = getAnswersForTopThreads(answersWithTopFrequentAPIs,matrix1,idf1);
				//System.out.println("Size of topAnswersForThreadsIds: "+topAnswersForThreadsIds.size());
				topThreadsAnswersIdsMap.put(rawQuery, topAnswersForThreadsIds);
				sum3+=topAnswersForThreadsIds.size();
				//crokageUtils.reportElapsedTime(initTime2,"topAnswersForThreadsIds");
				//initTime2 = System.currentTimeMillis();
				*/
				//last filter: relevance similarity
				Set<Integer> topKRelevantAnswersIds = getTopKRelevantAnswers(filteredAnswersWithAPIs,matrix1,idf1,key,processedQuery);
				//System.out.println("Size of topKRelevantAnswersIds: "+topKRelevantAnswersIds.size());
				recommendedResults.put(rawQuery, topKRelevantAnswersIds);
				//crokageUtils.reportElapsedTime(initTime2,"topKRelevantAnswersIds");
				topClasses=null;
				mergeIds=null;
				//crokageUtils.reportElapsedTime(initTime,"for query");
				
			}
			
			System.out.println("\n");
			String obsComp = obs + " - "+firstObs+ " luceneTopIdsMap (avg): "+sum1/keys.size();
			MetricResult metricResult = new MetricResult("luceneTopIdsMap",bm25TopNSmallLimit,bm25TopNBigLimit,topApisScoredPairsPercent,topSimilarContentsAsymRelevanceNumber,cutoff,bm25TopNSmallLimit,obsComp,numberOfAPIClasses);
			analyzeResults(luceneTopIdsMap,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"luceneTopIdsMap");
			crokageService.saveMetricResult(metricResult);
			//System.out.println(obsComp);
			
			//System.out.println("\n");
			/*obsComp = obs + " - "+firstObs+ " topAsymIdsMap (avg): "+sum2/keys.size();
			metricResult = new MetricResult("topAsymIdsMap",bm25TopNSmallLimit,bm25TopNBigLimit,topApisScoredPairsPercent,topSimilarContentsAsymRelevanceNumber,cutoff,null,obsComp,numberOfAPIClasses);
			analyzeResults(topAsymIdsMap,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"topAsymIdsMap");
			crokageService.saveMetricResult(metricResult);*/
			
			
			
			//System.out.println("\n");
			obsComp = obs + " - "+firstObs+ " topMergeIdsMap (avg): "+sum3/keys.size();
			metricResult = new MetricResult("topMergeIdsMap",bm25TopNSmallLimit,bm25TopNBigLimit,topApisScoredPairsPercent,topSimilarContentsAsymRelevanceNumber,cutoff,null,obsComp,numberOfAPIClasses);
			analyzeResults(topMergeIdsMap,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"topMergeIdsMap");
			crokageService.saveMetricResult(metricResult);
			//System.out.println(obsComp);
			
			
			obsComp = obs + " filteredAnswersWithApisIdsMap filter (avg): "+sum4/keys.size();
			metricResult = new MetricResult("filteredAnswersWithApisIdsMap",bm25TopNSmallLimit,bm25TopNBigLimit,topApisScoredPairsPercent,topSimilarContentsAsymRelevanceNumber,cutoff,null,obsComp,numberOfAPIClasses);
			analyzeResults(filteredAnswersWithApisIdsMap,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"filteredAnswersWithApisIdsMap");
			//System.out.println(obsComp);
			//crokageService.saveMetricResult(metricResult,useCodeInSimCalculus,obsComp,bm25TopNResults,cutoff,numberOfAPIClasses,topApisScoredPairsPercent);
			
			
			/*
			//System.out.println("\n");
			obsComp = obs + " topThreadsAnswersIdsMap filter (avg): "+sum3/keys.size();
			metricResult = new MetricResult("topThreadsAnswersIdsMap",bm25TopNResults,topApisScoredPairsPercent,topSimilarContentsAsymRelevanceNumber,cutoff,bm25TopNResults,obsComp);
			analyzeResults(topThreadsAnswersIdsMap,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"topThreadsAnswersIdsMap");
			//System.out.println(obsComp);
			//crokageService.saveMetricResult(metricResult,useCodeInSimCalculus,obsComp,bm25TopNResults,cutoff,numberOfAPIClasses,topApisScoredPairsPercent);
			*/
			//System.out.println("\n");
			//System.out.println("\n");
			
			for(int topk: topkArr) {
				Map<String, Set<Integer>> recommendedResultsTmp = new LinkedHashMap<>(recommendedResults);
				obsComp = firstObs+ " - final relevance. Size: "+topk;
				metricResult = new MetricResult("final relevance",bm25TopNSmallLimit,bm25TopNBigLimit,topApisScoredPairsPercent,topSimilarContentsAsymRelevanceNumber,cutoff,topk,obsComp,numberOfAPIClasses);
				analyzeResults(recommendedResultsTmp,groundTruthSelectedQueriesAnswersIdsMap,metricResult,"final relevance");
				//System.out.println(obsComp);
				crokageService.saveMetricResult(metricResult);
				recommendedResultsTmp=null;
			}
			
			CrokageUtils.reportElapsedTime(initTime,"for run");
			
		}
		
		}}}}}}}}}}}
		
		return recommendedResults;
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
	
	
	




	protected Set<Integer> getTopRelevantThreads(double[][] matrix1, double[][] idf1, Integer key, String processedQuery, Set<Integer> luceneBigSetThreadsIds) {
		bucketsIdsScores.clear();
		
		String title=null;
		for(Integer threadId: luceneBigSetThreadsIds) {
			title=threadsForUpvotedAnswersWithCodeIdsTitlesMap.get(threadId);
			if(StringUtils.isBlank(title)) {
				continue;				
			}
			
			//get vectors for query2 words
			double[][] matrix2 = CrokageUtils.getMatrixVectorsForQuery(title,soContentWordVectorsMap);
			
			//get idfs for query2
			double[][] idf2 = CrokageUtils.getIDFMatrixForQuery(title, soIDFVocabularyMap);
			
			double simPair = CrokageUtils.round(Matrix.simDocPair(matrix1,matrix2,idf1,idf2),6);
			
			bucketsIdsScores.put(threadId, simPair);
			
		}
				
		//int topSimilarTitles = bucketsIdsScores.size()*topSimilarContentsAsymRelevanceNumber/100;
		
		//sort scores in descending order and consider the first topSimilarQuestionsNumber parameter
		Map<Integer,Double> topKRelevantThreadsMap = bucketsIdsScores.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       .limit(topSimilarContentsAsymRelevanceNumber)
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		
		Set<Integer> topKRelevantThreadsIds = topKRelevantThreadsMap.keySet();
		
		
		return topKRelevantThreadsIds;
	
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

	


/*

	protected Set<Integer> getCandidateQuestionsFromTopApisOld(Set<String> topClasses) throws Exception {
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
	protected Set<Integer> getCandidateAnswersIdsOld(Set<Integer> topKRelevantQuestionsIds) {
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
		
		//initTime = System.currentTimeMillis();
		//System.out.println("Building corpus for tf-idf...");
		Corpus corpus = new Corpus(documents);
		VectorSpaceModel vectorSpace = new VectorSpaceModel(corpus);
		//crokageUtils.reportElapsedTime(initTime,"corpus building");
		//initTime = System.currentTimeMillis();
		
		
		for(Bucket bucket:candidateBuckets) {
			//second ranking
			try {
				
				//comparingContent = loadBucketContent(bucket);
				
				double tfIdfCosineSimScore = vectorSpace.cosineSimilarity(queryDocument, bucket.getDocument());
				bucket.setTfIdfCosineSimScore(tfIdfCosineSimScore);
				
				if(tfIdfCosineSimScore>maxCosSimScore) {
					maxCosSimScore=tfIdfCosineSimScore;
				}
				
				/*//parentTitle =  allQuestionsIdsTitlesMap.get(bucket.getParentId());
				comparingContent = loadBucketContent(bucket);		
				
				//get vectors for query2 words
				double[][] matrix2 = CrokageUtils.getMatrixVectorsForQuery(comparingContent,soContentWordVectorsMap);
				
				//get idfs for query2
				double[][] idf2 = CrokageUtils.getIDFMatrixForQuery(comparingContent, soIDFVocabularyMap);
				
				double simPair = CrokageUtils.round(Matrix.simDocPair(matrix1,matrix2,idf1,idf2),6);*/
				
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
			
				
				//countClasses(bucket.getCode()); //use recommended to score
				countMethods(bucket.getCode(),bucket);
				
				
				
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
				double simPair = bucket.getSimPair();
				simPair = (simPair / maxSimPair); //normalization
				
				double tfIdfCosineSimScore = bucket.getTfIdfCosineSimScore();
				tfIdfCosineSimScore = (tfIdfCosineSimScore / maxCosSimScore); //normalization
				//double tfIdfCosineSimScore = 0;
				
				double apiAnswerPairScore=0;
				if(!topAnswerParentPairsAnswerIdScoreMap.isEmpty() && topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId())!=null) {
					apiAnswerPairScore = topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId());
					apiAnswerPairScore = (apiAnswerPairScore/maxApiScore); //normalization
				}
				
				//double apiAnswerPairScore=0;
				
				double finalScore = BotComposer.calculateFinalScore(simPair,bucket,methodsCounterMap,apiAnswerPairScore,tfIdfCosineSimScore);
				
				
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
				/*
				
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
				
				*/
				
				
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
		corpus=null;
		vectorSpace=null;
		candidateBuckets=null;
		queryDocument=null;
		return candidateAnswersIds;
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




	protected void countClasses(String code) {
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

	protected void countMethods(String code,Bucket bucket) {
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



/*
	protected void loadAllAnswersIdsParentIds() {
		long initTime = System.currentTimeMillis();
		allAnswersWithUpvotesIdsParentIdsMap = crokageService.getAnswersIdsParentIds();
		crokageUtils.reportElapsedTime(initTime,"loadAllAnswersIdsParentIds");
	}
*/




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
		
		/*if(approach.contains("topMergeIdsMap")) {
			System.out.println("here");
		}*/
		
		Integer topk = metricResult.getTopk();
		String kvalue="";
		if(topk!=null && topk>0) {
			crokageUtils.reduceSetV2(recommended, topk);
		}else if(topk==null) {
			topk=1; //save in top1 
		}
		//Integer topk = metricResult.getTopk();
		//System.out.println("analyzeResults - considering topk= "+topk);
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

			double hit_k= CrokageUtils.round((double) hitK / goldSet.size(),2);
			double mrr = CrokageUtils.round(rrank_sum / goldSet.size(),2);
			double map = CrokageUtils.round(preck_sum / goldSet.size(),2);
			double mr = CrokageUtils.round(recall_sum / goldSet.size(),2);
			
			//metricResult.setTopk(maxSize);
			
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
	

}
