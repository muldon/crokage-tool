package com.ufu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;



public class SimpleTester {

	public SimpleTester() {

		try {
			
			//String text = "First make use of the method you found in Javadoc for the String class that will chop any string into a number of parts depending on the value of its string argument. Use this method to break up the string referenced by text into an array of words and assign this array to a local variable called wordArray. The method should declare a local variable capable of referencing a set of strings and assign to it an empty instance of a suitable class. Next iterate over wordArray and add its elements to the newly created set and finally return the set which should contain the distinct words that are in the string referenced by text.";
			String text = "the as the kk jj jj the";
			
			
			
	        Set<String> set = Arrays.stream(text.split(" +")).collect(Collectors.toSet());
	        System.out.println(set);
		        
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	
	 private static String loadStream(InputStream s) throws Exception {
	        BufferedReader br = new BufferedReader(new InputStreamReader(s));
	        StringBuilder sb = new StringBuilder();
	        String line;
	        while ((line = br.readLine()) != null)
	            sb.append(line).append("\n");
	        return sb.toString();
	    }
		

	public static void main(String[] args) {
		SimpleTester t = new SimpleTester();
	}
}
