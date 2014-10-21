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
		html +="<li> <a href=" + formElement("MemberList") + ">" + s.NAME + " ID:" + s.ID + "</a>" + "</li>";
	}
	html += "</ol>";
	return html;
}
protected String closeProjectRequestForm() {
	//Project id = request.getParameter("id");
	String htmlD = "<html><body>";
	htmlD += "<p><form name=" + formElement("id");
	htmlD += " method=" + formElement("get")+">";
	htmlD += "Project ID: <imput type=" + formElement("number")  + "name=" + formElement("create") + " value=" + formElement("Project ID") + ">";
	htmlD += "<imput type=" + formElement("submit") + " value=" +formElement("Close Project2") + ">";
	htmlD += "</form></p></body>";
	/*htmlD += "<form name=" + formElement("ID") + "action=" + formElement(Project.getByID(id)) + " method=" + formElement("get") + ">";
	htmlD += "ID of project: <imput type=" + formElement("text") + " name=" + formElement("id") +">";
	htmlD += "<imput type=" + formElement("submit") + " value=" + formElement("Close");
	htmlD += "</form>";*/
	return htmlD;
}
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	HttpSession session = request.getSession(true);
	PrintWriter out = response.getWriter();
	out.println(getPageIntro());
	String myName = "";
	Object nameObj = session.getAttribute("name");
	Object createNewProject = request.getParameter("createNewProject");
	Object deleteProject = request.getParameter("deleteProject");
	if(createNewProject!=null){
		System.out.print("create");
	}
	if(deleteProject!=null){
		out.println(closeProjectRequestForm());
	}
	if (nameObj != null) {
		myName = (String)nameObj;
		}
		if(!loggedIn(request)) {
			response.sendRedirect("LogIn");
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
				String htmlA = "<html><body><form action=" + formElement("") +">";
				htmlA += "<input type=" + formElement("submit") + "name=" + formElement("createNewProject");
				htmlA += " value=" +formElement("Create new project") +">";
				htmlA += "</form>";
				htmlA += "<form action=" + formElement("") + ">";
				htmlA += "<input type=" + formElement("submit") +"name=" + formElement("deleteProject");
				htmlA += " value=" +formElement("Close project") +">";
				htmlA += "</form>";
				out.println(htmlA);
				//out.println("<p><a href =" + formElement("CreateProject") + "> Create new project </p>");
				//out.println("<p><a href =" + formElement("DeleteProject") + "> Delete project </p>");
			}
			out.println("<p><a href =" + formElement("LogIn") + "> Log out </p>");
		}
	}
}
