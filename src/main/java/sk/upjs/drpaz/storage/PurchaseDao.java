package sk.upjs.drpaz.storage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public interface PurchaseDao {

	Purchase getById(long id) throws NoSuchElementException;

	Purchase save(Purchase purchase) throws NullPointerException, NoSuchElementException;

	List<Product> getProductsByPurchaseId(long id) throws NullPointerException, NoSuchElementException;
	
	double getTotalPriceById(long id) throws NullPointerException, NoSuchElementException;
	
	List<Purchase> getByDate(LocalDateTime datetimeStart, LocalDateTime datetimeEnd) throws NullPointerException, NoSuchElementException;
}
