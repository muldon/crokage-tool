package com.ufu.crokage.to;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "metricsresults")
public class MetricResult {
	@Id
    @SequenceGenerator(name="metricsresults_id_seq", sequenceName="metricsresults_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="metricsresults_id_seq")
    private Integer id;
	@Column(name="hitk10")
	private Double hitK10;
	
	@Column(name="hitk5")
	private Double hitK5;
	
	@Column(name="hitk1")
	private Double hitK1;
	
	@Column(name="mrrk10")
	private Double mrrK10;
	
	@Column(name="mrrk5")
	private Double mrrK5;
	
	@Column(name="mrrk1")
	private Double mrrK1;
	
	@Column(name="mapk10")
	private Double mapK10;
	
	@Column(name="mapk5")
	private Double mapK5;
	
	@Column(name="mapk1")
	private Double mapK1;
	
	@Column(name="mrk10")
	private Double mrK10;
	
	@Column(name="mrk5")
	private Double mrK5;
	
	@Column(name="mrk1")
	private Double mrK1;
	
	@Column(name="bm25topnsmallresults")
	private Integer bm25TopNSmallResults;
	
	@Column(name="bm25topnbigresults")
	private Integer bm25TopNBigResults;
	
	@Column(name="topapisscoredpairspercent")
	private Integer topApisScoredPairsPercent;
	
	@Column(name="topsimilarcontentsasymrelevancenumber")
	private Integer topSimilarContentsAsymRelevanceNumber;
	
	private Integer cutoff;
	
	private Integer topk;
	
	private String obs;
	
	@Column(name="classfreqweight")
	public Double classFreqWeight;
	
	@Column(name="methodfreqweight")
	public Double methodFreqWeight;
	
	@Column(name="repweight")
	public Double repWeight;
	
	@Column(name="simweight")
	public Double simWeight;
	
	@Column(name="upweight")
	public Double upWeight;
	
	@Column(name="tfidfcossimweight")
	public Double tfIdfCosSimWeight;
	
	public String approach;
	
	@Column(name="topclasses")
	private Integer topClasses;
	
	public Double getHitK10() {
		return hitK10;
	}
	public void setHitK10(Double hitK10) {
		this.hitK10 = hitK10;
	}
	public Double getHitK5() {
		return hitK5;
	}
	public void setHitK5(Double hitK5) {
		this.hitK5 = hitK5;
	}
	public Double getHitK1() {
		return hitK1;
	}
	public void setHitK1(Double hitK1) {
		this.hitK1 = hitK1;
	}
	public Double getMrrK10() {
		return mrrK10;
	}
	public void setMrrK10(Double mrrK10) {
		this.mrrK10 = mrrK10;
	}
	public Double getMrrK5() {
		return mrrK5;
	}
	public void setMrrK5(Double mrrK5) {
		this.mrrK5 = mrrK5;
	}
	public Double getMrrK1() {
		return mrrK1;
	}
	public void setMrrK1(Double mrrK1) {
		this.mrrK1 = mrrK1;
	}
	public Double getMapK10() {
		return mapK10;
	}
	public void setMapK10(Double mapK10) {
		this.mapK10 = mapK10;
	}
	public Double getMapK5() {
		return mapK5;
	}
	public void setMapK5(Double mapK5) {
		this.mapK5 = mapK5;
	}
	public Double getMapK1() {
		return mapK1;
	}
	public void setMapK1(Double mapK1) {
		this.mapK1 = mapK1;
	}
	public Double getMrK10() {
		return mrK10;
	}
	public void setMrK10(Double mrK10) {
		this.mrK10 = mrK10;
	}
	public Double getMrK5() {
		return mrK5;
	}
	public void setMrK5(Double mrK5) {
		this.mrK5 = mrK5;
	}
	public Double getMrK1() {
		return mrK1;
	}
	public void setMrK1(Double mrK1) {
		this.mrK1 = mrK1;
	}
	@Override
	public String toString() {
		return " & " + hitK10 + " & " + hitK5 + " & " + hitK1 + " & " + mrrK10 + " & " + mrrK5 + " & " + mrrK1 + " & " + mapK10 + " & " + mapK5 + " & "
				+ mapK1 + " & " + mrK10 + " & " + mrK5 + " & " + mrK1 + " \\\\";
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	public Integer getCutoff() {
		return cutoff;
	}
	public void setCutoff(Integer cutoff) {
		this.cutoff = cutoff;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	public double getClassFreqWeight() {
		return classFreqWeight;
	}
	public void setClassFreqWeight(double classFreqWeight) {
		this.classFreqWeight = classFreqWeight;
	}
	public double getMethodFreqWeight() {
		return methodFreqWeight;
	}
	public void setMethodFreqWeight(double methodFreqWeight) {
		this.methodFreqWeight = methodFreqWeight;
	}
	public double getRepWeight() {
		return repWeight;
	}
	public void setRepWeight(double repWeight) {
		this.repWeight = repWeight;
	}
	public double getSimWeight() {
		return simWeight;
	}
	public void setSimWeight(double simWeight) {
		this.simWeight = simWeight;
	}
	public double getUpWeight() {
		return upWeight;
	}
	public void setUpWeight(double upWeight) {
		this.upWeight = upWeight;
	}
	public String getApproach() {
		return approach;
	}
	public void setApproach(String approach) {
		this.approach = approach;
	}
	public Integer getTopClasses() {
		return topClasses;
	}
	public void setTopClasses(Integer topClasses) {
		this.topClasses = topClasses;
	}
	
	public Integer getTopk() {
		return topk;
	}
	public void setTopk(Integer topk) {
		this.topk = topk;
	}
	
	public MetricResult(String approach,Integer bm25TopNSmallResults,Integer bm25TopNBigResults, Integer topApisScoredPairsPercent, Integer topSimilarTitlesPercent,
			Integer cutoff, Integer topk, String obs,Integer numberofapiclasses) {
		super();
		this.bm25TopNSmallResults = bm25TopNSmallResults;
		this.bm25TopNBigResults = bm25TopNBigResults;
		this.topApisScoredPairsPercent = topApisScoredPairsPercent;
		this.topSimilarContentsAsymRelevanceNumber = topSimilarTitlesPercent;
		this.cutoff = cutoff;
		this.topk = topk;
		this.obs = obs;
		this.approach = approach;
		this.topClasses=numberofapiclasses;
	}
	public MetricResult() {
		super();
	}
	public Double getTfIdfCosSimWeight() {
		return tfIdfCosSimWeight;
	}
	public void setTfIdfCosSimWeight(Double tfIdfCosSimWeight) {
		this.tfIdfCosSimWeight = tfIdfCosSimWeight;
	}
	public Integer getBm25TopNSmallResults() {
		return bm25TopNSmallResults;
	}
	public void setBm25TopNSmallResults(Integer bm25TopNSmallResults) {
		this.bm25TopNSmallResults = bm25TopNSmallResults;
	}
	public Integer getBm25TopNBigResults() {
		return bm25TopNBigResults;
	}
	public void setBm25TopNBigResults(Integer bm25TopNBigResults) {
		this.bm25TopNBigResults = bm25TopNBigResults;
	}
	
	
	
}
