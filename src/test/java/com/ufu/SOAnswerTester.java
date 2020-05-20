package com.ufu;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;

public class SOAnswerTester {
	public static Integer sleepTimeBeforeAnswer = 35000;
	public static Integer timeBetweenKeys = 100;
	public static Integer maxWaitTimeAfterClickOrLoadPage = 20000;
	public static Short tries = 10;

	public static void main(String[] args) throws Exception {
		SOAnswerTester t = new SOAnswerTester();
		// t.submittingForm();
		//t.loginSO(null);
		//t.generateRandomBrowser();
		t.answerQuestion();
		
		//t.testGetElement();
	}

	

	private void answerQuestion() throws Exception {
		BrowserVersion browser = generateRandomBrowser();
		
		try (final WebClient webClient = new WebClient((browser))) {
			//webClient.getOptions().setJavaScriptEnabled(false);
	        webClient.getOptions().setCssEnabled(true);
	        webClient.getOptions().setRedirectEnabled(true);
	        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
	        webClient.getCookieManager().setCookiesEnabled(true);
	        
			webClient.setAjaxController(new AjaxController(){
				public boolean processSynchron(HtmlPage page, WebRequest request, boolean async)
			    {
			        return true;
			    }
			});
			
			final HtmlPage questionPage = webClient.getPage("http://stackoverflow.com/questions/39750042/replace-html-content-with-multiple-elements");
			waitForLoadingPage(questionPage);
			HtmlPage homePage = loginSOFromAnswerURL(webClient,questionPage);
			System.out.println(homePage.asText());
			
			Thread.sleep(511 + new Random().nextInt(4201));
			HtmlTextArea answerField = (HtmlTextArea)homePage.getHtmlElementById("wmd-input");
			typeAnswer("You can do this using Javascript or JQuery. Once you have your arrayList containing your values, you could replace `<li>PLACEHOLDER</li>` with the list values. Let your client do this job.",answerField); 
			System.out.println(answerField.getText());
			
			DomElement button = homePage.getElementById("submit-button"); 
			HtmlPage resultPost = button.click();
			//waitForResponseCondition(resultPost);
			System.out.println(resultPost.asText());
			 

		}

	}

	public BrowserVersion generateRandomBrowser() {
		BrowserVersion v1 = BrowserVersion.FIREFOX_45;
		BrowserVersion v2 = BrowserVersion.CHROME;
		BrowserVersion v3 = BrowserVersion.INTERNET_EXPLORER;
		BrowserVersion[] browsersArray = {v1,v2,v3};
				
		BrowserVersion chosen = browsersArray[new Random().nextInt(browsersArray.length)];
		System.out.println(chosen);
		return chosen;
	}

	

	private HtmlPage loginSOFromAnswerURL(WebClient webClient, HtmlPage questionPage) throws IOException, FailingHttpStatusCodeException, InterruptedException {
		Thread.sleep(1000 + new Random().nextInt(7000));
		DomElement link = questionPage.getFirstByXPath("//a[./text() = 'log in']");
		HtmlPage resultPost = link.click();
		waitForLoadingPage(resultPost);
		System.out.println(resultPost.asText());

		HtmlTextInput login = (HtmlTextInput) resultPost.getHtmlElementById("email");
		typeAnswer("h563476@mvrht.com", login);
		
		HtmlPasswordInput password = (HtmlPasswordInput) resultPost.getHtmlElementById("password");
		typeAnswer("rogeriocarlos0", password);
		
		Thread.sleep(1000 + new Random().nextInt(2000));

		HtmlButtonInput button = (HtmlButtonInput)resultPost.getElementById("submit-button");
		
		HtmlPage home = button.click();
		
		while(tries >= 0 && !home.asXml().contains("your reputation")){
			tries--;
			Thread.sleep(1000);
			home = button.click();
		}
		
		
		return home;

	}

	private void waitForHomeCondition(HtmlButtonInput button, HtmlPage home) throws InterruptedException, IOException {
		boolean homeCondition = home.asXml().contains("your reputation");
		while(tries >= 0 && !homeCondition){
			tries--;
			Thread.sleep(1000);
			home = button.click();
		}
		
	}

	private void waitForLoadingPage(HtmlPage home) throws InterruptedException {
		JavaScriptJobManager manager = home.getEnclosingWindow().getJobManager();
		while (manager.getJobCount() > 0) {
		    Thread.sleep(1000);
		}
		
	}

	

	private String typeAnswer(String string, HtmlElement answerField) throws InterruptedException, IOException {
		char[] chs = string.toCharArray();
		int size = chs.length;
		for (int i = 0; i < size; i++) {
			System.out.println(chs[i]);
			answerField.type(chs[i]);
			Thread.sleep(new Random().nextInt(200) + timeBetweenKeys);

		}

		return null;
	}

	private HtmlPage loginSO(WebClient webClient) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		if (webClient == null) {
			webClient = new WebClient((BrowserVersion.FIREFOX_45));
		}

		final HtmlPage page1 = webClient.getPage("https://stackoverflow.com/users/login");
		
		HtmlTextInput login = (HtmlTextInput) page1.getHtmlElementById("email");
		login.setValueAttribute("h563476@mvrht.com");

		HtmlPasswordInput password = (HtmlPasswordInput) page1.getHtmlElementById("password");
		password.setValueAttribute("rogeriocarlos0");

		HtmlButtonInput button = (HtmlButtonInput)page1.getElementById("submit-button");
		HtmlPage home = button.click();
		Thread.sleep(3000);   
		home = button.click();
		Thread.sleep(3000);
		return home;
		//System.out.println(home.asText());

		
	}

	

	
	private void testGetElement() throws Exception {
		BrowserVersion browser = generateRandomBrowser();
		
		try (final WebClient webClient = new WebClient((browser))) {
			webClient.getOptions().setJavaScriptEnabled(false);
	        webClient.getOptions().setCssEnabled(true);
	        webClient.getOptions().setRedirectEnabled(true);
	        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
	        webClient.getCookieManager().setCookiesEnabled(true);
	        
			webClient.setAjaxController(new AjaxController(){
				public boolean processSynchron(HtmlPage page, WebRequest request, boolean async)
			    {
			        return true;
			    }
			});
			
			final HtmlPage questionPage = webClient.getPage("http://stackoverflow.com/questions/39639824/word-count-in-a-string");
			waitForLoadingPage(questionPage);
			DomElement link = questionPage.getFirstByXPath("//a[./text() = 'log in']");
			System.out.println("a");
			
		}	
			
		
	}
}
