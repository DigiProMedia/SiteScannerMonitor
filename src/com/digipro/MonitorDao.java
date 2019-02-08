package com.digipro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

public class MonitorDao extends BaseDao {

	public MonitorDao(Properties properties) {
		super(properties);
	}

	public List<ScanReport> getScans(String idField, String tableName, String scanScope, Calendar from) {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		List<ScanReport> reportList = new ArrayList<>();
		try {
			DataSource dataSource = getDS();
			conn = dataSource.getConnection();

			String sql = "SELECT * from " + tableName
					+ " where n_campaign_id = 0 and n_status in(-1,2,3) and b_test = 0 and b_bulk_scan = 0 and d_created > ? ";

			if (scanScope != null)
				sql += " and vch_scan_scope = ? ";

			sql += " order by n_a_issue_count desc ";

			stmt = conn.prepareStatement(sql);
			stmt.setTimestamp(1, new Timestamp(from.getTimeInMillis()));

			if (scanScope != null)
				stmt.setString(2, scanScope);

			rs = stmt.executeQuery();
			while (rs.next()) {
				ScanReport scan = new ScanReport();
				scan.setScanId(rs.getString(idField));
				scan.setUrl(rs.getString("vch_url"));
				scan.setEmailAddress(rs.getString("vch_email_address"));
				scan.setChannelId(rs.getInt("n_channel_id"));
				scan.setTotalAIssues(rs.getInt("n_a_issue_count"));
				scan.setTotalAAIssues(rs.getInt("n_aa_issue_count"));
				scan.setInternal(rs.getBoolean("b_internal_scan"));
				scan.setGroupName(rs.getString("vch_group_name"));
				scan.setStatus(rs.getInt("n_status"));
				scan.setSecondsToProcess(rs.getInt("n_process_seconds"));
				scan.setProcessError(rs.getString("vch_processing_error"));
				reportList.add(scan);
			}

		} catch (Exception sqlException) {
			throw new RuntimeException(sqlException);
		} finally {
			cleanup(rs, conn, stmt);
		}

		return reportList;
	}

	public List<ScanReport> getInProgressAndPendingFullScans() {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		List<ScanReport> reportList = new ArrayList<>();
		try {
			DataSource dataSource = getDS();
			conn = dataSource.getConnection();

			String sql = "SELECT * from ss_full_scan "
					+ " where n_campaign_id = 0 and n_status in(0,1) and b_test = 0 and b_bulk_scan = 0 ";
			sql += " order by d_created desc ";

			stmt = conn.prepareStatement(sql);

			rs = stmt.executeQuery();
			while (rs.next()) {
				ScanReport scan = new ScanReport();
				scan.setScanId(rs.getString("vch_full_scan_id"));
				scan.setUrl(rs.getString("vch_url"));
				scan.setEmailAddress(rs.getString("vch_email_address"));
				scan.setStatus(rs.getInt("n_status"));

				Calendar now = Calendar.getInstance();
				Calendar created = Calendar.getInstance();
				created.setTimeInMillis(rs.getTimestamp("d_created").getTime());
				scan.setSecondsToProcess((int) (now.getTimeInMillis() - created.getTimeInMillis()) / 1000);

				reportList.add(scan);
			}

		} catch (Exception sqlException) {
			throw new RuntimeException(sqlException);
		} finally {
			cleanup(rs, conn, stmt);
		}

		return reportList;
	}

	public LastAccess getLastAccess(String ipAddress, ScanType scanType) {

		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		LastAccess lastAccess = null;

		try {
			DataSource dataSource = getDS();
			conn = dataSource.getConnection();

			stmt = conn.prepareStatement(
					"SELECT * from ss_scan_monitor WHERE vch_scan_type = ? and vch_processor_ip = ? ");

			stmt.setString(1, scanType.name());
			stmt.setString(2, ipAddress);
			rs = stmt.executeQuery();

			if (rs.next()) {
				lastAccess = new LastAccess();
				Calendar modified = Calendar.getInstance();
				modified.setTimeInMillis(rs.getTimestamp("d_modified", Calendar.getInstance()).getTime());
				lastAccess.setModified(modified);
				lastAccess.setCurrentScanCount(rs.getInt("n_current_scan_count"));
				lastAccess.setScanType(rs.getString("vch_scan_type"));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			cleanup(rs, conn, stmt);
		}

		return lastAccess;
	}
}
