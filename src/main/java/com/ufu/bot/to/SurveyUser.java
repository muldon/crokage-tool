package com.ufu.bot.to;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "surveyuser")
public class SurveyUser {
	private static final long serialVersionUID = -111252191111815641L;
	@Id
    @SequenceGenerator(name="surveyuser_id", sequenceName="surveyuser_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="surveyuser_id")
	private Integer id;
	
    private String login;
	
	private String nick;

	public String getId() {
		return login;
	}

	public void setId(String id) {
		this.login = id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	

	@Override
	public String toString() {
		return "SurveyUser [id=" + id + ", login=" + login + ", nick=" + nick + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((login == null) ? 0 : login.hashCode());
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
		SurveyUser other = (SurveyUser) obj;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		return true;
	}

	public SurveyUser(String id, String nick) {
		super();
		this.login = id;
		this.nick = nick;
	}

	public SurveyUser() {
		super();
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	
	
	

	
	
    
}