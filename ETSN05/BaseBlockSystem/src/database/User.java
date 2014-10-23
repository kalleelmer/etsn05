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
	public final String PASSWORD;
	public final String FIRST_NAME;
	public final String LAST_NAME;

	/**
	 * Constructs a new user object without adding it to the database
	 */
	public User(String username, String password, String firstName,
			String lastName) {
		USERNAME = safetyInput(username);
		PASSWORD = safetyInput(password);
		FIRST_NAME = safetyInput(firstName);
		LAST_NAME = safetyInput(lastName);
	}

	/**
	 * Returns a user object via the database given a certain username
	 * 
	 * @param username
	 * @return The user that was found, otherwise null
	 */
	public static User getByUsername(String userName) throws SecurityException,
			SQLException {
		String selectQuery = "SELECT * FROM users WHERE username='" + userName
				+ "';";
		ResultSet userSet = selectQuery(selectQuery);
		if (!userSet.next()) {
			return null;
		}
			return new User(userSet.getString("username"),
					userSet.getString("password"),
					userSet.getString("firstname"),
					userSet.getString("lastname"));
	}

	/**
	 * Retrieves a list with all users that are currently in the system
	 * 
	 * @return A list of the users, or null if no users were found
	 */
	public static List<User> getAllUsers() throws SQLException {
		String selectQuery = "SELECT * FROM users;";
		ResultSet userSet = selectQuery(selectQuery);
		List<User> allUsers = new ArrayList<User>();
		while (userSet.next()) {
			User user = new User(userSet.getString("username"),
					userSet.getString("password"),
					userSet.getString("firstname"),
					userSet.getString("lastname"));
			allUsers.add(user);
		}
		if (allUsers.isEmpty()) {
			return null;
		}
		return allUsers;
	}

	/**
	 * Checks if this user is admin
	 * 
	 * @return true if selected user is admin, otherwise false
	 */
	public boolean isAdmin() throws SQLException {
		return USERNAME.equals("admin");
	}

	/**
	 * 
	 * @throws IllegalArgumentException
	 */
	public void insert() throws SQLException {
		String insertQuery = "INSERT INTO users SET username='" + USERNAME
				+ "',PASSWORD='" + PASSWORD + "',firstname='" + FIRST_NAME
				+ "',lastname='" + LAST_NAME + "';";
		query(insertQuery);
	}

	/**
	 * Updates a user with new information to the database
	 */
	public void update() throws SecurityException, IllegalArgumentException,
			SQLException {
		if (isAdmin()) {
			throw new SecurityException();
		}
		String updateQuery = "UPDATE users SET password='" + PASSWORD
				+ "' WHERE username ='" + USERNAME + "';";
		query(updateQuery);
	}

	/**
	 * Deletes a user from the database
	 */
	public void delete() throws SecurityException, IllegalArgumentException,
			SQLException {
		if (isAdmin()) {
			throw new SecurityException();
		}
		String selectQuery = "SELECT username,password FROM users WHERE username='"
				+ USERNAME + "' AND PASSWORD='" + PASSWORD + "';";
		ResultSet userSet = selectQuery(selectQuery);
		if (!userSet.next()) {
			throw new IllegalArgumentException();
		}
		String deleteUsersQuery = "DELETE FROM users WHERE username='"
				+ USERNAME + "' AND password='" + PASSWORD + "';";
		query(deleteUsersQuery);
		String deleteMembersQuery = "DELETE FROM members WHERE username='"
				+ USERNAME + "';";
		query(deleteMembersQuery);
	}
}
