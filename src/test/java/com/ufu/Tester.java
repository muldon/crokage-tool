package com.ufu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.ufu.bot.CrokageApp;
import com.ufu.bot.tfidf.TfIdf;
import com.ufu.bot.tfidf.ngram.NgramTfIdf;
import com.ufu.bot.to.BucketOld;
import com.ufu.bot.util.BotComposer;
import com.ufu.crokage.util.CrokageUtils;


public class Tester {

	public Tester() throws Exception {
		Integer a = -1;
		String s2 = "// There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added. \n" + 
				"    MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); \n" + 
				"    mc.addMailcap(\"text/html;; x-java-content-handler=com.sun.mail.handlers.text_html\"); \n" + 
				"    mc.addMailcap(\"text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml\"); \n" + 
				"    mc.addMailcap(\"text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain\"); \n" + 
				"    mc.addMailcap(\"multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed\"); \n" + 
				"    mc.addMailcap(\"message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822\"); \n" + 
				"    CommandMap.setDefaultCommandMap(mc);";
		
		s2= s2.replaceAll("\\/\\*;","");
		System.out.println(s2);
		
		 
		String str = "Hello I'm your     String";
		String[] splited = str.split("\\s+");
			
		for(String token:splited) {
			System.out.println(token);
		}
		
		System.out.println(CrokageUtils.round((double) 0 / 100,4)*100);
		
		String someJsonString = "{name:\"MyNode\", width:200, height:100}";
		JSONObject jsonObj = new JSONObject(someJsonString);
		//System.out.println(jsonObj.get);
		
		
		String path = "/home/rodrigo/tmp/disconsideredPostsBigMap.txt.gz";
		
		GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(path));
		BufferedReader br = new BufferedReader(new InputStreamReader(gzip));
		br.readLine();
		
		/*Archiver archiver = ArchiverFactory.createArchiver("zip", "gz");
		archiver.extract(new File(path), new File("/home/rodrigo/tmp") );
		*/
		
		 for(Enumeration<NetworkInterface> e
                 = NetworkInterface.getNetworkInterfaces();
            e.hasMoreElements(); )
	    {
	        NetworkInterface ni = e.nextElement();
	        System.out.println(ni.getName() + " - " + formatMac(ni.getHardwareAddress()));
	    }
		
		
		CrokageApp crokageApp = new CrokageApp();
		System.out.println(crokageApp.prepareQueryForCrawler("How can I insert an element in array at a given position?"));
		
		
		//stripDuplicatesFromFile(CrokageStaticDataOld.INPUT_QUERIES_FILE_NLP2API);
		
		
		str = "How do I send an HTML email? javascript javac dd JAVA";
		StringTokenizer st = new StringTokenizer(str);
		Boolean containToken = Pattern.compile(".*\\bjava\\b.*").matcher(str.toLowerCase()).find();
		String token;
		
		System.out.println(log2(16)/10);
		System.out.println(log2(14)/10);
		System.out.println(log2(12)/10);
		System.out.println(log2(10)/10);
		System.out.println(log2(8)/10);
		System.out.println(log2(4)/10);
		System.out.println(log2(1)/10);
		//System.out.println(containToken);
		
		/*System.out.println("---- Split by space ------");
		while (st.hasMoreElements()) {
			token = st2.nextElement();
			if()
		}

		System.out.println("---- Split by comma ',' ------");
		StringTokenizer st2 = new StringTokenizer(str, ",");

		while (st2.hasMoreElements()) {
			System.out.println(st2.nextElement());
		}
		
		
	
		
		System.out.println(query.contains("java"));*/
		
		
		
		
		Pattern p = Pattern.compile("[,. ]+");

		// repeat only the following part:
		//String output = p.matcher(input).replaceAll("");
		SortedSet<String> set = new TreeSet<>();
        set.add("One");
        set.add("Two");
        set.add("Three");
        set.add("Four");
        set.add("Five");
		System.out.println(getLastElement(set));
		System.out.println(set.last());
		
		//System.out.println(set.pollLast());
        //set.get(set.size()-1);
		
		String s = " just looking guidance code     calling given web service look like sample web service chosen example  ";
		//System.out.println(StringUtils.normalizeSpace(s));
		
		Set<String> wordsSet = new HashSet<>();
		wordsSet.addAll(Arrays.stream(s.split(" +")).collect(Collectors.toSet()));
		System.out.println(wordsSet);
		Thread.getAllStackTraces().keySet().forEach((t) -> System.out.println(t.getName() + "\nIs Daemon " + t.isDaemon() + "\nIs Alive " + t.isAlive()));
		ExecutorService e = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		// Do work using something like either
		e.execute(new Runnable() {
		        public void run() {
		            // do one task
		        }
		    });
	}
	
	
	 protected static String formatMac(byte[] mac) {
	        if (mac == null)
	            return "UNKNOWN";
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < mac.length; i++) {
	            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
	        }
	        return sb.toString();
	    }
	
	public Object getLastElement(final Collection c) {
	    final Iterator itr = c.iterator();
	    Object lastElement = itr.next();
	    while(itr.hasNext()) {
	        lastElement = itr.next();
	    }
	    return lastElement;
	}
	
	public static BitSet computePrimes(int limit)
	{
	    final BitSet primes = new BitSet();
	    primes.set(0, false);
	    primes.set(1, false);
	    primes.set(2, limit, true);
	    for (int i = 0; i * i < limit; i++)
	    {
	        if (primes.get(i))
	        {
	            for (int j = i * i; j < limit; j += i)
	            {
	                primes.clear(j);
	            }
	        }
	    }
	    return primes;
	}
	
	public static double log2(int n)
	{
	    return (Math.log(n) / Math.log(2));
	}
	
	
	 public static boolean isPalindrome(String s) {
	        return s.length() <= 1 ||
	            (s.charAt(0) == s.charAt(s.length() - 1) &&
	             isPalindrome(s.substring(1, s.length() - 1)));
	    }


	    public static boolean isPalindromeForgiving(String s) {
	        return isPalindrome(s.toLowerCase().replaceAll("[\\s\\pP]", ""));
	    }

	    public static BigInteger fact(int a) {
	        return a == 1 ? BigInteger.ONE : BigInteger.valueOf(a).multiply(fact(a - 1));
	    }
	

	private void testRemoveEmptyElementFromList() {
		List<String> strs = new ArrayList<>();
		strs.add("");
		strs.remove("");
		System.out.println(strs);
	}



	private void testContainLinkToSo(String text) {
		boolean contain = CrokageUtils.testContainLinkToSo(text);
		System.out.println(contain);
		
	}



	private void testContainCode(String another7Code) {
		// TODO Auto-generated method stub
		boolean containCode = CrokageUtils.containCode(another7Code);
		System.out.println(containCode);
		
	}



	private void testRandom() {
		for(int i=0; i<20; i++) {
			int randomNum = ThreadLocalRandom.current().nextInt(1, 3);
			if(randomNum==1) {
				System.out.println(randomNum);
			}else {
				System.out.println("nopz");
			}
			
		}
		
	}



	private void testReadAnswerBotQuestions() throws Exception {
		CrokageUtils CrokageUtils = new CrokageUtils();
		CrokageUtils.initializeConfigs();
		//List<ExternalQuestion> answerBotQuestionAnswers = CrokageUtils.readExternalQuestionsAndAnswers(true);
		//System.out.println(answerBotQuestionAnswers);
		
	}



	private void testBuildAnswerPostBucket() throws Exception {
		String bodyParent = "<p>Given <code>Iterator&lt;Element&gt;</code>, how can we convert that <code>Iterator</code> to <code>ArrayList&lt;Element&gt;</code> (or <code>List&lt;Element&gt;</code>) in the <strong>best and fastest</strong> way possible, so that we can use <code>ArrayList</code>'s operations on it such as <code>get(index)</code>, <code>add(element)</code>, etc.</p>";
		String bodyAnswer = "<p>Better use a library like <a href=\"https://google.github.io/guava/releases/20.0/api/docs/com/google/common/collect/Lists.html#newArrayList-java.util.Iterator-\" rel=\"noreferrer\">Guava</a>:</p>\n" + 
				"\n" + 
				"<pre><code>import com.google.common.collect.Lists;\n" + 
				"\n" + 
				"Iterator&lt;Element&gt; myIterator = ... //some iterator\n" + 
				"List&lt;Element&gt; myList = Lists.newArrayList(myIterator);\n" + 
				"</code></pre>\n" + 
				"\n" + 
				"<p>Another Guava example:</p>\n" + 
				"\n" + 
				"<pre><code>ImmutableList.copyOf(myIterator);\n" + 
				"</code></pre>\n" + 
				"\n" + 
				"<p>or <a href=\"http://commons.apache.org/collections/\" rel=\"noreferrer\">Apache Commons Collections</a>:</p>\n" + 
				"\n" + 
				"<pre><code>import org.apache.commons.collections.IteratorUtils;\n" + 
				"\n" + 
				"Iterator&lt;Element&gt; myIterator = ...//some iterator\n" + 
				"\n" + 
				"List&lt;Element&gt; myList = IteratorUtils.toList(myIterator);       \n" + 
				"</code></pre>\n" + 
				"";
		
		String bodyAnswer2 = "<p>In Java 8, you can use the new <code>forEachRemaining</code> method that's been added to the <code>Iterator</code> interface:</p>\n" + 
				"\n" + 
				"<pre><code>List&lt;Element&gt; list = new ArrayList&lt;&gt;();\n" + 
				"iterator.forEachRemaining(list::add);\n" + 
				"</code></pre>\n" + 
				"";
		
		String titleParent = "Convert Iterator to ArrayList";
		
		
		/*PitBotApp2 app = new PitBotApp2();
		Post answer = new Post();
		answer.setId(28491752);
		answer.setBody(bodyAnswer2);
		answer.setParentId(10117026);
				
		Post parent = new Post();
		parent.setId(10117026);
		parent.setTitle(titleParent);
		parent.setBody(bodyParent);*/
		
		//app.storeInCache(parent);
		
		//BucketOld bucket = app.buildAnswerPostBucket(answer);
		//System.out.println(bucket);
		
		
		
		
	}






	private void testReadJson() throws IOException, ParseException, JSONException {
		
		URL url = Resources.getResource("jsonExample.json");
		String json = Resources.toString(url, Charsets.UTF_8);
		
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject)parser.parse(json);
		// loop array
        JSONArray items = (JSONArray) jsonObject.get("items");
        Iterator<JSONObject> iterator = items.iterator();
        while (iterator.hasNext()) {
        	JSONObject item = iterator.next();
        	String link = (String) item.get("link");
            System.out.println(link);
        }
		
	}

	private void testStep8() throws IOException {
		URL url;
		String text1,text2,text3,text4,text5,text6,text7,text8,text9,text10;
		
		//String[] fileNames = {"ds_2012"};
		String[] fileNames = {"ds_2012","ds_2013","ds_2014","ds_2015","ds_CDS","ds_IBM","hp-cs.txt","hp-dh.txt","huck-finn.txt","les-mis.txt","50-shades.txt"};
		
		Set<BucketOld> bucketOlds = new LinkedHashSet<>();
		BucketOld main = new BucketOld();
		main.setPostId(0);
		//main.setProcessedBodyStemmedStopped("how it fits into the larger picture of an organization, explains IBM’s Jeff Jonas, distinguished");
		main.setProcessedBodyStemmedStopped("A data scientist represents an evolution from the business or data analyst role. The formal training is similar, with a solid foundation typically in computer science and applications, modeling, statistics, analytics and math. What sets the data scientist apart is strong business acumen, coupled with the ability to communicate findings to both business and IT leaders in a way that can influence how an organization approaches a business challenge. Good data scientists will not just address business problems, they will pick the right problems that have the most value to the organization.\n" + 
				"The data scientist role has been described as “part analyst, part artist.” Anjul Bhambhri, vice president of big data products at IBM, says, “A data scientist is somebody who is inquisitive, who can stare at data and spot trends. It's almost like a Renaissance individual who really wants to learn and bring change to an organization.\"\n" + 
				"Whereas a traditional data analyst may look only at data from a single source – a CRM system, for example – a data scientist will most likely explore and examine data from multiple disparate sources. The data scientist will sift through all incoming data with the goal of discovering a previously hidden insight, which in turn can provide a competitive advantage or address a pressing business problem. A data scientist does not simply collect and report on data, but also looks at it from many angles, determines what it means, then recommends ways to apply the data.\n" + 
				"Data scientists");
		
		//PitBotApp2 app = new PitBotApp2();
		//PitSurveyService pitSurveyService = new PitSurveyService();
		//app.setMainBucket(main);
		
		for(int i=0; i<fileNames.length; i++){
			url = Resources.getResource(fileNames[i]);
			text1 = Resources.toString(url, Charsets.UTF_8);
			BucketOld bucketOld = new BucketOld();
			bucketOld.setProcessedBodyStemmedStopped(text1);
			bucketOld.setPostId(i+1);
			bucketOlds.add(bucketOld);
		}
		
		
		List<BucketOld> rankedBuckets = step8OnlyTfIdf(bucketOlds,main);
		showBucketsOrderByCosineDesc(rankedBuckets);
	}
	
	
	public List<BucketOld> step8OnlyTfIdf(Set<BucketOld> bucketOlds, BucketOld mainBucket) {
		List<BucketOld> bucketsList = new ArrayList<>(bucketOlds);
		
		/*
		 * Calculate tfidf for all terms
		 */
		List<String> bucketsTexts = new ArrayList<>();
		bucketsTexts.add(mainBucket.getProcessedBodyStemmedStopped());
		for(BucketOld bucketOld: bucketOlds){
			bucketsTexts.add(bucketOld.getProcessedBodyStemmedStopped());
		}
		
		List<Collection<String>> documents =  Lists.newArrayList(NgramTfIdf.ngramDocumentTerms(Lists.newArrayList(1,2,3), bucketsTexts));
		List<Map<String, Double>> tfs = Lists.newArrayList(TfIdf.tfs(documents));
		Map<String, Double> idfAll = TfIdf.idfFromTfs(tfs);
		
		Map<String,Double> tfsMainBucket = tfs.remove(0);
		HashMap<String, Double> tfIdfMainBucket = (HashMap)TfIdf.tfIdf(tfsMainBucket, idfAll);
		//buckets.remove(buckets.iterator().next());
		HashMap<String, Double> tfIdfOtherBucket;
		
		int pos = 0;
		
		for(Map<String, Double> tfsMap: tfs){
			tfIdfOtherBucket = (HashMap)TfIdf.tfIdf(tfsMap, idfAll);
			BucketOld postBucket = bucketsList.get(pos);
			double cosine = BotComposer.cosineSimilarity(tfIdfMainBucket, tfIdfOtherBucket);
			postBucket.setCosSim(CrokageUtils.round(cosine,4));
			pos++;
		}
				
		
       return bucketsList;
	}

	
	
	private void showBucketsOrderByCosineDesc(List<BucketOld> bucketsList) {
		Collections.sort(bucketsList, new Comparator<BucketOld>() {
		    public int compare(BucketOld o1, BucketOld o2) {
		        return o2.getCosSim().compareTo(o1.getCosSim());
		    }
		});
		int pos=1;
		for(BucketOld bucketOld: bucketsList){
			System.out.print("Rank: "+(pos)+ " cosine: "+bucketOld.getCosSim()+" id: "+bucketOld.getPostId()+ " -\n "+bucketOld.getPresentingBody());
			pos++;
			if(pos>10){
				break;
			}
		}
		
	}
	
	
	
	private void readAnswerBotQuestionsAndAnswers() throws IOException {
		URL url;
		String fileContent="";
		String query="";
		String answer="";
				
		for(int i=0; i<100; i++){
			url = Resources.getResource(i+".txt");
			fileContent = Resources.toString(url, Charsets.UTF_8);
			
			List<String> lines = IOUtils.readLines(new StringReader(fileContent));
			query = lines.get(1);
			query = query.replace("query : ","");
			
			lines.remove(0);
			lines.remove(0);
			lines.remove(0);
			lines.remove(0);
			lines.remove(lines.size()-1);
			
			answer = lines.stream().collect(Collectors.joining("\n"));
			System.out.println(answer);
			
		}
		
	}
	
	
	

	/**
	 * @throws Exception 
	 * 
	 * 
	**/
	private void testGetClassesNames() throws Exception {
		String code1 = "//Main bucket\n" + 
				"		BucketOld mainBucket = new BucketOld();\n" + 
				"		mainBucket.setClassesNames(apis);\n" + 
				"		\n" + 
				"		String presentingBody = CrokageUtils.buildPresentationBody(googleQuery);\n" + 
				"		\n" + 
				"		List<String> codes = CrokageUtils.getCodes(presentingBody);\n" + 
				"		mainBucket.setCodes(codes);\n" + 
				"		\n" + 
				"		String processedBodyStemmedStopped = CrokageUtils.buildProcessedBodyStemmedStopped(presentingBody,false);\n" + 
				"		mainBucket.setProcessedBodyStemmedStopped(processedBodyStemmedStopped+apisNames);\n" + 
				"		\n" + 
				"		System.out.println(mainBucket);\n" + 
				"		\n" + 
				"		//Remaining buckets\n" + 
				"		Set<BucketOld> buckets = new HashSet<>();\n" + 
				"		\n" + 
				"		/**\n" + 
				"	* sss\n" + 
				"	* aaa\n" + 
				" 	**/ " + 
				"		for(SoThread thread: threads) {\n" + 
				"			\n" + 
				"			List<Post> answers = thread.getAnswers();\n" + 
				"			for(Post answer: answers) {\n" + 
				"				BucketOld bucket = buildBucket(answer,true);\n" + 
				"				buckets.add(bucket);\n" + 
				"			}\n" + 
				"		}\n" + 
				"\n" + 
				"\n" + 
				"public class Tester {\n" + 
				"\n" + 
				"	public Tester() throws Exception {\n" + 
				"		String str = \"How do I send an HTML email? javascript javac dd JAVA\";\n" + 
				"		StringTokenizer st = new StringTokenizer(str);\n" + 
				"		Boolean containToken = Pattern.compile(\".*\\\\bjava\\\\b.*\").matcher(str.toLowerCase()).find();\n" + 
				"		String token;\n" + 
				"		}\n" + 
				"}\n" + 
				"\n" + 
				"private Set<ProcessedPostOld> getAllPostsFromSet() {\n" + 
				"		Set<ProcessedPostOld> processedPostsByFilter = new HashSet();\n" + 
				"		\n" + 
				"		logger.info(\"now creating new TOs...\");\n" + 
				"		for(Post post: postsByFilter) {\n" + 
				"			ProcessedPostOld newPost = new ProcessedPostOld();\n" + 
				"			BeanUtils.copyProperties(post,newPost);\n" + 
				"			processedPostsByFilter.add(newPost);\n" + 
				"		}\n" + 
				"		logger.info(\"finished new TOs...\");\n" + 
				"		return processedPostsByFilter;\n" + 
				"	}\n" + 
				"\n" + 
				"";
		
	
		//remove java keywords
		for(String keyword: CrokageUtils.keywords){
			code1= code1.replaceAll(keyword,"");
		}
		
		//remove double quotes
		code1= code1.replaceAll(CrokageUtils.DOUBLE_QUOTES_REGEX_EXPRESSION,"");
		/*Pattern pattern0 = Pattern.compile(CrokageUtils.DOUBLE_QUOTES_REGEX_EXPRESSION);
		Matcher matcher0 = pattern0.matcher(code1);
		while (matcher0.find()) {
			System.out.println(matcher0.group(0));
		}*/
		
		//remove comments
		//System.out.println(code1);
		code1 = code1.replaceAll( "//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/", "$1 " );
		//System.out.println(code1);
		
		
		//Get classes in camel case
		String regex = "\\b[A-Z][a-z]*([A-Z][a-z]*)*\\b";
		
		Set<String> classes = new HashSet<>();
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(code1);
		while (matcher.find()) {
			classes.add(matcher.group(0));
		}
		
		/*for(String className: classes) {
			System.out.println(className);
		}*/
		
		
	
		
	}


	private String getIdFromStr(String str) {
		String parts[] = str.split("\\|");
		String s = parts[0].replaceAll("\\D+","");
		return s;
	}





	private void testMatrix() {
		//Matrix map
		int[] cells[] = new int[5][5];
		
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				System.out.println(cells[i][j]);
			}
		}
		
		
		
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
	
	
	public static void main(String[] args) throws Exception {
		Tester t = new Tester();
		
	}

	
	private void identifyQuestionsIdsFromUrls(List<String> urls, Set<Integer> soQuestionsIds) {
		for(String url: urls){
			//String[] urlPart = url.split("\\/[[:digit:]].*\\/");
			Pattern pattern = Pattern.compile("\\/([\\d]+)");
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				soQuestionsIds.add(new Integer(matcher.group(1)));
				System.out.println("Recovering question from URL: "+url);
			}
			//System.out.println(url);
			
		}
		
	}    
	
	
	private void testFindExternalQuestionNumber() {
		
		String regex = "([\\d]+)";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher("----------------- No.812 -----------------");
		while (matcher.find()) {
			System.out.println(matcher.group(0));
		}
		

	}
	
	
	enum Roman{
	    i(1),iv(4),v(5), ix(9), x(10);
	    int weight;

	    private Roman(int weight) {
	        this.weight = weight;
	    }
	};
	
	
	static String decToRoman(int dec){
	    String roman="";
	    Roman[] values=Roman.values();
	    for (int i = values.length-1; i>=0; i--) {
	       while(dec>=values[i].weight){
	           roman+=values[i];
	           dec=dec-values[i].weight;
	       }            
	    }
	    return roman;
	}

	
	public void stripDuplicatesFromFile(String filename) throws Exception {
	    BufferedReader reader = new BufferedReader(new FileReader(filename));
	    Set<String> lines = new LinkedHashSet<String>(); 
	    String line;
	    while ((line = reader.readLine()) != null) {
	        lines.add(line.trim());
	    }
	    reader.close();
	    BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
	    for (String unique : lines) {
	        writer.write(unique);
	        writer.newLine();
	    }
	    writer.close();
	}
	
	
	/*protected double getRecallK(ArrayList<String> recommended, ArrayList<String> goldSet, int K) {
		K = recommended.size() < K ? recommended.size() : K;
		double found = 0;
		for (int index = 0; index < K; index++) {
			String id = recommended.get(index);
			if (contains(id, goldSet)) {
					found++;
			}
		}
		return found / goldSet.size();
	}*/
	
	
}
