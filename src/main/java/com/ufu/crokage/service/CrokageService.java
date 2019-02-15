package com.ufu.crokage.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufu.crokage.to.Bucket;
import com.ufu.crokage.to.MetricResult;
import com.ufu.crokage.to.Post;
import com.ufu.crokage.util.AbstractService;
import com.ufu.crokage.util.BotComposer;


@Service
@Transactional
public class CrokageService extends AbstractService{
		
	
	
	public CrokageService() {
		
	}

	@Transactional(readOnly = true)
	public List<Post> getAnswersWithCode(String startDate) {
		return genericRepository.getAnswersWithCode(startDate);
		
	}
	
	@Transactional(readOnly = true)
	public List<Post> getUpvotedPostsWithCode(String startDate) {
		return genericRepository.getUpvotedPostsWithCode(startDate);
	}

	

	@Transactional(readOnly = true)
	public List<Integer> findAllQuestionsIds() {
		return postsRepository.findAllQuestionsIds();
	}

	@Transactional(readOnly = true)
	public List<Post> findPostsById(Collection<Integer> somePosts) {
		return genericRepository.getPostsByIds(somePosts);
	}

	@Transactional(readOnly = true)
	public List<Integer> findAllPostsIds() {
		return postsRepository.findAllPostsIds();
	}

	@Transactional(readOnly = true)
	public Map<Integer, String> getQuestionsIdsTitles() {
		return genericRepository.getQuestionsIdsTitles();
	}

	@Transactional(readOnly = true)
	public Map<Integer, Integer> getAnswersIdsParentIds() {
		return genericRepository.getAnswersIdsParentIds();
	}

	@Transactional(readOnly = true)
	public List<Post> findUpVotedAnswersWithCodeByQuestionId(Integer id) {
		return postsRepository.findUpVotedAnswersWithCodeByQuestionId(id);
	}

	
	

	public void saveMetricResult(MetricResult metricResult) {
		metricResult.setClassFreqWeight(BotComposer.getApiWeight());
		metricResult.setMethodFreqWeight(BotComposer.getMethodFreqWeight());
		metricResult.setSimWeight(BotComposer.getSemWeight());
		metricResult.setBm25weight(BotComposer.getBm25Weight());
		metricResult.setTfIdfCosSimWeight(BotComposer.getTfIdfWeight());
		//metricResult.setUpWeight(BotComposer.getUpWeight());
		//metricResult.setRepWeight(BotComposer.getRepWeight());
		metricResultRepository.save(metricResult);
	}

	public Integer getMostUpvotedAnswerForQuestionId(Integer questionId) {
		List<Integer> ids = postsRepository.getMostUpvotedAnswerForQuestionId(questionId);
		return ids.isEmpty() ? null: ids.get(0);
	}
	
	

	public Post getMostUpvotedAnswerForQuestion(Integer questionId,Integer answerId) {
		List<Post> questions = postsRepository.getMostUpvotedAnswerForQuestion(questionId,answerId);
		return questions.isEmpty() ? null: questions.get(0);
	}

	
	public Map<Integer,String> getThreadsIdsTitlesForUpvotedAnswersWithCode() {
		return genericRepository.getThreadsIdsTitlesForUpvotedAnswersWithCode();
	}

	
	
	public Bucket getMostUpvotedAnswerForQuestionId2(Integer questionId) {
		List<Post> buckets = postsRepository.getMostUpvotedAnswerForQuestionId2(questionId);
		Post post= buckets.isEmpty() ? null: buckets.get(0);
		Bucket bucket=null;
		if(post!=null) {
			bucket = new Bucket(post.getId(),post.getProcessedBody());
		}else {
			System.out.println("Bucket null in getMostUpvotedAnswerForQuestionId2 for question:"+questionId);
		}
		return bucket;
	}

	

	public List<Bucket> getUpvotedAnswersIdsContentsAndParentContents() {
		return genericRepository.getUpvotedAnswersIdsContentsAndParentContents();
	}

	

	
	
	
	
	
}
