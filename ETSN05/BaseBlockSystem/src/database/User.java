package database;

import java.sql.ResultSet;

/**
 * User class...
 */
public class User extends Entity {
	String USERNAME;
	String PASSWORD;
	String FIRST_NAME;
	String LAST_NAME;
	boolean ACTIVE;
	
	/**
	 * Constructs a new user object without adding it to the database
	 */
	public User(String username, String password, String firstName, String lastName, boolean active){
		USERNAME = username;
		PASSWORD = password;
		FIRST_NAME = firstName;
		LAST_NAME = lastName;
		ACTIVE = active;
	}
	
	/**
	 * Checks if this user is admin
	 * @return true if selected user is admin, otherwise false
	 */
	public boolean isAdmin() {
		return USERNAME == "admin";
	}
	
	public void insert() {
		String query = "INSERT INTO users (name,password) VALUES(" + USERNAME + "," + PASSWORD + ")";
		query(query);
	}
	
	public static User getByUsername(String username) {
		return null;
	}
}
