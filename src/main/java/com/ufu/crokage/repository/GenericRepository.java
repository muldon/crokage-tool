package com.ufu.crokage.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ufu.crokage.to.Bucket;
import com.ufu.crokage.to.Evaluation;
import com.ufu.crokage.to.ExternalQuestion;
import com.ufu.crokage.to.Post;


public interface GenericRepository {
	
	
	public List<Post> findAllQuestions();

	public Map<Integer, Set<Integer>> getAllPostLinks();

	//public Map<Post, List<Post>> getQuestionsByFilters(String tagFilter,Integer limit, String maxCreationDate);
	
	public List<Post> getSomePosts();

	
	//public Set<Post> findClosedDuplicatedNonMastersByTagExceptProcessedQuestions(String tagFilter);

	public Set<Post> getPostsByFilters(String tagFilter);

	public Set<Integer> findRelatedQuestionsIds(Set<Integer> allQuestionsIds, Integer linkTypeId);

	public List<Post> findRankedList(Integer id, int userId, int phaseNum);

	List<ExternalQuestion> findNextExternalQuestionInternalSurveyUser(Integer userId, Integer phaseNumber);

	public List<Evaluation> getEvaluationByPhaseAndRelatedPost(Integer externalQuestionId, Integer phaseNumber);

	public List<ExternalQuestion> getExternalQuestionsByPhase(Integer phaseNumber);

	public List<Post> findRankedPosts(Integer id, Integer userId, int phaseNum);

	public List<Post> findRankedEvaluatedPosts(Integer id, Integer userId, int phaseNum);

	public List<Post> getAnswersWithCode(String startDate);

	public List<Post> getPostsByIds(Collection<Integer> postsListIds);

	public List<Bucket> getBucketsByIds(Set<Integer> postsListIds);

	public Map<Integer, String> getQuestionsIdsTitles();

	public Map<Integer, Integer> getAnswersIdsParentIds();

	List<Bucket> getQuestionsProcessedTitlesBodiesCodes();

	public List<Post> getUpvotedPostsWithCode(String startDate);

	public List<Bucket> getUpvotedAnswersIdsContentsAndParentContents();

	Map<Integer, String> getThreadsIdsTitlesForUpvotedAnswersWithCode();

	
	
    
}
