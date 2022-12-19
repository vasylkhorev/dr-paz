package sk.upjs.drpaz.storage.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import sk.upjs.drpaz.storage.dao.CategoryDao;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Category;
import sk.upjs.drpaz.storage.entities.Product;

public class MysqlCategoryDao implements CategoryDao {
	private JdbcTemplate jdbcTemplate;

	public MysqlCategoryDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Category getById(long id) {
		String sql = "SELECT id, name FROM category WHERE id = " + id;
		try {
			return jdbcTemplate.queryForObject(sql, new CategoryRowMapper());
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("category with id " + id + " not in DB");
		}

	}

	@Override
	public List<Category> getAll() {
		String sql = "SELECT id, name FROM category ORDER BY id";
		List<Category> categories = jdbcTemplate.query(sql, new CategoryRowMapper());
		return categories;
	}

	public List<Category> getByProduct(Product product) throws NullPointerException, NoSuchElementException {
		String sql = "SELECT id, name FROM category LEFT JOIN product_has_category phc ON category.id = phc.category_id WHERE product_id ="
				+ product.getId() + " ORDER BY id";
		return jdbcTemplate.query(sql, new CategoryRowMapper());
	}

	public Category save(Category category) throws NullPointerException, NoSuchElementException {
		if (category == null || category.getName() == null)
			throw new NullPointerException("cannot save null or have null as category");

		if (category.getId() == null) { // INSERT
			SimpleJdbcInsert sjdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
			sjdbcInsert.withTableName("category");
			sjdbcInsert.usingGeneratedKeyColumns("id");
			sjdbcInsert.usingColumns("name");

			Map<String, Object> values = new HashMap<>();

			values.put("name", category.getName());

			long id = sjdbcInsert.executeAndReturnKey(values).longValue();
			return new Category(id, category.getName());
		} else { // UPDATE
			String sql = "UPDATE category SET name= ? WHERE id = " + category.getId();
			int changed = jdbcTemplate.update(sql, category.getName());
			if (changed == 1)
				return category;
			throw new NoSuchElementException("category with id " + category.getId() + " not in DB");
		}
	}

	@Override
	public boolean delete(long id) {
		jdbcTemplate.update("DELETE FROM product_has_category WHERE category_id = " + id);
		int changed = jdbcTemplate.update("DELETE FROM category WHERE id = " + id);
		return changed == 1;
	}

	@Override
	public boolean addCategoryToProduct(Category category, Product product) {
		if (category == null || product == null || category.getName() == null || product.getName() == null) {
			throw new NullPointerException("cannot save null or have null as category or product");
		}
		if (category.getId() == null && product.getId() == null) { // category and product are not saved in db -- INSERT
			category = save(category);
			product = DaoFactory.INSTANCE.getProductDao().save(product);
		}
		if (category.getId() == null && product.getId() != null) {
			category = save(category);
		}
		if (category.getId() != null && product.getId() == null) {
			product = DaoFactory.INSTANCE.getProductDao().save(product);
		}
		SimpleJdbcInsert sjdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
		sjdbcInsert.withTableName("product_has_category");
		sjdbcInsert.usingColumns("product_id", "category_id");
		Map<String, Object> values = new HashMap<>();
		values.put("product_id", product.getId());
		values.put("category_id", category.getId());
		int changed = sjdbcInsert.execute(values);
		if (changed == 1) {
			return true;
		}
		return false;
	}

	private class CategoryRowMapper implements RowMapper<Category> {

		@Override
		public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
			Category category = new Category();
			category.setId(rs.getLong("id"));
			category.setName(rs.getString("name"));
			return category;
		}
	}
	//TODO test
	@Override
	public boolean deleteByProduct(Product product) throws NoSuchElementException, NullPointerException {
		String sql = "DELETE FROM product_has_category phc WHERE phc.product_id = " + product.getId();
		int changed = jdbcTemplate.update(sql);
		return changed == 1;
	}
	
	@Override
	public List<Category> getByName(String name) throws NullPointerException {
		if (name == null) {
			throw new NullPointerException("Name of category cannot be null.");
		}
		try {
			String sql = "SELECT id, name FROM category WHERE name LIKE ?";
			return jdbcTemplate.query(sql, new CategoryRowMapper(), name + "%");
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}