package sk.upjs.drpaz.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.*;
import java.util.ArrayList;
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
	
	private Product savedProduct1;
	private Product savedProduct2;
	private ProductDao productDao;
	
	private List<Product> productsList = new ArrayList<>();

	public MysqlPurchaseDaoTest() {
		DaoFactory.INSTANCE.setTesting();
		purchaseDao = DaoFactory.INSTANCE.getPurchaseDao();
		employeeDao = DaoFactory.INSTANCE.getEmployeeDao();
		productDao = DaoFactory.INSTANCE.getProductDao();
	}
	
	@BeforeEach
	void setUp() throws Exception {
		Purchase purchase = new Purchase();
		Employee employee1 = new Employee("TestPurchaseEmployee1Name","TestPurchaseEmployeeSurname","7357","test@email.com","testlogin","test","admin");
		Product product1 = new Product();
		Product product2 = new Product();
		
		product1.setName("TestProduct1");
		product1.setPrice(11);
		product1.setQuantity(15);
		product1.setAlertQuantity(11);
		product1.setDescription("TestProduct1Description");
		
		product2.setName("TestProduct2");
		product2.setPrice(10);
		product2.setQuantity(20);
		product2.setAlertQuantity(10);
		product2.setDescription("TestProduct2Description");
		
		savedProduct1 = productDao.save(product1);
		savedProduct2 = productDao.save(product2);
		productsList.add(savedProduct1);
		productsList.add(savedProduct2);
		
		savedEmployee = employeeDao.save(employee1);
		
		
		purchase.setEmployee(savedEmployee);
		purchase.setCreatedAt(new Timestamp(( Instant.now().toEpochMilli() + 500) / 1000 * 1000).toLocalDateTime()); // rounded to second
		purchase.setProductsInPurchase(productsList);
		
		savedPurchase = purchaseDao.save(purchase);
	}

	@AfterEach
	void tearDown() throws Exception {
		purchaseDao.delete(savedPurchase.getId());
		employeeDao.delete(savedEmployee.getId());
		if(savedEmployeeTest != null)
			employeeDao.delete(savedEmployeeTest.getId());
		
		productDao.delete(savedProduct1.getId());
		productDao.delete(savedProduct2.getId());
		productsList.removeAll(productsList);
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
		Employee employeeTest = new Employee("TestPurchaseEmployeeTestName","TestPurchaseEmployeeSurname","7357","test@email.com","testlogin","test","admin");
		savedEmployeeTest = employeeDao.save(employeeTest);
		
		assertThrows(NullPointerException.class, () -> purchaseDao.save(null), "Cannot save null");
		Purchase purchase = new Purchase();
		purchase.setEmployee(savedEmployeeTest);
		purchase.setCreatedAt(new Timestamp(0).toLocalDateTime());
		purchase.setProductsInPurchase(productsList);
		
		int size = purchaseDao.getAll().size();
		Purchase saved = purchaseDao.save(purchase);
		
		assertEquals(size + 1 , purchaseDao.getAll().size());
		assertNotNull(saved.getId());
		assertEquals(purchase.getEmployee(), saved.getEmployee());
		assertEquals(purchase.getCreatedAt(), saved.getCreatedAt());
		assertThrows(NullPointerException.class, () -> purchaseDao.save(new Purchase(null, null, null,null)),"Purchase name cannot be null");
		
		purchaseDao.delete(saved.getId());
		employeeDao.delete(savedEmployeeTest.getId());
	}
	
	@Test
	void updateTest() {
		Employee employeeTest = new Employee("TestPurchaseEmployeeTestNameChanged","TestPurchaseEmployeeSurnameChanged","7357111","testchanged@email.com","testloginChanged","testChanged","admin");
		savedEmployeeTest = employeeDao.save(employeeTest); 
		Purchase updated = new Purchase(
				savedPurchase.getId(),
				savedEmployeeTest,
				new Timestamp(0).toLocalDateTime(),
				productsList);
		int size = purchaseDao.getAll().size();
		updated = purchaseDao.save(updated);
		assertEquals(size, purchaseDao.getAll().size());
		
		Purchase fromDb = purchaseDao.getById(updated.getId());
		
		assertEquals(updated.getId(), fromDb.getId());
		assertEquals(updated.getEmployee().getId(), fromDb.getEmployee().getId());
		assertEquals(updated.getCreatedAt(), fromDb.getCreatedAt());
		assertThrows(NoSuchElementException.class, 
				()->purchaseDao.save(new Purchase(-1L, 
						new Employee("TestPurchaseEmployeeNameChanged","TestPurchaseEmployeeSurnameChanged","7357111","testchanged@email.com","testloginChanged","testChanged","admin"),
						new Timestamp(0).toLocalDateTime(),
						productsList)));
		purchaseDao.delete(updated.getId());
		employeeDao.delete(savedEmployeeTest.getId());
	}
	
	@Test
	void getProductsByPurchaseIdTest() {
		//TODO 
		// I will finish it later cause i need to add a way to save 
		//connection between purchase and product -> save to purchase_item
		assertTrue(false);
	}
	
	@Test
	void getTotalPriceByIdTest() {
		List<Product> productsListTotalTest = new ArrayList<>();
		double totalPrice = (savedProduct1.getPrice()*5)+(savedProduct2.getPrice()*7); 
		for (int i = 0; i < 5; i++) {
			productsListTotalTest.add(savedProduct1);
		}
		for (int i = 0; i < 7; i++) {
			productsListTotalTest.add(savedProduct2);
		}
		Purchase purchaseTotalTest = new Purchase();
		
		purchaseTotalTest.setEmployee(savedEmployee);
		purchaseTotalTest.setCreatedAt(new Timestamp(0).toLocalDateTime());
		purchaseTotalTest.setProductsInPurchase(productsListTotalTest);
		purchaseTotalTest = purchaseDao.save(purchaseTotalTest);
		assertEquals(totalPrice, purchaseDao.getTotalPriceById(purchaseTotalTest.getId()));
		purchaseDao.delete(purchaseTotalTest.getId());
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


