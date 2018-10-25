package com.ufu;

import java.util.ArrayList;
import java.util.List;

import com.ufu.bot.config.CrokageStaticDataOld;
import com.ufu.crokage.util.CrokageUtils;
import com.ufu.crokage.util.TextNormalizer;

public class Nlp2ApiTester {
	public static void main(String[] args) {
		String searchQuery = "How do I convert number into Roman Numerals?";
		
		TextNormalizer textNormalizer = new TextNormalizer();
		textNormalizer.setContent(searchQuery);
		searchQuery = textNormalizer.normalizeTextLight();
		int TOPK = 10;
		int caseNo = 31;
		//String suggested = new CodeSearchBDAReformulator(caseNo, searchQuery, TOPK, "both").provideRelevantAPIs();
		//System.out.println(suggested);
		String CROKAGE_HOME = "/home/rodrigo/Dropbox/Doutorado/projects/bot";
		
		try {
			String jarPath = CROKAGE_HOME;
			//String command = "java -jar "+jarPath+ "/myNlp2Api.jar "+ "-K 10 -task reformulate -query How do I send an HTML email?";
			List<String> command = new ArrayList<String>();
		    
		    command.add("java");
		    command.add("-jar");
		    command.add(jarPath+"/myNlp2Api.jar");
		    command.add("-K");
		    command.add("10");
		    command.add("-task");
		    command.add("reformulate");
		    command.add("-query");
		    command.add("How to round a number to n decimal places in Java");
			
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
