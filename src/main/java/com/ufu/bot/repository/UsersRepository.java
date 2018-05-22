package com.ufu.bot.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.User;



public interface UsersRepository extends CrudRepository<User, Integer> {

	
	
}
