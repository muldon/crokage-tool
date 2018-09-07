package com.ufu.bot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.ufu.bot.to.Bucket;
import com.ufu.bot.to.Evaluation;
import com.ufu.bot.to.Experiment;
import com.ufu.bot.to.ExternalQuestion;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.Rank;
import com.ufu.bot.to.RelatedPost;
import com.ufu.bot.to.Result;
import com.ufu.bot.to.SurveyUser;
import com.ufu.bot.util.AbstractService;



@Service
@Transactional
public class PitBotService extends AbstractService{
		
	
	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	
	public PitBotService() {
		
	}
	
	
	


	@Transactional(readOnly = true)
	public List<Post> getSomePosts() {
		return genericRepository.getSomePosts();
	}
	
	/*@Transactional(readOnly = true)
	public Map<Post, List<Post>> getQuestionsByFilters(String tagFilter,Integer limit, String maxCreationDate) {
		return genericRepository.getQuestionsByFilters(tagFilter,limit,maxCreationDate);
	}*/
	
	

	
	

	@Transactional(readOnly = true)
	public Map<Integer, Set<Integer>> getAllPostLinks() {
		return genericRepository.getAllPostLinks();
	}


	/*
	public void stemStop(List<ProcessedPostOld> somePosts) throws Exception {
		for (ProcessedPostOld processedPostOld : somePosts) {
			String body = processedPostOld.getBody();
			
			if(processedPostOld.getPostTypeId().equals(1)){ //pergunta
				String title = processedPostOld.getTitle();
				
				String[] titleContent = botUtils.separateWordsCodePerformStemmingStopWords(title,"title");
				processedPostOld.setTitle(titleContent[0] + " "+ titleContent[1]);
				
				String tagssyn = processedPostOld.getTags().replaceAll("<","");
				tagssyn = tagssyn.replaceAll(">"," ");
				tagssyn = botUtils.tagMastering(tagssyn);												
				processedPostOld.setTagsSyn(tagssyn);		
			}
			//respostas nao possuem tag nem title				
			
			String[] bodyContent = botUtils.separateWordsCodePerformStemmingStopWords(body,"body");
			//processedPost.setBody(bodyContent[0] + " "+ bodyContent[1]+ " "+botUtils.formatCode(bodyContent[2]));
			processedPostOld.setBody(bodyContent[0] + " "+ bodyContent[1]);
			if (!StringUtils.isBlank(bodyContent[2])) {
				processedPostOld.setCode(bodyContent[2]);
			}
				
				
			
			//question.setTags(tags);														
			processedPostsRepository.save(processedPostOld);
					
		}
		
	}
*/


	

	public void saveExperiment(Experiment experiment) {
		experimentRepository.save(experiment);
		
	}




	@Transactional(readOnly = true)
	public Set<Post> getPostsByFilters(String tagFilter) {
		return genericRepository.getPostsByFilters(tagFilter);
	}

	







	

	@Transactional(readOnly = true)
	public Set<Integer> recoverRelatedQuestionsIds(Set<Integer> allQuestionsIds, Integer linkTypeId) {
		return genericRepository.findRelatedQuestionsIds(allQuestionsIds,linkTypeId);
	}

	@Transactional(readOnly = true)
	public Set<Post> getAllPosts() {
		Set<Post> set = Sets.newHashSet(postsRepository.findAll());
		return set;
		
	}





	public void saveExternalQuestion(ExternalQuestion externalQuestion) {
		externalQuestionRepository.save(externalQuestion);
		
	}




	@Transactional(readOnly = true)
	public List<ExternalQuestion> getAllExternalQuestionsAnswerBot() {
		return externalQuestionRepository.findAllExternalQuestionsAnswerBot();
	}





	public void saveRelatedPost(RelatedPost relatedPost) {
		relatedPostRepository.save(relatedPost);
		
	}




	@Transactional(readOnly = true)
	public List<Post> getPostsByExternalQuestionId(Integer externalQuestionId) {
		return relatedPostRepository.findRelatedPosts(externalQuestionId);
	}





	public void saveSurveyUser(SurveyUser surveyUser1) {
		surveyUserRepository.save(surveyUser1);
		
	}




	@Transactional(readOnly = true)
	public SurveyUser getSurveyUserByLogin(String login) {
		return surveyUserRepository.findByLogin(login);
	}





	public void saveEvaluation(Evaluation evaluation) {
		evaluationRepository.save(evaluation);
		
	}





	public void saveResult(Result result) {
		resultRepository.save(result);
		
	}

	public ExternalQuestion saveExternalQuestionAndRelatedIds(ExternalQuestion externalQuestion, Map<Integer, Post> answerPostsCache) {
		Set<Integer> answerPostsIds = answerPostsCache.keySet();
		
		/*
		 * Save the external question if it is not already saved
		 */
		ExternalQuestion externalQuestionInDb = externalQuestionRepository.findByFileReferenceId(externalQuestion.getFileReferenceId());
		if(externalQuestionInDb==null) {
			logger.info("Question not found, saving new ");
			externalQuestion= externalQuestionRepository.save(externalQuestion);
		}else {
			logger.info("Question found ok");
			externalQuestion = externalQuestionInDb;
		}
		
		
		/*
		 * save all related posts (only answers)
		 */
		int newRelatedPosts = 0;
		int alreadyPresentInDb = 0;
		for(Integer answerPostId: answerPostsIds) {
			RelatedPost relatedPostInDb = relatedPostRepository.findByExternalQuestionIdAndPostId(externalQuestion.getId(), answerPostId);
			if(relatedPostInDb==null) {
				newRelatedPosts++;
				RelatedPost relatedPost = new RelatedPost(answerPostId,externalQuestion.getId(),answerPostsCache.get(answerPostId).getRelationTypeId());
				relatedPostRepository.save(relatedPost);
			}else {
				alreadyPresentInDb++;
			}
		}
		logger.info("New relatedposts found for externalQuestion "+externalQuestion.getId()+ " :"+newRelatedPosts);
		logger.info("Already present relatedposts for externalQuestion "+externalQuestion.getId()+ " :"+alreadyPresentInDb);
		return externalQuestion;
	}


	
	





	public int getCountExcludedPosts() {
		return countExcludedPosts;
	}





	public void setCountExcludedPosts(int countExcludedPosts) {
		this.countExcludedPosts = countExcludedPosts;
	}





	public int getCountPostIsAnAnswer() {
		return countPostIsAnAnswer;
	}





	public void setCountPostIsAnAnswer(int countPostIsAnAnswer) {
		this.countPostIsAnAnswer = countPostIsAnAnswer;
	}





	public void saveRanks(ExternalQuestion externalQuestion, List<Bucket> rankedList, Boolean internalEvaluation, Integer phaseNumber) {
		int order=1;
		for(Bucket bucket: rankedList) {
			RelatedPost relatedPost = relatedPostRepository.findByExternalQuestionIdAndPostId(externalQuestion.getId(),bucket.getPostId());
			Rank rank=new Rank(relatedPost.getId(),order,internalEvaluation,phaseNumber);
			rankRepository.save(rank);
			order++;
		}
		
	}




	@Transactional(readOnly = true)
	public List<Evaluation> getEvaluationByPhaseAndRelatedPost(Integer externalQuestionId, Integer phaseNumber) {
		List evaluationsObjects = genericRepository.getEvaluationByPhaseAndRelatedPost(externalQuestionId,phaseNumber);
		return (List<Evaluation>)evaluationsObjects;
		
		
		/*List<Evaluation> evaluations = new ArrayList<>();
		for(Object evalObj: evaluationsObjects) {
			Evaluation evaluation = new Evaluation();
			evaluation.setPostId(evalObj.);
		}
		*/
		
		//return evaluations;
	}


	@Transactional(readOnly = true)
	public List<ExternalQuestion> getExternalQuestionsByPhase(Integer phaseNumber) {
		return genericRepository.getExternalQuestionsByPhase(phaseNumber);
	}

	
	@Transactional(readOnly = true)
	public RelatedPost getRelatedPostByExternalQuestionIdAndPostId(Integer externalQuestionId, Integer postId) {
		return relatedPostRepository.findByExternalQuestionIdAndPostId(externalQuestionId,postId);
	}




	@Transactional(readOnly = true)
	public Rank getRankByRelatedPostIdAndPhase(Integer relatedPostId, Integer phase) {
		return rankRepository.findByRelatedPostIdAndPhase(relatedPostId,phase);
	}




	@Transactional(readOnly = true)
	public Rank getRankByRelatedPostIdAndInternalEvaluation(Integer relatedPostId, boolean internalEvaluation) {
		return rankRepository.findByRelatedPostIdAndInternalEvaluation(relatedPostId,internalEvaluation);
	}




	@Transactional(readOnly = true)
	public List<RelatedPost> getRelatedPostsByExternalQuestionId(Integer externalQuestionId) {
		return relatedPostRepository.findByExternalQuestionId(externalQuestionId);
	}




	@Transactional(readOnly = true)
	public List<Evaluation> getEvaluationsByPhaseAndUser(int phase, int user) {
		return evaluationRepository.findByPhaseUser(phase, user);
	}




	@Transactional(readOnly = true)
	public ExternalQuestion getExternalQuestionById(Integer externalQuestionId) {
		return externalQuestionRepository.findOne(externalQuestionId);
	}




	@Transactional(readOnly = true)
	public List<Rank> getRanksByPhase(int phase) {
		return rankRepository.findByPhase(phase);
	}


	@Transactional(readOnly = true)
	public List<Evaluation> getEvaluationsByRankId(Integer rankId) {
		return evaluationRepository.findByRankId(rankId);
	}





	public List<RelatedPost> getRelatedPostsByExternalQuestionIdAndPhase(Integer externalQuestionId, int phase) {
		return relatedPostRepository.findRelatedPostsByExternalQuestionIdAndPhase(externalQuestionId,phase);
	}
	
	
	
}
