package com.ufu.bot.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.Survey;



public interface SurveyRepository extends CrudRepository<Survey, Integer> {

	Survey findByInternalSurvey(boolean active);
    
	

	
}
