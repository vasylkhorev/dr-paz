package sk.upjs.drpaz.storage;

import org.springframework.jdbc.core.JdbcTemplate;

public enum DaoFactory {

	INSTANCE;

	private JdbcTemplate jdbcTemplate;

}
