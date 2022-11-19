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
			assertEquals(savedProduct.getName(), productGetByNameTest.getName());
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
}
