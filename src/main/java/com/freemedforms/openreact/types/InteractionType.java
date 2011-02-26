package com.freemedforms.openreact.types;

public enum InteractionType {

	I("I"), P("P"), T("T"), D("D"), C("C"), Y("Y");

	private final String value;

	private InteractionType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public String toString() {
		return this.value;
	}

}
