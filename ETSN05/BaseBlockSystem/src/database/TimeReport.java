package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

import database.Member.Role;

/**
 * This is the time report class and has the amount of minutes on a specific activity.
 * To create a full time report, sum all activities over a given time period.
 * @author etsn05
 *
 */
public class TimeReport extends Entity {
	public final int ID;
	public final String USERNAME;
	public final int PROJECT_ID;
	public final Member.Role ROLE;
	public final int ACTIVITY_TYPE;
	public final Date DATE;
	public final int DURATION;
	public final String SIGNER;
	
	public TimeReport(int id, String userName, int projectID, Member.Role role,
			int activityType, Date date, int duration, String signer) {
		ID = id;
		USERNAME = userName;
		PROJECT_ID = projectID;
		ROLE = role;
		ACTIVITY_TYPE = activityType;
		DATE = date;
		DURATION = duration;
		SIGNER = signer;
	}
	
	private static class ConditionBuilder {
		
		protected static String append(String modifier, Object object) throws ClassCastException {
			switch(modifier) {
			case "user":
				User user = (User) object;
				if (user == null) {
					return "";
				} else {
					return "user='" + user.USERNAME + "'";
				}
			case "project":
				Project project = (Project) object;
				if (project == null) {
					return "";
				} else {
					return "project=" + String.valueOf(project.ID);
				}
			case "signed":
				Boolean bool = (Boolean) object;
				if (bool == null) {
					return "";
				} else if (bool) {
					return "signed NOT LIKE 'null'";
				} else {
					return "signed LIKE 'null'";
				}
			case "start":
				String start = (String) object;
				if (start == null) {
					return "date BETWEEN '0000-00-00' AND ";
				} else {
					return "date BETWEEN '" + start + "' AND ";
				}
			case "end":
				String end = (String) object;
				if (end == null) {
					return "'9999-12-31'";
				} else {
					return "'" + end + "'";
				}
			case "role":
				String role = (String) object.toString();
				if (role == null) {
					return "";
				} else {
				}
			}
			return "";
		}
		
		
	}
	
	public static TimeReport getByID(int id) throws SQLException, Exception {
		String timeReportQuery = "SELECT * FROM timeReports WHERE id=" + id;
		ResultSet timeReportSet = selectQuery(timeReportQuery);
		if (!timeReportSet.next()) {
			return null;
		}
		TimeReport timeReport = new TimeReport(timeReportSet.getInt("id"),
				timeReportSet.getString("user"),
				timeReportSet.getInt("project"), Role.valueOf(timeReportSet
						.getString("role")),
				timeReportSet.getInt("activityType"),
				timeReportSet.getDate("date"),
				timeReportSet.getInt("duration"),
				timeReportSet.getString("signer"));
		return timeReport;
	}
	
	public static List<TimeReport> get(User user, Project project,
			Boolean signed, String start, String end, Role role,
			Integer activityType) throws IllegalArgumentException, SQLException, Exception {
		StringBuilder query = new StringBuilder();
		
		String input1;
		if (user == null) {
			input1 = " LIKE '%'";
		} else {
			input1 = "='" + user.USERNAME + "'"; 
		}
		String input2;
		if (project == null) {
			input2 = " LIKE '%'";
		} else {
			input2 = "=" + String.valueOf(project.ID);
		}
		String input3;
		if (signed == null) {
			input3 = " LIKE '%'";
		} else {
			if (signed) {
				input3 = " NOT LIKE 'null'";
			} else {
				input3 = " LIKE 'null'";
			}
		}
		String input4;
		if (start == null) {
			input4 = "'0000-00-00'";
		} else {
			input4 = "'" + start.toString() + "'";
		}
		String input5;
		if (end == null) {
			input5 = "'9999-12-31'";
		} else {
			input5 = "'" + end.toString() + "'";
		}
		String input6;
		if (role == null) {
			input6 = " LIKE '%'";
		} else {
			input6 = "='" + role.toString() + "'";
		}
		String input7;
		if (activityType == null) {
			input7 = " LIKE '%'";
		} else {
			input7 = "=" + String.valueOf(activityType);
		}
		String selectQuery = "SELECT * FROM timeReports WHERE user" + input1
				+ " AND project" + input2 + " AND role" + input6
				+ " AND activityType" + input7 + " AND date BETWEEN " + input4
				+ " AND " + input5 + " AND signer" + input3;
		ResultSet timeReportSet = selectQuery(selectQuery);
		List<TimeReport> foundList = new ArrayList<TimeReport>();
		while (timeReportSet.next()) {
			Date date = Date.valueOf(timeReportSet.getString("date"));
			TimeReport timeReport = new TimeReport(timeReportSet.getInt("id"),
					timeReportSet.getString("user"),
					timeReportSet.getInt("project"), Role.valueOf(timeReportSet
							.getString("role")),
					timeReportSet.getInt("activityType"), date,
					timeReportSet.getInt("duration"),
					timeReportSet.getString("signer"));
			foundList.add(timeReport);
		}
		if (foundList.size() == 0) {
			return null;
		} else {
			return foundList;
		}
	}

	public static List<TimeReport> getSummary(User user, Project project,
			boolean signed, String start, String end, Role role,
			int activityType, String summarizeBy) {
		return null;
	}

	/**
	 * Inserts a time report to the database
	 */
	public void insert() throws SQLException, Exception{
		String addQuery = "INSERT INTO timeReports VALUES("
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
				+ DATE
				+ "',"
				+ DURATION
				+ ",'"
				+ SIGNER
				+ "')";
		query(addQuery);
	}
	
	/**
	 * Updates an existing time report in the database
	 */
	public void update() throws SQLException, Exception {
		// Denna ska kontrollera huruvida tidrapporten är signerad. Om den är
		// signerad ska det inte gå att uppdatera den.
		String selectQuery = "SELECT * FROM timeReports WHERE id=" + ID;
		ResultSet rs = selectQuery(selectQuery);
		if (rs.next()) {
			// Oklart hur date ska fungera här.... Fattar noll
			String updateQuery = "UPDATE timeReports SET activityType="
					+ ACTIVITY_TYPE + ",duration=" + DURATION + ",signer='"
					+ SIGNER + "'";
			query(updateQuery);
		}
	}

	/**
	 * Deletes a time report from the database. 
	 * This method may only be called by the user who created this time report
	 * or the administrator of the system
	 */
	public void delete() throws SQLException, Exception {
		String deleteQuery = "DELETE FROM timeReports WHERE id=" + ID;
		query(deleteQuery);
	}
}
