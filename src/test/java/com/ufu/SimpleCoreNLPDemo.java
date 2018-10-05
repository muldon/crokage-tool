package com.ufu;

import java.util.List;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class SimpleCoreNLPDemo {
    public static void main(String[] args) { 
        // Create a document. No computation is done yet.
    	String text = "Joe Smith was born in California. " +
    		      "In 2017, he went to Paris, France in the summer. " +
    		      "His flight left at 3:00pm on July 10th, 2017. " +
    		      "After eating some escargot for the first time, Joe said, \"That was delicious!\" " +
    		      "He sent a postcard to his sister Jane Smith. " +
    		      "After hearing about Joe's trip, Jane decided she might go to France one day.";
    	
        Document doc = new Document(text);
        
        for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
            // We're only asking for words -- no need to load any models yet
          //  System.out.println("The second word of the sentence '" + sent + "' is " + sent.word(1));
            // When we ask for the lemma, it will load and run the part of speech tagger
          //  System.out.println("The third lemma of the sentence '" + sent + "' is " + sent.lemma(2));
            // When we ask for the parse, it will load and run the parser
          //  System.out.println("The parse of the sentence '" + sent + "' is " + sent.parse());
            // ...
            
           // List<String> lemmas = sent.lemmas();
           // System.out.println("Lemmas: "+lemmas);
        }
        
        
        
        
    }
    
    
    
}
