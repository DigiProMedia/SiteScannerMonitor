package com.digipro;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Monitor {

	static String ipAddress;
	static Properties properties;
	static int totalScanCount = 0;
	static LastAccess access = null;
	static MonitorDao dao;
	static final int INACTIVE_PERIOD_MINS = 10;
	static final Log log = LogFactory.getLog(Monitor.class);

	/**
	 * Args:
	 *  0 - Monitor type: 
	 *  	report - Scan Results report (daily or weekly controlled  by second params (day)
	 *  	scancheck - Trigger a scan check. This is due to an issue that still needs to be resolved. Scan check will restart the services if required
	 *      issuesReport - This is just a report to me to monitor for issues so we can catch them early. Runs every 30 minutes
	 *  1 - Frequency
	 *  	days for "report"
	 *  	minutes for "issuesReport"
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			log.warn("args not specified. Defaulting to report");
			args = new String[2];
			args[0] = "issuesReport";
			args[1] = "30"; // Minutes
		}

		if (args[0].equals("report") && args.length != 2)
			throw new RuntimeException("Number of days must be passed as the second parameter");

		loadProperties();
		dao = new MonitorDao(properties);

		if (args[0].equals("report"))
			sendReport(Integer.valueOf(args[1]));
		else if (args[0].equals("issuesReport"))
			sendIssuesReport(Integer.valueOf(args[1]));
		else
			checkScanProcesses();
	}

	private static void sendIssuesReport(int minutes) {
		ReportEmailService emailService = new ReportEmailService(dao, properties);
		emailService.checkAndSendIssuesReport(minutes);
	}

	private static void sendReport(int days) {
		ReportEmailService emailService = new ReportEmailService(dao, properties);
		emailService.checkAndSendReport(days);
	}

	public static void checkScanProcesses() {

		List<LastAccess> accessList = new ArrayList<>();

		getLastAccess(Property.BASIC_SCAN_ENABLED, ScanType.BASIC, accessList);
		getLastAccess(Property.BASIC_FOLDER_SCAN_ENABLED, ScanType.BASIC_FOLDER, accessList);
		getLastAccess(Property.FULL_SCAN_ENABLED, ScanType.FULL, accessList);

		// We have active scans on this machine.
		if (totalScanCount != 0)
			System.exit(0);

		Calendar threshold = Calendar.getInstance();
		threshold.set(Calendar.MINUTE, -INACTIVE_PERIOD_MINS);

		boolean restartRequired = false;
		String culpritScanType = "";
		for (LastAccess access : accessList) {
			if (access.getModified().getTimeInMillis() < threshold.getTimeInMillis()) {
				restartRequired = true;
				culpritScanType = access.getScanType();
			}
		}

		// For some reason log4j isn't working under windows task manager. Just create
		// our own with last details
		StringBuilder custLog = new StringBuilder();
		if (!restartRequired) {
			custLog.append("Restart not required\n");
			System.exit(0);
		} else
			log.info("Restarting due to " + culpritScanType);

		// Restart the service
		try {

			CommandResult result = CmdUtils.processCmd("NET STOP SiteScanner", true, true);
			custLog.append("Stopping SiteScanner Service\n");
			custLog.append("NET MSG: " + result.getResponse() + "\n");

			Thread.sleep(5000);

			result = CmdUtils.processCmd("NET START SiteScanner", true, true);
			custLog.append("Starting SiteScanner Service\n");
			custLog.append("NET MSG: " + result.getResponse());
		} catch (Exception e) {
			e.printStackTrace();
		}

		File file = new File("/sitescanner/logs/lastMonitor.txt");
		try {
			FileUtils.write(file, custLog.toString(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static LastAccess getLastAccess(String scanEnabledProperty, ScanType scanType, List<LastAccess> accessList) {
		if (properties.getProperty(scanEnabledProperty, "false").equals("true")) {
			access = dao.getLastAccess(ipAddress, scanType);
			if (access != null) {
				totalScanCount += access.getCurrentScanCount();
				accessList.add(access);
			}

			return access;
		}

		return null;
	}

	public static Properties loadProperties() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			ipAddress = address.getHostAddress();

		} catch (UnknownHostException e) {
			throw new RuntimeException("Could not get ip address for worker");
		}

		properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(Constants.PROPERTIES)));
			return properties;
		} catch (Exception e) {
			throw new RuntimeException("Clould not load property file " + Constants.PROPERTIES);
		}
	}
}
