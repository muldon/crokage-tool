package com.ufu.bot.to;

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
@Table(name = "externalquestion")
public class ExternalQuestion {
	@Id
    @SequenceGenerator(name="externalquestion_id_seq", sequenceName="externalquestion_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="externalquestion_id_seq")
	private Integer id;
	
	
	@Column(name="surveyid")
	private Integer surveyId;

	@Column(name="googlequery")	
	private String googleQuery;
	
	private String classes;
	
	@Column(name="userack")	
	private Boolean useRack;
	
	@Column(name="answerbotqueryid")	
	private Integer answerBotQueryId;
	
	@Transient
	private String answerBotAnswer;
	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Integer surveyId) {
		this.surveyId = surveyId;
	}

	public String getGoogleQuery() {
		return googleQuery;
	}

	public void setGoogleQuery(String googleQuery) {
		this.googleQuery = googleQuery;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	public Boolean getUseRack() {
		return useRack;
	}

	public void setUseRack(Boolean useRack) {
		this.useRack = useRack;
	}

	public Integer getAnswerBotQueryId() {
		return answerBotQueryId;
	}

	public void setAnswerBotQueryId(Integer answerBotqueryId) {
		this.answerBotQueryId = answerBotqueryId;
	}
	
	

	@Override
	public String toString() {
		return "ExternalQuestion [id=" + id + ", surveyId=" + surveyId + ", googleQuery=" + googleQuery + ", classes="
				+ classes + ", useRack=" + useRack + ", answerBotQueryId=" + answerBotQueryId + "]";
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
		ExternalQuestion other = (ExternalQuestion) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public ExternalQuestion() {
		
	}

	public ExternalQuestion(Integer id, Integer surveyId, String googleQuery, String classes, Boolean useRack,
			Integer answerBotqueryId) {
		super();
		this.id = id;
		this.surveyId = surveyId;
		this.googleQuery = googleQuery;
		this.classes = classes;
		this.useRack = useRack;
		this.answerBotQueryId = answerBotqueryId;
	}
	
	public ExternalQuestion(Integer answerBotqueryId, String query, String answer) {
		this.answerBotAnswer = answer;
		this.googleQuery = query;
		this.answerBotQueryId = answerBotqueryId;
	}
	
	
	
	
	
	
}
