package com.ufu;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class GoogleWebSearchTest {

  //@Before
  public void before() throws Exception {
  }

  //@After
  public void after() throws Exception {
  }
/*
  //@Test
  public void shouldSearch() throws IOException {
 //Intentionally commented out to comply with Google Terms of Service
    SearchQuery query = new SearchQuery.Builder("How do I create a file and write to it in Java?")
            .site("https://stackoverflow.com")
            .numResults(20).build();
        SearchResult result = new GoogleWebSearch().search(query);
        //assertThat(result.getSize(), equalTo(10));
        List<String> urls = result.getUrls();
        //List<String> urls = new ArrayList<>();
        Integer questionId;
        //urls.add("https://stackoverflow.com/questions/21961651/why-does-activesupport-add-method-forty-two-to-array/21962048");
        
        for(String url: urls){
        	//String[] urlPart = url.split("\\/[[:digit:]].*\\/");
        	
        	Pattern pattern = Pattern.compile("\\/([\\d]+)");
    		Matcher matcher = pattern.matcher(url);
    		if (matcher.find()) {
    			
    				System.out.println(matcher.group(1));
    				
    		}
        	
        	System.out.println(url);
        	
        	
        }
        
        
    
  }*/

   @Test
  public void shouldSearchWithSpecialCharacters() {
 //Intentionally commented out to comply with Google Terms of Service
  /*  SearchQuery query = new SearchQuery.Builder("java api")
        .site("stackoverflow.com")
        .numResults(10).build();
    SearchResult result = new GoogleWebSearchOld().search(query);
    assertThat(result.getSize(), equalTo(10));*/
  }

  @Test
  public void shouldSearchWithConfig() {
  //Intentionally commented out to comply with Google Terms of Service
   /* SearchQuery query = new SearchQuery.Builder("bunnies")
        .site("stackoverflow.com")
        .numResults(10).build();
    SearchConfig config = new SearchConfig();
    config.setGOOGLE_SEARCH_URL_PREFIX("https://www.google.de/search?");
    SearchResult result = new GoogleWebSearchOld(config).search(query);
    assertThat(result.getSize(), equalTo(10));*/
  }

}
