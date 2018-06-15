package com.ufu.bot.exception;

public class PitBotException extends Exception {
	private String message;

		
	public PitBotException(String mensagem) {
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
