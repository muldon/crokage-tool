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
import com.ufu.crokage.to.MetricResult;
import com.ufu.crokage.to.UserEvaluation;
import com.ufu.crokage.util.CrokageUtils;

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
	private Map<Integer,Set<String>> rackQueriesApis;
	//private Map<String,Set<String>> rackReformulatedQueriesApis;  // because output from rack is reformulated. This map contains the real output.
	private Map<Integer,Set<String>> bikerQueriesApis;
	private Map<Integer,Set<String>> nlp2ApiQueriesApis;
	private Map<String,Set<Integer>> bigMapApisIds;

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
		
		case "runApproach":
			runApproach();
			break;
		
		case "loadInvertedIndexFile":
			loadInvertedIndexFile();
			break;
		
		case "generateInvertedIndexFileFromSOPosts":
			generateInvertedIndexFileFromSOPosts();
			break;
		
		case "generateMetricsForApiExtractors":
			generateMetricsForApiExtractors(null);
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

	
	private void runApproach() throws Exception {
		//load the inverted index
		loadInvertedIndexFile();
		loadQueriesApisForApproaches();
		Map<Integer, Set<String>> recommendedApis = getRecommendedApis();
		System.out.println(recommendedApis);
	}



	private void loadInvertedIndexFile() throws IOException {
		if(bigMapApisIds==null) {
			bigMapApisIds = new HashMap<>();
			List<String> apisAndSOIds = Files.readAllLines(Paths.get(CrokageStaticData.BIG_MAP_INVERTED_INDEX_APIS_FILE_PATH), Charsets.UTF_8);
			for(String line: apisAndSOIds) {
				String[] parts = line.split(":\t");
				String api = parts[0];
				String[] ids = null;
				try {
					ids = parts[1].split(" ");
				} catch (Exception e) {
					System.out.println();
				}
				
				HashSet<Integer> idsSet = new HashSet<>();
				for(String id:ids) {
					idsSet.add(Integer.parseInt(id));
				}
				bigMapApisIds.put(api, idsSet);
			}
		}
		//System.out.println(bigMapApisIds);
	}



	private void generateInvertedIndexFileFromSOPosts() throws FileNotFoundException {
		//get posts containing code. Date parameter is optional for tests purpose.
		//String startDate = "2018-06-01"; //1495 results
		String startDate = null;
		List<Post> postsWithPreCode =  crokageService.getAnswersWithCode(startDate);
		
		Map<String,Set<Integer>> bigMapApisIds = new HashMap<>();
		
		for(Post answer:postsWithPreCode) {
			
			/*if(answer.getId().equals(50662268)) {
				System.out.println();
			}*/
			
			//extract apis from answer. For each api, add the api in a map, together with the other references for that post
			Set<String> codeSet=null;
			try {
				codeSet = crokageUtils.extractClassesFromCode(answer.getBody());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//ArrayList<String> codeClasses = new ArrayList(codeSet);
			if(codeSet.isEmpty()) {
				continue;
			}
			
			for(String api: codeSet) {
				if(bigMapApisIds.get(api)==null){
					HashSet<Integer> idsSet = new LinkedHashSet<>();
					idsSet.add(answer.getId());
					bigMapApisIds.put(api, idsSet);
				
				}else {
					/*if(api.equals("MainActivity")) {
						System.out.println();
					}*/
					Set<Integer> currentApis = bigMapApisIds.get(api);
					currentApis.add(answer.getId());
				}
				
			}
			
		}
		
		CrokageUtils.printBigMapIntoFile(bigMapApisIds,CrokageStaticData.BIG_MAP_INVERTED_INDEX_APIS_FILE_PATH);
		
	}


	private void generateInputQueriesFromNLP2ApiGroudTruth() throws Exception {
		List<String> inputQueries = getQueriesFromFile(CrokageStaticData.NLP2API_GOLD_SET_FILE);
		
		Path queriesFile = Paths.get(CrokageStaticData.INPUT_QUERIES_FILE_NLP2API);
		Files.write(queriesFile, inputQueries, Charset.forName("UTF-8"));
		
	}



	private Map<Integer, Set<String>> extractAPIsFromNLP2Api() throws Exception {
		
		try {
			
			if(queries==null) {
				queries = readInputQueries();
			}
			
			//queries = queries.subList(0, 5);
			//First generate inputQueries file in a format NLP2Api understand
			FileWriter fw = new FileWriter(CrokageStaticData.NLP2API_INPUT_QUERIES_FILE);
			for (String query: queries) {
				fw.write(query+"\n--\n"); //specific format to NLP2API understand
				
			}
		 	fw.close();
			
			
			if(callNLP2ApiProcess) {
					
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
		

		Map<Integer,Set<String>> nlp2ApiQueriesApis = getQueriesAndApisFromFileMayContainDupes(CrokageStaticData.NLP2API_OUTPUT_QUERIES_FILE);
		return nlp2ApiQueriesApis;
		
	}
	
	private Map<Integer, Set<String>> extractAPIsFromRACK() throws Exception {
			
		if(queries==null) {
			queries = readInputQueries();
		}
		
		//queries = queries.subList(0, 5);
		
		try {	
			
			//First generate inputQueries file in a format NLP2Api understand
			FileWriter fw = new FileWriter(CrokageStaticData.RACK_INPUT_QUERIES_FILE);
			for (String query: queries) {
				fw.write(query+"\n--\n"); //specific format to NLP2API understand
				
			}
		 	fw.close();
			
			if(callRACKApiProcess) {
			 	//call jar with parameters
			 	
			 	String jarPath = CrokageStaticData.CROKAGE_HOME;
			 	List<String> command = new ArrayList<String>();
			    
			 	
			 	//old version
			 	/*command.add("java");
			    command.add("-jar");
			    command.add(jarPath+"/rack-execOld.jar");
			    command.add("-K");
			    command.add(""+numberOfAPIClasses);
			    command.add("-task");
			    command.add("suggestAPI");
			    command.add("-queryFile");
			    command.add(CrokageStaticData.RACK_INPUT_QUERIES_FILE);
			    command.add("-resultFile");
			    command.add(CrokageStaticData.RACK_OUTPUT_QUERIES_FILE);*/
			 	
			 	
			 	//new version
			 	command.add("java");
			    command.add("-jar");
			    command.add(jarPath+"/rack-exec.jar");
			    command.add("-K");
			    command.add(""+numberOfAPIClasses);
			    command.add("-task");
			    command.add("suggestAPI");
			    command.add("-queryFile");
			    command.add(CrokageStaticData.RACK_INPUT_QUERIES_FILE);
			    command.add("-resultFile");
			    command.add(CrokageStaticData.RACK_OUTPUT_QUERIES_FILE);
				
				ProcessBuilder pb = new ProcessBuilder(command);
				Process p = pb.start();
				p.waitFor();
				String output = CrokageUtils.loadStream(p.getInputStream());
				String error = CrokageUtils.loadStream(p.getErrorStream());
				int rc = p.waitFor();
				System.out.println("Process ended with rc=" + rc);
				System.out.println("\nStandard Output:\n");
				System.out.println(output);
				String apis[] = output.replaceAll("\n", " ").split(" ");
				System.out.println(apis);
				System.out.println("\nStandard Error:\n");
				System.out.println(error);
		 	}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*//the real queries
		Map<String,Set<String>> rawApiQueriesApis = getQueriesAndApisFromFile(CrokageStaticData.RACK_INPUT_QUERIES_FILE);
				
		Map<Integer,Set<String>> reformulatedApiQueriesApis = getQueriesAndApisFromFileMayContainDupes(CrokageStaticData.RACK_OUTPUT_QUERIES_FILE);
		
		//Merge not reformulated queries with the generated APIs
		
		//Map<String,Set<String>> merged = new LinkedHashMap<>();
		
		Set<Entry<String, Set<String>>> rawEntries = rawApiQueriesApis.entrySet();
		Set<Entry<Integer, Set<String>>> reformulatedEntries = reformulatedApiQueriesApis.entrySet();
		
		Iterator rawIt = rawEntries.iterator();
		Iterator reformulatedIt = reformulatedEntries.iterator();
		
		while(reformulatedIt.hasNext() && rawIt.hasNext()) {
			Entry<Integer, Set<String>> reformulatedEntry = (Entry<Integer, Set<String>>)reformulatedIt.next();
			Entry<String, Set<String>> rawEntry = (Entry<String, Set<String>>)rawIt.next();
			
			String query = rawEntry.getKey();
			Set<String> apis = reformulatedEntry.getValue();
			
			if(query.contains("How do I retrieve available schemas in database")) {
				System.out.println();
			}
			
			rawEntry.setValue(apis);
		}*/
		Map<Integer,Set<String>> reformulatedApiQueriesApis = getQueriesAndApisFromFileMayContainDupes(CrokageStaticData.RACK_OUTPUT_QUERIES_FILE);
		
		return reformulatedApiQueriesApis;
		
	}
	
	
	
	private List<String> getQueriesFromFile(String fileName) throws IOException {
		List<String> queries = new ArrayList<>();
		// reading output from generated file
		List<String> queriesAndApis = Files.readAllLines(Paths.get(fileName), Charsets.UTF_8);
		Iterator<String> it = queriesAndApis.iterator();
		
		while(it.hasNext()) {
			String query = "";
			if(it.hasNext()) {
				query = it.next();
				queries.add(query);
				if(it.hasNext()) {
					it.next(); //APIs are in even lines
				}
			}
			
		}
		
		
		return queries;
	}
	
	
	@Deprecated
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
				if(it.hasNext()) {
					queryApis = it.next(); //APIs are in even lines
				}
			}
			
			/*if(query.contains("How do I retrieve available schemas in database")) {
				System.out.println();
			}*/
			
			queryApis = queryApis.replaceAll("\\s+"," ");
			
			List<String> rankedApis = Arrays.asList(queryApis.split(" ")).stream().map(String::trim).collect(Collectors.toList());
			int k = numberOfAPIClasses;
			if (rankedApis.size() < k) {
				//logger.warn("The number of retrieved APIs for query is lower than the number of rack classes set as parameter. Ajusting the parameter to -->" + rankedApis.size() + " apis");
				k = rankedApis.size();
			}
		
			//rankedApis = rankedApis.subList(0, k);
			
			//Set<Integer> rankedApisSet = ImmutableSet.copyOf(Iterables.limit(set, 20));
			
			Set<String> rankedApisSet = new LinkedHashSet<String>(rankedApis);
			CrokageUtils.setLimit(rankedApisSet,k);
			//rankedApisSet = ImmutableSet.copyOf(Iterables.limit(rankedApisSet, k));
			queriesApis.put(query.trim(), rankedApisSet);
			
			//logger.info("discovered classes for query: "+query+ " ->  " + queriesApis.get(query.trim()));
		}
		
		
		return queriesApis;
	}

	
	/*
	 * Extended version of getQueriesAndApisFromFile where the generated queries can be equal
	 */
	private Map<Integer, Set<String>> getQueriesAndApisFromFileMayContainDupes(String fileName) throws IOException {
		Map<Integer,Set<String>> queriesApis = new LinkedHashMap<>();
		// reading output from generated file
		List<String> queriesAndApis = Files.readAllLines(Paths.get(fileName), Charsets.UTF_8);
		if(limitQueries!=null) {
			queriesAndApis = queriesAndApis.subList(0, 2*limitQueries);
		}
		Iterator<String> it = queriesAndApis.iterator();
		int key = 1;
		while(it.hasNext()) {
			String query = "";
			String queryApis = "";
			if(it.hasNext()) {
				query = it.next();
				if(it.hasNext()) {
					queryApis = it.next(); //APIs are in even lines
				}
			}
			
			queryApis = queryApis.replaceAll("\\s+"," ");
			
			List<String> rankedApis = Arrays.asList(queryApis.split(" ")).stream().map(String::trim).collect(Collectors.toList());
			int k = numberOfAPIClasses;
			if (rankedApis.size() < k) {
				//logger.warn("The number of retrieved APIs for query is lower than the number of rack classes set as parameter. Ajusting the parameter to -->" + rankedApis.size() + " apis");
				k = rankedApis.size();
			}
			
			
			//rankedApis = rankedApis.subList(0, k);
			
			//Set<Integer> rankedApisSet = ImmutableSet.copyOf(Iterables.limit(set, 20));
			
			Set<String> rankedApisSet = new LinkedHashSet<String>(rankedApis);
			//rankedApisSet = ImmutableSet.copyOf(Iterables.limit(rankedApisSet, k));
			CrokageUtils.setLimit(rankedApisSet, k);
			queriesApis.put(key, rankedApisSet);
			//logger.info("discovered classes for query: "+query+ " ->  " + queriesApis.get(key));
			key++;
		}
		
		
		return queriesApis;
	}



	private Map<Integer,Set<String>> extractAPIsFromBIKER() throws Exception {
		// BIKER
		if(queries==null) {
			queries = readInputQueries();
		}
		
		// writing queries to be read by biker
		Map<Integer,Set<String>> bikerQueriesApis = new LinkedHashMap<>();
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
		
		int key = 1;
		// reading output from BIKER
		List<String> queriesWithApis = Files.readAllLines(Paths.get(CrokageStaticData.BIKER_OUTPUT_QUERIES_FILE), Charsets.UTF_8);
		if(limitQueries!=null) {
			queriesWithApis = queriesWithApis.subList(0, limitQueries);
		}
		for(String generatedLine: queriesWithApis) {
			String parts[] = generatedLine.split("=  ");
			List<String> rankedApis = Arrays.asList(parts[1].split("### ")).stream().map(String::trim).collect(Collectors.toList());
			rankedApis.remove("");
			
			int k = numberOfAPIClasses;
			if (rankedApis.size() < k) {
				//logger.warn("The number of retrieved APIs from BIKER is lower than the number of rack classes set as parameter. Ajusting the parameter to -->" + rankedApis.size() + " apis, returned by BIKER for this query.");
				k = rankedApis.size();
			}
			
			//rankedApis = rankedApis.subList(0, k);
			Set<String> rankedApisSetWithMethods = new LinkedHashSet<String>(rankedApis);
			
			if(bikerOnlyClasses) {
				Set<String> rankedApisSetClassesOnly = new LinkedHashSet<String>();
				for(String api: rankedApisSetWithMethods) {
					rankedApisSetClassesOnly.add(api.split("\\.")[0]);
				}
				
				CrokageUtils.setLimit(rankedApisSetClassesOnly,k);
				//rankedApisSetClassesOnly = ImmutableSet.copyOf(Iterables.limit(rankedApisSetClassesOnly, k));
				bikerQueriesApis.put(key, rankedApisSetClassesOnly);
			}else {
				bikerQueriesApis.put(key, rankedApisSetWithMethods);
			}
			
			//logger.info("Biker - discovered classes for query: "+parts[0]+ " ->  " + bikerQueriesApis.get(key));
			key++;
		}
		
		/*List<String> bikerClasses = new ArrayList<>();
		for(String api: list) {
			classes.add(api.split("\\.")[0]); 
		}*/
		
		logger.info("Biker finished... ");
		return bikerQueriesApis;
	}

	
	

	private void generateMetricsForApiExtractors(Integer[] kArray) throws Exception {
		
		//needs to be before extraction, because annotated dataset is used to generate queries file in case of crokage
		Map<Integer, Set<String>> goldSetQueriesApis = getGoldSetQueriesApis();

		int numApproaches = loadQueriesApisForApproaches();
		
		Map<Integer, Set<String>> recommendedApis = getRecommendedApis();
	
		
		if(subAction.contains("generatetableforapproaches")) {
			
			List<Integer> ks = new ArrayList<>();
			ks.add(10); ks.add(5); ks.add(1);
			if(numApproaches>1) {
				ks.remove(ks.size()-1);
			}
			MetricResult metricResult = new MetricResult();
			for(int k: ks) {
				numberOfAPIClasses = k;
				crokageUtils.reduceSet(goldSetQueriesApis,k);
				crokageUtils.reduceSet(recommendedApis, k);
				analyzeResults(recommendedApis,goldSetQueriesApis,metricResult);
			}
			logger.info(metricResult.toString());
		}else {
			analyzeResults(recommendedApis,goldSetQueriesApis,null);
		}
		
		
		
		
		/*
		if(subAction.contains("|")) { 
			
			if(subAction.contains("rack")) {
				rackQueriesApis = extractAPIsFromRACK();
			}
			if(subAction.contains("biker")) {
				bikerQueriesApis = extractAPIsFromBIKER();
			}
			if(subAction.contains("nlp2api")) {
				nlp2ApiQueriesApis = extractAPIsFromNLP2Api();
			}
		}else if(subAction.equals("biker")) {
			bikerQueriesApis = extractAPIsFromBIKER();
		}else if(subAction.equals("rack")) {
			rackQueriesApis = extractAPIsFromRACK();
		}else {
			nlp2ApiQueriesApis = extractAPIsFromNLP2Api();
		}*/
		
	
		
		
		
		
	}


	private int loadQueriesApisForApproaches() throws Exception {
		int numApproaches = 0;
		//subAction is transformed to lowercase
		if(subAction.contains("rack")) {
			rackQueriesApis = extractAPIsFromRACK();
			numApproaches++;
			logger.info("Metrics for RACK");
		}
		if(subAction.contains("biker")) {
			bikerQueriesApis = extractAPIsFromBIKER();
			numApproaches++;
			logger.info("Metrics for BIKER");
		}
		if(subAction.contains("nlp2api")) {
			nlp2ApiQueriesApis = extractAPIsFromNLP2Api();
			numApproaches++;
			logger.info("Metrics for nlp2api");
		}
		return numApproaches;
	}



	private Map<Integer, Set<String>> getGoldSetQueriesApis() throws IOException {
		Map<Integer, Set<String>> goldSetQueriesApis = new LinkedHashMap<>();
		
		if(dataSet.equals("crokage")) {
			List<UserEvaluation> evaluationsList = loadExcelGroundTruthQuestionsAndLikerts();
			//get goldset and update input queries to be used by approaches
			goldSetQueriesApis.putAll(getCrokageGoldSetByEvaluations(evaluationsList));
	
		}else if(dataSet.equals("nlp2api")) {
			goldSetQueriesApis.putAll(getQueriesAndApisFromFileMayContainDupes(CrokageStaticData.NLP2API_GOLD_SET_FILE));
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

	

	/*private HashSet<String> getRackApisForQuery(String query) {
		//logger.info("RACK: discovering related classes to query: " + query);
		CodeTokenProvider ctProvider = new CodeTokenProvider(query);
		List<String> apis = ctProvider.recommendRelevantAPIs();
		return new LinkedHashSet<>(apis);
	}*/

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
	
	
	private Map<Integer, Set<String>> getCrokageGoldSetByEvaluations(List<UserEvaluation> evaluationsWithBothUsersScales) throws FileNotFoundException {
		//BufferedWriter bw =null;
		//Map<Integer,Integer> externalQuestionOracleCounter = new HashMap<>();
		long initTime = System.currentTimeMillis();
		Map<String,Set<String>> goldSetQueriesApis = new LinkedHashMap<>();
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
					Set<String> codeSet = crokageUtils.extractClassesFromCode(post.getBody());
					//ArrayList<String> codeClasses = new ArrayList(codeSet);
					if(codeSet.isEmpty()) {
						continue;
					}
					
					if(goldSetQueriesApis.get(query)==null){
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
		
		//transform from querie
		Map<Integer, Set<String>> goldSetMap = new LinkedHashMap<>();
		Set<String> keySet = goldSetQueriesApis.keySet();
		
		int keyNumber =1;
		for(String keyQuery: keySet) {
			Set<String> apis = goldSetQueriesApis.get(keyQuery);
			goldSetMap.put(keyNumber, apis);
			keyNumber++;
		}
		
		
		//update input queries to be used by approaches
		try (PrintWriter out = new PrintWriter(CrokageStaticData.INPUT_QUERIES_FILE_CROKAGE)) {
		    for(String query: keySet) {
		    	out.println(query);
		    }
			
		}
		
		
		return goldSetMap;
	}
	
	
	private Map<Integer, Set<String>> getRecommendedApis() {
		Map<Integer, Set<String>> recommendedApis = new LinkedHashMap<>();
		//are the same for all
		//Set<String> queriesSet = new LinkedHashSet<>(queries);  
		Set<Integer> keys = null; 
		String approaches = subAction;
		
		int numApproaches = 0;
		if(rackQueriesApis!=null) {
			numApproaches++;
			keys = rackQueriesApis.keySet();
		}
		if(bikerQueriesApis!=null) {
			numApproaches++;
			keys = bikerQueriesApis.keySet();
		}
		if(nlp2ApiQueriesApis!=null) {
			numApproaches++;
			keys = nlp2ApiQueriesApis.keySet();
		}
				
		if(numApproaches>1){ 
			approaches = subAction.replace("|generatetableforapproaches", "");
			String apisArrOrder[] = approaches.split("\\|");
			//List<String> queriesList = new ArrayList<>(queriesSet);
			String bikerApi = null;
			String nlp2ApiApi = null;
			String rackApi = null;
			
			
			outer: for(Integer keyNum: keys) {
				Set<String> recommendedApisSet = new LinkedHashSet<>();
				Set<String> bikerApisSet = null;
				Set<String> rackApisSet = null;
				Set<String> nlpApisSet = null;
				
				Iterator<String> bikerIt = null;
				Iterator<String> rackIt = null;
				Iterator<String> nlpIt = null;
				
				
				if(rackQueriesApis!=null) {
					rackApisSet = rackQueriesApis.get(keyNum);
					rackIt = rackApisSet.iterator();
				}
				if(bikerQueriesApis!=null) {
					bikerApisSet = bikerQueriesApis.get(keyNum);
					bikerIt = bikerApisSet.iterator();
				}
				if(nlp2ApiQueriesApis!=null) {
					nlpApisSet = nlp2ApiQueriesApis.get(keyNum);
					nlpIt = nlpApisSet.iterator();
				}
				
				bikerApi = "";
				nlp2ApiApi = "";
				rackApi = "";
				
				
				int i=1;
				String chosenApi = null;
				//int key =1;
				while(true) { 
					//collect the ith API from each approach and merge
					chosenApi = null;
					boolean stop = true;
					
					if(bikerIt!=null && bikerIt.hasNext()) {
						bikerApi = bikerIt.next();
						stop = false;
					}
					
					if(nlpIt!=null && nlpIt.hasNext()) {
						nlp2ApiApi= nlpIt.next();
						stop = false;
					}
					if(rackIt!=null && rackIt.hasNext()) {
						rackApi = rackIt.next();
						stop = false;
					}
					
					if(stop) { //recommenders were not able to produce enough apis
						break;
					}
					
					//uses the order of the apis to merge the recommendation
					if(numApproaches==3) {
						if(bikerApi.equals(nlp2ApiApi) || bikerApi.equals(rackApi)) { //if 2 are equal, choose it, else one of each until numberOfAPIClasses
							chosenApi = bikerApi;
							recommendedApisSet.add(chosenApi);
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
							i++;
							
						} else if(nlp2ApiApi.equals(rackApi)) {
							chosenApi = nlp2ApiApi;
							recommendedApisSet.add(chosenApi);
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
							i++;
						} else {  //all different, follows the order
							
							
						}
						
					} 
					//two approaches or three approaches: rack+biker(+nlp), rack+nlp(+biker), biker+rack(+nlp), biker+nlp(+rack), nlp+rack(+biker), nlp+biker(+rack)
					if(apisArrOrder[0].equals("rack") && apisArrOrder[1].equals("biker")) {
						
						recommendedApisSet.add(rackApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(bikerApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
							
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(nlp2ApiApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}else if(apisArrOrder[0].equals("rack") && apisArrOrder[1].equals("nlp2api")) {
						
						recommendedApisSet.add(rackApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(nlp2ApiApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(bikerApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}else if(apisArrOrder[0].equals("biker") && apisArrOrder[1].equals("rack")) {
						
						recommendedApisSet.add(bikerApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(rackApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						//nlp
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(nlp2ApiApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
						
					}else if(apisArrOrder[0].equals("biker") && apisArrOrder[1].equals("nlp2api")) {
						
						recommendedApisSet.add(bikerApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(nlp2ApiApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(rackApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}else if(apisArrOrder[0].equals("nlp2api") && apisArrOrder[1].equals("rack")) {
						
						recommendedApisSet.add(nlp2ApiApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(rackApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(bikerApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}else if(apisArrOrder[0].equals("nlp2api") && apisArrOrder[1].equals("biker")) {
						recommendedApisSet.add(nlp2ApiApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						recommendedApisSet.add(bikerApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(rackApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}
			
				}
				recommendedApis.put(keyNum, recommendedApisSet);
				//key++;
				
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





	public void analyzeResults(Map<Integer, Set<String>> recommendedApis,Map<Integer, Set<String>> goldSetQueriesApis, MetricResult metricResult) {
		int hitK = 0;
		int correct_sum = 0;
		double rrank_sum = 0;
		double precision_sum = 0;
		double preck_sum = 0;
		double recall_sum = 0;
		double fmeasure_sum = 0;
		
		int k = numberOfAPIClasses;
		
		try {
			
			for (Integer keyQuery : goldSetQueriesApis.keySet()) {
				
				List<String> rapis = new ArrayList<>(recommendedApis.get(keyQuery));
				/*int K = rapis.size() < numberOfAPIClasses ? rapis.size() : numberOfAPIClasses;
				rapis = rapis.subList(0, K);*/
				ArrayList<String> gapis = new ArrayList<>(goldSetQueriesApis.get(keyQuery));
			
				/*if(keyQuery.contains("Read JSON Array")) {
					System.out.println();
				}*/
				
				hitK = hitK + isApiFound_K(rapis, gapis);
				rrank_sum = rrank_sum + getRRank(rapis, gapis);
				double preck = 0;
				preck = getAvgPrecisionK(rapis, gapis);
				preck_sum = preck_sum + preck;
				double recall = 0;
				recall = getRecallK(rapis, gapis);
				recall_sum = recall_sum + recall;
				//System.out.println(hitK);
				/*if(keyQuery==308) {
					System.out.println();
				}*/
				//System.out.println("query="+keyQuery+" -hitk="+hitK+" -rrank_sum:"+rrank_sum+" -preck:"+preck+ " -recall:"+recall );
			}

			double hit_k= CrokageUtils.round((double) hitK / goldSetQueriesApis.size(),4);
			double mrr = CrokageUtils.round(rrank_sum / goldSetQueriesApis.size(),4);
			double map = CrokageUtils.round(preck_sum / goldSetQueriesApis.size(),4);
			double mr = CrokageUtils.round(recall_sum / goldSetQueriesApis.size(),4);
			
			if(metricResult!=null) {
				if(k==10) {
					metricResult.setHitK10(""+hit_k);
					metricResult.setMrrK10(""+mrr);
					metricResult.setMapK10(""+map);
					metricResult.setMrK10(""+mr);
				}else if(k==5) {
					metricResult.setHitK5(""+hit_k);
					metricResult.setMrrK5(""+mrr);
					metricResult.setMapK5(""+map);
					metricResult.setMrK5(""+mr);
				}else {
					metricResult.setHitK1(""+hit_k);
					metricResult.setMrrK1(""+mrr);
					metricResult.setMapK1(""+map);
					metricResult.setMrK1(""+mr);
				}
			}
			
			
			logger.info("Results: \n"
					+"\nHit@" + k + ": " + hit_k
					+ "\nMRR@" + k + ": " + mrr
					+ "\nMAP@" + k + ": " + map
					+ "\nMR@" + k + ": " + mr
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
					//logger.info("Gold:"+gapi+"\t"+"Result:"+api);
					found = 1;
					break outer;
				}
			}
		}
		return found;
	}
	

	/*protected int isApiFound_K(List<String> rapis, ArrayList<String> gapis, int K) {
		// check if correct API is found
		K = rapis.size() < K ? rapis.size() : K;
		int found = 0;
		outer: for (int i = 0; i < K; i++) {
			String api = rapis.get(i);
			for (String gapi : gapis) {
				if (gapi.endsWith(api) || api.endsWith(gapi)) {
				//if (gapi.equals(api)) {
					//logger.info("Gold:"+gapi+"\t"+"Result:"+api);
					found = 1;
					break outer;
				}
			}
		}
		return found;
	}

*/






	
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
			//if (gapi.endsWith(api) || api.endsWith(gapi)) {
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
