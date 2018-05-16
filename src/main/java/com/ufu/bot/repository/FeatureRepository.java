package com.ufu.bot.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.Feature;



public interface FeatureRepository extends CrudRepository<Feature, Integer> {

	List<Feature> findByPairId(Integer id, Sort sort);

	@Modifying
	@Query(value= "delete from feature where pairid in (select id from pair where maintag = ?)", nativeQuery=true)
	void deleteByTag(String tagFilter);
    
	

	
}
