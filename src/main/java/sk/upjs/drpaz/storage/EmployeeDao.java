package sk.upjs.drpaz.storage;

import java.util.List;
import java.util.NoSuchElementException;

public interface EmployeeDao {

	Employee getById(long id) throws NoSuchElementException;
	
	List<Employee> getAll();

	Employee save(Employee employee) throws NullPointerException, NoSuchElementException;

	boolean delete(long id);

	Employee getByLogin(String login) throws NoSuchElementException, NullPointerException;
	
	boolean changePassword(String oldLogin, String oldPassword, String newLogin, String newPassword) throws NoSuchElementException, NullPointerException;

}
