package com.freemedforms.openreact.types;

public enum CodeSet {

	EN_CA("EN_CA", "en_CA"), EN_US("EN_US", "en_US"), EN_ZA("EN_ZA", "en_ZA"), FR_FR(
			"FR_FR", "fr_FR");

	private final String value;
	private final String lang;

	private CodeSet(String value, String lang) {
		this.value = value;
		this.lang = lang;
	}

	public String getValue() {
		return this.value;
	}

	public String getLang() {
		return this.lang;
	}

	public String toString() {
		return this.value;
	}

}
