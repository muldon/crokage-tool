package com.ufu.bot.googleSearch;

import lombok.Getter;

/**
 * Created on 15.07.2015 by afedulov
 */
@Getter
public class SearchHit {
  private final String url;

  public SearchHit(String url){
    this.url = url;
  }

public String getUrl() {
	return url;
}
  
  

}
