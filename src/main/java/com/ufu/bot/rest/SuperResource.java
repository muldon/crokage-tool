package com.ufu.bot.rest;

import java.sql.Timestamp;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.ufu.bot.service.TagsService;


@Component
public class SuperResource 
{
	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	protected TagsService tagsService;
	
	
	public String getText(String key){
   		return messageSource.getMessage(key, null , "Default", Locale.getDefault());
   	}
			
	protected Timestamp getCurrentDate(){
		java.util.Date date= new java.util.Date();
		Timestamp ts_now = new Timestamp(date.getTime());
		return ts_now;
	}
	
}