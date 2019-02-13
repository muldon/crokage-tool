package com.ufu.crokage.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.crokage.to.MetricResult;

public interface MetricResultRepository extends CrudRepository<MetricResult, Integer> {

	//Rank findByExternalQuestionIdAndPostIdAndInternalEvaluation(Integer externalQuestionId, Integer postId, boolean isInternalSurveyUser);

	

	
	
}
