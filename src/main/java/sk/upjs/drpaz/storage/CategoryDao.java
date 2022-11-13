package sk.upjs.drpaz.storage;

import java.util.List;
import java.util.NoSuchElementException;

public interface CategoryDao {

	List<Category> getByProduct(Product product) throws NullPointerException, NoSuchElementException;
}
