package sk.upjs.drpaz.storage;

import java.util.List;
import java.util.NoSuchElementException;

public interface ProductDao {

	/**
	 * @param id
	 * @return product with given id
	 * @throws NoSuchElementException if product with given id not found
	 */
	Product getById(long id) throws NoSuchElementException;

	/**
	 * @return list of all products
	 */
	List<Product> getAll();

	/**
	 * @param product
	 * @return saved product
	 * @throws NullPointerException
	 * @throws NoSuchElementException
	 */
	Product save(Product product) throws NullPointerException, NoSuchElementException;

	/**
	 * @param id
	 * @return
	 */
	boolean delete(long id);

}
