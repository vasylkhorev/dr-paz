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
		//TODO Lambda is available only at 1.8+
		//assertThrows(NoSuchElementException.class,  ()->employeeDao.getById(-1L));
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
		//TODO Lambda is available only at 1.8+
		//assertThrows(NoSuchElementException.class,  ()->employeeDao.getById(-1L));
		
		
	}
	
	@Test
	void getByNameTest() {
		//TODO getByName returns 1 employee object, 
			// however multiple employees can have the same Name!!!! 
	}	
	
	@Test 
	void getBySurnameTest() {
		//TODO getBySurname returns 1 employee object, 
			// however multiple employees can have the same Surname!!!! 
	}
	
	@Test
	void getByNameAndSurname() {
		//TODO getByName returns 1 employee object, 
			// however multiple employees can have the same Name and Surname!!!! 
	}
	
	
	@Test
	void changePasswordTest() {
		//TODO check if same after update (password is hashedand added to database)
	}
}
