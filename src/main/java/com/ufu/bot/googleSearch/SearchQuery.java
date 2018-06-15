package com.ufu.bot.googleSearch;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.Getter;
import lombok.ToString;

/**
 * Created on 15.07.2015 by afedulov
 * modified on 07/06/2018 by muldon (github)
 */
@ToString
@Getter
public class SearchQuery {

	private String query;
	private String site;
	private Integer numResults;

	public SearchQuery(String query, String site,Integer numResults) {
		try {
			this.query = URLEncoder.encode(query, "UTF-8");
			this.site = site;
			this.numResults = numResults;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	

	public String getQuery() {
		return query;
	}

	public String getSite() {
		return site;
	}

	public Integer getNumResults() {
		return numResults;
	}

}
