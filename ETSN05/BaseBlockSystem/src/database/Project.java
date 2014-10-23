package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is one of the subclasses that extends entity. It has members associated with it 
 * via the members table in the database. 
 * @author etsn05
 *
 */
public class Project extends Entity {
	public final int ID;
	public final String NAME;
	public boolean CLOSED;
	
	/**
	 * Constructs a new project, and automatically marks it as an open project
	 * @param id
	 * @param name
	 */
	public Project(int id, String name) {
		ID = id;
		NAME = safetyInput(name);
		CLOSED = false;
	}
	
	/**
	 * Constructs a new project, and automatically marks it as an open project
	 * @param name
	 */
	public Project(String name) {
		ID = 0;
		NAME = name;
		CLOSED = false;
	}
	
	/**
	 * Constructs a new project, but with the option to have it either open or closed
	 * @param id
	 * @param name
	 * @param closed
	 */
	private Project(int id, String name, boolean closed) {
		ID = id;
		NAME = name;
		CLOSED = closed;
	}

	/**
	 * Given a specific username, this method returns a list of all the projects
	 * the user with that username currently is enrolled in
	 * @param username
	 * @return
	 */
	public static List<Project> getByUser(String username) throws SQLException {
		if (username.equals("admin")) {
			String selectQuery = "SELECT * FROM projects;";
			ResultSet projectSet = selectQuery(selectQuery);
			List<Project> list = new ArrayList<Project>();
			while (projectSet.next()) {
				Project project = new Project(projectSet.getInt("id"),
						projectSet.getString("name"),
						projectSet.getBoolean("closed"));
				list.add(project);
			}
			return list;
		}
		String selectQuery = "SELECT project FROM members WHERE username='"
				+ username + "';";
		ResultSet memberSet = selectQuery(selectQuery);
		List<Project> foundList = new ArrayList<Project>();
		while (memberSet.next()) {
			String projectQuery = "SELECT * FROM projects WHERE id="
					+ memberSet.getInt("project") + ";";
			ResultSet projectSet = selectQuery(projectQuery);
			projectSet.next();
			Project project = new Project(projectSet.getInt("id"),
					projectSet.getString("name"),
					projectSet.getBoolean("closed"));
			foundList.add(project);
		}
		if (foundList.size() == 0) {
			return null;
		} else {
			return foundList;
		}
	}
	
	/**
	 * Returns a project from the database that has the given id
	 * @param id
	 * @return The corresponding project, otherwise null
	 */
	public static Project getByID(int id) throws SQLException {
		String selectQuery = "SELECT * FROM projects WHERE id=" + id + ";";
		ResultSet projectSet = selectQuery(selectQuery);
		if (!projectSet.next())
			return null;
		Project project = new Project(projectSet.getInt("id"),
				projectSet.getString("name"), projectSet.getBoolean("closed"));
		return project;
	}

	/**
	 * Inserts the project to the database
	 */
	public void insert() throws SQLException {
		String insertQuery = "INSERT INTO projects SET name='"
				+ NAME + "',closed=" + (CLOSED ? 1 : 0) + ";";
		query(insertQuery);
	}

	/**
	 * Updates a current project with new information in the database
	 */
	public void update() throws SQLException {
		System.out.println(ID);
		String updateQuery = "UPDATE projects SET name ='" + NAME
				+ "',closed=" + (CLOSED ? 1 : 0) + " WHERE id=" + ID + ";";
		query(updateQuery);
	}
}


