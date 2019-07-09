package com.ufu.crokage.to;

import java.util.List;
import java.util.Set;

public class PostRestTransfer extends GenericRestTransfer{
	public List<Post> posts;
	public Set<String> tags;
	public Integer queryId;
	

	public PostRestTransfer(List posts, String descricao,Set<String> tags, Integer queryId, String infoMessage, String errorMessage)
	{
		this.posts = posts;
		this.errorMessage = errorMessage;
		this.infoMessage = infoMessage;
		this.descricao = descricao;
		this.queryId = queryId;
		this.tags= tags;
		
	}
	
	public PostRestTransfer() {
		this.descricao=null;
		this.infoMessage=null;
		this.id=null;
		this.errorMessage=null;
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

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public Integer getQueryId() {
		return queryId;
	}

	public void setQueryId(Integer queryId) {
		this.queryId = queryId;
	}
	
	
	

}