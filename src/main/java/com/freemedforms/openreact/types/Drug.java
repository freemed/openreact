package com.freemedforms.openreact.types;

import java.io.Serializable;

public class Drug implements Serializable {

	private static final long serialVersionUID = 2786066280795042789L;

	private long drugId;
	private String drugCode;
	private CodeSet codeSet;
	private Boolean ignoreForInteraction;
	private String route;
	private String drugName;
	private ActingDuration actingDuration;

	public String getDrugCode() {
		return drugCode;
	}

	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
	}

	public CodeSet getCodeSet() {
		return codeSet;
	}

	public void setCodeSet(CodeSet codeSet) {
		this.codeSet = codeSet;
	}

	public void setIgnoreForInteraction(Boolean ignoreForInteraction) {
		this.ignoreForInteraction = ignoreForInteraction;
	}

	public Boolean getIgnoreForInteraction() {
		return ignoreForInteraction;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getRoute() {
		return route;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugId(long drugId) {
		this.drugId = drugId;
	}

	public long getDrugId() {
		return drugId;
	}

	public void setActingDuration(ActingDuration actingDuration) {
		this.actingDuration = actingDuration;
	}

	public ActingDuration getActingDuration() {
		return actingDuration;
	}

}
