package com.ufu.crokage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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
import com.ufu.crokage.to.MetricResult;
import com.ufu.crokage.to.Post;
import com.ufu.crokage.to.Query;
import com.ufu.crokage.to.UserEvaluation;
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
		
		loadModels();

		switch (action) {
		
		
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
			
		
		
		case "checkConditions":
			checkConditions();
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
			
	
		
		default:
			break;
		}
		
		System.out.println("Done running task.");
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

	
	private void loadModels() throws Exception {
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
	}


	protected Set<Integer> runApproach(Query query) throws Exception {
		long initTime = System.currentTimeMillis();
		
		String rawQuery = query.getQueryText();
		String processedQuery;
		Set<String> topClasses=null;
		Set<Integer> answersWithTopFrequentAPIs=null;
		Set<Integer> filteredAnswersWithAPIs=null;
		Set<Integer> luceneSmallSetIds=null;
		Set<Integer> luceneBigSetIds=null;
		Set<Integer> asymTopAnswersThreadsIds=null;
		Set<Integer> topAnswersForThreadsIds=null;
		Set<Integer> topKRelevantQuestionsIds=null;
		Set<Integer> tfIdfTopAnswersIds=null;
		Set<Integer> recommendedResults = new LinkedHashSet<>();
		
		/*for(String query: queries) {
			processedQueries.add(crokageUtils.processQuery(query));
		}*/
		
		
		//load apis considering approaches
		//getApisForApproaches();
		
		
		//combine state of art approaches considering the order in parameters
		/*Map<Integer, Set<String>> recommendedApis = getRecommendedApis();
		
		Set<Integer> keys = recommendedApis.keySet();
		if(keys.size()!=queries.size()) {
			throw new Exception("Queries size are different from the ones recommended by the API extractors. Consider extracting them again. ");
		}*/
				
		int run = 0;
		int count=0;
		String obsTmp;
		String firstObs;
		
	
		BotComposer.setSemWeight(semWeight);
	    BotComposer.setApiWeight(apiWeight); //must be set to 0 if API extractors are not used
		BotComposer.setMethodFreqWeight(methodWeight); 
		BotComposer.setCosSimWeight(cosSimWeight);
		
		int sum1=0;
		int sum2=0;
		
		processedQuery = crokageUtils.processQuery(rawQuery);
		System.out.println("Processed query: "+processedQuery);
		
		if(useExtractors) {
			//topClasses = new LinkedHashSet<>(recommendedApis.get(key));
			
			topClasses = getApisFromExtractors(rawQuery);
			crokageUtils.setLimitV2(topClasses, numberOfAPIClasses);
		}
		
		
		
		//Stage 1: BM25
 		luceneSmallSetIds = luceneSearch(processedQuery,bm25TopNSmallLimit,bm25TopNBigLimit);
		luceneBigSetIds = luceneSearcherBM25.getBigSetAnswersIds();
		luceneTopIdsMap.put(rawQuery, luceneSmallSetIds);
		sum1+=luceneSmallSetIds.size();
	
		//Stage 2: Asymmetric Relevance
		asymTopAnswersThreadsIds = getAnswersForTopThreads(luceneBigSetIds,processedQuery);
		topAsymIdsMap.put(rawQuery, asymTopAnswersThreadsIds);
		sum2+=asymTopAnswersThreadsIds.size();
		
		//merge
		Set<Integer> mergeIds=new HashSet<>(luceneSmallSetIds);
		mergeIds.addAll(asymTopAnswersThreadsIds);
		topMergeIdsMap1.put(rawQuery, mergeIds);
		
		//Stage 3: previously provided (getRecommendedApis();)
		
		//Stage 4: Score answers by API
		if(useExtractors) {
			scoreAnswersByAPIs(topClasses,luceneBigSetIds);
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





	protected Set<Integer> getAnswersForTopThreads(Set<Integer> relevantAnswersIds, String processedQuery) {
		//get vectors for query words
		double[][] matrix1 = CrokageUtils.getMatrixVectorsForQuery(processedQuery,soContentWordVectorsMap);
		
		//get idfs for query
		double[][] idf1 = CrokageUtils.getIDFMatrixForQuery(processedQuery, soIDFVocabularyMap);
		
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
			
			double asymScore = CrokageUtils.round(Matrix.simDocPair(matrix1,matrix2,idf1,idf2),6);
			
			bucketsIdsScores.put(bucket.getId(), asymScore);
			
			//bucket.setTitleScore(simPair);
			
		}
				
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
	
	
	protected Set<Integer> getTopKRelevantAnswers(Set<Integer> candidateAnswersIds, String processedQuery) throws IOException {
		Set<Bucket> candidateBuckets = new HashSet<>();
		for(Integer answerId: candidateAnswersIds) {
			candidateBuckets.add(allAnswersWithUpvotesAndCodeBucketsMap.get(answerId));
		}
		
		
		String comparingTitle;
		String parentTitle;
		String comparingContent;
		answersIdsScores.clear();
		double maxAsymScore=0;
		double maxApiScore=0;
		double maxCosSimScore=0;
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
				
				double tfIdfCosineSimScore = vectorSpace.cosineSimilarity(queryDocument, bucket.getDocument());
				bucket.setTfIdfCosineSimScore(tfIdfCosineSimScore);
				
				if(tfIdfCosineSimScore>maxCosSimScore) {
					maxCosSimScore=tfIdfCosineSimScore;
				}
				
				double asymScore = bucketsIdsScores.get(bucket.getId());
				bucket.setSimPair(asymScore);
				if(asymScore>maxAsymScore) {
					maxAsymScore=asymScore;
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
		
		
		sortMethodsByFrequency();
		
		
		//normalization and other relevance boosts
		for(Bucket bucket:candidateBuckets) {
				double asymScore = bucket.getSimPair();
				asymScore = (asymScore / maxAsymScore); //normalization
				
				double tfIdfCosineSimScore = bucket.getTfIdfCosineSimScore();
				tfIdfCosineSimScore = (tfIdfCosineSimScore / maxCosSimScore); //normalization
	
				double apiAnswerPairScore=0;
				if(!topAnswerParentPairsAnswerIdScoreMap.isEmpty() && topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId())!=null) {
					apiAnswerPairScore = topAnswerParentPairsAnswerIdScoreMap.get(bucket.getId());
					apiAnswerPairScore = (apiAnswerPairScore/maxApiScore); //normalization
				}
				
				double finalScore = BotComposer.calculateRankingScore(asymScore,bucket,methodsCounterMap,apiAnswerPairScore,tfIdfCosineSimScore);
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


	protected boolean codeContainAnyClass(String processedCode) {
		for(String bikerClass: bikerTopClasses) {
			if(processedCode.contains(bikerClass.toLowerCase())) {
				return true;
			}
		}
		
		return false;
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
	



	protected void generateMetricsForApiExtractors(Integer[] kArray) throws Exception {
		
		//needs to be before extraction, because annotated dataset is used to generate queries file in case of crokage
		Map<Integer, Set<String>> goldSetQueriesApis = getGoldSetQueriesApis();

		int numApproaches = getApisForApproaches();
		
		Map<Integer, Set<String>> recommendedApis = getRecommendedApis();
	
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
		//System.out.println(metricResult.toString());
	
		
		
		
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



	protected Map<Integer, Set<String>> getGoldSetQueriesApis() throws IOException {
		Map<Integer, Set<String>> goldSetQueriesApis = new LinkedHashMap<>();
		
		if(dataSet.equals("nlp2api")) {
			getQueriesAndApisFromFileMayContainDupes(goldSetQueriesApis,NLP2API_GOLD_SET_FILE);
		
		}else if(dataSet.equals("selectedqueries-training")) {
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
				/*if(keyQuery.equals("How do I convert angle from radians to degrees?")) {
					System.out.println();
				}*/
				
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
