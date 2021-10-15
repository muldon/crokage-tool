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
import org.apache.lucene.search.similarities.BM25Similarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ufu.crokage.config.AppAux;
import com.ufu.crokage.exception.CrokageException;
import com.ufu.crokage.tfidf.Corpus;
import com.ufu.crokage.tfidf.Document;
import com.ufu.crokage.tfidf.VectorSpaceModel;
import com.ufu.crokage.to.Bucket;
import com.ufu.crokage.to.Post;
import com.ufu.crokage.to.Query;
import com.ufu.crokage.util.BotComposer;
import com.ufu.crokage.util.CrokageUtils;
import com.ufu.crokage.util.IDFCalc;
import com.ufu.crokage.util.IndexLucene;
import com.ufu.crokage.util.Matrix;
import com.ufu.crokage.util.SearcherParams;

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
				//+ "\n numberOfComposedAnswers: " + numberOfComposedAnswers
				+ "\n");
		
		long initTime1 = System.currentTimeMillis();
		
		SearcherParams searcherParams =  new SearcherParams("BM25Similarity", new BM25Similarity(),bm25_k,bm25_b);
		
		loadUpvotedAnswersIdsWithCodeContentsAndParentContents();
		luceneSearcherBM25.buildSearchManager(allBucketsWithUpvotesMap,searcherParams);
		
		
		//load all word vectors only once
		readSoContentWordVectorsForAllWords();
				
		//load the inverted index (api, postsSet)
		loadInvertedIndexFile();
		
		//filter by cutoff and sort in descending order
		reduceBigMapFileToMininumAPIsCount();
		
		//read idf vocabulary map (word, idf)
		crokageUtils.readIDFVocabulary(soIDFVocabularyMap);
		
		CrokageUtils.reportElapsedTime(initTime1,"loading models");

		
		System.out.println("Done initializing app.");
		crokageUtils.reportElapsedTime(initTime,action);

	}


	


	public List<Post> extractAnswers(Query query) throws Exception {
		
		int topk=query.getNumberOfComposedAnswers();
		
		Set<Integer> recommendedResults = runApproach(query);
		
		//int limit = 20; 
		//CrokageUtils.setLimitV2(recommendedResults, limit);
		
		List<Post> sortedBuckets =  processAnswers(recommendedResults,query);
		
		//String composition = CrokageUtils.composeAnswers(query.getQueryText(),sortedBuckets,numberOfComposedAnswers);
		
		return sortedBuckets;
	}

	
	private ArrayList<Post> processAnswers(Set<Integer> answersIds, Query query) {
		ArrayList<Post> posts = new ArrayList<>();
		int topk=query.getNumberOfComposedAnswers();
		
		if(query.getReduceSentences()==null || !query.getReduceSentences()) {
			for(Integer id:answersIds) {
				posts.add(crokageService.findPostById(id));
				if(posts.size()==topk) {
					break;
				}
			}
			return posts;
		}
		
		Iterator<Integer> it = answersIds.iterator();
		
//		boolean stop=false;
		
//		for(int i=0; i<answersIds.size(); i++) {
//			if((i+1) >=topk) {
//				stop = true;
//			}
//			
//			if(it.hasNext()) {
//				Post post = crokageService.findPostById(it.next());
//				
//				//paper implementation
//				//boolean isRelevant = crokageUtils.processSentences(post,query.getQueryText());
//				
//				//user study implementation
//				boolean isRelevant = !StringUtils.isBlank(post.getProcessedBody());
//				
//				if(!isRelevant) {
//					stop = false;
//					i--;
//					System.out.println("Not relevant, discarding... "+post.getId());
//				}else {
//					posts.add(post);
//				}
//			}
//			
//			if(stop) {
//				break;
//			}
//		}
		
		for(Integer id: answersIds) {
			
			Post post = crokageService.findPostById(id);
			
			//paper implementation
			//boolean isRelevant = crokageUtils.processSentences(post,query.getQueryText());
			
			//user study implementation
			boolean blankProcessedBody = StringUtils.isBlank(post.getProcessedBody());
			boolean blankProcessedCode = StringUtils.isBlank(post.getProcessedCode());
			
			if(blankProcessedBody || blankProcessedCode) {
				System.out.println("Not relevant, discarding... "+post.getId());
			}else {
				posts.add(post);
			}
			if(posts.size()==topk) {
				break;
			}
		}
		
		answersIds=null;
		
		return posts;
	}

	
	

	protected Set<Integer> runApproach(Query query) throws Exception {
		//long initTime = System.currentTimeMillis();
		
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
		Set<Integer> topKRelevantAnswersIds = null;
		
		luceneTopIdsMap.clear();
		
		int run = 0;
		int count=0;
		String obsTmp;
		String firstObs;
		
	
		int sum1=0;
		int sum2=0;
		
		processedQuery = crokageUtils.processQuery(rawQuery);
		//System.out.println("Processed query: "+processedQuery);
		if(StringUtils.isBlank(processedQuery)){
			throw new CrokageException("Query:" + rawQuery+ " poorly formulated, please reformulate it and try again.");
		}
		
		//default is using classes
		BotComposer.setSemWeight(semWeight);
	    BotComposer.setApiWeight(0d);   //not using class match
		BotComposer.setMethodFreqWeight(methodWeight); 
		BotComposer.setTfIdfWeight(tfIdfWeight);
		BotComposer.setBm25Weight(bm25Weight);
		
		//Stage 1: BM25
		luceneSmallSetIds = luceneSearcherBM25.search(processedQuery, bm25TopNSmallLimit,bm25ScoreAnswerIdMap);
		luceneTopIdsMap.put(rawQuery, luceneSmallSetIds);
		
		topKRelevantAnswersIds = getTopKRelevantBuckets(luceneSmallSetIds,processedQuery,contentTypeTFIDF,contentTypeSemanticSim);
		
		return topKRelevantAnswersIds;
	}



	

	protected Set<Integer> getTopKRelevantBuckets(Set<Integer> candidateAnswersIds, String processedQuery, Integer contentTypeTFIDF, Integer contentTypeSemanticSim) throws IOException {
		Set<Bucket> candidateBuckets = new HashSet<>();
		for(Integer answerId: candidateAnswersIds) {
			candidateBuckets.add(allBucketsWithUpvotesMap.get(answerId));
		}
		
		
		String comparingTitle;
		String parentTitle;
		String comparingContent;
		bucketsIdsScores.clear();
		double maxSimPair=0;
		double maxApiScore=0;
		double maxTfIdfScore=0;
		double maxBm25Score=0;
		methodsCounterMap.clear();
		classesCounterMap.clear();
		//topicsCounterMap.clear();
		documents.clear();
		Document queryDocument = new Document(processedQuery, 0);
		documents.add(queryDocument);
		topAnswerParentPairsAnswerIdScoreMap.clear();
		
		
		
		//TD-IDF vocabulary
		for(Bucket bucket:candidateBuckets) {
			comparingContent = loadBucketContent(bucket,contentTypeTFIDF);
			Document document = new Document(comparingContent, bucket.getId());
			documents.add(document);
			bucket.setDocument(document);
		}

		Corpus corpus = new Corpus(documents);
		VectorSpaceModel vectorSpace = new VectorSpaceModel(corpus);
		
		for(Bucket candidateBucket:candidateBuckets) {
			try {
				/*
				 * 1- TF-IDF
				 */
				double cosineSimScore = vectorSpace.cosineSimilarity(queryDocument, candidateBucket.getDocument());
				candidateBucket.setTfIdfCosineSimScore(cosineSimScore);
				if(cosineSimScore>maxTfIdfScore) {
					maxTfIdfScore=cosineSimScore;
				}
				
				
				/*
				 * 3- Asymmetric Relevance  
				 */
				comparingContent = loadBucketContent(candidateBucket,contentTypeSemanticSim);
				double simPair = getSimPair(comparingContent, processedQuery);
				if(simPair==0d) {
					System.out.println("Huston, we have a problem !!");
					throw new RuntimeException("Huston, we have a problem !!");
				}
				candidateBucket.setSimPair(simPair);
				if(simPair>maxSimPair) {
					maxSimPair=simPair;
				}
				
				
				/*
				 * 5- Method score
				 */
				if(BotComposer.getMethodFreqWeight()>0) {
					countMethods(candidateBucket);
				}
				
				
				
				
			} catch (Exception e) {
				System.out.println("error here... post: "+candidateBucket.getId());
				throw e;
			}
			
		}
		
		
		
		/*
		 * Methods score
		 */
		Map<String,Integer> topMethodsCounterMap = methodsCounterMap.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		/*
		 * Topics score
		 */
		/*Map<Integer,Integer> topTopicsCounterMap = topicsCounterMap.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	*/
		
		
		//normalization and other relevance boosts
		for(Bucket candidateBucket:candidateBuckets) {
				double simPair = candidateBucket.getSimPair();
				simPair = (simPair / maxSimPair); //normalization
				
				double tfIdfScore = candidateBucket.getTfIdfCosineSimScore();
				tfIdfScore = (tfIdfScore / maxTfIdfScore); //normalization
				
				
				//double methodFreqScore = BotComposer.calculateScoreForCommonMethods(bucket,methodsCounterMap,topFrequencyMethods);
				double methodFreqScore = 0;
				if(BotComposer.getMethodFreqWeight()>0) {
					methodFreqScore = BotComposer.calculateScoreForCommonMethods(candidateBucket.getCode(),topMethodsCounterMap);
				}
				
				//double finalScore = BotComposer.calculateRankingScore(simPair,methodFreqScore,apiAnswerPairScore,tfIdfScore,bm25Score,topicsScore);
				double finalScore = BotComposer.calculateRankingScore(simPair,methodFreqScore,0,tfIdfScore,0);
				bucketsIdsScores.put(candidateBucket.getId(), finalScore);
				
			
		}
		
		Map<Integer,Double> topSimilarAnswers = bucketsIdsScores.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       //.limit(topSimilarAnswersNumber)
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		
		corpus=null;
		vectorSpace=null;
		candidateBuckets=null;
		queryDocument=null;
		return topSimilarAnswers.keySet();
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


	private double getSimPair(String comparingContent, String processedQuery) {
		if(StringUtils.isBlank(comparingContent)) {
			return 0d;
		}
		
		/*if(processedQuery.contains("glassfish methodexecutor exception") && comparingContent.equals(anObject)) {
			
		}*/
		
		//get vectors for query words
		double[][] matrix1 = CrokageUtils.getMatrixVectorsForQuery(processedQuery,soContentWordVectorsMap);
		
		//get idfs for query
		double[][] idf1 = CrokageUtils.getIDFMatrixForQuery(processedQuery, soIDFVocabularyMap);
	
		//get vectors for query2 words
		double[][] matrix2 = CrokageUtils.getMatrixVectorsForQuery(comparingContent,soContentWordVectorsMap);
		
		//get idfs for query2
		double[][] idf2 = CrokageUtils.getIDFMatrixForQuery(comparingContent, soIDFVocabularyMap);
		
		double simPair = 0;
		
		try {
			//simPair = CrokageUtils.round5(Matrix.simDocPair(matrix1,matrix2,idf1,idf2));
			simPair = Matrix.simDocPair(matrix1,matrix2,idf1,idf2);
		} catch (Exception e) {
			System.out.println("error in getSimPair...");
		}
		
		
		return simPair;
	}

	private double getSimPair(String comparingContent, Bucket bucket, double[][] matrix1, double[][] idf1) {
		
		//get vectors for query2 words
		double[][] matrix2 = CrokageUtils.getMatrixVectorsForQuery(comparingContent,soContentWordVectorsMap);
		
		//get idfs for query2
		double[][] idf2 = CrokageUtils.getIDFMatrixForQuery(comparingContent, soIDFVocabularyMap);
		
		double simPair = CrokageUtils.round(Matrix.simDocPair(matrix1,matrix2,idf1,idf2),6);
		
		return simPair;
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


	protected void countMethods(Bucket bucket) {
		Set<String> methods = CrokageUtils.getMethodCalls(bucket.getCode(),"java");
		bucket.setMethods(methods);
		for(String method: methods) {
			if(method.equals(".println(")) {
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
		methods=null;
	}











}
