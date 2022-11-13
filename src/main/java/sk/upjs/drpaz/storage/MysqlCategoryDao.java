package sk.upjs.drpaz.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class MysqlCategoryDao implements CategoryDao {
	private JdbcTemplate jdbcTemplate;

	public MysqlCategoryDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Category> getByProduct(Product product) throws NullPointerException, NoSuchElementException {
		String sql = "SELECT id, name FROM category LEFT JOIN product_has_category phc ON category.id = phc.category_id WHERE product_id ="
				+ product.getId() + " ORDER BY id";

		return jdbcTemplate.query(sql, new RowMapper<Category>() {

			@Override
			public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
				Category category = new Category();
				category.setId(rs.getLong("id"));
				category.setName(rs.getString("name"));
				return category;
			}

		});
	}

}
