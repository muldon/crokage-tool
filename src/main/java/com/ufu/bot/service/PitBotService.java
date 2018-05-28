package com.ufu.bot.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.ufu.bot.repository.CommentsRepository;
import com.ufu.bot.repository.ExperimentRepository;
import com.ufu.bot.repository.FeatureRepository;
import com.ufu.bot.repository.GenericRepository;
import com.ufu.bot.repository.PairRepository;
import com.ufu.bot.repository.PostsRepository;
import com.ufu.bot.repository.ProcessedPostsRepository;
import com.ufu.bot.repository.RecallRateRepository;
import com.ufu.bot.repository.UsersRepository;
import com.ufu.bot.to.Comment;
import com.ufu.bot.to.Experiment;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.ProcessedPostOld;
import com.ufu.bot.to.RecallRate;
import com.ufu.bot.to.User;
import com.ufu.bot.util.BotUtils;



@Service
@Transactional
public class PitBotService {
	
	@Autowired
	protected PostsRepository postsRepository;
	
	@Autowired
	protected CommentsRepository commentsRepository;
	
	@Autowired
	protected ProcessedPostsRepository processedPostsRepository;
	
	
	@Autowired
	protected GenericRepository genericRepository;
	
	@Autowired
	protected ExperimentRepository experimentRepository;
	
	@Autowired
	protected RecallRateRepository recallRateRepository;
	
	@Autowired
	protected FeatureRepository featureRepository;
	
	@Autowired
	protected PairRepository pairRepository;
	
	@Autowired
	protected UsersRepository usersRepository;
	
	
	@Autowired
	private BotUtils botUtils;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	
	public PitBotService() {
		
	}
	
	
	protected Timestamp getCurrentDate(){
		Timestamp ts_now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return ts_now;
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

	public void savePost(ProcessedPostOld newPost) {
		processedPostsRepository.save(newPost);
		
	}

	@Transactional(readOnly = true)
	public Set<ProcessedPostOld> getProcessedQuestions(String tagFilter) {
		return genericRepository.getProcessedQuestions(tagFilter);
	}


	

	public void saveExperiment(Experiment experiment) {
		experimentRepository.save(experiment);
		
	}


	public void saveRecallRate(RecallRate recallRate) {
		recallRateRepository.save(recallRate);
	}

	@Transactional(readOnly = true)
	public Set<Post> getPostsByFilters(String tagFilter) {
		return genericRepository.getPostsByFilters(tagFilter);
	}

	@Transactional(readOnly = true)
	public ProcessedPostOld findProcessedPostById(Integer id) {
		return processedPostsRepository.findOne(id);
	}




	public void cleanOldData(String tagFilter) {
		//clean old features for this tag
		featureRepository.deleteByTag(tagFilter);
		pairRepository.deleteByMaintag(tagFilter);
		logger.info("cleaned old data for tag... :"+tagFilter);
	}


	public List<ProcessedPostOld> findProcessedPostsByIdIn(Set<Integer> allApiIdsExceptDifferentPairsQuestionsIds) {
		return processedPostsRepository.findByIdIn(allApiIdsExceptDifferentPairsQuestionsIds);
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


	


	
	
}
