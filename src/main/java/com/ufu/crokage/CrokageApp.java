package com.ufu.crokage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ufu.crokage.config.AppAux;
import com.ufu.crokage.tfidf.Corpus;
import com.ufu.crokage.tfidf.Document;
import com.ufu.crokage.tfidf.VectorSpaceModel;
import com.ufu.crokage.to.AnswerParentPair;
import com.ufu.crokage.to.Bucket;
import com.ufu.crokage.to.Post;
import com.ufu.crokage.to.Query;
import com.ufu.crokage.util.BotComposer;
import com.ufu.crokage.util.CrokageUtils;
import com.ufu.crokage.util.IDFCalc;
import com.ufu.crokage.util.IndexLucene;
import com.ufu.crokage.util.Matrix;

@Component
@SuppressWarnings("unused")
public class CrokageApp extends AppAux{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@PostConstruct
	public void init() throws Exception {
		long initTime = System.currentTimeMillis();
		System.out.println("Initializing CrokageApp app...");
		initializeVariables();
		action = StringUtils.trim(action);
		
		System.out.println("\nConsidering parameters: \n" 
				+ "\n CROKAGE_HOME: " + CROKAGE_HOME
				+ "\n TMP_DIR: " + TMP_DIR
				+ "\n useExtractors: " + useExtractors 
				+ "\n numberOfComposedAnswers: " + numberOfComposedAnswers
				+ "\n");
		
		long initTime1 = System.currentTimeMillis();
		
		loadUpvotedAnswersIdsWithCodeContentsAndParentContents();
		luceneSearcherBM25.buildSearchManager(allAnswersWithUpvotesAndCodeBucketsMap);
		allThreadsIdsContentsMap=null;
		
		//load all word vectors only once
		readSoContentWordVectorsForAllWords();
				
		//load the inverted index (api, postsSet)
		loadInvertedIndexFile();
		
		//filter by cutoff and sort in descending order
		reduceBigMapFileToMininumAPIsCount();
		
		//read idf vocabulary map (word, idf)
		crokageUtils.readIDFVocabulary(soIDFVocabularyMap);
		
		resetAPIExtractors();
		
		//loadThreadsForUpvotedAnswersWithCodeIdsTitlesMap();
		loadUpvotedPostsWithCodeApisMap();
				
		CrokageUtils.reportElapsedTime(initTime1,"loading models");

		
		System.out.println("Done initializing app.");
		crokageUtils.reportElapsedTime(initTime,action);

	}


	


	public List<Post> extractAnswers(Query query) throws Exception {
		
		int topk=10;
		
		boolean override = false;
		if(query.getUseExtractors()!=null) {
			useExtractors=query.getUseExtractors();
			override=true;
		}
		if(query.getNumberOfComposedAnswers()!=null) {
			numberOfComposedAnswers=query.getNumberOfComposedAnswers();
			override=true;
		}
		
		if(override) {
			System.out.println("Overriding parameters: \nuseExtractors="+useExtractors+"\nnumberOfComposedAnswers="+numberOfComposedAnswers);
		}
		
		
		Set<Integer> recommendedResults = runApproach(query);
		
		CrokageUtils.setLimit(recommendedResults, topk);
		
		ArrayList<Post> sortedBuckets =  processAnswers(recommendedResults,query.getQueryText());
		
		//String composition = CrokageUtils.composeAnswers(query.getQueryText(),sortedBuckets,numberOfComposedAnswers);
		
		return sortedBuckets;
	}

	
	private ArrayList<Post> processAnswers(Set<Integer> answersIds, String query) {
		Iterator<Integer> it = answersIds.iterator();
		ArrayList<Post> posts = new ArrayList<>();
		boolean stop=false;
		
		for(int i=0; i<answersIds.size(); i++) {
			if((i+1) >=numberOfComposedAnswers) {
				stop = true;
			}
			
			if(it.hasNext()) {
				Post post = crokageService.findPostById(it.next());
				boolean isRelevant = crokageUtils.processSentences(post,query);
				if(!isRelevant) {
					stop = false;
					i--;
				}else {
					posts.add(post);
				}
			}
			
			if(stop) {
				break;
			}
		}
		
		answersIds=null;
		
		return posts;
	}

	
	

	protected Set<Integer> runApproach(Query query) throws Exception {
		long initTime = System.currentTimeMillis();
		
		String rawQuery = query.getQueryText();
		String processedQuery;
		Set<String> topClasses=null;
		Set<Integer> answersWithTopFrequentAPIs=null;
		Set<Integer> filteredAnswersWithAPIs=null;
		Set<Integer> luceneSmallSetIds=null;
		//Set<Integer> luceneBigSetIds=null;
		Set<Integer> asymTopAnswersThreadsIds=null;
		Set<Integer> topAnswersForThreadsIds=null;
		Set<Integer> topKRelevantQuestionsIds=null;
		Set<Integer> tfIdfTopAnswersIds=null;
		Set<Integer> recommendedResults = new LinkedHashSet<>();
		
		int run = 0;
		int count=0;
		String obsTmp;
		String firstObs;
		
	
		int sum1=0;
		int sum2=0;
		
		processedQuery = crokageUtils.processQuery(rawQuery);
		System.out.println("Processed query: "+processedQuery);
		
		if(!useExtractors) {
			//not using classes, weights are different
			topSimilarContentsAsymRelevanceNumber=50;
			apiWeight=0d;
		}
		
		
		//default is using classes
		BotComposer.setSemWeight(semWeight);
	    BotComposer.setApiWeight(apiWeight); 
		BotComposer.setMethodFreqWeight(methodWeight); 
		BotComposer.setTfIdfWeight(tfIdfWeight);
		BotComposer.setBm25Weight(bm25Weight);
		
		//Stage 1: BM25
		luceneSmallSetIds = luceneSearcherBM25.search(processedQuery, bm25TopNSmallLimit,bm25ScoreAnswerIdMap);
		luceneTopIdsMap.put(rawQuery, luceneSmallSetIds);
		sum1+=luceneSmallSetIds.size();
	
		//Stage 2: Asymmetric Relevance
		asymTopAnswersThreadsIds = scoreAnswersBySemantics(luceneSmallSetIds,processedQuery);
		topAsymIdsMap.put(rawQuery, asymTopAnswersThreadsIds);
		sum2+=asymTopAnswersThreadsIds.size();
		
		//merge
		Set<Integer> mergeIds=new HashSet<>(luceneSmallSetIds);
		mergeIds.addAll(asymTopAnswersThreadsIds);
		topMergeIdsMap1.put(rawQuery, mergeIds);
		
		//Stage 3: previously provided (getRecommendedApis();)
		
		//Stage 4: Score answers by API
		if(useExtractors) {
			topClasses = getApisFromExtractors(rawQuery);
			crokageUtils.setLimitV2(topClasses, numberOfAPIClasses);
			scoreAnswersByAPIs(topClasses,luceneSmallSetIds);
		}
			
		//Stage 5: Relevance calculation
		Set<Integer> topKRelevantAnswersIds = getTopKRelevantAnswers(mergeIds,processedQuery);
		
		recommendedResults.addAll(topKRelevantAnswersIds);
		topClasses=null;
		mergeIds=null;
		
		CrokageUtils.reportElapsedTime(initTime,"for running query");
		
		return recommendedResults;
	}



	



	private Set<String> getApisFromExtractors(String rawQuery) {
		Set<String> rackApis = getApisFromRack(rawQuery);
		Set<String> nlpApis = getApisFromNlp(rawQuery);
	
		nlpApis.addAll(rackApis);
		
		return nlpApis;
	}





	private Set<String> getApisFromNlp(String rawQuery) {
		Set<String> nlp = null;
		try {
			String jarPath = CROKAGE_HOME;
			//String command = "java -jar "+jarPath+ "/myNlp2Api.jar "+ "-K 10 -task reformulate -query How do I send an HTML email?";
			List<String> command = new ArrayList<String>();
		    
		    command.add("java");
		    command.add("-jar");
		    command.add(jarPath+"/myNlp2Api.jar");
		    command.add("-K");
		    command.add("10");
		    command.add("-task");
		    command.add("reformulate");
		    command.add("-query");
		    command.add(rawQuery);
			
			ProcessBuilder pb = new ProcessBuilder(command);
			Process p = pb.start();
			p.waitFor();
			String output = CrokageUtils.loadStream(p.getInputStream());
			String error = CrokageUtils.loadStream(p.getErrorStream());
			int rc = p.waitFor();
			String apis[] = output.split("\n");
			apis = ArrayUtils.remove(apis, 0);
			apis = ArrayUtils.remove(apis, 0);
			apis = ArrayUtils.remove(apis, 0);
			apis = ArrayUtils.remove(apis, 0);
			if(StringUtils.isBlank(apis[0])) {
				apis = ArrayUtils.remove(apis, 0);
			}
			apis = apis[0].split(" ");
			nlp = new LinkedHashSet<String>(Arrays.asList(apis));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return nlp;
	}





	private Set<String> getApisFromRack(String rawQuery) {
		Set<String> rackApis = null;
		try {
			
			String jarPath = CROKAGE_HOME;
			List<String> command = new ArrayList<String>();
		    
		    command.add("java");
		    command.add("-jar");
		    command.add(jarPath+"/rack-exec.jar");
		    command.add("-K");
		    command.add("10");
		    command.add("-task");
		    command.add("suggestAPI");
		    command.add("-query");
		    command.add(rawQuery);
			
			ProcessBuilder pb = new ProcessBuilder(command);
			Process p = pb.start();
			p.waitFor();
			String output = CrokageUtils.loadStream(p.getInputStream());
			String error = CrokageUtils.loadStream(p.getErrorStream());
			int rc = p.waitFor();
			String apis[] = output.split("\n");
			apis = ArrayUtils.remove(apis, 0);
			rackApis = new LinkedHashSet<String>(Arrays.asList(apis));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rackApis;
	}




	private double getSimPair(String comparingContent, Bucket bucket, double[][] matrix1, double[][] idf1) {
		
		//get vectors for query2 words
		double[][] matrix2 = CrokageUtils.getMatrixVectorsForQuery(comparingContent,soContentWordVectorsMap);
		
		//get idfs for query2
		double[][] idf2 = CrokageUtils.getIDFMatrixForQuery(comparingContent, soIDFVocabularyMap);
		
		double simPair = CrokageUtils.round(Matrix.simDocPair(matrix1,matrix2,idf1,idf2),6);
		
		return simPair;
	}


	protected Set<Integer> scoreAnswersBySemantics(Set<Integer> luceneSmallSetIds, String processedQuery) {
		Set<Bucket> luceneBigSetBuckets = new HashSet<>();
		Set<Bucket> luceneSmallSetBuckets = new HashSet<>();
		
		//get vectors for query words
		double[][] matrix1 = CrokageUtils.getMatrixVectorsForQuery(processedQuery,soContentWordVectorsMap);
		
		//get idfs for query
		double[][] idf1 = CrokageUtils.getIDFMatrixForQuery(processedQuery, soIDFVocabularyMap);
		
		
		/*
		for(Integer answerId: luceneBigSetIds) {
			luceneBigSetBuckets.add(allAnswersWithUpvotesAndCodeBucketsMap.get(answerId));
		}*/
		
		
		for(Integer answerId: luceneSmallSetIds) {
			luceneSmallSetBuckets.add(allAnswersWithUpvotesAndCodeBucketsMap.get(answerId));
		}
		
		//bucketsIdsBigSetScores.clear();
		bucketsIdsSmallSetScores.clear();
		String comparingContent; 
		
		/*for(Bucket bucket:luceneBigSetBuckets) {
			comparingContent = loadBucketContent(bucket,numberOfPostsInfoToMatchAsymmetricSimRelevance);
			if(StringUtils.isBlank(comparingContent)) {
				continue;
			}
			double simPair = getSimPair(comparingContent, bucket, matrix1,idf1);
			
			bucketsIdsBigSetScores.put(bucket.getId(), simPair);
			
		}*/
		
		for(Bucket bucket:luceneSmallSetBuckets) {
			comparingContent = loadBucketContent(bucket,numberOfPostsInfoToMatchAsymmetricSimRelevance);
			if(StringUtils.isBlank(comparingContent)) {
				continue;
			}
			double simPair = getSimPair(comparingContent, bucket, matrix1,idf1);
			
			bucketsIdsSmallSetScores.put(bucket.getId(), simPair);
			
		}
				
		//int topSimilarContents = bucketsIdsBigSetScores.size()*topSimilarContentsAsymRelevanceNumber/100;
		
		//sort scores in descending order and consider the first topSimilarQuestionsNumber parameter
		/*Map<Integer,Double> topKRelevantAnswersBucketsMap = bucketsIdsBigSetScores.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       .limit(topSimilarContentsAsymRelevanceNumber)
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		*/
		Map<Integer,Double> topKRelevantAnswersBucketsMap = bucketsIdsSmallSetScores.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       .limit(topSimilarContentsAsymRelevanceNumber)
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		
		luceneSmallSetBuckets=null;
		luceneBigSetBuckets=null;
		
		
		Set<Integer> topKRelevantAnswersIds = topKRelevantAnswersBucketsMap.keySet();
		//Set<Integer> topKRelevantAnswersIds = bucketsIdsSmallSetScores.keySet();
		
		return topKRelevantAnswersIds;
		
		
	}
	
	
	
	

	
	private Set<Integer> scoreAnswersByAPIs(Set<String> topClasses, Set<Integer> relevantAnswersIds) {
		Set<AnswerParentPair> answerParentThreads = new HashSet<>(); 
		List<Integer> topNAnswersIds = new ArrayList<>();
		
		for(String topClass:topClasses) {
			Set<Integer> answersIdsFromBigMap = filteredSortedMapAnswersIds.get(topClass);
			if(answersIdsFromBigMap==null) {
				continue;
			}	
			
			
			for(Integer relevantAnswerId: relevantAnswersIds) {
				if(answersIdsFromBigMap.contains(relevantAnswerId) && allAnswersWithUpvotesAndCodeBucketsMap.containsKey(relevantAnswerId)) { //filtered by lucene and is upvoted answer 
					AnswerParentPair answerParentPair = new AnswerParentPair(relevantAnswerId, allAnswersWithUpvotesAndCodeBucketsMap.get(relevantAnswerId).getParentId());
					calculateApiScore(answerParentPair,topClasses);
					answerParentThreads.add(answerParentPair);
					topAnswerParentPairsAnswerIdScoreMap.put(relevantAnswerId, answerParentPair.getApiScore());
				}
			}
		}
		
		//System.out.println("Number of pairs(answer+parent) containing topClasses: "+answerParentThreads.size());
		
		
		topNAnswersIds=null;
		answerParentThreads=null;
		
		return relevantAnswersIds;
	}




	protected void buildLuceneIndex() {
		long start = System.currentTimeMillis();
		IndexLucene indexer = new IndexLucene(SO_DIRECTORY_INDEX, SO_DIRECTORY_FILES);
		indexer.indexCorpusFiles();
		crokageUtils.reportElapsedTime(start, "buildLuceneIndex");
		
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
		System.out.println("Size of big map before: "+bigMapApisAnswersIds.size()+ " -after cutoff: "+filteredSortedMapAnswersIds.size());
		//generate reduced map
		//CrokageUtils.printMapInfosIntoCVSFile(filteredSortedMapAnswersIds,REDUCED_MAP_INVERTED_INDEX_APIS_FILE_PATH);
		crokageUtils.reportElapsedTime(initTime,"reduceBigMapFileToMininumAPIsCount");
	}




	protected void resetAPIExtractors() {
		rackQueriesApisMap.clear();
		bikerQueriesApisClassesMap.clear();
		nlp2ApiQueriesApisMap.clear();
	}



	
	
	protected Set<Integer> getTopKRelevantAnswers(Set<Integer> candidateAnswersIds, String processedQuery) throws IOException {
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
		double maxTfIdfScore=0;
		double maxBm25Score=0;
		methodsCounterMap.clear();
		classesCounterMap.clear();
		documents.clear();
		Document queryDocument = new Document(processedQuery, 0);
		documents.add(queryDocument);
		
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
				
				double cosineSimScore = vectorSpace.cosineSimilarity(queryDocument, bucket.getDocument());
				bucket.setTfIdfCosineSimScore(cosineSimScore);
				if(cosineSimScore>maxTfIdfScore) {
					maxTfIdfScore=cosineSimScore;
				}
				
				double bm25Score = bm25ScoreAnswerIdMap.get(bucket.getId());
				bucket.setBm25Score(bm25Score);
				if(bm25Score>maxBm25Score) {
					maxBm25Score=bm25Score;
				}
				
				
				double simPair= bucketsIdsSmallSetScores.get(bucket.getId());
				/*if(bucketsIdsBigSetScores.containsKey(bucket.getId())) {
					simPair = bucketsIdsBigSetScores.get(bucket.getId());
				}else {
					simPair = bucketsIdsSmallSetScores.get(bucket.getId());
				}*/
				if(simPair==0d) {
					System.out.println("Huston, we have a problem !!");
				}
				
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
		
		
		
		//normalization and other relevance boosts
		for(Bucket bucket:candidateBuckets) {
				double simPair = bucket.getSimPair();
				simPair = (simPair / maxSimPair); //normalization
				
				double tfIdfScore = bucket.getTfIdfCosineSimScore();
				tfIdfScore = (tfIdfScore / maxTfIdfScore); //normalization
				
				double bm25Score = bucket.getTfIdfCosineSimScore();
				bm25Score = (bm25Score / maxBm25Score); //normalization
	
				double apiAnswerPairScore=0;
				if(!topAnswerParentPairsAnswerIdScoreMap.isEmpty() && topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId())!=null) {
					apiAnswerPairScore = topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId());
					apiAnswerPairScore = (apiAnswerPairScore/maxApiScore); //normalization
				}
				
				double finalScore = BotComposer.calculateRankingScore(simPair,bucket,methodsCounterMap,apiAnswerPairScore,tfIdfScore,bm25Score);
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


	

	protected void sortMethodsByFrequency() {
		Map<String,Integer> topMethodsCounterMap = methodsCounterMap.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		
		/*System.out.println("Top methods: ");
		int i=0;
		for(String method:topMethodsCounterMap.keySet()) {
			System.out.println(method+" :"+topMethodsCounterMap.get(method));
			i++;
			if(i==10) {
				break;
			}
		}*/
			
		
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




	protected int getApisForApproaches() throws Exception {
		System.out.println("loading queries from approaches...");
		int numApproaches = 0;
		long initTime = System.currentTimeMillis();
		
		//load ground truth answers
		if(!dataSet.equals("selectedqueries-test")){
			loadGroundTruthSelectedQueries();
		}
		
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








}
