package com.ufu.crokage.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.ufu.bot.repository.GenericRepository;
import com.ufu.bot.to.ExternalQuestion;
import com.ufu.bot.to.Post;
import com.ufu.bot.util.CosineSimilarity;
import com.ufu.crokage.to.UserEvaluation;



@Component
public class CrokageUtils {
	
	/*
	 * Stores the value obtained from the pathFileEnvFlag file
	 */
	@Value("${useProxy}")
	public Boolean useProxy;
	
	@Value("${environment}")
	public String environment;
	
	@Value("${productionEnvironment}")
	public Boolean productionEnvironment;  
	
	@Value("${BIKER_HOME}")
	public String bikerHome;
	
	@Value("${CROKAGE_HOME}")
	public String crokageHome;
	
	@Value("${TMP_DIR}")
	public String tmpDir;
	
	@Value("${virutalPythonEnv}")
	public String virutalPythonEnv;
	
	@Value("${FAST_TEXT_INSTALLATION_DIR}")
	public String FAST_TEXT_INSTALLATION_DIR;
	
	@Value("${FAST_TEXT_MODEL_PATH}")
	public String FAST_TEXT_MODEL_PATH;
	
	@Value("${SO_IDF_VOCABULARY}")
	public String SO_IDF_VOCABULARY;
	
	@Value("${STOP_WORDS_FILE_PATH}")
	public String STOP_WORDS_FILE_PATH;
	
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
	private static DCG_TYPE dcgType = DCG_TYPE.COMB;
	private static List<String> stopWordsList;

	
	
	@Autowired
	private CosineSimilarity cs1;
	
	@Autowired
	protected GenericRepository genericRepository;
	
	@Autowired
	protected TextNormalizer textNormalizer;
	
	
	
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
	
	public static final String METHOD_CALLS_REGEX_EXPRESSION = "\\.\\w+\\(";
	public static final Pattern METHOD_CALLS_PATTERN = Pattern.compile(METHOD_CALLS_REGEX_EXPRESSION, Pattern.DOTALL);
		
	
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
	
	public CrokageUtils() throws Exception {
		//initializeConfigs();
	}
	
	@PostConstruct
	public void initializeConfigs() throws Exception {
		if(!configsInitialized){
			configsInitialized = true;
				
			factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
			standardTokenizer = new StandardTokenizer(factory);
			stopWords = EnglishAnalyzer.getDefaultStopSet();
			
			standardTokenizer.close();
			//loadTagSynonyms();
			//configureEnvironmentVariables();
		}
		/*
		if(minTokenSize==null) {
			Properties prop = new Properties();
			prop.load(CrokageUtils.class.getClassLoader().getResourceAsStream("application.properties")); 
			minTokenSize = new Integer(prop.getProperty("minTokenSize"));
			
		}*/
		parentPostsCache = new HashMap<>();
		parentPostsCache = new HashMap<Integer, Post>();
		answerPostsCache = new HashMap<Integer, Post>();
		
		stopWordsList = Files.readAllLines(Paths.get(STOP_WORDS_FILE_PATH)); 
		
	}
	
	

	/*private void configureEnvironmentVariables() {
		if(!StringUtils.isBlank(bikerHome)) {
			BIKER_HOME=bikerHome;
		}
		if(!StringUtils.isBlank(crokageHome)) {
			CROKAGE_HOME=crokageHome;
		}
		if(!StringUtils.isBlank(tmpDir)) {
			TMP_DIR=tmpDir;
		}
		
		
	}*/


	
	
	 public static double round(double pNumero, int pCantidadDecimales) {
	    BigDecimal value=null;
		value = new BigDecimal(pNumero);
	    value = value.setScale(pCantidadDecimales, RoundingMode.HALF_EVEN); 
	    return value.doubleValue(); 
	}
	
	/*
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
	*/
	/*
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
	*/
	
	
	
	public static void reportElapsedTime(long initTime, String processName) {
		endTime = System.currentTimeMillis();
		String duration = DurationFormatUtils.formatDuration(endTime-initTime, "HH:mm:ss,SSS");
		System.out.println("Done with "+processName+", duration: "+duration);
	}
	
	
	/*
	 * Remove especial simbols only if isolated. Remove .:;'" if surrounded by spaces in any side. ?!, from all 
	 */
	public static String removePunctuations(String content) {
		content = content.replaceAll("\\s+\\p{Punct}+\\s"," "); 
		content = content.replaceAll("\\s+\\."," ");
		content = content.replaceAll("\\.(\\s+|$)"," ");
		content = content.replaceAll("\\s+\\:"," ");
		content = content.replaceAll("\\:(\\s+|$)"," ");
		content = content.replaceAll("\"\\s+"," ");
		content = content.replaceAll("\\s+\""," ");
		content = content.replaceAll("\'\\s+"," ");
		content = content.replaceAll("\\s+\'"," ");
		
		content = content.replaceAll("\\s+(\\;|$)"," ");
		content = content.replaceAll("\\;\\s+"," ");
		content = content.replaceAll("\\,"," ");
		content = content.replaceAll("\\?"," ");
		content = content.replaceAll("\\!"," ");
		return content;
	}
	
	public static String removeAllPunctuations(String body) {
		body = body.replaceAll("\\p{Punct}+"," "); 
		body = body.replaceAll("[^\\x20-\\x7e]", " "); //non-UTF-8 chars
		return body;
	}
	
	public static String translateHTMLSimbols(String finalContent) {
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
	
	public static Set<String> getMethodCalls(String str) {
	    final Set<String> methodCalls = new HashSet<String>();
	    final Matcher matcher = CrokageUtils.METHOD_CALLS_PATTERN.matcher(str);
	    while (matcher.find()) {
	        methodCalls.add(matcher.group(0));
	    }
	    return methodCalls;
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
		//System.out.println("\nCodes: \n"+codeContent);
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
	
	public static boolean isNumeric(String str)
	{
		return str.matches("[+-]?\\d*(\\.\\d+)?");
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
		for(String keyword: CrokageUtils.keywords){
			code= code.replaceAll(keyword,"");
		}
		
		//remove double quotes contents
		code= code.replaceAll(CrokageUtils.DOUBLE_QUOTES_REGEX_EXPRESSION,"");
		
		//remove comments
		code = code.replaceAll( CrokageUtils.COMMENTS_REGEX_EXPRESSION, CrokageUtils.COMMENTS_REPLACEMENT_EXPRESSION );
		
		//Get classes in camel case
		Pattern pattern = Pattern.compile(CrokageUtils.CLASSES_CAMEL_CASE_REGEX_EXPRESSION);
		Matcher matcher = pattern.matcher(code);
		while (matcher.find()) {
			if(matcher.group(0)!=null && matcher.group(0).length()>2) {
				classesNames.add(matcher.group(0));
			}
			
		}
		
	}
	
/*
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
	}*/

	/*
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
		String bloquesAndLinks = blockquoteContent;
		bloquesAndLinks = bloquesAndLinks.replaceAll("\n", " ");
		bloquesAndLinks = retiraHtmlTags(bloquesAndLinks);
		bloquesAndLinks += " "+codeContent;
		
		
		textWithoutCodesLinksAndBlackquotes = textWithoutCodesLinksAndBlackquotes.replaceAll(PRE_CODE_REGEX_EXPRESSION, " ");
		textWithoutCodesLinksAndBlackquotes = removeHtmlTags(textWithoutCodesLinksAndBlackquotes);
		
		//trata antes de pegar somenta as palavras
		
		String separated = removeOtherSymbolsAccordingToPostPart(textWithoutCodesLinksAndBlackquotes,isAnswer);
		
		
		String onlyWords = getOnlyWords(separated);
		List<String> palavras = getWords(ONLY_WORDS_PATTERN, separated);
		for(String word: palavras){
			somentePalavras+= word+ " ";
		}
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
	
	*/
	
	
	

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


	
	
	
	
	public void getPostsLinks() {
		System.out.println("Retrieving PostLinks... ");
		if(allPostLinks==null){
			allPostLinks = genericRepository.getAllPostLinks();
		}
		System.out.println("PostLinks retrieved: " + allPostLinks.size());

	}
	
	public void generateBuckets() {
		if(bucketDuplicatiosMap==null){
			bucketDuplicatiosMap = new HashMap<Integer, Set<Integer>>();	
			getPostsLinks();
			//Pode haver mais de uma duplicada por questao.. BucketOld structure
			System.out.println("Building buckets");
					
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
			
			System.out.println("Buckets gerados...");
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

	
	
	
	


	
	
	/*public void removePostsWithIncompleteTitlesOld(Set<ProcessedPostOld> processedPostsByFilter, Set<ProcessedPostOld> closedDuplicatedNonMastersByTag) {
		System.out.println("Removing invalid posts. Processedposts before: "+processedPostsByFilter.size());
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
		System.out.println("Processed posts after removing invalid posts: "+processedPostsByFilter.size()+"\nInvalid posts: "+invalidPosts.size());
		
		
		System.out.println("...now removing invalid posts from closedDuplicatedNonMastersIdsByTag. Before: "+closedDuplicatedNonMastersByTag.size());
		invalidPosts = new HashSet<>();
		
		for(ProcessedPostOld processedPosts: closedDuplicatedNonMastersByTag) {
			st = new StringTokenizer(processedPosts.getTitle());
			if(st.countTokens() <= 3) {
				invalidPosts.add(processedPosts);
			}
			st = null;
		}
		
		closedDuplicatedNonMastersByTag.removeAll(invalidPosts);
		System.out.println("closedDuplicatedNonMastersIdsByTag after removing invalid posts: "+closedDuplicatedNonMastersByTag.size()+"\nInvalid posts: "+invalidPosts.size());
				
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
	
	
	public static String processQuery(String query) {
		query = query.toLowerCase();
		query = translateHTMLSimbols(query);
		
		//remove punctuations
		query = removeAllPunctuations(query);
		String[] words = query.split("\\s+");
		Set<String> validWords = new LinkedHashSet<>();
		
		//remove stop words or small words or numbers only
		assembleValidWords(validWords,words);
				
		String finalContent = String.join(" ", validWords);
		finalContent = finalContent.replaceAll("\u0000", "");
		validWords = null;
		query = null;
		words = null;
		return finalContent;
	}
	
	
	private static void assembleValidWords(Set<String> validWords, String[] words) {
		for(String word:words) {
			word = word.trim();
			if(!stopWordsList.contains(word) && !(word.length()<2) && !StringUtils.isBlank(word) && !isNumeric(word)) {
				validWords.add(word);
			}
			
		}
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
		List<String> links = getCodeValues(CrokageUtils.LINK_PATTERN, text);
		identifyQuestionsIdsFromUrls(links, soQuestionsIdsInsideTexts);
		return !soQuestionsIdsInsideTexts.isEmpty();
	}


	public static void identifyQuestionsIdsFromUrls(List<String> urls, Set<Integer> soQuestionsIds) {
		for(String url: urls){
			if(!url.contains("stackoverflow.com")) {
				//System.out.println("Discarting URL because its is not a SO url: "+url);
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
			//System.out.println("Discarting URL because its is not a SO url: "+url);
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
			System.out.println("id: "+id);
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
	
	
	public void buildFileForAgreementPhaseHighlightingDifferences(String evaluationsFileBeforeAgreement, String agreementFile) {
		try {
			FileInputStream excelFile = new FileInputStream(new File(evaluationsFileBeforeAgreement));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			iterator.next();
			
			Integer externalQuestionId=0;
			String query=null;
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				Cell currentCellColumnA = currentRow.getCell(0);
				Cell currentCellColumnC = currentRow.getCell(2);
				Cell currentCellColumnD = currentRow.getCell(3);
				
				if(currentCellColumnA!=null && currentCellColumnC==null) {
					
					String currentAValue = currentCellColumnA.getStringCellValue();
					query = currentAValue.trim();
				
				}else {
					if (currentCellColumnC != null && currentCellColumnC.getCellTypeEnum() == CellType.NUMERIC
						&& currentCellColumnD != null && currentCellColumnD.getCellTypeEnum() == CellType.NUMERIC) {
						
						Integer currentCValue = (int) (currentCellColumnC.getNumericCellValue());
						Integer currentDValue = (int) (currentCellColumnD.getNumericCellValue());
						//System.out.println("Scales: " + currentBValue + " - " + currentCValue);
	
						Cell cellE = currentRow.createCell(4);
						Cell cellF = currentRow.createCell(5);
						
						int diff = Math.abs(currentCValue - currentDValue);
						if(diff>1 && ((currentCValue>3) || (currentDValue>3)) ) {
							cellE.setCellValue("XXX");
							cellF.setCellValue("XXX");
						}else {
							cellE.setCellValue(currentCValue);
							cellF.setCellValue(currentDValue);
						}
						
						
					}
				
				}
			}
				
			FileOutputStream outputStream = new FileOutputStream(agreementFile);
	        workbook.write(outputStream);
	        workbook.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	public void readGroundTruthFile(List<UserEvaluation> evaluationsWithBothUsersScales, String fileName, Integer firstColumn, Integer secondColumn) {
		try {
			
			FileInputStream excelFile = new FileInputStream(new File(fileName));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			iterator.next();
			
			Integer externalQuestionId=0;
			String query=null;
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				Cell currentCellColumnA = currentRow.getCell(0);
				Cell currentCellColumnB = currentRow.getCell(1);
				Cell firstEvaluationCell = currentRow.getCell(firstColumn);
				Cell secondEvaluationCell = currentRow.getCell(secondColumn);
				
				
				if(currentCellColumnA!=null && firstEvaluationCell==null) {
					
					String currentAValue = currentCellColumnA.getStringCellValue();
					query = currentAValue.trim();
				
				}else {
					if (firstEvaluationCell != null && firstEvaluationCell.getCellTypeEnum() == CellType.NUMERIC
						&& secondEvaluationCell != null && secondEvaluationCell.getCellTypeEnum() == CellType.NUMERIC) {
						
						String currentBValue = (String) (currentCellColumnB.getStringCellValue());
						Integer currentFirstEvalValue = (int) (firstEvaluationCell.getNumericCellValue());
						Integer currentSecondEvalValue = (int) (secondEvaluationCell.getNumericCellValue());
						
						//System.out.println("Scales: " + currentBValue + " - " + currentCValue);
						Integer postId = identifyQuestionIdFromUrl(currentBValue);
						
						UserEvaluation eval = new UserEvaluation();
						eval.setExternalQuestionId(++externalQuestionId);
						eval.setLikertScaleUser1(currentFirstEvalValue);
						eval.setLikertScaleUser2(currentSecondEvalValue);
						eval.setQuery(query);
						eval.setPostId(postId);
						
						evaluationsWithBothUsersScales.add(eval);
					}
				
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public void readXlsxToEvaluationList(List<UserEvaluation> evaluationsWithBothUsersScales, String fileName, Integer firstColumn, Integer secondColumn, List<String> queries) {
		try {
	
			FileInputStream excelFile = new FileInputStream(new File(fileName));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			Integer externalQuestionId=null;
			String query=null;
			while (iterator.hasNext()) {
	
				Row currentRow = iterator.next();
				Cell currentCellColumnA = currentRow.getCell(0);
				Cell currentCellColumnB = currentRow.getCell(firstColumn);
				Cell currentCellColumnC = currentRow.getCell(secondColumn);
				
				if(currentCellColumnA!=null) {
					
					String currentAValue = currentCellColumnA.getStringCellValue();
					Integer postId = identifyQuestionIdFromUrl(currentAValue);
					if(currentAValue.contains("id:")) {
						String parts[] = currentAValue.split("\\|");
						String externalQuestionIdStr = parts[0].replaceAll("\\D+","");
						externalQuestionId = new Integer(externalQuestionIdStr);
						String queryParts[] = currentAValue.split(" - ");
						query = queryParts[1].trim();
						if(queries!=null) {
							queries.add(query);
						}
						
					}
					
					if (currentCellColumnB != null && currentCellColumnB.getCellTypeEnum() == CellType.NUMERIC
						&& currentCellColumnC != null && currentCellColumnC.getCellTypeEnum() == CellType.NUMERIC) {
						
						Integer currentBValue = (int) (currentCellColumnB.getNumericCellValue());
						Integer currentCValue = (int) (currentCellColumnC.getNumericCellValue());
						//System.out.println("Scales: " + currentBValue + " - " + currentCValue);
	
						UserEvaluation eval = new UserEvaluation();
						eval.setExternalQuestionId(externalQuestionId);
						eval.setLikertScaleUser1(currentBValue);
						eval.setLikertScaleUser2(currentCValue);
						eval.setQuery(query);
						eval.setPostId(postId);
						evaluationsWithBothUsersScales.add(eval);
					}
				
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	public void buildMatrixForKappa(List<UserEvaluation> evaluationsWithBothUsersScales, String fileNameGeneratedMatrix) throws IOException {
		BufferedWriter bw =null;
		try {
		
			bw = new BufferedWriter(new FileWriter(fileNameGeneratedMatrix));
			bw.write(";;1;2;3;4;5");
			
			//Matrix map
			int[] cells[] = new int[5][5];
			
			for(int i=0; i<5; i++) {
				bw.write("\n;"+(i+1)+";");
				for(int j=0; j<5; j++) {
					//System.out.println(cells[i][j]);
					cells[i][j] = getCellNumber(i+1,j+1,evaluationsWithBothUsersScales);
					//System.out.println("cell "+i+"-"+j+"= "+cells[i][j]);
					bw.write(cells[i][j]+";");
				}
			}
			
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
		
	}


	public int getCellNumber(int i, int j, List<UserEvaluation> allEvaluations) {
		int sum=0;
		for(UserEvaluation evaluation: allEvaluations) {
			if(evaluation.getLikertScaleUser1()==i && evaluation.getLikertScaleUser2()==j) {
				sum+=1;				
			}
			
			
		}
		
		return sum;
	}


	public static double computeDCG(final double rel, final int rank) {
		double dcg = 0.0;
		switch (dcgType) {

		case COMB:
			dcg = rel / (Math.log(rank + 1) / Math.log(2));
			break;

		case LIN:
			dcg = rel;
			if (rank > 1) {
				dcg = rel / (Math.log(rank) / Math.log(2));
			}
			break;

		case EXP:
			dcg = (Math.pow(2.0, rel) - 1.0) / ((Math.log(rank + 1) / Math.log(2)));
			break;

		}
	
		
		return round(dcg, 4);
	}
	
	public static double calculateIDCG(final int maxRelevance,int maxRank) {
		double idcg = 0;
		// if can get relevance for every item should replace the relevance score at this point, else
		// every item in the ideal case has relevance of 1
		//int itemRelevance = 1;
		
		for (int posRank = 1; posRank <= maxRank; posRank++){
			idcg += computeDCG(maxRelevance, posRank);
		}
		idcg = round(idcg, 4);
		
		return idcg;
	}
	
	
	public static enum DCG_TYPE {

        /**
         * Linear.
         */
        LIN,
        /**
         * Exponential.
         */
        EXP,
        /*
         * Implemented
         */
        COMB;
		
		
	}
	
	

	public Set<String> extractClassesFromCode(String content) {
		Document doc = Jsoup.parse(content);
		Elements elems = doc.select("code,pre");
		String codeText = elems.text();
		textNormalizer.setContent(codeText);
		return textNormalizer.normalizeSimpleCodeDiscardSmall();
		
	}

	public Set<String> extractClassesFromProcessedCode(String content) {
		textNormalizer.setContent(content);
		return textNormalizer.normalizeSimpleCodeDiscardSmall();
	}

	public void reduceSetV2(Map<String, Set<Integer>> goldSetQueriesApis, int k) {
		Set<String> keys = goldSetQueriesApis.keySet();
		for(String key: keys) {
			Set<Integer> goldSetApis = goldSetQueriesApis.get(key);
			setLimitV2(goldSetApis, k);
			
		}
	}

	public void reduceSet(Map<Integer, Set<String>> goldSetQueriesApis, int k) {
		Set<Integer> keys = goldSetQueriesApis.keySet();
		for(Integer key: keys) {
			Set<String> goldSetApis = goldSetQueriesApis.get(key);
			setLimit(goldSetApis, k);
		}
	}


	public static String loadStream(InputStream s) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(s));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null)
			sb.append(line).append("\n");
		return sb.toString();
	}


	public static void setLimit(Set<String> set, int k) {
		List<String> items = new ArrayList<String>();
	    items.addAll(set);
	    set.clear();
	    int trim = k>items.size() ? items.size() : k;
	    set.addAll(items.subList(0,trim));
		items = null;
	}
	
	public static <K> void setLimitV2(Set<K> set, int k) {
		Set<K> tmp = new HashSet<>(set);
		
		if(k<set.size()) {
			List<K> items = new ArrayList<K>();
		    items.addAll(set);
		    int trim = k>items.size() ? items.size() : k;
		    items = items.subList(0,trim);
			
		    for(K id: tmp) {
		    	if(!items.contains(id)) {
		    		set.remove(id);
		    	}
		    }
			
		}
				
	}


	public static String cleanCode(String body) {
		
		//volta simbolos de marcacao HTML para estado original 
		body = translateHTMLSimbols(body);		
		
		Document doc = Jsoup.parse(body);
		Elements elems = doc.select("code,pre");
		String codeText = elems.text();
		
		body = removeHtmlTags(codeText);
		
		return body;
	}
	

	public static Timestamp getCurrentDate(){
		Timestamp ts_now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return ts_now;
	}


	public static void printBigMapIntoFile(Map<String, Set<Integer>> bigMapApisIds,	String filePath) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		Set<Entry<String, Set<Integer>>> entries = bigMapApisIds.entrySet();
		for(Entry<String, Set<Integer>> entry: entries) {
			lines.append(entry.getKey()+ ":\t");
			Set<Integer> answerIds = entry.getValue();
			for(Integer id: answerIds) {
				lines.append(id+ " ");
			}
			lines.append("\n");
		}
		lines.append("end@");
		String linesStr = lines.toString().replace("\nend@", "");
		try (PrintWriter out = new PrintWriter(filePath)) {
		    out.println(linesStr);
		}
	}
	
	public static void writeMapToFile4(Map<String, Set<Integer>> queriesAndSOIdsMap, String filePath) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		Set<String> keys = queriesAndSOIdsMap.keySet();
		for(String key: keys) {
			Set<Integer> soQuestionsIds = queriesAndSOIdsMap.get(key);
			lines.append("\n"+key+ " >> ");
			for(Integer id: soQuestionsIds) {
				lines.append(id+ " ");
			}
		}
		String linesStr = lines.toString();
		try (PrintWriter out = new PrintWriter(filePath)) {
		    out.println(linesStr);
		}
	}
	
	


	public static void writeStringContentToFile(String content, String file) throws IOException {
		FileWriter fw = new FileWriter(file);
		fw.write(content); 
		fw.close();
		
	}


	public static void printMapInfosIntoCVSFile(Map<String, Set<Integer>> mapApis, String file) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		
		Set<Entry<String, Set<Integer>>> entries = mapApis.entrySet();
		for(Entry<String, Set<Integer>> entry: entries) {
			lines.append(entry.getKey()+ ";");
			Set<Integer> answerIds = entry.getValue();
			lines.append(answerIds.size());
			lines.append("\n");
		}
		lines.append("end@");
		String linesStr = lines.toString().replace("\nend@", "");
		try (PrintWriter out = new PrintWriter(file)) {
		    out.println(linesStr);
		}
		
	}


	public Map<String, double[]> readVectorsForQuery(String word) throws Exception {
		String modelPath = FAST_TEXT_MODEL_PATH;
		String fastTextIntallationDir = FAST_TEXT_INSTALLATION_DIR;
		
		//build a temporary file with words --size issues
		File tmpFile = new File("/home/rodrigo/tmp/tempWordsFile.txt");
		try (PrintWriter out = new PrintWriter(tmpFile)) {
		    out.println(word);
		}
		
		// echo "java filewriter file" | ./fasttext print-word-vectors
		//String[] cmd = { "bash", "-c", "echo \""+words+"\" | " + fastTextIntallationDir+ "/fasttext print-word-vectors "+modelPath };
		String[] cmd = { "bash", "-c", "echo \"$(cat "+tmpFile.getAbsolutePath()+")\" | " + fastTextIntallationDir+ "/fasttext print-word-vectors "+modelPath };
		
		ProcessBuilder pb = new ProcessBuilder(cmd);
		Process p = pb.start();
		p.waitFor();
		String output = CrokageUtils.loadStream(p.getInputStream());
		String error = CrokageUtils.loadStream(p.getErrorStream());
		int rc = p.waitFor();
		//System.out.println(output);
		String lines[] = output.split("\n");
		Map<String,double[]> vectorsMap = new LinkedHashMap<>();
		getVectorsFromLines(Arrays.asList(lines),vectorsMap);
		
		return vectorsMap;
	}


	public static void getVectorsFromLines(List<String> wordsAndVectors, Map<String,double[]> vectorsMap) {
		String[] parts;
		int vecSize=0;
		if(vectorsMap==null) {
			vectorsMap = new HashMap<>();
		}
		for(String line: wordsAndVectors) {
			parts = line.split(" ");
			vecSize = parts.length;
			int vecCol = 0;
			double[] vectors = new double[vecSize-1];
			for(int i=1;i<vecSize;i++) {
				//vectors[vecCol] = CrokageUtils.round(Double.parseDouble(parts[i]),6);
				vectors[vecCol] = Double.parseDouble(parts[i]);
				vecCol++;
			}
			vectorsMap.put(parts[0], vectors);
		}
		
		
	}


	public void writeMapToFile(Map<String, Double> idfs, String file) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		
		Set<String> keys = idfs.keySet();
		for(String key: keys) {
			lines.append(key);
			lines.append(" ");
			lines.append(idfs.get(key));
			lines.append("\n");
		}
		lines.append("end@");
		String linesStr = lines.toString().replace("\nend@", "");
		try (PrintWriter out = new PrintWriter(file)) {
		    out.println(linesStr);
		}
		
	}
	
	public void writeMapToFile2(Map<Integer, String> idsTitles, String file) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		String[] parts;
		String line;
		Set<Integer> keys = idsTitles.keySet();
		for(Integer key: keys) {
			lines.append(key);
			lines.append(" ");
			line = idsTitles.get(key);
			parts = line.split(" ");
			for(String word: parts) {
				lines.append(word);
				lines.append(" ");
			}
			lines.append("\n");
		}
		lines.append("end@");
		String linesStr = lines.toString().replace("\nend@", "");
		try (PrintWriter out = new PrintWriter(file)) {
		    out.println(linesStr);
		}
		
	}


	public void readIDFVocabulary(Map<String, Double> soIDFVocabularyMap) throws Exception {
		if(soIDFVocabularyMap!=null) {
			long initTime = System.currentTimeMillis();
			System.out.println("Reading all idfs from file...");
			String[] parts;
			List<String> wordsAndIDFs = Files.readAllLines(Paths.get(SO_IDF_VOCABULARY));
			for(String line: wordsAndIDFs) {
				parts = line.split(" ");
				soIDFVocabularyMap.put(parts[0], Double.parseDouble(parts[1]));
			}
			wordsAndIDFs = null;
			reportElapsedTime(initTime,"readIDFVocabulary");
		}else {
			throw new Exception("IDF vacabulary is null.");
		}
		
	}


	public static double[][] getIDFMatrixForQuery(String query, Map<String, Double> IDFVocabularyMap) {
		String queryTokens[] = query.trim().split("\\s+");
		double[][] matrix = new double[1][queryTokens.length];
		
		for(int i=0; i<queryTokens.length; i++) {
			String word = queryTokens[i];
			double idfValue = IDFVocabularyMap.get(word);
			matrix[0][i] = idfValue;
		}
				
		return matrix;
	}


	public static double[][] getMatrixVectorsForQuery(String query, Map<String, double[]> wordVectorsMap) {
		String queryTokens[] = query.trim().split("\\s+");
		double[][] matrix = new double[queryTokens.length][100];
		
		for(int i=0; i<queryTokens.length; i++) {
			String word = queryTokens[i];
			//double[] vectors = wordVectorsMap.get(word).stream().mapToDouble(Double::doubleValue).toArray();
			double[] vectors = wordVectorsMap.get(word);
			if(vectors==null) {
				System.out.println("error here: "+word);
			}
			matrix[i] = vectors;
		}
				
		return matrix;
	}
	
	

	public void readVectorsFromSOMapForWords(Map<String, double[]> soContentWordVectorsMap, Set<String> listOfWords, List<String> wordsAndVectorsLines) throws IOException {
		Set<String> allWordsSet = getWordsForList(listOfWords);
		//System.out.println("Reading vectors by demand for "+allWordsSet.size()+ " words. Size of soContentWordVectorsMap before: "+soContentWordVectorsMap.size());
		soContentWordVectorsMap.putAll(readVectorsFromSOMapForWords(allWordsSet,wordsAndVectorsLines));
		//System.out.println("Size after: "+soContentWordVectorsMap.size());
		
	}


	public Map<String, double[]> readVectorsFromSOMapForWords(Set<String> allQueriesWords, List<String> wordsAndVectors) throws IOException {
		long initTime = System.currentTimeMillis();
		//System.out.println("Reading all vectors from file...");
		String[] parts;
		int vecSize=0;
		Map<String, double[]> soContentWordVectorsMap = new HashMap<>();
		
		for(String line: wordsAndVectors) {
			parts = line.split(" ");
			if(!allQueriesWords.contains(parts[0])) {
				continue;
			}
			
			vecSize = parts.length;
			int vecCol = 0;
			//List<Double> vectors = new ArrayList<>();
			double[] vectors = new double[vecSize-1];
			for(int i=1;i<vecSize;i++) {
				vectors[vecCol] = CrokageUtils.round(Double.parseDouble(parts[i]),6);
				vecCol++;
			}
			soContentWordVectorsMap.put(parts[0], vectors);
		}
		//System.out.println(bigMapApisIds);
		if(!productionEnvironment) {
			reportElapsedTime(initTime,"readVectorsFromSOMapForWords");
		}
		return soContentWordVectorsMap;
	}


	public void readWordsFromFileToMap(Map<Integer, String> idsAndContentsMap, List<String> idsAndWords) throws IOException {
		long initTime = System.currentTimeMillis();
		String[] parts;
		String content;
		for(String line: idsAndWords) {
			parts = line.split(" ");
			content = line.replace(parts[0], "").trim();
			idsAndContentsMap.put(Integer.parseInt(parts[0]), content);
		}
		//System.out.println(bigMapApisIds);
		reportElapsedTime(initTime,"readWordsFromFileToMap");
		
	}
	
	public void readWordsFromFileToMap2(Map<Integer, Integer> soIdsIds, List<String> idsAndWords) throws IOException {
		//long initTime = System.currentTimeMillis();
		//System.out.println("Reading all ids and titles from file...");
		String[] parts;
		String title;
		for(String line: idsAndWords) {
			parts = line.split(" ");
			soIdsIds.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
		}
		//System.out.println(bigMapApisIds);
		//reportElapsedTime(initTime,"readWordsFromFileToMap2");
		
	}


	public void writeMapToFile3(Map<Integer, Integer> allAnswersIdsParentIdsMap, String file) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		Set<Integer> keys = allAnswersIdsParentIdsMap.keySet();
		for(Integer key: keys) {
			lines.append(key);
			lines.append(" ");
			lines.append(allAnswersIdsParentIdsMap.get(key));
			lines.append("\n");
		}
		lines.append("end@");
		String linesStr = lines.toString().replace("\nend@", "");
		try (PrintWriter out = new PrintWriter(file)) {
		    out.println(linesStr);
		}
		
	}
	
	public Set<String> getWordsForList(Set<String> listOfWords) {
		Set<String> allWordsSet = new HashSet<>();
		String words[];
		for(String query: listOfWords) {
			words = query.split("\\s+");
			allWordsSet.addAll(Arrays.asList(words));
		}
		allWordsSet.remove("");
		return allWordsSet;
	}


	public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
	    return map.entrySet()
	              .stream()
	              .filter(entry -> Objects.equals(entry.getValue(), value))
	              .map(Map.Entry::getKey)
	              .collect(Collectors.toSet());
	}


	public static <K, V extends Comparable<? super V>> List<Entry<K, V>> sortMapByValueDescending(Map<K, V> map) {

		List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());

		Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> e1, Entry<K, V> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedEntries;
	}
	
	public static double log2(int n)
	{
	    return (Math.log(n) / Math.log(2));
	}
	
	public void buildCsvQuestionsForEvaluation(String fileName,Map<String, List<Post>> allQueriesAndUpVotedCodedAnswersMap) throws IOException {
		BufferedWriter bw =null;
		try {
			bw = new BufferedWriter(new FileWriter(fileName));
			bw.write("Parent Title;Link;Rodrigo;Klerisson;Rodrigo agreement;Klerisson agreement\n\n");
			Set<String> queries = allQueriesAndUpVotedCodedAnswersMap.keySet(); 
						
			for(String query:queries){
				
				bw.write("\n"+query+"\n\n");
				List<Post> answers = allQueriesAndUpVotedCodedAnswersMap.get(query);					
				for(Post answer: answers) {
					String title = answer.getParent().getTitle().replaceAll(";", "");
					title = title.replaceAll("\"", "");
					title = title.replaceAll("\'", "");
					bw.write(title+";");
					bw.write("https://stackoverflow.com/questions/"+answer.getId()+"/\n");
				}
				
				bw.write("\n\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
		
	}

	public static Map<String,Set<Integer>> readCrawledQuestionsIds(String fileToRead) throws IOException {
		long initTime = System.currentTimeMillis();
		Map<String,Set<Integer>> queriesAndSOIdsMap = new LinkedHashMap<>();
		List<String> queriesAndGoogleSOIds = Files.readAllLines(Paths.get(fileToRead));
		String[] parts;
		for(String line: queriesAndGoogleSOIds) {
			parts = line.split(" >> ");
			if(parts.length>1) {
				String ids[]=null;
				ids = parts[1].split(" ");
				Set<Integer> idsInt = new LinkedHashSet<>();
				for(String idStr: ids) {
					idsInt.add(Integer.parseInt(idStr));
				}
				queriesAndSOIdsMap.put(parts[0], idsInt);
			}
			
		}
		
		reportElapsedTime(initTime,"readGoogleRelatedQuestionsIdsForNLP2Api");
		System.out.println("Size of googleQueriesAndSOIds: "+queriesAndSOIdsMap.size() +" for file: "+fileToRead);
		return queriesAndSOIdsMap;
	}
	
	
	public static Map<Integer,Set<String>> readPostsIdsApisMap(String fileToRead) throws IOException {
		long initTime = System.currentTimeMillis();
		Map<Integer,Set<String>> postsIdsApisMap = new HashMap<>();
		List<String> lines = Files.readAllLines(Paths.get(fileToRead));
		String[] parts;
		for(String line: lines) {
			parts = line.split(" >> ");
			if(parts.length>1) {
				String apis[]=null;
				apis = parts[1].split(" ");
				Set<String> apisStr = new LinkedHashSet<>();
				for(String idStr: apis) {
					apisStr.add(idStr);
				}
				postsIdsApisMap.put(Integer.parseInt(parts[0]), apisStr);
			}
			
		}
		
		reportElapsedTime(initTime,"readPostsIdsApisMap");
		System.out.println("Size of postsIdsApisMap: "+postsIdsApisMap.size() +" for file: "+fileToRead);
		return postsIdsApisMap;
	}


	public static Map<String,Set<Integer>> readFileToMap(String fileToRead) throws IOException {
		long initTime = System.currentTimeMillis();
		Map<String,Set<Integer>> contentMap = new LinkedHashMap<>();
		List<String> lines = Files.readAllLines(Paths.get(fileToRead));
		String[] parts;
		for(String line: lines) {
			parts = line.split(":");
			if(parts.length>1) {
				String ids[]=null;
				ids = parts[1].split(" ");
				Set<Integer> intContent = new LinkedHashSet<>();
				for(String idStr: ids) {
					intContent.add(new Integer(idStr.trim()));
				}
				contentMap.put(parts[0], intContent);
			}
			
		}
		
		reportElapsedTime(initTime,"readFileToMap");
		System.out.println("Size of readFileToMap: "+contentMap.size() +" for file: "+fileToRead);
		return contentMap;
	}

	
	
}
