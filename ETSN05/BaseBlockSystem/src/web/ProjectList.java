package web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import database.Project;

public class ProjectList extends servletBase {

	public ProjectList() {
		// TODO Auto-generated constructor stub
	}
protected String projectListRequestForm(List<Project> list){
	String html = "<html><body><p>Project List:</p>";
	html += "<ol>";
	for (Project s:list) {
		html +="<th>" + s.NAME + "<tb>";
	}
	return "";
}
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	HttpSession session = request.getSession(true);
	PrintWriter out = response.getWriter();
	if(!loggedIn(request)) {
		response.sendRedirect("LogIn");
	} else {
		String username = (String) session.getAttribute("username");
//		List<Project> list = Project.getByUser(username);
//		out.print(projectListRequestForm(list));
		out.println("<p><a href =" + formElement("LogIn") + "> Log out </p>");
	}
}
}
