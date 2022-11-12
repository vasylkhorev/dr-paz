package sk.upjs.drpaz.storage;

import org.springframework.jdbc.core.JdbcTemplate;

public enum DaoFactory {

	INSTANCE;

	private JdbcTemplate jdbcTemplate;
	private ProductDao productDao;
	private PurchaseDao purchaseDao;
	private EmployeeDao employeeDao;
	private CategoryDao categoryDao;
	private boolean testing = false;

	public void setTesting() {
		this.testing = true;
	}

	// TODO add to all methods mysql

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public ProductDao getProductDao() {
		return productDao;
	}

	public PurchaseDao getPurchaseDao() {
		return purchaseDao;
	}

	public EmployeeDao getEmployeeDao() {
		return employeeDao;
	}

	public CategoryDao getCategoryDao() {
		return categoryDao;
	}

	public boolean isTesting() {
		return testing;
	}

}
