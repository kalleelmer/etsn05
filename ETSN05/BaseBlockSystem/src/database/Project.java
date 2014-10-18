package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This class is one of the subclasses that extends entity. It has members associated with it 
 * via the members table in the database. 
 * @author etsn05
 *
 */
public class Project extends Entity {
	int ID;
	String NAME;
	boolean CLOSED;
	
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
	
	public static List<Project> getByUser(User user) {
		return null;
	}
	
	/**
	 * Returns a project from the database that has the given id
	 * @param id
	 * @return The corresponding project, otherwise null
	 */
	public static Project getByID(int id) {
		String query = "SELECT * FROM projects WHERE id=" + id;
		ResultSet rs = selectQuery(query);
		if (rs == null)
			return null;
		Project project = null;
		try {
			rs.next();
			project = new Project(rs.getInt("id"),rs.getString("name"),rs.getBoolean("closed"));
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
		return project;
	}
	
	/**
	 * Inserts the project to the database
	 */
	public void insert() {
		String query = "INSERT INTO projects(id,name,closed) VALUES(" + ID + ",'" + NAME + "'," + CLOSED + ")";
		query(query);
	}
	
	/**
	 * Updates a current project with new information in the database
	 */
	public void update() {
		//Detta behöver nog göras om. Skulle hända olika grejer beroende på om det fanns ett projekt 
		//eller inte.... Kolla STLDD
		String query = "UPDATE projects SET id=" + ID + ",name ='" + NAME + "',closed=" + CLOSED + ") WHERE id=" + ID;
		query(query);
	}
}
