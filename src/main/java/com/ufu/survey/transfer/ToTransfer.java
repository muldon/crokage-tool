package com.ufu.survey.transfer;

import java.util.List;

import org.springframework.beans.BeanUtils;


public class ToTransfer<T> 
{
	private List<T> list;
	private List<T> list2;
	private List<T> list3;
	private T to;
	
	private String errorMessage;
	private String infoMessage;


	public ToTransfer(List<T> list, T toObject, String infoMessage, String errorMessage)
	{
		if(toObject!=null){
			this.to = toObject;
		}
		
		
		this.errorMessage = errorMessage;
		this.infoMessage = infoMessage;
		this.list = list;
	}
	
	public ToTransfer(List<T> list,List<T> list2, T toObject, String infoMessage, String errorMessage)
	{
		if(toObject!=null){
			this.to = toObject;
		}
		
		
		this.errorMessage = errorMessage;
		this.infoMessage = infoMessage;
		this.list = list;
		this.list2=list2;
	}
	
	


	public ToTransfer() {
		super();
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

	public List<T> getList2() {
		return list2;
	}

	public void setList2(List<T> list2) {
		this.list2 = list2;
	}

	public List<T> getList3() {
		return list3;
	}

	public void setList3(List<T> list3) {
		this.list3 = list3;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setInfoMessage(String infoMessage) {
		this.infoMessage = infoMessage;
	}


	




	



}