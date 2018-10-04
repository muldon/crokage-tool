package com.ufu.crokage.to;

public class MetricResult {
	private String hitK10;
	private String hitK5;
	private String hitK1;
	
	private String mrrK10;
	private String mrrK5;
	private String mrrK1;
	
	private String mapK10;
	private String mapK5;
	private String mapK1;
	
	private String mrK10;
	private String mrK5;
	private String mrK1;
	
	
	
	
	public MetricResult() {
		this.hitK10 = "";
		this.hitK5 = "";
		this.hitK1 = "";
		this.mrrK10 = "";
		this.mrrK5 = "";
		this.mrrK1 = "";
		this.mapK10 = "";
		this.mapK5 = "";
		this.mapK1 = "";
		this.mrK10 = "";
		this.mrK5 = "";
		this.mrK1 = "";
	}
	public String getHitK10() {
		return hitK10;
	}
	public void setHitK10(String hitK10) {
		this.hitK10 = hitK10;
	}
	public String getHitK5() {
		return hitK5;
	}
	public void setHitK5(String hitK5) {
		this.hitK5 = hitK5;
	}
	public String getHitK1() {
		return hitK1;
	}
	public void setHitK1(String hitK1) {
		this.hitK1 = hitK1;
	}
	public String getMrrK10() {
		return mrrK10;
	}
	public void setMrrK10(String mrrK10) {
		this.mrrK10 = mrrK10;
	}
	public String getMrrK5() {
		return mrrK5;
	}
	public void setMrrK5(String mrrK5) {
		this.mrrK5 = mrrK5;
	}
	public String getMrrK1() {
		return mrrK1;
	}
	public void setMrrK1(String mrrK1) {
		this.mrrK1 = mrrK1;
	}
	public String getMapK10() {
		return mapK10;
	}
	public void setMapK10(String mapK10) {
		this.mapK10 = mapK10;
	}
	public String getMapK5() {
		return mapK5;
	}
	public void setMapK5(String mapK5) {
		this.mapK5 = mapK5;
	}
	public String getMapK1() {
		return mapK1;
	}
	public void setMapK1(String mapK1) {
		this.mapK1 = mapK1;
	}
	public String getMrK10() {
		return mrK10;
	}
	public void setMrK10(String mrK10) {
		this.mrK10 = mrK10;
	}
	public String getMrK5() {
		return mrK5;
	}
	public void setMrK5(String mrK5) {
		this.mrK5 = mrK5;
	}
	public String getMrK1() {
		return mrK1;
	}
	public void setMrK1(String mrK1) {
		this.mrK1 = mrK1;
	}
	@Override
	public String toString() {
		return " & " + hitK10 + " & " + hitK5 + " & " + hitK1 + " & " + mrrK10 + " & " + mrrK5 + " & " + mrrK1 + " & " + mapK10 + " & " + mapK5 + " & "
				+ mapK1 + " & " + mrK10 + " & " + mrK5 + " & " + mrK1 + " \\\\";
	}
	
	
	
	
}
