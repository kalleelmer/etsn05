package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
		undefined, manager, architect, developer, tester
	}

	/**
	 * Given a specific project id, this method returns a list of all members
	 * currently enrolled in that project
	 * @param id
	 * @return
	 */
	public static List<Member> getMembers(int id) throws SQLException, Exception {
		String selectQuery = "SELECT * FROM members WHERE project=" + id;
		ResultSet memberSet = selectQuery(selectQuery);
		List<Member> foundList = new ArrayList<Member>();
		while (memberSet.next()) {
			Member member = new Member(memberSet.getString("username"),
					memberSet.getInt("project"), Role.valueOf(memberSet
							.getString("role")));
			foundList.add(member);
		}
		if (foundList.size() == 0) {
			return null;
		} else {
			return foundList;
		}
	}

	/**
	 * Inserts a member to the database. If the member already exists, it updates with new data
	 */
	public void set() throws SQLException, Exception {
//		Finns redan member i databasen
		String selectQuery = "SELECT * FROM members WHERE username='" + USERNAME + "' AND project='" + PROJECT +"'";
		ResultSet memberSet = selectQuery(selectQuery);
		if (!memberSet.next()) {
			String addQuery = "INSERT INTO members(username,project,role) VALUES('"
					+ USERNAME + "'," + PROJECT + ",'" + ROLE + "')";
			query(addQuery);
		} else {
			// Ska denna eventuellt berätta att rollen inte förändrats om
			// man försöker byta till samma roll?
			String updateQuery = "UPDATE members SET role='" + ROLE + "'";
			query(updateQuery);
		}
	}
	
	/**
	 * Removes a member from the member list, i.e. removes a member from a project
	 */
	public void delete() throws SQLException, Exception {
		String deleteQuery = "DELETE FROM members WHERE username='" + USERNAME + "' AND PROJECT='" + PROJECT + "'";
		query(deleteQuery);
	}
}