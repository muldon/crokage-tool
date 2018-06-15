package com.ufu.bot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.ExternalQuestion;



public interface ExternalQuestionRepository extends CrudRepository<ExternalQuestion, Integer> {

	@Query(value="select * "
			  + " from externalquestion e, survey s"
			  + " where e.surveyid = s.id"
			  + " and s.active=true  "
			  + " order by e.id",nativeQuery=true)
	List<ExternalQuestion> findAllExternalQuestionsForActiveSurvey();

	@Query(value="select * "
			  + " from externalquestion e"
			  + " where e.answerbotqueryid is not null "
			  + " order by e.id",nativeQuery=true)
	List<ExternalQuestion> findAllExternalQuestionsAnswerBot();
    
	

	
}
