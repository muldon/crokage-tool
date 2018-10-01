package com.ufu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;



public class SimpleTester {

	public SimpleTester() {

		try {
			
			//working ... no modules necessary
			/*String path = "/home/rodrigo/projects/BIKER/StackOverflow/main/test2.py";
			ProcessBuilder pb = new ProcessBuilder(CrokageStaticData.PYTHON_HOME, path);
			Process p = pb.start();

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String ret = in.readLine();
			System.out.println("value is : " + ret);*/
			//---
			
			/*String userDir = System.getProperty("user.dir");
			ProcessBuilder pb = new ProcessBuilder(userDir+"/src/main/resources/test.sh");
			Process p = pb.start();
			p.waitFor();
			String output = loadStream(p.getInputStream());
	        String error  = loadStream(p.getErrorStream());
	        int rc = p.waitFor();
	        System.out.println("Process ended with rc=" + rc);
	        System.out.println("\nStandard Output:\n");
	        System.out.println(output);
	        System.out.println("\nStandard Error:\n");
	        System.out.println(error);*/
			//---
			
			/*List<QueryApis> bikerQueriesApis = new ArrayList<>();
			List<String> queriesWithApis = Files.readAllLines(Paths.get(CrokageStaticData.BIKER_OUTPUT_QUERIES_FILE), Charsets.UTF_8);
			for(String generatedLine: queriesWithApis) {
				String parts[] = generatedLine.split("=  ");
				String originalQuery = parts[0];
				//String rankedApis[] = parts[1].split("###");
				List<String> rankedApisList = new ArrayList<String>(Arrays.asList(parts[1].split("### ")).stream().map(String::trim).collect(Collectors.toList()));
				if(!rankedApisList.isEmpty() && rankedApisList.get(0).trim().equals("")) {
					rankedApisList.remove(0);
				}
				bikerQueriesApis.add(new QueryApis(originalQuery, rankedApisList));
				System.out.println("Biker - discovered classes for query: "+originalQuery+ " ->  " + rankedApisList.stream().limit(10).collect(Collectors.toList()));
				
			}
			System.out.println("Biker finished... ");*/
			
			
			//ProcessBuilder pb = new ProcessBuilder("/bin/bash","-c","export PYTHONPATH=/home/rodrigo/projects/BIKER/StackOverflow && exec");
			//Process process = pb.start();
			
			/*List<String>  processBuilderCommand = ImmutableList.of("/bin/bash","-c","echo $PYTHONPATH");

			ProcessBuilder processBuilder = new ProcessBuilder(processBuilderCommand).redirectErrorStream(true);
			processBuilder.environment().put("$PYTHONPATH","/home/rodrigo/projects/BIKER/StackOverflow");
			final Process process2 = processBuilder.start();

			BufferedReader in = new BufferedReader(new InputStreamReader(process2.getInputStream()));
			String ret = in.readLine();
			System.out.println("value is : " + ret);

			*/
			
			
			/*String path = "/home/rodrigo/projects/BIKER/StackOverflow/main/mytest.py";
			int exitCode = 1;
			try {
				String[] cmd = new String[2];
				cmd[0] = CrokageStaticData.PYTHON_HOME;
				cmd[1] = path;
				//cmd[0] = "echo $PATH";
				//cmd[1] = "$PYTHONPATH";
				String cmdLineStr = cmd[0] + " " + cmd[1];
				//String cmdLineStr = cmd[0];
				CommandLine cmdLine = CommandLine.parse(cmdLineStr);
				DefaultExecutor executor = new DefaultExecutor();
				exitCode = executor.execute(cmdLine);
			} catch (Exception exc) {
				exc.printStackTrace();
			}*/
			
			
			
			/*ProcessBuilder pb = new ProcessBuilder("/bin/bash","-c","echo $PATH");
			 //ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", CrokageStaticData.PYTHON_HOME, pathhh);
		        Map<String, String> env = pb.environment();
		        // set environment variable u
		        //env.put("PYTHONPATH", "/home/rodrigo/projects/BIKER/StackOverflow");

		        Process p = pb.start();
		    	BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String ret = in.readLine();
				System.out.println("value is : " + ret);
		        */
			
			
			
			/*String batchFile = CrokageStaticData.BAT_FILE_PATH2;
			String command = "cmd /c " + "./src/main/resources/test.bat";
			try {
				// System.out.println(command);
				Process p = Runtime.getRuntime().exec(command);
				try {
					p.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					 e.printStackTrace();
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				 e.printStackTrace();
				
			}*/
			
			
			
			/*String ar= "Integer.parseInt";
			String ar2 = "Collections.checkedCollection";
			String ar3 = "Files.readAllLines";
			
			List<String> list = new ArrayList<>();
			list.add(ar);
			list.add(ar2);
			list.add(ar3);
			List<String> classes = new ArrayList<>();
			
			for(String api: list) {
				classes.add(api.split("\\.")[0]); 
			}
			//System.out.println(classes);
			
			
			String str = "id:(1) | Refid: 1 - How do I convert number into Roman Numerals?";
			String parts[] = str.split(" - ");
			System.out.println(parts[1].trim());*/
			
			
			Set<String> set = new LinkedHashSet<>();
			set.add("1");
			set.add("2");
			set.add("3");
			set.add("1");
			set.add("2");
			set.add("2");
			set.add("2");
			//System.out.println(set);
			
			
			String apis = "RACK|BICKER|NLP2Api";
			String apisArrOrder[] = apis.split("\\|");
			System.out.println(apisArrOrder);
		        
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	
	 private static String loadStream(InputStream s) throws Exception {
	        BufferedReader br = new BufferedReader(new InputStreamReader(s));
	        StringBuilder sb = new StringBuilder();
	        String line;
	        while ((line = br.readLine()) != null)
	            sb.append(line).append("\n");
	        return sb.toString();
	    }
		

	public static void main(String[] args) {
		SimpleTester t = new SimpleTester();
	}
}
