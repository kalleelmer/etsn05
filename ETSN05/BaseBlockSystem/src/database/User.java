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
	 * Returns a user object via the database given a certain username
	 * @param username
	 * @return The user that was found, otherwise null
	 */
	public static User getByUsername(String userName) throws SQLException, SecurityException,
			Exception {
		userName.replaceAll(inputSafety, "");
		if (userName == "admin") {
			throw new SecurityException();
		}
		String selectQuery = "SELECT * FROM users WHERE username='" + userName
				+ "'";
		ResultSet userSet = selectQuery(selectQuery);
		if (!userSet.next()) {
			return null;
		} else {
			User user = new User(userSet.getString("username"),
					userSet.getString("password"),
					userSet.getString("firstname"),
					userSet.getString("lastname"));
			return user;
		}
	}
//	Från och med här behövs säkerhet kollas igenom (Man ska inte kunna plocka ut admin etc etc)
	/**
	 * Retrieves a list with all users that are currently in the system
	 * 
	 * @return A list of the users, or null if no users were found
	 */
	public static List<User> getAllUsers() throws SQLException, Exception {
		String selectQuery = "SELECT * FROM users";
		ResultSet userSet = selectQuery(selectQuery);
		List<User> allUsers = new ArrayList<User>();
		try {
			while (userSet.next()) {
				User user = new User(userSet.getString("username"),
						userSet.getString("password"), userSet.getString("firstname"),
						userSet.getString("lastname"));
				allUsers.add(user);
			} if (allUsers.size() == 0) {
				return null;
			} else {
				return allUsers;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Checks if this user is admin
	 * @return true if selected user is admin, otherwise false
	 */
	public boolean isAdmin() throws SQLException, Exception{
		String selectQuery = "SELECT username,password FROM users WHERE username='admin'";
		ResultSet userSet = selectQuery(selectQuery);
		if (!userSet.next()) {
			return false;
		} else {
			return USERNAME == userSet.getString("username") && PASSWORD == userSet.getString("password");
		}
	}

	/**
	 * 
	 * @throws IllegalArgumentException
	 */
	public void insert() throws IllegalArgumentException, SQLException, Exception {
		if (checkUsername(USERNAME) && checkPassword(PASSWORD)) {
			String insertQuery = "INSERT INTO users (username, password, firstname, lastname) VALUES('"
					+ USERNAME
					+ "', '"
					+ PASSWORD
					+ "','"
					+ FIRST_NAME.replaceAll(inputSafety, "")
					+ "','"
					+ LAST_NAME.replaceAll(inputSafety, "") + "')";
			query(insertQuery);
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Updates a user with new information to the database
	 */
	public void update() throws IllegalArgumentException, SQLException, Exception {
		if (checkUsername(USERNAME) && checkPassword(PASSWORD)) {
			String updateQuery = "UPDATE users SET password='" + PASSWORD
					+ "' WHERE username ='" + USERNAME + "'";
			query(updateQuery);
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Deletes a user from the database
	 */
	public void delete() throws SQLException, Exception{
		// OBS!!!!! Var detta ett av ställena där Foreign_KEY skulle komma att
		// bli ett problem?
		//Ta bort även i members-tabellen
//		Denna kommer ändras....
		String deleteUsersQuery = "DELETE * FROM users WHERE username='" + USERNAME + "' AND password='" + PASSWORD + "'";
		query(deleteUsersQuery);
		String deleteMembersQuery = "DELETE * FROM members WHERE username='" + USERNAME + "'";
		query(deleteMembersQuery);
		
	}
}
