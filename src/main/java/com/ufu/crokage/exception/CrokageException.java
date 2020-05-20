package com.ufu.crokage.exception;

public class CrokageException extends Exception {
	private String message;

		
	public CrokageException(String mensagem) {
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
