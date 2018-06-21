package com.ufu.survey.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ufu.bot.to.Evaluation;
import com.ufu.bot.to.ExternalQuestion;
import com.ufu.bot.to.SurveyUser;
import com.ufu.survey.transfer.ExternalQuestionTransfer;
import com.ufu.survey.transfer.GenericRestTransfer;
import com.ufu.survey.transfer.TokenTransfer;
import com.ufu.survey.util.TokenUtils;



@Component
@Path("/survey")
@Produces(MediaType.APPLICATION_JSON)
public class PitSurveyResource extends SuperResource {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String errorMessage = null;
	private String infoMessage = null;
		
	
	@Path("/authenticateUser")
	@POST	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TokenTransfer save(SurveyUser surveyUser) {
		try{
			
			surveyUser = pitSurveyService.authenticateUser(surveyUser);
			if(surveyUser==null) {
				errorMessage = "You have no power here ! ";
			}
			
		
		}catch(Exception e){
			errorMessage = "You have no power here ! ";
			logger.error(errorMessage+e);
		}
		
		return new TokenTransfer(TokenUtils.createToken(surveyUser),surveyUser.getId(),errorMessage);
	}
	
	
	
	@GET
	@Path("/externalQuestions/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ExternalQuestionTransfer getExternalQuestion(@PathParam("id") Integer externalQuestionId)
	{
		String errorMessage = null;
		String infoMessage = null;
		
		ExternalQuestion externalQuestion = null;
		try {
			externalQuestion = pitSurveyService.findExternalQuestionById(externalQuestionId);
						
		} catch (Exception e) {
			errorMessage = "Error when loading external question.";
			logger.error(errorMessage+e);
			
		}
						
		return new ExternalQuestionTransfer(null,externalQuestion,errorMessage,infoMessage);
		
	}
	
	
	
	
	@GET
	@Path("/getAllExternalQuestionsForActiveSurvey/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ExternalQuestionTransfer getTodosTopicos(@PathParam("userId") Integer userId)
	{
		String errorMessage = null;
		String infoMessage = null;
		
		List<ExternalQuestion> externalQuestions = null;
		try {
			externalQuestions = pitSurveyService.getAllExternalQuestionsForActiveSurvey(userId);
			
		} catch (Exception e) {
			errorMessage = "Error when loading external questions.";
			logger.error(errorMessage+e);
		}
						
		return new ExternalQuestionTransfer(externalQuestions,null,errorMessage,infoMessage);
		
	}
	
	
	@Path("/evaluation")
	@POST	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericRestTransfer save(Evaluation evaluation) {
		String errorMessage = null;
		String infoMessage = null;
				
		try{
			
			pitSurveyService.saveEvaluation(evaluation);
			//infoMessage = "Tópico salvo com sucesso ! ";
		
		}catch(Exception e){
			errorMessage = "Error when saving rating.";
			logger.error(errorMessage+e);
		}
		
		return new GenericRestTransfer(null, null, infoMessage, errorMessage);
	}
	
	
	
	
	
	
}