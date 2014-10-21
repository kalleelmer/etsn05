package database;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DBTester {
	
	@Rule
	 public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		User u1 = new User("Johan","losenj","Johan","Nilsson");
		User u2 = new User("Hannah","losenh","Hannah","Axelsson");
		User u3 = new User("Carina", "losenc","Carina","Melin");
		User u4 = new User("Daniel","losend","Daniel","Efraimsdotter");
		try {
			u1.insert();
			u2.insert();
			u3.insert();
			u4.insert();
		} catch(Exception e) {
			System.out.print("Problem occured in setUp");
		}
		
	}

	@After
	public void tearDown() throws Exception {
		User u1 = new User("Johan","losenj","Johan","Nilsson");
		User u2 = new User("Hannah","losenh","Hannah","Axelsson");
		User u3 = new User("Carina", "losenc","Carina","Melin");
		User u4 = new User("Daniel","losend","Daniel","Efraimsdotter");
		try {
			u1.delete();
			u2.delete();
			u3.delete();
			u4.delete();
		} catch (Exception e) {
			System.out.print("Problem occured in tearDown");
		}
		
	}

	@Test
	public void testGetByUsername() {
		User admin = new User("admin","adminp","admin","admin");
		try {
			User.getByUsername("admin");
			fail("Retrieved admin from the system");
		} catch(Exception e) {
		}
		try {
			admin.insert();
			fail("Was able to add admin to the system");
		} catch(Exception e) {
		}
		try {
			User johan = User.getByUsername("Johan");
			User hannah = User.getByUsername("Hannah");
			assertEquals(johan.USERNAME,"Johan");
			assertEquals(hannah.USERNAME, "Hannah");
		} catch (Exception e) {
			fail("Could not retrieve existing users from database");
		}
		try {
			User lena = User.getByUsername("Lena");
			assertNull(lena);
		} catch (Exception e) {
			fail("Retrieved value from nonexisting user in database");
		}
	}
	
	@Test
	public void testGetAllUsers() {
		
	}

}