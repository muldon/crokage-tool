package com.ufu.crokage.to;

import java.sql.Timestamp;
import java.util.Calendar;

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
	@Column(name="hitk")
	private Double hitK;
	
	
	@Column(name="mrrk")
	private Double mrrK;
	

	@Column(name="mapk")
	private Double mapK;
	
		
	@Column(name="mrk")
	private Double mrK;
	
	
	
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
	
	@Column(name="bm25weight")
	public Double bm25weight;
	
	public String approach;
	
	@Column(name="topclasses")
	private Integer topClasses;
	
	private Timestamp date;
	
	public MetricResult(String approach,Integer bm25TopNSmallResults,Integer bm25TopNBigResults, Integer topApisScoredPairsPercent, Integer topSimilarTitlesPercent, Integer cutoff, Integer topk, String obs,Integer numberofapiclasses) {
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
		this.date = new Timestamp(Calendar.getInstance().getTimeInMillis());
		
	}
	public MetricResult() {
		super();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Double getHitK() {
		return hitK;
	}
	public void setHitK(Double hitK) {
		this.hitK = hitK;
	}
	public Double getMrrK() {
		return mrrK;
	}
	public void setMrrK(Double mrrK) {
		this.mrrK = mrrK;
	}
	public Double getMapK() {
		return mapK;
	}
	public void setMapK(Double mapK) {
		this.mapK = mapK;
	}
	public Double getMrK() {
		return mrK;
	}
	public void setMrK(Double mrK) {
		this.mrK = mrK;
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
	public Integer getTopApisScoredPairsPercent() {
		return topApisScoredPairsPercent;
	}
	public void setTopApisScoredPairsPercent(Integer topApisScoredPairsPercent) {
		this.topApisScoredPairsPercent = topApisScoredPairsPercent;
	}
	public Integer getTopSimilarContentsAsymRelevanceNumber() {
		return topSimilarContentsAsymRelevanceNumber;
	}
	public void setTopSimilarContentsAsymRelevanceNumber(Integer topSimilarContentsAsymRelevanceNumber) {
		this.topSimilarContentsAsymRelevanceNumber = topSimilarContentsAsymRelevanceNumber;
	}
	public Integer getCutoff() {
		return cutoff;
	}
	public void setCutoff(Integer cutoff) {
		this.cutoff = cutoff;
	}
	public Integer getTopk() {
		return topk;
	}
	public void setTopk(Integer topk) {
		this.topk = topk;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	public Double getClassFreqWeight() {
		return classFreqWeight;
	}
	public void setClassFreqWeight(Double classFreqWeight) {
		this.classFreqWeight = classFreqWeight;
	}
	public Double getMethodFreqWeight() {
		return methodFreqWeight;
	}
	public void setMethodFreqWeight(Double methodFreqWeight) {
		this.methodFreqWeight = methodFreqWeight;
	}
	public Double getRepWeight() {
		return repWeight;
	}
	public void setRepWeight(Double repWeight) {
		this.repWeight = repWeight;
	}
	public Double getSimWeight() {
		return simWeight;
	}
	public void setSimWeight(Double simWeight) {
		this.simWeight = simWeight;
	}
	public Double getUpWeight() {
		return upWeight;
	}
	public void setUpWeight(Double upWeight) {
		this.upWeight = upWeight;
	}
	public Double getTfIdfCosSimWeight() {
		return tfIdfCosSimWeight;
	}
	public void setTfIdfCosSimWeight(Double tfIdfCosSimWeight) {
		this.tfIdfCosSimWeight = tfIdfCosSimWeight;
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
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public Double getBm25weight() {
		return bm25weight;
	}
	public void setBm25weight(Double bm25weight) {
		this.bm25weight = bm25weight;
	}
	
	
	
	
	
	
}
