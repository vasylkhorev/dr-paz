package sk.upjs.drpaz.storage;

import java.util.NoSuchElementException;

public interface EmployeeDao {

	Employee getById(long id) throws NoSuchElementException;

	Employee save(Employee employee) throws NullPointerException, NoSuchElementException;

	boolean delete(long id);

}
