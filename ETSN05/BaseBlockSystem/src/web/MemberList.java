package web;

import database.Member;
import database.Member.Role;
import database.Project;
import database.User;

import java.sql.SQLException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/MemberList")
public class MemberList extends servletBase {

	private <E> String dropDownMenu(List<E> list, String name) {
		String html = "<select name =" + formElement(name) + ">";
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
		} else if (s instanceof Member) {
			return ((Member) s).USERNAME;
			} else {
			return "";
		}
	}

	protected String memberListRequestForm(List<Member> list) {
		String html = "<html><body><p> Member list:</p>";
		html += "<ol>";
		for (Member p : list) {
			html += "<li>" + "Username: " + p.USERNAME + " Role: " + p.ROLE
					+ "</li>";
		}
		html += "</ol>";
		return html;
	}

	protected String managerRequestForm(List<Member> memberList, int id) {
		List<String> role = new ArrayList<String>();
		role.add("undefined");
		role.add("manager");
		role.add("architect");
		role.add("developer");
		role.add("tester");
		List<String> tableRow1 = Arrays.asList("", "User", "Role");
		List<String> tableRow2 = Arrays.asList("Display",
				dropDownMenu(memberList, "filterUser"),
				dropDownMenu(role, "filterRole"));
		String html = "<p> <form name=" + formElement("input");
		html += " method=" + formElement("get") + ">";
		html += "<table style=" + formElement("width:80%")
				+ tableRow(tableRow1) + "</tr><tr>" + tableRow(tableRow2);
		html += "<td><input type=" + formElement("submit") + "name="
				+ formElement("val") + "value=" + formElement("Change")
				+ "></td></tr>";
		html += "<input type='hidden' name=" + formElement("project") + " value=" + formElement(id+"") + '>';
		html += "</table>";
		html += "</form>";
		html = "<p><form name=" + formElement("input");
		html += " method=" + formElement("get");
		html += "<p><input type=" + formElement("text") + " name=" + formElement("add") + '>';    	
		html += "<input type='hidden' name=" + formElement("project") + " value=" + formElement(id + "") + '>';
		html += "<input type=" + formElement("submit") + " value='Add new member'>";
		html += "</form>";
		return html;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
		Object nameObj = session.getAttribute("name");
		int projectID;
		String filterUser = null;
		String filterRole = null;
		String myName = "";
		filterUser = request.getParameter("filterUser");
		filterRole = request.getParameter("filterRole");
		String addmember = request.getParameter("add");
		Object val = request.getParameter("val");
		try {
			projectID = Integer.parseInt(request.getParameter("project"));
		} catch (NumberFormatException e) {
			System.out.println(request.getParameter("Project"));
			out.println("Error: Project ID is not a number!");
			return;
		}
		try {
			out.println("<div>Project name: " + Project.getByID(projectID).NAME +   " Project ID is " + projectID + "</div>");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if (!loggedIn(request)) {
			response.sendRedirect("LogIn");
		}
		if (nameObj != null) {
			myName = (String) nameObj;
		}
		List<Member> members = null;
		
		if (addmember != null) {
			Member newMember = new Member(addmember, projectID, Member.Role.undefined);
			try {
				newMember.set();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		try {
			members = Project.getByID(projectID).getMembers();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(members == null) {
			members = new ArrayList<Member>();
		}
		if(val != null){
			System.out.println(filterRole);
			System.out.println(filterUser);
			Role role = Member.Role.valueOf(filterRole);
			Member newMember = new Member(filterUser, projectID, role);
			System.out.println("innanMemberset");
			try {
				newMember.set();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		out.println(memberListRequestForm(members));
		String u;
		boolean manager_b = false;
		for (Member m : members) {
			u = m.USERNAME;
			System.out.println("checkmanagerloop");
			if ((m.ROLE == Member.Role.manager && u.equals(myName)) || myName.equals("admin")) {
				manager_b = true;
				break;
			}
		}
		if (myName.equals("admin")) manager_b = true;
		if (manager_b) out.println(managerRequestForm(members, projectID));
	}
}
