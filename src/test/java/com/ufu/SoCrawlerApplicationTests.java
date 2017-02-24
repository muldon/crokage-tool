package com.ufu;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SoCrawlerApplicationTests {

	//@Test
	public void homePage() throws Exception  {

		try (final WebClient webClient = new WebClient()) {
			final HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");
			System.out.println(page.getTitleText());

			final String pageAsXml = page.asXml();
			System.out.println(pageAsXml);

			final String pageAsText = page.asText();
			System.out.println(pageAsText);

		}

	}
	
	//@Test
	public void homePage_Firefox() throws Exception {
	    try (final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38)) {
	        final HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");
	        System.out.println(page.getTitleText());
	    }
	}
	
	//@Test
	public void getElements() throws Exception {
	    try (final WebClient webClient = new WebClient()) {
	    	webClient.getOptions().setJavaScriptEnabled(false);
	        final HtmlPage page = webClient.getPage("http://www.bbc.com/");
	        final HtmlDivision div = page.getHtmlElementById("orb-footer");
	        System.out.println(div.asText());
	        //final HtmlAnchor anchor = page.getAnchorByName("anchor_name");
	    }
	}
	
	
	//@Test
	public void xpath() throws Exception {
	    try (final WebClient webClient = new WebClient()) {
	        final HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");

	        //get list of all divs
	        final List<?> divs = page.getByXPath("//div");

	        //get div which has a 'name' attribute of 'John'
	        final HtmlDivision div = (HtmlDivision) page.getByXPath("//div[@name='John']").get(0);
	    }
	}
	
	@Test
	public void submittingForm() throws Exception {
	    try (final WebClient webClient = new WebClient((BrowserVersion.FIREFOX_45))) {

	        // Get the first page
	        final HtmlPage page1 = webClient.getPage("http://www.w3schools.com/html/html_forms.asp");
	        //List<HtmlForm> forms = page1.getForms();
	        
	        System.out.println(page1.asText());
	        //HtmlForm loginForm = forms.get(0);
	        DomElement login = page1.getElementByName("firstname");
	        login.setTextContent("Mickey");
	        
	        DomElement password = page1.getElementByName("lastname");
	        password.setTextContent("Mouse");
	        
	        // Get the form that we are dealing with and within that form, 
	        // find the submit button and the field that we want to change.
	        final HtmlForm form = page1.getForms().get(0);
	        DomElement botao = form.getLastElementChild();
	        
	        System.out.println(botao.asText());
	        //final HtmlSubmitInput button = form.getInputByName("submitbutton");
	        //final HtmlTextInput textField = form.getInputByName("userid");

	        // Change the value of the text field
	        //textField.setValueAttribute("root");
	        

	        // Now submit the form by clicking the button and get back the second page.
	       
	        
	       
	       
	        
	        //final HtmlPage page2 = button.click();
	    }
	}
	

}
