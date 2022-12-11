package sk.upjs.drpaz.storage.entities;

import java.util.Objects;

/**
 * <pre>
 * Employee has
 * -{@link Long} id
 * -{@link String} name
 * -{@link String} surname
 * -{@link String} phone
 * -{@link String} email
 * -{@link String} login
 * -{@link String} password
 * -{@link String} role
 */
public class Employee {

	public Employee(Long id, String name, String surname, String phone, String email, String login, String password,
			String role) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.phone = phone;
		this.email = email;
		this.login = login;
		this.password = password;
		this.role = role;
	}

	public Employee() {
	}

	public Employee( String name, String surname, String phone, String email, String login, String password,
			String role) {
		this.name = name;
		this.surname = surname;
		this.phone = phone;
		this.email = email;
		this.login = login;
		this.password = password;
		this.role = role;
	}

	private Long id;
	private String name;
	private String surname;
	private String phone;
	private String email;
	private String login;
	private String password;
	private String role;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return this.name + " " + this.surname;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		return Objects.equals(email, other.email);
	}
	
}
