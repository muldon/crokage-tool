package com.ufu.bot.to;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "rank")
public class Rank {
	@Id
    @SequenceGenerator(name="rank_id_seq", sequenceName="rank_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="rank_id_seq")
	private Integer id;
		
	@Column(name="externalquestionid")
	private Integer externalQuestionId;

	@Column(name="postid")	
	private Integer postId;
	
	@Column(name="rankorder")	
	private Integer rankOrder;
	
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
		Rank other = (Rank) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	public Integer getRankOrder() {
		return rankOrder;
	}


	public void setRankOrder(Integer rankOrder) {
		this.rankOrder = rankOrder;
	}


	public Boolean getInternalEvaluation() {
		return internalEvaluation;
	}


	public void setInternalEvaluation(Boolean internalEvaluation) {
		this.internalEvaluation = internalEvaluation;
	}


	@Override
	public String toString() {
		return "Rank [id=" + id + ", externalQuestionId=" + externalQuestionId + ", postId=" + postId + ", rankOrder="
				+ rankOrder + ", internalEvaluation=" + internalEvaluation + "]";
	}


	public Rank(Integer externalQuestionId, Integer postId, Integer rankOrder, Boolean internalEvaluation) {
		super();
		this.externalQuestionId = externalQuestionId;
		this.postId = postId;
		this.rankOrder = rankOrder;
		this.internalEvaluation = internalEvaluation;
	}


	public Rank() {
		super();
	}


	
	
	
	

	
	
	
	
	
}
