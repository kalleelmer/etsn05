package web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MemberList extends servletBase {
	public MemberList () {
		super();
	}
protected String memberListRequestform() {
	return "";
}
protected void goGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
HttpSession session = request.getSession(true);
PrintWriter out = response.getWriter();
	if(!loggedIn(request)) {
		response.sendRedirect("LogIn");
	} else {
		out.println(memberListRequestform());
	}
}
}
