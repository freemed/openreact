package com.freemedforms.openreact.types;

public enum CodeSet {
	
	EN_CA("EN_CA"), EN_US("EN_US"), EN_ZA("EN_ZA"), FR_FR("fr_FR");

	private final String value;

	private CodeSet(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public String toString() {
		return this.value;
	}

}
