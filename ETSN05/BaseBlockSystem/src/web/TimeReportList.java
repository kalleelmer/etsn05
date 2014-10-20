package web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/TimeReportList")
public class TimeReportList extends servletBase {

	/**
	 * Instantiate a time report list servlet
	 */
	public TimeReportList() {
		// TODO Auto-generated constructor stub
	}
	
	 /**
     * Generates a form for Time Report  List. 
     * @return HTML code for the form
     */
    protected String timeReportListRequestForm() {
    	String html = "<html><body><p>Time Report List:</p>";
    	String[] tableRow1 = {"","User","Project","Start date","End date","Summarize by"};
    	String[] tableRow2 = {"Display",dropDownMenu(tableRow1,"wow","wow"),dropDownMenu(tableRow1,"wow","wow")
    			,dropDownMenu(tableRow1,"wow","wow"),dropDownMenu(tableRow1,"wow","wow"),dropDownMenu(tableRow1,"wow","wow")};
    	String[] tableRow3 = {"ID","Project","User","Activity type","Date","Duration"};
    	html += "<table style="+formElement("width:80%")+tableRow(tableRow1)+"</tr>"+tableRow(tableRow2);
    	html += "<td><input type=" + formElement("submit") + "value=" + formElement("Update") + "></td></tr>";
    	html += tableRow(tableRow3)+"</tr>";
    	html += "</table>";
    	return html;
    			
    }
    
    /**
     * Generates HTML code for table rows
     * @param list String array
     * @return String containing HTML syntax
     */
    private String tableRow(String[] list){
    	String html = "<tr>";
    	for (String s:list){
    		html +="<td>" +s + "</td>";
    	}
    	return html;
    }
    
    /**
     * TEMPORARY: REMOVE BEFORE FINAL RELEASE
     */
    private List<String> tmp(){
    	LinkedList<String> tmp = new LinkedList<String>();
    	tmp.add("hej");
    	tmp.add("he3dj");
    	tmp.add("hedj");
    	return tmp;
    }
    
    /**
     * Produces HTML code for the drop down menu
     * @param list Array of drop down options
     * @param name Name of the field to select
     * @param intro Start value
     * @return String containing HTML syntax
     */
    private String dropDownMenu(String[] list, String name,String intro){
    	String html = "<select name ="+formElement(name)+">";
    	html += "<option value="+intro+">"+intro+"</option>";
    	for (String s:list){
    		String f = formElement(s);
    		html += "<option value="+f+">"+f+"</option>";
    	}
    	html += "</select>";
    	return html;
    }
    
    private String generateList(List<String> list, String name,String intro){
    	return "";
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
    	String myName = "";
    	HttpSession session = request.getSession(true);
    	Object nameObj = session.getAttribute("name");
    	if (nameObj != null)
    		myName = (String)nameObj;  // if the name exists typecast the name to a string
		int state;

		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
    	
		
		if (!loggedIn(request))
			response.sendRedirect("LogIn");
		else
			out.println(timeReportListRequestForm());
			out.println("<p><a href =" + formElement("NewTimeReport") + "> Create new time report </p>");
			out.println("<p><a href =" + formElement("LogIn") + "> Log out </p>");
			out.println("</body></html>");
		
	}
    
    

}