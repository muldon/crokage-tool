package com.ufu.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.ufu.bot.googleSearch.GoogleWebSearch;
import com.ufu.bot.service.CrokageService;
import com.ufu.bot.to.Bucket;
import com.ufu.bot.to.Post;
import com.ufu.bot.util.BotUtils;
import com.ufu.crokage.config.CrokageStaticData;
import com.ufu.crokage.to.ExternalQuery;
import com.ufu.crokage.to.QueryApis;
import com.ufu.crokage.to.UserEvaluation;
import com.ufu.crokage.util.CrokageUtils;

import core.CodeTokenProvider;

@Component
public class CrokageApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CrokageService crokageService;

	@Autowired
	private CrokageUtils crokageUtils;

	@Autowired
	private GoogleWebSearch googleWebSearch;

	@Value("${action}")
	public String action;

	@Value("${subAction}")
	public String subAction;

	@Value("${obs}")
	public String obs;

	@Value("${numberOfAPIClasses}")
	public Integer numberOfAPIClasses;
	
	@Value("${limitQueries}")
	public Integer limitQueries;
	
	@Value("${bikerOnlyClasses}")
	public Boolean bikerOnlyClasses;
	

	/*
	 * Path to a file which contains a FLAG indicating if the environment is test or
	 * production. This file (environmentFlag.properties) contains only one line
	 * with a boolean value (useProxy = true|false) If useProxy = true, the proxy is
	 * not applied for the google search engine. Otherwise, proxy is set.
	 */
	@Value("${pathFileEnvFlag}")
	public String pathFileEnvFlag;

	/*
	 * Stores the value obtained from the pathFileEnvFlag file
	 */
	private Boolean useProxy;

	private long initTime;
	private long endTime;
	private Set<String> extractedAPIs;
	private List<String> queries;
	private Map<String,Set<String>> rackQueriesApis;
	private Map<String,Set<String>> bikerQueriesApis;

	@PostConstruct
	public void init() throws Exception {

		logger.info("Initializing CrokageApp app...");
		// initializeVariables();
		// botUtils.initializeConfigs();
		getPropertyValueFromLocalFile();

		logger.info("\nConsidering parameters: \n" 
				+ "\n action: " + action 
				+ "\n subAction: " + subAction 
				+ "\n pathFileEnvFlag: " + pathFileEnvFlag 
				+ "\n useProxy: " + useProxy 
				+ "\n numberOfAPIClasses: " + numberOfAPIClasses 
				+ "\n bikerOnlyClasses: " + bikerOnlyClasses
				+ "\n limitQueries: " + limitQueries
				+ "\n obs: " + obs 
				+ "\n");

		switch (action) {
		case "generateInputQueriesFromExcelGroudTruth":
			generateInputQueriesFromExcelGroudTruth();
			break;
		
		case "extractAPIsFromRACK":
			extractAPIsFromRACK();
			break;
		
		case "extractAPIsFromBIKER":
			extractAPIsFromBIKER();
			break;
		
		case "loadExcelGroundTruthQuestionsAndLikerts":
			loadExcelGroundTruthQuestionsAndLikerts();
			break;
			
		case "generateMetricsForApiExtractors":
			generateMetricsForApiExtractors();
			break;	

		default:
			break;
		}

	}

	private void generateInputQueriesFromExcelGroudTruth() throws FileNotFoundException {
		String file1 = "questionsLot1and2withoutWeightsAdjuster.xlsx";
		Integer likertsResearcher1AfterAgreementColumn = 3;
		Integer likertsResearcher2AfterAgreementColumn = 4;
		
		List<UserEvaluation> evaluationsList = new ArrayList<>();
		List<String> inputQueries = new ArrayList<>();
		crokageUtils.readXlsxToEvaluationList(evaluationsList,file1,likertsResearcher1AfterAgreementColumn,likertsResearcher2AfterAgreementColumn,inputQueries);
		System.out.println(inputQueries);
		
		try (PrintWriter out = new PrintWriter(CrokageStaticData.INPUT_QUERIES_FILE)) {
		    for(String query: inputQueries) {
		    	out.println(query);
		    }
			
		}
		
	}

	private Map<String,Set<String>> extractAPIsFromBIKER() throws IOException {
		// BIKER
		if(queries==null) {
			queries = readInputQueries();
		}
		
		// writing queries to be read by biker
		Map<String,Set<String>> bikerQueriesApis = new HashMap<>();
		Path bikerQueriesFile = Paths.get(CrokageStaticData.BIKER_INPUT_QUERIES_FILE);
		Files.write(bikerQueriesFile, queries, Charset.forName("UTF-8"));

		// writing script to be called
		Path scriptFile = Paths.get(CrokageStaticData.BIKER_SCRIPT_FILE);
		List<String> lines = Arrays.asList("export PYTHONPATH=" + CrokageStaticData.BIKER_HOME, "echo $PYTHONPATH", "cd $PYTHONPATH/main", "python " + CrokageStaticData.BIKER_RUNNER_PATH);
		Files.write(scriptFile, lines, Charset.forName("UTF-8"));
		File file = new File(CrokageStaticData.BIKER_SCRIPT_FILE);
		file.setExecutable(true);
		file.setReadable(true);
		file.setWritable(true);

		/*try {
			ProcessBuilder pb = new ProcessBuilder(CrokageStaticData.BIKER_SCRIPT_FILE);
			Process p = pb.start();
			p.waitFor();
			String output = loadStream(p.getInputStream());
			String error = loadStream(p.getErrorStream());
			int rc = p.waitFor();
			System.out.println("Process ended with rc=" + rc);
			System.out.println("\nStandard Output:\n");
			System.out.println(output);
			System.out.println("\nStandard Error:\n");
			System.out.println(error);
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		// reading output from BIKER
		List<String> queriesWithApis = Files.readAllLines(Paths.get(CrokageStaticData.BIKER_OUTPUT_QUERIES_FILE), Charsets.UTF_8);
		for(String generatedLine: queriesWithApis) {
			String parts[] = generatedLine.split("=  ");
			Set<String> rankedApisSet = new HashSet<String>(Arrays.asList(parts[1].split("### ")).stream().map(String::trim).collect(Collectors.toList()));
			/*if(!rankedApisSet.isEmpty() && rankedApisSet.contains("")) {
				rankedApisSet.remove("");
			}*/
			rankedApisSet.remove("");
			if(bikerOnlyClasses) {
				Set<String> bikerClasses = new HashSet<>();
				for(String api: rankedApisSet) {
					bikerClasses.add(api.split("\\.")[0]); 
				}
				bikerQueriesApis.put(parts[0].trim(), bikerClasses);
			}else {
				bikerQueriesApis.put(parts[0].trim(), rankedApisSet);
			}
			
			logger.info("Biker - discovered classes for query: "+parts[0]+ " ->  " + bikerQueriesApis.get(parts[0].trim()));
		}
		
		/*List<String> bikerClasses = new ArrayList<>();
		for(String api: list) {
			classes.add(api.split("\\.")[0]); 
		}*/
		
		logger.info("Biker finished... ");
		return bikerQueriesApis;
	}

	private Map<String,Set<String>> extractAPIsFromRACK() throws IOException {
		
		if(queries==null) {
			queries = readInputQueries();
		}
		Map<String,Set<String>> rackQueriesApis = new HashMap<>();
		
		// Rack
		for(String query: queries) {
			Set<String> apis = getRackApisForQuery(query);
			rackQueriesApis.put(query, apis);
			logger.info("Rack - discovered classes for query: "+query+ " ->  " + apis.stream().limit(numberOfAPIClasses).collect(Collectors.toList()));
		}
		
		logger.info("RACK Finished...");
		return rackQueriesApis;
	}

	private void generateMetricsForApiExtractors() throws Exception {
		//generateInputQueriesFromExcelGroudTruth();
		List<UserEvaluation> evaluationsList = loadExcelGroundTruthQuestionsAndLikerts();
		if(subAction.equals("ALL")) {
			rackQueriesApis = extractAPIsFromRACK();
			bikerQueriesApis = extractAPIsFromBIKER();
		}else if(subAction.equals("BIKER")) {
			bikerQueriesApis = extractAPIsFromBIKER();
		}else {
			rackQueriesApis = extractAPIsFromRACK();
		}
		
		generateMetricsForEvaluations(evaluationsList);
	}

	private List<UserEvaluation> loadExcelGroundTruthQuestionsAndLikerts() throws IOException {
		String file1 = "questionsLot1and2withoutWeightsAdjuster.xlsx";
		Integer likertsResearcher1AfterAgreementColumn = 3;
		Integer likertsResearcher2AfterAgreementColumn = 4;
		
		List<UserEvaluation> evaluationsList = new ArrayList<>();
		crokageUtils.readXlsxToEvaluationList(evaluationsList,file1,likertsResearcher1AfterAgreementColumn,likertsResearcher2AfterAgreementColumn,null);
		//generateMetricsForEvaluations(evaluationsList);
		return evaluationsList;
	}

	

	private HashSet<String> getRackApisForQuery(String query) {
		//logger.info("RACK: discovering related classes to query: " + query);
		CodeTokenProvider ctProvider = new CodeTokenProvider(query);
		List<String> apis = ctProvider.recommendRelevantAPIs();
		int k = numberOfAPIClasses;
		if (apis.size() < k) {
			logger.warn("The number of retrieved APIs from RACK is lower than the number of rack classes set as paramter. Ajusting the parameter to -->" + apis.size() + " apis, returned by RACK for this query.");
			k = apis.size();
		}

		apis = apis.subList(0, k);

		return new HashSet(apis);
	}

	private static String loadStream(InputStream s) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(s));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null)
			sb.append(line).append("\n");
		return sb.toString();
	}

	private List<String> readInputQueries() throws IOException {
		// read queries from file
		List<String> queries = FileUtils.readLines(new File(CrokageStaticData.INPUT_QUERIES_FILE), "utf-8");
		if(limitQueries!=null) {
			queries = queries.subList(0, limitQueries);
		}
		return queries;

	}

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
			if (!StringUtils.isBlank(useProxyStr)) {
				useProxy = new Boolean(useProxyStr);
			}
			String msg = "\nEnvironment property is (useProxy): ";
			if (useProxy) {
				msg += "with proxy";
			} else {
				msg += "no proxy";
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
	
	
	private void generateMetricsForEvaluations(List<UserEvaluation> evaluationsWithBothUsersScales) throws IOException {
		//BufferedWriter bw =null;
		//Map<Integer,Integer> externalQuestionOracleCounter = new HashMap<>();
		long initTime = System.currentTimeMillis();
		try {
			
			
			Set<ExternalQuery> consideredExternalQueries = new HashSet();
			List<UserEvaluation> consideredEvaluations = new ArrayList<>();
			List<UserEvaluation> goldSetEvaluations = new ArrayList<>();
			Set<ExternalQuery> goldSetExternalQueries = new HashSet();
			Map<Integer,Integer> externalQueryOracleCounter = new HashMap<>();
			Map<String,Set<String>> goldSetQueriesApis = new HashMap<>();
			
			int assessed = 0;
			int goldSet =0;
			int hits = 0;
			String query = null;
			
			for(UserEvaluation evaluation:evaluationsWithBothUsersScales){
				int likert1 = evaluation.getLikertScaleUser1();
				int likert2 = evaluation.getLikertScaleUser2();
				query = evaluation.getQuery();
				int diff = Math.abs(likert1 - likert2);
				if(diff>1) {
					continue;
				}
				assessed++;
				
				double meanFull = (likert1+likert2)/(double)2;
				double meanLikert=0d;
				meanLikert = BotUtils.round(meanFull,2);
				
				if(meanLikert>=4) { 
					goldSet++;
					Post post = crokageService.findPostById(evaluation.getPostId());
					Set<String> codeSet = crokageUtils.cleanCode(post.getBody());
					//ArrayList<String> codeClasses = new ArrayList(codeSet);
					
					if(goldSetQueriesApis.get(evaluation.getQuery())==null){
						goldSetQueriesApis.put(query, codeSet);
					}else {
						Set<String> currentClasses = goldSetQueriesApis.get(query);
						currentClasses.addAll(codeSet);
					}
					
				}
			}
			
			logger.info("\nTotal evaluations: "+evaluationsWithBothUsersScales.size()
				+ "\nConsidered with likert difference <=1: "+assessed	
				+ "\nConsidered external questions having evaluations with difference <=1: "+consideredExternalQueries.size()
					);
			
			int hitK = 0;
			int correct_sum = 0;
			double rrank_sum = 0;
			double precision_sum = 0;
			double preck_sum = 0;
			double recall_sum = 0;
			double fmeasure_sum = 0;
			int k = numberOfAPIClasses;
			
			Map<String, Set<String>> recommendedApis = new HashMap<>();
			if(rackQueriesApis!=null && bikerQueriesApis!=null) {
				recommendedApis.putAll(rackQueriesApis);
				
				for(String recommendedApi: bikerQueriesApis.keySet()) {
					Set<String> recommendedApisSet = recommendedApis.get(recommendedApi);  //must not be null
					recommendedApisSet.addAll(bikerQueriesApis.get(recommendedApi));
				}
				
			}else if(rackQueriesApis!=null) {
				recommendedApis.putAll(rackQueriesApis);
			}else if(bikerQueriesApis!=null) {
				recommendedApis.putAll(bikerQueriesApis);
			}
			
			for (String keyQuery : goldSetQueriesApis.keySet()) {
				
				try {
					
					ArrayList<String> rapis = new ArrayList<>(recommendedApis.get(keyQuery));
					ArrayList<String> gapis = new ArrayList<>(goldSetQueriesApis.get(keyQuery));
				
					double recall = 0;
					k = numberOfAPIClasses;
					/*recall = getRecallK(rapis, gapis, k);
					recall_sum = recall_sum + recall;
					*/
					hitK = hitK + isApiFound_K(rapis, gapis, k);
					

				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}

				
			
			logger.info(
					//"\nMR@" + numberOfAPIClasses + ": " + BotUtils.round(recall_sum / goldSetQueriesApis.size(),4)
					"\nHit@" + k + ": " + (double) hitK / goldSetQueriesApis.size()
					+ "");
			
			
			
			//RACK
			/*ArrayList<String> rackRecommendedQueries = new ArrayList(rackQueriesApis.get(evaluation.getQuery()));
			double recall = 0;
			recall = getRecallK(rackRecommendedQueries,codeClasses,codeClasses.size());
			recall_sum += recall;
			*/
			
			/*logger.info("\nHit@" + maxRankSize + ": " + BotUtils.round((double) hitK / goldSetExternalQueries.size(),4) 
						+ "\nMR@" + maxRankSize + ": " + BotUtils.round(recall_sum / goldSetExternalQueries.size(),4)
						+ "\nMRR@" + maxRankSize + ": " + BotUtils.round(rrank_sum / goldSetExternalQueries.size(),4)
						+ "\nMAP@" + maxRankSize + ": " + BotUtils.round(preck_sum / goldSetExternalQueries.size(),4)
						+ "\nAverageNdcgs@" + maxRankSize + ": " + 0
						+ "");*/
			
			
			consideredExternalQueries= null;
			consideredEvaluations = null;
	
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//bw.close();
		}
		
		crokageUtils.reportElapsedTime(initTime,"generateMetricsForEvaluations");
		
	}
	
	


	private double getAvgPrecisionK(List<Bucket> trimmedRankedList, List<UserEvaluation> goldSetEvaluations, Integer externalQuestionId) {
		int count=0;
		double found = 0;
		double linePrec = 0;
		for(Bucket bucket: trimmedRankedList) {
			count++;
			for(UserEvaluation evaluation: goldSetEvaluations) {
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


	protected int isApiFound_K(ArrayList<String> rapis, ArrayList<String> gapis, int K) {
		// check if correct API is found
		K = rapis.size() < K ? rapis.size() : K;
		int found = 0;
		outer: for (int i = 0; i < K; i++) {
			String api = rapis.get(i);
			for (String gapi : gapis) {
				//if (gapi.endsWith(api) || api.endsWith(gapi)) {
				if (gapi.equals(api)) {
					logger.info("Gold:"+gapi+"\t"+"Result:"+api);
					found = 1;
					break outer;
				}
			}
		}
		return found;
	}



	private double getRRank(List<Bucket> trimmedRankedList, List<UserEvaluation> goldSetEvaluations, Integer externalQuestionId) {
		double rrank = 0;
		int count=0;
		outer: for(Bucket bucket: trimmedRankedList) {
			count++;
			for(UserEvaluation evaluation: goldSetEvaluations) {
				if(evaluation.getExternalQuestionId().equals(externalQuestionId) && evaluation.getPostId().equals(bucket.getPostId())) {
					rrank = 1.0 / (count);
					break outer;
				}
			}
		}
		
		return rrank;
	}





	private double getRecallK(List<Bucket> trimmedRankedList, List<UserEvaluation> goldSetEvaluations, Integer externalQuestionId, int goldSetSize) {
		double found = 0;
		int rank = 0;
		for(Bucket bucket: trimmedRankedList) {
			rank++;
			for(UserEvaluation evaluation: goldSetEvaluations) {
				if(evaluation.getExternalQuestionId().equals(externalQuestionId) && evaluation.getPostId().equals(bucket.getPostId())) {
					found++;
					//logger.info("hit on rank: "+rank+ " -postid: "+bucket.getPostId());
				}
			}
		}
		
		return found/goldSetSize;
	}





	private int isRelevantPostFound(List<Bucket> trimmedRankedList, List<UserEvaluation> goldSetEvaluations, Integer externalQuestionId) {
		int found = 0;
		int rank = 0;
		outer: for(Bucket bucket: trimmedRankedList) {
			rank++;
			for(UserEvaluation evaluation: goldSetEvaluations) {
				if(evaluation.getExternalQuestionId().equals(externalQuestionId) && evaluation.getPostId().equals(bucket.getPostId())) {
					found = 1;
					//logger.info("hit on rank: "+rank);
					break outer;
				}
			}
		}
		
		return found;
	}

	
	protected double getRecallK(ArrayList<String> rapis, ArrayList<String> gapis, int K) {
		// getting recall at K
		K = rapis.size() < K ? rapis.size() : K;
		double found = 0;
		for (int index = 0; index < K; index++) {
			String api = rapis.get(index);
			if (isApiFound(api, gapis)) {
				found++;
			}
		}
		return found / gapis.size();
	}
	
	
	protected boolean isApiFound(String api, ArrayList<String> gapis) {
		// check if the API can be found
		for (String gapi : gapis) {
			if (gapi.endsWith(api) || api.endsWith(gapi)) {
				return true;
			}
		}
		return false;
	}
	
	

}
