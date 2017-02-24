package com.ufu.bot.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ufu.bot.to.Tags;
import com.ufu.bot.transfer.GenericRestTransfer;



@Component
@Path("/resource")
public class BotResource extends SuperResource
{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
		

		
	
	@GET
	@Path("/findAll/tags")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericRestTransfer findAll()
	{
		String errorMessage = null;
		
		logger.info("Busca por tags");
		
		List<Tags> tags = null;
				
		try {
			tags = tagsService.loadAll();
			logger.info(tags.toString());
		} catch (Exception e) {
			logger.error("Erro ao carregar tags: "+e);
			errorMessage = "Erro ao carregar tags.";
		}						
		return new GenericRestTransfer(tags,null,null, null ,errorMessage);
		
	}
	
	
	
	
}