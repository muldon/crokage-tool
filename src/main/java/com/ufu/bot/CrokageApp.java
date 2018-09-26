package com.ufu.bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	
	@Value("${callBIKERProcess}")
	public Boolean callBIKERProcess;
	
	@Value("${callNLP2ApiProcess}")
	public Boolean callNLP2ApiProcess;
	
	@Value("${callRACKApiProcess}")
	public Boolean callRACKApiProcess;
		
	@Value("${dataSet}")
	public String dataSet;

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
	private Map<String,Set<String>> nlp2ApiQueriesApis;

	@PostConstruct
	public void init() throws Exception {

		logger.info("Initializing CrokageApp app...");
		// initializeVariables();
		// botUtils.initializeConfigs();
		getPropertyValueFromLocalFile();
		subAction = subAction !=null ? subAction.toLowerCase().trim(): null;
		dataSet = dataSet !=null ? dataSet.toLowerCase().trim(): null;
		
		logger.info("\nConsidering parameters: \n" 
				+ "\n action: " + action 
				+ "\n subAction: " + subAction 
				+ "\n callBIKERProcess: " + callBIKERProcess
				+ "\n callNLP2ApiProcess: " + callNLP2ApiProcess
				+ "\n callRACKApiProcess: " + callRACKApiProcess
				+ "\n pathFileEnvFlag: " + pathFileEnvFlag 
				+ "\n useProxy: " + useProxy 
				+ "\n numberOfAPIClasses: " + numberOfAPIClasses 
				+ "\n bikerOnlyClasses: " + bikerOnlyClasses
				+ "\n limitQueries: " + limitQueries
				+ "\n dataSet: " + dataSet
				+ "\n obs: " + obs 
				+ "\n");

		switch (action) {
		
		case "generateMetricsForApiExtractors":
			generateMetricsForApiExtractors();
			break;	
		
		case "generateInputQueriesFromExcelGroudTruth":
			generateInputQueriesFromExcelGroudTruth();
			break;
		
		case "extractAPIsFromRACK":
			extractAPIsFromRACK();
			break;
		
		case "extractAPIsFromBIKER":
			extractAPIsFromBIKER();
			break;
			
		case "extractAPIsFromNLP2Api":
			extractAPIsFromNLP2Api();
			break;	
		
		case "loadExcelGroundTruthQuestionsAndLikerts":
			loadExcelGroundTruthQuestionsAndLikerts();
			break;
			
		
			
		case "generateInputQueriesFromNLP2ApiGroudTruth":
			generateInputQueriesFromNLP2ApiGroudTruth();
			break;		
	
		default:
			break;
		}

	}

	

	

	private void generateInputQueriesFromNLP2ApiGroudTruth() throws Exception {
		List<String> inputQueries = new ArrayList<>();
		Map<String,Set<String>> nlp2ApiQueriesApis = getQueriesAndApisFromFile(CrokageStaticData.NLP2API_GOLD_SET_FILE);
				
		Set<Entry<String, Set<String>>> entries = nlp2ApiQueriesApis.entrySet();
		for(Entry<String, Set<String>> entry: entries) {
			inputQueries.add(entry.getKey());
		}
		Path queriesFile = Paths.get(CrokageStaticData.INPUT_QUERIES_FILE_NLP2API);
		Files.write(queriesFile, inputQueries, Charset.forName("UTF-8"));
		
		
	}





	private Map<String, Set<String>> extractAPIsFromNLP2Api() throws Exception {
		
		try {
			
			if(queries==null) {
				queries = readInputQueries();
			}
			
			//queries = queries.subList(0, 5);
			
			if(callNLP2ApiProcess) {
				//First generate inputQueries file in a format NLP2Api understand
				FileWriter fw = new FileWriter(CrokageStaticData.NLP2API_INPUT_QUERIES_FILE);
				for (String query: queries) {
					fw.write(query+"\n--\n"); //specific format to NLP2API understand
					
				}
			 	fw.close();
				
				
		 		//call jar with parameters
			 	//java -jar /home/rodrigo/projects/bot/myNlp2Api.jar -K 10 -task reformulate -queryFile /home/rodrigo/projects/NLP2API-Replication-Package/NL-Query+GroundTruth.txt -outputFile /home/rodrigo/projects/NLP2API-Replication-Package/nlp2apiQueriesOutput.txt
				
			 	String jarPath = CrokageStaticData.CROKAGE_HOME;
				List<String> command = new ArrayList<String>();
			    
			    command.add("java");
			    command.add("-jar");
			    command.add(jarPath+"/myNlp2Api.jar");
			    command.add("-K");
			    command.add(""+numberOfAPIClasses);
			    command.add("-task");
			    command.add("reformulate");
			    command.add("-queryFile");
			    command.add(CrokageStaticData.NLP2API_INPUT_QUERIES_FILE);
			    command.add("-outputFile");
			    command.add(CrokageStaticData.NLP2API_OUTPUT_QUERIES_FILE);
				
				ProcessBuilder pb = new ProcessBuilder(command);
				Process p = pb.start();
				p.waitFor();
				String output = CrokageUtils.loadStream(p.getInputStream());
				String error = CrokageUtils.loadStream(p.getErrorStream());
				int rc = p.waitFor();
				System.out.println("Process ended with rc=" + rc);
				System.out.println("\nStandard Output:\n");
				System.out.println(output);
				//String apis[] = output.replaceAll("\n", " ").split(" ");
				//System.out.println(apis);
				System.out.println("\nStandard Error:\n");
				System.out.println(error);
		 	}
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		Map<String,Set<String>> nlp2ApiQueriesApis = getQueriesAndApisFromFile(CrokageStaticData.NLP2API_OUTPUT_QUERIES_FILE);
		return nlp2ApiQueriesApis;
		
	}
	
	private Map<String, Set<String>> getQueriesAndApisFromFile(String fileName) throws IOException {
		Map<String,Set<String>> queriesApis = new LinkedHashMap<>();
		// reading output from generated file
		List<String> queriesAndApis = Files.readAllLines(Paths.get(fileName), Charsets.UTF_8);
		Iterator<String> it = queriesAndApis.iterator();
		
		while(it.hasNext()) {
			String query = "";
			String queryApis = "";
			if(it.hasNext()) {
				query = it.next();
				queryApis = it.next(); //APIs are in even lines
			}
			
			List<String> rankedApis = Arrays.asList(queryApis.split(" ")).stream().map(String::trim).collect(Collectors.toList());
			int k = numberOfAPIClasses;
			if (rankedApis.size() < k) {
				logger.warn("The number of retrieved APIs for query is lower than the number of rack classes set as parameter. Ajusting the parameter to -->" + rankedApis.size() + " apis");
				k = rankedApis.size();
			}
			rankedApis = rankedApis.subList(0, k);
			
			Set<String> rankedApisSet = new LinkedHashSet<String>(rankedApis);
			Set<String> generatedClasses = new LinkedHashSet<>();
			for(String api: rankedApisSet) {
				generatedClasses.add(api); 
			}
			queriesApis.put(query.trim(), generatedClasses);
			
			logger.info("discovered classes for query: "+query+ " ->  " + queriesApis.get(query.trim()));
		}
		
		
		return queriesApis;
	}





	private Map<String,Set<String>> extractAPIsFromBIKER() throws Exception {
		// BIKER
		if(queries==null) {
			queries = readInputQueries();
		}
		
		// writing queries to be read by biker
		Map<String,Set<String>> bikerQueriesApis = new LinkedHashMap<>();
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

		if(callBIKERProcess) {
			try {
				ProcessBuilder pb = new ProcessBuilder(CrokageStaticData.BIKER_SCRIPT_FILE);
				Process p = pb.start();
				p.waitFor();
				String output = CrokageUtils.loadStream(p.getInputStream());
				String error = CrokageUtils.loadStream(p.getErrorStream());
				int rc = p.waitFor();
				System.out.println("Process ended with rc=" + rc);
				System.out.println("\nStandard Output:\n");
				System.out.println(output);
				System.out.println("\nStandard Error:\n");
				System.out.println(error);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		

		// reading output from BIKER
		List<String> queriesWithApis = Files.readAllLines(Paths.get(CrokageStaticData.BIKER_OUTPUT_QUERIES_FILE), Charsets.UTF_8);
		for(String generatedLine: queriesWithApis) {
			String parts[] = generatedLine.split("=  ");
			List<String> rankedApis = Arrays.asList(parts[1].split("### ")).stream().map(String::trim).collect(Collectors.toList());
			rankedApis.remove("");
			
			int k = numberOfAPIClasses;
			if (rankedApis.size() < k) {
				logger.warn("The number of retrieved APIs from BIKER is lower than the number of rack classes set as parameter. Ajusting the parameter to -->" + rankedApis.size() + " apis, returned by BIKER for this query.");
				k = rankedApis.size();
			}
			rankedApis = rankedApis.subList(0, k);
			Set<String> rankedApisSet = new LinkedHashSet<String>(rankedApis);
			
			if(bikerOnlyClasses) {
				Set<String> bikerClasses = new LinkedHashSet<>();
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

	private Map<String,Set<String>> extractAPIsFromRACK() throws Exception {
		
		if(queries==null) {
			queries = readInputQueries();
		}
		Map<String,Set<String>> rackQueriesApis = new LinkedHashMap<>();
		
		if(callRACKApiProcess) {
			try (PrintWriter out = new PrintWriter(CrokageStaticData.RACK_CACHE_QUERIES_DIR)) {
				for(String query: queries) {
					Set<String> apis = getRackApisForQuery(query);
					rackQueriesApis.put(query, apis);
					logger.info("Rack - discovered classes for query: "+query+ " ->  " + apis.stream().limit(numberOfAPIClasses).collect(Collectors.toList()));
					String joinedApis = String.join(" ", apis);
					out.write(query+"\n"+joinedApis+"\n");
				}
			}
			
		}else { //get from cache
			// reading output from RACK generated file
			rackQueriesApis = getQueriesAndApisFromFile(CrokageStaticData.RACK_CACHE_QUERIES_DIR);
			
		}
		
		
		logger.info("RACK Finished...");
		return rackQueriesApis;
	}

	private void generateMetricsForApiExtractors() throws Exception {
		if(subAction.equals("all")) {
			rackQueriesApis = extractAPIsFromRACK();
			bikerQueriesApis = extractAPIsFromBIKER();
			nlp2ApiQueriesApis = extractAPIsFromNLP2Api();
		}else if(subAction.equals("biker")) {
			bikerQueriesApis = extractAPIsFromBIKER();
		}else if(subAction.equals("rack")) {
			rackQueriesApis = extractAPIsFromRACK();
		}else {
			nlp2ApiQueriesApis = extractAPIsFromNLP2Api();
		}
		
		Map<String, Set<String>> goldSetQueriesApis = getGoldSetQueriesApis();
		Map<String, Set<String>> recommendedApis = getRecommendedApis();
		analyzeResults(recommendedApis,goldSetQueriesApis);
		
	}


	private Map<String, Set<String>> getGoldSetQueriesApis() throws IOException {
		Map<String, Set<String>> goldSetQueriesApis = new LinkedHashMap<>();
		
		if(dataSet.equals("crokage")) {
			List<UserEvaluation> evaluationsList = loadExcelGroundTruthQuestionsAndLikerts();
			goldSetQueriesApis.putAll(getCrokageGoldSetByEvaluations(evaluationsList));
	
		}else if(dataSet.equals("nlp2api")) {
			goldSetQueriesApis.putAll(getQueriesAndApisFromFile(CrokageStaticData.NLP2API_GOLD_SET_FILE));
		}
		
		return goldSetQueriesApis;
	}









	private void generateInputQueriesFromExcelGroudTruth() throws FileNotFoundException {
		String file1 = "questionsLot1and2withoutWeightsAdjuster.xlsx";
		Integer likertsResearcher1AfterAgreementColumn = 3;
		Integer likertsResearcher2AfterAgreementColumn = 4;
		
		List<UserEvaluation> evaluationsList = new ArrayList<>();
		List<String> inputQueries = new ArrayList<>();
		crokageUtils.readXlsxToEvaluationList(evaluationsList,file1,likertsResearcher1AfterAgreementColumn,likertsResearcher2AfterAgreementColumn,inputQueries);
		//System.out.println(inputQueries);
		
		try (PrintWriter out = new PrintWriter(CrokageStaticData.INPUT_QUERIES_FILE_CROKAGE)) {
		    for(String query: inputQueries) {
		    	out.println(query);
		    }
			
		}
		
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
		return new LinkedHashSet<>(apis);
	}

	private List<String> readInputQueries() throws Exception {
		String fileName = "";
		if(dataSet.equals("crokage")) {
			fileName = CrokageStaticData.INPUT_QUERIES_FILE_CROKAGE;
	
		}else if(dataSet.equals("nlp2api")) {
			fileName = CrokageStaticData.INPUT_QUERIES_FILE_NLP2API;
		}
		
		
		// read queries from file
		File inputQueriesFile = new File(fileName);
		if(!inputQueriesFile.exists()) { 
		    //generateInputQueriesFromExcelGroudTruth();
			throw new Exception("Input queries file needed...");
		}
		
		List<String> queries = FileUtils.readLines(inputQueriesFile, "utf-8");
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
	
	
	private Map<String, Set<String>> getCrokageGoldSetByEvaluations(List<UserEvaluation> evaluationsWithBothUsersScales) {
		//BufferedWriter bw =null;
		//Map<Integer,Integer> externalQuestionOracleCounter = new HashMap<>();
		long initTime = System.currentTimeMillis();
		Map<String,Set<String>> goldSetQueriesApis = new HashMap<>();
		try {
			//Set<ExternalQuery> consideredExternalQueries = new HashSet();
			/*List<UserEvaluation> consideredEvaluations = new ArrayList<>();
			List<UserEvaluation> goldSetEvaluations = new ArrayList<>();
			Set<ExternalQuery> goldSetExternalQueries = new HashSet();
			Map<Integer,Integer> externalQueryOracleCounter = new HashMap<>();*/
			
			int assessed = 0;
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
					
					Post post = crokageService.findPostById(evaluation.getPostId());
					Set<String> codeSet = crokageUtils.cleanCode(post.getBody());
					//ArrayList<String> codeClasses = new ArrayList(codeSet);
					if(codeSet.isEmpty()) {
						continue;
					}
					
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
				//+ "\nConsidered external questions having evaluations with difference <=1: "+consideredExternalQueries.size()
					);
			
			//consideredExternalQueries= null;
			//consideredEvaluations = null;
	
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//bw.close();
		}
		
		crokageUtils.reportElapsedTime(initTime,"generateMetricsForEvaluations");
		
		return goldSetQueriesApis;
	}
	
	
	private Map<String, Set<String>> getRecommendedApis() {
		Map<String, Set<String>> recommendedApis = new LinkedHashMap<>();
		
		if(rackQueriesApis!=null && bikerQueriesApis!=null && nlp2ApiQueriesApis!=null) { //all approaches
			Set<String> queries = rackQueriesApis.keySet();
			List<String> queriesList = new ArrayList<>(queries);
			
			for(String currentQuery: queriesList) {
				Set<String> recommendedApisSet = new LinkedHashSet<>();
				Set<String> bikerApisSet = bikerQueriesApis.get(currentQuery);
				Set<String> rackApisSet = rackQueriesApis.get(currentQuery);
				Set<String> nlpApisSet = nlp2ApiQueriesApis.get(currentQuery);
				
				Iterator<String> bikerIt = bikerApisSet.iterator();
				Iterator<String> rackIt = rackApisSet.iterator();
				Iterator<String> nlpIt = nlpApisSet.iterator();
				
				for(int i=0; i<numberOfAPIClasses; i++) { //collect the ith API from each approach at a time
					
					if(bikerIt.hasNext()) {
						recommendedApisSet.add(bikerIt.next());
					}
					if(nlpIt.hasNext()) {
						recommendedApisSet.add(nlpIt.next());
					}
					if(rackIt.hasNext()) {
						recommendedApisSet.add(rackIt.next());
					}
				}
				recommendedApis.put(currentQuery, recommendedApisSet);
				
			}
			
			
		}else if(rackQueriesApis!=null) {
			recommendedApis.putAll(rackQueriesApis);
		}else if(bikerQueriesApis!=null) {
			recommendedApis.putAll(bikerQueriesApis);
		}else {
			recommendedApis.putAll(nlp2ApiQueriesApis);
		}
		return recommendedApis;
	}





	public void analyzeResults(Map<String, Set<String>> recommendedApis,Map<String, Set<String>> goldSetQueriesApis) {
		int hitK = 0;
		int correct_sum = 0;
		double rrank_sum = 0;
		double precision_sum = 0;
		double preck_sum = 0;
		double recall_sum = 0;
		double fmeasure_sum = 0;
		int k = numberOfAPIClasses;
		
		try {
			
			for (String keyQuery : goldSetQueriesApis.keySet()) {
				List<String> rapis = new ArrayList<>(recommendedApis.get(keyQuery));
				/*int K = rapis.size() < numberOfAPIClasses ? rapis.size() : numberOfAPIClasses;
				rapis = rapis.subList(0, K);*/
				ArrayList<String> gapis = new ArrayList<>(goldSetQueriesApis.get(keyQuery));
			
				hitK = hitK + isApiFound_K(rapis, gapis);
				rrank_sum = rrank_sum + getRRank(rapis, gapis);
				double preck = 0;
				preck = getAvgPrecisionK(rapis, gapis);
				preck_sum = preck_sum + preck;
				double recall = 0;
				recall = getRecallK(rapis, gapis);
				recall_sum = recall_sum + recall;
				//System.out.println(hitK);

			}

				
			
			logger.info("Results: \n"
					//"\nMR@" + numberOfAPIClasses + ": " + BotUtils.round(recall_sum / goldSetQueriesApis.size(),4)
					+"\nHit@" + k + ": " + (double) hitK / goldSetQueriesApis.size()
					+ "\nMRR@" + k + ": " + rrank_sum / goldSetQueriesApis.size()
					+ "\nMAP@" + k + ": " + preck_sum / goldSetQueriesApis.size() 
					+ "\nMR@" + k + ": " + recall_sum / goldSetQueriesApis.size()
					+ "");
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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

	
	protected int isApiFound_K(List<String> rapis, ArrayList<String> gapis) {
		// check if correct API is found
		int found = 0;
		outer: for (int i = 0; i < rapis.size(); i++) {
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
	

	protected int isApiFound_K(List<String> rapis, ArrayList<String> gapis, int K) {
		// check if correct API is found
		K = rapis.size() < K ? rapis.size() : K;
		int found = 0;
		outer: for (int i = 0; i < K; i++) {
			String api = rapis.get(i);
			if(api.equals("PrintWriter")) {
				System.out.println();
			}
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








	
	protected double getRecallK(List<String> rapis, ArrayList<String> gapis, int K) {
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
	
	
	protected boolean isApiFound(String api, List<String> gapis) {
		// check if the API can be found
		for (String gapi : gapis) {
			/*if (gapi.endsWith(api) || api.endsWith(gapi)) {*/
			if (gapi.equals(api)) {
				return true;
			}
		}
		return false;
	}
	
	
	protected double getRRank(List<String> rapis, ArrayList<String> gapis) {
		double rrank = 0;
		for (int i = 0; i < rapis.size(); i++) {
			String api = rapis.get(i);
			if (isApiFound(api, gapis)) {
				rrank = 1.0 / (i + 1);
				break;
			}
		}
		return rrank;
	}
	
	
	
	protected double getAvgPrecisionK(List<String> rapis, ArrayList<String> gapis) {
		double linePrec = 0;
		double found = 0;
		for (int index = 0; index < rapis.size(); index++) {
			String api = rapis.get(index);
			if (isApiFound(api, gapis)) {
				found++;
				linePrec += (found / (index + 1));
			}
		}
		if (found == 0)
			return 0;

		return linePrec / found;
	}

	protected double getRecallK(List<String> rapis, ArrayList<String> gapis) {
		// getting recall at K
		double found = 0;
		for (int index = 0; index < rapis.size(); index++) {
			String api = rapis.get(index);
			if (isApiFound(api, gapis)) {
				found++;
			}
		}
		return found / gapis.size();
	}
	

}
