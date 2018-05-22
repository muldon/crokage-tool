package com.ufu.bot.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.Post;



public interface PostsRepository extends CrudRepository<Post, Integer> {

	List<Post> findByParentIdAndPostTypeId(Integer questionId, Integer postTypeId, Sort sort);

	List<Post> findByParentId(Integer questionId, Sort sort);
    
	

	
}
