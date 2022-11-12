package sk.upjs.drpaz.storage;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.jdbc.core.JdbcTemplate;

public class MysqlProductDao implements ProductDao {

	private JdbcTemplate jdbcTemplate;

	public MysqlProductDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Product getById(long id) throws NoSuchElementException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Product> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public Product save(Product product) throws NullPointerException, NoSuchElementException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean delete(long id) {
		// TODO Auto-generated method stub
		return false;
	}

}
