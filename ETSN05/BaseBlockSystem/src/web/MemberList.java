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
out.println(getPageIntro());
Object nameObj = session.getAttribute("username");
String myName = "";
if (nameObj != null) {
	myName = (String)nameObj;
}
	if(!loggedIn(request)) {
		response.sendRedirect("LogIn");
	} if (nameObj.Role.toString.equals("Manager")) { //hur anropar jag detta??
		
	}	else {
	
		out.println(memberListRequestform());
	}
}
}
