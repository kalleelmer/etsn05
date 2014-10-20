package web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import database.Member;
import database.Member.Role;
import database.Project;
import database.TimeReport;
import database.User;

@WebServlet("/TimeReportList")
public class TimeReportList extends servletBase {

	public TimeReportList() {
	}
	
	 /**
     * Generates a form for Time Report  List. 
     * @param logged in user
     * @return HTML code for the form
	 * @throws Exception 
	 * @throws SQLException 
	 * @throws SecurityException 
     */
    protected String timeReportListRequestForm(String userName) {
    	//Retrieving projects that user is member in
    	List<Project> projects=null;
    	try {
    		projects=Project.getByUser(userName);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
        List<String> users = Arrays.asList("", "User", "Project","Start date","End date","Summarize by");

    	
    	String html = "";
    	List<String> summary = Arrays.asList("Week","Activity Type","Project");
        List<String> tableRow1 = Arrays.asList("", "User", "Project","Start date","End date","Summarize by");
    	List<String> tableRow2 = Arrays.asList("Display",dropDownMenu(users,"name",""),
    			dropDownMenu(projects,"project","all projects"),
    			"<input type="+formElement("text") +"name="+formElement("startDate")+">",
    			"<input type="+formElement("text") +"name="+formElement("endDate")+">",
    			dropDownMenu(summary,"summary","all"));
    	String[] tableRow3 = {"ID","Project","User","Activity type","Date","Duration"};
    	html = "<p> <form name=" + formElement("input");
    	html += " method=" + formElement("get")+">";
    	html += "<table style="+formElement("width:80%")+tableRow(tableRow1)+"</tr><tr>"+tableRow(tableRow2);
    	html += "<td><input type=" + formElement("submit") + "value=" + formElement("update") + "></td></tr>";
    	html += "</table>";
    	html += "</form>";
    	return html;
    			
    }
    
    private List<User> getUserList(String userName) throws SQLException, Exception {
    	return null;
	}

	protected String newTimeReportRequestForm(String userName) {    
    	//Retrieving projects that user is member in
		List<Project> projects=null;
    	try {
    		projects=Project.getByUser(userName);
    	}catch(Exception e){
			e.printStackTrace();
    	}
    	
    	String html = "";
        List<String> activityType = Arrays.asList("1", "2","3","4");
        List<String> tableRow1 = Arrays.asList("Project", "Activity type","Date","Duration");
    	List<String> tableRow2 = Arrays.asList(dropDownMenu(projects,"createNewProject","select project"),
    			dropDownMenu(activityType,"createNewType","select activity type"),
    			"<input type="+formElement("text") +"name="+formElement("createNewDate")+">",
    			"<input type="+formElement("text") +"name="+formElement("createNewDuration")+">");
    	html = "<p> <form name=" + formElement("input");
    	html += " method=" + formElement("get")+">";
    	html += "<table style="+formElement("width:80%")+tableRow(tableRow1)+"</tr><tr>"+tableRow(tableRow2);
    	html += "<td><input type=" + formElement("submit") + "name=" + formElement("create") +"value="+formElement("process")+ "></td></tr>";
    	html += "</table>";
    	html += "</form>";
    	return html;
    			
    }
    
    
	 /**
     * Generates a form for Time Report  List. 
     * @param logged in user
     * @return HTML code for the form
     */
    protected String generateTimeReportList(String userName) {
    	return null;
    			
    }
    
    /**
     * Generates a table row with elements in list. Have to be enclosed with </tr>
     * @param list
     * @return
     */
    private String tableRow(List<String> list){
    	String html = "<tr>";
    	for (String s:list){
    		html +="<td>" +s + "</td>";
    	}
    	return html;
    }
    

    private <E> String getString(E s){
    	if (s instanceof String){
    		return (String) s;
    	}else if(s instanceof User){
    		return ((User) s).USERNAME;
    	}else if(s instanceof Project){
    		return ((Project) s).ID + "- "+ ((Project) s).NAME;
    	}
    	else{
    		return "";
    	}
    }
    
    private <E> String dropDownMenu(List<E> list, String name,String intro){
    	String html = "<select name ="+formElement(name)+">";
    	html += "<option value="+intro+">"+intro+"</option>";
    	if (list==null){
        	html += "</select>";
    		return html;
    	}
    	for (E s:list){
    		String f = formElement(getString(s));
    		html += "<option value="+f+">"+f+"</option>";
    	}
    	html += "</select>";
    	return html;
    }
    
 
    
  
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	HttpSession session = request.getSession(true);
    	String userName = (String) session.getAttribute("name");
    	Object update = request.getParameter("update");
    	String create = (String) request.getParameter("create");
    	
    	String createNewProject = request.getParameter("createNewProject");
    	String createNewType = request.getParameter("createNewType");
    	String createNewDate = request.getParameter("createNewDate");
    	String createNewDuration = request.getParameter("createNewDuration");

		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
    	out.println("<html><body><p>Time Report List:</p>");    	

    	
		// check that the user is logged in
			if (!loggedIn(request)){
				response.sendRedirect("LogIn");
			}else{
				if(update==null&&create==null){
					out.println(timeReportListRequestForm(userName));
				}else if(create!=null){
					if(create.equals("init")){
						out.println(newTimeReportRequestForm(userName));
					}else if (create.equals("process")){
						out.println(createTimeReport(userName,createNewProject,createNewType,createNewDate,createNewDuration));
						out.println(newTimeReportRequestForm(userName));
					}
				}else if(update!=null){
					
				}
				out.println("<p><a href =" + formElement("TimeReportList?create=init")+"> Create new time report </p>");
				out.println("<p><a href =" + formElement("TimeReportList")+"> Time report List</p>");
				out.println("<p><a href =" + formElement("LogIn") + "> Log out </p>");
				out.println("</body></html>");
			}
		
	}
    

	private String createTimeReport(String userName,String project,String activityType,String date,String duration) {
		Date dateObj=null;
		int durationInt = 0;
		int projectID = 0;
		try{
			dateObj = Date.valueOf(date);
		}catch(Exception e){
			return "<p>Date format: yyyy-mm-dd</p>";
		}
		try{
			durationInt = Integer.parseInt(duration);
		}catch(Exception e){
			return "<p>Fill in duration (minutes)</p>";
		}
		
		if (!project.equals("select")){
			String[] tmp = project.split("-",2);
			projectID = Integer.parseInt(tmp[0]);
		}else{
			return "<p>Select a project</p>";
		}
		if (activityType.equals("select")){
			return "<p>Select a activity type</p>";
		}
		TimeReport newTimeReport = new TimeReport(1,userName, projectID, getRole(userName,projectID),
				Integer.parseInt(activityType), dateObj, durationInt, null);
		try {
			newTimeReport.insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "<p>Time report created</p>";
	}
	
    private Role getRole(String userName, int project){
    	try {
			for (Member m :Member.getMembers(project)){
				if (m.USERNAME==userName){
					return m.ROLE;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
	

	

}