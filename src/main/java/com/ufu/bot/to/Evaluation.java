package com.ufu.bot.to;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "evaluation")
public class Evaluation {
	@Id
    @SequenceGenerator(name="evaluation_id_seq", sequenceName="evaluation_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="evaluation_id_seq")
	private Integer id;
	
	
	@Column(name="externalquestionid")
	private Integer externalQuestionId;

	@Column(name="postid")	
	private Integer postId;
	
	@Column(name="surveyuserid")	
	private Integer surveyUserId;
	
	@Column(name="likertscale")	
	private Integer likertScale;
		
	@Column(name="ratingdate")
	private Timestamp ratingDate;
	
	
	@Column(name="internalevaluation")	
	private Boolean internalEvaluation;


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getExternalQuestionId() {
		return externalQuestionId;
	}


	public void setExternalQuestionId(Integer externalQuestionId) {
		this.externalQuestionId = externalQuestionId;
	}


	public Integer getPostId() {
		return postId;
	}


	public void setPostId(Integer postId) {
		this.postId = postId;
	}


	public Integer getSurveyUserId() {
		return surveyUserId;
	}


	public void setSurveyUserId(Integer surveyUserId) {
		this.surveyUserId = surveyUserId;
	}


	public Integer getLikertScale() {
		return likertScale;
	}


	public void setLikertScale(Integer likertScale) {
		this.likertScale = likertScale;
	}


	public Timestamp getRatingDate() {
		return ratingDate;
	}


	public void setRatingDate(Timestamp ratingDate) {
		this.ratingDate = ratingDate;
	}


	public Boolean getInternalEvaluation() {
		return internalEvaluation;
	}


	public void setInternalEvaluation(Boolean internalEvaluation) {
		this.internalEvaluation = internalEvaluation;
	}


	@Override
	public String toString() {
		return "Evaluation [id=" + id + ", externalQuestionId=" + externalQuestionId + ", postId=" + postId
				+ ", surveyUserId=" + surveyUserId + ", likertScale=" + likertScale + ", ratingDate=" + ratingDate
				+ ", internalEvaluation=" + internalEvaluation + "]";
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
		Evaluation other = (Evaluation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	public Evaluation(Integer externalQuestionId, Integer postId, Integer surveyUserId, Integer likertScale,
			Timestamp ratingDate, Boolean internalEvaluation) {
		super();
		this.externalQuestionId = externalQuestionId;
		this.postId = postId;
		this.surveyUserId = surveyUserId;
		this.likertScale = likertScale;
		this.ratingDate = ratingDate;
		this.internalEvaluation = internalEvaluation;
	}


	public Evaluation() {
		super();
	}
	
	

	
	
	
	
	
}
