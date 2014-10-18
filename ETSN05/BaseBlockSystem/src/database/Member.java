package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Member extends Entity {
	private String USERNAME;
	private int PROJECT;
	private Role ROLE;

	public Member(String userName, int project, Role role) {
		USERNAME = userName;
		PROJECT = project;
		ROLE = role;
	}
	
	public enum Role {
		MANAGER, ARCHITECT, DEVELOPER, TESTER
	}

	
	public static List<Member> getMembers(User user, Project project) {
		
		return null;
	}
	
	public void set() {
//		Finns redan member i databasen
		String selectQuery = "SELECT * FROM members WHERE username='" + USERNAME + "' AND project='" + PROJECT +"'";
		ResultSet rs = selectQuery(selectQuery);
		try {
			if (!rs.next()) {
				String addQuery = "INSERT INTO members(username,project,role) VALUES('"
						+ USERNAME + "'," + PROJECT + ",'" + ROLE + "')";
				query(addQuery);
			} else {
				// Ska denna eventuellt berätta att rollen inte förändrats om
				// man försöker byta till samma roll?
				String updateQuery = "UPDATE members SET role='" + ROLE + "'";
				query(updateQuery);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void delete() {
		String deleteQuery = "DELETE FROM members WHERE username='" + USERNAME + "' AND PROJECT='" + PROJECT + "'";
		query(deleteQuery);
	}
}