package com.digipro;

public class ScanReport {
	String scanId;
	String groupName;

	int totalAIssues = 0;
	int totalAAIssues = 0;
	String processError;
	int secondsToProcess;
	int channelId;
	String url;
	String emailAddress;
	String reportUrl;

	boolean internal;
	int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean isInternal() {
		return internal;
	}

	public void setInternal(boolean internal) {
		this.internal = internal;
	}

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public String getScanId() {
		return scanId;
	}

	public void setScanId(String scanId) {
		this.scanId = scanId;
	}

	public int getSecondsToProcess() {
		return secondsToProcess;
	}

	public void setSecondsToProcess(int secondsToProcess) {
		this.secondsToProcess = secondsToProcess;
	}

	public String getProcessError() {
		return processError;
	}

	public void setProcessError(String processError) {
		if (processError != null) {
			if (processError.length() > 300)
				this.processError = processError.substring(0, 299);
			else
				this.processError = processError;
		}
	}

	public int getTotalAIssues() {
		return totalAIssues;
	}

	public void setTotalAIssues(int totalAIssues) {
		this.totalAIssues = totalAIssues;
	}

	public int getTotalAAIssues() {
		return totalAAIssues;
	}

	public void setTotalAAIssues(int totalAAIssues) {
		this.totalAAIssues = totalAAIssues;
	}

	public void incrementIssueCount(int priority, int issues) {
		if (priority == 1)
			totalAIssues += issues;
		else
			totalAAIssues += issues;
	}

	public boolean hasErrors() {
		if (getTotalAIssues() > 0 || getTotalAAIssues() > 0)
			return true;

		return false;
	}

}
