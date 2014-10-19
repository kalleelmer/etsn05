package database;

import java.sql.ResultSet;
import java.sql.SQLException;
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
			boolean signed, String start, String end, Role role,
			int activityType) throws IllegalArgumentException {
		Date startDate = Date.valueOf(start);
		Date endDate = Date.valueOf(end);
		
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
	
	public static void main(String[] args) {
		Date date = Date.valueOf("2014-08-12");
		TimeReport r1 = new TimeReport(2,"Goran",2,Role.developer,13,date,200,"ville");
		System.out.print(r1.DATE.toString());
		try {
			r1.insert();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
