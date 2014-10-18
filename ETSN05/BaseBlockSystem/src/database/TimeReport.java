package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import database.Member.Role;

public class TimeReport extends Entity {
	private final int ID;
	private final String USERNAME;
	private final int PROJECT_ID;
	private final Member.Role ROLE;
	private final int ACTIVITY_TYPE;
	private final int DURATION;
	private final String SIGNER;
	
	public TimeReport(int id, String userName, int projectID, Member.Role role,
			int activityType, int duration, String signer) {
		ID = id;
		USERNAME = userName;
		PROJECT_ID = projectID;
		ROLE = role;
		ACTIVITY_TYPE = activityType;
		DURATION = duration;
		SIGNER = signer;
	}
	
//	public static TimeReport getByID(int id) {
//		String selectQuery = "SELECT FROM timeReports WHERE id=" + id;
//		ResultSet rs = selectQuery(selectQuery);
//		try {
//			if (!rs.next()) {
//				return null;
//			} else {
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

	public void insert() {
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date());
		String addQuery = "INSERT INTO timeReports(id,user,project,role,activityType,date,duration,signer) VALUES("
				+ ID
				+ ",'"
				+ USERNAME
				+ "',"
				+ PROJECT_ID
				+ ",'"
				+ ROLE
				+ "',"
				+ ACTIVITY_TYPE
				+ ",'"
				+ date
				+ "',"
				+ DURATION
				+ ",'"
				+ SIGNER
				+ "')";
		query(addQuery);
	}
	
	public void update() {
		String selectQuery = "SELECT * FROM timeReports WHERE id=" + ID;
		ResultSet rs = selectQuery(selectQuery);
		try {
			if (rs.next()) {
				// Oklart hur date ska fungera här.... Fattar noll
				String updateQuery = "UPDATE timeReports SET activityType="
						+ ACTIVITY_TYPE + ",duration=" + DURATION + ",signer='"
						+ SIGNER + "'";
				query(updateQuery);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void delete() {
		String deleteQuery = "DELETE FROM timeReports WHERE id=" + ID;
		query(deleteQuery);
	}
}
