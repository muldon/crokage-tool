package com.ufu;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.ufu.crokage.util.CrokageUtils;




public class RackTest {
	public static String CROKAGE_HOME = "/home/rodrigo/Dropbox/Doutorado/projects/crokage-tool";
	public static String INPUT_QUERIES_FILE_CROKAGE = CROKAGE_HOME+"/data/inputQueriesCrokage.txt";
	public static String RACK_INPUT_QUERIES_FILE = CROKAGE_HOME+"data/rackApiQueriesInput.txt";    
	public static String RACK_OUTPUT_QUERIES_FILE = CROKAGE_HOME+"data/rackApiQueriesOutput.txt";
	public static String searchQuery = "How to round a number to n decimal places in Java"; 	
	
	public static void main(String[] args) {
		
		
		//CodeTokenProvider ctProvider = new CodeTokenProvider(query,TOPK);
		
		/*CodeTokenProvider ctProvider = new CodeTokenProvider(query);
		List<String> apis = ctProvider.recommendRelevantAPIs();
		System.out.println(apis.stream().limit(10).collect(Collectors.toList()) );*/
		
		//searchQuery = new TextNormalizer(searchQuery).normalizeTextLight();
		//int TOPK = 10;
		//int caseNo = 31;
		//String suggested = new CodeSearchBDAReformulator(caseNo, searchQuery, TOPK, "both").provideRelevantAPIs();
		//System.out.println(suggested);
		
		
		testSimpleQuery();
		
		//testFileQueries();
		
		
	}

	private static void testFileQueries() {
		try {
			
			List<String> queries = FileUtils.readLines(new File(INPUT_QUERIES_FILE_CROKAGE), "utf-8");
			queries = queries.subList(0, 10);
				
			//First generate inputQueries file in a format NLP2Api understand
			FileWriter fw = new FileWriter(RACK_INPUT_QUERIES_FILE);
			for (String query: queries) {
				fw.write(query+"\n--\n"); //specific format to NLP2API understand
				
			}
		 	fw.close();
			
			String jarPath = CROKAGE_HOME;
			//String command = "java -jar "+jarPath+ "/myNlp2Api.jar "+ "-K 10 -task reformulate -query How do I send an HTML email?";
			List<String> command = new ArrayList<String>();
		    
		 	command.add("java");
		    command.add("-jar");
		    command.add(jarPath+"/rack-exec.jar");
		    command.add("-K");
		    command.add("10");
		    command.add("-task");
		    command.add("suggestAPI");
//		    command.add("-queryFile");
//		    command.add(RACK_INPUT_QUERIES_FILE);
//		    command.add("-resultFile");
//		    command.add(RACK_OUTPUT_QUERIES_FILE);
		    command.add("-query");
		    command.add(searchQuery);
		    
			
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static void testSimpleQuery() {
		String rawQuery = "How to round a number to n decimal places in Java";
		Set<String> rackApis = null;
		try {
			
			String jarPath = CROKAGE_HOME;
			//String command = "java -jar "+jarPath+ "/myNlp2Api.jar "+ "-K 10 -task reformulate -query How do I send an HTML email?";
			List<String> command = new ArrayList<String>();
		    
		    command.add("java");
		    command.add("-jar");
		    command.add(jarPath+"/rack-exec.jar");
		    command.add("-K");
		    command.add("10");
		    command.add("-task");
		    command.add("suggestAPI");
		    command.add("-query");
		    command.add(rawQuery);
			
			ProcessBuilder pb = new ProcessBuilder(command);
			Process p = pb.start();
			p.waitFor();
			String output = CrokageUtils.loadStream(p.getInputStream());
			String error = CrokageUtils.loadStream(p.getErrorStream());
			int rc = p.waitFor();
			/*System.out.println("Process ended with rc=" + rc);
			System.out.println("\nStandard Output:\n");
			*/
			System.out.println(output);
			String apis[] = output.split("\n");
			apis = ArrayUtils.remove(apis, 0);
			rackApis = new LinkedHashSet<String>(Arrays.asList(apis));
			/*System.out.println(apis);
			System.out.println("\nStandard Error:\n");
			System.out.println(error);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(rackApis);
		
		
	}
}

