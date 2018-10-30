package com.ufu;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.code.stackexchange.client.query.StackExchangeApiQueryFactory;
import com.google.code.stackexchange.common.PagedList;
import com.google.code.stackexchange.schema.Paging;
import com.google.code.stackexchange.schema.Question;
import com.google.code.stackexchange.schema.Range;
import com.google.code.stackexchange.schema.StackExchangeSite;
import com.google.code.stackexchange.schema.User.QuestionSortOrder;

public class StackExchangeAppTest {


	@Test
	public void testSoSearch(){
		/*StackExchangeApiQueryFactory queryFactory = StackExchangeApiQueryFactory
		        .newInstance("applicationKey", 
		        StackExchangeSite.STACK_OVERFLOW);


		// Get all questions.
		String filter = "default";
		String tag= "java";
		
		Paging paging = new Paging(1, 100);
		PagedList<Question> questions = queryFactory.newQuestionApiQuery()
		     .withPaging(paging).withFilter(filter)
		     .withSort(Question.SortOrder.MOST_HOT)
		     .withTags(tag).list();

		//Get questions by answer ids. 
		long answerId = 21859130; 
		List<Question> question3s = queryFactory.newQuestionApiQuery()
		     .withPaging(paging).withFilter(filter)
		     .withSort(Question.SortOrder.MOST_RECENTLY_CREATED)
		     .withTags(tag).withAnswerIds(answerId).listQuestionsByAnswer();
		*/
		
		   StackExchangeApiQueryFactory queryFactory = StackExchangeApiQueryFactory  
                   .newInstance("gJMxDoCH9XhURvF7a3F4Kg((",  
                             StackExchangeSite.STACK_OVERFLOW);  
         Paging paging = new Paging(1, 2);  
         int minViews = 0;  
         int minAnswers = 0;  
         String filterName = "default";  
         String query = "Read JSON Array";  
         List<String> tagged = new ArrayList<String>();  
         tagged.add("java");  
         List<String> nottagged = new ArrayList<String>();  
         PagedList<Question> questions = queryFactory  
                   .newAdvanceSearchApiQuery()
                   .withFilter(filterName)
                   .withSort(QuestionSortOrder.MOST_RELEVANT)
                   .withQuery(query)
                   .withMinAnswers(1)
                   .withTags(tagged).list();  
		
		
         for(Question question:questions) {
        	 System.out.println(question.getQuestionId()+ " - "+question.getTitle());
         }
         
		System.out.println();
		
	}

}
