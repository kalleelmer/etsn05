package web;

import database.Member;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/MemberList")
public class MemberList extends servletBase {

	/**
	 * Instantiate a member list servlet
	 */
	public MemberList() {
		super();
	}

	protected String memberListRequestform() {
		return "<html><body><p> Test<p>";
	}
	protected String memberListRequestForm(List<Member> list){
		String html = "<html><body><p> Member list:</p>";
		return html;
	}

	protected void doGet(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
		Object nameObj = session.getAttribute("username");
		int projectID;
		try {
			projectID = Integer.parseInt(request.getParameter("project"));
		} catch (NumberFormatException e) {
			out.println("Error: Project ID is not a number!");
			return;
		}
		out.println("<div>Debug: Project ID is " + projectID + "</div>");
		if (!loggedIn(request)) {
			response.sendRedirect("LogIn");
		}
		List<Member> members = null;
		try {
			members = Member.getMembers(projectID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println(memberListRequestForm(members));
		if (((Member) nameObj).ROLE == Member.Role.manager) {

		} 
	}
}
