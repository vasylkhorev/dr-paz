package sk.upjs.drpaz.storage;

import java.util.List;
import java.util.NoSuchElementException;

public interface EmployeeDao {

	Employee getById(long id) throws NoSuchElementException;
	
	List<Employee> getAll();

	Employee save(Employee employee) throws NullPointerException, NoSuchElementException;

	boolean delete(long id);

	Employee getByLoginAndPassword(String login, String password) throws NoSuchElementException, NullPointerException;

}
