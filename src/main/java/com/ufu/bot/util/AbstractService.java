package com.ufu.bot.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.ufu.bot.repository.CommentsRepository;
import com.ufu.bot.repository.EvaluationRepository;
import com.ufu.bot.repository.ExperimentRepository;
import com.ufu.bot.repository.ExternalQuestionRepository;
import com.ufu.bot.repository.GenericRepository;
import com.ufu.bot.repository.MetricResultRepository;
import com.ufu.bot.repository.PostsRepository;
import com.ufu.bot.repository.RankRepository;
import com.ufu.bot.repository.RelatedPostRepository;
import com.ufu.bot.repository.ResultRepository;
import com.ufu.bot.repository.SurveyRepository;
import com.ufu.bot.repository.SurveyUserRepository;
import com.ufu.bot.repository.UsersRepository;
import com.ufu.bot.to.Comment;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.RelatedPost.RelationTypeEnum;
import com.ufu.bot.to.SoThread;
import com.ufu.bot.to.User;

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
	protected SurveyRepository surveyRepository;
	
	@Autowired
	protected SurveyUserRepository surveyUserRepository;
	
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
