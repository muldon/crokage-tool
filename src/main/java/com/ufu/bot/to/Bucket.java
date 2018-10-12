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
	
	@Column(name="processedtitle")
    private String processedTitle;
	
	@Transient
	private Integer score;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProcessedTitle() {
		return processedTitle;
	}

	public void setProcessedTitle(String processedTitle) {
		this.processedTitle = processedTitle;
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
	public String toString() {
		return "Bucket [id=" + id + ", processedTitle=" + processedTitle + ", score=" + score + "]";
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

	public Bucket(Integer id, String processedTitle) {
		super();
		this.id = id;
		this.processedTitle = processedTitle;
	}

	public Bucket() {
		super();
	}
	
	

	
	

	
	
    
}