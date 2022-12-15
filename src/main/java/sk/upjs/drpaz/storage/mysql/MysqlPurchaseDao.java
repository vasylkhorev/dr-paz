package sk.upjs.drpaz.storage.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.dao.PurchaseDao;
import sk.upjs.drpaz.storage.entities.Product;
import sk.upjs.drpaz.storage.entities.Purchase;

public class MysqlPurchaseDao implements PurchaseDao {
	private JdbcTemplate jdbcTemplate;

	public MysqlPurchaseDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private class PurchaseRowMapper implements RowMapper<Purchase> {
		@Override
		public Purchase mapRow(ResultSet rs, int rowNum) throws SQLException {
			Purchase purchase = new Purchase();
			purchase.setId(rs.getLong("id"));
			purchase.setEmployee(DaoFactory.INSTANCE.getEmployeeDao().getById(rs.getLong("employee_id")));
			purchase.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
			return purchase;
		}
	}

	public List<Purchase> getAll() {
		String sql = "SELECT id, employee_id, created_at FROM purchase ORDER BY id;";
		List<Purchase> purchases = jdbcTemplate.query(sql, new PurchaseRowMapper());
		return purchases;
	}

	public Purchase getById(long id) throws NoSuchElementException {
		String sql = "SELECT id, employee_id, created_at FROM Purchase WHERE id=" + id;
		try {
			return jdbcTemplate.queryForObject(sql, new PurchaseRowMapper());
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Purchase with id " + id + " not in DB");
		}
	}

	public Purchase save(Purchase purchase) throws NullPointerException, NoSuchElementException {
		if (purchase == null)
			throw new NullPointerException("Cannot save null purchase");
		if ( purchase.getProductsInPurchase() == null)
			throw new NullPointerException("Purchase cannot have null products");
		if (purchase.getId() == null) {
			SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
			simpleJdbcInsert.withTableName("Purchase");
			simpleJdbcInsert.usingGeneratedKeyColumns("id");
			simpleJdbcInsert.usingColumns("employee_id", "created_at");
			Map<String, Object> values = new HashMap<>();
			values.put("employee_id", purchase.getEmployee().getId());
			values.put("created_at", purchase.getCreatedAt());
			long id = simpleJdbcInsert.executeAndReturnKey(values).longValue();
			
			Purchase purchase2 = new Purchase(id, purchase.getEmployee(), purchase.getCreatedAt(),purchase.getProductsInPurchase());
			
			//new simpleJdbcInsert is there, because when creating..........tldr:otherwise error
			SimpleJdbcInsert simpleJdbcInsertPurchaseItem = new SimpleJdbcInsert(jdbcTemplate);
			simpleJdbcInsertPurchaseItem.withTableName("purchase_item");
			simpleJdbcInsertPurchaseItem.usingColumns("purchase_id", "product_id", "quantity", "price");
			
			for (Product product : purchase2.getProductsInPurchase()) {
				Map<String, Object> values1 = new HashMap<>();
				values1.put("purchase_id", id);
				values1.put("product_id", product.getId());
				//Using .frequency to find how many same products are in list
				values1.put("quantity", product.getQuantity());
				values1.put("price", product.getPrice());
				simpleJdbcInsertPurchaseItem.execute(values1);
				
			}
			//updating correct quantities.
			for (Product product : purchase2.getProductsInPurchase()) {
				jdbcTemplate.update("UPDATE product SET quantity = quantity - ? WHERE id = ?",product.getQuantity(), product.getId());
			}
			return purchase2;

		} else { // UPDATE
			String sql = "UPDATE purchase SET employee_id=?, created_at=? " + "WHERE id = ?";
			int changed = jdbcTemplate.update(sql, purchase.getEmployee().getId(), purchase.getCreatedAt(),
					purchase.getId());
			if (changed == 1) {
				return purchase;
			} else {
				throw new NoSuchElementException("No purchase with id " + purchase.getId() + " in DB");

			}
		}

	}

	@Override
	public List<Product> getProductsByPurchaseId(long id) throws NullPointerException, NoSuchElementException {

		String sql = "SELECT product.id, name, purchase_item.price, purchase_item.quantity, alert_quantity, description FROM purchase_item LEFT JOIN product ON product_id=product.id WHERE purchase_id="
				+ id + " ORDER BY product.id";
		return jdbcTemplate.query(sql, new ResultSetExtractor<List<Product>>() {

			@Override
			public List<Product> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<Product> products = new ArrayList<>();
				while (rs.next()) {
					Product product = new Product();
					product.setId(rs.getLong("id"));
					product.setName(rs.getString("name"));
					product.setPrice(rs.getDouble("price"));
					product.setQuantity(rs.getInt("quantity"));
					product.setAlertQuantity(rs.getInt("alert_quantity"));
					product.setDescription(rs.getString("description"));
					products.add(product);
				}
				return products;
			}
		});
	}

	@Override
	public double getTotalPriceById(long id) throws NullPointerException, NoSuchElementException {
		String sql = "SELECT SUM(quantity*price) FROM purchase_item WHERE purchase_id =" + id;
		return jdbcTemplate.query(sql, new ResultSetExtractor<Double>() {

			@Override
			public Double extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs.getDouble(1);
				return 0.0;
			}
		});
	}

	@Override
	public List<Purchase> getByDate(LocalDateTime datetimeStart, LocalDateTime datetimeEnd)
			throws NullPointerException, NoSuchElementException {
		if (datetimeStart == null && datetimeEnd == null)
			throw new NullPointerException("both dates cannot be null");
		if(datetimeEnd == null && datetimeStart != null) {
			String sql = "SELECT id, employee_id, created_at FROM purchase WHERE created_at > ? ";
			try {
				return jdbcTemplate.query(sql, new PurchaseRowMapper(), datetimeStart);
			} catch (DataAccessException e) {
				throw new NoSuchElementException(
						"No Purchase after " + datetimeStart.toString());
			}
		}
		if(datetimeEnd != null && datetimeStart == null) {
			String sql = "SELECT id, employee_id, created_at FROM purchase WHERE created_at < ? ";
			try {
				return jdbcTemplate.query(sql, new PurchaseRowMapper(), datetimeEnd);
			} catch (DataAccessException e) {
				throw new NoSuchElementException(
						"No Purchase after " + datetimeStart.toString());
			}
		}
		String sql = "SELECT id, employee_id, created_at FROM purchase WHERE created_at BETWEEN ? AND ?";
		try {
			return jdbcTemplate.query(sql, new PurchaseRowMapper(), datetimeStart, datetimeEnd);
		} catch (DataAccessException e) {
			throw new NoSuchElementException(
					"No Purchase between " + datetimeStart.toString() + " and " + datetimeEnd.toString());
		}
	}

	@Override
	public boolean delete(long id) {
		jdbcTemplate.update("DELETE FROM purchase_item WHERE purchase_id = " + id);
		int changed = jdbcTemplate.update("DELETE FROM purchase WHERE id = " + id);
		return changed == 1;
	}



}
