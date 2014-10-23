package database;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import database.Member.Role;

public class DatabaseWriter extends Entity{
	
	public static void write() throws IOException, SQLException {
		try {
		query("drop table users");
		query("drop table projects");
		query("drop table timeReports");
		query("drop table members");
		} catch(SQLException ex) {
			System.out.print("Du hade inga tabeller att ta bort");
		}
		query("create table users (username varchar(128), PRIMARY KEY(username), password varchar(128), firstname varchar(128), lastname varchar(128));");
		query("create table projects (id int, primary key(id), name varchar(128), closed boolean);");
		query("alter table projects modify column id int auto_increment");
		query("create table members (username varchar(128), project int, primary key(username, project), role enum('undefined','manager','developer','architect','tester'))");
		query("create table timeReports (id int, user varchar(128), primary key(id), project int, role enum('undefined','manager','developer','architect','tester'),activityType int, date Date, duration int, signer varchar(128))");
		query("alter table timeReports modify column id int auto_increment");
		query("insert into users set username='admin', password='adminp'");
		Path path = Paths.get("/home/etsn05/Puss2/etsn05/ETSN05/BaseBlockSystem/src/database/DatabaseContent");
	    List<String> list =  Files.readAllLines(path, StandardCharsets.UTF_8);
	    int i = 0;
	    while (!list.get(i).contains("#")) {
	    	String userInfo = list.get(i);
	    	String[] data = userInfo.split(";");
	    	User user = new User(data[0],data[1],data[2],data[3]);
	    	user.insert();
	    	i++;
	    }
	    i++;
	    while (!list.get(i).contains("#")) {
	    	Project project = new Project(list.get(i));
	    	project.insert();
	    	i++;
	    }
	    i++;
	    while (!list.get(i).contains("#")) {
	    	String userInfo = list.get(i);
	    	String[] data = userInfo.split(";");
	    	Member member = new Member(data[0],Integer.parseInt(data[1]),Role.valueOf(data[2]));
	    	member.set();
	    	i++;
	    }
	    i++;
	    while (!list.get(i).contains("#")) {
	    	String userInfo = list.get(i);
	    	String[] data = userInfo.split(";");
	    	TimeReport timeReport = new TimeReport(data[0],Integer.parseInt(data[1]),Role.valueOf(data[2]),Integer.parseInt(data[3]),Date.valueOf(data[4]),Integer.parseInt(data[5]),null);
	    	timeReport.insert();
	    	i++;
	    }
	}
	
	public static void main(String[] args) {
		try {
			write();
			System.out.print("Wrote to database");
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
