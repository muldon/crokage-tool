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
import com.ufu.survey.transfer.ToTransfer;
import com.ufu.survey.transfer.TokenTransfer;
import com.ufu.survey.util.TokenUtils;




@Component
@Path("/survey")
@Produces(MediaType.APPLICATION_JSON)
public class PitSurveyResource extends SuperResource {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
		
	
	@Path("/authenticateUser")
	@POST	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TokenTransfer authenticateUser(SurveyUser surveyUser) {
		String errorMessage = null;
		String infoMessage = null;
		
		try{
			
			surveyUser = pitSurveyService.authenticateUser(surveyUser);
			if(surveyUser==null) {
				errorMessage = "You have no power here ! ";
				return null;
			}
			
		
		}catch(Exception e){
			errorMessage = "You have no power here ! ";
			logger.error(errorMessage+e);
		}
		
		return new TokenTransfer(TokenUtils.createToken(surveyUser),surveyUser.getId(),errorMessage);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/loadUser/{id}")
	public ToTransfer loadUser(@PathParam("id") Integer id)
	{
		logger.info("Loading user : "+id);
		SurveyUser surveyUser = pitSurveyService.loadUser(id);
				
		return new ToTransfer(null,surveyUser, null,null);
		
	}
	
	
	
	@GET
	@Path("/loadQuestions/{internalSurvey}")
	@Produces(MediaType.APPLICATION_JSON)
	public ToTransfer loadQuestions(@PathParam("internalSurvey") Boolean internalSurvey)
	{
		String errorMessage = null;
		String infoMessage = null;
		ToTransfer toTransfer = new ToTransfer<ExternalQuestion>();
		
		try {
			pitSurveyService.loadQuestions(toTransfer,internalSurvey);
						
		} catch (Exception e) {
			errorMessage = "Error when loadQuestions.";
			logger.error(errorMessage+e);
			
		}
						
		return toTransfer;
		
	}
	
	@GET
	@Path("/loadNextQuestion/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ToTransfer loadNextQuestion(@PathParam("id") Integer userId)
	{
		String errorMessage = null;
		String infoMessage = null;
		ToTransfer toTransfer = new ToTransfer<ExternalQuestion>();
		
		try {
			pitSurveyService.loadNextQuestion(toTransfer,userId);
						
		} catch (Exception e) {
			errorMessage = "Error when loadCurrentQuestion.";
			logger.error(errorMessage+e);
			
		}
						
		return toTransfer;
		
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
	
	
	@Path("/saveRatings")
	@POST	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericRestTransfer saveRatings(Evaluation evaluation) {
		String errorMessage = null;
		String infoMessage = null;
				
		try{
			
			pitSurveyService.saveRatings(evaluation);
			infoMessage = "Ratings saved successfully.";
		
		}catch(Exception e){
			errorMessage = "Error when saving rating.";
			logger.error(errorMessage+e);
		}
		
		return new GenericRestTransfer(null, null, infoMessage, errorMessage);
	}
	
	
	
	
	
	
}