package com.ufu.bot.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.Evaluation;

public interface EvaluationRepository extends CrudRepository<Evaluation, Integer> {

	Evaluation findByExternalQuestionIdAndPostIdAndSurveyUserId(Integer id, Integer id2, Integer userId);
    
	

	
}
