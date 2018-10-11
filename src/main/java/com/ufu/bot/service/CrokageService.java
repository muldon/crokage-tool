package com.ufu.bot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufu.bot.to.Bucket;
import com.ufu.bot.to.Post;
import com.ufu.bot.util.AbstractService;


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
	public List<Bucket> getBucketsByIds(List<Integer> postsListIds) {
		return genericRepository.getBucketsByIds(postsListIds);
	}

	@Transactional(readOnly = true)
	public List<Integer> findAllQuestionsIds() {
		return postsRepository.findAllQuestionsIds();
	}

	@Transactional(readOnly = true)
	public List<Post> findPostsById(List<Integer> somePosts) {
		return genericRepository.getPostsByIds(somePosts);
	}

	public List<Integer> findAllPostsIds() {
		return postsRepository.findAllPostsIds();
	}
	
	
	
	
	
	
	
}
