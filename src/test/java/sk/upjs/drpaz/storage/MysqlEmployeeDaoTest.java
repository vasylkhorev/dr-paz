package sk.upjs.drpaz.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;

import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.dao.EmployeeDao;
import sk.upjs.drpaz.storage.dao.ProductDao;
import sk.upjs.drpaz.storage.dao.PurchaseDao;
import sk.upjs.drpaz.storage.entities.Employee;
import sk.upjs.drpaz.storage.entities.Product;
import sk.upjs.drpaz.storage.entities.Purchase;
import sk.upjs.drpaz.storage.exceptions.EntityAlreadyReferencedInDatabaseException;

class MysqlEmployeeDaoTest {

	private JdbcTemplate jdbcTemplate;
	private EmployeeDao employeeDao;
	private PurchaseDao purchaseDao;
	private ProductDao productDao;
	private Employee savedEmployee;
	
	public MysqlEmployeeDaoTest() {
		DaoFactory.INSTANCE.setTesting();
		employeeDao = DaoFactory.INSTANCE.getEmployeeDao();
		purchaseDao = DaoFactory.INSTANCE.getPurchaseDao();
		productDao = DaoFactory.INSTANCE.getProductDao();
		jdbcTemplate = DaoFactory.INSTANCE.getJdbcTemplate();
	}

	@BeforeEach
	void setUp() throws Exception {
		
		Employee employee = new Employee();
		employee.setName("TestName");
		employee.setSurname("TestSurname");
		employee.setPhone("7357");
		employee.setEmail("test@email.com");
		employee.setLogin("testLoginEmployee");
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
	void getByLoginTest() {
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
		assertThrows(NoSuchElementException.class,  ()-> employeeDao.getByName(null));
	}	
	
	@Test 
	void getBySurnameTest() {
		List<Employee> fromDb = employeeDao.getBySurname(savedEmployee.getSurname());
		for (Employee employeeGetBySurnameTest: fromDb) 
			assertEquals(savedEmployee.getSurname(), employeeGetBySurnameTest.getSurname());
		assertThrows(NoSuchElementException.class,  ()-> employeeDao.getBySurname(null));
	}
	
	@Test
	void getByNameAndSurnameTest() {
		List<Employee> fromDb = employeeDao.getByNameAndSurname(savedEmployee.getName(),savedEmployee.getSurname());
		for (Employee employeeGetByNameAndSurnameTest: fromDb) {
			assertEquals(savedEmployee.getName(), employeeGetByNameAndSurnameTest.getName());
			assertEquals(savedEmployee.getSurname(), employeeGetByNameAndSurnameTest.getSurname());
		}
		assertThrows(NoSuchElementException.class,  ()-> employeeDao.getByNameAndSurname(null, "TestSurnameGetNameSurnameTest"));
		assertThrows(NoSuchElementException.class,  ()-> employeeDao.getByNameAndSurname("TestNameGetNameSurnameTest", null));
		assertThrows(NoSuchElementException.class,  ()-> employeeDao.getByNameAndSurname(null, null));
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
	
	@Test
	void insertTest() {
		assertThrows(NullPointerException.class, ()-> employeeDao.save(new Employee()));
		
		Employee employee = new Employee("TestNameInsertTest", "TestSurnameInsertTest","123456789","email@email.com","TestLoginInsertTest", "heslo", "admin");
		int size = employeeDao.getAll().size();
		Employee saved = employeeDao.save(employee);
		Employee fromDb = employeeDao.getById(saved.getId());
		
		assertEquals(size + 1, employeeDao.getAll().size());
		assertNotNull(saved.getId());
		assertEquals(employee.getName(), fromDb.getName());
		assertEquals(employee.getSurname(), fromDb.getSurname());
		assertEquals(employee.getPhone(), fromDb.getPhone());
		assertEquals(employee.getEmail(), fromDb.getEmail());
		assertEquals(employee.getLogin(), fromDb.getLogin());
		assertTrue(BCrypt.checkpw(employee.getPassword(), fromDb.getPassword()));
		assertEquals(employee.getRole(), fromDb.getRole());

		employeeDao.delete(saved.getId());
		
		assertThrows(NullPointerException.class,  ()-> employeeDao.save(new Employee(null,"TestSurnameInsertTest",null,null,"TestLoginInsertTest", "TestPasswordInsertTest","Admin")), "Employee Name cannot be null");
		assertThrows(NullPointerException.class,  ()-> employeeDao.save(new Employee("TestNameInsertTest",null,null,null,"TestLogin", "TestPassword","Admin")), "Employee Surname cannot be null");
		assertThrows(NullPointerException.class,  ()-> employeeDao.save(new Employee("TestNameInsertTest","TestSurnameInsertTest",null,null, null, "TestPassword","Admin")), "Employee Login cannot be null");
		assertThrows(NullPointerException.class,  ()-> employeeDao.save(new Employee("TestNameInsertTest","TestSurnameInsertTest",null,null,"TestLoginInsertTest", null,"Admin")), "Employee Password cannot be null");
		assertThrows(NullPointerException.class,  ()-> employeeDao.save(new Employee("TestNameInsertTest","TestSurnameInsertTest",null,null,"TestLoginInsertTest", "TestPasswordInsertTest",null)), "Employee Role cannot be null");
	}
	
	@Test
	void updateTest() {
		Employee updated = new Employee();

		updated.setId(savedEmployee.getId());
		updated.setName("TestNameUpdateTest");
		updated.setSurname("TestSurnameUpdateTest");
		updated.setPhone("7357");
		updated.setEmail("testUpdateTest@email.com");
		updated.setLogin("testLoginUpdateTest");
		updated.setPassword("TestPasswordUpdateTest");
		updated.setRole("Admin");
		int size = employeeDao.getAll().size();
		updated = employeeDao.save(updated);
		assertEquals(size, employeeDao.getAll().size());
		
		Employee fromDb = employeeDao.getById(updated.getId());
		assertEquals(updated.getId(), fromDb.getId());
		assertEquals(updated.getName(), fromDb.getName());
		assertEquals(updated.getSurname(), fromDb.getSurname());
		assertEquals(updated.getPhone(), fromDb.getPhone());
		assertEquals(updated.getEmail(), fromDb.getEmail());
		assertEquals(updated.getRole(), fromDb.getRole());
		assertThrows(NoSuchElementException.class, 
				()-> employeeDao.save(new Employee(-1L,
										"TestNameUpdateTest",
										"TestSurnameUpdateTest",
										"7357",
										"testUpdateTest@email.com",
										"testLoginUpdateTest",
										"TestPasswordUpdateTest",
										"Admin")));
	}
	
	@Test
	void checkIfCanDeleteTest() {
		Employee employee = new Employee("name", "surname", null, null, String.valueOf(System.currentTimeMillis()), "heslo", "Predaj");
		Employee savedEmployee = employeeDao.save(employee);
		assertTrue(employeeDao.checkIfCanDelete(savedEmployee.getId()));
		
		Product product = new Product("Test",1.0, 1, 1, "testDescription");
		Product savedProduct = productDao.save(product);
		Purchase purchase = new Purchase();
		purchase.setEmployee(savedEmployee);
		purchase.setCreatedAt(new Timestamp(Instant.now().toEpochMilli()).toLocalDateTime());
		List<Product> list = Arrays.asList(savedProduct);
		purchase.setProductsInPurchase(list);
		Purchase savedPurchase = purchaseDao.save(purchase);
		
		assertFalse(employeeDao.checkIfCanDelete(savedEmployee.getId()));
		
		jdbcTemplate.update("DELETE FROM purchase_item WHERE purchase_id = " + savedPurchase.getId());
		purchaseDao.delete(savedPurchase.getId());
		productDao.delete(savedProduct.getId());
		employeeDao.delete(savedEmployee.getId());
	}
	
	@Test
	void deleteTest() {
		Employee employee = new Employee("name", "surname", null, null, String.valueOf(System.currentTimeMillis()), "heslo", "Predaj");
		Employee savedEmployee = employeeDao.save(employee);
		
		assertTrue(employeeDao.delete(savedEmployee.getId()));
		
		savedEmployee = employeeDao.save(employee);
		
		Product product = new Product("Test",1.0, 1, 1, "testDescription");
		Product savedProduct = productDao.save(product);
		Purchase purchase = new Purchase();
		purchase.setEmployee(savedEmployee);
		purchase.setCreatedAt(new Timestamp(Instant.now().toEpochMilli()).toLocalDateTime());
		List<Product> list = Arrays.asList(savedProduct);
		purchase.setProductsInPurchase(list);
		Purchase savedPurchase = purchaseDao.save(purchase);
		
		long saveEmployeeId = savedEmployee.getId();
		assertThrows(EntityAlreadyReferencedInDatabaseException.class, () -> employeeDao.delete(saveEmployeeId));
		
		jdbcTemplate.update("DELETE FROM purchase_item WHERE purchase_id = " + savedPurchase.getId());
		purchaseDao.delete(savedPurchase.getId());
		productDao.delete(savedProduct.getId());
		employeeDao.delete(savedEmployee.getId());
	}
}
