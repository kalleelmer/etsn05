package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is the superclass for all other classes in the database package.
 * It is the only class that communicates directly with the database.
 * 
 * @author etsn05
 * 
 */
public class Entity {
	protected static final String INPUTSAFETY = "[^-abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789]";

	protected static String safetyInput(String input) {
		if (input == null) {
			return null;
		}
		return input.replaceAll(INPUTSAFETY, "");
	}

	/**
	 * Executes SQL-queries and returns the corresponding ResultSet
	 * 
	 * @param query
	 *            The query to be executed
	 * @return
	 */
	protected static ResultSet selectQuery(String query) throws SQLException {
		Database.getInstance();
		Statement stmt = Database.getInstance().CONN.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	/**
	 * Executes SQL-queries
	 * 
	 * @param query
	 */
	protected static void query(String query) throws SQLException {
		Database.getInstance();
		Statement stmt = Database.INSTANCE.CONN.createStatement();
		stmt.executeUpdate(query);
		stmt.close();
	}

	/**
	 * This class is a singleton that creates the connection to the database.
	 * Once the getInstance() method is run this class cannot be created again,
	 * and will return the previously created database. When the database is
	 * created, getInstance() will only return the current database class which
	 * doesn't include any variables or data.
	 * 
	 * @author etsn05
	 * 
	 */
	private static class Database {
		private static Database INSTANCE = null;
		private Connection CONN;
		private static String serverURL = "jdbc:mysql://vm26.cs.lth.se/puss1402?"
				+ "user=puss1402&password=pwi8ww1k";
//		private static String serverURL = "jdbc:mysql://localhost/test_base?user=root&password=etsn05";

		private Database() throws SQLException {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			CONN = DriverManager.getConnection(serverURL);
			if (CONN == null) {
				throw new NullPointerException();
			}
		}

		/**
		 * The only accessible function, which controls whether or not a
		 * database object already has been created.
		 * 
		 * @return
		 */
		public static synchronized Database getInstance() throws SQLException {
			if (INSTANCE == null) {
				INSTANCE = new Database();
			}
			if (INSTANCE.CONN.isClosed()) {
				INSTANCE.CONN = DriverManager.getConnection(serverURL);
			}
			return INSTANCE;
		}
	}
}
