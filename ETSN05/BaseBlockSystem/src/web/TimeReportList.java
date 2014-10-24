package web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	/**
	 * Generates a form for Time Report List.
	 * 
	 * @param logged
	 *            in user
	 * @return HTML code for the form
	 * @throws SQLException
	 * @throws SecurityException
	 */
	protected String timeReportListMenuRequestForm(String userName) {
		// Retrieving projects that user is member in
		List<Project> projects = null;
		List<User> users = null;
		List<String> summary = getSummary();
		List<String> types = getTypes();
		List<String> roles = getRoles();
		try {
			projects = Project.getByUser(userName);
			users = getUsers(userName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String html = "";
		List<String> tableRow1 = Arrays.asList("", "User", "Project",
				"Activity type", "Roles", "Start date", "End date",
				"Summarize by");
		List<String> tableRow2 = Arrays.asList("Display",
				dropDownMenu(users, "filterUser", "all users"),
				dropDownMenu(projects, "filterProject", "all projects"),
				dropDownMenu(types, "filterType", "all activity types"),
				dropDownMenu(roles, "filterRole", "all roles"), "<input type="
						+ formElement("text") + "name="
						+ formElement("filterStartDate") + "size="+formElement("8") +"value="
						+ formElement("") + ">", "<input type="
						+ formElement("text") + "size="+formElement("8") + "name="
						+ formElement("filterEndDate") + "value="
						+ formElement("") + ">",
				dropDownMenu(summary, "filterSummary", "none"));
		html = "<p> <form name=" + formElement("input");
		html += " method=" + formElement("get") + ">";
		html += "<table border=" + formElement("1") + tableRow(tableRow1)
				+ "</tr><tr>" + tableRow(tableRow2);
		html += "<td><input type=" + formElement("submit") + "name="
				+ formElement("val") + "value=" + formElement("filter")
				+ "></td></tr>";
		html += "</table>";
		html += "</form>";
		return html;

	}

	private List<String> getRoles() {
		List<String> roles = Arrays.asList("manager", "developer", "architect",
				"tester");
		return roles;
	}

	protected String timeReportListRequestForm(String userName, String user,
			String project, String type, String startDate, String endDate,
			String role, String summary) {
		String html = "";
		if (!userName.equals("admin") && user.equals("all")
				&& project.equals("all")) {
			return "<p>You cant show both all users and all projects</p>";
		}
		if (!startDate.equals("")) {
			try {
				Date.valueOf(startDate);
			} catch (IllegalArgumentException e) {
				return "<p>Wrong date format. Format: yyyy-mm-dd</p>";
			}
		}
		if (!endDate.equals("")) {
			try {
				Date.valueOf(endDate);
			} catch (Exception e) {
				return "<p>Wrong date format. Format: yyyy-mm-dd</p>";
			}
		}

		if (summary.equals("none")) {
			List<TimeReport> timeReports = null;
			try {
				timeReports = getTimeReports(userName, user, project, type,startDate, endDate, role);
				
			} catch (Exception e) {
			}
			if (timeReports != null) {
				List<String> tableRow1 = Arrays.asList("User", "Project",
						"Acticvity type", "Role", "Date", "Duration","Update","Remove","Sign/Unsign");
				html += "<table border=" + formElement("1") + tableRow(tableRow1)
						+ "</tr>";
				for (TimeReport t : timeReports) {
					html += listTableRow(t, userName);
				}
			}
			html += "</tr></table>";
			return html;
		} else {
			Map<String,Integer> summaryList = null;
			try {
				summaryList = getSummaryMap(userName, user, project, type,startDate, endDate, role,summary);
			} catch (SecurityException | SQLException e) {
				
			}
			return summaryTable(summaryList,summary);
		}

	}
	

	private String summaryTable(Map<String, Integer> summaryList,String summary) {
		List<String> tableRow1 = Arrays.asList(summary, "Duration");
		List<String> t = new LinkedList<String>();
		String html = "<table border=" + formElement("1") + tableRow(tableRow1)+ "</tr>";
		Set<Map.Entry<String,Integer>> s = summaryList.entrySet();
		for (Map.Entry<String,Integer> m:s){
			t.add(m.getKey());
			t.add(Integer.toString(m.getValue()));
			html += tableRow(t)+"</tr>";
			t.clear();
			
		}
		return html;
	}

	private String listTableRow(TimeReport t, String userName) {
		String html = "<tr>";
		Project p = null;
		try {
			p = Project.getByID(t.PROJECT_ID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String activityType = "INVALID ACTIVITY TYPE CODE: " + t.ACTIVITY_TYPE;
		int activityNum = t.ACTIVITY_TYPE;
		List<String> activityNames = getTypes();
		for (String s : activityNames){
			String firstthree = s.substring(0, 3);
			int mynum = Integer.parseInt((firstthree.charAt(2) == '-' ? s.substring(0, 2) : firstthree));
			if (activityNum == mynum){
				activityType = s;
				break;
			}
		}
		html += "<td>" + t.USERNAME + "</td>" + "<td>" + p.ID + "-" + p.NAME
				+ "</td>" + "<td>" + activityType + "</td>" + "<td>"
				+ t.ROLE + "</td>" + "<td>" + t.DATE + "</td>" + "<td>"
				+ t.DURATION + "</td>";

		if (t.SIGNER.equals("null")
				&& (userName.equals("admin") || t.USERNAME.equals(userName))) {
			html += "<td><a href ="
					+ formElement("TimeReportList?val=update&t=" + t.ID)
					+ ">update</td>";
		} else {
			html += "<td>-</td>";
		}
		if (t.SIGNER.equals("null")
				&& (userName.equals("admin") || t.USERNAME.equals(userName))) {
			html += "<td><a href ="
					+ formElement("TimeReportList?val=remove&t=" + t.ID)
					+ ">remove</td>";
		} else {
			html += "<td>-</td>";
		}
		Role role = Member.Role.valueOf("tester");
		if (!userName.equals("admin")) {
			role = getRole(userName, t.PROJECT_ID);
		}
		if (userName.equals("admin")
				|| role.equals(Member.Role.valueOf("manager"))) {
			if (t.SIGNER.equals("null")) {
				html += "<td><a href ="
						+ formElement("TimeReportList?val=sign&t=" + t.ID)
						+ ">sign</td>";
			} else {
				html += "<td><a href ="
						+ formElement("TimeReportList?val=unsign&t=" + t.ID)
						+ ">unsign</td>";
			}
		}else{
			html+="<td></td>";
		}

		html += "</tr>";
		return html;
	}
	protected Map<String,Integer> getSummaryMap(String userName, String user,
			String project, String type, String startDate, String endDate,
			String role,String summary) throws SecurityException, SQLException {
		User userObj = null;
		Project projectObj = null;
		Role roleObj = null;
		Integer typeObj = null;
		String startDateObj = null;
		String endDateObj = null;

		if (!startDate.equals("")) {
			startDateObj = startDate;
		}
		if (!endDate.equals("")) {
			endDateObj = endDate;
		}
		if (!user.equals("all")) {
			userObj = User.getByUsername(user);
		}
		if (!project.equals("all")) {
			projectObj = Project.getByID(getID(project));
		}
		if (!type.equals("all")) {
			typeObj = getID(type);
		}
		if (!role.equals("all")) {
			roleObj = Member.Role.valueOf(role);
		}
		Map<String,Integer> summaryList = TimeReport.getSummary(userObj, projectObj,
				null, startDateObj, endDateObj, roleObj, typeObj,TimeReport.SumChooser.valueOf(summary));
		return summaryList;
	}
	
	protected List<TimeReport> getTimeReports(String userName, String user,
			String project, String type, String startDate, String endDate,
			String role) throws SecurityException, SQLException {
		User userObj = null;
		Project projectObj = null;
		Role roleObj = null;
		Integer typeObj = null;
		String startDateObj = null;
		String endDateObj = null;

		if (!startDate.equals("")) {
			startDateObj = startDate;
		}
		if (!endDate.equals("")) {
			endDateObj = endDate;
		}
		if (!user.equals("all")) {
			userObj = User.getByUsername(user);
		}
		if (!project.equals("all")) {
			projectObj = Project.getByID(getID(project));
		}
		if (!type.equals("all")) {
			typeObj = getID(type);
		}
		if (!role.equals("all")) {
			roleObj = Member.Role.valueOf(role);
		}
		List<TimeReport> timeReports = TimeReport.get(userObj, projectObj,
				null, startDateObj, endDateObj, roleObj, typeObj);
		if (timeReports == null) {
			return null;
		} else {
			if (userName.equals("admin")) {
				return timeReports;
			} else {
				List<TimeReport> timeReportsLegal = new LinkedList<TimeReport>();
				for (TimeReport t : timeReports) {
					if (t.USERNAME.equals(userName)) {
						timeReportsLegal.add(t);
					} else if (getRole(userName, t.PROJECT_ID).equals(
							Member.Role.valueOf("manager"))) {
						timeReportsLegal.add(t);
					}
				}
				return timeReportsLegal;
			}
		}
	}

	protected String newTimeReportRequestForm(String userName) {
		// Retrieving projects that user is member in
		List<Project> projects = null;
		try {
			projects = Project.getByUser(userName);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String html = "";
		List<String> activityType = getTypes();
		List<String> tableRow1 = Arrays.asList("Project", "Activity type",
				"Date", "Duration");
		List<String> tableRow2 = Arrays.asList(
				dropDownMenu(projects, "createNewProject", "select project"),
				dropDownMenu(activityType, "createNewType",
						"select activity type"), "<input type="
						+ formElement("text") + "name="
						+ formElement("createNewDate") + "size="+formElement("8")+ ">", "<input type="
						+ formElement("text") + "name="
						+ formElement("createNewDuration") + "size="+formElement("6") + ">");
		html = "<p> <form name=" + formElement("input");
		html += " method=" + formElement("get") + ">";
		html += "<table border=" + formElement("1") + tableRow(tableRow1)
				+ "</tr><tr>" + tableRow(tableRow2);
		html += "<td><input type=" + formElement("submit") + "name="
				+ formElement("val") + "value=" + formElement("create")
				+ "></td></tr>";
		html += "</table>";
		html += "</form>";
		return html;

	}
	
	protected String updateTimeReportRequestForm(String userName,int project_id, Date date,int activityType,int duration,String ID) throws SQLException {
		// Retrieving projects that user is member in
		String html = "";
		List<String> activityTypes = getTypes();
		List<String> projects = new LinkedList<String>();
		List<String> tableRow1 = Arrays.asList("Project", "Activity type","Date", "Duration","ID");
		List<String> tableRow2 = Arrays.asList(dropDownMenu(projects, "updateProject",getString(Project.getByID(project_id))),
				dropDownMenu(activityTypes, "updateType",Integer.toString(activityType)+"-TODO"),
				 "<input type=" + formElement("text") + "name="+ formElement("updateDate") +"value="+formElement(date.toString())+ "size="+formElement("8")+ ">",
				 "<input type=" + formElement("text") + "name="+ formElement("updateDuration") + "value="
								+ formElement(Integer.toString(duration)) + "size="+formElement("6") + ">",
				 "<input type=" + formElement("text") + "name="+ formElement("t") + "value="
								+ formElement(ID) + "size="+formElement("6") + ">");
		html = "<p> <form name=" + formElement("input");
		html += " method=" + formElement("get") + ">";
		html += "<table border=" + formElement("1") + tableRow(tableRow1)
				+ "</tr><tr>" + tableRow(tableRow2);
		html += "<td><input type=" + formElement("submit") + "name="
				+ formElement("val") + "value=" + formElement("save")
				+ "></td></tr>";
		html += "</table>";
		html += "</form>";
		return html;

	}

	private List<String> getSummary() {
		return Arrays.asList("week", "role", "project", "user");
	}

	private List<String> getTypes() {
		return Arrays.asList("11-SDP", "12-SRS", "13-SVVS", "14-STLDD",
				"15-SVVI", "16-SDDD", "17-SVVR", "18-SSD", "19-Final Report",
				"21-Functional test", "22-System test", "23-Regression test",
				"30-Meeting", "41-Lecture", "42-Exercise",
				"43-Computer exercise", "44-Home reading", "100-Other");
	}

	private List<User> getUsers(String user) throws SecurityException,
			SQLException {
		List<User> users = new LinkedList<User>();
		List<Member> members = new LinkedList<Member>();
		if (user.equals("admin")) {
			return User.getAllUsers();
		} else {
			List<Project> projects = Project.getByUser(user);
			if (projects != null) {
				for (Project p : projects) {
					if (getRole(user, p.ID).equals(
							Member.Role.valueOf("manager"))) {
						members.addAll(Project.getByID(p.ID).getMembers());
					}
				}
			}
			if (members.isEmpty()) {
				users.add(User.getByUsername(user));
				return users;
			} else {
				for (Member m : members) {
					User u = User.getByUsername(m.USERNAME);
					if (!users.contains(u)) {
						users.add(u);
					}
				}
			}
			return users;
		}
	}

	/**
	 * Generates a table row with elements in list. Have to be enclosed with
	 * </tr>
	 * 
	 * @param list
	 * @return
	 */
	private String tableRow(List<String> list) {
		String html = "<tr>";
		for (String s : list) {
			html += "<td>" + s + "</td>";
		}
		return html;
	}

	private <E> String getString(E s) {
		if (s instanceof String) {
			return (String) s;
		} else if (s instanceof User) {
			return ((User) s).USERNAME;
		} else if (s instanceof Project) {
			return ((Project) s).ID + "- " + ((Project) s).NAME;
		} else {
			return "";
		}
	}

	private <E> String dropDownMenu(List<E> list, String name, String intro) {
		String html = "<select name =" + formElement(name) + ">";
		html += "<option value=" + intro + ">" + intro + "</option>";
		if (list == null) {
			html += "</select>";
			return html;
		}
		for (E s : list) {
			String f = getString(s);
			html += "<option value=" + f + ">" + f + "</option>";
		}
		html += "</select>";
		return html;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(true);
		String userName = (String) session.getAttribute("name");
		String val = request.getParameter("val");
		String timeReportID = request.getParameter("t");

		String createNewProject = request.getParameter("createNewProject");
		String createNewType = request.getParameter("createNewType");
		String createNewDate = request.getParameter("createNewDate");
		String createNewDuration = request.getParameter("createNewDuration");
		
		String updateType = request.getParameter("updateType");
		String updateDuration = request.getParameter("updateDuration");
		String updateDate = request.getParameter("updateDate");
		String updateProject = request.getParameter("updateProject");
		//System.out.println(updateDuration);

		String filterUser = request.getParameter("filterUser");
		String filterProject = request.getParameter("filterProject");
		String filterType = request.getParameter("filterType");
		String filterStartDate = request.getParameter("filterStartDate");
		String filterEndDate = request.getParameter("filterEndDate");
		String filterSummary = request.getParameter("filterSummary");
		String filterRole = request.getParameter("filterRole");

		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
		out.println("<html>");

		// check that the user is logged in
		if (!loggedIn(request)) {
			response.sendRedirect("LogIn");
		} else {
			if (val == null) {
				out.println(timeReportListMenuRequestForm(userName));
			} else {
				if (val.equals("createInit")) {
					out.println(newTimeReportRequestForm(userName));
				} else if (val.equals("create")) {
					out.println(createTimeReport(userName, createNewProject,
							createNewType, createNewDate, createNewDuration,true,0));
					out.println(newTimeReportRequestForm(userName));
				} else if (val.equals("update")) {
					try {
						TimeReport t = TimeReport.getByID(Integer.parseInt(timeReportID));
						out.println(updateTimeReportRequestForm(userName,t.PROJECT_ID,t.DATE,t.ACTIVITY_TYPE,t.DURATION,timeReportID));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (val.equals("save")) {
					out.println(createTimeReport(userName, updateProject,
							updateType, updateDate, updateDuration,false,Integer.parseInt(timeReportID)));
					out.println(timeReportListMenuRequestForm(userName));
				} else if (val.equals("sign")) {
					try {
						TimeReport t = TimeReport.getByID(Integer.parseInt(timeReportID));
						TimeReport updateT =new TimeReport(t.ID,t.USERNAME,t.PROJECT_ID,t.ROLE,
								t.ACTIVITY_TYPE,t.DATE,t.DURATION,userName);
						updateT.update();
						out.println(timeReportListMenuRequestForm(userName));

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (val.equals("unsign")) {
					try {
						TimeReport t = TimeReport.getByID(Integer.parseInt(timeReportID));
						TimeReport updateT =new TimeReport(t.ID,t.USERNAME,t.PROJECT_ID,t.ROLE,
								t.ACTIVITY_TYPE,t.DATE,t.DURATION,null);
						updateT.update();
						out.println(timeReportListMenuRequestForm(userName));

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (val.equals("remove")) {
					try {
						TimeReport t = TimeReport.getByID(Integer.parseInt(timeReportID));
						t.delete();
						out.println(timeReportListMenuRequestForm(userName));

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (val.equals("filter")) {
					out.println(timeReportListMenuRequestForm(userName));
					out.println(timeReportListRequestForm(userName, filterUser,
							filterProject, filterType, filterStartDate,
							filterEndDate, filterRole, filterSummary));
				}
			}
			if (!userName.equals("admin")) {
				out.println("<p><a href ="
						+ formElement("TimeReportList?val=createInit")
						+ "> Create new time report </p>");
			}
			out.println("</body></html>");
		}
	}

	private int getID(String s) {
		String[] tmp = s.split("-", 2);
		return Integer.parseInt(tmp[0]);
	}

	private String createTimeReport(String user, String project, String type,
			String date, String duration,boolean create,int ID) {
		Date dateObj = null;
		int durationInt = 0;
		int projectID = 0;
		int typeInt = 0;
		try {
			dateObj = Date.valueOf(date);
		} catch (IllegalArgumentException e) {
			return "<p>Date format: yyyy-mm-dd</p>";
		}
		try {
			durationInt = Integer.parseInt(duration);
		} catch (NumberFormatException e) {
			return "<p>Fill in duration (minutes)</p>";
		}

		if (!project.equals("select")) {
			projectID = getID(project);
		} else {
			return "<p>Select a project</p>";
		}
		if (type.equals("select")) {
			return "<p>Select a activity type</p>";
		} else {
			typeInt = getID(type);
		}

		if (create){
			TimeReport newTimeReport = new TimeReport(user, projectID, getRole(
					user, projectID), typeInt, dateObj, durationInt, null);
			try {
				newTimeReport.insert();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "<p>Time report created</p>";
		}else{
			TimeReport updatedTimeReport = new TimeReport(ID,user, projectID, getRole(
					user, projectID), typeInt, dateObj, durationInt, null);

			try {
				updatedTimeReport.update();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "<p>Time report updated</p>";
		}
	}

	private Role getRole(String user, int projectID) {
		try {
			for (Member m : Project.getByID(projectID).getMembers()) {
				if (m.USERNAME.equals(user)) {
					return m.ROLE;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}