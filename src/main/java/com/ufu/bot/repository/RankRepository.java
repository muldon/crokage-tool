package com.ufu.bot.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.Rank;

public interface RankRepository extends CrudRepository<Rank, Integer> {

	Rank findByRelatedPostIdAndInternalEvaluation(Integer relatedPostId, Boolean internalEvaluation);

	
	//Rank findByExternalQuestionIdAndPostIdAndInternalEvaluation(Integer externalQuestionId, Integer postId, boolean isInternalSurveyUser);

	

	
	
}
