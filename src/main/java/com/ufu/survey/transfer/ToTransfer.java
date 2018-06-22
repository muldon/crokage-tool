package com.ufu.survey.transfer;

import java.util.List;

import org.springframework.beans.BeanUtils;


public class ToTransfer<T> 
{
	private List<T> list;
	private T to;
	
	private final String errorMessage;
	private final String infoMessage;


	public ToTransfer(List<T> list, T toObject, String infoMessage, String errorMessage)
	{
		if(toObject!=null){
			//BeanUtils.copyProperties(toObject, this.to);
			this.to = toObject;
		}
		
		
		this.errorMessage = errorMessage;
		this.infoMessage = infoMessage;
		this.list = list;
	}


	public List<T> getList() {
		return list;
	}


	public void setList(List<T> list) {
		this.list = list;
	}


	public T getTo() {
		return to;
	}


	public void setTo(T to) {
		this.to = to;
	}


	public String getErrorMessage() {
		return errorMessage;
	}


	public String getInfoMessage() {
		return infoMessage;
	}


	@Override
	public String toString() {
		return "ToTransfer [list=" + list + ", to=" + to + ", errorMessage=" + errorMessage + ", infoMessage="
				+ infoMessage + "]";
	}


	




	



}