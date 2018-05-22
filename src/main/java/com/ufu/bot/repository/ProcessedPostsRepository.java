package com.ufu.bot.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.ProcessedPostOld;



public interface ProcessedPostsRepository extends CrudRepository<ProcessedPostOld, Integer> {

	List<ProcessedPostOld> findByIdIn(Set<Integer> allApiIdsExceptDifferentPairsQuestionsIds);
    
	

	
}
