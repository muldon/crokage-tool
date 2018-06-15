package com.ufu.bot.to;

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
@Table(name = "relatedpost")
public class RelatedPost {
	private static final long serialVersionUID = -151652190111815641L;
	@Id
    @SequenceGenerator(name="relatedpost_id_seq", sequenceName="relatedpost_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="relatedpost_id_seq")
	private Integer id;
	
	@Column(name="postid")
	private Integer postId;
	
	@Column(name="externalquestionid")
	private Integer externalQuestionId;
	
	
		
	
	public RelatedPost() {
	}

	


	public RelatedPost(Integer postId, Integer externalQuestionId) {
		super();
		this.postId = postId;
		this.externalQuestionId = externalQuestionId;
	}




	public Integer getId() {
		return id;
	}




	public void setId(Integer id) {
		this.id = id;
	}




	public Integer getPostId() {
		return postId;
	}




	public void setPostId(Integer postId) {
		this.postId = postId;
	}




	public Integer getExternalQuestionId() {
		return externalQuestionId;
	}




	public void setExternalQuestionId(Integer externalQuestionId) {
		this.externalQuestionId = externalQuestionId;
	}




	public static long getSerialversionuid() {
		return serialVersionUID;
	}




	@Override
	public String toString() {
		return "RelatedPost [id=" + id + ", postId=" + postId + ", externalQuestionId=" + externalQuestionId + "]";
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
		RelatedPost other = (RelatedPost) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	


	
	
    
}