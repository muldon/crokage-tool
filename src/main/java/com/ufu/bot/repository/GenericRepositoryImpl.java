package com.ufu.bot.repository;

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
	public List<Post> findRankedList(Integer externalQuestionId, boolean isInternalSurveyUser) {
			
		String sql = "select p.* " + 
					"    from postsmin p, rank r" + 
					"    where p.id = r.postid " + 
					"    and r.externalquestionid = " + externalQuestionId+
					"    and r.internalevaluation = " + isInternalSurveyUser+
					"    order by r.rankorder";
		
		Query q = em.createNativeQuery(sql, Post.class);
		return (List<Post>) q.getResultList();
		
		
	}
	
	
	
	
	
	
}
