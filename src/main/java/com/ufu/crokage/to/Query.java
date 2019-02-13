package com.ufu.crokage.to;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Query {
	private static final long serialVersionUID = -121252191111815641L;
	
	private Integer id;
	
	private String queryText;
	
	private Integer numberOfComposedAnswers;
	
	private Boolean useExtractors;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQueryText() {
		return queryText;
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}

	public Integer getNumberOfComposedAnswers() {
		return numberOfComposedAnswers;
	}

	public void setNumberOfComposedAnswers(Integer numberOfComposedAnswers) {
		this.numberOfComposedAnswers = numberOfComposedAnswers;
	}

	public Boolean getUseExtractors() {
		return useExtractors;
	}

	public void setUseExtractors(Boolean useExtractors) {
		this.useExtractors = useExtractors;
	}

	@Override
	public String toString() {
		return "Query [id=" + id + ", queryText=" + queryText + ", numberOfComposedAnswers=" + numberOfComposedAnswers
				+ ", useExtractors=" + useExtractors + "]";
	}
	

	
    
}