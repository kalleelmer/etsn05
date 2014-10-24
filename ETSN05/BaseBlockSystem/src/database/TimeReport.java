package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.sql.Date;
import java.text.SimpleDateFormat;

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
	
	private static TimeReport convertFromDB(ResultSet rs) throws SQLException {
		return new TimeReport(rs.getInt("id"),
				rs.getString("user"), rs
						.getInt("project"), Role.valueOf(rs
						.getString("role")), rs
						.getInt("activityType"), Date
						.valueOf(rs.getString("date")),
				rs.getInt("duration"), rs
						.getString("signer"));
	}
	
	public static List<TimeReport> getTimeReportToModify(User user) throws SQLException {
		if (user.USERNAME.equals("admin")) {
			return get(null,null,null,null,null,null,null);
		}
		List<TimeReport> resultList = new ArrayList<TimeReport>();
		String selectManagerQuery = "SELECT * FROM timeReports WHERE user='" + user.USERNAME + "' AND role='manager' GROUP BY project";
		ResultSet managerSet = selectQuery(selectManagerQuery);
		while (managerSet.next()) {
			List<Member> projectMembers = Project.getByID(managerSet.getInt("project")).getMembers();
			for (Member m : projectMembers) {
				String selectTimeReportQuery = "SELECT * FROM timeReports WHERE user='" + m.USERNAME + "' AND project=" + managerSet.getInt("project") + ";";
				ResultSet membersSet = selectQuery(selectTimeReportQuery);
				while (membersSet.next()) {
					resultList.add(convertFromDB(membersSet));
				}
			}
		}
		String selectNotManagerQuery = "SELECT * FROM timeReports WHERE user='" + user.USERNAME + "' AND role NOT LIKE 'manager';";
		ResultSet notManagerSet = selectQuery(selectNotManagerQuery);
		while (notManagerSet.next()) {
			resultList.add(convertFromDB(notManagerSet));
		}
		if (resultList.isEmpty()) {
			return null;
		}
		return resultList;
	}
	
	public static TimeReport getByID(int id) throws SQLException {
		String timeReportQuery = "SELECT * FROM timeReports WHERE id=" + id;
		ResultSet timeReportSet = selectQuery(timeReportQuery);
		if (!timeReportSet.next()) {
			return null;
		}
		return convertFromDB(timeReportSet);
	}
	
	private static String Cond(Object object) {
		if (object == (null)) {
			return "'%'";
		}
		switch(object.getClass().getSimpleName()) {
		case "User":
			User user = (User) object;
			return "'" + user.USERNAME + "'";
		case "Project":
			Project project = (Project) object;
			return String.valueOf(project.ID);
		case "Boolean":
			if ((Boolean) object) {
				return " AND signer NOT LIKE 'null'";
			} return " AND signer LIKE 'null'";
		case "Role":
			Role role = (Role) object;
			return "'" + role.toString() + "'";
		case "Integer":
			return "'" + String.valueOf(object) + "'";
		}
		return "";
	}
	
	public static List<TimeReport> get(User user, Project project,
			Boolean signed, String start, String end, Role role,
			Integer activityType) throws SQLException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String selectQuery = "SELECT * FROM timeReports WHERE user LIKE "
				+ Cond(user)
				+ " AND project LIKE "
				+ Cond(project)
				+ (signed == (null) ? "" : Cond(signed))
				+ " AND date BETWEEN '"
				+ (start == (null) ? "1970-01-01" : start)
				+ "' AND '"
				+ (end == (null) ? format.format(Calendar.getInstance()
						.getTime()) : end) + "' AND role LIKE " + Cond(role)
				+ " AND activityType LIKE " + Cond(activityType) + ";";
		ResultSet timeReportSet = selectQuery(selectQuery);
		List<TimeReport> foundList = new ArrayList<TimeReport>();
		while (timeReportSet.next()) {
			foundList.add(convertFromDB(timeReportSet));			
		}
		if (foundList.isEmpty()) {
			return null;
		} return foundList;
	}

	public static Map<String, Integer> getSummary(User user, Project project,
			Boolean signed, String start, String end, Role role,
			Integer activityType, SumChooser summarizeBy) throws IllegalArgumentException, SQLException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String selectQuery = "SELECT "
				+ (summarizeBy.equals(SumChooser.week) ? "date" : summarizeBy) 
				+ ", SUM(duration) FROM timeReports WHERE user LIKE "
				+ Cond(user)
				+ " AND project LIKE "
				+ Cond(project)
				+ (signed == (null) ? "" : Cond(signed))
				+ " AND date BETWEEN '"
				+ (start == (null) ? "1970-01-01" : start)
				+ "' AND '"
				+ (end == (null) ? format.format(Calendar.getInstance()
						.getTime()) : end) + "' AND role LIKE " + Cond(role)
				+ " AND activityType LIKE " + Cond(activityType) + " GROUP BY " + (summarizeBy.equals(SumChooser.week) ? "date" : summarizeBy) + ";";
		ResultSet timeReportSet = selectQuery(selectQuery);
		Map<String,Integer> foundMap = new TreeMap<String, Integer>();
		while (timeReportSet.next()) {
			foundMap.put(timeReportSet.getString((summarizeBy.equals(SumChooser.week) ? "date" : summarizeBy.toString())), timeReportSet.getInt("SUM(duration)"));
		}
		if (foundMap.isEmpty()) {
			return null;
		} else if (summarizeBy.equals(SumChooser.week)) {
			Map<String, Integer> weekMap = new TreeMap<String, Integer>();
			Iterator<Entry<String,Integer>> itr = foundMap.entrySet().iterator();
			Entry<String, Integer> first = itr.next();
			Calendar currentDate = Calendar.getInstance();
			Calendar nextDate = Calendar.getInstance();
			currentDate.setTime(Date.valueOf(first.getKey()));
			int sum = first.getValue();
			while (itr.hasNext()) {
				Entry<String, Integer> next = itr.next();
				nextDate.setTime(Date.valueOf(next.getKey()));
				if (currentDate.get(Calendar.WEEK_OF_YEAR) == (nextDate
						.get(Calendar.WEEK_OF_YEAR))
						&& currentDate.get(Calendar.YEAR) == (nextDate
								.get(Calendar.YEAR))) {
					sum += next.getValue();
				} else {
					weekMap.put(currentDate.get(Calendar.YEAR) + ": " + currentDate.get(Calendar.WEEK_OF_YEAR), sum);
					currentDate.setTime(Date.valueOf(next.getKey()));
					sum = next.getValue();
				}
			}
			weekMap.put(currentDate.get(Calendar.YEAR) + ": " + currentDate.get(Calendar.WEEK_OF_YEAR), sum);
			return weekMap;	
		}
		return foundMap;
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
			if (!timeReportSet.getString("signer").equals("null")) {
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
