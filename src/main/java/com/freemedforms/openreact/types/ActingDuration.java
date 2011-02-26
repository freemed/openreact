package com.freemedforms.openreact.types;

public enum ActingDuration {

	LONG_ACTING("LONG_ACTING"), SHORT_ACTING("SHORT_ACTING"), UNKNOWN_ACTING(
			"UNKNOWN_ACTING"), UNSPECIFIED_DURATION("");

	private final String value;

	private ActingDuration(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public String toString() {
		return this.value;
	}

}
