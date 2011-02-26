package com.freemedforms.openreact.types;

import java.io.Serializable;

public class DrugInteraction implements Serializable {

	private static final long serialVersionUID = -8683381440720829529L;

	private long id;
	private Drug drug1;
	private Drug drug2;
	private String atc1;
	private String atc2;
	private InteractionType level;
	private String webLink;
	private String risk;

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setDrug1(Drug drug1) {
		this.drug1 = drug1;
	}

	public Drug getDrug1() {
		return drug1;
	}

	public void setDrug2(Drug drug2) {
		this.drug2 = drug2;
	}

	public Drug getDrug2() {
		return drug2;
	}

	public void setAtc1(String atc1) {
		this.atc1 = atc1;
	}

	public String getAtc1() {
		return atc1;
	}

	public void setAtc2(String atc2) {
		this.atc2 = atc2;
	}

	public String getAtc2() {
		return atc2;
	}

	public void setLevel(InteractionType level) {
		this.level = level;
	}

	public InteractionType getLevel() {
		return level;
	}

	public void setWebLink(String webLink) {
		this.webLink = webLink;
	}

	public String getWebLink() {
		return webLink;
	}

	public void setRisk(String risk) {
		this.risk = risk;
	}

	public String getRisk() {
		return risk;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("DrugInteraction{").append("id=")
				.append(id).append(",drug1=").append(drug1).append(",drug2=")
				.append(drug2).append(",atc1=").append(atc1).append(",atc2=")
				.append(atc2).append(",level=").append(level).append(
						",webLink=").append(webLink).append("}").toString();
	}

}
