package com.ufu.bot.to;

import java.util.List;
import java.util.Set;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BucketOld {
	private static final long serialVersionUID = -11115191211815641L;
	
	private Integer postId;
	
	private Integer parentId;
	
	private String presentingBody;
	
	private String processedBodyStemmedStopped;
	
	private Integer postScore;
	
	private List<String> codes;
	
	private Set<String> classesNames;
	    
	private Integer userReputation;
	
	private Integer relationTypeId;
	
	/*
	 * Similarity values compared to main bucket
	 */
	private Double cosSim;
	private Double coverageScore;
	private Double codeSizeScore;
	private Double repScore;
	private Double upScore;
	
	private Double composedScore;
	
	public BucketOld() {
		cosSim = 0d;
		coverageScore = 0d;
		repScore = 0d;
		upScore = 0d;
		composedScore = 0d;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((postId == null) ? 0 : postId.hashCode());
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
		BucketOld other = (BucketOld) obj;
		if (postId == null) {
			if (other.postId != null)
				return false;
		} else if (!postId.equals(other.postId))
			return false;
		return true;
	}



	public Integer getPostId() {
		return postId;
	}



	public void setPostId(Integer postId) {
		this.postId = postId;
	}



	public Integer getParentId() {
		return parentId;
	}



	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}



	


	public String getProcessedBodyStemmedStopped() {
		return processedBodyStemmedStopped;
	}



	public void setProcessedBodyStemmedStopped(String processedBodyStemmedStopped) {
		this.processedBodyStemmedStopped = processedBodyStemmedStopped;
	}



	public Integer getPostScore() {
		return postScore;
	}



	public void setPostScore(Integer postScore) {
		this.postScore = postScore;
	}






	public Integer getUserReputation() {
		return userReputation;
	}



	public void setUserReputation(Integer userReputation) {
		this.userReputation = userReputation;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	public String getPresentingBody() {
		return presentingBody;
	}



	public void setPresentingBody(String presentingBody) {
		this.presentingBody = presentingBody;
	}






	public List<String> getCodes() {
		return codes;
	}



	public void setCodes(List<String> codes) {
		this.codes = codes;
	}



	public Set<String> getClassesNames() {
		return classesNames;
	}



	public void setClassesNames(Set<String> classesNames) {
		this.classesNames = classesNames;
	}



	public Double getCosSim() {
		return cosSim;
	}



	public void setCosSim(Double cosSim) {
		this.cosSim = cosSim;
	}







	@Override
	public String toString() {
		return "BucketOld [postId=" + postId + ", parentId=" + parentId + ", presentingBody=" + presentingBody
				+ ", processedBodyStemmedStopped=" + processedBodyStemmedStopped + ", postScore=" + postScore
				+ ", codes=" + codes + ", classesNames=" + classesNames + ", userReputation=" + userReputation
				+ ", relationTypeId=" + relationTypeId + ", cosSim=" + cosSim + ", coverageScore=" + coverageScore
				+ ", codeSizeScore=" + codeSizeScore + ", repScore=" + repScore + ", upScore=" + upScore
				+ ", composedScore=" + composedScore + "]";
	}



	public Double getCoverageScore() {
		return coverageScore;
	}



	public void setCoverageScore(Double coverageScore) {
		this.coverageScore = coverageScore;
	}



	public Double getRepScore() {
		return repScore;
	}



	public void setRepScore(Double repScore) {
		this.repScore = repScore;
	}



	public Double getUpScore() {
		return upScore;
	}



	public void setUpScore(Double upScore) {
		this.upScore = upScore;
	}



	public Double getComposedScore() {
		return composedScore;
	}



	public void setComposedScore(Double composedScore) {
		this.composedScore = composedScore;
	}



	public Double getCodeSizeScore() {
		return codeSizeScore;
	}



	public void setCodeSizeScore(Double codeSizeScore) {
		this.codeSizeScore = codeSizeScore;
	}



	public Integer getRelationTypeId() {
		return relationTypeId;
	}



	public void setRelationTypeId(Integer relationTypeId) {
		this.relationTypeId = relationTypeId;
	}







	
	

	
	
    
}