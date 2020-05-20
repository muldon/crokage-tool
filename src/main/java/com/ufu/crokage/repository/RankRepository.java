package com.ufu.crokage.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ufu.crokage.to.Rank;

public interface RankRepository extends CrudRepository<Rank, Integer> {

	Rank findByRelatedPostIdAndInternalEvaluation(Integer relatedPostId, Boolean internalEvaluation);

	Rank findByRelatedPostIdAndPhase(Integer relatedPostId, Integer phase);

	List<Rank> findByPhase(int phase);

	
	//Rank findByExternalQuestionIdAndPostIdAndInternalEvaluation(Integer externalQuestionId, Integer postId, boolean isInternalSurveyUser);

	

	
	
}
