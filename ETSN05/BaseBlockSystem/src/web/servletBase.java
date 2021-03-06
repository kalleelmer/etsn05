package web;

import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This class is the superclass for all servlets in the application. It includes
 * basic functionality required by many servlets, like for example a page head
 * written by all servlets, and the connection to the database.
 * 
 * This application requires a database. For username and password, see the
 * constructor in this class.
 * 
 * <p>
 * The database can be created with the following SQL command: mysql> create
 * database base;
 * <p>
 * The required table can be created with created with: mysql> create table
 * users(name varchar(10), password varchar(10), primary key (name));
 * <p>
 * The administrator can be added with: mysql> insert into users (name,
 * password) values('admin', 'adminp');
 * 
 * @author Martin Host
 * @version 1.0
 * 
 */
public class servletBase extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// Define states
	protected static final int LOGIN_FALSE = 0;
	protected static final int LOGIN_TRUE = 1;
	protected Connection conn = null;

	/**
	 * Constructs a servlet and makes a connection to the database
	 */
	public servletBase() {
	}

	/**
	 * Checks if a user is logged in or not.
	 * 
	 * @param request
	 *            The HTTP Servlet request (so that the session can be found)
	 * @return true if the user is logged in, otherwise false.
	 */
	protected boolean loggedIn(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		Object objectState = session.getAttribute("state");
		int state = LOGIN_FALSE;
		if (objectState != null)
			state = (Integer) objectState;
		Calendar loginTime = (Calendar) session.getAttribute("login_time");
		Calendar now = Calendar.getInstance();
		if (loginTime == null) return false;
		long ltMillis = loginTime.getTimeInMillis();
		long nMillis = now.getTimeInMillis();
		long diff = nMillis - ltMillis;
		long twentyminutes = 20 * 60 * 1000;
		if (diff > twentyminutes) return false;
		session.setAttribute("login_time", now);
		if (LogIn.checkUser((String)session.getAttribute("name"), (String)session.getAttribute("password")) != LogIn.LoginStatus.ok) return false;
		return (state == LOGIN_TRUE);
	}

	/**
	 * Can be used to construct form elements.
	 * 
	 * @param par
	 *            Input string
	 * @return output string = "par"
	 */
	protected String formElement(String par) {
		return '"' + par + '"';
	}

	/**
	 * Constructs the header of all servlets.
	 * 
	 * @return String with html code for the header.
	 */
	protected String getPageIntro() {
		String intro = "<html>"
				+ "<head><title>ETSN05 1402</title><style>a { color: #6A5A5B; }</style></head>"
				+ "<body bgcolor=\"#F5F4F4\"><font color=\"#6A5A5B\">";
		return intro;
	}

}
