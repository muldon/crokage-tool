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
			//System.out.println(apisArrOrder);
			
			
			String t = "Post [id=50662268, body=<p>You appear to have a number of issues. However, <code>D/ADB_SERVICES: closing because is_eof=1 r=-1 s-&gt;fde.force_eof=0</code> doesn't appear to be one of them. The D/ indicates that it is DEBUG message and shouldn't be a show stopper <em>(This appears to be a device (android implementation) dependant message some may get this message other may not)</em>.</p>\n" + 
					"\n" + 
					"<h3>Number Format Exception</h3>\n" + 
					"\n" + 
					"<p>Before the database and table gets created (assuming only the above activity) then the insert could fail (if the name input is not numeric) as you are trying to parse the <strong>name</strong> as an int for the <strong>weight</strong> and the <strong>height</strong>.</p>\n" + 
					"\n" + 
					"<p>You would have an exception along the lines of the following :-</p>\n" + 
					"\n" + 
					"<pre><code>06-03 00:08:31.518 2745-2745/va.vehicleapp E/AndroidRuntime: FATAL EXCEPTION: main\n" + 
					"    java.lang.NumberFormatException: Invalid int: \"Fred\"\n" + 
					"        at java.lang.Integer.invalidInt(Integer.java:138)\n" + 
					"        at java.lang.Integer.parse(Integer.java:375)\n" + 
					"        at java.lang.Integer.parseInt(Integer.java:366)\n" + 
					"        at java.lang.Integer.parseInt(Integer.java:332)\n" + 
					"        at va.vehicleapp.MainActivity.ClickMe(MainActivity.java:172)\n" + 
					"        at va.vehicleapp.MainActivity.access$000(MainActivity.java:16)\n" + 
					"        at va.vehicleapp.MainActivity$1.onClick(MainActivity.java:44)\n" + 
					"        at android.view.View.performClick(View.java:4084)\n" + 
					"        at android.view.View$PerformClick.run(View.java:16966)\n" + 
					"        at android.os.Handler.handleCallback(Handler.java:615)\n" + 
					"        at android.os.Handler.dispatchMessage(Handler.java:92)\n" + 
					"        at android.os.Looper.loop(Looper.java:137)\n" + 
					"        at android.app.ActivityThread.main(ActivityThread.java:4745)\n" + 
					"        at java.lang.reflect.Method.invokeNative(Native Method)\n" + 
					"        at java.lang.reflect.Method.invoke(Method.java:511)\n" + 
					"        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:786)\n" + 
					"        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:553)\n" + 
					"        at dalvik.system.NativeStart.main(Native Method)\n" + 
					"</code></pre>\n" + 
					"\n" + 
					"<p>As such you should change to use the following lines within the <code>onClick</code> method :-</p>\n" + 
					"\n" + 
					"<pre><code>    String weight = txtWeight.getText().toString(); // CHANGED to txtWeight not txtName\n" + 
					"    String height = txtHeight.getText().toString(); // CHANGED to txtHeight not txtName\n" + 
					"</code></pre>\n" + 
					"\n" + 
					"<h3>The Table will not be created</h3>\n" + 
					"\n" + 
					"<p>I suspect that if you look at the log you will see (or would have seen at some time, if you even got as far as invoking the onCreate method) something along the lines of :-</p>\n" + 
					"\n" + 
					"<pre><code>06-02 23:24:12.135 2113-2113/? E/SQLiteLog: (1) near \"TABLEDataOfSchedule_tableID\": syntax error\n" + 
					"06-02 23:24:12.135 2113-2113/? D/AndroidRuntime: Shutting down VM\n" + 
					"06-02 23:24:12.135 2113-2113/? W/dalvikvm: threadid=1: thread exiting with uncaught exception (group=0xa6244288)\n" + 
					"06-02 23:24:12.135 2113-2113/? E/AndroidRuntime: FATAL EXCEPTION: main\n" + 
					"    java.lang.RuntimeException: Unable to start activity ComponentInfo{va.vehicleapp/va.vehicleapp.MainActivity}: android.database.sqlite.SQLiteException: near \"TABLEDataOfSchedule_tableID\": syntax error (code 1): , while compiling: CREATE TABLEDataOfSchedule_tableID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT,AGE INTEGER NOT NULL DEFAULT 0, GENDER_M INTEGER NOT NULL DEFAULT 0, GENDER_F INTEGER NOT NULL DEFAULT 0, TRAUMA INTEGER NOT NULL DEFAULT 0, WEIGHT INTEGER NOT NULL DEFAULT 0, HEIGHT INTEGER NOT NULL DEFAULT 0);\n" + 
					"        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2059)\n" + 
					"        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2084)\n" + 
					"        at android.app.ActivityThread.access$600(ActivityThread.java:130)\n" + 
					"        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1195)\n" + 
					"        at android.os.Handler.dispatchMessage(Handler.java:99)\n" + 
					"        at android.os.Looper.loop(Looper.java:137)\n" + 
					"        at android.app.ActivityThread.main(ActivityThread.java:4745)\n" + 
					"        at java.lang.reflect.Method.invokeNative(Native Method)\n" + 
					"        at java.lang.reflect.Method.invoke(Method.java:511)\n" + 
					"        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:786)\n" + 
					"        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:553)\n" + 
					"        at dalvik.system.NativeStart.main(Native Method)\n" + 
					"     Caused by: android.database.sqlite.SQLiteException: near \"TABLEDataOfSchedule_tableID\": syntax error (code 1): , while compiling: CREATE TABLEDataOfSchedule_tableID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT,AGE INTEGER NOT NULL DEFAULT 0, GENDER_M INTEGER NOT NULL DEFAULT 0, GENDER_F INTEGER NOT NULL DEFAULT 0, TRAUMA INTEGER NOT NULL DEFAULT 0, WEIGHT INTEGER NOT NULL DEFAULT 0, HEIGHT INTEGER NOT NULL DEFAULT 0);\n" + 
					"        at android.database.sqlite.SQLiteConnection.nativePrepareStatement(Native Method)\n" + 
					"        at android.database.sqlite.SQLiteConnection.acquirePreparedStatement(SQLiteConnection.java:882)\n" + 
					"        at android.database.sqlite.SQLiteConnection.prepare(SQLiteConnection.java:493)\n" + 
					"        at android.database.sqlite.SQLiteSession.prepare(SQLiteSession.java:588)\n" + 
					"        at android.database.sqlite.SQLiteProgram.&lt;init&gt;(SQLiteProgram.java:58)\n" + 
					"        at android.database.sqlite.SQLiteStatement.&lt;init&gt;(SQLiteStatement.java:31)\n" + 
					"        at android.database.sqlite.SQLiteDatabase.executeSql(SQLiteDatabase.java:1663)\n" + 
					"        at android.database.sqlite.SQLiteDatabase.execSQL(SQLiteDatabase.java:1594)\n" + 
					"        at va.vehicleapp.DataBase.onCreate(DataBase.java:27)\n" + 
					"        at android.database.sqlite.SQLiteOpenHelper.getDatabaseLocked(SQLiteOpenHelper.java:252)\n" + 
					"        at android.database.sqlite.SQLiteOpenHelper.getWritableDatabase(SQLiteOpenHelper.java:164)\n" + 
					"        at va.vehicleapp.MainActivity.onCreate(MainActivity.java:23)\n" + 
					"        at android.app.Activity.performCreate(Activity.java:5008)\n" + 
					"        at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1079)\n" + 
					"        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2023)\n" + 
					"        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2084) \n" + 
					"        at android.app.ActivityThread.access$600(ActivityThread.java:130) \n" + 
					"        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1195) \n" + 
					"        at android.os.Handler.dispatchMessage(Handler.java:99) \n" + 
					"        at android.os.Looper.loop(Looper.java:137) \n" + 
					"        at android.app.ActivityThread.main(ActivityThread.java:4745) \n" + 
					"        at java.lang.reflect.Method.invokeNative(Native Method) \n" + 
					"        at java.lang.reflect.Method.invoke(Method.java:511) \n" + 
					"        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:786) \n" + 
					"        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:553) \n" + 
					"        at dalvik.system.NativeStart.main(Native Method) \n" + 
					"</code></pre>\n" + 
					"\n" + 
					"<p>This is because in your <strong>DataBase</strong> class you have a few omissions, spaces and an opening parenthesis, that will result in the table not being created.</p>\n" + 
					"\n" + 
					"<p>The fix for these is to </p>\n" + 
					"\n" + 
					"<ol>\n" + 
					"<li>change the <code>onCreate</code> method to (see comments for changes made)</li>\n" + 
					"</ol>\n" + 
					"\n" + 
					"<p>:-</p>\n" + 
					"\n" + 
					"<pre><code>@Override\n" + 
					"public void onCreate(SQLiteDatabase db){\n" + 
					"    db.execSQL(\"CREATE TABLE \" + TABLE_NAME + //&lt;&lt;&lt;&lt; ADDED space\n" + 
					"            \" (\" +  // ADDED to enclose column definition in brackets\n" + 
					"            COL_ID + \" INTEGER PRIMARY KEY AUTOINCREMENT, \" +\n" + 
					"            COL_NAME + \" TEXT,\" +\n" + 
					"            COL_AGE + \" INTEGER NOT NULL DEFAULT 0, \" +\n" + 
					"            COL_GENDER_M + \" INTEGER NOT NULL DEFAULT 0, \" +\n" + 
					"            COL_GENDER_F + \" INTEGER NOT NULL DEFAULT 0, \" +\n" + 
					"            COL_TRAUMA + \" INTEGER NOT NULL DEFAULT 0, \" +\n" + 
					"            COL_WEIGHT + \" INTEGER NOT NULL DEFAULT 0, \" +\n" + 
					"            COL_HEIGHT + \" INTEGER NOT NULL DEFAULT 0);\");\n" + 
					"}\n" + 
					"</code></pre>\n" + 
					"\n" + 
					"<ol start=\"2\">\n" + 
					"<li><p>either, delete the App's Data, uninstall the App or increase the Database version to 2 i.e. (change <code>super(context, DATABASE_NAME, null,1);</code> to <code>super(context, DATABASE_NAME, null,2);</code></p></li>\n" + 
					"<li><p>and then Rerun the App.</p></li>\n" + 
					"</ol>\n" + 
					"\n" + 
					"<h3>Testing Result</h3>\n" + 
					"\n" + 
					"<p>Making the above changes, running <em>(after deleting the App's Data important to do to create the correct table)</em>, completing the input fields using :-</p>\n" + 
					"\n" + 
					"<ul>\n" + 
					"<li>Fred,</li>\n" + 
					"<li>10,</li>\n" + 
					"<li>21,</li>\n" + 
					"<li>45,</li>\n" + 
					"<li>male (ticked), </li>\n" + 
					"<li>female (ticked), \n" + 
					"\n" + 
					"<ul>\n" + 
					"<li>able to tick both????\n" + 
					"-insert_trauma_subtitle not ticked </li>\n" + 
					"</ul></li>\n" + 
					"</ul>\n" + 
					"\n" + 
					"<p>resulted in Toast confirming insertion.</p>\n" + 
					", title=null, tags=null, postTypeId=2, acceptedAnswerId=null, parentId=50654857, creationDate=2018-06-03 00:19:17.017, score=0, viewCount=null, ownerUserId=4744514, lastEditorUserId=null, lastEditorDisplayName=null, lastEditDate=null, lastActivityDate=2018-06-03 00:19:17.017, answerCount=null, commentCount=0, favoriteCount=null, closedDate=null, communityOwnedDate=null, comments=null, user=null, titleVectors=null, tagVectors=null, bodyVectors=null, topicVectors=null, similarityScore=0.0, classesNames=null, topKrelatedQuestions=null, evaluation=null]";
			
			String mod = t.replaceAll("(?m)^.*?Exception.*(?:\\R+^\\s*at .*)+", "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
			//System.out.println(mod);
			
			
			String content = "public static <T> String getLoggingOutput(String prefix,\n" + 
					"        Set<ConstraintViolation<T>> violations) {\n" + 
					"    \n" + 
					"} public static <T> String getLoggingOutput(String prefix,\n" + 
					"        Set<ConstraintViolation<T>> violations) {\n" + 
					"    \n" + 
					"}";
			content = content.replaceAll("\\<([^)]+)\\>","");
			System.out.println(content);
			
		        
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
