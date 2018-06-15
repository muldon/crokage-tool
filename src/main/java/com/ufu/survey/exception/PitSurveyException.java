package com.ufu.survey.exception;

public class PitSurveyException extends Exception {
	private String message;

		
	public PitSurveyException(String mensagem) {
		super();
		this.message = mensagem;
	}



	public String getMessage() {
		return message;
	}



	public void setMessage(String message) {
		this.message = message;
	}

	
	
	
}
