package com.ufu.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufu.bot.googleSearch.GoogleWebSearch;
import com.ufu.bot.googleSearch.SearchQuery;
import com.ufu.bot.googleSearch.SearchResult;
import com.ufu.bot.service.PitBotService;
import com.ufu.bot.util.BotUtils;

import core.CodeTokenProvider;


@Component
public class PitBotApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected Set<Integer> closedDuplicatedNonMastersIdsByTag;
	
	@Autowired
	private PitBotService pitBotService;
	
	@Autowired
	private BotUtils botUtils;
	
	
	@Value("${inputQueriesPath}")
	public String inputQueriesPath;  
	
	
	@Value("${numberOfRackClasses}")
	public Integer numberOfRackClasses;  
	
	
	@Value("${numberOfGoogleResults}")
	public Integer numberOfGoogleResults;  
	
	@Value("${shuffleListOfQueriesBeforeGoogleSearch}")
	public Boolean shuffleListOfQueriesBeforeGoogleSearch;
		
	@Value("${pickUpOnlyTheFirstQuery}")
	public Boolean pickUpOnlyTheFirstQuery;
	
	/*
	 * Path to a file which contains a FLAG indicating if the environment is test or production. This file (environmentFlag.properties) contains only one line with a boolean value (isTest = true|false)
	 * If isTest = true, the proxy is not applied for the google search engine. Otherwise, proxy is set.   
	 */
	@Value("${pathFileEnvFlag}")
	public String pathFileEnvFlag;   
	
	/*
	 * Stores the value obtained from the pathFileEnvFlag file 
	 */
	private Boolean isTest;
	
	private List<String> queriesList;
	
	private long initTime; 
	private long endTime;
	

	
	@PostConstruct
	public void init() throws Exception {
		
		logger.info("Initializing app...");
		
		getPropertyValueFromLocalFile();
		
				
		logger.info("\nConsidering parameters: \n"
				+ "\n inputQueriesPath: "+inputQueriesPath
				+ "\n numberOfRackClasses: "+numberOfRackClasses
				+ "\n numberOfGoogleResults: "+numberOfGoogleResults
				+ "\n pathFileEnvFlag: "+pathFileEnvFlag
				+ "\n isTest: "+isTest
				+ "\n shuffleListOfQueriesBeforeGoogleSearch: "+shuffleListOfQueriesBeforeGoogleSearch
				+ "\n pickUpOnlyTheFirstQuery: "+pickUpOnlyTheFirstQuery
				+ "\n");
		

		/*
		 * Step 1: Question in Natural Language
		 * Read queries from a text file and insert into a list
		 */		
		File file = new File(inputQueriesPath);
		queriesList = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	logger.info(line);
		       queriesList.add(line);
		    }
		}
		
		
		if(queriesList.isEmpty()) {
			throw new Exception("No input query found in the input file. Aborting...");
		}
		

		if(shuffleListOfQueriesBeforeGoogleSearch) {
			Collections.shuffle(queriesList);
		}
		
		
		if(pickUpOnlyTheFirstQuery) {  //for tests
			processQuery(queriesList.get(0));
		}else {
			//For each query 
			int i=1;
			for(String query: queriesList) {
				logger.info("Processing query "+i+ " -> "+query);
				processQuery(query);
				i++;
			}
		}
		
		
		//String duration = DurationFormatUtils.formatDuration(endTimeLoop-initTimeLoop, "HH:mm:ss,SSS");
		
		
				
					
	}


	


	private void processQuery(String query) {
		
		/*
		 * Step 2: API Classes Extraction
		 * RACK  
		 *   *** Considering only the first query
		 */
		initTime = System.currentTimeMillis();
		List<String> apis = step2(query);
		botUtils.reportElapsedTime(initTime,"step2 - RACK ");
		
		
		
		/*
		 * Step 3: Query Preparation
		 * 
		 */
		String googleQuery = step3(apis,query);
		
		
		/*
		 * Step 4: Query Serach
		 * 
		 */
		initTime = System.currentTimeMillis();
		Set<Integer> soQuestionsIds = step4(googleQuery);
		botUtils.reportElapsedTime(initTime,"step4 - Google Search ");
		
		
		
		/*
		 * Step 5: Fetch Questions Content in SO
		 * 
		 */
		
		
		
	}

	
	

	/**
	 * Rack tool
	 * @param query
	 * @return List<String> representing the associated classes
	 */
	private List<String> step2(String query) {
		//list is not null. It has been already verified.
		logger.info("RACK: discovering related classes to query: "+query);
		CodeTokenProvider ctProvider = new CodeTokenProvider(query);
		List<String> apis = ctProvider.recommendRelevantAPIs();
		logger.info("Finished... discored classes:"+ apis.stream().limit(numberOfRackClasses).collect(Collectors.toList()));
		return apis;
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



	/**
	 * Google Search
	 * @param googleQuery
	 * @return Set<Integer>
	 */
	private Set<Integer> step4(String googleQuery) {
		logger.info("Initiating Google Search... Using query: "+googleQuery);
		Set<Integer> soQuestionsIds = new LinkedHashSet<>();
		
		try {
			SearchQuery searchQuery = new SearchQuery.Builder(googleQuery)
			        .site("https://stackoverflow.com")
			        .numResults(numberOfGoogleResults).build();
			SearchResult result = new GoogleWebSearch().search(searchQuery);
			//assertThat(result.getSize(), equalTo(10));
			List<String> urls = result.getUrls();
			//List<String> urls = new ArrayList<>();
			Integer questionId;
			//urls.add("https://stackoverflow.com/questions/21961651/why-does-activesupport-add-method-forty-two-to-array/21962048");
			
			for(String url: urls){
				//String[] urlPart = url.split("\\/[[:digit:]].*\\/");
				Pattern pattern = Pattern.compile("\\/([\\d]+)");
				Matcher matcher = pattern.matcher(url);
				if (matcher.find()) {
					soQuestionsIds.add(new Integer(matcher.group(1)));
					logger.info("Recovering question from URL: "+url);
				}
				//System.out.println(url);
				
			}
		} catch (IOException e) {
			System.out.println("Error ... "+e);
			e.printStackTrace();
		}
		
		for(Integer soQuestionId: soQuestionsIds) {
			logger.info("Id: "+soQuestionId);
		}
	
		return soQuestionsIds;
		
	}








	
	private void getPropertyValueFromLocalFile() {
		Properties prop = new Properties();
		InputStream input = null;
		isTest = true;
		try {

			input = new FileInputStream(pathFileEnvFlag);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			String isTestStr = prop.getProperty("isTest");
			if(!StringUtils.isBlank(isTestStr)) {
				isTest = new Boolean(isTestStr);
			}
			String msg = "\nEnvironment property is: ";
			if(isTest) {
				msg+= "Test";
			}else {
				msg+= "Production";
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


	    
	
	
}
