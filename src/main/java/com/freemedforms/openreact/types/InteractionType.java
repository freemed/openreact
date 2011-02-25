package com.freemedforms.openreact.types;

public enum InteractionType {

	INFORMATION("I"), USE_CAUTION("P"), TAKE_INTO_ACCOUNT("T"), DEPRECATED("D"), COUNTERINDICATED(
			"C");

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
