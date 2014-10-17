package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Project class...
 * @author etsn05
 *
 */
public class Project extends Entity {
	int ID;
	String NAME;
	boolean CLOSED;
	
	/**
	 * Constructs a new project without adding it to the database
	 * @param id
	 * @param name
	 */
	public Project(int id, String name) {
		ID = id;
		NAME = name;
		CLOSED = false;
	}
	
	public Project(int id, String name, boolean closed) {
		ID = id;
		NAME = name;
		CLOSED = closed;
	}
	
	public static List<Project> getByUser(User user) {
		return null;
	}
	
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
	
	public void insert() {
		String query = "INSERT INTO projects (id,name,closed) VALUES(" + ID + ",'" + NAME + "'," + CLOSED + ")";
		query(query);
	}
	
	public void update() {
		String query = "UPDATE projects SET id=" + ID + ",name ='" + NAME + "',closed=" + CLOSED + ") WHERE id=" + ID;
		query(query);
	}
	
	
	
}
