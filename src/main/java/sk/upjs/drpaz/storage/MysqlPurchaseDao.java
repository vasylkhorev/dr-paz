package sk.upjs.drpaz.storage;

import java.util.NoSuchElementException;

import org.springframework.jdbc.core.JdbcTemplate;

public class MysqlPurchaseDao implements PurchaseDao {
	private JdbcTemplate jdbcTemplate;

	public MysqlPurchaseDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Purchase getById(long id) throws NoSuchElementException {
		// TODO Auto-generated method stub
		return null;
	}

	public Purchase save(Purchase purchase) throws NullPointerException, NoSuchElementException {
		// TODO Auto-generated method stub
		return null;
	}

}
