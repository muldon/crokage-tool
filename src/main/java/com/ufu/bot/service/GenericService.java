package com.ufu.bot.service;

import java.sql.Timestamp;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ufu.bot.repository.TagsRepository;


public class GenericService {
	@Autowired
	protected TagsRepository tagsRepository;
		
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	protected Timestamp getCurrentDate(){
		Timestamp ts_now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return ts_now;
	}
	
	
		
}
