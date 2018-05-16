package com.ufu;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Tester {

	public Tester() {
		String str = "How do I send an HTML email? javascript javac dd JAVA";
		StringTokenizer st = new StringTokenizer(str);
		Boolean containToken = Pattern.compile(".*\\bjava\\b.*").matcher(str.toLowerCase()).find();
		String token;
		
		
		//System.out.println(containToken);
		
		/*System.out.println("---- Split by space ------");
		while (st.hasMoreElements()) {
			token = st2.nextElement();
			if()
		}

		System.out.println("---- Split by comma ',' ------");
		StringTokenizer st2 = new StringTokenizer(str, ",");

		while (st2.hasMoreElements()) {
			System.out.println(st2.nextElement());
		}
		
		
		
		System.out.println(query.contains("java"));*/
		
		List<String> apis = new ArrayList<>();
		apis.add("Message");
		apis.add("Message2");
		System.out.println(step3(apis, "How do I javass connect to a MongoDB Database?"));
	}
	
	
	private String step3(List<String> apis, String query) {
		String completeQuery = "";
		
		Boolean containJavaToken = Pattern.compile(".*\\bjava\\b.*").matcher(query.toLowerCase()).find();
		
		if(!containJavaToken) {
			completeQuery += "java ";
		}
		completeQuery += query + " "+ apis.get(0) +" "+ apis.get(1);
		
		return completeQuery;
	}
	
	
	public static void main(String[] args) {
		Tester t = new Tester();
		
	}

}
