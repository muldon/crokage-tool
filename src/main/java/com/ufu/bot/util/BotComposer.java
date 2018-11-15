package com.ufu.bot.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufu.bot.to.Bucket;
import com.ufu.bot.to.BucketOld;
import com.ufu.bot.to.RelatedPost.RelationTypeEnum;
import com.ufu.crokage.util.CrokageUtils;

@Component
public class BotComposer {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${alphaCosSim}")
	public Double alphaCosSim; 
	
	@Value("${betaCoverageScore}")
	public Double betaCoverageScore; 
	
	@Value("${gamaCodeSizeScore}")
	public Double gamaCodeSizeScore; 
	
	@Value("${deltaRepScore}")
	public Double deltaRepScore; 
	
	@Value("${epsilonUpScore}")
	public Double epsilonUpScore; 
	
	@Value("${relationType_FROM_GOOGLE_QUESTION}")
	public Double relationTypeFromGoogleQuestion; 
	
	@Value("${relationType_FROM_GOOGLE_ANSWER}")
	public Double relationTypeFromGoogleAnswer; 
	
	@Value("${relationType_RELATED_NOT_DUPE}")
	public Double relationTypeRelatedNotDupe; 
	
	@Value("${relationType_LINKS_INSIDE_TEXTS}")
	public Double relationTypeLinksInsideTexts; 
	
	@Value("${relationType_RELATED_DUPE}")
	public Double relationTypeRelatedDupe; 
	
	
	public static double classFreqWeight;
	public static double methodFreqWeight;
	public static double repWeight;
	public static double simWeight;
	public static double upWeight;
	
	
	
	
	public void rankList(List<BucketOld> bucketsList) {
		
	/*	logger.info("Ranking with weights: "+
				"\n alphaCosSim = "+alphaCosSim +
				"\n betaCoverageScore = "+betaCoverageScore +
				"\n gamaCodeSizeScore = "+gamaCodeSizeScore +
				"\n deltaRepScore = "+deltaRepScore +
				"\n epsilonUpScore = "+epsilonUpScore+
				//relation weights
				" \n and ajuster weights of: "+
				"\n relationTypeFromGoogleQuestion = "+relationTypeFromGoogleQuestion +
				"\n relationTypeFromGoogleAnswer = "+relationTypeFromGoogleAnswer +
				"\n relationTypeRelatedNotDupe = "+relationTypeRelatedNotDupe +
				"\n relationTypeLinksInsideTexts = "+relationTypeLinksInsideTexts +
				"\n relationTypeRelatedDupe = "+relationTypeRelatedDupe 
				);
		*/
		//int count = 0;
		
		for(BucketOld bucketOld: bucketsList){
			double factorsScore =  alphaCosSim 	  * bucketOld.getCosSim() 
					              + betaCoverageScore * bucketOld.getCoverageScore() 
					              + gamaCodeSizeScore * bucketOld.getCodeSizeScore() 
					              + deltaRepScore 	  * bucketOld.getRepScore()
					              + epsilonUpScore    * bucketOld.getUpScore(); 
			double adjuster;
			
			if(bucketOld.getRelationTypeId().equals(RelationTypeEnum.FROM_GOOGLE_QUESTION_T1.getId())) {
				adjuster = relationTypeFromGoogleQuestion;
			}else if(bucketOld.getRelationTypeId().equals(RelationTypeEnum.FROM_GOOGLE_ANSWER_T4.getId())) {
				adjuster = relationTypeFromGoogleAnswer;
			}else if(bucketOld.getRelationTypeId().equals(RelationTypeEnum.RELATED_DUPE_T2.getId())) {
				adjuster = relationTypeRelatedDupe;
			}else if(bucketOld.getRelationTypeId().equals(RelationTypeEnum.RELATED_NOT_DUPE_T3.getId())) {
				adjuster = relationTypeRelatedNotDupe;
			}else {
				adjuster = relationTypeLinksInsideTexts;
			}
			double composedScore = adjuster*factorsScore;
			
			
			/*if(count<50) {
				logger.info("Composed score = "+composedScore+ " - adjuster: "+adjuster+ " - factorsScore: "+factorsScore);
			}
			count++;*/
			bucketOld.setComposedScore(BotUtils.round(composedScore,5));
		}
		
		
		Collections.sort(bucketsList, new Comparator<BucketOld>() {
		    public int compare(BucketOld o1, BucketOld o2) {
		        return o2.getComposedScore().compareTo(o1.getComposedScore());
		    }
		});

	}


	public void calculateScores(Double avgReputation, Double avgScore,HashMap<String, Double> tfIdfMainBucket, HashMap<String, Double> tfIdfOtherBucket, BucketOld mainBucket, BucketOld postBucket) {
		double cosine = cosineSimilarity(tfIdfMainBucket, tfIdfOtherBucket);
		postBucket.setCosSim(BotUtils.round(cosine,4));
		
		double coverageScore = calculateCoverageScore(mainBucket.getClassesNames(),postBucket.getClassesNames());
		postBucket.setCoverageScore(BotUtils.round(coverageScore,4));
		
		double codeScore = calculateCodeSizeScore(postBucket.getCodes());
		postBucket.setCodeSizeScore(BotUtils.round(codeScore,4));
		
		double repScore = calculateRepScore(postBucket.getUserReputation());
		postBucket.setRepScore(BotUtils.round(repScore,4));
		
		double upScore = calculateUpScore(postBucket.getPostScore());
		postBucket.setUpScore(BotUtils.round(upScore,4));
		
		/*if(postBucket.getPostId().equals(1323480)) {
			System.out.println("here");
		}*/
	}
	
	
	private double calculateUpScoreOld(Double avgScore, Integer postScore) {
		if(postScore==null) {
			return 0d;
		}
		double diff = avgScore-postScore;
		double repScore = 1/(1+ (Math.pow(Math.E,(diff))));
		
		return repScore;
	}


	
	private double calculateRepScoreOld(Double avgReputation,Integer userReputation) {
		if(userReputation==null) {
			return 0d;
		}
		double diff = avgReputation-userReputation;
		double repScore = 1/(1+ (Math.pow(Math.E,(diff))));
		
		return repScore;
	}
	
	
	
	
	public static double calculateUpScore(Integer upVotes) {
		Integer range1  = 1;
		Integer range2  = 5;
		Integer range3  = 10;
		Integer range4  = 25;
		Integer range5  = 50;
		Integer range6  = 75;
		Integer range7  = 100;
		Integer range8  = 200;
		Integer range9  = 500;
				
		if(upVotes==null) {
			return 0d;
		}
		double upScore = 0d;
		
		if(upVotes<=range1) {
			upScore = 0.1d;
		}else if(upVotes > range1 && upVotes <= range2) {
			upScore = 0.2d;
		}else if(upVotes > range2 && upVotes <= range3) {
			upScore = 0.3d;
		}else if(upVotes > range3 && upVotes <= range4) {
			upScore = 0.4d;		
		}else if(upVotes > range4 && upVotes <= range5) {
			upScore = 0.5d;
		}else if(upVotes > range5 && upVotes <= range6) {
			upScore = 0.6d;
		}else if(upVotes > range6 && upVotes <= range7) {
			upScore = 0.7d;
		}else if(upVotes > range7 && upVotes <= range8) {
			upScore = 0.8d;
		}else if(upVotes > range8 && upVotes <= range9) {
			upScore = 0.9d;
		}else if(upVotes > range9) {
			upScore = 1d;
		}
		
				
		return upScore;
	}
	
	
	public static double calculateRepScore(Integer userReputation) {
		Integer range1   = 1;
		Integer range2   = 5;
		Integer range3   = 10;
		Integer range4   = 20;
		Integer range5   = 50;
		Integer range6   = 75;
		Integer range7   = 100;
		Integer range8   = 200;
		Integer range9   = 500;
		Integer range10  = 1000;
		Integer range11  = 1500;
		Integer range12  = 2000;
		Integer range13  = 3000;
		Integer range14  = 5000;
		Integer range15  = 10000;
		Integer range16  = 15000;
		Integer range17  = 20000;
		Integer range18  = 50000;
		Integer range19  = 100000;
		Integer range20  = 200000;
		
		if(userReputation==null) {
			return 0d;
		}
		double repScore = 0d;
		
		if(userReputation > range1 && userReputation <= range2) {
			repScore = 0.05d;
		}else if(userReputation > range2 && userReputation <= range3) {
			repScore = 0.10d;
		}else if(userReputation > range3 && userReputation <= range4) {
			repScore = 0.15d;		
		}else if(userReputation > range4 && userReputation <= range5) {
			repScore = 0.20d;
		}else if(userReputation > range5 && userReputation <= range6) {
			repScore = 0.25d;
		}else if(userReputation > range6 && userReputation <= range7) {
			repScore = 0.30d;
		}else if(userReputation > range7 && userReputation <= range8) {
			repScore = 0.35d;
		}else if(userReputation > range8 && userReputation <= range9) {
			repScore = 0.40d;
		}else if(userReputation > range9 && userReputation <= range10) {
			repScore = 0.45d;
		}else if(userReputation > range10 && userReputation <= range11) {
			repScore = 0.50d;
		}else if(userReputation > range11 && userReputation <= range12) {
			repScore = 0.55d;
		}else if(userReputation > range12 && userReputation <= range13) {
			repScore = 0.60d;		
		}else if(userReputation > range13 && userReputation <= range14) {
			repScore = 0.65d;
		}else if(userReputation > range14 && userReputation <= range15) {
			repScore = 0.70d;
		}else if(userReputation > range15 && userReputation <= range16) {
			repScore = 0.75d;
		}else if(userReputation > range16 && userReputation <= range17) {
			repScore = 0.80d;
		}else if(userReputation > range17 && userReputation <= range18) {
			repScore = 0.85d;
		}else if(userReputation > range18 && userReputation <= range19) {
			repScore = 0.90d;
		}else if(userReputation > range19 && userReputation <= range20) {
			repScore = 0.95d;
		}else if(userReputation > range20) {
			repScore = 1.00d;
		}
		
				
		return repScore;
	}


	public static double calculateCodeSizeScore(List<String> postCodes) {
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


	public static double calculateCoverageScore(Set<String> mainBucketClassesNames, Set<String> postBucketlassesNames) {
		Set<String> intersection = new HashSet<String>(mainBucketClassesNames);
		
		float pSetSize = postBucketlassesNames.size();
		if(pSetSize==0f) {
			return 0d;
		}
		
		intersection.retainAll(postBucketlassesNames);
				
		int mSetIntersectionPsetSize = intersection.size();
		
		
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


	public static double calculateFinalScore(double simPair, Set<String> topClasses, Bucket bucket,	Map<String, Integer> methodsCounterMap) {
		
		double finalScore;
		
		double classFreqScore = calculateScoreForPresentClasses(bucket.getCode(),topClasses);
		
		double methodFreqScore = calculateScoreForCommonMethods(bucket.getCode(),methodsCounterMap);
		
		//pontuar substring comum entre a maoiria dos códigos ?
		
		/*double codeScore = BotComposer.calculateCodeSizeScore(CrokageUtils.getPreCodes(bucket.getBody()));
		
		simPair+= codeScore;*/
		
		double repScore = calculateRepScore(bucket.getUserReputation());
	
		double upScore = calculateUpScore(bucket.getUpVotesScore());
		
		finalScore = simPair*simWeight + classFreqScore*classFreqWeight + repScore*repWeight + upScore*upWeight + methodFreqScore*methodFreqWeight;
		
		return finalScore;
		
	}


	public static double getClassFreqWeight() {
		return classFreqWeight;
	}


	public static void setClassFreqWeight(double classFreqWeight) {
		BotComposer.classFreqWeight = classFreqWeight;
	}


	public static double getMethodFreqWeight() {
		return methodFreqWeight;
	}


	public static void setMethodFreqWeight(double methodFreqWeight) {
		BotComposer.methodFreqWeight = methodFreqWeight;
	}


	public static double getRepWeight() {
		return repWeight;
	}


	public static void setRepWeight(double repWeight) {
		BotComposer.repWeight = repWeight;
	}


	public static double getSimWeight() {
		return simWeight;
	}


	public static void setSimWeight(double simWeight) {
		BotComposer.simWeight = simWeight;
	}


	public static double getUpWeight() {
		return upWeight;
	}


	public static void setUpWeight(double upWeight) {
		BotComposer.upWeight = upWeight;
	}

 
	
	
	
	
}
