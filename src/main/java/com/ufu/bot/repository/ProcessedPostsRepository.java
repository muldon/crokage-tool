package com.ufu.bot.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.ProcessedPosts;



public interface ProcessedPostsRepository extends CrudRepository<ProcessedPosts, Integer> {

	List<ProcessedPosts> findByIdIn(Set<Integer> allApiIdsExceptDifferentPairsQuestionsIds);
    
	

	
}
