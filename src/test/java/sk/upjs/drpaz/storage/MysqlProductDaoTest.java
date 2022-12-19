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

import sk.upjs.drpaz.storage.dao.CategoryDao;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.dao.EmployeeDao;
import sk.upjs.drpaz.storage.dao.ProductDao;
import sk.upjs.drpaz.storage.dao.PurchaseDao;
import sk.upjs.drpaz.storage.entities.Category;
import sk.upjs.drpaz.storage.entities.Employee;
import sk.upjs.drpaz.storage.entities.Product;
import sk.upjs.drpaz.storage.entities.Purchase;
import sk.upjs.drpaz.storage.exceptions.EntityAlreadyReferencedInDatabaseException;

class MysqlProductDaoTest {

	private EmployeeDao employeeDao;
	private PurchaseDao purchaseDao;
	private ProductDao productDao;
	private CategoryDao categoryDao;
	private Product savedProduct;
	private JdbcTemplate jdbcTemplate;
	
	
	public MysqlProductDaoTest() {
		DaoFactory.INSTANCE.setTesting();
		productDao = DaoFactory.INSTANCE.getProductDao();
		purchaseDao = DaoFactory.INSTANCE.getPurchaseDao();
		employeeDao = DaoFactory.INSTANCE.getEmployeeDao();
		categoryDao = DaoFactory.INSTANCE.getCategoryDao();
		jdbcTemplate = DaoFactory.INSTANCE.getjdbcTemplate();
	}

	@BeforeEach
	void setUp() throws Exception {
		Product product = new Product();
		product.setName("TestProduct");
		product.setPrice(10);
		product.setQuantity(10);
		product.setAlertQuantity(10);
		product.setDescription("TestProductDescription");
		savedProduct = productDao.save(product);

	}

	@AfterEach
	void tearDown() throws Exception {
		productDao.delete(savedProduct.getId());
	}
	
	@Test
	void getByIdTest() {
		Product fromDb = productDao.getById(savedProduct.getId());
		assertEquals(savedProduct.getId(), fromDb.getId());
		assertEquals(savedProduct.getName(), fromDb.getName());
		assertEquals(savedProduct.getPrice(), fromDb.getPrice());
		assertEquals(savedProduct.getQuantity(), fromDb.getQuantity());
		assertEquals(savedProduct.getDescription(), fromDb.getDescription());
		assertEquals(savedProduct.getAlertQuantity(), fromDb.getAlertQuantity());
		assertThrows(NoSuchElementException.class, ()-> productDao.getById(-1));
	}
	
	@Test
	void getAllTest() {
		List<Product> allProducts = productDao.getAll();
		assertNotNull(allProducts);
		assertTrue(allProducts.size() > 0);
		assertNotNull(allProducts.get(0));
	}
	
	@Test
	void getByNameTest() {
		List<Product> fromDb = productDao.getByName(savedProduct.getName());
		for (Product productGetByNameTest: fromDb) {
			assertTrue(productGetByNameTest.getName().startsWith(savedProduct.getName()));
		}
		assertThrows(NoSuchElementException.class, ()->productDao.getByName(null));
	}
	
	@Test
	void insertTest() {
		assertThrows(NullPointerException.class, ()-> productDao.save(null), "Cannot save null");
		Product product = new Product();
		product.setName("New Test Product");
		int size = productDao.getAll().size();
		Product saved = productDao.save(product);
		assertEquals(size + 1, productDao.getAll().size());
		assertNotNull(saved.getId());
		assertEquals(product.getName(), saved.getName());
		assertEquals(product.getPrice(), saved.getPrice());
		assertEquals(product.getQuantity(), saved.getQuantity());
		assertEquals(product.getAlertQuantity(), saved.getAlertQuantity());
		assertEquals(product.getDescription(), saved.getDescription());
		productDao.delete(saved.getId());
		assertThrows(NullPointerException.class,  ()-> productDao.save(new Product(null, null, 5, 5, 5, "TestProductDescription")), "Product name cannot be null");

	}
	
	@Test
	void updateTest() {
		Product updated = new Product(savedProduct.getId(), "Test Product Changed", 7, 7, 7, "Test Product Description Changed");
		int size = productDao.getAll().size();
		productDao.save(updated);
		assertEquals(size, productDao.getAll().size());
		
		Product fromDb = productDao.getById(updated.getId());
		
		assertEquals(updated.getId(), fromDb.getId());
		assertEquals(updated.getName(), fromDb.getName());
		assertThrows(NoSuchElementException.class,
				()->productDao.save(new Product(-1L, "Test Product ChangedChanged", 7, 7, 7, "Test Product Description ChangedChanged")));
		
	}
	
	@Test
	void getByCategoryTest() {
		Product product1 = new Product();
		product1.setName("CategoryTest1");
		product1.setQuantity(0);
		product1.setPrice(0);
		Product product2 = new Product();
		product2.setName("CategoryTest2");
		product2.setQuantity(0);
		product2.setPrice(0);
		Product savedProduct1 = productDao.save(product1);
		Product savedProduct2 = productDao.save(product2);
		List<Product> list = Arrays.asList(savedProduct1, savedProduct2);
		
		
		Category category = new Category();
		category.setName("CategoryTest");
		Category savedCategory = categoryDao.save(category);
		
		List<Product> fromDb = productDao.getByCategory(savedCategory);
		assertEquals(0, fromDb.size());
		
		categoryDao.addCategoryToProduct(savedCategory, savedProduct1);
		categoryDao.addCategoryToProduct(savedCategory, savedProduct2);
		
		fromDb = productDao.getByCategory(savedCategory);
		
		assertTrue(fromDb.size() == 2);
		
		for (int i = 0; i < 2; i++) {
			assertEquals(fromDb.get(i).getId(), list.get(i).getId());
			assertEquals(fromDb.get(i).getName(), list.get(i).getName());
			assertEquals(fromDb.get(i).getPrice(), list.get(i).getPrice());
			assertEquals(fromDb.get(i).getQuantity(), list.get(i).getQuantity());
			assertEquals(fromDb.get(i).getAlertQuantity(), list.get(i).getAlertQuantity());
			assertEquals(fromDb.get(i).getDescription(), list.get(i).getDescription());
		}	
		
		categoryDao.delete(savedCategory.getId());
		productDao.delete(savedProduct1.getId());
		productDao.delete(savedProduct2.getId());
	}
	
	@Test
	void deleteTest() {
		Product product = new Product();
		product.setName("DeleteTest");
		product.setQuantity(0);
		product.setPrice(0);
		
		Product savedProduct = productDao.save(product);
		int size = productDao.getAll().size();
		boolean check = productDao.delete(savedProduct.getId());
		int size2 = productDao.getAll().size();
		assertTrue(check);
		assertEquals(size-1, size2);
		
		
		Employee employee = new Employee("name", "surname", null, null, String.valueOf(System.currentTimeMillis()), "heslo", "Predaj");
		Employee savedEmployee = employeeDao.save(employee);
		
		Product product1 = new Product("Test",1.0, 1, 1, "testDescription");
		savedProduct = productDao.save(product1);
		
		Purchase purchase = new Purchase();
		purchase.setEmployee(savedEmployee);
		purchase.setCreatedAt(new Timestamp(Instant.now().toEpochMilli()).toLocalDateTime());
		List<Product> list = Arrays.asList(savedProduct);
		purchase.setProductsInPurchase(list);
		Purchase savedPurchase = purchaseDao.save(purchase);
		
		long savedProductId = savedProduct.getId();
		assertThrows(EntityAlreadyReferencedInDatabaseException.class, () -> productDao.delete(savedProductId));
		
		jdbcTemplate.update("DELETE FROM purchase_item WHERE purchase_id = " + savedPurchase.getId());
		purchaseDao.delete(savedPurchase.getId());
		productDao.delete(savedProduct.getId());
		employeeDao.delete(savedEmployee.getId());
	}
}
