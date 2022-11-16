package sk.upjs.drpaz.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class MysqlEmployeeDao implements EmployeeDao {

	private class EmployeeRowMapper implements RowMapper<Employee> {
		@Override
		public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
			Employee employee = new Employee();
			employee.setId(rs.getLong("id"));
			employee.setName(rs.getString("name"));
			employee.setSurname(rs.getString("surname"));
			employee.setPhone(rs.getString("phone"));
			employee.setEmail(rs.getString("email"));
			employee.setLogin(rs.getString("login"));
			employee.setPassword(rs.getString("password"));
			employee.setRole(rs.getString("role"));
			return employee;
		}
	}

	private JdbcTemplate jdbcTemplate;

	public MysqlEmployeeDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Employee getById(long id) throws NoSuchElementException {
		String sql = "SELECT id, name, surname, phone, email, login, password, role FROM Employee WHERE id =" + id;
		return jdbcTemplate.queryForObject(sql, new EmployeeRowMapper());
	}

	@Override
	public Employee save(Employee employee) throws NullPointerException, NoSuchElementException {
		if (employee == null) {
			throw new NullPointerException("Cannot save null Employee");
		}
		if (employee.getSurname() == null || employee.getName() == null || employee.getLogin() == null
				|| employee.getPassword() == null || employee.getRole() == null) {
			throw new NullPointerException("Name, Surname, Login, Password, Role cannot be null");
		}
		if (employee.getId() == null) { // INSERT
			SimpleJdbcInsert sInsert = new SimpleJdbcInsert(jdbcTemplate);
			sInsert.withTableName("employee");
			sInsert.usingGeneratedKeyColumns("id");
			sInsert.usingColumns("name", "surname", "phone", "email", "login", "password", "role");

			Map<String, Object> values = new HashMap<>();
			values.put("name", employee.getName());
			values.put("surname", employee.getSurname());
			values.put("phone", employee.getPhone());
			values.put("email", employee.getEmail());
			values.put("login", employee.getLogin());
			values.put("password", employee.getPassword());
			values.put("role", employee.getRole());

			long id = sInsert.executeAndReturnKey(values).longValue();
			return new Employee(id, employee.getName(), employee.getSurname(), employee.getPhone(), employee.getEmail(),
					employee.getLogin(), employee.getPassword(), employee.getRole());

		} else { // UPDATE
			String sql = "UPDATE employee SET name=?,surname=?,phone=?, email=?, login=?,password=?,role=? "
					+ "WHERE id=?";
			int updated = jdbcTemplate.update(sql, employee.getName(), employee.getSurname(), employee.getPhone(),
					employee.getEmail(), employee.getLogin(), employee.getPassword(), employee.getRole());
			if (updated == 1) {
				return employee;
			} else {
				throw new NoSuchElementException("No employe with id " + employee.getId() + " in DB");
			}
		}

	}

	@Override
	public boolean delete(long id) {
		int changed = jdbcTemplate.update("DELETE FROM employee WHERE id = " + id);
		return changed == 1;
	}

	@Override
	public Employee getByLoginAndPassword(String login, String password) {
		try {
			String sql = "SELECT id, name, surname, phone, email, login, password, role FROM Employee WHERE login=? AND password=?";
			return jdbcTemplate.queryForObject(sql, new EmployeeRowMapper(), login, password);
		} catch (DataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Employee> getAll() {
		String sql = "SELECT id, name, surname, phone, email, login, password, role FROM employee";
		return jdbcTemplate.query(sql, new EmployeeRowMapper());
	}

}
