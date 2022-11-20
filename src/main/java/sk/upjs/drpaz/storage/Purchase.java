package sk.upjs.drpaz.storage;

import java.time.LocalDateTime;
import java.util.List;

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

	public Purchase(Long id, Employee employee, LocalDateTime createdAt) {
		this.id = id;
		this.employee = employee;
		this.createdAt = createdAt;
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
	
	@Override
	public String toString() {
		return getId() + "";
	}
}
