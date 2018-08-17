package com.ufu.bot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.Evaluation;

public interface EvaluationRepository extends CrudRepository<Evaluation, Integer> {

	@Query(value="select e.* "
			  + " from evaluation e, rank r"
			  + " where r.id = e.rankid"
			  + " and r.phase = ?  "
			  + " and e.surveyuserid = ?",nativeQuery=true)
	List<Evaluation> findByPhaseUser(Integer phase, Integer userId);

	//Evaluation findByExternalQuestionIdAndPostIdAndSurveyUserId(Integer id, Integer id2, Integer userId);
    
	

	
}
