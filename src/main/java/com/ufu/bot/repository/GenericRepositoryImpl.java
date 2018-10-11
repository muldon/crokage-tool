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

import com.ufu.bot.to.Evaluation;
import com.ufu.bot.to.ExternalQuestion;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.PostLink;
import com.ufu.bot.util.BotUtils;

@Repository
public class GenericRepositoryImpl implements GenericRepository {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;



	@Override
	public List<Post> findAllQuestions() {
		Query q = em.createNativeQuery("select * from postsmin p where p.posttypeid =1 order by id desc", Post.class);
		return (List<Post>) q.getResultList();
	}

		

	

	private List<Post> getAnwersForParentsIds(String questionsIds) {
		Query q = em.createNativeQuery("select * from postsmin p where p.parentid in "+questionsIds+ " and p.body like '%<pre><code>%'", Post.class);
		return (List<Post>) q.getResultList();
	}



	private List<Post> getAnswersWithCodeForQuestion(Integer id) {
		//Query q = em.createNativeQuery("select * from posts p where p.parentid = "+id+ " and p.body like '%<pre><code>%'", Post.class);
		
		Query q = em.createNativeQuery("select * from postsmin p where p.parentid = "+id+ " and p.body like '%<pre><code>%'", Post.class);
		
		//Query q = em.createNativeQuery("select * from posts p where p.parentid = "+id, Post.class);
		
		return (List<Post>) q.getResultList();
	}



	@Override
	public Map<Integer, Set<Integer>> getAllPostLinks() {
		
		Query q = em.createNativeQuery("select * from postlinksmin p where linktypeid = 3", PostLink.class);
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
	public List<Post> getSomePosts() {
		Query q = em.createNativeQuery("select * from postsmin p where p.tagssyn like '%java%' and p.tagssyn not like '%javascript%' limit 1000", Post.class);
		return (List<Post>) q.getResultList();
	}



	
	


	@Override
	public Set<Post> getPostsByFilters(String tagFilter) {
		
		logger.info("getPostsByFilters: "+tagFilter);
		String sql = " select * from postsmin  p " 
				+ " WHERE   1=1 ";
		
		sql += BotUtils.getQueryComplementByTag(tagFilter);
				
		sql += " order by p.id ";
		
		
		Query q = em.createNativeQuery(sql, Post.class);
		
		List<Post> postsTmp = q.getResultList();
		Set<Post> postsSet = new HashSet<>();
				
		logger.info("Post in getPostsByFilters step 1: "+postsTmp.size());
		for(Post post:postsTmp){
			if(StringUtils.isBlank(post.getTitle()) || StringUtils.isBlank(post.getBody())) {
				continue;
			}	
			postsSet.add(post);
		}
		logger.info("Post in getPostsByFilters step 2: "+postsSet.size());
		postsTmp = null;
		return postsSet;
	}



	@Override
	public Set<Integer> findRelatedQuestionsIds(Set<Integer> allQuestionsIds,Integer linkTypeId) {
		Set<Integer> relatedQuestionsIds = new HashSet<>();
		
		String inCommand = "(";
		for(Integer questionId:allQuestionsIds) {
			inCommand+= questionId+ ",";
		}
		inCommand+= "#";
		inCommand = inCommand.replaceAll(",#", ")");
		
		//logger.info("In command: "+inCommand);
		
		String query = "select * from postlinksmin where linktypeid = "+linkTypeId+" and postid in " + inCommand+  
				" union " + 
				" select * from postlinksmin where linktypeid = "+linkTypeId+" and relatedpostid in " + inCommand; 
		
		logger.info(query);
		
		Query q = em.createNativeQuery(query, PostLink.class);
		Set<PostLink> postsLinks = new HashSet(q.getResultList());
		for(PostLink postLink:postsLinks) {
			relatedQuestionsIds.add(postLink.getPostId());
			relatedQuestionsIds.add(postLink.getRelatedPostId());
		}
		
		return relatedQuestionsIds;
	}





	@Override
	public List<Post> findRankedList(Integer externalQuestionId, int userId, int phaseNum) {
			
		String sql = "select p.*  " + 
				"  from postsmin p, relatedpost rp, rank r  " + 
				"  where p.id = rp.postid  " + 
				"  and rp.id = r.relatedpostid	 " + 
				"  and rp.externalquestionid =  " + externalQuestionId+
				"  and r.phase =  " + phaseNum+
				"  and r.id not in (" + 
				"  select r2.id" + 
				"	  from relatedpost rp2, rank r2, evaluation e2" + 
				"      where rp2.id = r2.relatedpostid	 " + 
				"      and r2.id = e2.rankid" + 
				"	  and rp2.externalquestionid = " + externalQuestionId+
				"     and r2.phase =  " + phaseNum+
				"	  and e2.surveyuserid = " +userId+ 
				"  )" + 
				"  order by r.rankorder";
		
		Query q = em.createNativeQuery(sql, Post.class);
		return (List<Post>) q.getResultList();
		
		
	}



	@Override
	public List<Post> findRankedPosts(Integer externalQuestionId, Integer userId, int phaseNum) {
		
		String sql = "select p.*  " + 
				"  from postsmin p, relatedpost rp, rank r  " + 
				"  where p.id = rp.postid  " + 
				"  and rp.id = r.relatedpostid	 " + 
				"  and rp.externalquestionid =  " + externalQuestionId+
				"  and r.phase =  " + phaseNum+
				"  order by r.rankorder";
		
		Query q = em.createNativeQuery(sql, Post.class);
		return (List<Post>) q.getResultList();
	}
	

	@Override
	public List<Post> findRankedEvaluatedPosts(Integer externalQuestionId, Integer userId, int phaseNum) {
		String sql = "  select p.*" + 
				"	  from postsmin p,relatedpost rp2, rank r2, evaluation e2" + 
				"     where rp2.id = r2.relatedpostid	 " + 
				"	  and p.id = rp2.postid	" +	
				"     and r2.id = e2.rankid" + 
				"	  and rp2.externalquestionid = " + externalQuestionId+
				"     and r2.phase =  " + phaseNum+
				"	  and e2.surveyuserid = " +userId;
				
		
		Query q = em.createNativeQuery(sql, Post.class);
		return (List<Post>) q.getResultList();
	}
	



	@Override
	public List<ExternalQuestion> findNextExternalQuestionInternalSurveyUser(Integer userId, Integer phaseNumber) {
		String sql = "select * " + 
				" from externalquestion eq" 
				+ " where eq.id not in "  
				+ " (select rp.externalquestionid"  
				+ "  from evaluation e, rank r, relatedpost rp"  
				+ "  where e.rankid = r.id "
				+ " and r.relatedpostid = rp.id "
				+ " and e.surveyuserid = "+userId  
				+ " ) order by externalid  "
				+ " limit 2";  
				
		/*if(bringTwoQuestions) {
			sql+= " limit 2";
		}*/
				
	
		Query q = em.createNativeQuery(sql, ExternalQuestion.class);
		return (List<ExternalQuestion>) q.getResultList();
		
	}





	@Override
	public List<Evaluation> getEvaluationByPhaseAndRelatedPost(Integer externalQuestionId, Integer phaseNumber) {
		String sql = " select rp.postid, e.surveyuserid, e.likertscale" + 
				" from relatedpost rp, rank r, evaluation e" + 
				" where rp.id=r.relatedpostid" + 
				" and e.rankid = r.id" + 
				" and rp.externalquestionid = " + externalQuestionId+
				" and r.phase=" + phaseNumber+ 
				" order by rp.postid, e.surveyuserid";  
			
		Query q = em.createNativeQuery(sql);
		
		List<Object[]> rows = q.getResultList();
		List<Evaluation> result = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			Evaluation evaluation = new Evaluation();
			evaluation.setPostId((Integer) row[0]);
			evaluation.setSurveyUserId((Integer) row[1]);
			evaluation.setLikertScale((Integer) row[2]);
			result.add(evaluation);			
		}
		
		return result;
	}





	@Override
	public List<ExternalQuestion> getExternalQuestionsByPhase(Integer phaseNumber) {
		String sql = " select distinct(e.*)" + 
				" from externalquestion e, relatedpost rp, rank r" + 
				" where e.id = rp.externalquestionid " + 
				" and r.relatedpostid = rp.id" + 
				" and r.phase = " +phaseNumber+ 
				" order by e.id ";  
			
		Query q = em.createNativeQuery(sql, ExternalQuestion.class);
		return (List<ExternalQuestion>) q.getResultList();
	}





	@Override
	public List<Post> getAnswersWithCode(String startDate) {
		
		String sql = " select * "
				+ " from postsmin po"  
				+ " where po.body like '%<pre><code>%' "
				+ " and po.posttypeid = 2 ";
		
		if(!StringUtils.isBlank(startDate)) {
			sql += " and po.creationdate > '" + startDate + "'";
		}
			
		Query q = em.createNativeQuery(sql, Post.class);
		List<Post> posts = (List<Post>) q.getResultList();
		logger.info("getAnswersWithCode: "+posts.size()+ " posts retrieved");
		return posts;
	}





	@Override
	public List<Post> getPostsByIds(List<Integer> soAnswerIds) {
		String idsIn = " ";
		for(Integer soId: soAnswerIds) {
			idsIn+= soId+ ",";
		}
		idsIn+= "#end";
		idsIn = idsIn.replace(",#end", "");
		
		String sql = " select * "
				+ " from postsmin po"  
				+ " where po.id in ("+idsIn+")";
		
			
		Query q = em.createNativeQuery(sql, Post.class);
		List<Post> posts = (List<Post>) q.getResultList();
		//logger.info("getPostsByIds: "+posts.size());
		return posts;
	}










	
	
	
}
