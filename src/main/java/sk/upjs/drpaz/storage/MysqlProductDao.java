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

public class MysqlProductDao implements ProductDao {

	private class ProductRowMapper implements RowMapper<Product> {
		public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
			Product product = new Product();
			product.setId(rs.getLong("id"));
			product.setName(rs.getString("name"));
			product.setPrice(rs.getDouble("price"));
			product.setQuantity(rs.getInt("quantity"));
			product.setAlertQuantity(rs.getInt("alert_quantity"));
			product.setDescription(rs.getString("description"));
			return product;
		}
	}

	private JdbcTemplate jdbcTemplate;

	public MysqlProductDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// TODO TRY-CATCH BLOCK
	public Product getById(long id) throws NoSuchElementException {
		String sql = "SELECT id, name, price, quantity, alert_quantity, description FROM product WHERE id = " + id;
		return jdbcTemplate.queryForObject(sql, new ProductRowMapper());
	}

	public List<Product> getAll() {
		String sql = "SELECT id, name, price, quantity, alert_quantity, description FROM product";
		List<Product> products = jdbcTemplate.query(sql, new ProductRowMapper());
		return products;

	}

	public Product save(Product product) throws NullPointerException, NoSuchElementException {
		// TODO SOMETHING ELSE???
		if (product == null || product.getName() == null)
			throw new NullPointerException("cannot save null or have null as product");

		if (product.getId() == null) { // INSERT
			SimpleJdbcInsert sjdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
			sjdbcInsert.withTableName("product");
			sjdbcInsert.usingGeneratedKeyColumns("id");
			sjdbcInsert.usingColumns("name");
			sjdbcInsert.usingColumns("price");
			sjdbcInsert.usingColumns("quantity");
			sjdbcInsert.usingColumns("alert_quantity");
			sjdbcInsert.usingColumns("description");

			Map<String, Object> values = new HashMap<>();

			values.put("name", product.getName());
			values.put("price", product.getPrice());
			values.put("quantity", product.getQuantity());
			values.put("alert_quantity", product.getAlertQuantity());
			values.put("description", product.getDescription());

			long id = sjdbcInsert.executeAndReturnKey(values).longValue();
			return new Product(id, product.getName(), product.getPrice(), product.getQuantity(),
					product.getAlertQuantity(), product.getDescription());
		} else { // UPDATE
			String sql = "UPDATE product SET name= ?, price= ?, quantity= ?, alert_quantity= ?, description= ? WHERE id = "
					+ product.getId();
			int changed = jdbcTemplate.update(sql, product.getName(), product.getPrice(), product.getQuantity(),
					product.getAlertQuantity(), product.getDescription());
			if (changed == 1)
				return product;
			throw new NoSuchElementException("product with id " + product.getId() + " not in DB");
		}

	}

	public boolean delete(long id) {
		int changed = jdbcTemplate.update("DELETE FROM product WHERE id = " + id);
		return changed == 1;

	}

}
