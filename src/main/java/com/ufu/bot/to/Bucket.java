package com.ufu.bot.to;

import java.util.List;
import java.util.Set;


public class Bucket {
	private static final long serialVersionUID = -11115191211815641L;
	
	private Integer postId;
	
	private Integer parentId;
	
	private String presentingBody;
	
	private String processedBodyStemmedStopped;
	
	private Integer postScore;
	
	private List<String> codes;
	
	private Set<String> classesNames;
	    
	private Integer userReputation;
	
	private Double cosSim;
	
	
	public Bucket() {
		
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
		Bucket other = (Bucket) obj;
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
		return "Bucket [postId=" + postId + ", parentId=" + parentId + ", presentingBody=" + presentingBody + ", processedBodyStemmedStopped=" + processedBodyStemmedStopped + ", postScore=" + postScore + ", codes=" + codes + ", classesNames=" + classesNames + ", userReputation=" + userReputation + ", cosSim=" + cosSim + "]";
	}





	
	

	
	
    
}