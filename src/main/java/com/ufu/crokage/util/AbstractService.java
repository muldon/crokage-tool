package com.ufu.crokage.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.ufu.crokage.repository.CommentsRepository;
import com.ufu.crokage.repository.EvaluationRepository;
import com.ufu.crokage.repository.ExperimentRepository;
import com.ufu.crokage.repository.ExternalQuestionRepository;
import com.ufu.crokage.repository.GenericRepository;
import com.ufu.crokage.repository.MetricResultRepository;
import com.ufu.crokage.repository.PostsRepository;
import com.ufu.crokage.repository.RankRepository;
import com.ufu.crokage.repository.RelatedPostRepository;
import com.ufu.crokage.repository.ResultRepository;
import com.ufu.crokage.repository.UsersRepository;
import com.ufu.crokage.to.Comment;
import com.ufu.crokage.to.Post;
import com.ufu.crokage.to.User;

public abstract class AbstractService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	protected PostsRepository postsRepository;
	
	@Autowired
	protected CommentsRepository commentsRepository;	
	
	@Autowired
	protected GenericRepository genericRepository;
	
	@Autowired
	protected ExperimentRepository experimentRepository;
	
	@Autowired
	protected ExternalQuestionRepository externalQuestionRepository; 
	
	@Autowired
	protected RelatedPostRepository relatedPostRepository;
	
	@Autowired
	protected EvaluationRepository evaluationRepository;
	
	@Autowired
	protected ResultRepository resultRepository;
	
	@Autowired
	protected RankRepository rankRepository;
	
		
	@Autowired
	protected UsersRepository usersRepository;
	
	
	@Autowired
	protected MetricResultRepository metricResultRepository;
	
	
	protected int countExcludedPosts;
	protected int countPostIsAnAnswer;
	
	public Timestamp getCurrentDate(){
		Timestamp ts_now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return ts_now;
	}
	

	
	private void setCommentsUsers(List<Comment> questionComments) {
		for(Comment comment: questionComments) {
			if(comment.getUserId()!=null) {
				comment.setUser(findUserById(comment.getUserId()));
			}
			//allCommentsIds.add(comment.getId());
		}
		
	}
	
	@Transactional(readOnly = true)
	public User findUserById(Integer userId) {
		return usersRepository.findOne(userId);
	}
	

	/*
	 * Answers have postTypeId = 2
	 */
	@Transactional(readOnly = true)
	public List<Post> findUpVotedAnswersByQuestionId(Integer questionId) {
		//return postsRepository.findByParentIdAndPostTypeId(questionId,2,new Sort(Sort.Direction.ASC, "id"));
		//return postsRepository.findByParentId(questionId,new Sort(Sort.Direction.ASC, "id"));
		return postsRepository.findUpVotedAnswersByQuestionId(questionId);
	}
	
	@Transactional(readOnly = true)
	public Post findPostById(Integer id) {
		return postsRepository.findOne(id);
	}
	

	@Transactional(readOnly = true)
	public List<Comment> getCommentsByPostId(Integer postId) {
		return commentsRepository.findByPostId(postId,new Sort(Sort.Direction.ASC, "id"));
	}

}
