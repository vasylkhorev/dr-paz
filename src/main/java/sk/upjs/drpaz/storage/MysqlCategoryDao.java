package sk.upjs.drpaz.storage;

import java.util.NoSuchElementException;

import org.springframework.jdbc.core.JdbcTemplate;

public class MysqlCategoryDao implements CategoryDao {
	private JdbcTemplate jdbcTemplate;

	public MysqlCategoryDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Category getByProduct(Product product) throws NullPointerException, NoSuchElementException {
		// TODO Auto-generated method stub
		return null;
	}

}
