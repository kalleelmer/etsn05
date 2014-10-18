package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This class is the member of a project. Each member object knows which project
 * it is linked to. A user can be member of several projects, and will then have
 * its username in several different member objects.
 * 
 * @author etsn05
 *
 */
public class Member extends Entity {
	public final String USERNAME;
	public final int PROJECT;
	public final Role ROLE;

	public Member(String userName, int project, Role role) {
		USERNAME = userName;
		PROJECT = project;
		ROLE = role;
	}
	
	/**
	 * Enumeration including the different roles a member of a project can have. 
	 * Each role has a toString() method which returns a string representation of the role
	 * @author etsn05
	 *
	 */
	public enum Role {
		MANAGER {
			public String toString() {
				return "Manager";
			}
		}, ARCHITECT {
			public String toString() {
				return "Architect";
			}
		}, DEVELOPER {
			public String toString() {
				return "Developer";
			}
		}, TESTER {
			public String toString() {
				return "Tester";
			}
		}
	}

//	Denna metod ska skickas change request på
	public static List<Member> getMembers(User user, Project project) {
		return null;
	}
	
	/**
	 * Inserts a member to the database. If the member already exists, it updates with new data
	 */
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
	
	/**
	 * Removes a member from the member list, i.e. removes a member from a project
	 */
	public void delete() {
		String deleteQuery = "DELETE FROM members WHERE username='" + USERNAME + "' AND PROJECT='" + PROJECT + "'";
		query(deleteQuery);
	}
}