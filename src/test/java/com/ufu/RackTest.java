package com.ufu;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.ufu.crokage.config.CrokageStaticData;
import com.ufu.crokage.util.CrokageUtils;




public class RackTest {

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
		
		
		//testSimpleQuery();
		
		testFileQueries();
		
		
	}

	private static void testFileQueries() {
		try {
			
			List<String> queries = FileUtils.readLines(new File(CrokageStaticData.INPUT_QUERIES_FILE_CROKAGE), "utf-8");
			queries = queries.subList(0, 10);
			
			
			//First generate inputQueries file in a format NLP2Api understand
			FileWriter fw = new FileWriter(CrokageStaticData.RACK_INPUT_QUERIES_FILE);
			for (String query: queries) {
				fw.write(query+"\n--\n"); //specific format to NLP2API understand
				
			}
		 	fw.close();
			
			String jarPath = CrokageStaticData.CROKAGE_HOME;
			//String command = "java -jar "+jarPath+ "/myNlp2Api.jar "+ "-K 10 -task reformulate -query How do I send an HTML email?";
			List<String> command = new ArrayList<String>();
		    
		 	command.add("java");
		    command.add("-jar");
		    command.add(jarPath+"/rack-exec.jar");
		    command.add("-K");
		    command.add("10");
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static void testSimpleQuery() {

		String searchQuery = "How to send email in Java?";
		try {
			
			String jarPath = CrokageStaticData.CROKAGE_HOME;
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
}

