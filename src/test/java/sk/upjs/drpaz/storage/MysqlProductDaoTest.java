package sk.upjs.drpaz.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MysqlProductDaoTest {

	private ProductDao productDao;
	private Product savedProduct;
	
	public MysqlProductDaoTest() {
		DaoFactory.INSTANCE.setTesting();
		productDao = DaoFactory.INSTANCE.getProductDao();
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
	
	//TODO Not working, still trying to fix setUp, 
	//org.springframework.dao.DataIntegrityViolationException: PreparedStatementCallback; Field 'name' doesn't have a default value; nested exception is java.sql.SQLException: Field 'name' doesn't have a default value
	//on adding to database same problem as with MysqlPurchaseDaoTest
	//save in MysqlProductDao when executeAndReturnKey it returns null and Product object is also null

	@Test
	void getByIdTest() {
		Product fromDb = productDao.getById(savedProduct.getId());
		assertEquals(savedProduct.getId(), fromDb.getId());
		assertEquals(savedProduct.getName(), fromDb.getName());
		assertEquals(savedProduct.getPrice(), fromDb.getPrice());
		assertEquals(savedProduct.getQuantity(), fromDb.getQuantity());
		assertEquals(savedProduct.getDescription(), fromDb.getDescription());
		assertEquals(savedProduct.getAlertQuantity(), fromDb.getAlertQuantity());
		//TODO Lambda is available only at 1.8+
		//assertThrows(NoSuchElementException.class, ()->productDao.getById(-1));
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
			assertEquals(savedProduct.getName(), productGetByNameTest.getName());
		}
		//TODO Lambda is available only at 1.8+
		//assertThrows(NoSuchElementException.class, ()->productDao.getByName(null));
	}
	
	@Test
	void insertTest() {
		//TODO Lambda is available only at 1.8+
		//assertThows(NullPointerException.class, ()-> productDao.save(null), "Cannot save null");
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
		//TODO Lambda is available only at 1.8+
		//assertThrows(NullPointerException.class,  ()-> productDao.save(new Product(null, null, 5, 5, 5, "TestProductDescription")), "Product name cannot be null");
		//assertThrows(NullPointerException.class,  ()-> productDao.save(new Product(null, "TestProductName", null, 5, 5, "TestProductDescription")), "Product price cannot be null");
		//assertThrows(NullPointerException.class,  ()-> productDao.save(new Product(null, "TestProductName", 5, null, 5, "TestProductDescription")), "Product quantity cannot be null");
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
		//TODO Lambda is available only at 1.8+
		//assertThrows(NoSuchElementException.class,
		//		()->productDao.save(new Product(-1L, "Test Product ChangedChanged", 7, 7, 7, "Test Product Description ChangedChanged")));
		
	}
}