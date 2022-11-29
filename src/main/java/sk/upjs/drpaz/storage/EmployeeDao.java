package sk.upjs.drpaz.storage;

import java.util.List;
import java.util.NoSuchElementException;

public interface EmployeeDao {

	/**
	 * @param id
	 * @return {@link Employee} with given id
	 * @throws NoSuchElementException
	 */
	Employee getById(long id) throws NoSuchElementException;
	
	/**
	 * @return List of all Employees in DB
	 */
	List<Employee> getAll();

	/**
	 * @param employee
	 * @return	{@link Employee} that was saved to DB
	 * @throws NullPointerException
	 * @throws NoSuchElementException
	 * @throws UniqueAlreadyInDatabaseException
	 */
	Employee save(Employee employee) throws NullPointerException, NoSuchElementException, UniqueAlreadyInDatabaseException;

	/**
	 * @param id
	 * @return True if Employee was deleted
	 */
	boolean delete(long id);

	/**
	 * @param login
	 * @return {@link Employee} with given login
	 * @throws NoSuchElementException
	 * @throws NullPointerException
	 */
	Employee getByLogin(String login) throws NoSuchElementException, NullPointerException;
	
	/**
	 * @param oldLogin
	 * @param oldPassword
	 * @param newLogin
	 * @param newPassword
	 * @return True if password or login changed
	 * @throws NoSuchElementException
	 * @throws NullPointerException
	 */
	boolean changePassword(String oldLogin, String oldPassword, String newLogin, String newPassword) throws NoSuchElementException, NullPointerException;
	
	/**
	 * @param name
	 * @return List of {@link Employee} with given name
	 * @throws NoSuchElementException
	 */
	List<Employee> getByName(String name) throws NoSuchElementException;
	
	/**
	 * @param surname
	 * @return List of {@link Employee} with given surname
	 * @throws NoSuchElementException
	 */
	List<Employee> getBySurname(String surname) throws NoSuchElementException;
	
	/**
	 * @param name
	 * @param surname
	 * @return List of {@link Employee} with given name and surname
	 * @throws NoSuchElementException
	 */
	List<Employee> getByNameAndSurname(String name, String surname) throws NoSuchElementException;

}
