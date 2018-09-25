package com.ufu;

import com.ufu.crokage.util.TextNormalizer;

import code.search.bda.scorecalc.CodeSearchBDAReformulator;

public class Nlp2ApiTester {
	public static void main(String[] args) {
		String searchQuery = "How do I convert number into Roman Numerals?";
		searchQuery = new TextNormalizer(searchQuery).normalizeTextLight();
		int TOPK = 10;
		int caseNo = 31;
		String suggested = new CodeSearchBDAReformulator(caseNo, searchQuery, TOPK, "both").provideRelevantAPIs();
		System.out.println(suggested);
		
	}
}
