package sk.upjs.drpaz.storage;

import java.util.NoSuchElementException;

public interface CategoryDao {

	Category getByProduct(Product product) throws NullPointerException, NoSuchElementException;
}
