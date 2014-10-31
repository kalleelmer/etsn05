package web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import database.Project;

@WebServlet("/ProjectList")
public class ProjectList extends servletBase {

	protected String projectListRequestForm(List<Project> list) {
		if (list == null) return "";
		String html = "<html><body><p>Project List:</p>";
		html += "<table border = " + formElement("1") + "><tr><td>ID</td><td>Name</td><td>Active</td>";
		for (Project p : list) {
			html += "<tr><td>" + p.ID + "</td> <td> <a href="
					+ formElement("MemberList" + "?project=" + p.ID) + ">"
					+ p.NAME + "</a>" + "</td><td>" + !p.CLOSED + "</td></tr>";
		}
		html += "</table>";
		return html;
	}

	protected String closeProjectRequestForm(List<Project> list) {
		String html;
		html = "<p>Close project: <br><table border=" + formElement("1")
				+ "><tr><td>";
		html += "<form name=" + formElement("input");
		html += " method=" + formElement("get");
		html += "<form action=''>";
		html += "<select name=" + formElement("close") + ">";
		for(Project p : list){
			html += "<option value=" + formElement(p.ID+"") + ">" + formElement(p.NAME).replace("\"", "") + " ID=" + formElement(p.ID + "") + "</option>";
		}
		html += "</select><input type=" + formElement("submit") + "onclick="
				+ formElement("return confirm('Are you sure?')") + "value="
				+ formElement("Close") + '>';
		html += "</form></td></tr></table><br>";
		return html;
	}

	protected String newProjectRequestForm() {
		String html;
		html = "<p>Add new project: <br> Project names should only include letters a to z, numbers and -. All other inputs will be filtered <table border=" + formElement("1")
				+ "><tr><td>";
		html += "<form name=" + formElement("input");
		html += " method=" + formElement("get");
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
		List<Project> list = null;
		if (!loggedIn(request)) {
			response.sendRedirect("LogIn");
		}
		if (nameObj != null) {
			myName = (String) nameObj;
		}
		if (myName.equals("admin")) {
			String htmlA = "<html><h1>Project List</h1><body><table border =" + formElement("1") + "><tr><td><br><form action=" + formElement("")
					+ ">";
			htmlA += "<input type=" + formElement("submit") + "name="
					+ formElement("createNewProject");
			htmlA += " value=" + formElement("Create new project") + ">";
			htmlA += "</form>";
			htmlA += "<form action=" + formElement("") + ">";
			htmlA += "<input type=" + formElement("submit") + "name="
					+ formElement("deleteProject");
			htmlA += " value=" + formElement("Close project") + ">";
			htmlA += "</form></tr></td></table>";
			out.println(htmlA);
		}

		try {
			list = Project.getByUser(myName);
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		if (list == (null)) {
			out.println("List is empty");
		}
		
		if (deleteProject != null) {
			out.println(closeProjectRequestForm(list));
			return;
		}
		if (createNewProject != null) {
			out.println(newProjectRequestForm());
			return;
		}

		if (createName != null) {
			Project p = new Project(createName);
			try {
				p.insert();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (close != null) {
			Project p = null;
			int x = Integer.parseInt(close);
			System.out.println(close);
			try {
				if(Project.getByID(x) == null) {
					out.println("No project with that ID");
				} else {
					p = Project.getByID(x);
					p.CLOSED = true;
					p.update();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

			out.print(projectListRequestForm(list));
//			if (myName.equals("admin")) {
//				String htmlA = "<html><body><form action=" + formElement("")
//						+ ">";
//				htmlA += "<input type=" + formElement("submit") + "name="
//						+ formElement("createNewProject");
//				htmlA += " value=" + formElement("Create new project") + ">";
//				htmlA += "</form>";
//				htmlA += "<form action=" + formElement("") + ">";
//				htmlA += "<input type=" + formElement("submit") + "name="
//						+ formElement("deleteProject");
//				htmlA += " value=" + formElement("Close project") + ">";
//				htmlA += "</form>";
//				out.println(htmlA);
//			}
		}
	}
}
