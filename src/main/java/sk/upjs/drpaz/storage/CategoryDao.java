package sk.upjs.drpaz.storage;

import java.util.List;
import java.util.NoSuchElementException;

public interface CategoryDao {
	
	/**
	  * 
	  * @return
	  */
	List<Category> getAll();
	
	/**
	 * @param product
	 * @return
	 * @throws NullPointerException
	 * @throws NoSuchElementException
	 */
	List<Category> getByProduct(Product product) throws NullPointerException, NoSuchElementException;
	
	/**
	 * @param category
	 * @return saved category
	 * @throws NullPointerException
	 * @throws NoSuchElementException
	 */
	Category save(Category category) throws NullPointerException, NoSuchElementException;
	
	/**
	 * @param id
	 * @return
	 */
	boolean delete(long id);

	/**
	 * @param id
	 * @return
	 */
	Category getById(Long id);
	
}
