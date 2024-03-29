package com.ufu;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;


public class BasicPipelineExample2 {

  public static String text = "Joe Smith was born in California. " +
      "In 2017, he went to Paris, France in the summer. " +
      "His flight left at 3:00pm on July 10th, 2017. " +
      "After eating some escargot for the first time, Joe said, \"That was delicious!\" " +
      "He sent a postcard to his sister Jane Smith. " +
      "He is ok. " +
      "Simple, right? Remove element at given index, insert it at desired index. Let's see if it works for the second test case."+
      "He is ok to go now. " +
      "After hearing about Joe's trip, Jane decided she might go to France one day.";

  public static void main(String[] args) {
    // set up pipeline properties
    Properties props = new Properties();
    // set the list of annotators to run
    props.setProperty("annotators", "tokenize,ssplit,pos,parse");
    // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
    props.setProperty("coref.algorithm", "neural");
    // build pipeline
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    // create a document object
    CoreDocument document = new CoreDocument(text);
    // annnotate the document
    pipeline.annotate(document);
    // examples

    // 10th token of the document
    CoreLabel token = document.tokens().get(10);
    System.out.println("Example: token");
    System.out.println(token);
    System.out.println();

    // text of the first sentence
    String sentenceText = document.sentences().get(0).text();
    System.out.println("Example: sentence");
    System.out.println(sentenceText);
    System.out.println();

    // second sentence
    CoreSentence sentence = document.sentences().get(0);
    
    // list of the part-of-speech tags for the second sentence
    List<String> posTags = sentence.posTags();
    System.out.println("Example: pos tags");
    System.out.println(posTags);
    System.out.println();
    
    Tree constituencyParse = sentence.constituencyParse();
    System.out.println("Example: constituency parse");
    System.out.println(constituencyParse);
    System.out.println();

    List<CoreSentence> sentences = document.sentences();
    List<CoreSentence> selectedSentences = new ArrayList<>();
    List<CoreSentence> removedSentences = new ArrayList<>();

	TregexPattern pattern1 = TregexPattern.compile("VP << (NP < /NN.?/) < /VB.?/");
	TregexPattern pattern2 = TregexPattern.compile("NP ! < PRP [<< VP | $ VP]");
	// Run the pattern on one particular tree
	
    
    
    for(CoreSentence eachSentence: sentences) {
    	System.out.println("Sentence: "+eachSentence);
    	
    	posTags = eachSentence.posTags();
    	System.out.println("POS: "+posTags);
        
    	Tree constituencyParseTree = eachSentence.constituencyParse();
    	System.out.println("Example: constituency parse");
    	System.out.println(constituencyParseTree);
    	
    	TregexMatcher matcher1 = pattern1.matcher(constituencyParseTree);
    	TregexMatcher matcher2 = pattern2.matcher(constituencyParseTree);
    	
    	// Iterate over all of the subtrees that matched
    	if (!matcher1.findNextMatchingNode() && !matcher2.findNextMatchingNode()) {
    		removedSentences.add(eachSentence);
    	}
    	
    	//constituencyParseTree.con
    	
    	/*if(constituencyParseTree.contains("VP") && constituencyParseTree.contains("NP")) {
    		selectedSentences.add(eachSentence);
    	}else {
    		removedSentences.add(eachSentence);
    	}*/
    	
    }
    
    System.out.println("Removed sentences:");
    for(CoreSentence eachSentence: removedSentences) {
    	System.out.println("Sentence: "+eachSentence);
    	text = text.replace(eachSentence.text(), "");
    }
    
    System.out.println("Remaining text:"+text);
    
    
    
    /*
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
    for(CoreMap sentence: sentences) {
    	  // traversing the words in the current sentence
    	  // a CoreLabel is a CoreMap with additional token-specific methods
    	  for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
    	    // this is the text of the token
    	    String word = token.get(TextAnnotation.class);
    	    // this is the POS tag of the token
    	    String pos = token.get(PartOfSpeechAnnotation.class);
    	    // this is the NER label of the token
    	    String ne = token.get(NamedEntityTagAnnotation.class);
    	  }

    	  // this is the parse tree of the current sentence
    	  Tree tree = sentence.get(TreeAnnotation.class);

    	  // this is the Stanford dependency graph of the current sentence
    	  SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
    	}
  */
  }

}
