package com.ufu.bot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufu.bot.to.Tags;


@Service
@Transactional
public class TagsService extends GenericService{


	@Transactional(readOnly = true)
	public List<Tags> loadAll() {
		return (List<Tags>) tagsRepository.findAll();
	}

	
	public Tags save(Tags tags) {
		return tagsRepository.save(tags);
	}


	public void delete(Tags tags) {
		tagsRepository.delete(tags);
	}

	@Transactional(readOnly = true)
	public Tags findOne(Integer id) {
		return tagsRepository.findOne(id);
	}


	
	
	
}
