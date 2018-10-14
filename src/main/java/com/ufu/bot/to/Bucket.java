package com.ufu.bot.to;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "postsmin")
public class Bucket {
	private static final long serialVersionUID = -11118191211815641L;
	@Id
	private Integer id;
	
	private String body;
	
	private String code;
	
	private Integer score;
	
	private Integer userReputation;
	
	private Integer commentCount;
	
	private Integer viewCount;
	
	private Boolean acceptedAnswer;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
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
		return "Bucket [id=" + id + ", body=" + body + ", code=" + code + ", score=" + score + ", userReputation="
				+ userReputation + ", commentCount=" + commentCount + ", viewCount=" + viewCount + ", acceptedAnswer="
				+ acceptedAnswer + "]";
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
	
	

	
	

	
	
    
}