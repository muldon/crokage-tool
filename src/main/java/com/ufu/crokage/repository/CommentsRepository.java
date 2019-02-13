package com.ufu.crokage.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.ufu.crokage.to.Comment;



public interface CommentsRepository extends CrudRepository<Comment, Integer> {

	List<Comment> findByPostId(Integer postId, Sort sort);
    
	

	
}
