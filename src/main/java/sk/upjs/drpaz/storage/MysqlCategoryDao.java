package sk.upjs.drpaz.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;



public class MysqlCategoryDao implements CategoryDao {
	private JdbcTemplate jdbcTemplate;

	public MysqlCategoryDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public Category getById(Long id) {
		String sql = "SELECT id, name FROM category WHERE id = " + id;
		return jdbcTemplate.queryForObject(sql, new CategoryRowMapper());
	}
	
	@Override
	public List<Category> getAll() {
		String sql = "SELECT id, name FROM category";
		List<Category> categories = jdbcTemplate.query(sql, new CategoryRowMapper() );
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
			throw new NoSuchElementException("product with id " + category.getId() + " not in DB");
		}
	}
	
	@Override
	public boolean delete(long id) {
		int changed = jdbcTemplate.update("DELETE FROM category WHERE id = " + id);
		return changed == 1;
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

}
