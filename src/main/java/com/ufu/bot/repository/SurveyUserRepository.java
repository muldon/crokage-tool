package com.ufu.bot.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.SurveyUser;



public interface SurveyUserRepository extends CrudRepository<SurveyUser, Integer> {

	SurveyUser findByLogin(String login);
    
	

	
}
