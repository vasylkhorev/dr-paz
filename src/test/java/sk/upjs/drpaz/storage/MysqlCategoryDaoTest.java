package sk.upjs.drpaz.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MysqlCategoryDaoTest {

	private Category savedCategory;
	private CategoryDao categoryDao;
	
	public MysqlCategoryDaoTest() {
		DaoFactory.INSTANCE.setTesting();
		categoryDao = DaoFactory.INSTANCE.getCategoryDao();
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
		//TODO do unit test but there is a join in getByProduct, not sure if it changes anything;
	}
	
	@Test
	void getAllTest() {
		List<Category> allCategories = categoryDao.getAll();
		assertNotNull(allCategories);
		assertTrue(allCategories.size() > 0);
		assertNotNull(allCategories.get(0));
	}
	
	@Test
	void insertTest() {
		//TODO Lambda is available only at 1.8+
		//assertThrows(NullPointerException.class, () -> categoryDao.save(null), "Cannot save null");
		Category category = new Category();
		category.setName("New category");
		int size = categoryDao.getAll().size();
		Category saved = categoryDao.save(category);
		assertEquals(size + 1 , categoryDao.getAll().size());
		assertNotNull(saved.getId());
		assertEquals(category.getName(), saved.getName());
		categoryDao.delete(saved.getId());
		//TODO Lambda is available only at 1.8+
		//assertThrows(NullPointerException.class, 
		//		     () -> categoryDao.save(new Category(null, null)),"Subject name cannot be null");
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
		//TODO Lambda is available only at 1.8+
		//assertThrows(NoSuchElementException.class, 
		//		     ()->categoryDao.save(new Category(-1L, "Changed")));	
	}



}
