package com.digipro;

/**
 * @formatter:off
 * 
 *
 */
public enum Status {

	STATUS_PENDING(0, "Pending"), 
	STATUS_PROCESSING(1, "Processing"), 
	STATUS_COMPLETE_WITH_ISSUES(2, "Completed"), 
	STATUS_COMPLETE_WITHOUT_ISSUES(3, "Completed - No Issues"), 
	STATUS_ERROR(-1, "Error");

	int status;
	String name;

	private Status(int status, String name) {
		this.status = status;
		this.name = name;
	}
	
	

	public String getName() {
		return name;
	}



	public static Status get(int status) {
		for (Status value : values()) {
			if (value.status == status)
				return value;
		}

		return null;
	}
}
