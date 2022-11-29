package sk.upjs.drpaz.storage;

import java.util.List;
import java.util.NoSuchElementException;

public interface CategoryDao {
	
	/**
	  * @return List of all {@link Category} in database
	  */
	List<Category> getAll();
	
	/**
	 * @param product
	 * @return List of {@link Category}
	 * @throws NullPointerException
	 * @throws NoSuchElementException
	 */
	List<Category> getByProduct(Product product) throws NullPointerException, NoSuchElementException;
	
	/**
	 * @param {@link Category}
	 * @return saved {@link Category}
	 * @throws NullPointerException
	 * @throws NoSuchElementException
	 */
	Category save(Category category) throws NullPointerException, NoSuchElementException;
	
	/**
	 * @param id
	 * @return boolean
	 */
	boolean delete(long id);
	 /**
	  * @param category
	  * @param product
	  * @return True if {@link Category} was assigned to a {@link Product}
	  */
	boolean addCategoryToProduct(Category category, Product product);

	/**
	 * @param id
	 * @return {@link Category} that has specified id
	 */
	Category getById(long id);
	
	
	
}
