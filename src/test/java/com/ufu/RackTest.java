package com.ufu;

import java.util.List;
import java.util.stream.Collectors;

import core.CodeTokenProvider;

public class RackTest {

	public static void main(String[] args) {
		String query = "How to send email in Java?";
		int TOPK=5;
		//CodeTokenProvider ctProvider = new CodeTokenProvider(query,TOPK);
		CodeTokenProvider ctProvider = new CodeTokenProvider(query);
		List<String> apis = ctProvider.recommendRelevantAPIs();
		System.out.println(apis.stream().limit(10).collect(Collectors.toList()));
		
	}
}

