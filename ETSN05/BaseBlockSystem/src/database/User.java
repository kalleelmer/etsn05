package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
		String query = "INSERT INTO users (name, password) VALUES('" + USERNAME + "', '" + 
                PASSWORD + "')";
		query(query);
	}
	
	public static User getByUsername(String username) {
		String query = "SELECT * FROM users WHERE name='" + username + "'";
		ResultSet rs = selectQuery(query);
		if (rs == null)
			return null;
		User user = null;
		try {
			rs.next();
			user = new User(rs.getString("name"), rs.getString("password"), "prel", "prel", true);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return user;
	}

	public static List<User> getAllUsers() {
		String query = "SELECT * FROM users";
		ResultSet rs = selectQuery(query);
		List<User> allUsers = null;
		try {
			allUsers = new ArrayList<User>();
			while (rs.next()) {
				User user = new User(rs.getString("name"), rs.getString("password"), "prel", "prel", true);
				allUsers.add(user);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return allUsers;
	}
	
	public void update() {
		String query = "UPDATE users SET name ='" + USERNAME + "', password='" + PASSWORD +"' WHERE name ='" + USERNAME + "'";
		query(query);
	}
	
	public void delete() {
		String query = "DELETE FROM users WHERE name='" + USERNAME + "'";
		query(query);
	}
}
