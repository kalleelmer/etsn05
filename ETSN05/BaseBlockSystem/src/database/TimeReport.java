package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
	public final int DURATION;
	public final String SIGNER;
	
//	Här ska Date skickas med....
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
	
	/**
	 * Inserts a time report to the database
	 */
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
	
	/**
	 * Updates an existing time report in the database
	 */
	public void update() {
		// Denna ska kontrollera huruvida tidrapporten är signerad. Om den är
		// signerad ska det inte gå att uppdatera den.
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
	
	/**
	 * Deletes a time report from the database. 
	 * This method may only be called by the user who created this time report
	 * or the administrator of the system
	 */
	public void delete() {
		String deleteQuery = "DELETE FROM timeReports WHERE id=" + ID;
		query(deleteQuery);
	}
}
