package com.ufu.bot.googleSearch;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created on 15.07.2015 by afedulov
 */
@Slf4j
@Component
public class GoogleWebSearch {
  private final Logger log = LoggerFactory.getLogger(this.getClass());	
  
  @Value("${APIKEY}")
  public String apiKey;
	
  @Value("${CUSTOM_SEARCH_ID}")
  public String customSearchId; 
  
  
  //private final String PLHD_QUERY       = "__query__";
  //private       String PLHD_RESULTS_NUM = "_num_";
  
  //private       String  PURE_URL_REGEX = "/url\\?q=(.*)&sa.*";
  //private final Pattern PURE_URL_PATTERN = Pattern.compile(PURE_URL_REGEX);

  //private String CACHE_URL         = "http://webcache.googleusercontent.com";

  //private String GOOGLE_SEARCH_URL_PREFIX 	= "https://www.google.com/search?";
 
  private String customSearchURL; 
  
  public String getGoogleSearchUrl(){
    //return  GOOGLE_SEARCH_URL_PREFIX + "q=" + PLHD_QUERY + PLHD_RESULTS_NUM + PLHD_SITE;
	//  customSearchURL = customSearchURL.replaceAll(regex, replacement)
	  
	return customSearchURL;  
  }
	  

  public GoogleWebSearch() {
	 	  
  }
  
  

  public SearchResult search(SearchQuery query, Boolean useProxy) throws ParseException   {
    log.debug("Search query: {}", query);
    //HttpEntity entity = getResponse(query).getEntity();
    //BufferedWriter bw = null;
       
    List<SearchHit> hitsUrls = null;
    try {
    	//bw = new BufferedWriter(new FileWriter("novoHtml.txt"));
    	String content = getResponse2(query,useProxy);
    	//bw.write(content);
    	//hitsUrls = parseResponse2(DupeUtils.readFile("novoHtml.txt"));
    	//hitsUrls = parseResponse2(content);
    	hitsUrls = parseJsonResponse(content);
    	//String html = EntityUtils.toString(entity);
    	//hitsUrls = parseResponse(html);
    	
      
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
    return new SearchResult(query, hitsUrls);
  }

  private String getResponse2(SearchQuery query, Boolean useProxy) throws MalformedURLException, IOException {
	//String uri = getUri(query);  
	String uri = getUri2(query);
	
	HttpURLConnection connection;
	if(useProxy) {
		//Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 9050));
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.ufu.br",3128));
		connection = (HttpURLConnection) new URL(uri).openConnection(proxy);
	}else {
		connection = (HttpURLConnection) new URL(uri).openConnection();
	}
	
	connection.setRequestProperty("User-Agent", UserAgentUtils.getUserAgent());
	//connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
	connection.setRequestProperty("Host", "www.google.com.br");
	connection.setRequestProperty("Connection", "keep-alive");
	connection.setRequestProperty("Pragma", "no-cache");
	connection.setRequestProperty("Cache-Control", "no-cache");
	connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,/;q=0.8");
	connection.setConnectTimeout(20000);
	String html = IOUtils.toString(connection.getInputStream(), "UTF-8");
	connection = null;
	//System.out.println(html);
	return html;
}



  private String getUri2(SearchQuery query) {
	log.info("GoogleWebSearch - getUri2 - reading custom google search parameters: "
				 + "\n apiKey = "+apiKey
				 + "\n customSearchId = "+customSearchId
				  );
	
	customSearchURL = "https://www.googleapis.com/customsearch/v1?key=" + apiKey + "&cx=" + customSearchId + "&q="+ query.getQuery() + "&alt=json" + "&num="+query.getNumResults();
	customSearchURL = customSearchURL.replaceAll("\\s+","");
	log.info("Complete URL: "+customSearchURL);
	
	return customSearchURL;
}

/*  
private HttpResponse getResponse(SearchQuery query) {
    String uri = getUri(query);
    log.debug("Complete URL: {}", uri);
    HttpClient client = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(uri);
    
    //HttpHost target = new HttpHost("stackoverflow.com", 443, "https");
    HttpHost proxy = new HttpHost("proxy.ufu.br",3128, "http");

    RequestConfig config = RequestConfig.custom()
            .setProxy(proxy)
            .build();
    
    request.setConfig(config);
    
    HttpResponse result = null;
    try {
      result = client.execute(request);
      int statusCode = result.getStatusLine().getStatusCode();
      System.out.println(statusCode);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }
  */
  

 /* private String getUri(SearchQuery query) {
    String uri = CONFIG.getGoogleSearchUrl().replaceAll(CONFIG.PLHD_QUERY, query.getQuery());
    uri = uri.replaceAll(CONFIG.PLHD_RESULTS_NUM,
                         ifPresent(CONFIG.PLHD_RESULTS_NUM, query.getNumResults()));
    uri = uri.replaceAll(CONFIG.PLHD_SITE, ifPresent(CONFIG.PLHD_SITE, query.getSite()));
    return uri;
  }*/

 /* private String ifPresent(String plhd, Object param) {
    if (param != null) {
      log.trace("URL parameter: {}", plhd + param);
      return plhd + param;
    } else {
      return "";
    }
  }*/

  /*protected List<SearchHit> parseResponse(String document) {
    Document searchDoc = Jsoup.parse(document);
    Elements contentDiv = searchDoc.select("div#search");
    List<SearchHit> hitsUrls = new ArrayList<SearchHit>();
    Elements articlesLinks = contentDiv.select("a[href]"); // a with href
    for (Element link : articlesLinks) {
      String linkHref = link.attr("href");
      Matcher matcher = PURE_URL_PATTERN.matcher(linkHref);
      if (matcher.matches()) {
        String pureUrl = matcher.group(1);
        if (pureUrl.startsWith("https://stackoverflow.com")){ //  !pureUrl.startsWith(CONFIG.CACHE_URL)) {
          hitsUrls.add(new SearchHit(pureUrl));
          log.trace("{}", pureUrl);
        }
      }
    }
    return hitsUrls;
  }*/
  
  /*protected List<SearchHit> parseResponse2(String document) {
	    Document searchDoc = Jsoup.parse(document);
	    Elements contentDiv = searchDoc.select("div#search");
	    List<SearchHit> hitsUrls = new ArrayList<SearchHit>();
	    Elements articlesLinks = contentDiv.select("a[href]"); // a with href
	    for (Element link : articlesLinks) {
	      String linkHref = link.attr("href");
          if (linkHref.startsWith("https://stackoverflow.com")){ //  !pureUrl.startsWith(CONFIG.CACHE_URL)) {
        	  hitsUrls.add(new SearchHit(linkHref));
        	  log.trace("{}", linkHref);
	      }
	    }
	    return hitsUrls;
	  }*/
  
  protected List<SearchHit> parseJsonResponse(String json) throws ParseException {
	  List<SearchHit> hitsUrls = new ArrayList<SearchHit>();
	  JSONParser parser = new JSONParser();
	  JSONObject jsonObject = (JSONObject)parser.parse(json);
		// loop array
      JSONArray items = (JSONArray) jsonObject.get("items");
      Iterator<JSONObject> iterator = items.iterator();
      while (iterator.hasNext()) {
      	JSONObject item = iterator.next();
      	String link = (String) item.get("link");
        hitsUrls.add(new SearchHit(link));
      }
      
      return hitsUrls;
  }

  @Data
  public static class SearchConfig {
    /* Placeholders */
    private final String PLHD_QUERY       = "__query__";
    private       String PLHD_RESULTS_NUM = "&num=";
    private       String PLHD_SITE        = "&as_sitesearch=";

    private       String  PURE_URL_REGEX = "/url\\?q=(.*)&sa.*";
    private final Pattern PURE_URL_PATTERN = Pattern.compile(PURE_URL_REGEX);

    //private String CACHE_URL         = "http://webcache.googleusercontent.com";

    private String GOOGLE_SEARCH_URL_PREFIX 	       = "https://www.google.com/search?";
   

    public String getGoogleSearchUrl(){
      return  GOOGLE_SEARCH_URL_PREFIX + "q=" + PLHD_QUERY + PLHD_RESULTS_NUM + PLHD_SITE;
    }
   
  }
}
