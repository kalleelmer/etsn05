package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The user class is the object representation of the user in the database and
 * has the same parameters as the table users. It knows itself it it is admin or
 * not and can be updated with new information.
 */
public class User extends Entity {
	public final String USERNAME;
	private final String PASSWORD;
	public final String FIRST_NAME;
	public final String LAST_NAME;

	/**
	 * Constructs a new user object without adding it to the database
	 */
	public User(String username, String password, String firstName,
			String lastName) {
		USERNAME = username;
		PASSWORD = password;
		FIRST_NAME = firstName;
		LAST_NAME = lastName;
	}

	/**
	 * Checks if this user is admin
	 * 
	 * @return true if selected user is admin, otherwise false
	 */
	public boolean isAdmin() {
		return USERNAME == "admin";
	}

	/**
	 * Inserts the user to the database
	 */
	public void insert() {
		String query = "INSERT INTO users (username, password, firstname, lastname) VALUES('"
				+ USERNAME
				+ "', '"
				+ PASSWORD
				+ "','"
				+ FIRST_NAME
				+ "','"
				+ LAST_NAME + "')";
		query(query);
	}

	/**
	 * Returns a user object via the database given a certain username
	 * 
	 * @param username
	 * @return The user that was found, otherwise null
	 */
	public static User getByUsername(String username) {
		String query = "SELECT * FROM users WHERE username='" + username + "'";
		ResultSet rs = selectQuery(query);
		if (rs == null)
			return null;
		User user = null;
		try {
			rs.next();
			user = new User(rs.getString("username"), rs.getString("password"),
					rs.getString("firstname"), "lastname");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return user;
	}

	/**
	 * Retrieves a list with all users that are currently in the system
	 * 
	 * @return A list of the users
	 */
	public static List<User> getAllUsers() {
		String query = "SELECT * FROM users";
		ResultSet rs = selectQuery(query);
		List<User> allUsers = null;
		try {
			allUsers = new ArrayList<User>();
			while (rs.next()) {
				User user = new User(rs.getString("username"),
						rs.getString("password"), rs.getString("firstname"),
						rs.getString("lastname"));
				allUsers.add(user);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return allUsers;
	}

	/**
	 * Updates a user with new information to the database
	 */
	public void update() {
		String query = "UPDATE users SET password='"
				+ PASSWORD + "' WHERE username ='" + USERNAME + "'";
		query(query);
	}

	/**
	 * Deletes a user from the database
	 */
	public void delete() {
		// OBS!!!!! Var detta ett av ställena där Foreign_KEY skulle komma att
		// bli ett problem?
		//Ta bort även i members-tabellen
//		Denna kommer ändras....
		String query = "DELETE FROM users WHERE username='" + USERNAME + "'";
		query(query);
	}
}
