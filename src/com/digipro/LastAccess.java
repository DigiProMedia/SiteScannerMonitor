package com.digipro;

import java.util.Calendar;

public class LastAccess {
	private Calendar modified;
	private int currentScanCount;
	private String scanType;

	public String getScanType() {
		return scanType;
	}

	public void setScanType(String scanType) {
		this.scanType = scanType;
	}

	public Calendar getModified() {
		return modified;
	}

	public void setModified(Calendar modified) {
		this.modified = modified;
	}

	public int getCurrentScanCount() {
		return currentScanCount;
	}

	public void setCurrentScanCount(int currentScanCount) {
		this.currentScanCount = currentScanCount;
	}

}
