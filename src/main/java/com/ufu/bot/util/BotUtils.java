package com.ufu.bot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrTokenizer;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.ufu.bot.repository.GenericRepository;
import com.ufu.bot.to.Bucket;
import com.ufu.bot.to.ExternalQuestion;
import com.ufu.bot.to.Feature;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.Survey.SurveyEnum;
import com.ufu.bot.to.SurveyUser;



@Component
public class BotUtils {
	
	@Value("${minTokenSize}")
	public Integer minTokenSize;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static Map<String, String> sourceToMaster;
	private static CharArraySet stopWords;
	private AttributeFactory factory;
	private static StandardTokenizer standardTokenizer;
	private Boolean configsInitialized = false;
	private int countRecoveredPostsFromLinks=0;
	private static long endTime;
	private static Map<Integer, Set<Integer>> bucketDuplicatiosMap;
	//private Set<Integer> allDuplicatedQuestionsIds;
	//private static Set<PostLink> allPostLinks;
	private static Map<Integer, Set<Integer>> allPostLinks;
	private Map<Integer,Post> parentPostsCache;
	private Map<Integer,Post> answerPostsCache;
	

	@Value("${phaseNumber}")
	public Integer phaseNumber; 
	
	@Autowired
	private CosineSimilarity cs1;
	
	@Autowired
	protected GenericRepository genericRepository;
	
	
	public static final String PRE_CODE_REGEX_EXPRESSION = "(?sm)<pre><code>(.*?)</code></pre>";
	//public static final String CODE_REGEX_EXPRESSION = "(?sm)<pre.*?><code>(.*?)</code></pre>";
	public static final Pattern PRE_CODE_PATTERN = Pattern.compile(PRE_CODE_REGEX_EXPRESSION, Pattern.DOTALL); 
	
	public static final String CODE_MIN_REGEX_EXPRESSION = "(?sm)<code>(.*?)</code>";
	public static final Pattern CODE_MIN_PATTERN = Pattern.compile(CODE_MIN_REGEX_EXPRESSION, Pattern.DOTALL); 
	
	public static final String BLOCKQUOTE_EXPRESSION = "(?sm)<blockquote>(.*?)</blockquote>";
	public static final Pattern BLOCKQUOTE_PATTERN = Pattern.compile(BLOCKQUOTE_EXPRESSION, Pattern.DOTALL);
	
	//public static final String LINK_EXPRESSION_OUT = "(?sm)<a href=(.*?) rel=\"nofollow\">";
	//public static final String LINK_EXPRESSION_OUT = "(?sm)<a href=(.*?)>";
	public static final String LINK_EXPRESSION_OUT = "(?sm)<a href=\"(.*?)\"(.*?)</a>";
	public static final Pattern LINK_PATTERN = Pattern.compile(LINK_EXPRESSION_OUT, Pattern.DOTALL);
	
	public static final String LINK_TARGET_EXPRESSION_OUT = "(?sm)http(.*?)\\s";
	public static final Pattern LINK_TARGET_PATTERN = Pattern.compile(LINK_TARGET_EXPRESSION_OUT, Pattern.DOTALL);
	
	
	public static final String IMG_EXPRESSION_OUT = "(?sm)<img (.*?)>";
	public static final Pattern IMG_PATTERN = Pattern.compile(IMG_EXPRESSION_OUT, Pattern.DOTALL);
	
	public static final String ONLY_WORDS_EXPRESSION = "(?<!\\S)\\p{Alpha}+(?!\\S)";
	public static final Pattern ONLY_WORDS_PATTERN = Pattern.compile(ONLY_WORDS_EXPRESSION, Pattern.DOTALL);
	
	public static final String NOT_ONLY_WORDS_EXPRESSION = "(?<!\\S)(?!\\p{Alpha}+(?!\\S))\\S+";
	public static final Pattern NOT_ONLY_WORDS_PATTERN = Pattern.compile(NOT_ONLY_WORDS_EXPRESSION, Pattern.DOTALL);
	
	
	public static final String COMMENTS_REGEX_EXPRESSION = "//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/";
	public static final String COMMENTS_REPLACEMENT_EXPRESSION = "$1 ";
	
	private final String TMP_PATH = "/home/rodrigo/tmp/";
	
	public static final String CLASSES_CAMEL_CASE_REGEX_EXPRESSION = "\\b[A-Z][a-z]*([A-Z][a-z]*)*\\b";
	
	public static final String DOUBLE_QUOTES_REGEX_EXPRESSION = "\"(.*?)\"";
	
	public static final String NUMBERS_REGEX_EXPRESSION = "([\\d]+)";
		
	
	public static final String keywords[] = { "abstract", "assert", "boolean",
            "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "extends", "false",
            "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native",
            "new", "null", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true",
            "try", "void", "volatile", "while" };

   	
	
	private static String htmlTags[] = {"<p>","</p>","<pre>","</pre>","<blockquote>","</blockquote>", /*"<a href=\"","\">",*/
			"</a>","<img src=","alt=","<ol>","</ol>","<li>","</li>","<ul>",
			"</ul>","<br>","</br>","<h1>","</h1>","<h2>","</h2>","<strong>","</strong>",
			"<code>","</code>","<em>","</em>","<hr>"};
	
	private static String htmlTagsWithCodeAndBlockquotes[] = {"<p>","</p>", /*"<a href=\"","\">",*/
			"</a>","<img src=","alt=","<ol>","</ol>","<li>","</li>","<ul>",
			"</ul>","<br>","</br>","<h1>","</h1>","<h2>","</h2>","<strong>","</strong>",
			"<em>","</em>","<hr>"};
	
	//stemmed - stopped
	//private static String unncessaryWords[] = {"java", "how", "what"};
	private static String unncessaryWords[] = {}; //td-idf comparates a query with an answer content, but this content is reinforced with its parent title and body
	
	public BotUtils() throws Exception {
		initializeConfigs();
	}
	
	
	public void initializeConfigs() throws Exception {
		if(!configsInitialized){
			configsInitialized = true;
				
			factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
			standardTokenizer = new StandardTokenizer(factory);
			stopWords = EnglishAnalyzer.getDefaultStopSet();
			
			standardTokenizer.close();
			loadTagSynonyms();
		}
		
		if(minTokenSize==null) {
			Properties prop = new Properties();
			prop.load(BotUtils.class.getClassLoader().getResourceAsStream("application.properties")); 
			minTokenSize = new Integer(prop.getProperty("minTokenSize"));
			
		}
		parentPostsCache = new HashMap<>();
		parentPostsCache = new HashMap<Integer, Post>();
		answerPostsCache = new HashMap<Integer, Post>();
				
	}
	
	
	 public static double round(double pNumero, int pCantidadDecimales) {
	    // the function is call with the values Redondear(625.3f, 2)
	    BigDecimal value = new BigDecimal(pNumero);
	    value = value.setScale(pCantidadDecimales, RoundingMode.HALF_EVEN); // here the value is correct (625.30)
	    return value.doubleValue(); // but here the values is 625.3
	}
	
	
	public String tokenizeStopStem(String input) throws Exception {
		String token;
		if (StringUtils.isBlank(input)) {
			return "";
		}
		StringReader sr = new StringReader(input);
		
		standardTokenizer.setReader(sr);
		TokenStream stream = new StopFilter(new LowerCaseFilter(new PorterStemFilter(standardTokenizer)), stopWords);

		CharTermAttribute charTermAttribute = standardTokenizer.addAttribute(CharTermAttribute.class);
		stream.reset();

		StringBuilder sb = new StringBuilder();
		while (stream.incrementToken()) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			token = charTermAttribute.toString();
			if(token.length()>minTokenSize) {
				sb.append(token);
			}
			
		}

		stream.end();
		stream.close();
		token = null;
		sr = null;
		return sb.toString();
	}
	
	
	public String removeStopWords(String textFile) throws Exception {
		String token;
		if (StringUtils.isBlank(textFile)) {
			return "";
		}
		StringReader sr = new StringReader(textFile);
		standardTokenizer.setReader(sr);
		
		TokenStream stream = new StopFilter(standardTokenizer, stopWords);

		CharTermAttribute charTermAttribute = standardTokenizer.addAttribute(CharTermAttribute.class);
		stream.reset();

		StringBuilder sb = new StringBuilder();
		while (stream.incrementToken()) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			token = charTermAttribute.toString();
			if(token.length()>minTokenSize) {
				sb.append(token);
			}
			
		}

		stream.end();
		stream.close();
		token = null;
		sr = null;
		return sb.toString();
	}
	
	
	
	
	public void reportElapsedTime(long initTime, String processName) {
		
		endTime = System.currentTimeMillis();
		String duration = DurationFormatUtils.formatDuration(endTime-initTime, "HH:mm:ss,SSS");
		logger.info("Elapsed time: "+duration+ " of the execution of  "+processName);
		
	}
	
	
	
	
	
	private static String translateHTMLSimbols(String finalContent) {
		finalContent = finalContent.replaceAll("&amp;","&");
		finalContent = finalContent.replaceAll("&lt;", "<");
		finalContent = finalContent.replaceAll("&gt;", ">");
		finalContent = finalContent.replaceAll("&quot;", "\"");
		finalContent = finalContent.replaceAll("&apos;", "\'"); 
		
		return finalContent;
	}
	
	
	public static List<String> getCodeValues(Pattern patter,String str) {
	    final List<String> tagValues = new ArrayList<String>();
	    final Matcher matcher = patter.matcher(str);
	    while (matcher.find()) {
	        tagValues.add(matcher.group(1));
	    }
	    return tagValues;
	}
	

	public static List<String> getPreCodes(String presentingBody) {
		//String codeContent = "";
		List<String> codes = getCodeValues(PRE_CODE_PATTERN, presentingBody);
		//int i=0;
		/*for(String code: codes){
			//codeContent+= "code "+(i+1)+":"+code+ "\n\n";
			codeContent+= code+ "\n\n";
			//i++;
		}*/
		//logger.info("\nCodes: \n"+codeContent);
		return codes;
	}
	
	public static List<String> getSimpleCodes(String presentingBody) {
		//first remove pre codes
		
		presentingBody = presentingBody.replaceAll(PRE_CODE_REGEX_EXPRESSION, " ");
		//String codeContent="";
		List<String> smallCodes = getCodeValues(CODE_MIN_PATTERN, presentingBody);
		/*for(String code: smallCodes){
			codeContent+= code+ "\n\n";
		}*/
		
		return smallCodes;
	}

	/**
	 * Remove images and html tags, except codes. For the links, leaves only the target.
	 * @param body
	 * @return
	 */
	public String buildPresentationBody(String body, boolean removeHtmlTags) {
		
		String finalBody = translateHTMLSimbols(body);
		
		finalBody = finalBody.replaceAll(IMG_EXPRESSION_OUT, " ");
		
		finalBody = extractLinksTargets(finalBody);
		
		if(removeHtmlTags) {
			finalBody = removeHtmlTagsExceptCode(finalBody);
		}
		
		return finalBody;
	}
	

	public Set<String> getClassesNames(List<String> codes) {

		Set<String> classesNames = new HashSet();
		for(String code: codes) {
			getClassesNamesForString(classesNames,code);
		}
		return classesNames;
				
	}

	public void getClassesNamesForString(Set<String> classesNames, String code) {
		//remove java keywords
		for(String keyword: BotUtils.keywords){
			code= code.replaceAll(keyword,"");
		}
		
		//remove double quotes contents
		code= code.replaceAll(BotUtils.DOUBLE_QUOTES_REGEX_EXPRESSION,"");
		
		//remove comments
		code = code.replaceAll( BotUtils.COMMENTS_REGEX_EXPRESSION, BotUtils.COMMENTS_REPLACEMENT_EXPRESSION );
		
		//Get classes in camel case
		Pattern pattern = Pattern.compile(BotUtils.CLASSES_CAMEL_CASE_REGEX_EXPRESSION);
		Matcher matcher = pattern.matcher(code);
		while (matcher.find()) {
			if(matcher.group(0)!=null && matcher.group(0).length()>2) {
				classesNames.add(matcher.group(0));
			}
			
		}
		
	}
	

	public String buildProcessedBodyStemmedStopped(String presentingBody, boolean isAnswer) throws Exception {
		
		String finalStr = presentingBody.replaceAll(LINK_TARGET_EXPRESSION_OUT, "");
		
		//finalStr = finalStr.replaceAll(CODE_MIN_REGEX_EXPRESSION, "");
		//finalStr = finalStr.replaceAll("<code>", " ").replaceAll("</code>", " ");
		
		//blockquotes
		String blockquoteContent = "";
		List<String> blockquotes = getCodeValues(BLOCKQUOTE_PATTERN, finalStr);
		for(String blockquote: blockquotes){
			blockquoteContent+= blockquote+ "\n\n";
		}
		
		String textWithoutCodesLinksAndBlackquotes = finalStr.replaceAll(BLOCKQUOTE_EXPRESSION, " ");
		
		
		String codeContent = "";
		List<String> codes = getCodeValues(PRE_CODE_PATTERN, textWithoutCodesLinksAndBlackquotes);
		//int i=0;
		for(String code: codes){
			codeContent+= code+ "\n\n";
		}
		
		textWithoutCodesLinksAndBlackquotes = textWithoutCodesLinksAndBlackquotes.replaceAll(PRE_CODE_REGEX_EXPRESSION, " ");
		
		List<String> smallCodes = getCodeValues(CODE_MIN_PATTERN, textWithoutCodesLinksAndBlackquotes);
		for(String code: smallCodes){
			codeContent+= code+ "\n\n";
		}
		
		
		textWithoutCodesLinksAndBlackquotes = textWithoutCodesLinksAndBlackquotes.replaceAll(CODE_MIN_REGEX_EXPRESSION, " ");
		
		textWithoutCodesLinksAndBlackquotes = removeOtherSymbolsAccordingToPostPart(textWithoutCodesLinksAndBlackquotes,isAnswer);
		
		String onlyWords = getOnlyWords(textWithoutCodesLinksAndBlackquotes);
		
		onlyWords = onlyWords.toLowerCase()+ " ";
		
		String specificTerms = "";
		List<String> notOnlyWords = getWords(NOT_ONLY_WORDS_PATTERN, textWithoutCodesLinksAndBlackquotes);
		for(String word: notOnlyWords){
			specificTerms+= word+ " ";
		}
		
		specificTerms = removeSpecialSymbols(specificTerms.trim());
		specificTerms = specificTerms.replaceAll("\\b\\w{1,1}\\b\\s?", "");   //remove single small tokens like "s"
		
		String stoppedStemmed = tokenizeStopStem(onlyWords.trim());
		
		//finalStr = stoppedStemmed + " "+specificTerms+ " "+blockquoteContent+ " "+codeContent;
		finalStr = stoppedStemmed + " "+specificTerms+ " "+blockquoteContent;
		//System.out.println(finalStr);
		codeContent = null;
		textWithoutCodesLinksAndBlackquotes= null;
		specificTerms = null;
		stoppedStemmed = null;
		onlyWords = null;
		blockquoteContent = null;
		//replace \n to space
		//replace more than one space by one space only
		finalStr = StringUtils.normalizeSpace(finalStr);
		
		return finalStr;
	}

	
	public String[] separateWordsCodePerformStemmingStopWords(String content, boolean isAnswer) throws Exception {
		String[] finalContent = new String[4];
		
		//volta simbolos de marcacao HTML para estado original 
		String originalSymbols = translateHTMLSimbols(content);		
		
		String textWithoutCodesLinksAndBlackquotes = originalSymbols.replaceAll(LINK_EXPRESSION_OUT, " ");
		
		textWithoutCodesLinksAndBlackquotes = textWithoutCodesLinksAndBlackquotes.replaceAll(IMG_EXPRESSION_OUT, " ");
		
		//blockquotes
		String blockquoteContent = "";
		List<String> blockquotes = getCodeValues(BLOCKQUOTE_PATTERN, textWithoutCodesLinksAndBlackquotes);
		for(String blockquote: blockquotes){
			blockquoteContent+= blockquote+ " ";
		}
		blockquoteContent = removeHtmlTags(blockquoteContent);
		blockquoteContent = blockquoteContent.replaceAll("\n", " ");
		textWithoutCodesLinksAndBlackquotes = textWithoutCodesLinksAndBlackquotes.replaceAll(BLOCKQUOTE_EXPRESSION, " ");
		
		//codes
		String codeContent = "";
		List<String> codes = getCodeValues(PRE_CODE_PATTERN, textWithoutCodesLinksAndBlackquotes);
		//int i=0;
		for(String code: codes){
			//codeContent+= "code "+(i+1)+":"+code+ "\n\n";
			codeContent+= code+ "\n\n";
			//i++;
		}
		
		//codeContent = codeContent.replaceAll("\n", " ");
		codeContent = removeHtmlTags(codeContent);
					
		//textoSemCodigosLinksEBlackquotes = textoSemCodigosLinksEBlackquotes.replaceAll(BLOCKQUOTE_EXPRESSION, " ");
		//String codeToBeJoinedInBody = retiraSimbolosCodeParaBody(codeContent);
				
		//String bloquesAndLinks = blockquoteContent+ " "+linksContent;
		/*String bloquesAndLinks = blockquoteContent;
		bloquesAndLinks = bloquesAndLinks.replaceAll("\n", " ");
		bloquesAndLinks = retiraHtmlTags(bloquesAndLinks);
		bloquesAndLinks += " "+codeContent;*/
		
		
		textWithoutCodesLinksAndBlackquotes = textWithoutCodesLinksAndBlackquotes.replaceAll(PRE_CODE_REGEX_EXPRESSION, " ");
		textWithoutCodesLinksAndBlackquotes = removeHtmlTags(textWithoutCodesLinksAndBlackquotes);
		
		//trata antes de pegar somenta as palavras
		
		String separated = removeOtherSymbolsAccordingToPostPart(textWithoutCodesLinksAndBlackquotes,isAnswer);
		
		
		String onlyWords = getOnlyWords(separated);
		/*List<String> palavras = getWords(ONLY_WORDS_PATTERN, separated);
		for(String word: palavras){
			somentePalavras+= word+ " ";
		}*/
		onlyWords = onlyWords.toLowerCase()+ " ";
		
		String specificTerms = "";
		List<String> naoPalavras = getWords(NOT_ONLY_WORDS_PATTERN, separated);
		for(String word: naoPalavras){
			specificTerms+= word+ " ";
		}
		specificTerms+= blockquoteContent;
		
		specificTerms = removeSpecialSymbols(specificTerms);
		
		String stoppedStemmed = tokenizeStopStem(onlyWords.trim());
		
		finalContent[0] = stoppedStemmed; //+ " "+somentePalavrasCodeClean;
		finalContent[1] = specificTerms.trim();
		finalContent[2] = codeContent.trim();
		
		return finalContent;
		

	}
	
	
	
	
	

	public static String getOnlyWords(String separated) {
		String somentePalavras="";
		List<String> palavras = getWords(ONLY_WORDS_PATTERN, separated);
		for(String word: palavras){
			somentePalavras+= word+ " ";
		}
		return somentePalavras;
		
	}



	public String removeOtherSymbolsAccordingToPostPart(String finalContent, boolean isAnswer) {
		if(!isAnswer){
			finalContent = finalContent.replaceAll("\\?", " ");
		}else {
			finalContent = finalContent.replaceAll("\\:", " ");
		}
		return finalContent;
	}
	
	
	
	/**
	 * Retira tags de marcação do body ou title  
	 */
	public static String removeHtmlTags(String content) {
		if(content==null || content.trim().equals("")){
			return "";
		}
		//boolean startedHtml = false;
		for(String tag: htmlTags){
			content= content.replaceAll(tag," ");
		}
		//content = content.replaceAll("<a href=", " ");
		//content = content.replaceAll("</a>", " ");

		return content;
		
	}
	
	/**
	 * Retira tags de marcação do body ou title  
	 */
	public static String removeHtmlTagsExceptCode(String content) {
		if(content==null || content.trim().equals("")){
			return "";
		}
		//boolean startedHtml = false;
		for(String tag: htmlTagsWithCodeAndBlockquotes){
			content= content.replaceAll(tag," ");
		}
		//content= content.replaceAll("<code>","\n<code>\n");
		//content= content.replaceAll("</code>","\n</code>\n");
		//content = content.replaceAll("<a href=", " ");
		//content = content.replaceAll("</a>", " ");

		return content;
		
	}
	
	
	public static List<String> getWords(Pattern patter,String str) {
	    final List<String> tagValues = new ArrayList<String>();
	    final Matcher matcher = patter.matcher(str);
	    while (matcher.find()) {
	        tagValues.add(matcher.group(0));
	    }
	    return tagValues;
	}
	
	
	
	
	
	public static String removeSpecialSymbols(String finalContent) {
		
		//nao precisam de espaco
		finalContent = finalContent.replaceAll("\\+"," ");
		finalContent = finalContent.replaceAll("\\^", " ");
		finalContent = finalContent.replaceAll(":", " ");
		finalContent = finalContent.replaceAll(";", " ");
		finalContent = finalContent.replaceAll("-", " ");
		finalContent = finalContent.replaceAll("\\+", " ");
		finalContent = finalContent.replaceAll("&", " ");
		finalContent = finalContent.replaceAll("\\*", " ");
		finalContent = finalContent.replaceAll("\\~", " ");
		finalContent = finalContent.replaceAll("\\\\", " ");
		finalContent = finalContent.replaceAll("/", " ");
		//finalContent = finalContent.replaceAll("\\'", "simbaspassimpl");
		finalContent = finalContent.replaceAll("\\`", " ");
		finalContent = finalContent.replaceAll("\"", " ");
		finalContent = finalContent.replaceAll("\\(", " ");
		finalContent = finalContent.replaceAll("\\)", " ");
		finalContent = finalContent.replaceAll("\\[", " ");
		finalContent = finalContent.replaceAll("\\]", " ");
		finalContent = finalContent.replaceAll("\\{", " ");
		finalContent = finalContent.replaceAll("\\}", " ");
		finalContent = finalContent.replaceAll("\\?", " ");
		finalContent = finalContent.replaceAll("\\|", " ");
		finalContent = finalContent.replaceAll("\\%", " ");
		finalContent = finalContent.replaceAll("\\$", " ");
		finalContent = finalContent.replaceAll("\\@", " ");
		finalContent = finalContent.replaceAll("\\<", " ");
		finalContent = finalContent.replaceAll("\\>", " ");
		finalContent = finalContent.replaceAll("\\#", " ");
		finalContent = finalContent.replaceAll("\\=", " ");
		finalContent = finalContent.replaceAll("\\.", " ");
		finalContent = finalContent.replaceAll("\\,", " ");
		finalContent = finalContent.replaceAll("\\_", " ");
		finalContent = finalContent.replaceAll("\\!", " ");
		finalContent = finalContent.replaceAll("\\'", " ");		
		
		finalContent = finalContent.replaceAll("\n", " ");
		
		return finalContent;
	}
	
	
public static String removeSpecialSymbolsTitles(String finalContent) {
		
		finalContent = removeSpecialSymbols(finalContent);
		finalContent = finalContent.replaceAll("\\?", "");
		
		return finalContent;
	}




	public static String getQueryComplementByTag(String tagFilter) {
		String query="";
		if(tagFilter!=null && !"".equals(tagFilter)) {
			if(tagFilter.equals("java")) {
				query += " and tags like '%java%' and tags not like '%javascript%' "; 
			}else {
				query += " and tags like '%"+tagFilter+"%'";
			}
		}
		return query;
		
	}
	
	
	public static String getQueryComplementByTagStrict(String tagFilter) {
		String query="";
		if(tagFilter!=null && !"".equals(tagFilter)) {
			query += " and tags like '%<"+tagFilter+">%'";
			
		}
		return query;
		
	}

	
	public static String tagMastering(String tags) throws Exception {
		if (tags == null) {
			return "";
		}
		StrTokenizer tokenizer = new StrTokenizer(tags);
		Set<String> tagsSet = new HashSet<>();
		for (String token : tokenizer.getTokenArray()) {

			String master = sourceToMaster.get(token);
			if (master == null) {
				master = token;
			}
			tagsSet.add(master);
		}
		String str = StringUtils.join(tagsSet, " ");
		tokenizer = null;
		tagsSet = null;
		 				
		return str;
	}


	public void loadTagSynonyms() throws Exception {
		sourceToMaster = new HashMap<>();

		String csvFile = "/tagSynonyms.csv";
		String line = "";
		String cvsSplitBy = ",";

		InputStream in = getClass().getResourceAsStream(csvFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		try (BufferedReader br = reader) {
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] tags = line.split(cvsSplitBy);
				sourceToMaster.put(tags[0], tags[1]);
			}
		} catch (IOException e) {
			throw e;
		}
	}


	public static <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValuesDesc(Map<K, V> map) {

		List<Entry<K, V>> sortedEntries = new LinkedList<Entry<K, V>>(map.entrySet());

		Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> e1, Entry<K, V> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedEntries;
	}

	public static <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K, V> map) {

		List<Entry<K, V>> sortedEntries = new LinkedList<Entry<K, V>>(map.entrySet());

		Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> e1, Entry<K, V> e2) {
				return e1.getValue().compareTo(e2.getValue());
			}
		});

		return sortedEntries;
	}
	
	public void getPostsLinks() {
		logger.info("Retrieving PostLinks... ");
		if(allPostLinks==null){
			allPostLinks = genericRepository.getAllPostLinks();
		}
		logger.info("PostLinks retrieved: " + allPostLinks.size());

	}
	
	public void generateBuckets() {
		if(bucketDuplicatiosMap==null){
			bucketDuplicatiosMap = new HashMap<Integer, Set<Integer>>();	
			getPostsLinks();
			//Pode haver mais de uma duplicada por questao.. Bucket structure
			logger.info("Building buckets");
					
			for(Map.Entry<Integer, Set<Integer>> entry : allPostLinks.entrySet()){
				
				Integer postId = entry.getKey();
				Set<Integer> mastersIds = entry.getValue(); 
				
				for(Integer masterId: mastersIds) {
					Set<Integer> duplicatedOfMaster = bucketDuplicatiosMap.get(masterId);  
					if(duplicatedOfMaster==null){
						duplicatedOfMaster = new HashSet<Integer>();
						bucketDuplicatiosMap.put(masterId,duplicatedOfMaster);
					}
					duplicatedOfMaster.add(postId);
				}
				
			}
			
			logger.info("Buckets gerados...");
		}
	}
	

	public static Map<Integer, Set<Integer>> getBucketDuplicatiosMap() {
		return bucketDuplicatiosMap;
	}

		
	
	public static String getDataBase(String fullPath) {
		String dataBaseName[] = fullPath.split("5432/?");
		return dataBaseName[1];
	}
	
	
	public static Set<Integer> getRelatedPostIds(Integer questionId) {
		//Set<Integer> relatedPostIds = new HashSet<Integer>();
		/*for(PostLink postLink: allPostLinks){
			if(postLink.getPostId().equals(questionId)){
				relatedPostIds.add(postLink.getRelatedPostId());
			}
		}*/
		//Set<Integer> relatedPostsIdsTmp = allPostLinks.get(questionId);
		
		return allPostLinks.get(questionId);
	}

	
	
	
	
	
	public Feature computeFeatures(String string1, String string2) throws Exception {
		Feature f = new Feature();
		f.setCosine(calculateCosine(string1,string2));
		return f;
	}
	
	public double calculateCosine(String string1, String string2) throws Exception {
		if(string1==null || string2==null){
			return 0d;
		}
		string1 = string1.trim();
		string2 = string2.trim();
		if(string1.equals("") || string2.equals("")){
			return 0d;
		}
		
		double sim_score = cs1.getCosineSimilarityScore(string1,string2);
		return sim_score;
	}



	
	
	/*public void removePostsWithIncompleteTitlesOld(Set<ProcessedPostOld> processedPostsByFilter, Set<ProcessedPostOld> closedDuplicatedNonMastersByTag) {
		logger.info("Removing invalid posts. Processedposts before: "+processedPostsByFilter.size());
		StringTokenizer st = null;
		Set<ProcessedPostOld> invalidPosts = new HashSet<>();
		
		
			
		for(ProcessedPostOld processedPosts: processedPostsByFilter) {
			st = new StringTokenizer(processedPosts.getTitle());
			if(st.countTokens() <= 3) {
				invalidPosts.add(processedPosts);
			}
			st = null;
		}
		
		processedPostsByFilter.removeAll(invalidPosts);
		logger.info("Processed posts after removing invalid posts: "+processedPostsByFilter.size()+"\nInvalid posts: "+invalidPosts.size());
		
		
		logger.info("...now removing invalid posts from closedDuplicatedNonMastersIdsByTag. Before: "+closedDuplicatedNonMastersByTag.size());
		invalidPosts = new HashSet<>();
		
		for(ProcessedPostOld processedPosts: closedDuplicatedNonMastersByTag) {
			st = new StringTokenizer(processedPosts.getTitle());
			if(st.countTokens() <= 3) {
				invalidPosts.add(processedPosts);
			}
			st = null;
		}
		
		closedDuplicatedNonMastersByTag.removeAll(invalidPosts);
		logger.info("closedDuplicatedNonMastersIdsByTag after removing invalid posts: "+closedDuplicatedNonMastersByTag.size()+"\nInvalid posts: "+invalidPosts.size());
				
	}*/
	
	
	
	
	
	
	public static <K, V> void printMap(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
        }
    }


	public String extractLinksTargets(String strInput) {
		final Matcher matcher = LINK_PATTERN.matcher(strInput);
	    while (matcher.find()) {
	    /*    System.out.println(matcher.group(0));
	        System.out.println(matcher.group(1));
	        System.out.println(matcher.group(2));*/
	        
	        strInput = strInput.replace(matcher.group(0), matcher.group(1));
	        strInput = strInput.replace(matcher.group(2), "");
	        
	        
	    }
		return strInput;
	}
	
	
	public String removeLinkTargets(String strInput) {
		final Matcher matcher = LINK_TARGET_PATTERN.matcher(strInput);
	    while (matcher.find()) {
	        strInput = strInput.replace(matcher.group(0), "");
	    }
		return strInput;
	}



	 public static boolean isJavaKeyword(String keyword) {
	        return (Arrays.binarySearch(keywords, keyword) >= 0);
	    }


	public static String removeDuplicatedTokens(String processedBodyStemmedStopped, String separator) {
		processedBodyStemmedStopped = Arrays.stream(processedBodyStemmedStopped.split(separator)).distinct().collect(Collectors.joining(separator));
		return processedBodyStemmedStopped;
	}


	public String removeUnnecessaryWords(String content) {
		if(content==null || content.trim().equals("")){
			return "";
		}
		//boolean startedHtml = false;
		for(String word: unncessaryWords){
			content= content.replaceAll(word," ");
		}
		return content;
	}


	public static int countLines(String str){
	   String[] lines = str.split("\r\n|\r|\n");
	   return  lines.length;
	}
	
	
	
	public List<ExternalQuestion> readAnswerBotQuestionsAndAnswersOld() throws IOException {
		List<ExternalQuestion> answerBotQuestionAnswers = new ArrayList<>();
		
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
			//System.out.println(answer);
			//answerBotQuestionAnswers.add(new ExternalQuestion(i+1,query,answer));
		}
		
		return answerBotQuestionAnswers;
		
	}
	
	
	public List<ExternalQuestion> readExternalQuestionsAndAnswers(Boolean runRack, String obs) throws IOException {
		List<ExternalQuestion> externalQuestionAnswers = new ArrayList<>();
		
		URL url;
		String fileContent="";
		logger.info("...reading external questions");		
		url = Resources.getResource("external_questions.txt");
		fileContent = Resources.toString(url, Charsets.UTF_8);
		
		List<String> lines = IOUtils.readLines(new StringReader(fileContent));
		
		Iterator it = lines.iterator();
		//int externalId=1;
		Integer externalId=0;
		
		while(it.hasNext()){
			String numberLine = (String)it.next();
			
			if(numberLine.startsWith("----------------- No.")) {
				Pattern pattern = Pattern.compile(BotUtils.NUMBERS_REGEX_EXPRESSION);
				Matcher matcher = pattern.matcher(numberLine);
				while (matcher.find()) {
					externalId = new Integer(matcher.group(0));
				}
				
				if(!it.hasNext()) {
					logger.error("Error when reading query from file: ");
				}	
				String queryLine = (String)it.next();
			
				if(queryLine.startsWith("query:")) {
					queryLine= queryLine.replace("query:", "").trim();
					//System.out.println(queryLine);
					
					if(!it.hasNext()) {
						logger.error("Error when reading link from query: "+queryLine);
					}else {
						String linkLine = (String)it.next();
						if(linkLine.startsWith("link:")) {
							linkLine= linkLine.replace("link:", "").trim();
							
							if(phaseNumber==1 || phaseNumber==2 || phaseNumber==3) {  //1/3 of questions, first lot
							
								if(externalId<=11 || (externalId>=34 && externalId<=44) || (externalId>=67 && externalId<=77)) {
									ExternalQuestion externalQuestion = new ExternalQuestion(externalId,queryLine,null,null,runRack,obs,linkLine);
									externalQuestionAnswers.add(externalQuestion);
								}
								
							}else if(phaseNumber==4 || phaseNumber==5) {  //1/3 of questions, second lot
								
								if((externalId>=12 && externalId<=22) || (externalId>=45 && externalId<=55) || (externalId>=78 && externalId<=88)) {
									ExternalQuestion externalQuestion = new ExternalQuestion(externalId,queryLine,null,null,runRack,obs,linkLine);
									externalQuestionAnswers.add(externalQuestion);
								}
							}else if(phaseNumber==7 || phaseNumber==8) {  //1/3 of questions, third lot
								
								if((externalId>=23 && externalId<=33) || (externalId>=56 && externalId<=66) || (externalId>=89 && externalId<=99)) {
									ExternalQuestion externalQuestion = new ExternalQuestion(externalId,queryLine,null,null,runRack,obs,linkLine);
									externalQuestionAnswers.add(externalQuestion);
								}
							}
							
							//phase 10 to define yet... external survey... 
							//externalId++;
							
							
						}
						
					}
					
				}
			}
		}
		
		
		
		
		/*for(int i=0; i<100; i++){
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
			//System.out.println(answer);
			answerBotQuestionAnswers.add(new ExternalQuestion(i+1,query,answer));
		}*/
		
		return externalQuestionAnswers;
		
	}
	
	
	
	public void storeParentPostInCache(Post post) {
		if(!parentPostsCache.containsKey(post.getId())) {
			parentPostsCache.put(post.getId(), post);
		}
		
	}

	public void storeAnswerPostInCache(Post post) {
		if(!answerPostsCache.containsKey(post.getId())) {
			answerPostsCache.put(post.getId(), post);
		}
		
	}

	public Map<Integer, Post> getParentPostsCache() {
		return parentPostsCache;
	}


	public Map<Integer, Post> getAnswerPostsCache() {
		return answerPostsCache;
	}


	public static boolean containCode(String another7Code) {
		List<String> preCodes = getPreCodes(another7Code);
		List<String> simpleCodes = getSimpleCodes(another7Code);
		
		return (!preCodes.isEmpty() || !simpleCodes.isEmpty()); 
		
	}

	

	public static boolean testContainLinkToSo(String text) {
		Set<Integer> soQuestionsIdsInsideTexts = new HashSet<>();
		List<String> links = getCodeValues(BotUtils.LINK_PATTERN, text);
		identifyQuestionsIdsFromUrls(links, soQuestionsIdsInsideTexts);
		return !soQuestionsIdsInsideTexts.isEmpty();
	}


	public static void identifyQuestionsIdsFromUrls(List<String> urls, Set<Integer> soQuestionsIds) {
		for(String url: urls){
			if(!url.contains("stackoverflow.com")) {
				//logger.info("Discarting URL because its is not a SO url: "+url);
				continue;
			}
			//String[] urlPart = url.split("\\/[[:digit:]].*\\/");
			Pattern pattern = Pattern.compile("\\/([\\d]+)");
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				soQuestionsIds.add(new Integer(matcher.group(1)));
				
			}
			
		}
		
	}
	
	
	public static Integer identifyQuestionIdFromUrl(String url) {
		if(!url.contains("stackoverflow.com")) {
			//logger.info("Discarting URL because its is not a SO url: "+url);
			return null;
		}
		//String[] urlPart = url.split("\\/[[:digit:]].*\\/");
		Pattern pattern = Pattern.compile("\\/([\\d]+)");
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			return new Integer(matcher.group(1));
		}
		return null;
		
	}


	public Set<Integer> getStaticIdsForTests() {
		HashSet<Integer> soPostsIds = new LinkedHashSet<>();
		soPostsIds.add(10117026);
		soPostsIds.add(6416706);
		soPostsIds.add(10117051);
		soPostsIds.add(35593309);
		soPostsIds.add(11018325);
		soPostsIds.add(23329173);
		soPostsIds.add(40064173);
		soPostsIds.add(46107706);
		soPostsIds.add(8174964);
		soPostsIds.add(9740830);
		
		for(Integer id:soPostsIds) {
			logger.info("id: "+id);
		}
		
		return soPostsIds;
	}


	public void addMapCacheCount(Map<Integer, Integer> map, Integer id) {
		if(map.get(id)==null) {
			map.put(id, 1);
		}else{
			int actualCount = map.get(id);
			map.put(id, actualCount+1);
		}
	}

	
	
	
	
}
