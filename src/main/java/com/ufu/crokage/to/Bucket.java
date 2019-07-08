package com.ufu.crokage.to;

import java.util.Map;
import java.util.Set;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.ufu.crokage.tfidf.Document;

@XmlRootElement
public class Bucket {
	protected static final long serialVersionUID = -11118191211815641L;
	@Id
	protected Integer id;
	
	protected String body;
	
	protected String processedTitle;
	
	protected String title;
	
	protected String processedBody;
	
	protected String code;
	
	protected String processedCode;
	
	protected Integer upVotesScore;
	
	protected Integer userReputation;
	
	protected Integer commentCount;
	
	protected Integer viewCount;
	
	protected Boolean acceptedAnswer;
	
	protected Integer acceptedAnswerId;
	
	protected String acceptedAnswerBody;
	
	protected Integer parentId;
	
	protected Integer parentUpVotesScore;
	
	protected Double calculatedScore;
	
	protected String parentProcessedTitle;
	
	protected String parentProcessedTitleLemma;
	
	protected String parentProcessedBody;
	
	protected String parentProcessedBodyLemma;
	
	protected String parentProcessedCode;
	
    private String processedBodyLemma;
	
    private String processedTitleLemma;
	
	protected Double titleScore;
	
	protected Document document;
	
	protected Double tfIdfCosineSimScore;
	
	protected Double bm25Score;
	
	protected Double simPair;
	
	protected String acceptedOrMostUpvotedAnswerOfParentProcessedBody;
	
	protected String acceptedOrMostUpvotedAnswerOfParentProcessedCode;
	
	protected String threadContent;
	
	protected Set<Bucket> answers;
	
	protected String topics;
	
	protected String hotTopics;
	
	protected Map<Integer,Double> hotTopicsIdValueMap;
	
	protected Map<Integer,Double> topicsIdValueMap;
	
	protected Set<Integer> topScoredAnswersSet;
		
	private String topScoredAnswers;
	
	protected Double topicScore;
	
	protected Integer intersectionSize;
	
	protected Integer tagId;
	
	protected Set<String> methods;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

	

	public Integer getUpVotesScore() {
		return upVotesScore;
	}

	public void setUpVotesScore(Integer upVotesScore) {
		this.upVotesScore = upVotesScore;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bucket other = (Bucket) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	

	public Bucket() {
		super();
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	


	@Override
	public String toString() {
		return "Bucket [id=" + id + "]";
	}

	public Integer getUserReputation() {
		return userReputation;
	}

	public void setUserReputation(Integer userReputation) {
		this.userReputation = userReputation;
	}

	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	public Integer getViewCount() {
		return viewCount;
	}

	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}

	public Boolean getAcceptedAnswer() {
		return acceptedAnswer;
	}

	public void setAcceptedAnswer(Boolean acceptedAnswer) {
		this.acceptedAnswer = acceptedAnswer;
	}

	public Integer getParentUpVotesScore() {
		return parentUpVotesScore;
	}

	public void setParentUpVotesScore(Integer parentUpVotesScore) {
		this.parentUpVotesScore = parentUpVotesScore;
	}

	public Double getCalculatedScore() {
		return calculatedScore;
	}

	public void setCalculatedScore(Double calculatedScore) {
		this.calculatedScore = calculatedScore;
	}

	public String getProcessedBody() {
		return processedBody;
	}

	public void setProcessedBody(String processedBody) {
		this.processedBody = processedBody;
	}

	public String getProcessedCode() {
		return processedCode;
	}

	public void setProcessedCode(String processedCode) {
		this.processedCode = processedCode;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getAcceptedAnswerId() {
		return acceptedAnswerId;
	}

	public void setAcceptedAnswerId(Integer acceptedAnswerId) {
		this.acceptedAnswerId = acceptedAnswerId;
	}

	public String getProcessedTitle() {
		return processedTitle;
	}

	public void setProcessedTitle(String processedTitle) {
		this.processedTitle = processedTitle;
	}

	public String getParentProcessedTitle() {
		return parentProcessedTitle;
	}

	public void setParentProcessedTitle(String parentProcessedTitle) {
		this.parentProcessedTitle = parentProcessedTitle;
	}

	public String getParentProcessedBody() {
		return parentProcessedBody;
	}

	public void setParentProcessedBody(String parentProcessedBody) {
		this.parentProcessedBody = parentProcessedBody;
	}

	public String getParentProcessedCode() {
		return parentProcessedCode;
	}

	public void setParentProcessedCode(String parentProcessedCode) {
		this.parentProcessedCode = parentProcessedCode;
	}

	public Double getTitleScore() {
		return titleScore;
	}

	public void setTitleScore(Double titleScore) {
		this.titleScore = titleScore;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Double getTfIdfCosineSimScore() {
		return tfIdfCosineSimScore;
	}

	public void setTfIdfCosineSimScore(Double tfIdfCosineSimScore) {
		this.tfIdfCosineSimScore = tfIdfCosineSimScore;
	}

	public Double getSimPair() {
		return simPair;
	}

	public void setSimPair(Double simPair) {
		this.simPair = simPair;
	}

	public String getAcceptedOrMostUpvotedAnswerOfParentProcessedBody() {
		return acceptedOrMostUpvotedAnswerOfParentProcessedBody;
	}

	public void setAcceptedOrMostUpvotedAnswerOfParentProcessedBody(
			String acceptedOrMostUpvotedAnswerOfParentProcessedBody) {
		this.acceptedOrMostUpvotedAnswerOfParentProcessedBody = acceptedOrMostUpvotedAnswerOfParentProcessedBody;
	}

	public String getAcceptedOrMostUpvotedAnswerOfParentProcessedCode() {
		return acceptedOrMostUpvotedAnswerOfParentProcessedCode;
	}

	public void setAcceptedOrMostUpvotedAnswerOfParentProcessedCode(
			String acceptedOrMostUpvotedAnswerOfParentProcessedCode) {
		this.acceptedOrMostUpvotedAnswerOfParentProcessedCode = acceptedOrMostUpvotedAnswerOfParentProcessedCode;
	}

	public String getAcceptedAnswerBody() {
		return acceptedAnswerBody;
	}

	public void setAcceptedAnswerBody(String acceptedAnswerBody) {
		this.acceptedAnswerBody = acceptedAnswerBody;
	}

	

	public Bucket(Integer id, String processedBody) {
		super();
		this.id = id;
		this.processedBody = processedBody;
	}

	public String getThreadContent() {
		return threadContent;
	}

	public void setThreadContent(String threadContent) {
		this.threadContent = threadContent;
	}

	public Double getBm25Score() {
		return bm25Score;
	}

	public void setBm25Score(Double bm25Score) {
		this.bm25Score = bm25Score;
	}

	public String getProcessedBodyLemma() {
		return processedBodyLemma;
	}

	public void setProcessedBodyLemma(String processedBodyLemma) {
		this.processedBodyLemma = processedBodyLemma;
	}

	public String getProcessedTitleLemma() {
		return processedTitleLemma;
	}

	public void setProcessedTitleLemma(String processedTitleLemma) {
		this.processedTitleLemma = processedTitleLemma;
	}

	public Set<Bucket> getAnswers() {
		return answers;
	}

	public void setAnswers(Set<Bucket> answers) {
		this.answers = answers;
	}

	public String getParentProcessedBodyLemma() {
		return parentProcessedBodyLemma;
	}

	public void setParentProcessedBodyLemma(String parentProcessedBodyLemma) {
		this.parentProcessedBodyLemma = parentProcessedBodyLemma;
	}

	public String getParentProcessedTitleLemma() {
		return parentProcessedTitleLemma;
	}

	public void setParentProcessedTitleLemma(String parentProcessedTitleLemma) {
		this.parentProcessedTitleLemma = parentProcessedTitleLemma;
	}

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public String getHotTopics() {
		return hotTopics;
	}

	public void setHotTopics(String hotTopics) {
		this.hotTopics = hotTopics;
	}

	public Map<Integer, Double> getHotTopicsIdValueMap() {
		return hotTopicsIdValueMap;
	}

	public void setHotTopicsIdValueMap(Map<Integer, Double> hotTopicsIdValueMap) {
		this.hotTopicsIdValueMap = hotTopicsIdValueMap;
	}

	

	public String getTopScoredAnswers() {
		return topScoredAnswers;
	}

	public void setTopScoredAnswers(String topScoredAnswers) {
		this.topScoredAnswers = topScoredAnswers;
	}

	public Set<Integer> getTopScoredAnswersSet() {
		return topScoredAnswersSet;
	}

	public void setTopScoredAnswersSet(Set<Integer> topScoredAnswersSet) {
		this.topScoredAnswersSet = topScoredAnswersSet;
	}

	public Double getTopicScore() {
		return topicScore;
	}

	public void setTopicScore(Double topicScore) {
		this.topicScore = topicScore;
	}

	public Map<Integer, Double> getTopicsIdValueMap() {
		return topicsIdValueMap;
	}

	public void setTopicsIdValueMap(Map<Integer, Double> topicsIdValueMap) {
		this.topicsIdValueMap = topicsIdValueMap;
	}

	public Integer getIntersectionSize() {
		return intersectionSize;
	}

	public void setIntersectionSize(Integer intersectionSize) {
		this.intersectionSize = intersectionSize;
	}

	public Set<String> getMethods() {
		return methods;
	}

	public void setMethods(Set<String> methods) {
		this.methods = methods;
	}

	public Integer getTagId() {
		return tagId;
	}

	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}

	
	public enum TagEnum {
		JAVA (1), 
	    PHP (2),
	    PYTHON (3);
	  
	    private final Integer id;
		
		TagEnum(Integer id){
			this.id = id;
		}
		
		
		
	    public static TagEnum getTagEnum(Integer id){
	    	switch(id){
	    		case(1): return JAVA;
	    		case(2): return PHP;
	    		case(3): return PYTHON;
	    	  	}
	    	return null;
	    }
	
		public Integer getId() {
			return id;
		}
	
		
	
	}


	
    
}