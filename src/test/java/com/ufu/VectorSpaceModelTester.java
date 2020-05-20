package com.ufu;


import java.util.ArrayList;

import com.ufu.crokage.tfidf.Corpus;
import com.ufu.crokage.tfidf.Document;
import com.ufu.crokage.tfidf.VectorSpaceModel;

/**
 * the tester class.
 * @author swapneel
 */
public class VectorSpaceModelTester {

	public static void main(String[] args) {
		
		String a = "the fox jumped into the woods";
		String b = "What you have should work. If, however, the spaces provided are defaulting to...";
		String c = "What you have should work. If, however, the spaces provided are defaulting to... the fox jumped into the woods ";
		
		
		Document query = new Document(a,1);
		Document d1 = new Document(b,2);
		Document d2 = new Document(c,3);
		/*Document d3 = new Document("ds_2014");
		Document d4 = new Document("ds_2015");*/

	
		ArrayList<Document> documents = new ArrayList<Document>();
		documents.add(query);
		documents.add(d1);
		documents.add(d2);
	/*	documents.add(d3);
		documents.add(d4);*/
//		documents.add(d5);
//		
//		documents.add(hpcs);
//		documents.add(hpdh);
//		documents.add(gray);
		
		Corpus corpus = new Corpus(documents);
		
		VectorSpaceModel vectorSpace = new VectorSpaceModel(corpus);
		
//		for (int i = 0; i < documents.size(); i++) {
//			for (int j = i + 1; j < documents.size(); j++) {
//				Document doc1 = documents.get(i);
//				Document doc2 = documents.get(j);
//				System.out.println("\nComparing " + doc1 + " and " +  doc2);
//				System.out.println(vectorSpace.cosineSimilarity(doc1, doc2));
//			}
//		}
		

		for(int i = 0; i < documents.size(); i++) {
			Document doc = documents.get(i);
			System.out.println("\nComparing to " + doc);
			System.out.println(vectorSpace.cosineSimilarity(query, doc));
		}
	}

}