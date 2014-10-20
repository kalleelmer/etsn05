package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.rowset.CachedRowSet;

import com.sun.rowset.CachedRowSetImpl;

/**
 * This class is the superclass for all other classes in the database package.
 * It is the only class that communicates directly with the database.
 * 
 * @author etsn05
 *
 */
public class Entity {
	protected static final String INPUTSAFETY = "[^-abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖ0123456789]";
	protected static final String USERNAMESYNTAX = "(\\W)";
	protected static final String PASSWORDSYNTAX = "[^a-z]";
	
	/**
	 * Verify validity of a username according to constraints
	 * @param username The username to verify
	 * @return true if valid, false otherwise
	 */
	protected static boolean checkUsername(String username) {
		String test = username.replaceAll(USERNAMESYNTAX, "");
		//Kontrollerar att inga otillåtna tecken finns
		return test.length() == username.length() && test.length() <= 10 && test.length() >= 5;
		//Kollar att användarnamnet ligger inom tillåtna gränser
	}
	
	/**
	 * Verify validity of a password according to constraints
	 * @param password The password to verify
	 * @return true if valid, false otherwise
	 */
	protected static boolean checkPassword(String password) {
		String test = password.replaceAll(PASSWORDSYNTAX, "");
		//Kollar otillåtna värden
		return test.length() == password.length() && test.length() == 6;
		//Kollar att längden stämmer
	}

	/**
	 * Executes SQL-queries and returns the corresponding ResultSet
	 * 
	 * @param query
	 *            The query to be executed
	 * @return
	 */
	protected static ResultSet selectQuery(String query) throws SQLException, Exception{
		Database.getInstance();
		Statement stmt = Database.CONN.createStatement(); 
		ResultSet rs = stmt.executeQuery(query);
		CachedRowSet rowSet = new CachedRowSetImpl();
		//Skapar rowset som håller datan för att kunna stänga connection
		rowSet.populate(rs);
		stmt.close();
		Database.CONN.close();
		return rowSet;
	}
	
	/**
	 * Executes SQL-queries
	 * @param query
	 */
	protected static void query(String query) throws SQLException, Exception {
		Database.getInstance();
		Statement stmt = Database.CONN.createStatement();
		stmt.executeUpdate(query);
		stmt.close();
		Database.CONN.close();
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
		private static Connection CONN;
		
		private Database() throws Exception {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			String serverURL = "jdbc:mysql://vm26.cs.lth.se/puss1402?"
					+ "user=puss1402&password=pwi8ww1k";
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
		public static synchronized Database getInstance()
				throws NullPointerException, SQLException, Exception {
			if (INSTANCE == null) {
				INSTANCE = new Database();
			}
			return INSTANCE;
		}
	}
}

