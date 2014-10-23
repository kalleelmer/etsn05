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
		String html;
		html = "<p>Close project: <br><table border=" + formElement("1")
				+ "><tr><td>";
		html += "<form name=" + formElement("input");
		html += " method=" + formElement("get");
		html += "<p> Project ID: <input type=" + formElement("number")
				+ " name=" + formElement("close") + '>';
		html += "<input type=" + formElement("submit") + "onclick="
				+ formElement("return confirm('Are you sure?')") + "value="
				+ formElement("Close") + '>';
		html += "</form></td></tr></table><br>";
		return html;
	}

	protected String newProjectRequestForm() {
		String html;
		html = "<p>Add new project: <br><table border=" + formElement("1")
				+ "><tr><td>";
		html += "<form name=" + formElement("input");
		html += " method=" + formElement("get");
		html += "<p> Project ID: <input type=" + formElement("number")
				+ " name=" + formElement("createID") + '>';
		html += "<p> Project name: <input type=" + formElement("text")
				+ " name=" + formElement("createName") + '>';
		html += "<input type=" + formElement("submit") + "onclick="
				+ formElement("return confirm('Are you sure?')") + "value="
				+ formElement("Create") + '>';
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
		String close = request.getParameter("close");
		String createID = request.getParameter("createID");
		String createName = request.getParameter("createName");
		if (!loggedIn(request)) {
			response.sendRedirect("LogIn");
		}

		if (deleteProject != null) {
			out.println(closeProjectRequestForm());
			return;
		}
		if (createNewProject != null) {
			out.println(newProjectRequestForm());
			return;
		}
		if (nameObj != null) {
			myName = (String) nameObj;
		}

		if (createID != null && createName != null) {
			int y = Integer.parseInt(createID);
			Project p = null;
			try {
				p = Project.getByID(y);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(p == null){
				out.println("Project ID already in use");
			}
			p = new Project(y, createName);
		}
		if (close != null) {
			Project p = null;
			int x = Integer.parseInt(close);
			try {
				if(Project.getByID(x) == null) {
					out.println("No project with that ID");
				} else {
					p = Project.getByID(x);
					p.CLOSED = true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
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
