package web;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Random;

/**
 * Servlet implementation class Administration. 
 * Constructs a page for administration purpose. 
 * Checks first if the user is logged in and then if it is the administrator. 
 * If that is OK it displays all users and a form for adding new users.
 * 
 *  @author Martin Host
 *  @version 1.0
 */
@WebServlet("/UserList")
public class UserList extends servletBase {
	private static final long serialVersionUID = 1L;
	private static final int PASSWORD_LENGTH = 6;
       
    /**
     * @see servletBase#servletBase()
     */
    public UserList() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /**
     * generates a form for adding new users
     * @return HTML code for the form
     */
    private String addUserForm() {
    	String html;
    	html = "<table border=" + formElement("1") + "><td><tr>";
    	html = "<p>Add new user: <form name=" + formElement("input");
    	html += " method=" + formElement("get");
    	html += "<p> User name: <input type=" + formElement("text") + " name=" + formElement("addname") + '>';    	
    	html += "<p> First name: <input type=" + formElement("text") + " name=" + formElement("firstname") + '>';    	
    	html += "<p> Last name: <input type=" + formElement("text") + " name=" + formElement("lastname") + '>';    	    	
    	html += "<input type=" + formElement("submit") + "value=" + formElement("Add user") + '>';
    	html += "</form></tr></td></table>";
    	return html;
    }
    
    private String addChangePWForm(String name) {
    	String html;
    	html = "<p><form name=" + formElement("input");
    	html += " method=" + formElement("get");
    	html += "<p><input type=" + formElement("text") + " name=" + formElement("password") + '>';    	
    	html += "<input type='hidden' name=" + formElement("name") + " value=" + formElement(name) + '>';
    	html += "<input type=" + formElement("submit") + " value='Change PW'>";
    	html += "</form>";
    	return html;
    }
    
    /**
     * Checks if a username corresponds to the requirements for user names. 
     * @param name The investigated username
     * @return True if the username corresponds to the requirements
     */
    private boolean checkNewName(String name) {
    	int length = name.length();
    	boolean ok = (length>=5 && length<=10);
    	if (ok)
    		for (int i=0; i<length; i++) {
    			int ci = (int)name.charAt(i);
    			boolean thisOk = ((ci>=48 && ci<=57) || 
    					          (ci>=65 && ci<=90) ||
    					          (ci>=97 && ci<=122));
    			//String extra = (thisOk ? "OK" : "notOK");
    			//System.out.println("bokst:" + name.charAt(i) + " " + (int)name.charAt(i) + " " + extra);
    			ok = ok && thisOk;
    		}    	
    	return ok;
    }
    
    /**
     * Creates a random password.
     * @return a randomly chosen password
     */
    private String createPassword() {
    	String result = "";
    	Random r = new Random();
    	for (int i=0; i<PASSWORD_LENGTH; i++)
    		result += (char)(r.nextInt(26)+97); // 122-97+1=26
    	return result;
    }
    
    
    /**
     * Adds a user and a randomly generated password to the database.
     * @param name Name to be added
     * @return true if it was possible to add the name. False if it was not, e.g. 
     * because the name already exist in the database. 
     */
    private boolean addUser(String name, String firstName, String lastName) {
    	boolean resultOk = true;
    	try{
			Statement stmt = conn.createStatement();
			String statement = "insert into users (username, firstname, lastname, password) values('" + name + "', '" + firstName + "',"
					+ "'" + lastName + "', '" + 
			                     createPassword() + "')";
			System.out.println(statement);
		    stmt.executeUpdate(statement); 
		    stmt.close();
			
		} catch (SQLException ex) {
		    resultOk = false;
		    // System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
    	return resultOk;
    }
    
    /**
     * Deletes a user from the database. 
     * If the user does not exist in the database nothing happens. 
     * @param name name of user to be deleted. 
     */
    private void deleteUser(String name) {
    	try{
			Statement stmt = conn.createStatement();
			String statement = "delete from users where username='" + name + "'"; 
			System.out.println(statement);
		    stmt.executeUpdate(statement); 
		    stmt.close();
			
		} catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
    }


	/**
	 * Handles input from the user and displays information for administration. 
	 * 
	 * First it is checked if the user is logged in and that it is the administrator. 
	 * If that is the case all users are listed in a table and then a form for adding new users is shown. 
	 * 
	 * Inputs are given with two HTTP input types: 
	 * addname: name to be added to the database (provided by the form)
	 * deletename: name to be deleted from the database (provided by the URLs in the table)
	 * 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
		
		String myName = "";
    	HttpSession session = request.getSession(true);
    	Object nameObj = session.getAttribute("name");
    	if (nameObj != null)
    		myName = (String)nameObj;  // if the name exists typecast the name to a string
		
		// check that the user is logged in
		if (!loggedIn(request))
			response.sendRedirect("LogIn");
		else
			if (myName.equals("admin")) {
				String cpwPass = request.getParameter("password");
				String cpwUser = request.getParameter("name");
				if (cpwPass != null){
					Statement stmt;
					try {
						stmt = conn.createStatement();
					    int result = stmt.executeUpdate("update users set password='" + cpwPass + "' where username='" + cpwUser + "'");
					    if (result==0){
					    	out.println("Password change failed.");
					    }else{
					    	out.println("Password change successful.");
					    }
					} catch (SQLException e) {
						e.printStackTrace();
					}		 
				}
				out.println("<h1>Administration page " + "</h1>");
				
				// check if the administrator wants to add a new user in the form
				System.out.println(request.getParameter("password"));
				System.out.println(request.getParameter("name"));
				String newName = request.getParameter("addname");
				String firstName = request.getParameter("firstname");
				String lastName = request.getParameter("lastname");
				if (newName != null && firstName !=null && lastName !=null) {
					if (checkNewName(newName)) {
						boolean addPossible = addUser(newName, firstName, lastName);
						if (!addPossible)
							out.println("<p>Error: Suggested user name not possible to add</p>");
					}	else
						out.println("<p>Error: Suggested name not allowed</p>");
				}
					
				// check if the administrator wants to delete a user by clicking the URL in the list
				String deleteName = request.getParameter("deletename");
				if (deleteName != null) {
					if (checkNewName(deleteName)) {
						deleteUser(deleteName);
					}	else
						out.println("<p>Error: URL wrong</p>");
				}
				
				try {
					Statement stmt = conn.createStatement();		    
				    ResultSet rs = stmt.executeQuery("select * from users order by username asc");
				    out.println("<p>Registered users:</p>");
				    out.println("<table border=" + formElement("1") + ">");
				    out.println("<tr><td>USER NAME</td><td>PASSWORD</td><td>FIRST NAME</td><td>LAST NAME</td></tr>");
				    while (rs.next( )) {
				    	String name = rs.getString("username");
				    	String pw = rs.getString("password");
				    	String fname = rs.getString("firstname");
				    	String lname = rs.getString("lastname");
				    	String deleteURL = "UserList?deletename="+name;
				    	String deleteCode = "<a href=" + formElement(deleteURL) +
				    			            " onclick="+formElement("return confirm('Are you sure you want to delete "+name+"?')") + 
				    			            "> delete </a>";
				    	String cpwCode = addChangePWForm(name);
				    	if (name.equals("admin")){
				    		deleteCode = "";
				    		cpwCode = "";
				    	}
				    	out.println("<tr>");
				    	out.println("<td>" + name + "</td>");
				    	out.println("<td>" + pw + "</td>");
				    	out.println("<td>" + fname + "</td>");
				    	out.println("<td>" + lname + "</td>");
				    	out.println("<td>" + cpwCode + "</td>");
				    	out.println("<td>" + deleteCode + "</td>");
				    	out.println("</tr>");
				    }
				    out.println("</table>");
				    stmt.close();
				} catch (SQLException ex) {
				    System.out.println("SQLException: " + ex.getMessage());
				    System.out.println("SQLState: " + ex.getSQLState());
				    System.out.println("VendorError: " + ex.getErrorCode());
				}
				out.println(addUserForm());
				out.println("</body></html>");
			} else  // name not admin
				response.sendRedirect("blank.html");
	}

	/**
	 *
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
