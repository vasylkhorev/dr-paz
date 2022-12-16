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
import org.springframework.security.crypto.bcrypt.BCrypt;

import sk.upjs.drpaz.storage.dao.EmployeeDao;
import sk.upjs.drpaz.storage.entities.Employee;
import sk.upjs.drpaz.storage.exceptions.UniqueAlreadyInDatabaseException;

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
		try {
			return jdbcTemplate.queryForObject(sql, new EmployeeRowMapper());
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("employee with id " + id + " not in DB");
		}
	}

	@Override
	public Employee save(Employee employee) throws NullPointerException, NoSuchElementException, UniqueAlreadyInDatabaseException {

		if (employee == null) {
			throw new NullPointerException("Cannot save null Employee");
		}
		if (employee.getSurname() == null || employee.getName() == null || employee.getLogin() == null
				|| employee.getPassword() == null || employee.getRole() == null) {
			throw new NullPointerException("Name, Surname, Login, Password, Role cannot be null");
		}
		
		if (getByLogin(employee.getLogin()) != null) 
			if (getByLogin(employee.getLogin()).getId() != employee.getId())
				throw new UniqueAlreadyInDatabaseException("Employee with login " + employee.getLogin() + " is already in database, use changePassword for login change!");
		
		
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

			// hashing password with salt
			String salt = BCrypt.gensalt();
			values.put("password", BCrypt.hashpw(employee.getPassword(), salt));
			values.put("role", employee.getRole());

			long id = sInsert.executeAndReturnKey(values).longValue();
			return new Employee(id, employee.getName(), employee.getSurname(), employee.getPhone(), employee.getEmail(),
					employee.getLogin(), employee.getPassword(), employee.getRole());

		} else { // UPDATE WITHOUT CHANGING LOGIN AND PASSWORD SEE changePassword
			String sql = "UPDATE employee SET name=?,surname=?,phone=?, email=?, role=? " + "WHERE id=?";
			int updated = jdbcTemplate.update(sql, employee.getName(), employee.getSurname(), employee.getPhone(),
					employee.getEmail(), employee.getRole(), employee.getId());
			if (updated == 1) {
				return employee;
			} else {
				throw new NoSuchElementException("No employee with id " + employee.getId() + " in DB");
			}
		}

	}

	@Override
	public boolean delete(long id) {
		jdbcTemplate.update("DELETE FROM purchase WHERE employee_id = " + id);
		int changed = jdbcTemplate.update("DELETE FROM employee WHERE id = " + id);
		return changed == 1;
	}

	@Override
	public Employee getByLogin(String login) {
		if (login == null)
			throw new NoSuchElementException("Employee cannot have null as login");
		try {
			String sql = "SELECT id, name, surname, phone, email, login, password, role FROM Employee WHERE login=?";
			return jdbcTemplate.queryForObject(sql, new EmployeeRowMapper(), login);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Employee> getAll() {
		String sql = "SELECT id, name, surname, phone, email, login, password, role FROM employee ORDER BY id";
		return jdbcTemplate.query(sql, new EmployeeRowMapper());
	}

	@Override
	public boolean changePassword(String oldLogin, String oldPassword, String newLogin, String newPassword)
			throws NoSuchElementException, NullPointerException {
		if (oldLogin == null || oldPassword == null || newLogin == null || newPassword == null) {
			throw new NullPointerException("logins or passwords cannot be null");
		}
		Employee employee;
		String sql = "SELECT id, name, surname, phone, email, login, password, role FROM Employee WHERE login=?";
		try {
			employee = jdbcTemplate.queryForObject(sql, new EmployeeRowMapper(), oldLogin);
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
		sql = "UPDATE employee SET login=?,password=? WHERE login=?";
		if (BCrypt.checkpw(oldPassword, employee.getPassword())) {
			int updated = jdbcTemplate.update(sql, newLogin, BCrypt.hashpw(newPassword, BCrypt.gensalt()),
					oldLogin);
			if (updated == 1) {
				return true;
			}
		}
		if (oldPassword.equals(employee.getPassword())) {
			int updated = jdbcTemplate.update(sql, newLogin, BCrypt.hashpw(newPassword, BCrypt.gensalt()),
					oldLogin);
			if (updated == 1) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Employee> getByName(String name) throws NoSuchElementException {
		if (name == null)
			throw new NoSuchElementException("employee cannot have null as Name");
		try {
			String sql = "SELECT id, name, surname, phone, email, login, password, role FROM Employee WHERE name LIKE ?";
			return jdbcTemplate.query(sql, new EmployeeRowMapper(), name + "%");
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Employee> getBySurname(String surname) throws NoSuchElementException {
		if (surname == null)
			throw new NoSuchElementException("employee cannot have null as surname");
		try {
			String sql = "SELECT id, name, surname, phone, email, login, password, role FROM Employee WHERE surname LIKE ?";
			return jdbcTemplate.query(sql, new EmployeeRowMapper(), surname + "%");
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Employee> getByNameAndSurname(String name, String surname) throws NoSuchElementException {
		if (surname == null || name == null)
			throw new NoSuchElementException("employee cannot have null as surname or name");
		try {
			String sql = "SELECT id, name, surname, phone, email, login, password, role FROM Employee WHERE name LIKE ? AND surname LIKE ?";
			return jdbcTemplate.query(sql, new EmployeeRowMapper(), name + "%", surname + "%");
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
