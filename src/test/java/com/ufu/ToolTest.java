package com.ufu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;



@RunWith(SpringRunner.class)
@SpringBootTest
public class ToolTest {

	//@Test
	/*public void saveEvaluation() throws Exception  {

		Query query = crokageService.findQueryById(1);
		System.out.println(query);
		
		ResultEvaluation resultEvaluation = new ResultEvaluation();
		resultEvaluation.setQueryId(query.getId());
		resultEvaluation.setPostsIds("posts ids here....");
		resultEvaluation.setLikertValue(3);
		crokageService.saveResultEvaluation(resultEvaluation);
		
	}*/
	
	@Test
	public void testBackEndRestInterface() throws Exception  {

		try {

			Client client = Client.create();

			WebResource webResource = client.resource("http://isel.ufu.br:8080/crokage/query/getsolutions");

			String input = "{\"numberOfComposedAnswers\":5,\"reduceSentences\":false,\"queryText\":\"rest assured\",\"ipAddress\":\"191.55.97.161\"}";

			ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);

			if (response.getStatus() != 201 && response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
				     + response.getStatus());
			}

			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		  } catch (Exception e) {

			e.printStackTrace();

		  }
		
		
		
	}
	
	

}
