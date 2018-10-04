package com.ufu.bot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufu.bot.to.Post;
import com.ufu.bot.util.AbstractService;



@Service
@Transactional
public class CrokageService extends AbstractService{
		
	
	
	public CrokageService() {
		
	}

	@Transactional(readOnly = true)
	public List<Post> getAnswersWithCode(String startDate) {
		return genericRepository.getAnswersWithCode(startDate);
		
	}
	
	
	
	
}
