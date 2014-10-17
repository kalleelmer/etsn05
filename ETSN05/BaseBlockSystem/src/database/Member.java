package database;

public class Member extends Entity {
	private String USERNAME;
	private int PROJECT;
	private Role ROLE;

	public Member(String userName, int project, Role role) {
		USERNAME = userName;
		PROJECT = project;
		ROLE = role;
	}
}