package com.ufu.survey.transfer;

public class TokenTransfer
{

	private final String token;
	private final Integer userId;
	private final String errorMessage;


	public TokenTransfer(String token, Integer userId, String errorMessage)
	{
		this.token = token;
		this.userId = userId;
		this.errorMessage = errorMessage;
	}


	public String getToken()
	{
		return this.token;
	}


	public Integer getUserId() {
		return userId;
	}


	public String getErrorMessage() {
		return errorMessage;
	}
	
	

}