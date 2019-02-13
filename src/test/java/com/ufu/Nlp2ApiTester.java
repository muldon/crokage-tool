package com.ufu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.SynchronizedMultivariateSummaryStatistics;

import com.ufu.crokage.util.CrokageUtils;
import com.ufu.crokage.util.TextNormalizer;

public class Nlp2ApiTester {
	public static void main(String[] args) {
		String rawQuery = "How to round a number to n decimal places in Java";
		
		TextNormalizer textNormalizer = new TextNormalizer();
		textNormalizer.setContent(rawQuery);
		rawQuery = textNormalizer.normalizeTextLight();
		String topk = "10";
		//String suggested = new CodeSearchBDAReformulator(caseNo, searchQuery, TOPK, "both").provideRelevantAPIs();
		//System.out.println(suggested);
		String CROKAGE_HOME = "/home/rodrigo/Dropbox/Doutorado/projects/crokage-tool";
		
		Set<String> nlp = null;
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
		    command.add(rawQuery);
			
			ProcessBuilder pb = new ProcessBuilder(command);
			Process p = pb.start();
			p.waitFor();
			String output = CrokageUtils.loadStream(p.getInputStream());
			String error = CrokageUtils.loadStream(p.getErrorStream());
			int rc = p.waitFor();
			//System.out.println("Process ended with rc=" + rc);
			//System.out.println("\nStandard Output:\n");
			System.out.println(output);
			
			String apis[] = output.split("\n");
			apis = ArrayUtils.remove(apis, 0);
			apis = ArrayUtils.remove(apis, 0);
			apis = ArrayUtils.remove(apis, 0);
			apis = ArrayUtils.remove(apis, 0);
			if(StringUtils.isBlank(apis[0])) {
				apis = ArrayUtils.remove(apis, 0);
			}
			apis = apis[0].split(" ");
			nlp = new LinkedHashSet<String>(Arrays.asList(apis));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(nlp);
		
	}
}
