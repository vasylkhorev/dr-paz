package sk.upjs.drpaz.storage.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import sk.upjs.drpaz.storage.dao.ProductDao;
import sk.upjs.drpaz.storage.entities.Category;
import sk.upjs.drpaz.storage.entities.Product;
import sk.upjs.drpaz.storage.exceptions.EntityAlreadyReferencedInDatabaseException;

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

	public Product getById(long id) throws NoSuchElementException {
		String sql = "SELECT id, name, price, quantity, alert_quantity, description FROM product WHERE id = " + id;
		try {
			return jdbcTemplate.queryForObject(sql, new ProductRowMapper());
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("product with id " + id + " not in DB");
		}
	}

	public List<Product> getAll() {
		String sql = "SELECT id, name, price, quantity, alert_quantity, description FROM product ORDER BY id";
		List<Product> products = jdbcTemplate.query(sql, new ProductRowMapper());
		return products;

	}

	public Product save(Product product) throws NullPointerException, NoSuchElementException {
		if (product == null || product.getName() == null)
			throw new NullPointerException("cannot save null or have null as product");

		if (product.getId() == null) { // INSERT
			SimpleJdbcInsert sjdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
			sjdbcInsert.withTableName("product");
			sjdbcInsert.usingGeneratedKeyColumns("id");
			sjdbcInsert.usingColumns("name", "price", "quantity", "alert_quantity", "description");

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

	//TODO need to update UnitTEST
	public boolean delete(long id) throws EntityAlreadyReferencedInDatabaseException {
		if(!checkIfCanDelete(id)) {
			throw new EntityAlreadyReferencedInDatabaseException("Product with id: " + id + " is already referenced in DB.");
		}else {
			jdbcTemplate.update("DELETE FROM product_has_category WHERE product_id = " + id);
			int changed = jdbcTemplate.update("DELETE FROM product WHERE id = " + id);
			return changed == 1;
		}
	}

	@Override
	public List<Product> getByName(String name) throws NoSuchElementException {
		if (name == null) {
			throw new NoSuchElementException("product cannot have null as name");
		}
		String sql = "SELECT id, name, price, quantity, alert_quantity, description FROM product WHERE name LIKE ?";
		try {
			return jdbcTemplate.query(sql, new ProductRowMapper(),name + "%");
		} catch (DataAccessException e) {
			throw new NoSuchElementException("product with name " + name + " not in DB");
		}
	}

	@Override
	public List<Product> getByCategory(Category category) throws NullPointerException, NoSuchElementException {
		//TODO test, message me if you dont want to write tests, maybe i can try ):
		String sql = "SELECT id, name, price, quantity, alert_quantity, description FROM product p JOIN product_has_category phc ON p.id=phc.product_id WHERE category_id= " + category.getId();
		return jdbcTemplate.query(sql, new ProductRowMapper());
	}
	
	//TODO tests for this
	//THIS IS FOR CHECKING IF WE CAN DELETE Product
	@Override
	public boolean checkIfCanDelete(long id) throws NullPointerException, NoSuchElementException {
		String sql = "SELECT COUNT(*) FROM purchase_item WHERE product_id = " + id;
		int count = jdbcTemplate.queryForObject(sql, Integer.class);
		
		if (count == 0) {
			return true;
		}else {
			return false;
		}
	}

}
