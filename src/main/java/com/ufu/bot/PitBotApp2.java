package com.ufu.bot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.ufu.bot.exception.PitBotException;
import com.ufu.bot.googleSearch.GoogleWebSearch;
import com.ufu.bot.googleSearch.SearchQuery;
import com.ufu.bot.googleSearch.SearchResult;
import com.ufu.bot.service.PitBotService;
import com.ufu.bot.to.Bucket;
import com.ufu.bot.to.Comment;
import com.ufu.bot.to.Evaluation;
import com.ufu.bot.to.ExternalQuestion;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.RelatedPost;
import com.ufu.bot.to.RelatedPost.RelationTypeEnum;
import com.ufu.bot.to.SoThread;
import com.ufu.bot.util.BotUtils;
import com.ufu.survey.service.PitSurveyService;

import core.CodeTokenProvider;


@Component
public class PitBotApp2 {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PitBotService pitBotService;
	
	@Autowired
	private PitSurveyService pitSurveyService;
	
	@Autowired
	private BotUtils botUtils;
	
		
	@Autowired
	private GoogleWebSearch googleWebSearch;
		
	@Value("${phaseNumber}")
	public Integer phaseNumber;  
	
	@Value("${phaseSection}")
	public Integer phaseSection;  
		
	
	@Value("${obs}")
	public String obs;  
	
	
	@Value("${numberOfQueriesToTest}")
	public Integer numberOfQueriesToTest;
	
	@Value("${runRack}")
	public Boolean runRack;  
		
	@Value("${runGoogleSearch}")
	public Boolean runGoogleSearch;  
	
	
	@Value("${numberOfRackClasses}")
	public Integer numberOfRackClasses;  
	
	
	@Value("${numberOfGoogleResults}")
	public Integer numberOfGoogleResults;  
	
	@Value("${internalSurveyRankListSize}")
	public Integer internalSurveyRankListSize;  
	
	@Value("${externalSurveyRankListSize}")
	public Integer externalSurveyRankListSize;  
	
	@Value("${metricsGenerationRankListSize}")
	public Integer metricsGenerationRankListSize;  
	
	
	/*@Value("${APIKEY}")
	public String apiKey;
	
	@Value("${CUSTOM_SEARCH_ID}")
	public String customSearchId; */ 
	
	@Value("${shuffleListOfQueriesBeforeGoogleSearch}")
	public Boolean shuffleListOfQueriesBeforeGoogleSearch;
		
	
	
	@Value("${minTokenSize}")
	public Integer minTokenSize;
	
	/*@Value("${tagFilter}")
	public String tagFilter;  //null for all
*/	
	
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
	
	/*
	 * Path to a file which contains a FLAG indicating if the environment is test or production. This file (environmentFlag.properties) contains only one line with a boolean value (useProxy = true|false)
	 * If useProxy = true, the proxy is not applied for the google search engine. Otherwise, proxy is set.   
	 */
	@Value("${pathFileEnvFlag}")
	public String pathFileEnvFlag;   
	
	/*
	 * Stores the value obtained from the pathFileEnvFlag file 
	 */
	private Boolean useProxy;
	
		
	
	private List<String> queriesList;
	
	private Set<Integer> allQuestionsIds;
	private Set<Integer> allAnwersIds;
	private Set<Integer> allCommentsIds;
	
	private long initTime; 
	private long endTime;
	
	
	//protected Set<Post> postsByFilter;
	//private Bucket mainBucket;
	private List<ExternalQuestion> externalQuestions;
	private Set<SoThread> augmentedThreads;
	private String googleQuery;
	private List<String> rackApis;
	
	private Double avgScore;
	private Double avgReputation;
	private int goldSetRelevantAnswers;
	
	
	
	
	@PostConstruct
	public void init() throws Exception {
		
		logger.info("Initializing app...");
		//initializeVariables();
		botUtils.initializeConfigs();
		getPropertyValueFromLocalFile();
		
				
		logger.info("\nConsidering parameters: \n"
				+ "\n phaseNumber: "+phaseNumber
				+ "\n phaseSection: "+phaseSection
				+ "\n pathFileEnvFlag: "+pathFileEnvFlag
				+ "\n obs: "+obs
				+ "\n numberOfQueriesToTest: "+numberOfQueriesToTest
				+ "\n shuffleListOfQueriesBeforeGoogleSearch: "+shuffleListOfQueriesBeforeGoogleSearch
				+ "\n runRack: "+runRack
				+ "\n numberOfRackClasses: "+numberOfRackClasses
				+ "\n runGoogleSearch: "+runGoogleSearch
				+ "\n useProxy: "+useProxy
				+ "\n numberOfGoogleResults: "+numberOfGoogleResults
				+ "\n minTokenSize: "+minTokenSize
				+ "\n internalSurveyRankListSize: "+internalSurveyRankListSize
				+ "\n externalSurveyRankListSize: "+externalSurveyRankListSize
				+ "\n metricsGenerationRankListSize (or k): "+metricsGenerationRankListSize
				+ "\n");
				
		
		switch (phaseNumber) {
		case 1:
			initTime = System.currentTimeMillis();
			runPhase1or4or7();
			botUtils.reportElapsedTime(initTime," runPhase1 ");
			break;
		case 2:
			runPhase2or5or8();
			break;
			
		case 3:
			initTime = System.currentTimeMillis();
			runPhase3or6();
			botUtils.reportElapsedTime(initTime," runPhase3 ");
			break;
		case 4:
			initTime = System.currentTimeMillis();
			runPhase1or4or7();
			botUtils.reportElapsedTime(initTime," runPhase1 ");
			break;
		case 5:
			runPhase2or5or8();
			break;
		case 6:
			initTime = System.currentTimeMillis();
			runPhase3or6();
			botUtils.reportElapsedTime(initTime," runPhase6 ");
			break;
		case 7:
			initTime = System.currentTimeMillis();
			runPhase1or4or7();
			botUtils.reportElapsedTime(initTime," runPhase1 ");
			break;
		case 8:
			runPhase2or5or8();
			break;
		default:
			break;
		}
			

		
	}
	
	



	private void runPhase1or4or7() throws Exception {
		logWeights();
		
		/*
		 * Step 1: Question in Natural Language
		 * Read queries from a text file and insert into a list. Only 1/3.
		 */	
		step1();
				
		
		if(numberOfQueriesToTest!=null && numberOfQueriesToTest>0) {
			List<ExternalQuestion> newList = new ArrayList<ExternalQuestion>(externalQuestions.subList(0, numberOfQueriesToTest));
			externalQuestions = new ArrayList<>(newList);
			
		}
		
		int count =1;
		for(ExternalQuestion externalQuestion: externalQuestions) {
			
			initializeVariables();
			logger.info("\n\n\n\n\n\n\nProcessing new question: "+externalQuestion);
			initTime = System.currentTimeMillis();
			runSteps2to6(externalQuestion.getRawQuery()); 
			botUtils.reportElapsedTime(initTime,"runSteps2to6");
			
			externalQuestion.setGoogleQuery(googleQuery);
			externalQuestion.setUseRack(runRack);
			externalQuestion.setClasses(String.join(", ", rackApis));
									
			logger.info("saving external question and related ids for external question: "+externalQuestion.getExternalId());
			pitBotService.saveExternalQuestionAndRelatedIds(externalQuestion,botUtils.getAnswerPostsCache());
			count++;
			if(count>99) {
				break;
			}
			
			initTime = System.currentTimeMillis();
			List<Bucket> scoredBucketList = pitSurveyService.runSteps7and8(externalQuestion,augmentedThreads);
			
			Integer maxRankSize = getMaxRankSize();
			List<Bucket> trimmedRankedList = pitSurveyService.step9(scoredBucketList,maxRankSize);
			
			logger.info("list of ranks generated, now saving ranks for question "+externalQuestion.getExternalId());
			pitBotService.saveRanks(externalQuestion,trimmedRankedList,true,phaseNumber);
			
		}
	}
	
	
	private Integer getMaxRankSize() {
		Integer maxRankSize=null;
		if(phaseNumber.equals(1) || phaseNumber.equals(2) || phaseNumber.equals(4)) {
			maxRankSize = internalSurveyRankListSize;
		}else if(phaseNumber.equals(6)) {
			maxRankSize = metricsGenerationRankListSize;
		}else if(phaseNumber.equals(7)) {
			maxRankSize = externalSurveyRankListSize;
		}
		return maxRankSize;
	}





	private void logWeights() {
		logger.info("Ranking with weights: "+
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
	}





	private void runPhase2or5or8() throws IOException {
		//load external questions and their related posts and store in cache
		//pitSurveyService.loadQuestionsAndRelatedPostsToCache();
		pitSurveyService.loadExternalQuestions();
	}
	

	/*


	private void runPhase3Old() throws Exception {
		//load external questions and their related posts and store in cache
		pitSurveyService.loadQuestionsAndRelatedPostsToCache();
		pitSurveyService.runPhase3Old();
	}*/


	private void runPhase3or6() throws Exception {
		
		int referencePhaseNumber = phaseNumber - 2;  //this reference number can be 1 or 4. 
		String fileName = "Phase"+(phaseNumber-1)+"AfterAgreement";
		String fileNameMatrixKappaBeforeAgreement = "Phase"+(phaseNumber-1)+"MatrixForKappaBeforeAgreement.csv";
		String fileNameMatrixKappaAfterAgreement = "Phase"+(phaseNumber-1)+"MatrixForKappaAfterAgreement.csv";
		String reportCGFileName = "Phase"+(phaseNumber-1)+"CGReport.csv";
		
		Integer likertsResearcher1BeforeAgreementColumn = 1;
		Integer likertsResearcher2BeforeAgreementColumn = 2;
		Integer likertsResearcher1AfterAgreementColumn = 3;
		Integer likertsResearcher2AfterAgreementColumn = 4;
				
		if(phaseSection==1) { //Build excel with evaluations. Agreement phase. 
			List<ExternalQuestion> externalQuestions = pitBotService.getExternalQuestionsByPhase(referencePhaseNumber);
			buildCsvPhaseSection1(fileName,externalQuestions,referencePhaseNumber);
			
		}else if(phaseSection==2) { //Use previous xlsx spreedShed to build a matrix of values for the scales - before agreement 
			List<Evaluation> evaluationsWithBothUsersScales = new ArrayList<>();
			readXlsxToEvaluationList(evaluationsWithBothUsersScales,fileName,likertsResearcher1BeforeAgreementColumn,likertsResearcher2BeforeAgreementColumn);
			buildMatrixForKappa(evaluationsWithBothUsersScales,fileNameMatrixKappaBeforeAgreement); 
		
		}else if(phaseSection==3) { //Use previous xlsx spreedShed to build a matrix of values for the scales - after agreement
			List<Evaluation> evaluationsWithBothUsersScales = new ArrayList<>();
			readXlsxToEvaluationList(evaluationsWithBothUsersScales,fileName,likertsResearcher1AfterAgreementColumn,likertsResearcher2AfterAgreementColumn);
			buildMatrixForKappa(evaluationsWithBothUsersScales,fileNameMatrixKappaAfterAgreement);
		
		}
		
		else if(phaseNumber==3 && phaseSection==4) {//Discover the best adjuster weights. Use the previous analyzed posts to discover what is the influence of the post origin the post quality. Set adjuster weights.
			List<Evaluation> evaluationsWithBothUsersScales = new ArrayList<>();
			readXlsxToEvaluationList(evaluationsWithBothUsersScales,fileName,likertsResearcher1AfterAgreementColumn,likertsResearcher2AfterAgreementColumn);
			buildReportAdjustmentWeights(evaluationsWithBothUsersScales,reportCGFileName);
			
		
		}else if(phaseNumber==6) {
			String previousAgreementPhase = "Phase2AfterAgreement";
			List<Evaluation> phase2EvaluationsWithBothUsersScales = new ArrayList<>();
			readXlsxToEvaluationList(phase2EvaluationsWithBothUsersScales,previousAgreementPhase,likertsResearcher1AfterAgreementColumn,likertsResearcher2AfterAgreementColumn);
			
			List<Evaluation> phase5EvaluationsWithBothUsersScales = new ArrayList<>();
			readXlsxToEvaluationList(phase5EvaluationsWithBothUsersScales,fileName,likertsResearcher1AfterAgreementColumn,likertsResearcher2AfterAgreementColumn);
			
			
			if(phaseSection==4) {
				generateMetricsForEvaluations(phase5EvaluationsWithBothUsersScales);
				generateMetricsForEvaluations(phase2EvaluationsWithBothUsersScales);
				
			
			}else if(phaseSection==5) {
				generateComposerWeights(phase5EvaluationsWithBothUsersScales);
			}
		}
			
		
	}


	private void generateComposerWeights(List<Evaluation> phase5EvaluationsWithBothUsersScales) {
		// TODO Auto-generated method stub
		
	}




	private void generateMetricsForEvaluations(List<Evaluation> evaluationsWithBothUsersScales) throws IOException {
		//BufferedWriter bw =null;
		Map<Integer,Integer> externalQuestionOracleCounter = new HashMap<>();
		
		try {
			
			int assessed = 0;
			Set<ExternalQuestion> consideredExternalQuestions = new HashSet();
			List<Evaluation> consideredEvaluations = new ArrayList<>();
			
			for(Evaluation evaluation:evaluationsWithBothUsersScales){
				int likert1 = evaluation.getLikertScaleUser1();
				int likert2 = evaluation.getLikertScaleUser2();
				int diff = Math.abs(likert1 - likert2);
				if(diff>1) {
					continue;
				}
				assessed++;
				consideredExternalQuestions.add(pitBotService.getExternalQuestionById(evaluation.getExternalQuestionId()));
				
				double meanFull = (likert1+likert2)/(double)2;
				double meanLikert = BotUtils.round(meanFull,2);
				
				evaluation.setMeanLikert(meanLikert);
				consideredEvaluations.add(evaluation);
			}
			logger.info("\nTotal evaluations: "+evaluationsWithBothUsersScales.size()
				+ "\nConsidered with likert difference <=1: "+assessed	
				+ "\nConsidered external questions having evaluations with difference <=1: "+consideredExternalQuestions.size()
					);
			
			
			
			Integer maxRankSize = getMaxRankSize(); //or k
			/*
			 * The value of k is maxRankSize
			 */
			
			/*
			 * 5 likert scales, k as cutoff
			 */
			double idcg = BotUtils.calculateIDCG(5,maxRankSize);
			double ndcg = 0;
			//bootstrap here
			double all_ndcgs[] = new double[consideredExternalQuestions.size()];
			int index =0;
			 
			for(ExternalQuestion externalQuestion: consideredExternalQuestions) {
				//initializeVariables();
				String questionStr = "Id: "+externalQuestion.getId()+ " - externalId: "+externalQuestion.getExternalId()+ " - Gquery: "+externalQuestion.getGoogleQuery();
				logger.info("\n\n\n\n\n\n\n Recovering related posts to question id: "+questionStr);
				
				List<Post> answers = new ArrayList<>();
				
				List<RelatedPost> relatedPosts = pitBotService.getRelatedPostsByExternalQuestionId(externalQuestion.getId());
				logger.info("Posts found for this query: "+relatedPosts.size());	
				for(RelatedPost relatedPost: relatedPosts) {
					Post answer = pitBotService.findPostById(relatedPost.getPostId());
					answer.setRelationTypeId(relatedPost.getRelationTypeId());
					answers.add(answer);
				}
				
				SoThread thread = new SoThread(null, answers);
				Set<SoThread> threadListOneElement = new HashSet<>();
				threadListOneElement.add(thread);
				
				
				List<Bucket> scoredBucketList = pitSurveyService.runSteps7and8(externalQuestion,threadListOneElement);
				
				
				List<Bucket> trimmedRankedList = pitSurveyService.step9(scoredBucketList,maxRankSize);
				double dcg = computeDCG(trimmedRankedList,consideredEvaluations,externalQuestion.getId());
				ndcg = BotUtils.round(dcg / idcg , 4);
				all_ndcgs[index] = ndcg;
				
				index++;
				//testar ...
				
			}
			
			double averageNdcgs = Arrays.stream(all_ndcgs).average().orElse(Double.NaN); 
			logger.info("Average ndcg: "+averageNdcgs);
			
			
			/*logger.info("\nHit@" + maxRankSize + ": " + BotUtils.round((double) hitK / externalQuestionsWithGoldSet.size(),4) 
						+ "\nMR@" + maxRankSize + ": " + BotUtils.round(recall_sum / externalQuestionsWithGoldSet.size(),4)
						+ "\nMRR@" + maxRankSize + ": " + BotUtils.round(rrank_sum / externalQuestionsWithGoldSet.size(),4)
						+ "\nMAP@" + maxRankSize + ": " + BotUtils.round(preck_sum / externalQuestionsWithGoldSet.size(),4)
						+ "");*/
			
			//end bootstrap here
			
			/*bw = new BufferedWriter(new FileWriter(reportCGFileName));
			bw.write("Post Origin;Count;CG;Ideal CG;NCG\n\n");
			
			bw.write("\nTotal;"+assessed+"\n");*/
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//bw.close();
		}
		
		
		
	}
	

	private double computeDCG(List<Bucket> trimmedRankedList, List<Evaluation> consideredEvaluations, Integer externalQuestionId) {
		double dcg = 0;
		int rank = 0;
		for(Bucket bucket: trimmedRankedList) {
			rank++;
			for(Evaluation evaluation: consideredEvaluations) {
				if(evaluation.getExternalQuestionId().equals(externalQuestionId) && evaluation.getPostId().equals(bucket.getPostId())) {
					dcg += BotUtils.computeDCG(evaluation.getMeanLikert(),rank );
					break;					
				}
			}
			
		}
				
		return dcg;
	}





	private void generateComposerWeightsOld(List<Evaluation> evaluationsWithBothUsersScales, String reportCGFileName) throws IOException {
		//BufferedWriter bw =null;
		Map<Integer,Integer> externalQuestionOracleCounter = new HashMap<>();
		
		
		try {
			
			int assessed = 0;
			List<Evaluation> goldSetEvaluations = new ArrayList<>();
			Set<ExternalQuestion> externalQuestionsWithGoldSet = new HashSet();
			
			for(Evaluation evaluation:evaluationsWithBothUsersScales){
				/*RelatedPost relatedPost = pitBotService.getRelatedPostByExternalQuestionIdAndPostId(evaluation.getExternalQuestionId(), evaluation.getPostId());					
				Rank rank = pitBotService.getRankByRelatedPostIdAndInternalEvaluation(relatedPost.getId(),true);
				evaluation.setRank(rank);
				if(rank==null) {
					logger.error("Rank can not be null: relatedPostId: "+relatedPost.getId());
				}*/
				
				int likert1 = evaluation.getLikertScaleUser1();
				int likert2 = evaluation.getLikertScaleUser2();
				int diff = Math.abs(likert1 - likert2);
				if(diff>1) {
					continue;
				}
				assessed++;
				
				double meanFull = (likert1+likert2)/(double)2;
				double meanLikert = BotUtils.round(meanFull,2);
				
				evaluation.setMeanLikert(meanLikert);
				
				if(meanLikert>=4) {
					goldSetEvaluations.add(evaluation);
					externalQuestionsWithGoldSet.add(pitBotService.getExternalQuestionById(evaluation.getExternalQuestionId()));
					botUtils.addMapCacheCount(externalQuestionOracleCounter,evaluation.getExternalQuestionId());
					
				}
				
				
				//ranks.put(rank.getId(), rank);
			}
			logger.info("\nTotal evaluations: "+evaluationsWithBothUsersScales.size()
				+ "\nConsidered with likert difference <=1: "+assessed	
				+ "\nRelevant answers (mean likert > 4): "+goldSetEvaluations.size()
				+ "\nExternal questions with gold set: "+externalQuestionsWithGoldSet.size()
					);
			
			
			
			/*
			 * Considering only questions from phase 4 at first
			 */
			/*int referencePhase = 4;
			
			List<ExternalQuestion> externalQuestionsPhaseRef = pitBotService.getExternalQuestionsByPhase(referencePhase);
			List<ExternalQuestion> externalQuestionsPhaseGoldSet = new ArrayList<>();
			for(Evaluation goldEvaluation: goldSetEvaluations) {
				
				for(ExternalQuestion externalQuestionPhase: externalQuestionsPhaseRef) {
					if(goldEvaluation.getExternalQuestionId().equals(externalQuestionPhase.getId())){ 
					   
						
					}
				}
				
				
			}*/
			
			externalQuestionsWithGoldSet = ImmutableSet.copyOf(Iterables.limit(externalQuestionsWithGoldSet, 5));
			
			int hitK = 0;
			double rrank_sum = 0;
			double precision_sum = 0;
			double preck_sum = 0;
			double recall_sum = 0;
			double fmeasure_sum = 0;
			Integer maxRankSize = getMaxRankSize(); //or k
			/*
			 * The value of k is maxRankSize
			 */
			
			for(ExternalQuestion externalQuestion: externalQuestionsWithGoldSet) {
				//initializeVariables();
				String questionStr = "Id: "+externalQuestion.getId()+ " - externalId: "+externalQuestion.getExternalId()+ " - Gquery: "+externalQuestion.getGoogleQuery();
				logger.info("\n\n\n\n\n\n\n Recovering related posts to question id: "+questionStr);
				
				List<Post> answers = new ArrayList<>();
				
				List<RelatedPost> relatedPosts = pitBotService.getRelatedPostsByExternalQuestionId(externalQuestion.getId());
				logger.info("Posts found for this query: "+relatedPosts.size());	
				for(RelatedPost relatedPost: relatedPosts) {
					Post answer = pitBotService.findPostById(relatedPost.getPostId());
					answer.setRelationTypeId(relatedPost.getRelationTypeId());
					answers.add(answer);
				}
				
				SoThread thread = new SoThread(null, answers);
				Set<SoThread> threadListOneElement = new HashSet<>();
				threadListOneElement.add(thread);
				
				
				List<Bucket> scoredBucketList = pitSurveyService.runSteps7and8(externalQuestion,threadListOneElement);
				int goldSetSize = externalQuestionOracleCounter.get(externalQuestion.getId());
				
				//bootstrap here
				List<Bucket> trimmedRankedList = pitSurveyService.step9(scoredBucketList,maxRankSize);
				hitK += isRelevantPostFound(trimmedRankedList,goldSetEvaluations,externalQuestion.getId());
				double recall = 0;
				recall = getRecallK(trimmedRankedList,goldSetEvaluations,externalQuestion.getId(),goldSetSize);
				recall_sum += recall;
				double rrank = getRRank(trimmedRankedList,goldSetEvaluations,externalQuestion.getId());
				rrank_sum += rrank;
				double preck = getAvgPrecisionK(trimmedRankedList,goldSetEvaluations,externalQuestion.getId());
				preck_sum += preck;
				
				//end bootstrap here
			}
			
			logger.info("\nHit@" + maxRankSize + ": " + BotUtils.round((double) hitK / externalQuestionsWithGoldSet.size(),4) 
						+ "\nMR@" + maxRankSize + ": " + BotUtils.round(recall_sum / externalQuestionsWithGoldSet.size(),4)
						+ "\nMRR@" + maxRankSize + ": " + BotUtils.round(rrank_sum / externalQuestionsWithGoldSet.size(),4)
						+ "\nMAP@" + maxRankSize + ": " + BotUtils.round(preck_sum / externalQuestionsWithGoldSet.size(),4)
						+ "");
			/*bw = new BufferedWriter(new FileWriter(reportCGFileName));
			bw.write("Post Origin;Count;CG;Ideal CG;NCG\n\n");
			
			bw.write("\nTotal;"+assessed+"\n");*/
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//bw.close();
		}
		
		
		
	}




	private double getAvgPrecisionK(List<Bucket> trimmedRankedList, List<Evaluation> goldSetEvaluations, Integer externalQuestionId) {
		int count=0;
		double found = 0;
		double linePrec = 0;
		for(Bucket bucket: trimmedRankedList) {
			count++;
			for(Evaluation evaluation: goldSetEvaluations) {
				if(evaluation.getExternalQuestionId().equals(externalQuestionId) && evaluation.getPostId().equals(bucket.getPostId())) {
					found++;
					linePrec += (found / count);
				}
			}
		}
		
		if (found == 0) {
			return 0;
		}
		
		return linePrec / found;
	}





	private double getRRank(List<Bucket> trimmedRankedList, List<Evaluation> goldSetEvaluations, Integer externalQuestionId) {
		double rrank = 0;
		int count=0;
		outer: for(Bucket bucket: trimmedRankedList) {
			count++;
			for(Evaluation evaluation: goldSetEvaluations) {
				if(evaluation.getExternalQuestionId().equals(externalQuestionId) && evaluation.getPostId().equals(bucket.getPostId())) {
					rrank = 1.0 / (count);
					break outer;
				}
			}
		}
		
		return rrank;
	}





	private double getRecallK(List<Bucket> trimmedRankedList, List<Evaluation> goldSetEvaluations, Integer externalQuestionId, int goldSetSize) {
		double found = 0;
		int rank = 0;
		for(Bucket bucket: trimmedRankedList) {
			rank++;
			for(Evaluation evaluation: goldSetEvaluations) {
				if(evaluation.getExternalQuestionId().equals(externalQuestionId) && evaluation.getPostId().equals(bucket.getPostId())) {
					found++;
					logger.info("hit on rank: "+rank);
				}
			}
		}
		
		return found/goldSetSize;
	}





	private int isRelevantPostFound(List<Bucket> trimmedRankedList, List<Evaluation> goldSetEvaluations, Integer externalQuestionId) {
		int found = 0;
		int rank = 0;
		outer: for(Bucket bucket: trimmedRankedList) {
			rank++;
			for(Evaluation evaluation: goldSetEvaluations) {
				if(evaluation.getExternalQuestionId().equals(externalQuestionId) && evaluation.getPostId().equals(bucket.getPostId())) {
					found = 1;
					logger.info("hit on rank: "+rank);
					break outer;
				}
			}
		}
		
		return found;
	}

	
	private double getRecallK(List<Bucket> trimmedRankedList, List<Evaluation> goldSetEvaluations, Integer externalQuestionId) {
		double found = 0;
		int rank = 0;
		outer: for(Bucket bucket: trimmedRankedList) {
			rank++;
			/*if(isFoundPost(bucket,goldSetEvaluations)) {
				found++;
			}*/
		}
		
		return found / goldSetEvaluations.size();
	}



/*
	private void calculateMetrics(List<Bucket> trimmedRankedList, List<Evaluation> goldSetEvaluationsWithMeanLikerts, Integer externalQuestionId) {
		int rankCount = 0;
		for(Bucket bucket: trimmedRankedList) {
			rankCount++;
			
		}
		
		
	}*/





	private void buildReportAdjustmentWeights(List<Evaluation> evaluationsWithBothUsersScales, String reportCGFileName) throws IOException {
		BufferedWriter bw =null;
		try {
			
			List<Evaluation> fromGQuestion = new ArrayList<>();
			List<Evaluation> relatedDupe = new ArrayList<>();
			List<Evaluation> relatedNotDupe = new ArrayList<>();
			List<Evaluation> fromGAnswer = new ArrayList<>();
			
			Double fromGQuestionCG  = 0d,  idealFromGQuestionCG 	= 0d,  ncgT1 = 0d;
			Double relatedDupeCG    = 0d,  idealRelatedDupeCG 		= 0d,  ncgT2 = 0d;
			Double relatedNotDupeCG = 0d,  idealRelatedNotDupeCG 	= 0d,  ncgT3 = 0d;
			Double fromGAnswerCG    = 0d,  idealFromGAnswerCG 		= 0d,  ncgT4 = 0d;
			
			int assessed = 0;
			for(Evaluation evaluation:evaluationsWithBothUsersScales){
				RelatedPost relatedPost = pitBotService.getRelatedPostByExternalQuestionIdAndPostId(evaluation.getExternalQuestionId(), evaluation.getPostId());					
				//Rank rank = pitBotService.getRankByRelatedPostIdAndPhase(relatedPost.getId(),1);
				//System.out.println(relatedPost.getRelationTypeId());
				Integer relatedTypeId = relatedPost.getRelationTypeId();
				//evaluation.setRelatedTypeId(relatedTypeId);
				int likert1 = evaluation.getLikertScaleUser1();
				int likert2 = evaluation.getLikertScaleUser2();
				int diff = Math.abs(likert1 - likert2);
				if(diff>1) {
					continue;
				}
				assessed++;
				double meanFull = (likert1+likert2)/(double)2;
				double meanLikert = BotUtils.round(meanFull,2);
				
				if(relatedTypeId.equals(RelationTypeEnum.FROM_GOOGLE_QUESTION_T1.getId())) {
					fromGQuestion.add(evaluation);
					fromGQuestionCG+= meanLikert;
					idealFromGQuestionCG += 5;
					
				}else if(relatedTypeId.equals(RelationTypeEnum.RELATED_DUPE_T2.getId())) {
					relatedDupe.add(evaluation);
					relatedDupeCG+= meanLikert;
					idealRelatedDupeCG += 5;
					
				}else if(relatedTypeId.equals(RelationTypeEnum.RELATED_NOT_DUPE_T3.getId())) {
					relatedNotDupe.add(evaluation);
					relatedNotDupeCG+= meanLikert;
					idealRelatedNotDupeCG += 5;
					
				}else if(relatedTypeId.equals(RelationTypeEnum.FROM_GOOGLE_ANSWER_T4.getId())) {
					fromGAnswer.add(evaluation);
					fromGAnswerCG+= meanLikert;
					idealFromGAnswerCG += 5;
				}
				
			}
			ncgT1 = BotUtils.round((fromGQuestionCG/idealFromGQuestionCG),2);
			ncgT2 = BotUtils.round((relatedDupeCG/idealRelatedDupeCG),2);
			ncgT3 = BotUtils.round((relatedNotDupeCG/idealRelatedNotDupeCG),2);
			ncgT4 = BotUtils.round((fromGAnswerCG/idealFromGAnswerCG),2);
			
			bw = new BufferedWriter(new FileWriter(reportCGFileName));
			bw.write("Post Origin;Count;CG;Ideal CG;NCG\n\n");
			bw.write("From Google Question;"+fromGQuestion.size()+";"+fromGQuestionCG+";"+idealFromGQuestionCG+";"+ncgT1+"\n");
			bw.write("Related Dupe;"+relatedDupe.size()+";"+relatedDupeCG+";"+idealRelatedDupeCG+";"+ncgT2+"\n");
			bw.write("Related Not Dupe;"+relatedNotDupe.size()+";"+relatedNotDupeCG+";"+idealRelatedNotDupeCG+";"+ncgT3+"\n");
			bw.write("From Google Answer;"+fromGAnswer.size()+";"+fromGAnswerCG+";"+idealFromGAnswerCG+";"+ncgT4+"\n");
			
			bw.write("\nTotal;"+assessed+"\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
		
	}





	private void readXlsxToEvaluationList(List<Evaluation> evaluationsWithBothUsersScales, String fileName, Integer firstColumn, Integer secondColumn) {
		try {

			FileInputStream excelFile = new FileInputStream(new File(fileName + ".xlsx"));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			Integer externalQuestionId=null;
			while (iterator.hasNext()) {

				Row currentRow = iterator.next();
				Cell currentCellColumnA = currentRow.getCell(0);
				Cell currentCellColumnB = currentRow.getCell(firstColumn);
				Cell currentCellColumnC = currentRow.getCell(secondColumn);
				
				if(currentCellColumnA!=null) {
					
					String currentAValue = currentCellColumnA.getStringCellValue();
					Integer postId = botUtils.identifyQuestionIdFromUrl(currentAValue);
					if(currentAValue.contains("id:")) {
						String parts[] = currentAValue.split("\\|");
						String externalQuestionIdStr = parts[0].replaceAll("\\D+","");
						externalQuestionId = new Integer(externalQuestionIdStr);
					}
					
					if (currentCellColumnB != null && currentCellColumnB.getCellTypeEnum() == CellType.NUMERIC
						&& currentCellColumnC != null && currentCellColumnC.getCellTypeEnum() == CellType.NUMERIC) {
						
						Integer currentBValue = (int) (currentCellColumnB.getNumericCellValue());
						Integer currentCValue = (int) (currentCellColumnC.getNumericCellValue());
						//System.out.println("Scales: " + currentBValue + " - " + currentCValue);
	
						Evaluation eval = new Evaluation();
						eval.setExternalQuestionId(externalQuestionId);
						eval.setLikertScaleUser1(currentBValue);
						eval.setLikertScaleUser2(currentCValue);
						eval.setPostId(postId);
						evaluationsWithBothUsersScales.add(eval);
					}
				
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}





	private void buildCsvPhaseSection1(String fileName,List<ExternalQuestion> externalQuestions, int referencePhaseNumber) throws IOException {
		BufferedWriter bw =null;
		try {
			bw = new BufferedWriter(new FileWriter(fileName+".csv"));
			bw.write(";Researcher1;Researcher2;Researcher1 agreement;Researcher2 agreement\n\n");
			
			for(ExternalQuestion externalQuestion:externalQuestions){
									
				bw.write("id:("+externalQuestion.getId()+") | Refid: "+ externalQuestion.getExternalId()+ " - "+ externalQuestion.getRawQuery());
				bw.write("\n"+externalQuestion.getUrl()+"\n\n");
				List<Evaluation> evaluations = pitBotService.getEvaluationByPhaseAndRelatedPost(externalQuestion.getId(),referencePhaseNumber);
				
				Iterator<Evaluation> it = evaluations.iterator();
				
				while (it.hasNext()) {
					Evaluation eval1 = it.next();
					String link = "https://stackoverflow.com/questions/"+eval1.getPostId()+"/ ";
					bw.write(link);	
					if(it.hasNext()) {
						Evaluation eval2 = it.next();
						if(eval1.getPostId().equals(eval2.getPostId())) {
							bw.write(";"+eval1.getLikertScale()+";"+eval2.getLikertScale());
							if(Math.abs(eval1.getLikertScale()-eval2.getLikertScale())>1) { //if the difference is high, review... 
								bw.write(";xxx;xxx");
							}else {
								bw.write(";"+eval1.getLikertScale()+";"+eval2.getLikertScale());
							}
							bw.write("\n");
							
						}else {
							bw.write("; ---- waiting---- \n");
						}
						
					}
					
				}
					
				bw.write("\n\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
		
	}





	private void runPhase5() {
		// TODO Auto-generated method stub
		
	}



	private void runPhase6() {
		
		
	}
	
	private void runPhase7() {
		// TODO Auto-generated method stub
		
	}
	

	
	private void step1() throws Exception {
		
		externalQuestions = botUtils.readExternalQuestionsAndAnswers(runRack,obs);
	
	}

	private void runSteps2to6(String query) throws Exception {
		
		
		if(runRack){
			/*
			 * Step 2: API Classes Extraction
			 * RACK  
			 *   *** Considering only the first query
			 */
			//initTime = System.currentTimeMillis();
			rackApis = step2(query);
			//botUtils.reportElapsedTime(initTime,"step2 - RACK ");
		}
		
		
		/*
		 * Step 3: Query Preparation
		 * 
		 */
		googleQuery = step3(rackApis,query);
		
		
		Set<Integer> soPostsIds = null;
		if(runGoogleSearch) {
		/*
		 * Step 4: Query Serach
		 * 
		 */
		//initTime = System.currentTimeMillis();
		soPostsIds = step4(googleQuery);
		//botUtils.reportElapsedTime(initTime,"step4 - Google Search ");
		
		}else { //static list for tests
			soPostsIds = botUtils.getStaticIdsForTests();
		}
		
		
		/*
		 * Step 5: Fetch Questions Content in SO
		 * 
		 */
		//initTime = System.currentTimeMillis();
		Set<SoThread> threads = step5(soPostsIds);
		//botUtils.reportElapsedTime(initTime,"step5 - Fetch Questions Content in SO ");
		
		
		/*
		 * Step 6: Links Retrieval
		 * 
		 */
		//initTime = System.currentTimeMillis();
		augmentedThreads = step6(threads);
		//botUtils.reportElapsedTime(initTime,"step6 - Links Retrieval ");
		
		
		
		
	}

	
	

	

	/**
	 * Rack tool
	 * @param query
	 * @return List<String> representing the associated classes
	 * @throws PitBotException 
	 */
	public List<String> step2(String query){
		//list is not null. It has been already verified.
		logger.info("RACK: discovering related classes to query: "+query);
		CodeTokenProvider ctProvider = new CodeTokenProvider(query);
		List<String> apis = ctProvider.recommendRelevantAPIs();
		if(apis.size()<numberOfRackClasses){
			logger.warn("The number of retrieved APIs from RACK is lower than the number of rack classes set as paramter. Ajusting the parameter to -->"+apis.size()+" apis, returned by RACK for this query.");
			numberOfRackClasses = apis.size();
		}
		
		apis = apis.subList(0, numberOfRackClasses);
		logger.info("Finished... discored classes:"+ apis.stream().limit(numberOfRackClasses).collect(Collectors.toList()));
		return apis;
	}

	
	
	
	public String step3(List<String> apis, String query) {
		String completeQuery = "";
		
		Boolean containJavaToken = Pattern.compile(".*\\bjava\\b.*").matcher(query.toLowerCase()).find();
		
		if(!containJavaToken) {
			completeQuery += "java ";
		}
		
		completeQuery += query;
						
		if(runRack) {
			for(int i=0; i<numberOfRackClasses;i++) {
				completeQuery += " "+ apis.get(i);
			}
			completeQuery = BotUtils.removeDuplicatedTokens(completeQuery," ");
		}
		
		
		return completeQuery;
	}



	/**
	 * Google Search
	 * @param googleQuery
	 * @return Set<Integer>
	 */
	public Set<Integer> step4(String googleQuery) {
		logger.info("Initiating Google Search... Using query: "+googleQuery);
		Set<Integer> soQuestionsIds = new LinkedHashSet<>();
		
		try {
			SearchQuery searchQuery = new SearchQuery(googleQuery, "stackoverflow.com", numberOfGoogleResults);
			        //.site("https://stackoverflow.com")
			SearchResult result = googleWebSearch.search(searchQuery,useProxy);
			List<String> urls = result.getUrls();
			BotUtils.identifyQuestionsIdsFromUrls(urls,soQuestionsIds);
						
		} catch (Exception e) {
			System.out.println("Error ... "+e);
			e.printStackTrace();
		}
		
		/*for(Integer soQuestionId: soQuestionsIds) {
			logger.info("Id: "+soQuestionId);
		}*/
	
		return soQuestionsIds;
		
	}


	



	/**
	 * Fetch Questions Content in SO ( the related SO question from SO together with their answers and comments).
	 * @param soQuestionsIds
	 * @return a list of threads, where each thread is a question with all their answers and comments.
	 */
	public Set<SoThread> step5(Set<Integer> soQuestionsIds) {
		return pitBotService.assembleListOfThreads(soQuestionsIds,RelationTypeEnum.FROM_GOOGLE_QUESTION_OR_ANSWER_T5.getId());
		
	}



	/**
	 * Links Retrieval
	 * @param threads
	 * @return an augmented list of threads
	 */
	public Set<SoThread> step6(Set<SoThread> threads) {
		logger.info("Number of threads before: "+threads.size());
		
		for(SoThread thread: threads) {
			allQuestionsIds.add(thread.getQuestion().getId());
		}
		logger.info("Number of questions before: "+allQuestionsIds.size());
		
		/*
		 * Verify if questions ids are in PostLinks table. If so, retrieve the related posts.
		 * LinkTypeId - type of link (currently either 1, linked, or 3, dupe)
		 */
		Set<Integer> allRelatedDupesIds = pitBotService.recoverRelatedQuestionsIds(allQuestionsIds,3);
		Set<Integer> otherRelatedIds = pitBotService.recoverRelatedQuestionsIds(allQuestionsIds,1);
		
		String relatedIdsStr = "";
		String otherRelatedIdsStr = "";
		for(Integer relatedId: allRelatedDupesIds) {
			relatedIdsStr+= relatedId+ " - ";
		}
		for(Integer relatedId: otherRelatedIds) {
			otherRelatedIdsStr+= relatedId+ " - ";
		}
		logger.info("Number of allRelatedDupesIds: "+allRelatedDupesIds.size()+ " \n List: "+relatedIdsStr);
		logger.info("Number of otherRelatedIdsStr: "+otherRelatedIds.size()+ " \n List: "+otherRelatedIdsStr);
				
		allRelatedDupesIds.removeAll(allQuestionsIds);
		otherRelatedIds.removeAll(allQuestionsIds);
		logger.info("Number of remaing threads to assemble: dupes: "+allRelatedDupesIds.size() + " + others: "+otherRelatedIds.size());
		
		Set<SoThread> relatedDupeThreads = pitBotService.assembleListOfThreads(allRelatedDupesIds,RelationTypeEnum.RELATED_DUPE_T2.getId());
		Set<SoThread> otherRelatedThreads = pitBotService.assembleListOfThreads(otherRelatedIds,RelationTypeEnum.RELATED_NOT_DUPE_T3.getId());
		
		logger.info("Number of new threads: dupes: "+relatedDupeThreads.size()+ " + others: "+otherRelatedThreads.size());
		
		threads.addAll(relatedDupeThreads);
		threads.addAll(otherRelatedThreads);
		
		logger.info("First total number of threads: "+threads.size()+" obtained only by PostLinks table. Now fetching links inside texts to assemble new threads...");
		
		
		/*
		 * Now check for links inside the texts of all posts and comments
		 */
		Set<Post> allPostsFromAllThreads = new HashSet<>();
		Set<Comment> allCommentsFromAllThreads = new HashSet<>();
		Set<Integer> soQuestionsIdsInsideTexts = new HashSet<>();
		
		for(SoThread thread: threads){
			allPostsFromAllThreads.add(thread.getQuestion());
			allCommentsFromAllThreads.addAll(thread.getQuestion().getComments());
			
			List<Post> answers = thread.getAnswers();
			for(Post answer: answers) {
				allPostsFromAllThreads.add(answer);
				allCommentsFromAllThreads.addAll(answer.getComments());
				
			}
		}
		
		List<String> textsToVerify = new ArrayList<>();
		for(Post post: allPostsFromAllThreads) {
			/*if(post.getId().equals(10975386)){
				logger.info("Good post is here: "+post);
			}*/
			textsToVerify.add(post.getBody());
		}
		for(Comment comment: allCommentsFromAllThreads) {
			textsToVerify.add(comment.getText());
		}
		
		logger.info("Number of texts to extract links from: "+textsToVerify.size());
		for(String text: textsToVerify) {
			List<String> links = botUtils.getCodeValues(BotUtils.LINK_PATTERN, text);
			BotUtils.identifyQuestionsIdsFromUrls(links, soQuestionsIdsInsideTexts);
			
		}
		logger.info("Number of recovered questions from URLs: "+soQuestionsIdsInsideTexts.size());
		
		logger.info("Number of questions ids identified inside links: "+soQuestionsIdsInsideTexts.size());
		
		Set<SoThread> newThreadsAssembledFromLinks = pitBotService.assembleListOfThreads(soQuestionsIdsInsideTexts,RelationTypeEnum.LINKS_INSIDE_TEXTS_T6.getId());
		logger.info("Number of new Threads built from link's ids: "+newThreadsAssembledFromLinks.size());
		
		logger.info("Number of posts that are answers and had their parent thread fetched: "+pitBotService.getCountPostIsAnAnswer());
		
		threads.addAll(newThreadsAssembledFromLinks);
		logger.info("Second total number of threads: "+threads.size()+" as a result of all discovered threads...");
		
		logger.info("Total number of parent posts stored in cache: "+botUtils.getParentPostsCache().size());
		logger.info("Total number of answer posts stored in cache: "+botUtils.getAnswerPostsCache().size());
		
		return threads;
	}



	
	

	

	private void showBucketsOrderByCosineDesc(List<Bucket> bucketsList) {
		Collections.sort(bucketsList, new Comparator<Bucket>() {
		    public int compare(Bucket o1, Bucket o2) {
		        return o2.getCosSim().compareTo(o1.getCosSim());
		    }
		});
		int pos=1;
		for(Bucket bucket: bucketsList){
			logger.info("Rank: "+(pos)+ " cosine: "+bucket.getCosSim()+" id: "+bucket.getPostId()+ " -\n "+bucket.getPresentingBody());
			pos++;
			if(pos>10){
				break;
			}
		}
		
	}
	

	/*private void showRankedList(List<Bucket> rankedBuckets) {
		int pos=0;
		for(Bucket bucket: rankedBuckets){
			logger.info("Rank: "+(pos+1)+ " total Score: "+bucket.getComposedScore() +" - cosine: "+bucket.getCosSim()+ " - coverageScore: "+bucket.getCoverageScore()+ " - codeSizeScore: "+bucket.getCodeSizeScore() +" - repScore: "+bucket.getRepScore()+ " - upScore: "+bucket.getUpScore()+ " - id: "+bucket.getPostId()+ " \n "+bucket.getPresentingBody());
			buildOutPutFile(bucket,pos+1);
			pos++;
			if(pos==20){
				break;
			}
		}
	}*/

	


	

	

	/*private Set<Post> getPostsFromThreads(Set<SoThread> threads) {
		Set<Post> allPosts = new HashSet<>();
		for(SoThread thread: threads){
			
			
		}
		return allPosts;
	}*/



	


	private void getPropertyValueFromLocalFile() {
		Properties prop = new Properties();
		InputStream input = null;
		useProxy = false;
		try {

			input = new FileInputStream(pathFileEnvFlag);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			String useProxyStr = prop.getProperty("useProxy");
			if(!StringUtils.isBlank(useProxyStr)) {
				useProxy = new Boolean(useProxyStr);
			}
			String msg = "\nEnvironment property is (useProxy): ";
			if(useProxy) {
				msg+= "with proxy";
			}else {
				msg+= "no proxy";
			}
			
			logger.info(msg);
			

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	

	public void initializeVariables() {
		allQuestionsIds = new HashSet<>();
		allAnwersIds = new HashSet<>();
		allCommentsIds = new HashSet<>();
		avgScore = 0d;
		avgReputation = 0d;
		rackApis = new ArrayList<>();
		googleQuery="";
	}


	


	
	
	
	
	
	
	/*private void performStemStop() throws Exception {
		List<ProcessedPostOld> tmpList = new ArrayList<>();
		tmpList.addAll(processedPostsByFilter);
		
		List<ProcessedPostOld> somePosts = new ArrayList<>(); 
		
		int maxQuestions = 1000;
		int init=0;
		int end= maxQuestions;
		int remainingSize = tmpList.size();
		logger.info("all questions ids:  "+remainingSize);
		while(remainingSize>maxQuestions){ //this is for memory issues
			somePosts = tmpList.subList(init, end);
			pitBotService.stemStop(somePosts);
			remainingSize = remainingSize - maxQuestions;
			init+=maxQuestions;
			end+=maxQuestions;
			if(init%50000==0) {
				logger.info("stemming...:  "+init);
			}
		}
		somePosts = tmpList.subList(init, init+remainingSize);
		//remaining
		pitBotService.stemStop(somePosts);
				
		tmpList = null;
	}*/

	
	private void buildMatrixForKappa(List<Evaluation> evaluationsWithBothUsersScales, String fileNameGeneratedMatrix) throws IOException {
		BufferedWriter bw =null;
		try {
		
			bw = new BufferedWriter(new FileWriter(fileNameGeneratedMatrix));
			bw.write(";;1;2;3;4;5");
			
			//Matrix map
			int[] cells[] = new int[5][5];
			
			for(int i=0; i<5; i++) {
				bw.write("\n;"+(i+1)+";");
				for(int j=0; j<5; j++) {
					//System.out.println(cells[i][j]);
					cells[i][j] = getCellNumber(i+1,j+1,evaluationsWithBothUsersScales);
					//System.out.println("cell "+i+"-"+j+"= "+cells[i][j]);
					bw.write(cells[i][j]+";");
				}
			}
			
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
		
	}





	private int getCellNumber(int i, int j, List<Evaluation> allEvaluations) {
		int sum=0;
		for(Evaluation evaluation: allEvaluations) {
			if(evaluation.getLikertScaleUser1()==i && evaluation.getLikertScaleUser2()==j) {
				sum+=1;				
			}
			
			
		}
		
		return sum;
	}


	
	
}
