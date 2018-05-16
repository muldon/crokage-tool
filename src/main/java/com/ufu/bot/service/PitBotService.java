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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufu.bot.repository.ExperimentRepository;
import com.ufu.bot.repository.FeatureRepository;
import com.ufu.bot.repository.GenericRepository;
import com.ufu.bot.repository.PairRepository;
import com.ufu.bot.repository.PostsRepository;
import com.ufu.bot.repository.ProcessedPostsRepository;
import com.ufu.bot.repository.RecallRateRepository;
import com.ufu.bot.to.Experiment;
import com.ufu.bot.to.Posts;
import com.ufu.bot.to.ProcessedPosts;
import com.ufu.bot.to.RecallRate;
import com.ufu.bot.util.BotUtils;



@Service
@Transactional
public class PitBotService {
	
	//@Value("${tagFilter}")
	//public String tagFilter;  //null for all
	
	
	
	@Autowired
	protected PostsRepository postsRepository;
	
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
	private BotUtils botUtils;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	
	public PitBotService() {
		
	}
	
	
	protected Timestamp getCurrentDate(){
		Timestamp ts_now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return ts_now;
	}


	@Transactional(readOnly = true)
	public List<Posts> getSomePosts() {
		return genericRepository.getSomePosts();
	}
	
	@Transactional(readOnly = true)
	public Map<Posts, List<Posts>> getQuestionsByFilters(String tagFilter,Integer limit, String maxCreationDate) {
		return genericRepository.getQuestionsByFilters(tagFilter,limit,maxCreationDate);
	}
	
	

	@Transactional(readOnly = true)
	public Posts findPostById(Integer id) {
		return postsRepository.findOne(id);
	}

	

	@Transactional(readOnly = true)
	public Map<Integer, Set<Integer>> getAllPostLinks() {
		return genericRepository.getAllPostLinks();
	}


	
	public void stemStop(List<ProcessedPosts> somePosts) throws Exception {
		for (ProcessedPosts processedPost : somePosts) {
			String body = processedPost.getBody();
			
			if(processedPost.getPostTypeId().equals(1)){ //pergunta
				String title = processedPost.getTitle();
				
				String[] titleContent = botUtils.separaSomentePalavrasNaoSomentePalavras(title,"title");
				processedPost.setTitle(titleContent[0] + " "+ titleContent[1]);
				
				String tagssyn = processedPost.getTags().replaceAll("<","");
				tagssyn = tagssyn.replaceAll(">"," ");
				tagssyn = botUtils.tagMastering(tagssyn);												
				processedPost.setTagsSyn(tagssyn);		
			}
			//respostas nao possuem tag nem title				
			
			String[] bodyContent = botUtils.separaSomentePalavrasNaoSomentePalavras(body,"body");
			//processedPost.setBody(bodyContent[0] + " "+ bodyContent[1]+ " "+botUtils.formatCode(bodyContent[2]));
			processedPost.setBody(bodyContent[0] + " "+ bodyContent[1]);
			if (!StringUtils.isBlank(bodyContent[2])) {
				processedPost.setCode(bodyContent[2]);
			}
				
				
			
			//question.setTags(tags);														
			processedPostsRepository.save(processedPost);
					
		}
		
	}


	public void savePost(ProcessedPosts newPost) {
		processedPostsRepository.save(newPost);
		
	}

	@Transactional(readOnly = true)
	public Set<ProcessedPosts> getProcessedQuestions(String tagFilter) {
		return genericRepository.getProcessedQuestions(tagFilter);
	}


	@Transactional(readOnly = true)
	public Set<ProcessedPosts> findClosedDuplicatedNonMastersByTag(String tagFilter) throws Exception {
		
		logger.info("recuperando findClosedDuplicatedNonMastersByTag... :"+tagFilter);
		Set<ProcessedPosts> rawClosedDuplicatedNonMastersByTag = genericRepository.findClosedDuplicatedNonMastersByTag(tagFilter);
		logger.info("findClosedDuplicatedNonMastersByTag: "+rawClosedDuplicatedNonMastersByTag.size());
		
		return rawClosedDuplicatedNonMastersByTag;
		
	}
	
	
	@Transactional(readOnly = true)
	public Set<Integer> findClosedDuplicatedNonMastersByTagStrict(String tagFilter) throws Exception {
		
		logger.info("recuperando findClosedDuplicatedNonMastersByTagStrict... :"+tagFilter);
		Set<Integer> rawClosedDuplicatedNonMastersByTag = genericRepository.findClosedDuplicatedNonMastersByTagStrict(tagFilter);
		logger.info("findClosedDuplicatedNonMastersByTagStrict: "+rawClosedDuplicatedNonMastersByTag.size());
		
		return rawClosedDuplicatedNonMastersByTag;
		
	}


	public void saveExperiment(Experiment experiment) {
		experimentRepository.save(experiment);
		
	}


	public void saveRecallRate(RecallRate recallRate) {
		recallRateRepository.save(recallRate);
	}

	@Transactional(readOnly = true)
	public Set<Posts> getQuestionsByFilters(String tagFilter) {
		return genericRepository.getQuestionsByFilters(tagFilter);
	}

	@Transactional(readOnly = true)
	public ProcessedPosts findProcessedPostById(Integer id) {
		return processedPostsRepository.findOne(id);
	}




	public void cleanOldData(String tagFilter) {
		//clean old features for this tag
		featureRepository.deleteByTag(tagFilter);
		pairRepository.deleteByMaintag(tagFilter);
		logger.info("cleaned old data for tag... :"+tagFilter);
	}


	public List<ProcessedPosts> findProcessedPostsByIdIn(Set<Integer> allApiIdsExceptDifferentPairsQuestionsIds) {
		return processedPostsRepository.findByIdIn(allApiIdsExceptDifferentPairsQuestionsIds);
	}


	


	
	
}
