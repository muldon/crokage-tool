package com.ufu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ufu.crokage.config.AppAux;
import com.ufu.crokage.to.Query;
import com.ufu.crokage.to.ResultEvaluation;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ToolTest extends AppAux{

	@Test
	public void saveEvaluation() throws Exception  {

		Query query = crokageService.findQueryById(1);
		System.out.println(query);
		
		ResultEvaluation resultEvaluation = new ResultEvaluation();
		resultEvaluation.setQueryId(query.getId());
		resultEvaluation.setPostsIds("posts ids here....");
		resultEvaluation.setLikertValue(3);
		crokageService.saveResultEvaluation(resultEvaluation);
		
	}
	
	
	

}
