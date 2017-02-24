package com.ufu.bot.transfer;

import java.io.Serializable;
import java.util.List;

public class GenericRestTransfer<T> implements Serializable
{
	private static final long serialVersionUID = -2140576335747945587L;
	protected Integer id;
	protected String descricao;
	protected String errorMessage;
	protected String infoMessage;
	protected List<?> list;
	

	public <T> GenericRestTransfer(List<T> list,Integer id, String descricao, String infoMessage, String errorMessage)
	{
		this.id = id;
		this.errorMessage = errorMessage;
		this.infoMessage = infoMessage;
		this.descricao = descricao;
		this.list = list;
		
	}
	
		

	public Integer getId() {
		return id;
	}


	public String getErrorMessage() {
		return errorMessage;
	}


	public String getInfoMessage() {
		return infoMessage;
	}


	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		return "GenericRestTransfer [id=" + id + ", descricao=" + descricao + ", errorMessage=" + errorMessage
				+ ", infoMessage=" + infoMessage + ", list=" + list + "]";
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setInfoMessage(String infoMessage) {
		this.infoMessage = infoMessage;
	}
	
	

}