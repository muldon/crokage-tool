package com.ufu.bot.util;

import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;

import com.ufu.bot.repository.CommentsRepository;
import com.ufu.bot.repository.EvaluationRepository;
import com.ufu.bot.repository.ExperimentRepository;
import com.ufu.bot.repository.ExternalQuestionRepository;
import com.ufu.bot.repository.GenericRepository;
import com.ufu.bot.repository.PostsRepository;
import com.ufu.bot.repository.RelatedPostRepository;
import com.ufu.bot.repository.ResultRepository;
import com.ufu.bot.repository.SurveyRepository;
import com.ufu.bot.repository.SurveyUserRepository;
import com.ufu.bot.repository.UsersRepository;

public abstract class AbstractRepositoriesUtils {
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
	protected UsersRepository usersRepository;
	
	@Autowired
	protected SurveyRepository surveyRepository;
	
	@Autowired
	protected SurveyUserRepository surveyUserRepository;
		
	@Autowired
	protected BotUtils botUtils;
	
	
	protected Timestamp getCurrentDate(){
		Timestamp ts_now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return ts_now;
	}
}
