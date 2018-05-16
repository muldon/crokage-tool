package com.ufu;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.code.stackexchange.client.query.StackExchangeApiQueryFactory;
import com.google.code.stackexchange.common.PagedList;
import com.google.code.stackexchange.schema.Paging;
import com.google.code.stackexchange.schema.Question;
import com.google.code.stackexchange.schema.StackExchangeSite;
import com.ufu.bot.googleSearch.GoogleWebSearchOld;
import com.ufu.bot.googleSearch.SearchQuery;
import com.ufu.bot.googleSearch.SearchResult;
import com.ufu.bot.service.TagsService;
import com.ufu.bot.to.Tags;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class BotApplicationTests {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private TagsService tagsService;
	@Test
	public void contextLoads() {
	}

	//@Test
	public void getTags() {
		List<Tags> tags = tagsService.loadAll();
		for (Tags tag : tags) {
			logger.info(tag.toString());
		}
		// perspectivaService.saveAll(perspectivas);

	}
	
	
	//@Test
	public void testGoogleSearch(){
		 SearchQuery query = new SearchQuery.Builder("“Rails get index of “each” loop ruby-on-rails ruby")
			        .site("https://stackoverflow.com")
			        .numResults(10).build();
			    SearchResult result = new GoogleWebSearchOld().search(query);
			    //assertThat(result.getSize(), equalTo(10));
			    List<String> urls = result.getUrls();
			    for(String url: urls){
			    	System.out.println(url);
			    }
	}
	
	
	//@Test
	public void testSoSearch(){
		StackExchangeApiQueryFactory queryFactory = StackExchangeApiQueryFactory
		        .newInstance("applicationKey", 
		        StackExchangeSite.STACK_OVERFLOW);


		// Get all questions.
		String filter = "default";
		String tag= "java";
		
		Paging paging = new Paging(1, 100);
		PagedList<Question> questions = queryFactory.newQuestionApiQuery()
		     .withPaging(paging).withFilter(filter)
		     .withSort(Question.SortOrder.MOST_HOT)
		     .withTags(tag).list();

		//Get questions by answer ids. 
		long answerId = 21859130; 
		List<Question> question3s = queryFactory.newQuestionApiQuery()
		     .withPaging(paging).withFilter(filter)
		     .withSort(Question.SortOrder.MOST_RECENTLY_CREATED)
		     .withTags(tag).withAnswerIds(answerId).listQuestionsByAnswer();
		
		System.out.println();
		
	}
	

}
