package com.digipro;

public class Constants {

	public static final String PROPERTIES = "/properties/sitescanner.properties";
	public static final String REPORT_FILE = "/properties/sitescanner.report.update";

	public static final String PROP_DB_URL = "DB_URL";
	public static final String PROP_DB_USER = "DB_USER";
	public static final String PROP_DB_PASS = "DB_PASS";
	public static final String ENVIRONMENT = "ENVIRONMENT";

	// Scan status
	public static final int STATUS_PENDING = 0;
	public static final int STATUS_PROCESSING = 1;
	public static final int STATUS_COMPLETE_WITH_ISSUES = 2;
	public static final int STATUS_COMPLETE_WITHOUT_ISSUES = 3;
	public static final int STATUS_ERROR = -1;

	public static final int CHANNEL_ID_ADOTLABS = 1010;
	public static final int CHANNEL_ID_DELUXE = 1019;
	public static final int CHANNEL_ID_NRA = 1020;

	public static final String TMP_DIR = "/tmp/SiteScanner/";
	public static final String SCAN_CONFIG_BASIC = "/sitescanner/SortSite_BasicScan.ssconfig";
	public static final String SCAN_CONFIG_BASIC_SCOPE_FOLDER = "/sitescanner/SortSite_BasicScanScopeFolder.ssconfig";
	public static final String SCAN_CONFIG_FULL = "/sitescanner/SortSite_FullScan.ssconfig";

	public static final String[] REPLACE_ICON = { "https://c1164686.ssl.cf3.rackcdn.com/favicon.ico",
			"http://dev-web-adot-reports.s3-us-west-2.amazonaws.com/favicon.ico" };
	public static final String[] REPLACE_URL = { "https://app.powermapper.com/vres", "Report" };

}
