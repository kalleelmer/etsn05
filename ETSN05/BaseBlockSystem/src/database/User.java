package database;

/**
 * User class...
 */
public class User {
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
	
	
}
