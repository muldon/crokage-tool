package com.ufu.bot.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufu.bot.tfidf.VectorSpaceModel;
import com.ufu.bot.to.Bucket;

@Component
public class BotComposer {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${alphaCosSim}")
	public Double alphaCosSim; 
	
	@Value("${betaCoverageScore}")
	public Double betaCoverageScore; 
	
	@Value("${gamaCodeScore}")
	public Double gamaCodeScore; 
	
	@Value("${deltaRepScore}")
	public Double deltaRepScore; 
	
	@Value("${epsilonUpScore}")
	public Double epsilonUpScore; 
	
	
	public void rankList(List<Bucket> bucketsList) {
		logger.info("Ranking with weights: "+
				"\n alphaCosSim = "+alphaCosSim +
				"\n betaCoverageScore = "+betaCoverageScore +
				"\n gamaCodeScore = "+gamaCodeScore +
				"\n deltaRepScore = "+deltaRepScore +
				"\n epsilonUpScore = "+epsilonUpScore
				);
		
		
		for(Bucket bucket: bucketsList){
			double composedScore =  alphaCosSim 	  * bucket.getCosSim() 
					              + betaCoverageScore * bucket.getCoverageScore() 
					              + gamaCodeScore 	  * bucket.getCodeScore() 
					              + deltaRepScore 	  * bucket.getRepScore()
					              + epsilonUpScore    * bucket.getUpScore(); 
			
			bucket.setComposedScore(composedScore);
		}
		
		Collections.sort(bucketsList, new Comparator<Bucket>() {
		    public int compare(Bucket o1, Bucket o2) {
		        return o2.getComposedScore().compareTo(o1.getComposedScore());
		    }
		});
		
		
	}


	public void calculateScores(Double avgReputation, Double avgScore,HashMap<String, Double> tfIdfMainBucket, HashMap<String, Double> tfIdfOtherBucket, Bucket mainBucket, Bucket postBucket) {
		double cosine = cosineSimilarity(tfIdfMainBucket, tfIdfOtherBucket);
		postBucket.setCosSim(cosine);
		
		double coverageScore = calculateCoverageScore(mainBucket.getClassesNames(),postBucket.getClassesNames());
		postBucket.setCoverageScore(coverageScore);
		
		double codeScore = calculateCodeScore(postBucket.getCodes());
		postBucket.setCodeScore(codeScore);
		
		double repScore = calculateRepScore(avgReputation,postBucket.getUserReputation());
		postBucket.setRepScore(repScore);
		
		double upScore = calculateUpScore(avgScore, postBucket.getPostScore());
		postBucket.setUpScore(upScore);
	}
	
	
	private double calculateUpScore(Double avgScore, Integer postScore) {
		if(postScore==null) {
			return 0d;
		}
		double diff = avgScore-postScore;
		double repScore = 1/(1+ (Math.pow(Math.E,(diff))));
		
		return repScore;
	}


	private double calculateRepScore(Double avgReputation,Integer userReputation) {
		if(userReputation==null) {
			return 0d;
		}
		double diff = avgReputation-userReputation;
		double repScore = 1/(1+ (Math.pow(Math.E,(diff))));
		
		return repScore;
	}


	private double calculateCodeScore(List<String> postCodes) {
		if(postCodes.size()==0) {
			return 0d;
		}
		List<String> cleanList = new ArrayList<>();
		String adjusted = "";
		for(String code: postCodes) {
			adjusted = code.replaceAll("(?m)^[ \t]*\r?\n", "");
			cleanList.add(adjusted);
		}
		
		double totalSize = 0d;
		double codeScore = 0d;
		double average= 0d;
		
		if(cleanList.size()>0) {
			int numberOfLines=0;
			for(String code: cleanList) {
				numberOfLines+= BotUtils.countLines(code);
			}
			average = numberOfLines / cleanList.size();
			totalSize = average;
		}
		codeScore = 1 / Math.sqrt(totalSize);
		
		return codeScore;
	}


	public double calculateCoverageScore(Set<String> mainBucketClassesNames, Set<String> postBucketlassesNames) {
		float pSetSize = postBucketlassesNames.size();
		if(pSetSize==0f) {
			return 0d;
		}
		
		mainBucketClassesNames.retainAll(postBucketlassesNames);
		
		
		int mSetIntersectionPsetSize = mainBucketClassesNames.size();
		
		
		double coverageScore = mSetIntersectionPsetSize / pSetSize;
		
		return coverageScore;
	}
	
	
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
		return getDotProduct(query, weights2) / (getMagnitude(query) * getMagnitude(weights2));
	}
	
	
	
	
}
