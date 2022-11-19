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

	public MysqlPurchaseDaoTest() {
		DaoFactory.INSTANCE.setTesting();
		purchaseDao = DaoFactory.INSTANCE.getPurchaseDao();
	}
	
	@BeforeEach
	void setUp() throws Exception {
		Purchase purchase = new Purchase();
		Employee employee1 = new Employee("TestPurchaseEmployeeName","TestPurchaseEmployeeSurname","7357","test@email.com","testlogin","test","admin");
		
		purchase.setEmployee(employee1);
		purchase.setCreatedAt(new Timestamp(0).toLocalDateTime());
		savedPurchase = purchaseDao.save(purchase);
	}

	@AfterEach
	void tearDown() throws Exception {
		purchaseDao.delete(savedPurchase.getId());
	}
	
	//TODO Not working, still trying to fix setUp, 
	//org.springframework.dao.DataIntegrityViolationException: PreparedStatementCallback; Field 'employee_id' doesn't have a default value; nested exception is java.sql.SQLException: Field 'employee_id' doesn't have a default value
	//on adding new purchase
	//save in MysqlPurchaseDao when executeAndReturnKey it returns null and Purchase object is also null

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
		//assertEquals(savedPurchase.getEmployee(), fromDb.getEmployee());
		assertEquals(savedPurchase.getCreatedAt(), fromDb.getCreatedAt());
		//TODO Lambda is available only at 1.8+
		//assertThrows(NoSuchElementException.class,()->subjectDao.getById(-1));

	}
	
	@Test
	void insertTest() {
		//TODO Lambda is available only at 1.8+
		//assertThrows(NullPointerException.class, () -> categoryDao.save(null), "Cannot save null");
		Purchase purchase = new Purchase();
		purchase.setEmployee(new Employee("TestPurchaseEmployeeName","TestPurchaseEmployeeSurname","7357","test@email.com","testlogin","test","admin"));
		purchase.setCreatedAt(new Timestamp(0).toLocalDateTime());
		int size = purchaseDao.getAll().size();
		Purchase saved = purchaseDao.save(purchase);
		assertEquals(size + 1 , purchaseDao.getAll().size());
		assertNotNull(saved.getId());
		assertEquals(purchase.getEmployee(), saved.getEmployee());
		assertEquals(purchase.getCreatedAt(), saved.getCreatedAt());
		purchaseDao.delete(saved.getId());
		//TODO Lambda is available only at 1.8+
		//assertThrows(NullPointerException.class, 
		//		     () -> purchaseDao.save(new Category(null, null)),"Purchase name cannot be null");
	}
	
	@Test
	void updateTest() {
		Purchase updated = new Purchase(
				savedPurchase.getId(),
				new Employee("TestPurchaseEmployeeNameChanged","TestPurchaseEmployeeSurnameChanged","7357111","testchanged@email.com","testloginChanged","testChanged","admin"),
				new Timestamp(0).toLocalDateTime());
		int size = purchaseDao.getAll().size();
		purchaseDao.save(updated);
		assertEquals(size, purchaseDao.getAll().size());
		
		Purchase fromDb = purchaseDao.getById(updated.getId());
		
		assertEquals(updated.getId(), fromDb.getId());
		assertEquals(updated.getEmployee(), fromDb.getEmployee());
		assertEquals(updated.getCreatedAt(), fromDb.getCreatedAt());
		//TODO Lambda is available only at 1.8+
		//assertThrows(NoSuchElementException.class, 
		//		()->purchaseDao.save(new Purchase(-1L, 
		//				new Employee("TestPurchaseEmployeeNameChanged","TestPurchaseEmployeeSurnameChanged","7357111","testchanged@email.com","testloginChanged","testChanged","admin"),
		//				new Timestamp(0).toLocalDateTime()));

	}

	@Test
	void getProductsByPurchaseTest() {
		//TODO
	}
	
	@Test
	void getTotalPriceById() {
		//TODO
	}
	
	@Test
	void getByDateTest() {
		//TODO
	}
	
}


