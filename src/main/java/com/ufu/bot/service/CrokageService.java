package com.ufu.bot.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public Set<Post> getPostsByIds(List<Integer> postsListIds) {
		Set<Post> fetchedPosts = new HashSet<>();
		
		/*List<Post> fetchedPosts = new ArrayList<>();
		int maxQuestions = 50;
		List<Integer> somePostsQuestionsIds = new ArrayList<>(); 
		
		int init=0;
		int end=maxQuestions;
		int remainingSize = postsListIds.size();
		logger.info("all questions ids:  "+remainingSize);
		while(remainingSize>maxQuestions){ //this is for memory issues
			somePostsQuestionsIds = postsListIds.subList(init, end);
			fetchedPosts.addAll(genericRepository.getPostsByIds(somePostsQuestionsIds));
			remainingSize = remainingSize - maxQuestions;
			init+=maxQuestions;
			end+=maxQuestions;
		}
		somePostsQuestionsIds = postsListIds.subList(init, init+remainingSize);
		fetchedPosts.addAll(genericRepository.getPostsByIds(somePostsQuestionsIds));*/
		
		fetchedPosts.addAll(genericRepository.getPostsByIds(postsListIds));
		return fetchedPosts;
	}
	
	
	
	
}
