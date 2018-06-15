package com.ufu.bot.googleSearch;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * Created on 15.07.2015 by afedulov
 */
@NotThreadSafe
@Getter
public class SearchResult {
  private final List<SearchHit> hits;
  private final SearchQuery query;

  public SearchResult(SearchQuery query, List<SearchHit> hits) {
    this.query = query;
    this.hits = hits;
  }

  public List<String> getUrls(){
    List<String> urls = new ArrayList<String>(hits.size());
    for (SearchHit hit : hits) {
      urls.add(hit.getUrl());
    }
    return urls;
  }

  public int getSize(){
    return hits.size();
  }

}
