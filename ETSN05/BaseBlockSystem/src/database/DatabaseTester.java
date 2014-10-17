package database;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatabaseTester {
	User u1;
	User u2;

	@Before
	public void setUp() throws Exception {
		u1 = new User("Maja", "hest", "Maja", "Svensson", true);
		u2 = new User("Robert", "rumpa", "Robert", "Turesson", true);
		u1.insert();
		u2.insert();
	}

	@After
	public void tearDown() throws Exception {
		u1.delete();
		u2.delete();
	}

	@Test
	public void testUpdate() {
		User u3 = new User("Maja","hund","Maja","Svensson",true);
		u3.update();
		
	}

}
