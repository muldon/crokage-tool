package com.ufu.crokage.util;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ufu.bot.to.Bucket;

@Component
public class LuceneSearcherBM25 {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Analyzer standardAnalyzer;
	
	private Directory index;
	
	public static Integer indexedListSize;

	private ScoreDoc[] hits;

	private IndexSearcher searcher;

	private IndexReader reader;

	private Integer parseErrosNum;

	private Map<Integer, Bucket> answersCache;
	
	private IndexWriterConfig config;
	
	private Set<Integer> bigSetAnswersIds;
	
	 
	public LuceneSearcherBM25() throws Exception {
		initializeConfigs();
	}
	
	
	@PostConstruct
	public void initializeConfigs() throws Exception {
		parseErrosNum = 0;
		standardAnalyzer = new StandardAnalyzer();
		bigSetAnswersIds = new LinkedHashSet<>();
		// 1. create the index
		index = new RAMDirectory();
		answersCache = new HashMap<>();
		config = new IndexWriterConfig(standardAnalyzer);
		//default
		config.setSimilarity(new BM25Similarity(0.05f, 0.03f));
	
	}
	
	
	public void buildSearchManager(Map<Integer, Bucket> allAnswersWithUpvotesAndCodeBucketsMap,	Map<Integer, String> allThreadsIdsContentsMap) throws Exception {
		logger.info("LuceneSearcherBM25.buildSearchManager 2. Indexing all threads whose aswers have code: "+allAnswersWithUpvotesAndCodeBucketsMap.size());
		indexedListSize = allThreadsIdsContentsMap.size();
		IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
		IndexWriter w = new IndexWriter(index, config);
		
		for (Integer answerId: allAnswersWithUpvotesAndCodeBucketsMap.keySet()) {
			int parentId = allAnswersWithUpvotesAndCodeBucketsMap.get(answerId).getParentId();
			String finalContent = allThreadsIdsContentsMap.get(parentId);			
			addDocument(w, finalContent, answerId);
		}
		w.close();
		reader = DirectoryReader.open(index);
		searcher = new IndexSearcher(reader);
		
		
	}

	
	public void buildSearchManager(Map<Integer, Bucket> allAnswersWithUpvotesAndCodeBucketsMap) throws Exception {
		logger.info("LuceneSearcherBM25.buildSearchManager. Indexing all upvoted scored aswers with code: "+allAnswersWithUpvotesAndCodeBucketsMap.size());
		long initTime2 = System.currentTimeMillis();
		
		indexedListSize = allAnswersWithUpvotesAndCodeBucketsMap.size();
		IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
		IndexWriter w = new IndexWriter(index, config);
		
		Set<Integer> bucketsIds = allAnswersWithUpvotesAndCodeBucketsMap.keySet();
		
		for (Integer id: bucketsIds) {
			Bucket answerBucket = allAnswersWithUpvotesAndCodeBucketsMap.get(id);
			//String finalContent = answerBucket.getParentProcessedTitle()+" "+answerBucket.getParentProcessedBody()+" "+answerBucket.getParentProcessedCode()+" "+answerBucket.getProcessedBody()+ " "+answerBucket.getProcessedCode();
			String finalContent = answerBucket.getParentProcessedTitle()+" "+answerBucket.getParentProcessedBody()+" "+answerBucket.getParentProcessedCode()+" "+answerBucket.getProcessedBody()+ " "+answerBucket.getProcessedCode();
			/*if(!StringUtils.isBlank(answerBucket.getAcceptedAnswerBody())) {
				finalContent+= " "+answerBucket.getAcceptedAnswerBody();
			}*/
		    //String finalContent = answerBucket.getThreadContent();
			
			addDocument(w, finalContent, id);
			
		}
		w.close();
		
		reader = DirectoryReader.open(index);
		searcher = new IndexSearcher(reader);
		CrokageUtils.reportElapsedTime(initTime2,"LuceneSearcherBM25.buildSearchManager");
	}
	
	

	public Set<Integer> search(String query, Integer smallSetLimit, Integer bigSetLimit) throws Exception {
		//logger.info("Searching por query: "+query);
		Set<Integer> smallSetAnswersIds = new LinkedHashSet<>();
		bigSetAnswersIds.clear();
		
		QueryParser parser = new QueryParser("content", standardAnalyzer);
		
		Query myquery = parser.parse(query);
		
		TopDocs docs = searcher.search(myquery, bigSetLimit);
				
		ScoreDoc[] hits = docs.scoreDocs;
		if(hits.length<bigSetLimit) {
			bigSetLimit=hits.length;
		}
		if(hits.length<smallSetLimit) {
			smallSetLimit=hits.length;
		}
			
		for (int i = 0; i < bigSetLimit; i++) {
			ScoreDoc item = hits[i];
			Document doc = searcher.doc(item.doc);
			bigSetAnswersIds.add(Integer.valueOf(doc.get("id")));
		}
		
		for (int i = 0; i < smallSetLimit; i++) {
			ScoreDoc item = hits[i];
			Document doc = searcher.doc(item.doc);
			smallSetAnswersIds.add(Integer.valueOf(doc.get("id")));
		}
				
		return smallSetAnswersIds;		
		
	}

	/*
	 * public Integer search(String querystr, Integer
	 * maxConsideredSeachNumberForClassifier) throws Exception { Query q = new
	 * QueryParser("content", analyzer).parse(querystr); TopDocs docs =
	 * searcher.search(q, maxConsideredSeachNumberForClassifier); hits =
	 * docs.scoreDocs; q = null; return hits.length; }
	 */

	public Integer getQuestion(int numTest) throws Exception {
		int docId = hits[numTest].doc;
		Document d = searcher.doc(docId);

		Integer id = Integer.valueOf(d.get("id"));

		return id;
	}
	/*
	 * public Question getQuestion(int numTest) throws Exception { int docId =
	 * hits[numTest].doc; Document d = searcher.doc(docId);
	 * 
	 * Integer id = Integer.valueOf(d.get("id")); String title = d.get("title");
	 * String body = d.get("body"); String tags = d.get("tags"); Question
	 * question = new Question(id,title,body,tags);
	 * 
	 * return question; }
	 */

	public Integer getDocno(int numTest) throws Exception {
		int docId = hits[numTest].doc;
		Document d = searcher.doc(docId);
		return Integer.valueOf(d.get("id"));
	}

	public void finalize() throws Exception {
		reader.close();
	}

	private static void addDocument(IndexWriter w, String postContent, Integer id) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("content", postContent, Field.Store.YES));
		doc.add(new StringField("id", id.toString(), Field.Store.YES));
		w.addDocument(doc);
	}

	
	/*public Bucket getAnswer(Integer id) {
		return answersCache.get(id);
	}*/
	

	public void setSearchSimilarityParams(Float bm25ParameterK, Float bm25ParameterB) {
		searcher.setSimilarity(new BM25Similarity(bm25ParameterK, bm25ParameterB));
		config.setSimilarity(new BM25Similarity(bm25ParameterK, bm25ParameterB));
		logger.info("Setting k: "+bm25ParameterK+ " b: "+bm25ParameterB);
	}


	public Set<Integer> getBigSetAnswersIds() {
		return bigSetAnswersIds;
	}


	
	
	

	
}