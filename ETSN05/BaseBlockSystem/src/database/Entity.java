package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Entity {
	
	public Entity() {
		Database.getInstance();
	}
	
	/**
	 * Executes SQL-queries and returns the corresponding ResultSet
	 * @param query The query to be executed
	 * @return 
	 */
	protected ResultSet selectQuery(String query) {
		try {
			Database.getInstance();
			Statement stmt = Database.CONN.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			stmt.close();
			return rs;
		} catch (SQLException ex) {
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * Executes SQL-queries
	 * @param query
	 */
	protected void query(String query) {
		try {
			Database.getInstance();
			Statement stmt = Database.CONN.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
		} catch (SQLException ex) {
		} catch (Exception e) {
		}

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
				CONN = DriverManager.getConnection("jdbc:mysql://localhost/base?" +
	            "user=root&password=etsn05");
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

		public static synchronized Database getInstance() {
			if (INSTANCE == null) {
				INSTANCE = new Database();
			}
			return INSTANCE;
		}
		
	}
}

