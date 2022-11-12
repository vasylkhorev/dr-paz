package sk.upjs.drpaz.storage;

import java.util.NoSuchElementException;

import org.springframework.jdbc.core.JdbcTemplate;

public class MysqlEmployeeDao implements EmployeeDao {
	private JdbcTemplate jdbcTemplate;

	public MysqlEmployeeDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Employee save(Employee employee) throws NullPointerException, NoSuchElementException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean delete(Employee employee) {
		// TODO Auto-generated method stub
		return false;
	}

}
