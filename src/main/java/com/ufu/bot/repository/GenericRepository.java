package com.ufu.bot.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ufu.bot.to.Posts;
import com.ufu.bot.to.ProcessedPosts;


public interface GenericRepository {
	
	
	public List<Posts> findAllQuestions();

	public Map<Integer, Set<Integer>> getAllPostLinks();

	public Map<Posts, List<Posts>> getQuestionsByFilters(String tagFilter,Integer limit, String maxCreationDate);
	
	public List<Posts> getSomePosts();

	public Set<ProcessedPosts> getProcessedQuestions(String tagFilter);

	//public Set<Posts> findClosedDuplicatedNonMastersByTagExceptProcessedQuestions(String tagFilter);

	public Set<Posts> getQuestionsByFilters(String tagFilter);

	public Set<ProcessedPosts> findClosedDuplicatedNonMastersByTag(String tagFilter);

	public Set<Integer> findClosedDuplicatedNonMastersByTagStrict(String tagFilter);
	

	
    
}
