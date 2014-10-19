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
	protected static final String inputSafety = "[^-abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖ0123456789]";
	protected static final String usernameSyntax = "(\\W)";
	protected static final String passwordSyntax = "[^a-z]";
	
	public Entity() {
		Database.getInstance();
	}
	
	protected static boolean checkUsername(String username) {
		String test = username.replaceAll(usernameSyntax, "");
		return test.length() == username.length() && test.length() <= 10 && test.length() >= 5;
	}
	
	protected static boolean checkPassword(String password) {
		String test = password.replaceAll(passwordSyntax, "");
		return test.length() == password.length() && test.length() == 6;
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
		// Denna statement stängs aldrig, ny öppnas varje gång, se över detta?
		Statement stmt = Database.CONN.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}
	
	/**
	 * Executes SQL-queries
	 * @param query
	 */
	protected static void query(String query) throws SQLException, Exception {
		Database.getInstance();
		Statement stmt = Database.CONN.createStatement();
		stmt.executeUpdate(query);
	}
	
	/**
	 * This class is a singleton that creates the connection to the database.
	 * Once the getInstance() method is run this class cannot be created again, and will return null instead.
	 * When the database is created, getInstance() will only return the current database class
	 * which doesn't include any variables or data.
	 * @author etsn05
	 *
	 */
	private static class Database {
		private static Database INSTANCE = null;
		private static Connection CONN;
		
		private Database() {
			try{
	    		DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
				CONN = DriverManager.getConnection("jdbc:mysql://vm26.cs.lth.se/puss1402?" +
	            "user=puss1402&password=pwi8ww1k");
				if (CONN == null) {
					System.out.print("Skit också");
				}
			} catch (SQLException ex) {
			    System.out.println("SQLException: " + ex.getMessage());
			    System.out.println("SQLState: " + ex.getSQLState());
			    System.out.println("VendorError: " + ex.getErrorCode());
			} catch (Exception e) {
				System.out.println("SQL error: " + e.getMessage());
			}
	    }
		
		/**
		 * The only accessible function, which controls whether or not a database object already has been created.
		 * @return
		 */
		public static synchronized Database getInstance() {
			if (INSTANCE == null) {
				INSTANCE = new Database();
			}
			return INSTANCE;
		}
		
	}
}

