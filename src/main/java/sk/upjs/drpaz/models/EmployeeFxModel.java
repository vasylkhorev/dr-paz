package sk.upjs.drpaz.models;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Employee;

public class EmployeeFxModel {

	private Long id;
	private StringProperty name = new SimpleStringProperty();
	private StringProperty surname = new SimpleStringProperty();
	private StringProperty phone = new SimpleStringProperty();
	private StringProperty email = new SimpleStringProperty();
	private StringProperty login = new SimpleStringProperty();
	private StringProperty password = new SimpleStringProperty();
	private StringProperty role = new SimpleStringProperty();
	
	private ObservableList<Employee> allEmployees;
	
	public EmployeeFxModel() {
		List<Employee> list = DaoFactory.INSTANCE.getEmployeeDao().getAll();
		allEmployees = FXCollections.observableArrayList(list);
	}
	
	public EmployeeFxModel(Employee employee) {
		this.id = employee.getId();
		setName(employee.getName());
		setSurname(employee.getSurname());
		setPhone(employee.getPhone());
		setEmail(employee.getEmail());
		setLogin(employee.getLogin());
		setPassword(employee.getPassword());
		setRole(employee.getRole());
		
		List<Employee> list = DaoFactory.INSTANCE.getEmployeeDao().getAll();
		allEmployees = FXCollections.observableArrayList(list);
	}

	
	//GETTERS AND SETTERS
	public StringProperty nameProperty() {
		return name;
	}
	
	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty surnameProperty() {
		return surname;
	}

	public String getSurname() {
		return surname.get();
	}

	public void setSurname(String surname) {
		this.surname.set(surname);
	}

	public StringProperty phoneProperty() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone.set(phone);
	}
	
	public String getPhone() {
		return phone.get();
	}
	
	public StringProperty emailProperty() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email.set(email);
	}
	
	public String getEmail() {
		return email.get();
	}
	 
	public StringProperty loginProperty() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login.set(login);
	}
	
	public String getLogin() {
		return login.get();
	}
	
	public StringProperty passwordProperty() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password.set(password);
	}
	
	public String getPassword() {
		return password.get();
	}
	
	public StringProperty roleProperty() {
		return role;
	}
	
	public void setRole(String role) {
		this.role.set(role);
	}
	
	public String getRole() {
		return role.get();
	}
	
	public ObservableList<Employee> getAllEmployeesModel() {
		return allEmployees;
	}
	
	public Employee getEmployee() {
		return new Employee(id, getName(), getSurname(), getPhone(), getEmail(), getLogin(), getPassword(), getRole());
	}
	
	public List<Employee> getAllEmployees() {
		return new ArrayList<>(allEmployees);
	}
	
	public ObservableList<Employee> getAllEmployeesModelByNameAndSurname(String name, String surname) {
		List<Employee> list = DaoFactory.INSTANCE.getEmployeeDao().getByNameAndSurname(name, surname);
		allEmployees = FXCollections.observableArrayList(list);
		return allEmployees;
	}
	
	public ObservableList<Employee> getAllEmployeesModelByName(String name) {
		List<Employee> list = DaoFactory.INSTANCE.getEmployeeDao().getByName(name);
		allEmployees = FXCollections.observableArrayList(list);
		return allEmployees;
	}
	
	public ObservableList<Employee> getAllEmployeesModelBySurname(String surname) {
		List<Employee> list = DaoFactory.INSTANCE.getEmployeeDao().getBySurname(surname);
		allEmployees = FXCollections.observableArrayList(list);
		return allEmployees;
	}
}
