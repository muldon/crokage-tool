package com.ufu.crokage.util;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
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

import com.ufu.crokage.to.Bucket;

@Component
public class LuceneSearcher {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Analyzer standardAnalyzer;
	
	private Directory index;
	
	public static Integer indexedListSize;

	private ScoreDoc[] hits;

	private IndexSearcher searcher;

	private IndexReader reader;
	
	private IndexWriter w;

	private Integer parseErrosNum;

	private Map<Integer, Bucket> answersCache;
	
	private IndexWriterConfig config;
	
	private Set<Integer> bigSetAnswersIds;
	
	 
	public LuceneSearcher() throws Exception {
		//initializeConfigs();
	}
	
	/*
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
		//config.setSimilarity(new BM25Similarity(0.05f, 0.03f));
		config.setSimilarity(new BM25Similarity(1.2f, 0.75f));
		//config.setSimilarity(new LMJelinekMercerSimilarity(lambda));
		//config.setSimilarity(new LMDirichletSimilarity());
	}*/
	
	
		
	public void buildSearchManager(Map<Integer, Bucket> allAnswersWithUpvotesAndCodeBucketsMap, SearcherParams searcherParams) throws Exception {
		logger.info("LuceneSearcher.buildSearchManager: searcherParams="+searcherParams+". Indexing all upvoted scored aswers with code: "+allAnswersWithUpvotesAndCodeBucketsMap.size());
		long initTime2 = System.currentTimeMillis();
		standardAnalyzer = new StandardAnalyzer();
		index = new RAMDirectory();
		
		indexedListSize = allAnswersWithUpvotesAndCodeBucketsMap.size();
		config = new IndexWriterConfig(standardAnalyzer);
		
		config.setSimilarity(new BM25Similarity(searcherParams.getK(), searcherParams.getB()));
		
		w = new IndexWriter(index, config);
		
		Set<Integer> bucketsIds = allAnswersWithUpvotesAndCodeBucketsMap.keySet();
		
		for (Integer id: bucketsIds) {
			Bucket answerBucket = allAnswersWithUpvotesAndCodeBucketsMap.get(id);
			String finalContent = answerBucket.getParentProcessedTitle()+" "+answerBucket.getParentProcessedBody()+" "+answerBucket.getParentProcessedCode()+" "+answerBucket.getProcessedBody()+ " "+answerBucket.getProcessedCode();
			//String finalContent = answerBucket.getParentProcessedTitleLemma()+" "+answerBucket.getParentProcessedBodyLemma()+" "+answerBucket.getParentProcessedCode()+" "+answerBucket.getProcessedBodyLemma()+ " "+answerBucket.getProcessedCode();
			//String finalContent = answerBucket.getParentProcessedTitle()+" "+answerBucket.getParentProcessedBody()+" "+answerBucket.getProcessedBody();
			/*if(!StringUtils.isBlank(answerBucket.getAcceptedAnswerBody())) {
				finalContent+= " "+answerBucket.getAcceptedAnswerBody();
			}*/
		    //String finalContent = answerBucket.getThreadContent();
			
			addDocument(w, finalContent, id);
			
		}
		
		w.close();
		reader = DirectoryReader.open(index);
		searcher = new IndexSearcher(reader);
		
		searcher.setSimilarity(new BM25Similarity(searcherParams.getK(), searcherParams.getB()));
		
		CrokageUtils.reportElapsedTime(initTime2,"LuceneSearcher.buildSearchManager");
	}
	
	

	public Set<Integer> search(String query, Integer smallSetLimit, Map<Integer, Float> bm25ScoreAnswerIdMap) throws Exception {
		//logger.info("Searching por query: "+query);
		Set<Integer> smallSetAnswersIds = new LinkedHashSet<>();
		//bigSetAnswersIds.clear();
		bm25ScoreAnswerIdMap.clear();
		
		QueryParser parser = new QueryParser("content", standardAnalyzer);
		
		Query myquery = parser.parse(query);
		
		//TopDocs docs = searcher.search(myquery, bigSetLimit);
		TopDocs docs = searcher.search(myquery, smallSetLimit);
				
		ScoreDoc[] hits = docs.scoreDocs;
		/*if(hits.length<bigSetLimit) {
			bigSetLimit=hits.length;
		}*/
		
		/*if(hits.length<smallSetLimit) {
			smallSetLimit=hits.length;
		}*/
			
		for (int i = 0; i < hits.length; i++) {
			ScoreDoc item = hits[i];
			Document doc = searcher.doc(item.doc);
			bm25ScoreAnswerIdMap.put(Integer.valueOf(doc.get("id")), item.score);
			smallSetAnswersIds.add(Integer.valueOf(doc.get("id")));
		}
		
		/*for (int i = smallSetLimit; i < bigSetLimit; i++) {
			ScoreDoc item = hits[i];
			Document doc = searcher.doc(item.doc);
			bm25ScoreAnswerIdMap.put(Integer.valueOf(doc.get("id")), item.score);
			bigSetAnswersIds.add(Integer.valueOf(doc.get("id")));
		}*/
				
		return smallSetAnswersIds;		
		
	}
	
	public Set<Integer> searchDupes(String query, Integer smallSetLimit, Bucket nonMaster) throws Exception {
		Set<Integer> smallSetAnswersIds = new LinkedHashSet<>();
		QueryParser parser = new QueryParser("content", standardAnalyzer);
		Query myquery = parser.parse(query);
		TopDocs docs = searcher.search(myquery, smallSetLimit);
				
		ScoreDoc[] hits = docs.scoreDocs;
		if(hits.length<smallSetLimit) {
			smallSetLimit=hits.length;
		}
			
		for (int i = 0; i < smallSetLimit; i++) {
			ScoreDoc item = hits[i];
			Document doc = searcher.doc(item.doc);
			smallSetAnswersIds.add(Integer.valueOf(doc.get("id")));
		}
		
		/*for (int i = smallSetLimit; i < bigSetLimit; i++) {
			ScoreDoc item = hits[i];
			Document doc = searcher.doc(item.doc);
			bm25ScoreAnswerIdMap.put(Integer.valueOf(doc.get("id")), item.score);
			bigSetAnswersIds.add(Integer.valueOf(doc.get("id")));
		}*/
				
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


	public void buildSearchManager(List<Bucket> allUpvotedScoredQuestions) throws IOException {
		logger.info("LuceneSearcher.buildSearchManager. Indexing all upvoted scored questions: "+allUpvotedScoredQuestions.size());
		long initTime2 = System.currentTimeMillis();
		
		indexedListSize = allUpvotedScoredQuestions.size();
		IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
		IndexWriter w = new IndexWriter(index, config);
		
		for (Bucket bucket: allUpvotedScoredQuestions) {
			String finalContent = bucket.getParentProcessedTitle()+" "+bucket.getParentProcessedBody()+ " "+bucket.getProcessedCode();
			if(bucket.getAcceptedAnswerId()!=null) {
				finalContent+= " "+bucket.getAcceptedOrMostUpvotedAnswerOfParentProcessedBody()+ " "+bucket.getAcceptedOrMostUpvotedAnswerOfParentProcessedCode();
			}
			addDocument(w, finalContent, bucket.getId());
			
		}
		w.close();
		
		reader = DirectoryReader.open(index);
		searcher = new IndexSearcher(reader);
		CrokageUtils.reportElapsedTime(initTime2,"LuceneSearcher.buildSearchManager");
		
	}


	


	


	
	
	

	
}