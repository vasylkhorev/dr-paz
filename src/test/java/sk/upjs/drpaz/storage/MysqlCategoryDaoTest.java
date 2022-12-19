package sk.upjs.drpaz.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sk.upjs.drpaz.storage.dao.CategoryDao;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.dao.ProductDao;
import sk.upjs.drpaz.storage.entities.Category;
import sk.upjs.drpaz.storage.entities.Product;

class MysqlCategoryDaoTest {

	private Category savedCategory;
	private CategoryDao categoryDao;
	private ProductDao productDao;
	
	public MysqlCategoryDaoTest() {
		DaoFactory.INSTANCE.setTesting();
		categoryDao = DaoFactory.INSTANCE.getCategoryDao();
		productDao = DaoFactory.INSTANCE.getProductDao();
	}
	
	@BeforeEach
	void setUp() throws Exception {
		Category category = new Category();
		category.setName("Test Category");
		savedCategory = categoryDao.save(category);
	}

	@AfterEach
	void tearDown() throws Exception {
		categoryDao.delete(savedCategory.getId());
	}
	
	@Test
	void getByProductTest() {
		assertThrows(NullPointerException.class, () -> categoryDao.getByProduct(null), "Cannot save null");
		Category category1 = new Category();
		category1.setName("TestNameAddCategoryToProductTest");
		Category savedCategory1 = categoryDao.save(category1);
			
		Product product1 = new Product("TestProductAddCategory1",2d,2,2,"TestDescriptionAddCategory1");
		Product savedProduct1 = productDao.save(product1);

		List<Category> listOfCategoriesBefore = categoryDao.getByProduct(savedProduct1);
		int sizeOfListBefore = listOfCategoriesBefore.size();
		
		categoryDao.addCategoryToProduct(savedCategory, savedProduct1);
		categoryDao.addCategoryToProduct(savedCategory1, savedProduct1);

		List<Category> listOfCategoriesAfter = categoryDao.getByProduct(savedProduct1);
		int sizeOfListAfter = listOfCategoriesAfter.size();
		
		assertEquals(sizeOfListBefore+2,sizeOfListAfter);
		if (sizeOfListBefore+2 == sizeOfListAfter) {
			assertEquals(listOfCategoriesAfter.get(0).getName(), savedCategory.getName());
			assertEquals(listOfCategoriesAfter.get(1).getName(), savedCategory1.getName());
		}else {
			assertTrue(false);
		}

		categoryDao.delete(savedCategory1.getId());
		productDao.delete(savedProduct1.getId());
	}
	
	@Test
	void getAllTest() {
		List<Category> allCategories = categoryDao.getAll();
		assertNotNull(allCategories);
		assertTrue(allCategories.size() > 0);
		assertNotNull(allCategories.get(0));
	}
	
	@Test
	void getByIdTest() {
		Category fromDb = categoryDao.getById(savedCategory.getId());
		assertEquals(savedCategory.getId(), fromDb.getId());
		assertEquals(savedCategory.getName(), fromDb.getName());
		assertThrows(NoSuchElementException.class,()->categoryDao.getById(-1l));
	}
	
	@Test
	void insertTest() {
		assertThrows(NullPointerException.class, () -> categoryDao.save(null), "Cannot save null");
		
		Category category = new Category();
		category.setName("New category");
		int size = categoryDao.getAll().size();
		Category saved = categoryDao.save(category);
		Category fromDb = categoryDao.getById(saved.getId());
		
		assertEquals(size + 1 , categoryDao.getAll().size());
		assertNotNull(fromDb.getId());
		assertEquals(category.getName(), fromDb.getName());
		categoryDao.delete(fromDb.getId());
		
		assertThrows(NullPointerException.class, 
				     () -> categoryDao.save(new Category(null, null)),"Subject name cannot be null");
	}
	
	@Test
	void updateTest() {
		Category updated = new Category(savedCategory.getId(), "Changed name");
		int size = categoryDao.getAll().size();		
		categoryDao.save(updated);
		assertEquals(size, categoryDao.getAll().size());

		Category fromDb = categoryDao.getById(updated.getId());

		assertEquals(updated.getId(), fromDb.getId());
		assertEquals(updated.getName(), fromDb.getName());
		assertThrows(NoSuchElementException.class, 
				     ()->categoryDao.save(new Category(-1l, "Changed")));	
	}
	
	@Test
	void addCategoryToProductTest() {
		assertThrows(NullPointerException.class, () -> categoryDao.addCategoryToProduct(savedCategory, null));
		assertThrows(NullPointerException.class, () -> categoryDao.addCategoryToProduct(null, new Product("TestProduct",1d,1,1,"TestDescription")));
		
		Category category1 = new Category();
		category1.setName("TestNameAddCategoryToProductTest");
		Category savedCategory1 = categoryDao.save(category1);
		
		Category category2 = new Category();
		category2.setName("TestNameAddCategoryToProductTest2");
		Category savedCategory2 = categoryDao.save(category2);
		
		Product product1 = new Product("TestProductAddCategory1",2d,2,2,"TestDescriptionAddCategory1");
		Product savedProduct1 = productDao.save(product1);
		
		Product product2 = new Product("TestProductAddCategory2",4d,4,4,"TestDescriptionAddCategory2");
		Product savedProduct2 = productDao.save(product2);
		
		int size1Before = categoryDao.getByProduct(savedProduct1).size();
		int size2Before = categoryDao.getByProduct(savedProduct2).size();
		
		boolean statusadd1 = categoryDao.addCategoryToProduct(savedCategory1, savedProduct1);
		boolean statusadd2 = categoryDao.addCategoryToProduct(savedCategory2, savedProduct1);
		boolean statusadd3 = categoryDao.addCategoryToProduct(savedCategory1, savedProduct2);
		
		assertTrue(statusadd1);
		assertTrue(statusadd2);
		assertTrue(statusadd3);
		
		int size1After = categoryDao.getByProduct(savedProduct1).size();
		int size2After = categoryDao.getByProduct(savedProduct2).size();
		
		List<Category> listGetByProduct1 = categoryDao.getByProduct(savedProduct1);
		List<Category> listGetByProduct2 = categoryDao.getByProduct(savedProduct2);
		
		
		assertEquals(size1Before+2, size1After);
		assertEquals(size2Before+1, size2After);
		if(size1Before+2 == size1After) {
			assertEquals(listGetByProduct1.get(0).getId(), savedCategory1.getId());
			assertEquals(listGetByProduct1.get(0).getName(), savedCategory1.getName());
			assertEquals(listGetByProduct1.get(1).getId(), savedCategory2.getId());
			assertEquals(listGetByProduct1.get(1).getName(), savedCategory2.getName());
		}else {
			assertTrue(false);
		}
		if(size2Before+1 == size2After) {
			assertEquals(listGetByProduct2.get(0).getId(), savedCategory1.getId());
			assertEquals(listGetByProduct2.get(0).getName(), savedCategory1.getName());
		}else {
			assertTrue(false);
		}
		
		assertThrows(NullPointerException.class, () -> categoryDao.addCategoryToProduct(new Category(), null));
		assertThrows(NullPointerException.class, () -> categoryDao.addCategoryToProduct(null, new Product()));
		
		categoryDao.delete(savedCategory1.getId());
		categoryDao.delete(savedCategory2.getId());
		productDao.delete(savedProduct1.getId());
		productDao.delete(savedProduct2.getId());
	}
	
	@Test
	void deleteByProductTest() {
		Product product = new Product("Test",1.0,1,1,"description");
		Category category = new Category();
		category.setName("Test");
		Product savedProduct = productDao.save(product);
		Category savedCategory = categoryDao.save(category);
		categoryDao.addCategoryToProduct(savedCategory, savedProduct);
		List<Category>list = categoryDao.getByProduct(savedProduct);
		assertTrue(list.size()==1);
		assertEquals(list.get(0).getId(), savedCategory.getId());
		assertEquals(list.get(0).getName(), savedCategory.getName());
		
		categoryDao.delete(savedCategory.getId());
		productDao.delete(savedProduct.getId());
	}
	
	@Test
	void deleteTest() {
		Category category = new Category();
		category.setName("deleteTest");
		
		Category savedCategory = categoryDao.save(category);
		int size = categoryDao.getAll().size();
		boolean delBool = categoryDao.delete(savedCategory.getId());
		assertTrue(delBool);
		int size2 = categoryDao.getAll().size();
		assertEquals(size-1, size2);
		
		boolean delBool2 = categoryDao.delete(-1);
		assertTrue(!delBool2);
	}
	
	@Test
	void getByNameTest() {
		List<Category> fromDb = categoryDao.getByName(savedCategory.getName());
		for (Category category: fromDb) {
			assertEquals(category.getName(), savedCategory.getName());
		}
		assertThrows(NullPointerException.class, ()-> categoryDao.getByName(null));
	}
}
