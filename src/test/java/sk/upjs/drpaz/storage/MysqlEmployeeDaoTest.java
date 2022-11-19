package sk.upjs.drpaz.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

class MysqlEmployeeDaoTest {

	private EmployeeDao employeeDao;
	private Employee savedEmployee;
	
	public MysqlEmployeeDaoTest() {
		DaoFactory.INSTANCE.setTesting();
		employeeDao = DaoFactory.INSTANCE.getEmployeeDao();
	}

	@BeforeEach
	void setUp() throws Exception {
		Employee employee = new Employee();
		employee.setName("TestName");
		employee.setSurname("TestSurname");
		employee.setPhone("7357");
		employee.setEmail("test@email.com");
		employee.setLogin("testLogin");
		employee.setPassword("TestPassword");
		employee.setRole("Admin");
		savedEmployee = employeeDao.save(employee);
	}

	@AfterEach
	void tearDown() throws Exception {
		employeeDao.delete(savedEmployee.getId());
	}
	
	@Test
	void getByIdTest() {
		Employee fromDb = employeeDao.getById(savedEmployee.getId());
		assertEquals(savedEmployee.getId(), fromDb.getId());
		assertEquals(savedEmployee.getName(), fromDb.getName());
		assertEquals(savedEmployee.getSurname(), fromDb.getSurname());
		assertEquals(savedEmployee.getPhone(), fromDb.getPhone());
		assertEquals(savedEmployee.getEmail(), fromDb.getEmail());
		assertEquals(savedEmployee.getLogin(), fromDb.getLogin());
		assertTrue(BCrypt.checkpw(savedEmployee.getPassword(), fromDb.getPassword()));
		assertEquals(savedEmployee.getRole(), fromDb.getRole());
		assertThrows(NoSuchElementException.class,  ()-> employeeDao.getById(-1L));
	}
	
	@Test
	void getAllTest() {
		List<Employee> allEmployees = employeeDao.getAll();
		assertNotNull(allEmployees);
		assertTrue(allEmployees.size() > 0);
		assertNotNull(allEmployees.get(0));
	}
	
	@Test
	void getByLogin() {
		// this is for unique login, database is not set for unique login!!!!!!!!
		Employee fromDb = employeeDao.getByLogin(savedEmployee.getLogin());
		assertEquals(savedEmployee.getId(), fromDb.getId());
		assertEquals(savedEmployee.getName(), fromDb.getName());
		assertEquals(savedEmployee.getSurname(), fromDb.getSurname());
		assertEquals(savedEmployee.getPhone(), fromDb.getPhone());
		assertEquals(savedEmployee.getEmail(), fromDb.getEmail());
		assertEquals(savedEmployee.getLogin(), fromDb.getLogin());
		assertTrue(BCrypt.checkpw(savedEmployee.getPassword(), fromDb.getPassword()));
		assertEquals(savedEmployee.getRole(), fromDb.getRole());
		assertThrows(NoSuchElementException.class,  ()-> employeeDao.getById(-1));
		
		
	}
	
	@Test
	void getByNameTest() {
		List<Employee> fromDb = employeeDao.getByName(savedEmployee.getName());
		for (Employee employeeGetByNameTest: fromDb) 
			assertEquals(savedEmployee.getName(), employeeGetByNameTest.getName());
	}	
	
	@Test 
	void getBySurnameTest() {
		List<Employee> fromDb = employeeDao.getBySurname(savedEmployee.getSurname());
		for (Employee employeeGetBySurnameTest: fromDb) 
			assertEquals(savedEmployee.getSurname(), employeeGetBySurnameTest.getSurname());
	}
	
	@Test
	void getByNameAndSurname() {
		List<Employee> fromDb = employeeDao.getByNameAndSurname(savedEmployee.getName(),savedEmployee.getSurname());
		for (Employee employeeGetByNameAndSurnameTest: fromDb) {
			assertEquals(savedEmployee.getName(), employeeGetByNameAndSurnameTest.getName());
			assertEquals(savedEmployee.getSurname(), employeeGetByNameAndSurnameTest.getSurname());
		}
	}
	
	
	@Test
	void changePasswordTest() {
		String newLogin = "testLoginForChange";
		String newPassword = "testHesla";
		
		employeeDao.changePassword(savedEmployee.getLogin(), savedEmployee.getPassword(), newLogin, newPassword);
		Employee fromDb = employeeDao.getById(savedEmployee.getId());
		assertEquals(newLogin, fromDb.getLogin());
		assertTrue(BCrypt.checkpw(newPassword, fromDb.getPassword()));
		assertThrows(NullPointerException.class,  ()-> employeeDao.changePassword(null, null, null, null));
		assertFalse(employeeDao.changePassword("notInDb", "notInDb", "notInDb", "notInDb"));
	}
}
