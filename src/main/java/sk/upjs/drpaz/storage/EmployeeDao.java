package sk.upjs.drpaz.storage;

import java.util.NoSuchElementException;

public interface EmployeeDao {

	Employee save(Employee employee) throws NullPointerException, NoSuchElementException;

	boolean delete(Employee employee);

}
