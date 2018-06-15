package com.ufu.bot.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ufu.bot.to.Post;


public interface GenericRepository {
	
	
	public List<Post> findAllQuestions();

	public Map<Integer, Set<Integer>> getAllPostLinks();

	//public Map<Post, List<Post>> getQuestionsByFilters(String tagFilter,Integer limit, String maxCreationDate);
	
	public List<Post> getSomePosts();

	
	//public Set<Post> findClosedDuplicatedNonMastersByTagExceptProcessedQuestions(String tagFilter);

	public Set<Post> getPostsByFilters(String tagFilter);

	public Set<Integer> findRelatedQuestionsIds(Set<Integer> allQuestionsIds);

	
	

	
    
}
