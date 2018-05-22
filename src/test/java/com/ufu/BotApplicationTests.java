package com.ufu;

import java.util.HashSet;
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

import com.google.code.stackexchange.client.query.StackExchangeApiQueryFactory;
import com.google.code.stackexchange.common.PagedList;
import com.google.code.stackexchange.schema.Paging;
import com.google.code.stackexchange.schema.Question;
import com.google.code.stackexchange.schema.StackExchangeSite;
import com.ufu.bot.googleSearch.GoogleWebSearchOld;
import com.ufu.bot.googleSearch.SearchQuery;
import com.ufu.bot.googleSearch.SearchResult;
import com.ufu.bot.service.PitBotService;
import com.ufu.bot.service.TagsService;
import com.ufu.bot.to.Comment;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.Tags;
import com.ufu.bot.to.User;
import com.ufu.bot.util.BotUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class BotApplicationTests {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private TagsService tagsService;
	
	@Autowired
	private PitBotService pitBotService;
	
	@Autowired
	private BotUtils botUtils;
	
	//@Test
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
	
	
	@Test
	public void getComments() {
		logger.info("\n\nTesting getComments....");
		Integer postId = 910522;
		List<Comment> comments = pitBotService.getCommentsByPostId(postId);
		for (Comment comment : comments) {
			logger.info(comment.getText());
		}
		

	}
		
	
	@Test
	public void getAnswers() {
		logger.info("\n\nTesting getAnswers....");
		Integer postId = 910374;
		List<Post> answers = pitBotService.findAnswersByQuestionId(postId);
		for (Post answer : answers) {
			logger.info(answer.getBody());
		}
		

	}
	
	
	@Test
	public void getUser() {
		logger.info("\n\nTesting getUser....");
		Integer userId = 112532;
		User user = pitBotService.findUserById(userId);
		System.out.println(user);

	}
		
	
	@Test
	public void getRelatedQuestionsIds() {
		logger.info("\n\nTesting getRelatedQuestionsIds....");
		
		Set<Integer> set = new HashSet<>();
		set.add(910374);
		
		
		Set<Integer> allRelatedQuestionsIds = pitBotService.recoverRelatedQuestionsIds(set);
		System.out.println(allRelatedQuestionsIds);

	}
	
	

	@Test
	public void testStemStop() throws Exception {
		logger.info("\n\ntestStemStop....");
		Integer questionId = 910522;
		
		Post post = pitBotService.findPostById(questionId);
		botUtils.initializeConfigs();
		String[] bodyContent = botUtils.separateWordsCodePerformStemmingStopWords(post.getBody(),"body");
		System.out.println(bodyContent[0]);
		System.out.println(bodyContent[1]);
		System.out.println(bodyContent[2]);
		

	}
		
	

}
