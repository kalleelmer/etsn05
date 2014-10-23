package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
		USERNAME = safetyInput(userName);
		PROJECT_ID = projectID;
		ROLE = role;
		ACTIVITY_TYPE = activityType;
		DATE = date;
		DURATION = duration;
		SIGNER = safetyInput(signer);
	}
	
	public TimeReport(String userName, int projectID, Member.Role role, int activityType, Date date, int duration, String signer) {
		ID = 0;
		USERNAME = safetyInput(userName);
		PROJECT_ID = projectID;
		ROLE = role;
		ACTIVITY_TYPE = activityType;
		DATE = date;
		DURATION = duration;
		SIGNER = safetyInput(signer);
	}
	
	private static class ConditionBuilder {
		
		private static String append(String modifier, Object object) throws ClassCastException {
			switch(modifier) {
			case "user":
				User user = (User) object;
				if (user == null) {
					return "";
				} else {
					return "user='" + user.USERNAME + "' AND ";
				}
			case "project":
				Project project = (Project) object;
				if (project == null) {
					return "";
				} else {
					return "project=" + String.valueOf(project.ID) + " AND ";
				}
			case "signed":
				Boolean bool = (Boolean) object;
				if (bool == null) {
					return "";
				} else if (bool) {
					return "signer NOT LIKE 'null' AND ";
				} else {
					return "signer LIKE 'null' AND ";
				}
			case "start":
				String start = (String) object;
				if (start == null) {
					return "date BETWEEN '1900-01-01' AND ";
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
				Member.Role role = (Member.Role) object;
				if (role == null) {
					return "";
				} else {
					return "role='" + role.toString() + "' AND ";
				}
			case "activityType":
				Integer activityType = (Integer) object;
				if (activityType == null) {
					return "";
				} else {
					return "activityType=" + activityType.toString() + " AND ";
				}
			}
			return "";
		}
		
		public static StringBuilder appendAll(User user, Project project, Boolean signed, Role role, Integer activityType, String start, String end) {
			StringBuilder condition = new StringBuilder();
			condition.append(ConditionBuilder.append("user", user));
			condition.append(ConditionBuilder.append("project",project));
			condition.append(ConditionBuilder.append("signed",signed));
			condition.append(ConditionBuilder.append("role", role));
			condition.append(ConditionBuilder.append("activityType",activityType));
			condition.append(ConditionBuilder.append("start", start));
			condition.append(ConditionBuilder.append("end", end));
			return condition;
		}
	}
	
	public static TimeReport getByID(int id) throws SQLException {
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
			Integer activityType) throws IllegalArgumentException, SQLException {
		StringBuilder condition = ConditionBuilder.appendAll( user, project, signed, role, activityType, start, end);
		String selectQuery = "SELECT * FROM timeReports WHERE " + condition.toString() + ";";
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

	public static Map<String, Integer> getSummary(User user, Project project,
			Boolean signed, String start, String end, Role role,
			Integer activityType, SumChooser summarizeBy) throws IllegalArgumentException, SQLException {
		StringBuilder condition = ConditionBuilder.appendAll(user, project,
				signed, role, activityType, start, end);
		if (summarizeBy.equals(SumChooser.week)) {
			if (start == null || end == null) {
				throw new IllegalArgumentException();
			}
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(Date.valueOf(start));
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(Date.valueOf(end));
			if (startCal.get(Calendar.YEAR) != endCal.get(Calendar.YEAR)) {
				throw new IllegalArgumentException();
			}
			String selectWeekQuery = "SELECT date, SUM(duration) from timeReports WHERE "
					+ condition.toString() + " GROUP BY date;";
			ResultSet weekReportSet = selectQuery(selectWeekQuery);
			return sumByWeeks(weekReportSet);
		}
		String selectQuery = "SELECT " + summarizeBy
				+ ", SUM(duration) FROM timeReports WHERE "
				+ condition.toString() + " GROUP BY " + summarizeBy + ";";
		ResultSet timeReportSet = selectQuery(selectQuery);
		Map<String, Integer> foundMap = new TreeMap<String, Integer>();
		while (timeReportSet.next()) {
			foundMap.put(timeReportSet.getString(1), timeReportSet.getInt(2));
		}
		return foundMap;
	}
	
	private static Map<String, Integer> sumByWeeks(ResultSet weekReportSet) throws SQLException {
		Map<String, Integer> foundWeekMap = new TreeMap<String, Integer>();
		Calendar cldCurrent = Calendar.getInstance();
		Calendar cldNext = Calendar.getInstance();
		if (!weekReportSet.next()) {
			return null;
		}
		do {
			cldCurrent.setTime(weekReportSet.getDate(1));
			cldNext.setTime(weekReportSet.getDate(1));
			int duration = 0;
			boolean withinRange = true;
			while (cldCurrent.get(Calendar.WEEK_OF_YEAR) == cldNext
					.get(Calendar.WEEK_OF_YEAR) && withinRange) {
				duration += weekReportSet.getInt(2);
				if (weekReportSet.next()) {
					cldNext.setTime(weekReportSet.getDate(1));
				} else {
					withinRange = false;
				}
			}
			String week = "" + cldCurrent.get(Calendar.WEEK_OF_YEAR);
			foundWeekMap.put(week, duration);
			if (weekReportSet.isAfterLast()) {
				break;
			}
		} while (weekReportSet.next());
		return foundWeekMap;
		
	}
	
	public enum SumChooser {
		user, project, role, activityType, week
	}

	/**
	 * Inserts a time report to the database
	 */
	public void insert() throws SQLException {
		String addQuery = "INSERT INTO timeReports SET user='" + USERNAME
				+ "',project=" + PROJECT_ID + ",role='" + ROLE
				+ "',activityType=" + ACTIVITY_TYPE + ",date='" + DATE
				+ "',duration=" + DURATION + ",signer='" + SIGNER + "';";
		query(addQuery);
	}

	/**
	 * Updates an existing time report in the database
	 */
	public void update() throws SQLException {
		String selectQuery = "SELECT * FROM timeReports WHERE id=" + ID + ";";
		ResultSet timeReportSet = selectQuery(selectQuery);
		if (timeReportSet.next()) {
			String updateQuery = "";
			if (!timeReportSet.getString("signer").equals(null)) {
				updateQuery = "UPDATE timeReports SET signer='" + SIGNER + "' WHERE id=" + ID +";";
			} else {
				updateQuery = "UPDATE timeReports SET activityType="
						+ ACTIVITY_TYPE + ",duration=" + DURATION + ",signer='"
						+ SIGNER + "' WHERE id=" + ID +";";
			}
			query(updateQuery);
		}
	}

	/**
	 * Deletes a time report from the database. 
	 * This method may only be called by the user who created this time report
	 * or the administrator of the system
	 */
	public void delete() throws SQLException {
		String deleteQuery = "DELETE FROM timeReports WHERE id=" + ID + ";";
		query(deleteQuery);
	}
}
