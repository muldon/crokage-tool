package com.ufu;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
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
import com.ufu.bot.util.Matrix;
import com.ufu.crokage.util.CrokageUtils;
import com.ufu.crokage.util.TextNormalizer;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class BotApplicationTests extends AbstractService{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	protected PitBotService pitBotService;
	
	@Autowired
	protected CrokageUtils crokageUtils;
	
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
		logger.info("Testing getComments....");
		Integer postId = 910522;
		List<Comment> comments = pitBotService.getCommentsByPostId(postId);
		for (Comment comment : comments) {
			logger.info(comment.getText());
		}
		

	}
		
	
	//@Test
	public void getAnswers() {
		logger.info("Testing getAnswers....");
		Integer postId = 910374;
		List<Post> answers = pitBotService.findUpVotedAnswersByQuestionId(postId);
		for (Post answer : answers) {
			logger.info(answer.getBody());
		}
		

	}
	
	
	//@Test
	public void getUser() {
		logger.info("Testing getUser....");
		Integer userId = 112532;
		User user = pitBotService.findUserById(userId);
		System.out.println(user);

	}
		
	
	//@Test
	public void getRelatedQuestionsIds() {
		logger.info("Testing getRelatedQuestionsIds....");
		
		Set<Integer> set = new HashSet<>();
		set.add(910374);
		
		Set<Integer> allRelatedQuestionsIds = pitBotService.recoverRelatedQuestionsIds(set,1);
		System.out.println(allRelatedQuestionsIds);

	}
	
	
	
	
	
	
	@Test
	public void testGetClassesFromCodes1() throws Exception {
		logger.info("testStemStop....");
		Integer questionId = 37505462;
		
		Post post = pitBotService.findPostById(questionId);
		String body = post.getBody();
		
		
		Set<String> codeSet = crokageUtils.extractClassesFromCode(body);
		System.out.println(codeSet);
		assertTrue(codeSet.contains("ConstraintViolation"));
		
		//System.out.println(code);
		
		/*System.out.println(TextNormalizer.isCamelCase("Camel01C")); 
		System.out.println(TextNormalizer.isCamelCase("camelC"));
		System.out.println(TextNormalizer.isCamelCase("Camel"));
		System.out.println(TextNormalizer.isCamelCase("CamelCase"));
		System.out.println(TextNormalizer.isCamelCase("camel"));
		System.out.println(TextNormalizer.isCamelCase("Graphics2D"));*/
	
	}
	
	
	@Test
	public void testCamelCase() throws Exception {
		logger.info("testStemStop....");
		Integer questionId = 37505462;
		
		Post post = pitBotService.findPostById(questionId);
		String body = post.getBody();
		
		
		Set<String> codeSet = crokageUtils.extractClassesFromCode(body);
		System.out.println(codeSet);
		assertTrue(codeSet.contains("ConstraintViolation"));
		
		assertTrue(TextNormalizer.isCamelCase("Camel01C")); 
		assertFalse(TextNormalizer.isCamelCase("camelC"));
		assertTrue(TextNormalizer.isCamelCase("Camel"));
		assertTrue(TextNormalizer.isCamelCase("CamelCase"));
		assertFalse(TextNormalizer.isCamelCase("camel"));
		assertTrue(TextNormalizer.isCamelCase("Graphics2D"));
	
	}
	
	
	

	//@Test
	public void testStemStop() throws Exception {
		logger.info("testStemStop....");
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
		
		
		List<Post> relatedPosts = pitBotService.getPostsByExternalQuestionId(1);
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
	
	
	//@Test
	public void simulateUserEvaluation() {
		//List<Evaluation> allEvaluations = (List<Evaluation>)evaluationRepository.findByPhaseUser(4,1);
		List<Evaluation> allEvaluations = pitBotService.getEvaluationsByPhaseAndUser(4,1);
		for(Evaluation evaluation: allEvaluations) {
			Evaluation clone = new Evaluation();
			BeanUtils.copyProperties(evaluation, clone);
			clone.setId(null);
			clone.setSurveyUserId(2);
			pitBotService.saveEvaluation(clone);
		}
		
		
	}	
	
	
	/*@Test
	public void postsWithCode() {
		//List<Evaluation> allEvaluations = (List<Evaluation>)evaluationRepository.findByPhaseUser(4,1);
		List<Post> allEvaluations = pitBotService.getEvaluationsByPhaseAndUser(4,1);
		for(Evaluation evaluation: allEvaluations) {
			Evaluation clone = new Evaluation();
			BeanUtils.copyProperties(evaluation, clone);
			clone.setId(null);
			clone.setSurveyUserId(2);
			pitBotService.saveEvaluation(clone);
		}
		
		
	}	*/
	
	//@Test
	public void testSimilarity() throws Exception {
		
		String query1 = "How to send HTTP request in java?";
		String query2 = "How to programmatically send a HTTP request with parameters?";
		String query3 = "How do I do a HTTP GET in Java?";
		String query4 = "How to call and pass the parameters to the Servlet using the Java in my swing application?";
		String query5 = "How to calling and passing the parameter to the Servlet with the Javac in my swing app?";
		String query6 = "Most efficient way to write a string to a file";
		
		CrokageUtils crokageUtils = new CrokageUtils();
		
		query1 = CrokageUtils.processQuery(query1);
		query2 = CrokageUtils.processQuery(query2);
		query3 = CrokageUtils.processQuery(query3);
		query4 = CrokageUtils.processQuery(query4);
		query5 = CrokageUtils.processQuery(query5);
		query6 = CrokageUtils.processQuery(query6);
		
		long initTime = System.currentTimeMillis();
		
		Map<String, double[]> vectorsWords1 = crokageUtils.readVectorsForQuery(query1);
		crokageUtils.reportElapsedTime(initTime,"readVectorsForQuery");
		Map<String, double[]> vectorsWords2 = crokageUtils.readVectorsForQuery(query2);
		Map<String, double[]> vectorsWords3 = crokageUtils.readVectorsForQuery(query3);
		Map<String, double[]> vectorsWords4 = crokageUtils.readVectorsForQuery(query4);
		Map<String, double[]> vectorsWords5 = crokageUtils.readVectorsForQuery(query5);
		Map<String, double[]> vectorsWords6 = crokageUtils.readVectorsForQuery(query6);
		
		
		
		double[][] matrix1 = CrokageUtils.getMatrixVectorsForQuery(query1, vectorsWords1);
		double[][] matrix2 = CrokageUtils.getMatrixVectorsForQuery(query2, vectorsWords2);
		double[][] matrix3 = CrokageUtils.getMatrixVectorsForQuery(query3, vectorsWords3);
		double[][] matrix4 = CrokageUtils.getMatrixVectorsForQuery(query4, vectorsWords4);
		double[][] matrix5 = CrokageUtils.getMatrixVectorsForQuery(query5, vectorsWords5);
		double[][] matrix6 = CrokageUtils.getMatrixVectorsForQuery(query6, vectorsWords6);
		
		Map<String, Double> soIDFVocabularyMap=new HashMap<>();
		crokageUtils.readIDFVocabulary(soIDFVocabularyMap);
		
		/*double[][] idf1 = CrokageUtils.getIDFMatrixForQuery(query1, soIDFVocabularyMap);
		double[][] idf2 = CrokageUtils.getIDFMatrixForQuery(query2, soIDFVocabularyMap);
		double[][] idf3 = CrokageUtils.getIDFMatrixForQuery(query3, soIDFVocabularyMap);
		double[][] idf4 = CrokageUtils.getIDFMatrixForQuery(query4, soIDFVocabularyMap);
		double[][] idf5 = CrokageUtils.getIDFMatrixForQuery(query5, soIDFVocabularyMap);*/
		double[][] idf1 = {{1,1,1,1}};
		double[][] idf2 = {{1,1,1,1,1}};
		double[][] idf3 = {{1,1,1}};
		double[][] idf4 = {{1,1,1,1,1,1,1,1}};
		double[][] idf5 = {{1,1,1,1,1,1,1}};
		double[][] idf6 = {{1,1,1,1,1}};

		
		
		//System.out.println(vectorsWords1);
		//System.out.println(vectorsWords2);
		//double[][] matrix1 
		
		double simPair12 = Matrix.simDocPair(matrix1,matrix2,idf1,idf2);
		double simPair21 = Matrix.simDocPair(matrix2,matrix1,idf2,idf1);
		double simPair13 = Matrix.simDocPair(matrix1,matrix3,idf1,idf3);
		double simPair14 = Matrix.simDocPair(matrix1,matrix4,idf1,idf4);
		double simPair45 = Matrix.simDocPair(matrix4,matrix5,idf4,idf5);
		double simPair56 = Matrix.simDocPair(matrix5,matrix6,idf5,idf6);
		
		
		System.out.println("simPair12= "+simPair12);
		//System.out.println(simPair21);
		System.out.println("simPair13= "+simPair13);
		System.out.println("simPair14= "+simPair14);
		System.out.println("simPair45= "+simPair45);
		System.out.println("simPair56= "+simPair56);
		
		
	}
	
	@Test
	public void testMethodCallsCapture() {
		Integer answersIds[] = {910522,45960571,2900755,1053475,1911947,34959450,1921200,34959470,1928690,1928707,1928778,1969067,34959570};
		for(Integer postId:answersIds) {
			Post post = postsRepository.findOne(postId);
			System.out.println("id: "+post.getId()+ " \n- code: "+post.getCode()+ " \n\nMethods found: ");
			
			Set<String> codes = crokageUtils.getMethodCalls(post.getCode());
			for(String code:codes) {
				System.out.println(code);
			}
			System.out.println("\n\n\n");
		}
		
		
		

	}
	
	

}
