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
		html +="<li>" + s.NAME + "</li>"; //bör detta vara länkar till de olika projekten?
	}
	html += "</ol>";
	return html;
}
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	HttpSession session = request.getSession(true);
	PrintWriter out = response.getWriter();
	out.println(getPageIntro());
	String myName = "";
	Object nameObj = session.getAttribute("username");
	if (nameObj != null) {
		myName = (String)nameObj;
	}
	if(!loggedIn(request)) {
		response.sendRedirect("LogIn");
	} 
	 else {
	
		//String username = (String) session.getAttribute("username");
		List<Project> list = Project.getByUser(myName);
		out.print(projectListRequestForm(list));
		if (myName.equals("admin")) {
			out.println("<p><a href =" + formElement("CreateProject") + "> Create new project </p>");
			out.println("<p><a href =" + formElement("DeleteProject") + "> Delete project </p>");
		}
		out.println("<p><a href =" + formElement("LogIn") + "> Log out </p>");
	}
}
}
