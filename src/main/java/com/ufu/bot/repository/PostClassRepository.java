package com.ufu.bot.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.PostType;

public interface PostClassRepository extends CrudRepository<PostType, Integer> {

	
	
}
