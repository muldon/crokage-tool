package com.ufu.survey.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.ufu.bot.to.Comment;
import com.ufu.bot.to.Evaluation;
import com.ufu.bot.to.Experiment;
import com.ufu.bot.to.ExternalQuestion;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.Survey;
import com.ufu.bot.to.SurveyUser;
import com.ufu.bot.to.User;
import com.ufu.bot.util.AbstractRepositoriesUtils;
import com.ufu.survey.transfer.ToTransfer;



@Service
@Transactional
public class PitSurveyService extends AbstractRepositoriesUtils{
		
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${runRack}")
	public Boolean runRack;  
	
	
	public PitSurveyService() {
		
	}
	
	


	@Transactional(readOnly = true)
	public List<Post> getSomePosts() {
		return genericRepository.getSomePosts();
	}
	
	/*@Transactional(readOnly = true)
	public Map<Post, List<Post>> getQuestionsByFilters(String tagFilter,Integer limit, String maxCreationDate) {
		return genericRepository.getQuestionsByFilters(tagFilter,limit,maxCreationDate);
	}*/
	
	

	@Transactional(readOnly = true)
	public Post findPostById(Integer id) {
		return postsRepository.findOne(id);
	}

	

	@Transactional(readOnly = true)
	public Map<Integer, Set<Integer>> getAllPostLinks() {
		return genericRepository.getAllPostLinks();
	}


	/*
	public void stemStop(List<ProcessedPostOld> somePosts) throws Exception {
		for (ProcessedPostOld processedPostOld : somePosts) {
			String body = processedPostOld.getBody();
			
			if(processedPostOld.getPostTypeId().equals(1)){ //pergunta
				String query = processedPostOld.getTitle();
				
				String[] titleContent = pitSurveyUtils.separateWordsCodePerformStemmingStopWords(query,"query");
				processedPostOld.setTitle(titleContent[0] + " "+ titleContent[1]);
				
				String tagssyn = processedPostOld.getTags().replaceAll("<","");
				tagssyn = tagssyn.replaceAll(">"," ");
				tagssyn = pitSurveyUtils.tagMastering(tagssyn);												
				processedPostOld.setTagsSyn(tagssyn);		
			}
			//respostas nao possuem tag nem query				
			
			String[] bodyContent = pitSurveyUtils.separateWordsCodePerformStemmingStopWords(body,"body");
			//processedPost.setBody(bodyContent[0] + " "+ bodyContent[1]+ " "+pitSurveyUtils.formatCode(bodyContent[2]));
			processedPostOld.setBody(bodyContent[0] + " "+ bodyContent[1]);
			if (!StringUtils.isBlank(bodyContent[2])) {
				processedPostOld.setCode(bodyContent[2]);
			}
				
				
			
			//question.setTags(tags);														
			processedPostsRepository.save(processedPostOld);
					
		}
		
	}
*/


	

	public void saveExperiment(Experiment experiment) {
		experimentRepository.save(experiment);
		
	}


	

	@Transactional(readOnly = true)
	public List<Comment> getCommentsByPostId(Integer postId) {
		return commentsRepository.findByPostId(postId,new Sort(Sort.Direction.ASC, "id"));
	}


	/*
	 * Answers have postTypeId = 2
	 */
	public List<Post> findAnswersByQuestionId(Integer questionId) {
		//return postsRepository.findByParentIdAndPostTypeId(questionId,2,new Sort(Sort.Direction.ASC, "id"));
		return postsRepository.findByParentId(questionId,new Sort(Sort.Direction.ASC, "id"));
	}


	public User findUserById(Integer userId) {
		return usersRepository.findOne(userId);
	}


	public Set<Integer> recoverRelatedQuestionsIds(Set<Integer> allQuestionsIds) {
		return genericRepository.findRelatedQuestionsIds(allQuestionsIds);
	}


	public Set<Post> getAllPosts() {
		Set<Post> set = Sets.newHashSet(postsRepository.findAll());
		return set;
		
	}

	@Transactional(readOnly = true)
	public ExternalQuestion findExternalQuestionById(Integer externalQuestionId) {
		return externalQuestionRepository.findOne(externalQuestionId);
	}


	@Transactional(readOnly = true)
	public List<ExternalQuestion> getAllExternalQuestionsForActiveSurvey(Integer userId) {
		List<ExternalQuestion> externalQuestions = externalQuestionRepository.findAllExternalQuestionsForActiveSurvey(); 
		for(ExternalQuestion externalQuestion: externalQuestions) {
			
		}
		
		
		
		return externalQuestions;
	}


	

	public Survey saveSurvey(Survey survey) {
		return surveyRepository.save(survey);
		
	}


	@Transactional(readOnly = true)
	public Survey getInternalSurvey() {
		return surveyRepository.findByInternalSurvey(true);
	}


	public ExternalQuestion saveExternalQuestion(ExternalQuestion externalQuestion) {
		return externalQuestionRepository.save(externalQuestion);
		
	}


	public void saveEvaluation(Evaluation evaluation) {
		evaluation.setRatingDate(getCurrentDate());
		
	}




	public SurveyUser authenticateUser(SurveyUser surveyUser) {
		return surveyUserRepository.findByLogin(surveyUser.getLogin());
		
	}




	public SurveyUser loadUser(Integer userId) {
		return surveyUserRepository.findOne(userId);
	}




	public void loadQuestions(ToTransfer toTransfer, Boolean internalSurvey) {
		int randomNum;
		if(internalSurvey) {
			List<ExternalQuestion> externalQuestionsWithRack = externalQuestionRepository.findExternalQuestionsInternalSurvey(true);
			List<ExternalQuestion> externalQuestionsWithoutRack = externalQuestionRepository.findExternalQuestionsInternalSurvey(false);
						
			toTransfer.setList(externalQuestionsWithRack);
					
			List<ExternalQuestion> randomList1= new ArrayList<>();
			List<ExternalQuestion> randomList2= new ArrayList<>();
			
			//para testes - retirar
			if(externalQuestionsWithoutRack.isEmpty()) {
				externalQuestionsWithoutRack.addAll(externalQuestionsWithRack);
			}
			
			for(int i=0; i<externalQuestionsWithoutRack.size(); i++) {//lists contain the same size
				randomNum = ThreadLocalRandom.current().nextInt(1, 3);
				if(randomNum==1) {
					randomList1.add(externalQuestionsWithRack.get(i));
					randomList2.add(externalQuestionsWithoutRack.get(i));
				}else {
					randomList2.add(externalQuestionsWithRack.get(i));
					randomList1.add(externalQuestionsWithoutRack.get(i));
				}
			}
			
			toTransfer.setList2(randomList1);
			toTransfer.setList3(randomList2);
		
		}else {
			//use the winner (parameterized) perspective to show questions
			List<ExternalQuestion> externalQuestionsWithOrWithoutRack = externalQuestionRepository.findExternalQuestionsInternalSurvey(runRack);
			
			
		}
		
		
		
	}


	


	
	
}
