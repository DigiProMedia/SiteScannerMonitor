package com.digipro;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChannelGroupFactory {

	private static final Log log = LogFactory.getLog(ChannelGroupFactory.class);
	private static final String DEFAULT_EMAIL = "support@adotpro.com";
	private static final String DEFAULT_REPORT_URL = "https://www.adotlabs.com/scan-results?scanId=";
	private static Channel[] channels;
	private static Group[] groups;

	public static Channel getChannel(int channelId, Properties properties) {

		Channel[] channelList = getChannels(properties);
		for (Channel channel : channelList) {
			if (channel.getId().equals(String.valueOf(channelId))) {
				return channel;
			}
		}

		Channel channel = new Channel();
		channel.setEmail(DEFAULT_EMAIL);
		channel.setUrl(DEFAULT_REPORT_URL);
		return channel;
	}

	public static String getChannelScanUrl(ScanType scanType, ScanReport report, Properties properties) {
		Channel channel = getChannel(report.getChannelId(), properties);

		String url = null;

		if (report.getGroupName() != null) {
			// https://host.adotlabs.com/scan-report/partner/jvzoohost?scanId=31e0665e-a48c-4e3b-b185-124c0e145363&aErrors=1&aaErrors=2
			url = channel.getUrl().substring(0, channel.getUrl().lastIndexOf("/")) + "/scan-report";
			url += "/partner/" + report.getGroupName() + "?scanId=" + report.getScanId() + "&scanType="
					+ scanType.name().toLowerCase();
			url += "&aErrors=" + report.getTotalAIssues() + "&aaErrors=" + report.getTotalAAIssues() + "&site_url="
					+ report.getUrl();
		} else {
			url = channel.getUrl() + report.getScanId() + "&scanType=" + scanType.name().toLowerCase();
			url += "&aErrors=" + report.getTotalAIssues() + "&aaErrors=" + report.getTotalAAIssues() + "&site_url="
					+ report.getUrl();
		}

		return url;
	}

	public static Channel[] getChannels(Properties properties) {
		if (channels != null)
			return channels;

		String channelJson = properties.getProperty(Property.CHANNELS);
		channels = GsonUtil.gson.fromJson(channelJson, Channel[].class);
		return channels;
	}

	public static Group[] getGroups(Properties properties) {
		if (groups != null)
			return groups;

		String groupJson = properties.getProperty(Property.GROUPS);
		groups = GsonUtil.gson.fromJson(groupJson, Group[].class);
		return groups;
	}

	public static Group getGroup(String groupName, Properties properties) {

		Group[] groupList = getGroups(properties);
		for (Group group : groupList) {
			if (group.getName().equals(groupName)) {
				return group;
			}
		}

		log.error("Could not retrieve group from property file!");
		return null;
	}
}
