package com.digipro;

public enum ScanType {
	FULL("scopeSite"), BASIC("scopePage"), BASIC_FOLDER("scopeFolder");

	private String scanScope;

	private ScanType(String scanScope) {
		this.scanScope = scanScope;
	}

	public String getScanScope() {
		return scanScope;
	}

}
