package com.ufu;

import java.sql.Timestamp;
import java.util.Calendar;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class BotApplicationTests extends AbstractService{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	protected PitBotService pitBotService;
	
	//@Test
	public void contextLoads() {
	}

	
	
	
	/*//@Test
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
	*/
	
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
	
	

	//@Test
	public void getPost() {
		Integer postId = 12125311;
		Post post = postsRepository.findOne(postId);
		System.out.println(post);

	}
	
	
	//@Test
	public void getComments() {
		logger.info("\n\nTesting getComments....");
		Integer postId = 910522;
		List<Comment> comments = pitBotService.getCommentsByPostId(postId);
		for (Comment comment : comments) {
			logger.info(comment.getText());
		}
		

	}
		
	
	//@Test
	public void getAnswers() {
		logger.info("\n\nTesting getAnswers....");
		Integer postId = 910374;
		List<Post> answers = pitBotService.findUpVotedAnswersByQuestionId(postId);
		for (Post answer : answers) {
			logger.info(answer.getBody());
		}
		

	}
	
	
	//@Test
	public void getUser() {
		logger.info("\n\nTesting getUser....");
		Integer userId = 112532;
		User user = pitBotService.findUserById(userId);
		System.out.println(user);

	}
		
	
	//@Test
	public void getRelatedQuestionsIds() {
		logger.info("\n\nTesting getRelatedQuestionsIds....");
		
		Set<Integer> set = new HashSet<>();
		set.add(910374);
		
		
		Set<Integer> allRelatedQuestionsIds = pitBotService.recoverRelatedQuestionsIds(set,1);
		System.out.println(allRelatedQuestionsIds);

	}
	
	

	//@Test
	public void testStemStop() throws Exception {
		logger.info("\n\ntestStemStop....");
		Integer questionId = 910522;
		
		Post post = pitBotService.findPostById(questionId);
		botUtils.initializeConfigs();
		String[] bodyContent = botUtils.separateWordsCodePerformStemmingStopWords(post.getBody(),true);
		System.out.println(bodyContent[0]);
		System.out.println(bodyContent[1]);
		System.out.println(bodyContent[2]);
		

	}
		
	//@Test
	public void testExternalQuestions() {
		ExternalQuestion externalQuestion = new ExternalQuestion(1,"queryLine","googlequery","classes",true,"obs","linkLine");
		pitBotService.saveExternalQuestion(externalQuestion);
		System.out.println(externalQuestion);
		
		List<ExternalQuestion> answerBotQuestions = pitBotService.getAllExternalQuestionsAnswerBot();
		System.out.println(answerBotQuestions);
	}
	
	//@Test
	public void testRelatedQuestions() {
		RelatedPost relatedPost = new RelatedPost(12125311,1,1);
		//pitBotService.saveRelatedPost(relatedPost);
		System.out.println(relatedPost);
		
		
		List<Post> relatedPosts = pitBotService.getRelatedPosts(1);
		System.out.println(relatedPosts);
	}
	
	//@Test
	public void testRank() {
		
		Rank rank = new Rank(1,1,true,1);
		rankRepository.save(rank);
		//System.out.println(relatedPost);
				
	}
	
	
	//@Test
	public void insertSurveyUsers() {
		String user1 = "05134163669";
		String user2 = "1234567899";
		SurveyUser surveyUser1 = new SurveyUser(user1,"rodrigo");
		SurveyUser surveyUser2 = new SurveyUser(user2,"klerisson");
		pitBotService.saveSurveyUser(surveyUser1);
		pitBotService.saveSurveyUser(surveyUser2);
		
		System.out.println(surveyUser1);
		
		SurveyUser surveyUser = pitBotService.getSurveyUserByLogin(user1);
		System.out.println(surveyUser);
	}
	
	//@Test
	public void testEvaluation() {
		Evaluation evaluation = new Evaluation(1,1,5,new Timestamp(Calendar.getInstance().getTimeInMillis()));
		pitBotService.saveEvaluation(evaluation);
		System.out.println(evaluation);
	}	
	
	//@Test
	public void testExperiment() {
		Experiment experiment = new Experiment(1,0.12,0.24,0.55,0.76,0.99,new Timestamp(Calendar.getInstance().getTimeInMillis()),"duration...","first test");
		pitBotService.saveExperiment(experiment);
		System.out.println(experiment);
		
		
	}	
	
	//@Test
	public void testResult() {
		Result result = new Result(1,"obs...",4,1,0.12,0.24,0.55,0.76);
		pitBotService.saveResult(result);
		System.out.println(result);
		
		
	}	

}
