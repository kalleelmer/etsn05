package web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import database.Project;

@WebServlet("/ProjectList")
public class ProjectList extends servletBase {

	/**
	 * Instantiate a project list servlet
	 */
	public ProjectList() {
		// TODO Auto-generated constructor stub
	}

	protected String projectListRequestForm(List<Project> list) {
		String html = "<html><body><p>Project List:</p>";
		html += "<ol>";
		for (Project p : list) {
			html += "<li> <a href="
					+ formElement("MemberList" + "?project=" + p.ID) + ">"
					+ p.NAME + " ID:" + p.ID + "</a>" + "</li>";
		}
		html += "</ol>";
		return html;
	}

	protected String closeProjectRequestForm() {
		// Project id = request.getParameter("id");
		String htmlD = "<html><body>";
		htmlD += "<p>Close Project</p>" + "<p>Project ID:</p>";
		htmlD += "<p><form name=" + formElement("id");
		htmlD += " method=" + formElement("get") + ">";
		htmlD += "<imput type=" + formElement("number") + "name="
				+ formElement("close") + " value=" + formElement("Project ID")
				+ ">";
		htmlD += "<imput type=" + formElement("submit") + "onclick=" + formElement("return confirm('Are you sure?')") + " value="
				+ formElement("Close Project2") + ">";
		htmlD += "</form></p></body>";
		return htmlD;
	}
	
	protected String newProjectRequestForm() {
		/*String htmlC = "<html>";
		htmlC = "<p>Create new project</p>";
		htmlC += "<form name=" + formElement("newProject");
		htmlC += " method=" + formElement("get") + ">";
		htmlC += "Project ID: <imput type=" + formElement("text") + "name="
				+ formElement("createID") + " value=" + formElement("Project ID")
				+ "><br>";
		htmlC += "Project Name: <imput type=" + formElement("text") + "name="
				+ formElement("createName") + " value=" + formElement("Project name")
				+ "><br>";
		htmlC += "<imput type=" + formElement("submit") + "onclick=" + formElement("return confirm('Are you sure?')") + " value="
				+ formElement("Create project") + ">";
		htmlC += "</form></html>";
		return htmlC;*/
		String html;
		html = "<p>Add new user: <br><table border=" + formElement("1") + "><tr><td>";
		html += "<form name=" + formElement("input");
		html += " method=" + formElement("get");
		html += "<p> Project ID: <input type=" + formElement("text") + " name=" + formElement("addname") + '>';    	
		html += "<p> First name: <input type=" + formElement("text") + " name=" + formElement("firstname") + '>';    	
		html += "<p> Last name: <input type=" + formElement("text") + " name=" + formElement("lastname") + '>';    	    	
		html += "<input type=" + formElement("submit") + "value=" + formElement("Add user") + '>';
		html += "</form></td></tr></table><br>";
		return html;
	}

	protected void doGet(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
		String myName = "";
		Object nameObj = session.getAttribute("name");
		Object createNewProject = request.getParameter("createNewProject");
		Object deleteProject = request.getParameter("deleteProject");
		Object close = request.getParameter("close");
		Object createID = request.getParameter("createID");
		Object createName = request.getParameter("createName");
		if (!loggedIn(request)) {
			response.sendRedirect("LogIn");
		}
		
		if (deleteProject != null) {
			out.println(closeProjectRequestForm());
		}
		if (createNewProject != null){
			out.println(newProjectRequestForm());
		}
		if (nameObj != null) {
			myName = (String) nameObj;
		}
		
		if (createID != null && createName != null) {
		/*	Project p = new Project((Integer) createID,(String) createName);
			try {
				p.insert();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		if (close != null) {
			/*Project p;
			try {
				p = Project.getByID((Integer) close);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			p.CLOSED = true;*/
		}

		else {
			List<Project> list = null;
			try {
				list = Project.getByUser(myName);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			out.print(projectListRequestForm(list));
			if (myName.equals("admin")) {
				String htmlA = "<html><body><form action=" + formElement("")
						+ ">";
				htmlA += "<input type=" + formElement("submit") + "name="
						+ formElement("createNewProject");
				htmlA += " value=" + formElement("Create new project") + ">";
				htmlA += "</form>";
				htmlA += "<form action=" + formElement("") + ">";
				htmlA += "<input type=" + formElement("submit") + "name="
						+ formElement("deleteProject");
				htmlA += " value=" + formElement("Close project") + ">";
				htmlA += "</form>";
				out.println(htmlA);
			}
			out.println("<p><a href =" + formElement("LogIn")
					+ "> Log out </p>");
		}
	}
}
