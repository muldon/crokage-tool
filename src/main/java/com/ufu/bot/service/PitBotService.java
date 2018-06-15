package com.ufu.bot.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.ufu.bot.to.Comment;
import com.ufu.bot.to.Evaluation;
import com.ufu.bot.to.Experiment;
import com.ufu.bot.to.ExternalQuestion;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.RelatedPost;
import com.ufu.bot.to.Result;
import com.ufu.bot.to.SurveyUser;
import com.ufu.bot.to.User;
import com.ufu.bot.util.AbstractRepositoriesUtils;



@Service
@Transactional
public class PitBotService extends AbstractRepositoriesUtils{
		
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	
	public PitBotService() {
		
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
				String title = processedPostOld.getTitle();
				
				String[] titleContent = botUtils.separateWordsCodePerformStemmingStopWords(title,"title");
				processedPostOld.setTitle(titleContent[0] + " "+ titleContent[1]);
				
				String tagssyn = processedPostOld.getTags().replaceAll("<","");
				tagssyn = tagssyn.replaceAll(">"," ");
				tagssyn = botUtils.tagMastering(tagssyn);												
				processedPostOld.setTagsSyn(tagssyn);		
			}
			//respostas nao possuem tag nem title				
			
			String[] bodyContent = botUtils.separateWordsCodePerformStemmingStopWords(body,"body");
			//processedPost.setBody(bodyContent[0] + " "+ bodyContent[1]+ " "+botUtils.formatCode(bodyContent[2]));
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
	public Set<Post> getPostsByFilters(String tagFilter) {
		return genericRepository.getPostsByFilters(tagFilter);
	}

	





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





	public void saveExternalQuestion(ExternalQuestion externalQuestion) {
		externalQuestionRepository.save(externalQuestion);
		
	}





	public List<ExternalQuestion> getAllExternalQuestionsAnswerBot() {
		return externalQuestionRepository.findAllExternalQuestionsAnswerBot();
	}





	public void saveRelatedPost(RelatedPost relatedPost) {
		relatedPostRepository.save(relatedPost);
		
	}





	public List<Post> getRelatedPosts(Integer externalQuestionId) {
		return relatedPostRepository.findRelatedPosts(externalQuestionId);
	}





	public void saveSurveyUser(SurveyUser surveyUser1) {
		surveyUserRepository.save(surveyUser1);
		
	}





	public SurveyUser getSurveyUserByLogin(String login) {
		return surveyUserRepository.findByLogin(login);
	}





	public void saveEvaluation(Evaluation evaluation) {
		evaluationRepository.save(evaluation);
		
	}





	public void saveResult(Result result) {
		resultRepository.save(result);
		
	}

	public void saveExternalQuestionAndRelatedIds(ExternalQuestion answerBotQuestion, Map<Integer, Post> allRetrievedPostsCache) {
		Set<Integer> relatedPostsIds = allRetrievedPostsCache.keySet();
		
		/*
		 * Save the external question
		 */
		externalQuestionRepository.save(answerBotQuestion);
		
		/*
		 * save all related posts 
		 */
		for(Integer relatedPostId: relatedPostsIds) {
			RelatedPost relatedPost = new RelatedPost(relatedPostId,answerBotQuestion.getId());
			relatedPostRepository.save(relatedPost);
		}
		
	}


	


	
	
}
