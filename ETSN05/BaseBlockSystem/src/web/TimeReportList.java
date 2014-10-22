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
    protected String timeReportListMenuRequestForm(String userName) {
    	//Retrieving projects that user is member in
    	List<Project> projects=null;
    	List<User> users=null;
    	List<String> summary = getSummary();
    	List<String> types = getTypes();
    	try {
    		projects=Project.getByUser(userName);
    		users =getUsers(userName);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	String html = "";
        List<String> tableRow1 = Arrays.asList("", "User", "Project","Acticvity type","Start date","End date","Summarize by");
    	List<String> tableRow2 = Arrays.asList("Display",dropDownMenu(users,"filterUser","all users"),
    			dropDownMenu(projects,"filterProject","all projects"),dropDownMenu(types,"filterTypes","all activity types"),
    			"<input type="+formElement("text") +"name="+formElement("filterStartDate")+">",
    			"<input type="+formElement("text") +"name="+formElement("filterEndDate")+">",
    			dropDownMenu(summary,"filterSummary","none"));
    	html = "<p> <form name=" + formElement("input");
    	html += " method=" + formElement("get")+">";
    	html += "<table style="+formElement("width:80%")+tableRow(tableRow1)+"</tr><tr>"+tableRow(tableRow2);
    	html += "<td><input type=" + formElement("submit") + "name=" + formElement("val")+"value=" + formElement("filter") + "></td></tr>";
    	html += "</table>";
    	html += "</form>";
    	return html;
    			
    }
   
    protected String timeReportListRequestForm(String userName,String user,String project,String type,
    		String startDate,String endDate,String summary) {
    	
    	try {
    		projects=Project.getByUser(userName);
    		users =getUsers(userName);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	
    	html += "</form>";
    	return html;
    			
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
    	html += "<td><input type=" + formElement("submit") + "name=" + formElement("val") +"value="+formElement("create")+ "></td></tr>";
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
    
    private List<String> getSummary (){
    	return Arrays.asList("week","role","project","user");
    }
    
    private List<String> getTypes (){
    	return Arrays.asList("11","12","13","14");
    }
    
    private List<User> getUsers (String user) throws SecurityException, SQLException, Exception{
    	List<User> users = new LinkedList<User>();
    	List<Member> members = new LinkedList<Member>();
    	if (user.equals("admin")){
    		return User.getAllUsers();
    	}else{
    		List<Project> projects = Project.getByUser(user);
    		if(projects!=null){
    			for (Project p:projects){
    				if(getRole(user,p.ID).equals(Member.Role.valueOf("manager"))){
    					members.addAll(Member.getMembers(p.ID));
    				}
    			}
    		}
    		if(members.isEmpty()){
    			users.add(User.getByUsername(user));
    			return users;
    		}else{
    			for(Member m:members){
    				User u = User.getByUsername(m.USERNAME);
    				if(!users.contains(u)){
    					users.add(u);
    				}
    			}
    		}
    		return users;
    	}
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
    		String f = getString(s);
    		html += "<option value="+f+">"+f+"</option>";
    	}
    	html += "</select>";
    	return html;
    }
    
 
    
  
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	HttpSession session = request.getSession(true);
    	String userName = (String) session.getAttribute("name");
    	String val = request.getParameter("val");
    	
    	String createNewProject = request.getParameter("createNewProject");
    	String createNewType = request.getParameter("createNewType");
    	String createNewDate = request.getParameter("createNewDate");
    	String createNewDuration = request.getParameter("createNewDuration");
    	
    	String filterUser = request.getParameter("filterUser");
    	String filterProject = request.getParameter("filterProject");
    	String filterType = request.getParameter("filterType");
    	String filterStartDate = request.getParameter("filterStartDate");
    	String filterEndDate = request.getParameter("filterEndDate");
    	String filterSummary = request.getParameter("filterSummary");

		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
    	out.println("<html><body><p>Time Report List:</p>");    	

    	
		// check that the user is logged in
			if (!loggedIn(request)){
				response.sendRedirect("LogIn");
			}else{
				if(val==null){
					out.println(timeReportListMenuRequestForm(userName));
				}else{
					if(val.equals("createInit")){
						out.println(newTimeReportRequestForm(userName));
					}else if (val.equals("create")){
						out.println(createTimeReport(userName,createNewProject,createNewType,createNewDate,createNewDuration));
						out.println(newTimeReportRequestForm(userName));
					}else if(val.equals("update")){
						//TODO
					}else if(val.equals("remove")){
						//TODO
					}else if(val.equals("sign")){
						//TODO
					}else if(val.equals("filter")){
						out.println(timeReportListRequestForm(userName,filterUser,filterProject,filterType,filterStartDate,filterEndDate,filterSummary));
					}
				}
				out.println("<p><a href =" + formElement("TimeReportList?val=createInit")+"> Create new time report </p>");
				out.println("<p><a href =" + formElement("TimeReportList")+"> Time report List</p>");
				out.println("<p><a href =" + formElement("LogIn") + "> Log out </p>");
				out.println("</body></html>");
			}
		}


    

	private String createTimeReport(String user,String project,String type,String date,String duration) {
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
		if (type.equals("select")){
		}
		System.out.println(user);
		System.out.println(projectID);
		System.out.println(getRole(user,projectID));
		System.out.println(durationInt);

		TimeReport newTimeReport = new TimeReport(user, projectID, getRole(user,projectID),
				Integer.parseInt(type), dateObj, durationInt, "");
		try {
			newTimeReport.insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "<p>Time report created</p>";
	}
	
    private Role getRole(String user, int projectID){
    	try {
			for (Member m :Member.getMembers(projectID)){
				if (m.USERNAME.equals(user)){
					return m.ROLE;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
	

	

}