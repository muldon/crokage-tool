package com.ufu.bot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.ExternalQuestion;



public interface ExternalQuestionRepository extends CrudRepository<ExternalQuestion, Integer> {

	@Query(value="select e.* "
			  + " from externalquestion e, survey s"
			  + " where e.surveyid = s.id"
			  + " and s.active=true  "
			  + " order by e.id",nativeQuery=true)
	List<ExternalQuestion> findAllExternalQuestionsForActiveSurvey();

	@Query(value="select e.* "
			  + " from externalquestion e"
			  + " where e.answerbotqueryid is not null "
			  + " order by e.id",nativeQuery=true)
	List<ExternalQuestion> findAllExternalQuestionsAnswerBot();

	/*@Query(value="select e.* "
			  + " from externalquestion e, survey s"
			  + " where e.surveyid = s.id"
			  + " and s.internalsurvey=true "
			  + " and e.userack=?1 "
			  + " order by e.id",nativeQuery=true)
	List<ExternalQuestion> findExternalQuestionsInternalSurvey(boolean userack);*/
	
	List<ExternalQuestion> findByUseRack(boolean userack);

	/*@Query(value="select * " + 
				" from externalquestion eq" + 
				" where eq.surveyid = 1" + 
				" and eq.id not in " + 
				" (select e.externalquestionid" + 
				"  from evaluation e" + 
				"  where e.surveyuserid = ?1)" + 
				" order by externalid  " + 
				" limit 2",nativeQuery=true)
	List<ExternalQuestion> findNextExternalQuestionInternalSurveyUser(Integer userId);*/
    
	
	@Query(value="select * " + 
			" from externalquestion eq" + 
			" where eq.id not in " + 
			" (select r.externalquestionid" + 
			"  from evaluation e, rank r" + 
			"  where r.id = e.rankid "
			+ " and e.surveyuserid = ?1)" + 
			" order by externalid  " + 
			" limit 2",nativeQuery=true)
	List<ExternalQuestion> findNextExternalQuestionInternalSurveyUser(Integer userId);
	
}
