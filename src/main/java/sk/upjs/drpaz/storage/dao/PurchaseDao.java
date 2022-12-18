package sk.upjs.drpaz.storage.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import sk.upjs.drpaz.storage.entities.Product;
import sk.upjs.drpaz.storage.entities.Purchase;

public interface PurchaseDao {

	/**
	 * @param id
	 * @return {@link Purchase} with given id
	 * @throws NoSuchElementException
	 */
	Purchase getById(long id) throws NoSuchElementException;
	
	/**
	 * @return List of all {@link Purchase} in DB
	 */
	List<Purchase> getAll();

	/**
	 * @param purchase
	 * @return {@link Purchase} that was saved in DB
	 * @throws NullPointerException
	 * @throws NoSuchElementException
	 */
	Purchase save(Purchase purchase) throws NullPointerException, NoSuchElementException;
	
	/**
	 * @param id
	 * @return List of {@link Product} that are in purchase
	 * @throws NullPointerException
	 * @throws NoSuchElementException
	 */
	List<Product> getProductsByPurchaseId(long id) throws NullPointerException, NoSuchElementException;
	
	/**
	 * @param id
	 * @return double that represent SUM of price for all products in purchase 
	 * @throws NullPointerException
	 * @throws NoSuchElementException
	 */
	double getTotalPriceById(long id) throws NullPointerException, NoSuchElementException;
	
	/**
	 * @param datetimeStart
	 * @param datetimeEnd
	 * @return List of {@link Purchase} where created time is between certain period
	 * @throws NullPointerException
	 * @throws NoSuchElementException
	 */
	List<Purchase> getByDate(LocalDateTime datetimeStart, LocalDateTime datetimeEnd) throws NullPointerException, NoSuchElementException;
	
	/**
	 * @param id
	 * @return True if deleted
	 */
	boolean delete(long id);

	/**
	 * @param id
	 * @return boolean
	 * @throws NullPointerException
	 * @throws NoSuchElementException
	 */
	boolean checkIfCanDelete(long id) throws NullPointerException, NoSuchElementException;
}
