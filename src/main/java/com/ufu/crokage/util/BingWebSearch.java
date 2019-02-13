package com.ufu.crokage.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ufu.crokage.to.SearchResults;

public class BingWebSearch {
	// Enter a valid subscription key.
	static String subscriptionKey = "572a52ef67424c93b97a995801378220";

	/*
	 * If you encounter unexpected authorization errors, double-check these values
	 * against the endpoint for your Bing Web search instance in your Azure
	 * dashboard.
	 */
	private static String host = "https://api.cognitive.microsoft.com";
	private static String path = "/bing/v7.0/search";
	public static String rawQuery = "insert element array position";
	private static String completeQuery = "java "+rawQuery+" site:stackoverflow.com";
	private static Integer topk = 20;

	
	public static SearchResults SearchWeb (String searchQuery) throws Exception {
	    // Construct the URL.
	    URL url = new URL(host + path + "?q=" +  URLEncoder.encode(searchQuery, "UTF-8") + "&count="+topk);

	    // Open the connection.
	    HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
	    connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

	    // Receive the JSON response body.
	    InputStream stream = connection.getInputStream();
	    String response = new Scanner(stream).useDelimiter("\\A").next();

	    // Construct the result object.
	    SearchResults results = new SearchResults(new HashMap<String, String>(), response);

	    // Extract Bing-related HTTP headers.
	    Map<String, List<String>> headers = connection.getHeaderFields();
	    for (String header : headers.keySet()) {
	        if (header == null) continue;      // may have null key
	        if (header.startsWith("BingAPIs-") || header.startsWith("X-MSEdge-")){
	            results.relevantHeaders.put(header, headers.get(header).get(0));
	        }
	    }
	    stream.close();
	    return results;
	}
	
	public static String prettify(String json_text) {
	    JsonParser parser = new JsonParser();
	    JsonObject json = parser.parse(json_text).getAsJsonObject();
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    return gson.toJson(json);
	}
	
	public static void main (String[] args) {
	    // Confirm the subscriptionKey is valid.
	    if (subscriptionKey.length() != 32) {
	        System.out.println("Invalid Bing Search API subscription key!");
	        System.out.println("Please paste yours into the source code.");
	        System.exit(1);
	    }

	    // Call the SearchWeb method and print the response.
	    try {
	        //System.out.println("Searching the Web for: " + rawQuery);
	        SearchResults result = SearchWeb(completeQuery);
	        //System.out.println("\nRelevant HTTP Headers:\n");
	        //for (String header : result.relevantHeaders.keySet())
	        //System.out.println(header + ": " + result.relevantHeaders.get(header));
	        //System.out.println("\nJSON Response:\n");
	        System.out.println(prettify(result.jsonResponse));
	    }
	    catch (Exception e) {
	        e.printStackTrace(System.out);
	        System.exit(1);
	    }
	}
	
	
		
	
}
/*

class SearchResults{
    HashMap<String, String> relevantHeaders;
    String jsonResponse;
    SearchResults(HashMap<String, String> headers, String json) {
        relevantHeaders = headers;
        jsonResponse = json;
    }
}*/
