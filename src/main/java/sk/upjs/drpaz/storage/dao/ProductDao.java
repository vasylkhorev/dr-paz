package sk.upjs.drpaz.storage.dao;

import java.util.List;
import java.util.NoSuchElementException;

import sk.upjs.drpaz.storage.entities.Product;

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
	 * @param name
	 * @return List of {@link Product} where Product has said name
	 * @throws NoSuchElementException
	 */
	List<Product> getByName(String name) throws NoSuchElementException;

	/**
	 * @param product
	 * @return saved product
	 * @throws NullPointerException
	 * @throws NoSuchElementException
	 */
	Product save(Product product) throws NullPointerException, NoSuchElementException;

	/**
	 * @param id
	 * @return True if deleted
	 */
	boolean delete(long id);

}
