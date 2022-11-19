package sk.upjs.drpaz.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.*;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MysqlPurchaseDaoTest {
	
	private Purchase savedPurchase;
	private PurchaseDao purchaseDao;
	private EmployeeDao employeeDao;
	private Employee savedEmployee;
	private Employee savedEmployeeTest;

	public MysqlPurchaseDaoTest() {
		DaoFactory.INSTANCE.setTesting();
		purchaseDao = DaoFactory.INSTANCE.getPurchaseDao();
		employeeDao = DaoFactory.INSTANCE.getEmployeeDao();
	}
	
	@BeforeEach
	void setUp() throws Exception {
		Purchase purchase = new Purchase();
		Employee employee1 = new Employee("TestPurchaseEmployeeName","TestPurchaseEmployeeSurname","7357","test@email.com","testlogin","test","admin");
		savedEmployee = employeeDao.save(employee1);
		purchase.setEmployee(savedEmployee);
		purchase.setCreatedAt(new Timestamp(( Instant.now().toEpochMilli() + 500) / 1000 * 1000).toLocalDateTime()); // rounded to second
		savedPurchase = purchaseDao.save(purchase);
	}

	@AfterEach
	void tearDown() throws Exception {
		purchaseDao.delete(savedPurchase.getId());
		employeeDao.delete(savedEmployee.getId());
		if(savedEmployeeTest != null)
			employeeDao.delete(savedEmployeeTest.getId());
	}

	@Test
	void getAllTest() {
		List<Purchase> allPurchases = purchaseDao.getAll();
		assertNotNull(allPurchases);
		assertTrue(allPurchases.size() > 0);
		assertNotNull(allPurchases.get(0));
	}
	
	@Test
	void getByIdTest() {
		Purchase fromDb = purchaseDao.getById(savedPurchase.getId());
		assertEquals(savedPurchase.getId(), fromDb.getId());
		assertEquals(savedPurchase.getEmployee().getId(), fromDb.getEmployee().getId());
		assertEquals(savedPurchase.getCreatedAt(), fromDb.getCreatedAt());
		assertThrows(NoSuchElementException.class,()->purchaseDao.getById(-1));

	}
	
	@Test
	void insertTest() {
		Employee employeeTest = new Employee("TestPurchaseEmployeeName","TestPurchaseEmployeeSurname","7357","test@email.com","testlogin","test","admin");
		savedEmployeeTest = employeeDao.save(employeeTest);
		assertThrows(NullPointerException.class, () -> purchaseDao.save(null), "Cannot save null");
		Purchase purchase = new Purchase();
		purchase.setEmployee(savedEmployeeTest);
		purchase.setCreatedAt(new Timestamp(0).toLocalDateTime());
		int size = purchaseDao.getAll().size();
		Purchase saved = purchaseDao.save(purchase);
		assertEquals(size + 1 , purchaseDao.getAll().size());
		assertNotNull(saved.getId());
		assertEquals(purchase.getEmployee(), saved.getEmployee());
		assertEquals(purchase.getCreatedAt(), saved.getCreatedAt());
		purchaseDao.delete(saved.getId());
		assertThrows(NullPointerException.class, () -> purchaseDao.save(new Purchase(null, null, null)),"Purchase name cannot be null");
	}
	
	@Test
	void updateTest() {
		Employee employeeTest = new Employee("TestPurchaseEmployeeNameChanged","TestPurchaseEmployeeSurnameChanged","7357111","testchanged@email.com","testloginChanged","testChanged","admin");
		Employee savedEmployeeTest = employeeDao.save(employeeTest); 
		Purchase updated = new Purchase(
				savedPurchase.getId(),
				savedEmployeeTest,
				new Timestamp(0).toLocalDateTime());
		int size = purchaseDao.getAll().size();
		purchaseDao.save(updated);
		assertEquals(size, purchaseDao.getAll().size());
		
		Purchase fromDb = purchaseDao.getById(updated.getId());
		
		assertEquals(updated.getId(), fromDb.getId());
		assertEquals(updated.getEmployee().getId(), fromDb.getEmployee().getId());
		assertEquals(updated.getCreatedAt(), fromDb.getCreatedAt());
		assertThrows(NoSuchElementException.class, 
				()->purchaseDao.save(new Purchase(-1L, 
						new Employee("TestPurchaseEmployeeNameChanged","TestPurchaseEmployeeSurnameChanged","7357111","testchanged@email.com","testloginChanged","testChanged","admin"),
						new Timestamp(0).toLocalDateTime())));
	}

	@Test
	void getProductsByPurchaseTest() {
		//TODO 
		// I will finish it later cause i need to add a way to save 
		//connection between purchase and product -> save to purchase_item
	}
	
	@Test
	void getTotalPriceById() {
		//TODO
		// I will finish it later cause i need to add a way to save 
		//connection between purchase and product -> save to purchase_item
	}
	
	@Test
	void getByDateTest() {
		LocalDateTime dateStart = new Timestamp(( Instant.now().toEpochMilli() -35000) / 1000 * 1000).toLocalDateTime();
		LocalDateTime dateEnd = new Timestamp(( Instant.now().toEpochMilli() + 35000) / 1000 * 1000).toLocalDateTime();
		
		purchaseDao.getByDate( dateStart , dateEnd );
		assertTrue(dateStart.isBefore(savedPurchase.getCreatedAt()));
		assertTrue(dateEnd.isAfter(savedPurchase.getCreatedAt()));
		assertThrows(NullPointerException.class, ()->purchaseDao.getByDate(null, null));
	}
	
}


