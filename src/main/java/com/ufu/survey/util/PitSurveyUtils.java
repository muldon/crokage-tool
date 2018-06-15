package com.ufu.survey.util;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.ufu.bot.to.ExternalQuestion;



@Component
public class PitSurveyUtils {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
		
	public List<ExternalQuestion> readAnswerBotQuestionsAndAnswers() throws IOException {
		List<ExternalQuestion> answerBotQuestionAnswers = new ArrayList<>();
		
		URL url;
		String fileContent="";
		String query="";
		String answer="";
				
		for(int i=0; i<100; i++){
			url = Resources.getResource(i+".txt");
			fileContent = Resources.toString(url, Charsets.UTF_8);
			
			List<String> lines = IOUtils.readLines(new StringReader(fileContent));
			query = lines.get(1);
			query = query.replace("query : ","");
			
			lines.remove(0);
			lines.remove(0);
			lines.remove(0);
			lines.remove(0);
			lines.remove(lines.size()-1);
			
			answer = lines.stream().collect(Collectors.joining("\n"));
			//System.out.println(answer);
			answerBotQuestionAnswers.add(new ExternalQuestion(i+1,query,answer));
		}
		
		return answerBotQuestionAnswers;
		
	}
	
}
