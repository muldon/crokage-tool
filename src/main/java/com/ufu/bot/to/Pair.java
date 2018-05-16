package com.ufu.bot.to;

import java.util.HashMap;

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
@Table(name = "pair")
public class Pair {
	@Id
    @SequenceGenerator(name="pair_id", sequenceName="pair_id",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="pair_id")
	private Integer id;

	
	private Integer question1Id;
	
	@Transient
	private ProcessedPosts question1;
	
	
	@Transient
	private String question1Type;

	
	private Integer question2Id;
	
	@Transient
	private ProcessedPosts question2;

	@Transient
	private String question2Type;
	
	
	private Boolean duplicated;

	private String maintag;
	

	@Transient
	private HashMap<String, Feature> features;
	
	
	public Pair() {
		super();
	}


	public Pair(Integer nonMaster, String nonMasterType, Integer master, String masterType) {
		super();
		this.question1Id = nonMaster;
		this.question1Type = nonMasterType;
		this.question2Id = master;
		this.question2Type = masterType;
	}
	
	public Pair(Integer question1Id, Integer question2Id) {
		super();
		this.question1Id = question1Id;
		this.question2Id = question2Id;
		
	}

	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((question1Id == null) ? 0 : question1Id.hashCode());
		result = prime * result + ((question2Id == null) ? 0 : question2Id.hashCode());
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
		Pair other = (Pair) obj;
		if (question1Id == null) {
			if (other.question1Id != null)
				return false;
		} else if (!question1Id.equals(other.question1Id))
			return false;
		if (question2Id == null) {
			if (other.question2Id != null)
				return false;
		} else if (!question2Id.equals(other.question2Id))
			return false;
		return true;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

	
	public Boolean getDuplicated() {
		return duplicated;
	}

	public void setDuplicated(Boolean duplicated) {
		this.duplicated = duplicated;
	}

	

	public String getMaintag() {
		return maintag;
	}

	public void setMaintag(String maintag) {
		this.maintag = maintag;
	}

	public HashMap<String, Feature> getFeatures() {
		return features;
	}

	public void setFeatures(HashMap<String, Feature> features) {
		this.features = features;
	}


	

	public String getQuestion1Type() {
		return question1Type;
	}


	public void setQuestion1Type(String nonMasterType) {
		this.question1Type = nonMasterType;
	}


	

	public String getQuestion2Type() {
		return question2Type;
	}


	public void setQuestion2Type(String masterType) {
		this.question2Type = masterType;
	}


	@Override
	public String toString() {
		return "Pair [id=" + id + ", question1Id=" + question1Id + ", question1Type=" + question1Type + ", question2Id=" + question2Id + ", question2Type=" + question2Type + ", duplicated=" + duplicated
				+ ", maintag=" + maintag + ", features=" + features + "]";
	}


	public Integer getQuestion1Id() {
		return question1Id;
	}


	public void setQuestion1Id(Integer question1Id) {
		this.question1Id = question1Id;
	}


	public ProcessedPosts getQuestion1() {
		return question1;
	}


	public void setQuestion1(ProcessedPosts question1) {
		this.question1 = question1;
	}


	public Integer getQuestion2Id() {
		return question2Id;
	}


	public void setQuestion2Id(Integer question2Id) {
		this.question2Id = question2Id;
	}


	public ProcessedPosts getQuestion2() {
		return question2;
	}


	public void setQuestion2(ProcessedPosts question2) {
		this.question2 = question2;
	}

	
	
		
	

}
