package com.ufu;

public class SimpleTester {

	public SimpleTester() {
		int j=1;
		int count = 1;
		while(j<240) {
			System.out.println("count: "+count+" - "+j);
			j = j+7;
			count++;
		}
		
		
		
	}
	
	public static void main(String[] args) {
		SimpleTester t = new SimpleTester();
	}
}
