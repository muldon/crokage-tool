package com.ufu.bot.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ufu.bot.to.Post;



public interface PostsRepository extends CrudRepository<Post, Integer> {

	List<Post> findByParentIdAndPostTypeId(Integer questionId, Integer postTypeId, Sort sort);

	List<Post> findByParentId(Integer questionId, Sort sort);

	@Query(value="select p.* "
			  + " from postsmin p"
			  + " where p.parentid = ?1"
			  + " and p.score>0  "
			  + " order by p.id",nativeQuery=true)
	List<Post> findUpVotedAnswersByQuestionId(Integer questionId);

	@Query(value="select p.id "
			  + " from postsmin p"
			  + " where p.posttypeid = 1",nativeQuery=true)
	List<Integer> findAllQuestionsIds();

	@Query(value="select p.id "
			  + " from postsmin p",nativeQuery=true)
	List<Integer> findAllPostsIds();

	
	

	
}
