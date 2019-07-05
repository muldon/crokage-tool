package com.ufu.crokage.resources;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.ufu.crokage.CrokageApp;
import com.ufu.crokage.config.AppAux;
import com.ufu.crokage.to.GenericRestTransfer;
import com.ufu.crokage.to.Post;
import com.ufu.crokage.to.PostRestTransfer;
import com.ufu.crokage.to.Query;
import com.ufu.crokage.util.CrokageUtils;



@Path("/query")
@Produces(MediaType.APPLICATION_JSON)
@RestController
public class QueryResource extends AppAux{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public CrokageApp crokageApp;
	
	private DateTimeFormatter dtf;
	LocalDateTime now;
	
	
	{
		dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		now = LocalDateTime.now();
	}
	
	@GET
	@Path("/test/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericRestTransfer getTopico(@PathParam("id") Short id)
	{
		String errorMessage = null;
		String infoMessage = null;
		logger.info("Busca por id: "+id);
		
		
		try {
			System.out.println(id);
						
		} catch (Exception e) {
			logger.error("Erro ao carregar id: "+e);
			errorMessage = "Erro ao carregar id.";
		}
						
		return new GenericRestTransfer(null,"ok",errorMessage,infoMessage);
		
	}
	
	@Path("/gettestsolutions")
	@POST	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericRestTransfer getTestSolutions(Query query) {
		String errorMessage = null;
		String infoMessage = null;
		String composition= "";
		long initTime1 = System.currentTimeMillis();
		try{
			System.out.println(query);
			composition = "Query: How to round a number to n decimal places in Java\n-----------------------------------------------------------------------\n\nRank:1 (https://stackoverflow.com/questions/24884082)\n You can use BigDecimal \n\n  BigDecimal value = new BigDecimal(\"2.3\");\nvalue = value.setScale(0, RoundingMode.UP);\nBigDecimal value1 = new BigDecimal(\"-2.3\");\nvalue1 = value1.setScale(0, RoundingMode.UP);\nSystem.out.println(value + \"n\" + value1);\n  \n\n Refer: http://www.javabeat.net/precise-rounding-of-decimals-using-rounding-mode-enumeration/ \n\n\n-------------------------------------next answer-------------------------------------\n\nRank:2 (https://stackoverflow.com/questions/4826827)\n  \n\n  double d = 9232.129394d;\n  \n\n you can use http://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html \n\n  BigDecimal bd = new BigDecimal(d).setScale(2, RoundingMode.HALF_EVEN);\nd = bd.doubleValue();\n  \n\n   \n\n  d = Math.round(d*100)/100.0d;\n  \n\n with both solutions  d == 9232.13  \n\n\n";
			infoMessage = "ok....  ! ";
		
		}catch(Exception e){
			logger.error("Erro ao consultar query: "+e);
			errorMessage = "Erro ao consultar query.";
		}
		System.out.println("finished !");
		CrokageUtils.reportElapsedTime(initTime1,"getsolutions");
		
		return new GenericRestTransfer(null, composition, infoMessage,errorMessage);
	}
	

	
	@Path("/getsolutions")
	@POST	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PostRestTransfer save(Query query) {
		String errorMessage = null;
		String infoMessage = null;
		String composition= "";
		long initTime1 = System.currentTimeMillis();
		List<Post> posts = new ArrayList<>();
		
		try{
			String logMessage = "...at: "+dtf.format(now)+" - query: "+query.getQueryText()+ " - num ans:"+query.getNumberOfComposedAnswers();
			System.out.println(logMessage);
			
			if(query==null || StringUtils.isBlank(query.getQueryText())) {
				errorMessage="Query is null";
			}else {
				//composition = crokageApp.extractAnswers(query);
				posts = crokageApp.extractAnswers(query);
				
				/*Post post = crokageService.findPostById(8881748);
				Post post2 = crokageService.findPostById(11986716);
				
				posts.add(post);
				posts.add(post2);*/
								
				infoMessage = "Answers returned: "+posts.size();
			}
		
		
		}catch(Exception e){
			logger.error("Error ... sorry... : "+e);
			errorMessage = "Error ... sorry....";
		}
		//System.out.println("finished !");
		//CrokageUtils.reportElapsedTime(initTime1,"getsolutions");
		
		return new PostRestTransfer(posts, null, infoMessage,errorMessage);
	}
	
	
	
	
}