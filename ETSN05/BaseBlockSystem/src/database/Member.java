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
		USERNAME = safetyInput(userName);
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
	
	public static Member getMember(int id, String username) throws SQLException{
		String selectQuery = "SELECT * FROM members WHERE project=" + id + " AND username='" + username + "';";
		ResultSet memberSet = selectQuery(selectQuery);
		if (!memberSet.next()) {
			return null;
		}
		return new Member(memberSet.getString("username"),
				memberSet.getInt("project"), Role.valueOf(memberSet
						.getString("role")));

	}

	/**
	 * Returns a list of all member object linked to this user
	 * @param id
	 * @return
	 */
	public static List<Member> getMembers(User user) throws SQLException {
		String selectQuery = "SELECT * FROM members WHERE username='" + user.USERNAME + "';";
		ResultSet memberSet = selectQuery(selectQuery);
		List<Member> foundList = new ArrayList<Member>();
		while (memberSet.next()) {
			Member member = new Member(memberSet.getString("username"),
					memberSet.getInt("project"), Role.valueOf(memberSet
							.getString("role")));
			if (!member.USERNAME.equals("admin")) {
				foundList.add(member);
			}
		}
		if (foundList.isEmpty()) {
			return null;
		}
		return foundList;
	}

	/**
	 * Inserts a member to the database. If the member already exists, it updates with new data
	 */
	public void set() throws SQLException {
		String selectQuery = "SELECT * FROM members WHERE username='" + USERNAME + "' AND project='" + PROJECT +"';";
		ResultSet memberSet = selectQuery(selectQuery);
		if (!memberSet.next()) {
			String addQuery = "INSERT INTO members SET username='" + USERNAME
					+ "',project=" + PROJECT + ",role='" + ROLE + "';";
			query(addQuery);
		} else {
			String updateQuery = "UPDATE members SET role='" + ROLE + "' WHERE username='" + USERNAME + "' AND project=" + PROJECT + ";";
			query(updateQuery);
		}
	}
	
	/**
	 * Removes a member from the member list, i.e. removes a member from a project
	 */
	public void delete() throws SQLException {
		String deleteQuery = "DELETE FROM members WHERE username='" + USERNAME
				+ "' AND PROJECT=" + PROJECT + ";";
		query(deleteQuery);
	}
}