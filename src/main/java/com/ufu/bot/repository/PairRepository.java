package com.ufu.bot.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.Pair;



public interface PairRepository extends CrudRepository<Pair, Integer> {

	void deleteByMaintag(String tagFilter);
    
	

	
}
