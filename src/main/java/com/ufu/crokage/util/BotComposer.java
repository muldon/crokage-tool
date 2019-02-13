package com.ufu.crokage.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ufu.crokage.to.Bucket;

@Component
public class BotComposer {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	public static double apiWeight;
	public static double methodFreqWeight;
	public static double cosSimWeight;
	public static double semWeight;
	
	
	
	
	
	private static double getDotProduct(HashMap<String, Double> query, HashMap<String, Double> weights2) {
		double product = 0;
		for (String term : query.keySet()) {
			Double secondWeight = weights2.get(term);
			if(secondWeight!=null){
				product += query.get(term) * secondWeight;
			}
			
		}
		
		return product;
	}
	
	private static double getMagnitude(HashMap<String, Double> weights) {
		double magnitude = 0;
		for (double weight : weights.values()) {
			magnitude += weight * weight;
		}
		
		return Math.sqrt(magnitude);
	}
	
	
	public static double cosineSimilarity(HashMap<String, Double> query, HashMap<String, Double> weights2) {
		if(query.isEmpty()||weights2.isEmpty()) {
			return 0d;
		}
		return getDotProduct(query, weights2) / (getMagnitude(query) * getMagnitude(weights2));
	}


	
	
	public static double calculateScoreForPresentClasses(String code, Set<String> topClasses) {
		
		double i = 0;
		for (String topClass : topClasses) {
			if (code.contains(topClass)) {
				return 1 - i;
			}
			i += 0.1;
		}

		return 0;
	}
	

	public static double calculateScoreForCommonMethods(String code, Map<String, Integer> methodsCounterMap) {
		for(String topMethod:methodsCounterMap.keySet()) {
			if(code.contains(topMethod)) {
				int topMethodFrequency = methodsCounterMap.get(topMethod);
				double score = CrokageUtils.log2(topMethodFrequency)/10;
				return score;
			}
		}
		return 0;
	}


	public static double calculateRankingScore(double asymScore, Bucket bucket, Map<String, Integer> methodsCounterMap, double apiAnswerPairScore,double cosSimScore) {
		double finalScore;
		
		double methodFreqScore = calculateScoreForCommonMethods(bucket.getCode(),methodsCounterMap);
		
		finalScore = asymScore*semWeight + apiAnswerPairScore*apiWeight + methodFreqScore*methodFreqWeight + cosSimScore*cosSimWeight;
		
		return finalScore;
		
	}


	public static double getApiWeight() {
		return apiWeight;
	}


	public static void setApiWeight(double classFreqWeight) {
		BotComposer.apiWeight = classFreqWeight;
	}


	public static double getMethodFreqWeight() {
		return methodFreqWeight;
	}


	public static void setMethodFreqWeight(double methodFreqWeight) {
		BotComposer.methodFreqWeight = methodFreqWeight;
	}


	
	public static double getSemWeight() {
		return semWeight;
	}


	public static void setSemWeight(double asymWeight) {
		BotComposer.semWeight = asymWeight;
	}


	

	public static double getCosSimWeight() {
		return cosSimWeight;
	}


	public static void setCosSimWeight(double tfIdfWeight) {
		BotComposer.cosSimWeight = tfIdfWeight;
	}


	

 
	
	
	
	
}
