package com.ufu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.ufu.bot.to.Bucket;
import com.ufu.bot.to.Post;
import com.ufu.bot.to.SoThread;
import com.ufu.bot.util.BotUtils;

public class Tester {

	public Tester() throws Exception {
		String str = "How do I send an HTML email? javascript javac dd JAVA";
		StringTokenizer st = new StringTokenizer(str);
		Boolean containToken = Pattern.compile(".*\\bjava\\b.*").matcher(str.toLowerCase()).find();
		String token;
		
		
		//System.out.println(containToken);
		
		/*System.out.println("---- Split by space ------");
		while (st.hasMoreElements()) {
			token = st2.nextElement();
			if()
		}

		System.out.println("---- Split by comma ',' ------");
		StringTokenizer st2 = new StringTokenizer(str, ",");

		while (st2.hasMoreElements()) {
			System.out.println(st2.nextElement());
		}
		
		
		
		System.out.println(query.contains("java"));*/
		
		List<String> apis = new ArrayList<>();
		apis.add("Message");
		apis.add("Message2");
		//System.out.println(step3(apis, "How do I javass connect to a MongoDB Database?"));
		
		BotUtils botUtils = new BotUtils();
		botUtils.initializeConfigs();
		
		String another1PreCodeAndCode = "<p>I have this field:</p>" + 
				"" + 
				"<pre><code>HashMap&lt;String, HashMap&gt; selects = new HashMap&lt;String, HashMap&gt;();" + 
				"</code></pre>" + 
				"" + 
				"<p>For each <code>Hash&lt;String, HashMap&gt;</code> I need to create a <code>ComboBox</code>, whose items are the value (which happens to be a HashMap itself) of <code>HashMap &lt;String, **HashMap**&gt;</code>.</p>" + 
				"" + 
				"<p>By way of (non-functioning) demonstartion:</p>" + 
				"" + 
				"<pre><code>for(int i=0;i&lt;selects.size();i++)" + 
				"{" + 
				"   HashMap h = selects[i].getValue();" + 
				"   ComboBox cb = new ComboBox();" + 
				"   for(int y=0;y&lt;h.size();i++)" + 
				"   {" + 
				"      cb.items.add(h[y].getValue);" + 
				"   }" + 
				"}" + 
				"</code></pre>" + 
				"";
		
		
				String textLink = "<span class=\"comment-copy\">In Java 8 using Lambda Expression: <a href=\"http://stackoverflow.com/a/25616206/1503859\">stackoverflow.com/a/25616206/1503859</a></span>";
		
		String text2 = "<p>Still in my learning phase and either I misunderstand the implements keyword.  Am at the chapter where the app's MainActivity is supposed to handle all click related functions.</p>" + 
				"" + 
				"<p>I don't know if it's the book missing something, or I misread something in the book.  But when I debug the app I never get the OnClick call. Otherwise the app runs with no errors.</p>" + 
				"" + 
				"<p>While I already know how to assign a new button listener am stuck on this chapter and would love to be able to move on.</p>" + 
				"" + 
				"<p>Here's the entire code, it's very short.</p>" + 
				"" + 
				"<p>Would appreciate knowing what I am missing here.</p>" + 
				"" + 
				"<p>Thank you</p>" + 
				"" + 
				"<pre><code>package ted.com.eventhandling;" + 
				"" + 
				"import android.os.Bundle;" + 
				"import android.support.annotation.Nullable;" + 
				"import android.support.v7.app.AppCompatActivity;" + 
				"import android.util.Log;" + 
				"import android.view.View;" + 
				"import android.widget.Toast;" + 
				"" + 
				"" + 
				"public class MainActivity2 extends AppCompatActivity" + 
				"        implements View.OnClickListener" + 
				"{" + 
				"    @Override" + 
				"    protected void onCreate(@Nullable Bundle savedInstanceState)" + 
				"    {" + 
				"        super.onCreate(savedInstanceState);" + 
				"        setContentView(R.layout.activity_main);" + 
				"" + 
				"    }" + 
				"" + 
				"    @Override" + 
				"    public void onClick(View view)" + 
				"    {" + 
				"        switch(view.getId())" + 
				"        {" + 
				"            case R.id.button1:" + 
				"                show(\"Button One\");" + 
				"                break;" + 
				"            case R.id.button2:" + 
				"                show(\"Button Two\");" + 
				"                break;" + 
				"            case R.id.button3:" + 
				"                show(\"Button Three\");" + 
				"                break;" + 
				"            default:" + 
				"                show(\"Oh shit\");" + 
				"                break;" + 
				"        }" + 
				"    }" + 
				"    void show(String message)" + 
				"    {" + 
				"        Toast.makeText(this, message, Toast.LENGTH_LONG).show();" + 
				"        Log.i(getClass().getName(), message);" + 
				"    }" + 
				"" + 
				"" + 
				"}" + 
				"</code></pre>" + 
				""; 
		
		
		/*String allLinks = "";
		List<String> links = botUtils.getCodeValues(BotUtils.LINK_PATTERN, text2);
		
		Set<Integer> soQuestionsIds = new HashSet<>();
		identifyQuestionsIdsFromUrls(links, soQuestionsIds);
		
		
		System.out.println(soQuestionsIds);*/
		
		String title = "How to for each the hashmap? [duplicate]";
		//String[] titleContent = botUtils.separaSomentePalavrasNaoSomentePalavras(title,"title");
		
		HashSet soLinksIds = new HashSet<>(); 
		
		//List<String> links = botUtils.getCodeValues(BotUtils.LINK_PATTERN, textLink);
		//identifyQuestionsIdsFromUrls(links,soLinksIds);
		//System.out.println(soLinksIds);
		
		//System.out.println(links);
		
		/*String[] bodyContent = botUtils.separateWordsCodePerformStemmingStopWords(text2,"body");
		System.out.println(bodyContent[0]);
		System.out.println(bodyContent[1]);
		System.out.println(bodyContent[2]);*/
		
		
		String linkBlockquoteCode = "<p>I was reading <a href=\"http://www.ibm.com/developerworks/java/library/j-dcl.html\" rel=\"nofollow noreferrer\">this article</a> about \"Double-Checked locking\" and out of the main topic of the article I was wondering why at some point of the article the author uses the next Idiom:  </p>\n" + 
				"\n" + 
				"<blockquote>\n" + 
				"  <p>Listing 7. Attempting to solve the out-of-order write problem  </p>\n" + 
				"\n" + 
				"<pre><code>public static Singleton getInstance()  \n" + 
				"{\n" + 
				"    if (instance == null)\n" + 
				"    {\n" + 
				"        synchronized(Singleton.class) {      //1\n" + 
				"            Singleton inst = instance;         //2\n" + 
				"            if (inst == null)\n" + 
				"            {\n" + 
				"                synchronized(Singleton.class) {  //3\n" + 
				"                    inst = new Singleton();        //4\n" + 
				"                }\n" + 
				"                instance = inst;                 //5\n" + 
				"            }\n" + 
				"        }\n" + 
				"    }\n" + 
				"    return instance;\n" + 
				"}\n" + 
				"</code></pre>\n" + 
				"</blockquote>\n" + 
				"\n" + 
				"<p>And my question is: \n" + 
				"Is there any reason to synchronize twice some code with the same lock?\n" + 
				"Have this any purpose it?</p>\n" + 
				"\n" + 
				"<p>Many thanks in advance.</p>\n" + 
				"";
		
		
		String linkText = "<p>I have created my own Tree implementation for <a href=\"https://stackoverflow.com/questions/144642/tree-directed-acyclic-graph-implementation\">various reasons</a> and have come up with two classes, a 'base' class that is a generic tree node that is chock full of logic and another class that extends that one which is more specialised.</p>\n" + 
				"\n" + 
				"<p>In my base class certain methods involve instantiating new tree nodes (e.g. adding children). These instantations are inside logic (in a nested loop, say) which makes the logic hard to separate from the instantation.</p>\n" + 
				"\n" + 
				"<p>So, if I don't override these instantations in the specific class the wrong type of node will be created. However, I don't <em>want</em> to override those methods because they also contained shared logic that shouldn't be duplicated!</p>\n" + 
				"\n" + 
				"<p>The problem can be boiled down to this:</p>\n" + 
				"\n" + 
				"<pre><code>public class Foo {\n" + 
				"    public String value() { return \"foo\"; }\n" + 
				"\n" + 
				"    public Foo doStuff() {\n" + 
				"        // Logic logic logic..\n" + 
				"        return new Foo();\n" + 
				"    }\n" + 
				"}\n" + 
				"\n" + 
				"class Bar extends Foo {\n" + 
				"    public String value() { return \"bar\"; } \n" + 
				"}\n" + 
				"\n" + 
				"new Bar().doStuff().value(); // returns 'foo', we want 'bar'\n" + 
				"</code></pre>\n" + 
				"\n" + 
				"<p>The first thing that popped into my head would have a 'create hook' that extending classes could override:</p>\n" + 
				"\n" + 
				"<pre><code>public Foo createFooHook(/* required parameters */) {\n" + 
				"  return new Foo();\n" + 
				"}\n" + 
				"</code></pre>\n" + 
				"\n" + 
				"<p>Now. while it was a fine first thought, there is a stench coming off that code something awful. There is something very... <em>wrong</em> about it. </p>\n" + 
				"\n" + 
				"<p>It's like cooking while naked-- it feels dangerous and <em>unnecessary</em>.</p>\n" + 
				"\n" + 
				"<p>So, <strong>how would you deal with this situation?</strong></p>";
		
		linkText+="\n\n\n"+linkText;
		
		//String linkExtracted = botUtils.extractLinksTargets(linkBlockquoteCode);
		
		
		//System.out.println(linkExtracted);
		//String onlyLink = botUtils.getCodeValues(patter, str)
		
		String withStrong = "<p>I wonder if anyone uses commercial/free java obfuscators on his own commercial product. I know only about one project that actually had an obfuscating step in the ant build step for releases.</p>\n" + 
				"\n" + 
				"<p>Do you obfuscate? And if so, why do you obfuscate?</p>\n" + 
				"\n" + 
				"<p>Is it really a way to protect the code or is it just a better feeling for the developers/managers?</p>\n" + 
				"\n" + 
				"<p><strong>edit:</strong> Ok, I to be exact about my point: Do you obfuscate to protect your IP (your algorithms, the work you've put into your product)? I won't obfuscate for security reasons, that doesn't feel right. So I'm only talking about protecting your applications code against competitors.</p>\n" + 
				"\n" + 
				"<p><a href=\"https://stackoverflow.com/users/988/staffan\">@staffan</a> has a good point:</p>\n" + 
				"\n" + 
				"<blockquote>\n" + 
				"  <p>The reason to stay away from chaining code flow is that some of those changes makes it impossible for the JVM to efficiently optimize the code. In effect it will actually degrade the performance of your application.</p>\n" + 
				"</blockquote>\n" + 
				"";
		
		String another = "<p>After reading <a href=\"https://stackoverflow.com/questions/9033/hidden-features-of-c\">Hidden Features of C#</a> I wondered, What are some of the hidden features of Java?</p>";
		
		String another2 = "<p>I am having a strange DB2 issue when I run DBUnit tests.  My DBUnit tests are highly customized, but I don't think it is the issue.  When I run the tests, I get a failure: </p>\n" + 
				"\n" + 
				"<blockquote>\n" + 
				"  <p>SQLCODE: -1084, SQLSTATE: 57019</p>\n" + 
				"</blockquote>\n" + 
				"\n" + 
				"<p><a href=\"https://www1.columbia.edu/sec/acis/db2/db2m0/sql1000.htm\" rel=\"nofollow noreferrer\">which translates to</a> </p>\n" + 
				"\n" + 
				"<blockquote>\n" + 
				"  <p>SQL1084C Shared memory segments cannot be allocated.</p>\n" + 
				"</blockquote>\n" + 
				"\n" + 
				"<p>It sounds like a weird memory issue, though here's the big strange thing.  If I ssh to the test database server, then go in to db2 and do \"connect to MY_DB\", the tests start succeeding!  This seems to have no relation to the supposed memory error that is being reported.</p>\n" + 
				"\n" + 
				"<p>I have 2 tests, and the first one actually succeeds, the second one is the one that fails.  However, it fails in the DBUnit setup code, when it is obtaining the connection to the DB server to load my xml dataset.</p>\n" + 
				"\n" + 
				"<p>Any ideas what might be going on?</p>\n" + 
				"";
		
		
		String another3codes= "<p>So, I'm getting some compile errors on netbeans 6.5 generated web service code for a java ME client to a c# (vs2005) web service.  I've trimmed my example significantly, and it still shows the problem, and not being able to return a collection of things is pretty much a deal-breaker.  </p>\n" + 
				"\n" + 
				"<p>c# web service (SimpleWebService.asmx)</p>\n" + 
				"\n" + 
				"<pre><code>&lt;%@ WebService Language=\"C#\" Class=\"SimpleWebService\" %&gt;\n" + 
				"\n" + 
				"using System;\n" + 
				"using System.Web;\n" + 
				"using System.Web.Services;\n" + 
				"using System.Web.Services.Protocols;\n" + 
				"\n" + 
				"[WebService(Namespace = \"http://sphereinabox.com/\")]\n" + 
				"[WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]\n" + 
				"public class SimpleWebService  : System.Web.Services.WebService {\n" + 
				"\n" + 
				"    [WebMethod]\n" + 
				"    public CustomType[] GetSomething() {\n" + 
				"        return new CustomType[] {new CustomType(\"hi\"), new CustomType(\"bye\")};\n" + 
				"    }\n" + 
				"    public class CustomType {\n" + 
				"        public string Name;\n" + 
				"        public CustomType(string _name) {\n" + 
				"            Name = _name;\n" + 
				"        }\n" + 
				"        public CustomType() {\n" + 
				"        }\n" + 
				"    }\n" + 
				"}\n" + 
				"</code></pre>\n" + 
				"\n" + 
				"<p>WSDL (automatically generated by vs2005):</p>\n" + 
				"\n" + 
				"<pre><code>&lt;?xml version=\"1.0\" encoding=\"utf-8\"?&gt;\n" + 
				"&lt;wsdl:definitions xmlns:s=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://schemas.xmlsoap.org/wsdl/soap12/\" xmlns:mime=\"http://schemas.xmlsoap.org/wsdl/mime/\" xmlns:tns=\"http://sphereinabox.com/\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:tm=\"http://microsoft.com/wsdl/mime/textMatching/\" xmlns:http=\"http://schemas.xmlsoap.org/wsdl/http/\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" targetNamespace=\"http://sphereinabox.com/\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"&gt;\n" + 
				"  &lt;wsdl:types&gt;\n" + 
				"    &lt;s:schema elementFormDefault=\"qualified\" targetNamespace=\"http://sphereinabox.com/\"&gt;\n" + 
				"      &lt;s:element name=\"GetSomething\"&gt;\n" + 
				"        &lt;s:complexType /&gt;\n" + 
				"      &lt;/s:element&gt;\n" + 
				"      &lt;s:element name=\"GetSomethingResponse\"&gt;\n" + 
				"        &lt;s:complexType&gt;\n" + 
				"          &lt;s:sequence&gt;\n" + 
				"            &lt;s:element minOccurs=\"0\" maxOccurs=\"1\" name=\"GetSomethingResult\" type=\"tns:ArrayOfCustomType\" /&gt;\n" + 
				"          &lt;/s:sequence&gt;\n" + 
				"        &lt;/s:complexType&gt;\n" + 
				"      &lt;/s:element&gt;\n" + 
				"      &lt;s:complexType name=\"ArrayOfCustomType\"&gt;\n" + 
				"        &lt;s:sequence&gt;\n" + 
				"          &lt;s:element minOccurs=\"0\" maxOccurs=\"unbounded\" name=\"CustomType\" nillable=\"true\" type=\"tns:CustomType\" /&gt;\n" + 
				"        &lt;/s:sequence&gt;\n" + 
				"      &lt;/s:complexType&gt;\n" + 
				"      &lt;s:complexType name=\"CustomType\"&gt;\n" + 
				"        &lt;s:sequence&gt;\n" + 
				"          &lt;s:element minOccurs=\"0\" maxOccurs=\"1\" name=\"Name\" type=\"s:string\" /&gt;\n" + 
				"        &lt;/s:sequence&gt;\n" + 
				"      &lt;/s:complexType&gt;\n" + 
				"    &lt;/s:schema&gt;\n" + 
				"  &lt;/wsdl:types&gt;\n" + 
				"  &lt;wsdl:message name=\"GetSomethingSoapIn\"&gt;\n" + 
				"    &lt;wsdl:part name=\"parameters\" element=\"tns:GetSomething\" /&gt;\n" + 
				"  &lt;/wsdl:message&gt;\n" + 
				"  &lt;wsdl:message name=\"GetSomethingSoapOut\"&gt;\n" + 
				"    &lt;wsdl:part name=\"parameters\" element=\"tns:GetSomethingResponse\" /&gt;\n" + 
				"  &lt;/wsdl:message&gt;\n" + 
				"  &lt;wsdl:portType name=\"SimpleWebServiceSoap\"&gt;\n" + 
				"    &lt;wsdl:operation name=\"GetSomething\"&gt;\n" + 
				"      &lt;wsdl:input message=\"tns:GetSomethingSoapIn\" /&gt;\n" + 
				"      &lt;wsdl:output message=\"tns:GetSomethingSoapOut\" /&gt;\n" + 
				"    &lt;/wsdl:operation&gt;\n" + 
				"  &lt;/wsdl:portType&gt;\n" + 
				"  &lt;wsdl:binding name=\"SimpleWebServiceSoap\" type=\"tns:SimpleWebServiceSoap\"&gt;\n" + 
				"    &lt;soap:binding transport=\"http://schemas.xmlsoap.org/soap/http\" /&gt;\n" + 
				"    &lt;wsdl:operation name=\"GetSomething\"&gt;\n" + 
				"      &lt;soap:operation soapAction=\"http://sphereinabox.com/GetSomething\" style=\"document\" /&gt;\n" + 
				"      &lt;wsdl:input&gt;\n" + 
				"        &lt;soap:body use=\"literal\" /&gt;\n" + 
				"      &lt;/wsdl:input&gt;\n" + 
				"      &lt;wsdl:output&gt;\n" + 
				"        &lt;soap:body use=\"literal\" /&gt;\n" + 
				"      &lt;/wsdl:output&gt;\n" + 
				"    &lt;/wsdl:operation&gt;\n" + 
				"  &lt;/wsdl:binding&gt;\n" + 
				"  &lt;wsdl:binding name=\"SimpleWebServiceSoap12\" type=\"tns:SimpleWebServiceSoap\"&gt;\n" + 
				"    &lt;soap12:binding transport=\"http://schemas.xmlsoap.org/soap/http\" /&gt;\n" + 
				"    &lt;wsdl:operation name=\"GetSomething\"&gt;\n" + 
				"      &lt;soap12:operation soapAction=\"http://sphereinabox.com/GetSomething\" style=\"document\" /&gt;\n" + 
				"      &lt;wsdl:input&gt;\n" + 
				"        &lt;soap12:body use=\"literal\" /&gt;\n" + 
				"      &lt;/wsdl:input&gt;\n" + 
				"      &lt;wsdl:output&gt;\n" + 
				"        &lt;soap12:body use=\"literal\" /&gt;\n" + 
				"      &lt;/wsdl:output&gt;\n" + 
				"    &lt;/wsdl:operation&gt;\n" + 
				"  &lt;/wsdl:binding&gt;\n" + 
				"  &lt;wsdl:service name=\"SimpleWebService\"&gt;\n" + 
				"    &lt;wsdl:port name=\"SimpleWebServiceSoap\" binding=\"tns:SimpleWebServiceSoap\"&gt;\n" + 
				"      &lt;soap:address location=\"http://localhost/SimpleWebService/SimpleWebService.asmx\" /&gt;\n" + 
				"    &lt;/wsdl:port&gt;\n" + 
				"    &lt;wsdl:port name=\"SimpleWebServiceSoap12\" binding=\"tns:SimpleWebServiceSoap12\"&gt;\n" + 
				"      &lt;soap12:address location=\"http://localhost/SimpleWebService/SimpleWebService.asmx\" /&gt;\n" + 
				"    &lt;/wsdl:port&gt;\n" + 
				"  &lt;/wsdl:service&gt;\n" + 
				"&lt;/wsdl:definitions&gt;\n" + 
				"</code></pre>\n" + 
				"\n" + 
				"<p>Generated (netbeans) code that fails to compile, this was created going through the \"Add -> New JavaME to Web Services Client\" wizard.  (SimpleWebService_Stub.java)</p>\n" + 
				"\n" + 
				"<pre><code>    public ArrayOfCustomType GetSomething() throws java.rmi.RemoteException {\n" + 
				"        Object inputObject[] = new Object[] {\n" + 
				"        };\n" + 
				"\n" + 
				"        Operation op = Operation.newInstance( _qname_operation_GetSomething, _type_GetSomething, _type_GetSomethingResponse );\n" + 
				"        _prepOperation( op );\n" + 
				"        op.setProperty( Operation.SOAPACTION_URI_PROPERTY, \"http://sphereinabox.com/GetSomething\" );\n" + 
				"        Object resultObj;\n" + 
				"        try {\n" + 
				"            resultObj = op.invoke( inputObject );\n" + 
				"        } catch( JAXRPCException e ) {\n" + 
				"            Throwable cause = e.getLinkedCause();\n" + 
				"            if( cause instanceof java.rmi.RemoteException ) {\n" + 
				"                throw (java.rmi.RemoteException) cause;\n" + 
				"            }\n" + 
				"            throw e;\n" + 
				"        }\n" + 
				"\n" + 
				"//////// Error on next line, symbol ArrayOfCustomType_fromObject not defined\n" + 
				"        return ArrayOfCustomType_fromObject((Object[])((Object[]) resultObj)[0]);\n" + 
				"    }\n" + 
				"</code></pre>\n" + 
				"\n" + 
				"<p>it turns out with this contrived example (the \"CustomType\" in my production problem has more than one field) I also get errors from this fun code in the same generated (SimpleWebService_Stub.java) generated code.  The errors are that string isn't defined (it's String in java, and besides I think this should be talking about CustomType anyway).</p>\n" + 
				"\n" + 
				"<pre><code>private static string string_fromObject( Object obj[] ) {\n" + 
				"    if(obj == null) return null;\n" + 
				"    string result = new string();\n" + 
				"    return result;\n" + 
				"}\n" + 
				"</code></pre>\n" + 
				"";
		
		String another4= "<p>I need to calculate <code>Math.exp()</code> from java very frequently, is it possible to get a native version to run faster than <strong>java</strong>'s <code>Math.exp()</code>??</p>\n" + 
				"\n" + 
				"<p>I tried just jni + C, but it's slower than just plain <strong>java</strong>.</p>";
		
		String another5 = "<p>It is possible, with PHP 5 the two current options seem to be use <a href=\"http://www.zend.com/en/products/server-ce/\" rel=\"nofollow\">Zend Server CE</a> as your PHP web server.  ZSCE supports Java connectivity when the <a href=\"http://files.zend.com/help/Zend-Server-Community-Edition/zend-server-community-edition.htm#working_with_the_java_bridge.htm\" rel=\"nofollow\">Java Bridge</a> is enabled.</p>\n" + 
				"\n" + 
				"<p>The other option appears to be <a href=\"http://php-java-bridge.sourceforge.net/pjb/\" rel=\"nofollow\">PHP/Java Bridge</a> which is a SourceForge project.</p>\n" + 
				"";
		
		String another6Code = "<p><code>IDE</code> may not be enough for designing everything (but it helps lot in development), its always better to learn API and IDE both together.</p>";
		
		/*String presentingBody = botUtils.buildPresentationBody(another6Code);
		System.out.println(presentingBody);
		List<String> codes = botUtils.getCodes(presentingBody);
		String processedBodyStemmedStopped = botUtils.buildProcessedBodyStemmedStopped(presentingBody,true);
		System.out.println(processedBodyStemmedStopped);*/
		
		
		testGetClassesNames();
		
	}
	
	/**
	 * 
	 * 
	**/
	private void testGetClassesNames() {
		String code1 = "//Main bucket\n" + 
				"		Bucket mainBucket = new Bucket();\n" + 
				"		mainBucket.setClassesNames(apis);\n" + 
				"		\n" + 
				"		String presentingBody = botUtils.buildPresentationBody(googleQuery);\n" + 
				"		\n" + 
				"		List<String> codes = botUtils.getCodes(presentingBody);\n" + 
				"		mainBucket.setCodes(codes);\n" + 
				"		\n" + 
				"		String processedBodyStemmedStopped = botUtils.buildProcessedBodyStemmedStopped(presentingBody,false);\n" + 
				"		mainBucket.setProcessedBodyStemmedStopped(processedBodyStemmedStopped+apisNames);\n" + 
				"		\n" + 
				"		System.out.println(mainBucket);\n" + 
				"		\n" + 
				"		//Remaining buckets\n" + 
				"		Set<Bucket> buckets = new HashSet<>();\n" + 
				"		\n" + 
				"		/**\n" + 
				"	* sss\n" + 
				"	* aaa\n" + 
				" 	**/ " + 
				"		for(SoThread thread: threads) {\n" + 
				"			\n" + 
				"			List<Post> answers = thread.getAnswers();\n" + 
				"			for(Post answer: answers) {\n" + 
				"				Bucket bucket = buildBucket(answer,true);\n" + 
				"				buckets.add(bucket);\n" + 
				"			}\n" + 
				"		}\n" + 
				"\n" + 
				"\n" + 
				"public class Tester {\n" + 
				"\n" + 
				"	public Tester() throws Exception {\n" + 
				"		String str = \"How do I send an HTML email? javascript javac dd JAVA\";\n" + 
				"		StringTokenizer st = new StringTokenizer(str);\n" + 
				"		Boolean containToken = Pattern.compile(\".*\\\\bjava\\\\b.*\").matcher(str.toLowerCase()).find();\n" + 
				"		String token;\n" + 
				"		}\n" + 
				"}\n" + 
				"\n" + 
				"private Set<ProcessedPostOld> getAllPostsFromSet() {\n" + 
				"		Set<ProcessedPostOld> processedPostsByFilter = new HashSet();\n" + 
				"		\n" + 
				"		logger.info(\"now creating new TOs...\");\n" + 
				"		for(Post post: postsByFilter) {\n" + 
				"			ProcessedPostOld newPost = new ProcessedPostOld();\n" + 
				"			BeanUtils.copyProperties(post,newPost);\n" + 
				"			processedPostsByFilter.add(newPost);\n" + 
				"		}\n" + 
				"		logger.info(\"finished new TOs...\");\n" + 
				"		return processedPostsByFilter;\n" + 
				"	}\n" + 
				"\n" + 
				"";
		
	
		//remove java keywords
		for(String keyword: BotUtils.keywords){
			code1= code1.replaceAll(keyword,"");
		}
		
		//remove double quotes
		code1= code1.replaceAll(BotUtils.DOUBLE_QUOTES_REGEX_EXPRESSION,"");
		/*Pattern pattern0 = Pattern.compile(BotUtils.DOUBLE_QUOTES_REGEX_EXPRESSION);
		Matcher matcher0 = pattern0.matcher(code1);
		while (matcher0.find()) {
			System.out.println(matcher0.group(0));
		}*/
		
		//remove comments
		System.out.println(code1);
		code1 = code1.replaceAll( "//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/", "$1 " );
		System.out.println(code1);
		
		
		//Get classes in camel case
		String regex = "\\b[A-Z][a-z]*([A-Z][a-z]*)*\\b";
		
		Set<String> classes = new HashSet<>();
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(code1);
		while (matcher.find()) {
			classes.add(matcher.group(0));
		}
		
		for(String className: classes) {
			System.out.println(className);
		}
		
		
	}


	private String step3(List<String> apis, String query) {
		String completeQuery = "";
		
		Boolean containJavaToken = Pattern.compile(".*\\bjava\\b.*").matcher(query.toLowerCase()).find();
		
		if(!containJavaToken) {
			completeQuery += "java ";
		}
		completeQuery += query + " "+ apis.get(0) +" "+ apis.get(1);
		
		return completeQuery;
	}
	
	
	public static void main(String[] args) throws Exception {
		Tester t = new Tester();
		
	}

	
	private void identifyQuestionsIdsFromUrls(List<String> urls, Set<Integer> soQuestionsIds) {
		for(String url: urls){
			//String[] urlPart = url.split("\\/[[:digit:]].*\\/");
			Pattern pattern = Pattern.compile("\\/([\\d]+)");
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				soQuestionsIds.add(new Integer(matcher.group(1)));
				System.out.println("Recovering question from URL: "+url);
			}
			//System.out.println(url);
			
		}
		
	}    
	
}
