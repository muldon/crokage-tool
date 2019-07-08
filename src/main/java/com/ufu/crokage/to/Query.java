package com.ufu.crokage.to;

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
@Table(name = "query")
public class Query {
	private static final long serialVersionUID = -121252191111815641L;
	
	@Id
    @SequenceGenerator(name="query_id_seq", sequenceName="query_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="query_id_seq")
	private Integer id;
	
	private String query;
	
	@Column(name="numberofcomposedanswers")
	private Integer numberOfComposedAnswers;
	
	@Column(name="ipaddress")
	private String ipAddress;
	
	private Timestamp date;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String queryText) {
		this.query = queryText;
	}

	public Integer getNumberOfComposedAnswers() {
		return numberOfComposedAnswers;
	}

	public void setNumberOfComposedAnswers(Integer numberOfComposedAnswers) {
		this.numberOfComposedAnswers = numberOfComposedAnswers;
	}

	

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	

	@Override
	public String toString() {
		return "Query [id=" + id + ", query=" + query + ", numberOfComposedAnswers=" + numberOfComposedAnswers
				+ ", ipAddress=" + ipAddress + ", date=" + date + "]";
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}
	

	
    
}