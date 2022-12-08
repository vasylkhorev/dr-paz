package sk.upjs.drpaz.storage.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <pre>
 * Purchase has
 * -{@link Long} id
 * -{@link Employee} employee
 * -{@link LocalDateTime} createdAt
 * -List of {@link Product} productsInPurchase
 * </pre>
 */
public class Purchase {

	private Long id;
	private Employee employee;
	private LocalDateTime createdAt;
	private List<Product> productsInPurchase;

	public List<Product> getProductsInPurchase() {
		return productsInPurchase;
	}

	public void setProductsInPurchase(List<Product> productsInPurchase) {
		this.productsInPurchase = productsInPurchase;
	}

	

	public Purchase(Long id, Employee employee, LocalDateTime createdAt, List<Product> productsInPurchase) {
		this.id = id;
		this.employee = employee;
		this.createdAt = createdAt;
		this.productsInPurchase = productsInPurchase;
	}

	public Purchase() {
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public String getFormattedDate() {
		return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss"));
	}
	
	@Override
	public String toString() {
		return getId() + "";
	}
}
