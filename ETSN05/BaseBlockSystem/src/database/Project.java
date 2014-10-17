package database;

/**
 * Project class...
 * @author etsn05
 *
 */
public class Project {
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
	
	
	
}
