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
	protected boolean CLOSED;
	
	/**
	 * Constructs a new project, and automatically marks it as an open project
	 * @param id
	 * @param name
	 */
	public Project(int id, String name) {
		ID = id;
		NAME = name;
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
	public Project(int id, String name, boolean closed) {
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
	public static List<Project> getByUser(String username) throws SQLException, Exception {
		if (username.equals("admin")) {
			String selectQuery = "SELECT * FROM projects;";
			ResultSet rs = selectQuery(selectQuery);
			List<Project> list = new ArrayList<Project>();
			while (rs.next()) {
				Project project = new Project(rs.getInt("id"),
						rs.getString("name"),
						rs.getBoolean("closed"));
				list.add(project);
			}
			return list;
		}
		String selectQuery = "SELECT project FROM members WHERE username='"
				+ username + "'";
		ResultSet memberSet = selectQuery(selectQuery);
		List<Project> foundList = new ArrayList<Project>();
		while (memberSet.next()) {
			//Itererar igenom resultset
			String projectQuery = "SELECT * FROM projects WHERE id="
					+ memberSet.getInt("project");
			ResultSet projectSet = selectQuery(projectQuery);
			projectSet.next();
			Project project = new Project(projectSet.getInt("id"),
					projectSet.getString("name"),
					projectSet.getBoolean("closed"));
			foundList.add(project);
		}
		if (foundList.size() == 0) {
			//Gav inga träffar
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
	public static Project getByID(int id) throws SQLException, Exception {
		String selectQuery = "SELECT * FROM projects WHERE id=" + id;
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
	public void insert() throws SQLException, Exception {
		String insertQuery = "INSERT INTO projects(name,closed) VALUES('" + NAME.replaceAll(INPUTSAFETY, "") + "'," + CLOSED + ")";
		query(insertQuery);
	}

	/**
	 * Updates a current project with new information in the database
	 */
	public void update() throws SQLException, Exception {
		// Detta behöver nog göras om. Skulle hända olika grejer beroende på om
		// det fanns ett projekt
		// eller inte.... Kolla STLDD
		String updateQuery = "UPDATE projects SET id=" + ID + ",name ='" + NAME.replaceAll(INPUTSAFETY,"")
				+ "',closed=" + CLOSED + ") WHERE id=" + ID;
		query(updateQuery);
	}
	
	public static void main(String[] args) {
		Project p3 = new Project(2, "funnyproject");
		Project p4 = new Project("boringproject");
		try {
			p3.insert();
			p4.insert();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

