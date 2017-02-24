package com.ufu;

import org.junit.Test;

import com.google.code.stackexchange.client.query.StackExchangeApiQueryFactory;
import com.google.code.stackexchange.common.PagedList;
import com.google.code.stackexchange.schema.Answer;
import com.google.code.stackexchange.schema.Paging;
import com.google.code.stackexchange.schema.Question;
import com.google.code.stackexchange.schema.StackExchangeSite;

public class SOGetPostsTester {
	public static void main(String[] args) {
		SOGetPostsTester t = new SOGetPostsTester();
	}

	public SOGetPostsTester() {
		
		getPosts();
		


	}

	private void getPosts() {

		StackExchangeApiQueryFactory queryFactory = StackExchangeApiQueryFactory.newInstance("vo1kZl9xJGZ5mDBBKJwqsQ((",StackExchangeSite.STACK_OVERFLOW);

		// Get all questions.
		String filter = "default";
		Paging paging = new Paging(1, 100);
		PagedList<Question> questions = queryFactory.newQuestionApiQuery().withPaging(paging).withFilter(filter).withSort(Question.SortOrder.MOST_RECENTLY_CREATED).withTags("java").list();
		for (Question q : questions) {
			System.out.println(q.getTitle() + " --- " + q.getAnswerCount());
			for (Answer a : q.getAnswers()) {
				a.getCreationDate();
			}
		}

		// Get questions by answer ids.
		/*
		 * long answerId = 21859130; PagedList<Question> question3s =
		 * queryFactory.newQuestionApiQuery()
		 * .withPaging(paging).withFilter(filter)
		 * .withSort(Question.SortOrder.MOST_RECENTLY_CREATED)
		 * .withTags(tag).withAnswerIds(answerId).listQuestionsByAnswer();
		 */
		
	}

}
