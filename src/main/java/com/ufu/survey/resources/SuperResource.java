package com.ufu.survey.resources;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ufu.survey.service.PitSurveyService;
import com.ufu.survey.util.PitSurveyUtils;


@Component
public class SuperResource 
{
	@Autowired
	protected PitSurveyUtils pitSurveyUtils;
	@Autowired
	protected PitSurveyService pitSurveyService;
	@Autowired
	
	
			
	protected Timestamp getCurrentDate(){
		java.util.Date date= new java.util.Date();
		Timestamp ts_now = new Timestamp(date.getTime());
		return ts_now;
	}
	
}