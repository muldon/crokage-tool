package com.ufu;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.code.stackexchange.client.query.StackExchangeApiQueryFactory;
import com.google.code.stackexchange.common.PagedList;
import com.google.code.stackexchange.schema.Paging;
import com.google.code.stackexchange.schema.Question;
import com.google.code.stackexchange.schema.StackExchangeSite;
import com.ufu.bot.googleSearch.GoogleWebSearch;
import com.ufu.bot.googleSearch.SearchQuery;
import com.ufu.bot.googleSearch.SearchResult;
import com.ufu.bot.service.PitBotService;
import com.ufu.bot.to.Comment;
import com.ufu.bot.to.Evaluation;
import com.ufu.bot.to.Experiment;
import com.ufu.bot.to.ExternalQuestion;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.Rank;
import com.ufu.bot.to.RelatedPost;
import com.ufu.bot.to.Result;
import com.ufu.bot.to.SurveyUser;
import com.ufu.bot.to.User;
import com.ufu.bot.util.AbstractService;
import com.ufu.bot.util.BotUtils;
import com.ufu.bot.util.Matrix;
import com.ufu.crokage.util.CrokageUtils;
import com.ufu.crokage.util.TextNormalizer;

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
