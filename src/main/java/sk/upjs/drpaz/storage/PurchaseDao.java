package sk.upjs.drpaz.storage;

import java.util.NoSuchElementException;

public interface PurchaseDao {

	Purchase getById(long id) throws NoSuchElementException;

	Purchase save(Purchase purchase) throws NullPointerException, NoSuchElementException;
}
