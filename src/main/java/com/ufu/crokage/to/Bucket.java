package com.ufu.crokage.to;

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
	
	protected String parentProcessedBody;
	
	protected String parentProcessedCode;
	
	protected Double titleScore;
	
	protected Document document;
	
	protected Double tfIdfCosineSimScore;
	
	protected Double bm25Score;
	
	protected Double simPair;
	
	protected String acceptedOrMostUpvotedAnswerOfParentProcessedBody;
	
	protected String acceptedOrMostUpvotedAnswerOfParentProcessedCode;
	
	protected String threadContent;
	
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
		return "Bucket [id=" + id + ", body=" + body + ", code=" + code + ", calculatedScore=" + calculatedScore + "]";
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

 
	

	
	

	
	
    
}