package com.ufu.crokage.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.crokage.to.User;



public interface UsersRepository extends CrudRepository<User, Integer> {

	
	
}
