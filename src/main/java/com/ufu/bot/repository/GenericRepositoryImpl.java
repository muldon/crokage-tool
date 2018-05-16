package com.ufu.bot.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ufu.bot.to.PostLink;
import com.ufu.bot.to.Posts;
import com.ufu.bot.to.ProcessedPosts;
import com.ufu.bot.util.BotUtils;

@Repository
public class GenericRepositoryImpl implements GenericRepository {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;



	@Override
	public List<Posts> findAllQuestions() {
		Query q = em.createNativeQuery("select * from posts p where p.posttypeid =1 order by id desc", Posts.class);
		return (List<Posts>) q.getResultList();
	}

	

	@Override
	@Deprecated
	public Map<Posts, List<Posts>> getQuestionsByFilters(String tagFilter, Integer limit, String maxCreationDate) {
		Map<Posts, List<Posts>> postsByFilterMap = new HashMap<>();
		Map<Integer, Posts> questionsMap = new HashMap<>();
		
		logger.info("getQuestionsByFilters: "
				+ "\n tag: "+tagFilter
				+ "\n limit "+limit
				+ "\n maxCreationDate "+maxCreationDate
				);
		
		String sql = " select * from posts  p " 
				+ " WHERE   p.posttypeId = 1 "
				+ " and p.title is not null "
				+ " and p.body is not null ";
			//	+ " and p.AnswerCount > 0 ";
		
		
		if(!StringUtils.isBlank(maxCreationDate)) {
			sql+= " and p.creationdate < '"+maxCreationDate+"'";
		}
		
		sql += BotUtils.getQueryComplementByTag(tagFilter);
		
		sql += " order by p.id ";
		
		if(limit!=null){
			sql+= " limit "+limit;
		}
		
		Query q = em.createNativeQuery(sql, Posts.class);
		
		List<Posts> questionsTmp = q.getResultList();
		List<Posts> questions = new ArrayList<Posts>();
				
		logger.info("Questions with AnswerCount > 0: "+questionsTmp.size()+"\nNow fetching answers for each question...");
		
		String questionsIds = "(";
		
		for(Posts question:questionsTmp){
			if(StringUtils.isBlank(question.getTitle()) || StringUtils.isBlank(question.getBody())) {
				continue;
			}	
			questions.add(question);
			questionsMap.put(question.getId(), question);
			questionsIds += question.getId()+ ",";
		}
		questionsIds += "#";
		questionsIds = questionsIds.replaceAll(",#", ")");
		
		
		List<Posts> answersWithCode = getAnwersForParentsIds(questionsIds);
		logger.info("Done retrieving answers with code, total: "+answersWithCode.size()+". Now building data structure containing questions and answers map");	
		
		for(Posts answerWithCode: answersWithCode) {
			Integer questionOfAnswerId = answerWithCode.getParentId();
			Posts questionOfAnswer = questionsMap.get(questionOfAnswerId); 
			
			if(postsByFilterMap.containsKey(questionOfAnswer)) {
				List<Posts> answersOfQuestion = postsByFilterMap.get(questionOfAnswer);
				answersOfQuestion.add(answerWithCode);
			}else {
				List<Posts> answersOfQuestion = new ArrayList<>();
				answersOfQuestion.add(answerWithCode);
				postsByFilterMap.put(questionOfAnswer,answersOfQuestion);
			}
			
		}
		
		logger.info("Questions with answers containing code: "+postsByFilterMap.entrySet().size());
		/*	List<Posts> answers = getAnswersWithCodeForQuestion(question.getId());
			if(answers.size()>0) {
				postsByFilterMap.put(question, answers);
				questionsContainingAnwersWithCode++;
			}else {
				questionsContainingAnwersWithNoCode ++;
			}
			postsByFilterMap.put(question, answers);
			if(numQuestion%1000==0) {
				logger.info("Processing question "+numQuestion+ " of "+total);
			}
			numQuestion++;
			
		}*/
		
		
		
		//logger.info("Invalid posts (title or body is blank): "+disconsidered);
		
		return postsByFilterMap;
	}
	
	
	

	

	private List<Posts> getAnwersForParentsIds(String questionsIds) {
		Query q = em.createNativeQuery("select * from posts p where p.parentid in "+questionsIds+ " and p.body like '%<pre><code>%'", Posts.class);
		return (List<Posts>) q.getResultList();
	}



	private List<Posts> getAnswersWithCodeForQuestion(Integer id) {
		//Query q = em.createNativeQuery("select * from posts p where p.parentid = "+id+ " and p.body like '%<pre><code>%'", Posts.class);
		
		Query q = em.createNativeQuery("select * from posts p where p.parentid = "+id+ " and p.body like '%<pre><code>%'", Posts.class);
		
		//Query q = em.createNativeQuery("select * from posts p where p.parentid = "+id, Posts.class);
		
		return (List<Posts>) q.getResultList();
	}



	@Override
	public Map<Integer, Set<Integer>> getAllPostLinks() {
		
		Query q = em.createNativeQuery("select * from postlinks p where linktypeid = 3", PostLink.class);
		Set<PostLink> postsLinks = new HashSet(q.getResultList());
		
		Map<Integer, Set<Integer>> postsLinksMap = new HashMap<>();
		
		for(PostLink postLink: postsLinks) {
			//postsLinksMap.put(postLink.getPostId(), postLink.getRelatedPostId());
			
			Integer postId = postLink.getPostId();
			Integer relatedPostId = postLink.getRelatedPostId(); 
			
			if(postsLinksMap.containsKey(postId)) {
				Set<Integer> relatedPostIds = postsLinksMap.get(postId);
				relatedPostIds.add(relatedPostId);
			}else {
				Set<Integer>  relatedPostIds= new HashSet<Integer>();
				relatedPostIds.add(relatedPostId);
				postsLinksMap.put(postId,relatedPostIds);
			}
			
		}
		
		return postsLinksMap;
	}

	



	@Override
	public List<Posts> getSomePosts() {
		Query q = em.createNativeQuery("select * from posts p where p.tagssyn like '%java%' and p.tagssyn not like '%javascript%' limit 1000", Posts.class);
		return (List<Posts>) q.getResultList();
	}



	@Override
	public Set<ProcessedPosts> getProcessedQuestions(String tagFilter) {
		logger.info("getProcessedQuestions: "+tagFilter);
		
		String sql = " select * from processedposts p " 
				+ " WHERE  p.posttypeId = 1 ";
				
		sql += BotUtils.getQueryComplementByTag(tagFilter);
		
		sql += " and p.AnswerCount > 0 ";
		
		sql += " and p.score> 5 ";  //subset of java questions
		
		sql += " and trim(p.body)!=''";  //body nao deve ser vazio
		
		sql += " order by p.id ";
		
		Query q = em.createNativeQuery(sql, ProcessedPosts.class);
		
		Set<ProcessedPosts> questions = new HashSet<>(q.getResultList());
		
		return questions;
	}
	
	@Override
	public Set<ProcessedPosts> findClosedDuplicatedNonMastersByTag(String tagFilter) {
		String sql = " select * from processedposts  p " 
				+ " WHERE p.closeddate is not null";
		
		sql += BotUtils.getQueryComplementByTag(tagFilter);
		
		sql +=  " and trim(p.body)!='' "+
				" and p.id in" + 
				" ( select pl.postid" + 
				" from postlinks pl" + 
				" where pl.linktypeid = 3" + 
				" and pl.relatedpostid in " +
				"   (select id from posts p  " + 
				"   WHERE  p.posttypeId = 1"; 
		sql += BotUtils.getQueryComplementByTag(tagFilter);
		sql += 	"   and trim(p.body)!=''" + 
				"   and p.AnswerCount > 0)" + 
				" ) " + 
				" and p.AnswerCount > 0 "; 
		
		
		Query q = em.createNativeQuery(sql, ProcessedPosts.class);
		
		List<ProcessedPosts> posts = q.getResultList();
		Set<ProcessedPosts> postsSet = new HashSet<>();
				
		logger.info("Posts in findClosedDuplicatedNonMastersByTag step 1: "+posts.size());
		for(ProcessedPosts post:posts){
			if(StringUtils.isBlank(post.getTitle()) || StringUtils.isBlank(post.getBody())) {
				continue;
			}	
			postsSet.add(post);
		}
		logger.info("Posts in findClosedDuplicatedNonMastersByTag step 2: "+postsSet.size());
		
		return postsSet;
	}
	
	
	@Override
	public Set<Integer> findClosedDuplicatedNonMastersByTagStrict(String tagFilter) {
		String sql = " select id from processedposts p " 
				+ " WHERE p.closeddate is not null";
		
		sql += BotUtils.getQueryComplementByTagStrict(tagFilter);
		
		sql +=  " and trim(p.body)!='' "+
				" and p.id in" + 
				" ( select pl.postid" + 
				" from postlinks pl" + 
				" where pl.linktypeid = 3" + 
				" and pl.relatedpostid in " +
				"   (select id from posts p  " + 
				"   WHERE  p.posttypeId = 1"; 
		sql += BotUtils.getQueryComplementByTagStrict(tagFilter);
		sql += 	"   and trim(p.body)!=''" + 
				"   and p.AnswerCount > 0)" + 
				" ) " + 
				" and p.AnswerCount > 0 "; 
		
		Query q = em.createNativeQuery(sql);
		
		Set<Integer> postsSet = new HashSet<>(q.getResultList());
						
		logger.info("Posts in findClosedDuplicatedNonMastersByTagStrict: "+postsSet.size());
		
		return postsSet;
	}
	
	
	/*@Override
	public Set<Posts> findClosedDuplicatedNonMastersByTagExceptProcessedQuestions(String tagFilter) {
		String sql = " select * from posts p  " + 
					" where p.id in" +
					" (select id from posts  p  " + 
					"  WHERE   p.posttypeId = 1 " + 
					"  and p.closeddate is not null";  
		
		sql += BotUtils.getQueryComplementByTag(tagFilter);
		
	   		sql += "  and p.id in" + 
					"   (select distinct(pl.postid)" + 
					"   from postlinks pl, processedposts pq" + 
					"   where pl.relatedpostid = pq.id" + 
					"   and pl.linktypeid = 3)" +
					"	and pq.score>5 "+         
					" except" + 
					"   (select id from processedposts pp  " + 
					"   WHERE  pp.posttypeId = 1)" + 
					" )" + 
					" order by id ";
		
		
		Query q = em.createNativeQuery(sql, Posts.class);
		
		List<Posts> posts = q.getResultList();
		Set<Posts> realSet = new HashSet<>();
				
		//logger.info("Posts in findClosedDuplicatedNonMastersByTag before blank checking: "+posts.size());
		for(Posts post:posts){
			if(StringUtils.isBlank(post.getTitle()) || StringUtils.isBlank(post.getBody())) {
				continue;
			}	
			realSet.add(post);
		}
		//logger.info("Posts in findClosedDuplicatedNonMastersByTag after blank checking: "+realSet.size());
		posts = null;
		return realSet;
	}*/



	@Override
	public Set<Posts> getQuestionsByFilters(String tagFilter) {
		
		logger.info("getQuestionsByFilters: "+tagFilter);
		String sql = " select * from posts  p " 
				+ " WHERE   p.posttypeId = 1 ";
		
		sql += BotUtils.getQueryComplementByTag(tagFilter);
				
		sql += " order by p.id ";
		
		
		Query q = em.createNativeQuery(sql, Posts.class);
		
		List<Posts> posts = q.getResultList();
		Set<Posts> postsSet = new HashSet<>();
				
		logger.info("Posts in getQuestionsByFilters step 1: "+posts.size());
		for(Posts post:posts){
			if(StringUtils.isBlank(post.getTitle()) || StringUtils.isBlank(post.getBody())) {
				continue;
			}	
			postsSet.add(post);
		}
		logger.info("Posts in getQuestionsByFilters step 2: "+postsSet.size());
		posts = null;
		return postsSet;
	}
	
	
	
	
	
	
}
