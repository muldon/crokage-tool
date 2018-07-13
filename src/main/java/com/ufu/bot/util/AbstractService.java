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
	protected BotUtils botUtils;
	
	protected int countExcludedPosts;
	protected int countPostIsAnAnswer;
	
	protected Timestamp getCurrentDate(){
		Timestamp ts_now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return ts_now;
	}
	

	@Transactional(readOnly = true)
	public Set<SoThread> assembleListOfThreads(Set<Integer> soPostsIds, Integer relationTypeId) {
		//allQuestionsIds.addAll(soQuestionsIds);
		Set<SoThread> threads = new LinkedHashSet<>();
		boolean postIsQuestion = true;		
		for(Integer questionId: soPostsIds) {
			Post post = findPostById(questionId);
			if(post==null) {
				countExcludedPosts++;
				continue; //could have been excluded 
			}
			
			if(post.getPostTypeId().equals(2)) { //answer
				postIsQuestion=false;
				countPostIsAnAnswer++;
				//fetch the parent only once
				if(!soPostsIds.contains(post.getParentId())) {
					post = findPostById(post.getParentId());
				}else {
					//parent is already present in the list and will be processed later
					continue;
				}
				
			}
			
			if(post.getOwnerUserId()!=null) {
				post.setUser(findUserById(post.getOwnerUserId()));
			}
			
			List<Comment> questionComments = getCommentsByPostId(questionId);
			setCommentsUsers(questionComments);
			post.setComments(questionComments);
			
			botUtils.storeParentPostInCache(post);
			
			List<Post> answers = findUpVotedAnswersByQuestionId(post.getId());
			List<Post> validAnswers = new ArrayList<Post>();
			
			for(Post answer: answers) {
				if(answer.getScore()<1) {
					continue;    //disconsider posts without positive scores
				}
				if(answer.getOwnerUserId()!=null) {
					answer.setUser(findUserById(answer.getOwnerUserId()));
				}
				boolean containCode = BotUtils.containCode(answer.getBody());
				boolean containLink = BotUtils.testContainLinkToSo(answer.getBody());
				if(!containCode) {
					if(!containLink) {
						logger.info("Disconsidering post without any post and without any link :"+answer.getId());
						continue;    //disconsider posts without any code and without any link to another post
					}else {
						logger.info("Answer has no code, but has link and will be cut off later in step 7."+answer.getId());
					}
					
				}
				
				List<Comment> answerComments = getCommentsByPostId(answer.getId());
				setCommentsUsers(answerComments);
				answer.setComments(answerComments);
				if(relationTypeId.equals(RelationTypeEnum.FROM_GOOGLE_QUESTION_OR_ANSWER.getId())) {
					if(postIsQuestion) {
						answer.setRelationTypeId(RelationTypeEnum.FROM_GOOGLE_QUESTION.getId());
					}else {
						answer.setRelationTypeId(RelationTypeEnum.FROM_GOOGLE_ANSWER.getId());
					}
				}else {
					answer.setRelationTypeId(relationTypeId);
				}
				botUtils.storeAnswerPostInCache(answer);
				validAnswers.add(answer);
			}
			
			if(!validAnswers.isEmpty()) {
				SoThread soThread = new SoThread(post,validAnswers);
				threads.add(soThread);
			}
			
			
		}
		
		
		logger.info("Number of posts that has been excluded, or is not present in dataset because it is not a java post, or is newer than the dataset: "+countExcludedPosts);
		
		return threads;
		
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
