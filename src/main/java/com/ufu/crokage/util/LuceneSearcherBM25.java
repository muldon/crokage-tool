package com.ufu.crokage.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

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
	
	 
	public LuceneSearcherBM25() throws Exception {
		initializeConfigs();
	}
	
	
	@PostConstruct
	public void initializeConfigs() throws Exception {
		parseErrosNum = 0;
		standardAnalyzer = new StandardAnalyzer();
		
		// 1. create the index
		index = new RAMDirectory();
		answersCache = new HashMap<>();
		config = new IndexWriterConfig(standardAnalyzer);
		config.setSimilarity(new BM25Similarity(0.05f, 0.03f));
	
	}

	public void buildSearchManager(List<Bucket> upvotedScoredAnswers) throws Exception {
		logger.info("LuceneSearcherBM25.buildSearchManager. Indexing all upvoted scored aswers with code: "+upvotedScoredAnswers.size());

		indexedListSize = upvotedScoredAnswers.size();
		//logger.info("Number of answers to index: " + upvotedScoredAnswers.size() + " \nIndexing.... ");
				
		//String postsIds = "Indexing... \n";
		IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
		IndexWriter w = new IndexWriter(index, config);
		for (int i = 0; i < upvotedScoredAnswers.size(); i++) {
			Bucket answerBucket = upvotedScoredAnswers.get(i);
			//answersCache.put(answerBucket.getId(),answerBucket);
			String finalContent = answerBucket.getParentProcessedTitle()+" "+answerBucket.getParentProcessedBody()+" "+answerBucket.getParentProcessedCode()+" "+answerBucket.getProcessedBody()+ " "+answerBucket.getProcessedCode();			
			
			/*if(i%10000==0) {
				System.out.println(i+ " indexed ...");
			}*/
			//logger.info("Indexando questão: "+post.getId()+ " - Conteudo: "+finalContent); 
			//postsIds+= post.getId() + " - ";
			/*if(i%20==0){
				postsIds+="\n";
			}*/
			addDocument(w, finalContent, answerBucket.getId());
		}
		//logger.info(postsIds);
		w.close();
		
		//logger.info("Questions indexed. \nErrors in parse: "+questionsParseErrors.size());

		reader = DirectoryReader.open(index);
		searcher = new IndexSearcher(reader);
		//searcher.setSimilarity(new BM25Similarity(ClassifyService.bm25ParameterK, ClassifyService.bm25ParameterB));

	}
	
	

	public Set<Integer> search(String query, Integer maxConsideredSeachNumberForClassifier) throws Exception {
		//logger.info("Searching por query: "+query);
		Set<Integer> answersIds = new LinkedHashSet<>();
		
		QueryParser parser = new QueryParser("content", standardAnalyzer);
		
		Query myquery = parser.parse(query);
		
		TopDocs docs = searcher.search(myquery, maxConsideredSeachNumberForClassifier);
				
		ScoreDoc[] hits = docs.scoreDocs;
		if(hits.length<maxConsideredSeachNumberForClassifier) {
			maxConsideredSeachNumberForClassifier=hits.length;
		}
		
		for (int i = 0; i < maxConsideredSeachNumberForClassifier; i++) {
			ScoreDoc item = hits[i];
			Document doc = searcher.doc(item.doc);
			answersIds.add(Integer.valueOf(doc.get("id")));
		}
		
		return answersIds;		
		
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

	
}