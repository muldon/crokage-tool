package com.ufu.crokage.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import config.StaticData;


public class BodyCleaner {

	String orgFolder;
	String normFolderText;
	String normFolderCode;

	public BodyCleaner() {
		this.orgFolder = StaticData.EXP_HOME + "/dataset/answer";
		this.normFolderCode = StaticData.EXP_HOME + "/dataset/answer-norm-code";
		this.normFolderText = StaticData.EXP_HOME + "/dataset/answer-norm-text";
	}

	protected void saveBodyText(String outFile, String content) {
		ContentWriter.writeContent(outFile, content);
	}

	protected void saveBodyCode(String outFile, String content) {
		ContentWriter.writeContent(outFile, content);
	}

	protected static Set<String> extractCode(String postHTML) {
		Document doc = Jsoup.parse(postHTML);
		Elements elems = doc.select("code,pre");
		String codeText = elems.text();
		return new TextNormalizer(codeText).normalizeSimpleCodeDiscardSmall();
	}

	protected static String extractText(String postHTML) {
		Document doc = Jsoup.parse(postHTML);
		doc.select("code,pre").remove();
		return doc.text();
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//new BodyCleaner().cleanTheBody();
	}
}
