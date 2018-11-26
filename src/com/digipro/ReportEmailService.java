package com.digipro;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

/**
 * Send report once per day.
 *
 */
public class ReportEmailService {
	private static final String SUBJECT = "Daily Scan Report - %s Free : %s Full ";
	private static final String FROM = "support@adotpro.com";

	private MonitorDao dao;
	private Properties properties;

	public ReportEmailService(MonitorDao dao, Properties properties) {
		this.dao = dao;
		this.properties = properties;
	}

	public void checkAndSendReport(int days) {
		AmazonSimpleEmailService client = createSESClient();
		StringBuilder emailBody = new StringBuilder();

		Calendar from = Calendar.getInstance();
		from.add(Calendar.DAY_OF_YEAR, -days);

		System.err.println(from.getTime());
		List<ScanReport> freeList = dao.getScans("vch_basic_scan_id", "ss_basic_scan", "scopeFolder", from);
		List<ScanReport> paidList = dao.getScans("vch_full_scan_id", "ss_full_scan", null, from);

		if (freeList.size() == 0 && paidList.size() == 0) {
			emailBody.append(String.format("No scans within the last %s days%", days, (days == 1 ? "" : "s")));
		} else {
			emailBody.append(String.format("<h1>Period: Last %s</h1>", (days == 1 ? "24 hours" : days + " days")));
			addScansToBody(emailBody, freeList, "Free Scans", ScanType.BASIC_FOLDER, properties);
			addScansToBody(emailBody, paidList, "Full Scans", ScanType.FULL, properties);
		}

		emailBody.append("<br><br><br><br>");

		List<String> toList = new ArrayList<>();
		String[] recipients = properties.getProperty(Property.REPORT_RECIPIENTS).split(",");
		for (String recipient : recipients)
			toList.add(recipient);

		SendEmailRequest request = new SendEmailRequest()
				.withDestination(
						new Destination().withToAddresses(toList))
				.withMessage(new Message()
						.withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(emailBody.toString()))
								.withText(new Content().withCharset("UTF-8").withData("")))
						.withSubject(new Content().withCharset("UTF-8")
								.withData(String.format(SUBJECT, freeList.size(), paidList.size()))))
				.withSource(FROM);
		// Comment or remove the next line if you are not using a
		// configuration set
		// .withConfigurationSetName(CONFIGSET);
		client.sendEmail(request);
		System.out.println("Email sent!");
	}

	private void addScansToBody(StringBuilder html, List<ScanReport> reportList, String title, ScanType scanType,
			Properties properties) {
		if (reportList.size() == 0)
			return;

		html.append("<h2>" + title + " - " + reportList.size() + "</h2>");
		html.append(
				"<table cellspacing=\"0\" cellpadding=\"2\" width=\"100%\"><tr><td><b>URL</b></td><td><b>Email</b></td><td><b>Report</b></td><td><b>A</b></td><td><b>AA</b></td><td><b>Status</b></td><td><b>Scan Time</b></td><td><b>Internal</b></td><td><b>Error</b></td></tr>");
		int count = 0;
		for (ScanReport report : reportList) {
			if (count++ % 2 == 0)
				html.append("<tr bgcolor=\"#f6eaea\" style=\"padding-bottom:5px;padding-top:5px;\">");
			else
				html.append("<tr>");

			html.append("<td>" + report.getUrl() + "</td>");
			html.append("<td>" + report.getEmailAddress() + "</td>");

			if (report.getStatus() == -1)
				html.append("<td></td>");
			else
				html.append("<td><a href=\"" + ChannelGroupFactory.getChannelScanUrl(scanType, report, properties)
						+ "\">Report</td>");

			html.append("<td>" + report.getTotalAIssues() + "</td>");
			html.append("<td>" + report.getTotalAAIssues() + "</td>");

			if (report.getStatus() == -1)
				html.append("<td>Error</td>");
			else
				html.append("<td>Complete</td>");

			html.append("<td>" + formatTime(report.getSecondsToProcess()) + "</td>");

			html.append("<td>" + (report.isInternal() ? "Yes" : "No") + "</td>");

			html.append("<td>" + StringUtils.defaultIfBlank(report.getProcessError(), "") + "</td>");

			html.append("</tr>");
		}

		html.append("</table>");
	}

	public static String formatTime(int duration) {
		if (duration == 0)
			return "";

		long longVal = duration;
		int hours = (int) longVal / 3600;
		int remainder = (int) longVal - hours * 3600;
		int mins = remainder / 60;
		remainder = remainder - mins * 60;
		int secs = remainder;

		return String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
	}

	private AmazonSimpleEmailService createSESClient() {
		AWSCredentials credentials = new BasicAWSCredentials(properties.getProperty(Property.AWS_S3_KEY),
				properties.getProperty(Property.AWS_S3_SECRET));
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_WEST_2).build();

		return client;
	}

}
