package com.ufu.survey.transfer;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.ufu.bot.to.ExternalQuestion;




public class ExternalQuestionTransfer extends ExternalQuestion
{
	private static final long serialVersionUID = -7512143108450216531L;

	protected List<ExternalQuestion> externalQuestions;
	protected Short listSize;
	protected String errorMessage;
	protected String infoMessage;
	
	
	public ExternalQuestionTransfer(List<ExternalQuestion> externalQuestions,ExternalQuestion externalQuestion ,String errorMessage, String infoMessage) {
		this.externalQuestions = externalQuestions;
		if(externalQuestion!=null){
			BeanUtils.copyProperties(externalQuestion, this);
		}
		if(externalQuestions!=null){
			this.listSize = (short)externalQuestions.size();
		}
		
		this.errorMessage = errorMessage;
		this.infoMessage = infoMessage;
	
	}
	
	public ExternalQuestionTransfer(){
		
	}

	public List<ExternalQuestion> getExternalQuestions() {
		return externalQuestions;
	}

	public void setExternalQuestions(List<ExternalQuestion> externalQuestions) {
		this.externalQuestions = externalQuestions;
	}

	public Short getListSize() {
		return listSize;
	}

	public void setListSize(Short listSize) {
		this.listSize = listSize;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getInfoMessage() {
		return infoMessage;
	}

	public void setInfoMessage(String infoMessage) {
		this.infoMessage = infoMessage;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "ExternalQuestionTransfer [externalQuestions=" + externalQuestions + ", listSize=" + listSize + ", errorMessage=" + errorMessage + ", infoMessage=" + infoMessage + "]";
	}

	








	


}