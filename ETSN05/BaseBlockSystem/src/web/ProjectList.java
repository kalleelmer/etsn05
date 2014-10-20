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
protected String projectListRequestForm(List<Project> list){
	String html = "<html><body><p>Project List:</p>";
	html += "<ol>";
	for (Project s:list) {
		html +="<li> <a href=" + formElement("MemberList") + ">" + formElement((String)s.NAME) + "</a>" + "</li>";
	}
	html += "</ol>";
	return html;
}
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	HttpSession session = request.getSession(true);
	PrintWriter out = response.getWriter();
	out.println(getPageIntro());
	String myName = "";
	Object nameObj = session.getAttribute("name");
	if (nameObj != null) {
		myName = (String)nameObj;
		}
		if(!loggedIn(request)) {
			response.sendRedirect("LogIn");
		} 
		else {

			//String username = (String) session.getAttribute("username");
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
				out.println("<p><a href =" + formElement("CreateProject") + "> Create new project </p>");
				out.println("<p><a href =" + formElement("DeleteProject") + "> Delete project </p>");
			}
			out.println("<p><a href =" + formElement("LogIn") + "> Log out </p>");
		}
	}
}
