package com.ufu.bot.googleSearch;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created on 15.07.2015 by afedulov
 */
@Slf4j
public class GoogleWebSearchOld {
  private final Logger log = LoggerFactory.getLogger(this.getClass());	

  private SearchConfig CONFIG = new SearchConfig();

  public GoogleWebSearchOld() {
  }

  public GoogleWebSearchOld(SearchConfig config) {
    this.CONFIG = config;
  }

  public SearchResult search(SearchQuery query) {
    log.debug("Search query: {}", query);
    HttpEntity entity = getResponse(query).getEntity();
    List<SearchHit> hitsUrls = null;
    try {
      hitsUrls = parseResponse(EntityUtils.toString(entity));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new SearchResult(query, hitsUrls);
  }

  public SearchResult search(String query, int numResults) {
    SearchQuery searchQuery = new SearchQuery.Builder(query).numResults(numResults).build();
    return search(searchQuery);
  }


  private String getResponse2(SearchQuery query) throws MalformedURLException, IOException {
		String uri = getUri(query);  
		//Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 9050));
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.ufu.br",3128));
		//HttpURLConnection connection = (HttpURLConnection) new URL(uri).openConnection();
		
		HttpURLConnection connection = (HttpURLConnection) new URL(uri).openConnection(proxy);
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
  
  
  private HttpResponse getResponse(SearchQuery query) {
    String uri = getUri(query);
    log.debug("Complete URL: {}", uri);
    HttpClient client = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(uri);
    HttpResponse result = null;
    try {
      result = client.execute(request);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  private String getUri(SearchQuery query) {
    String uri = CONFIG.getGoogleSearchUrl().replaceAll(CONFIG.PLHD_QUERY, query.getQuery());
    uri = uri.replaceAll(CONFIG.PLHD_RESULTS_NUM,
                         ifPresent(CONFIG.PLHD_RESULTS_NUM, query.getNumResults()));
    uri = uri.replaceAll(CONFIG.PLHD_SITE, ifPresent(CONFIG.PLHD_SITE, query.getSite()));
    return uri;
  }

  private String ifPresent(String plhd, Object param) {
    if (param != null) {
      log.trace("URL parameter: {}", plhd + param);
      return plhd + param;
    } else {
      return "";
    }
  }

  protected List<SearchHit> parseResponse(String document) {
    Document searchDoc = Jsoup.parse(document);
    Elements contentDiv = searchDoc.select("div#search");
    List<SearchHit> hitsUrls = new ArrayList<SearchHit>();
    Elements articlesLinks = contentDiv.select("a[href]"); // a with href
    for (Element link : articlesLinks) {
      String linkHref = link.attr("href");
      Matcher matcher = CONFIG.PURE_URL_PATTERN.matcher(linkHref);
      if (matcher.matches()) {
        String pureUrl = matcher.group(1);
        if (pureUrl.startsWith("https://stackoverflow.com")){ //  !pureUrl.startsWith(CONFIG.CACHE_URL)) {
          hitsUrls.add(new SearchHit(pureUrl));
          log.trace("{}", pureUrl);
        }
      }
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

    private String CACHE_URL         = "http://webcache.googleusercontent.com";

    private String GOOGLE_SEARCH_URL_PREFIX = "https://www.google.com/search?";

    public String getGoogleSearchUrl(){
      return  GOOGLE_SEARCH_URL_PREFIX + "q=" + PLHD_QUERY + PLHD_RESULTS_NUM + PLHD_SITE;
    }
  }
}
