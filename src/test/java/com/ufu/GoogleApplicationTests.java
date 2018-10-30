package com.ufu;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ufu.bot.googleSearch.GoogleWebSearch;
import com.ufu.bot.googleSearch.SearchQuery;
import com.ufu.bot.googleSearch.SearchResult;
import com.ufu.bot.service.PitBotService;
import com.ufu.bot.util.AbstractService;
import com.ufu.bot.util.BotUtils;
import com.ufu.crokage.util.CrokageUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class GoogleApplicationTests extends AbstractService{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	protected PitBotService pitBotService;
	
	@Autowired
	protected CrokageUtils crokageUtils;
	
	@Autowired
	private GoogleWebSearch googleWebSearch;
	
	//@Test
	public void contextLoads() {
	}

	
	
	
	@Test
	public void testGoogleCall() {
		String googleQuery="write string to text file";
		logger.info("Initiating Google Search... Using query: "+googleQuery);
		Set<Integer> soQuestionsIds = new LinkedHashSet<>();
		
		try {
			SearchQuery searchQuery = new SearchQuery(googleQuery, "stackoverflow.com", 10);
			        //.site("https://stackoverflow.com")
			SearchResult result = googleWebSearch.search(searchQuery,false);
			List<String> urls = result.getUrls();
			BotUtils.identifyQuestionsIdsFromUrls(urls,soQuestionsIds);
						
		} catch (Exception e) {
			System.out.println("Error ... "+e);
			e.printStackTrace();
		}
		
		

	}
	
	

}
