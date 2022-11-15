package sk.upjs.drpaz.storage;

import java.time.LocalDateTime;

public class Purchase {

	private Long id;
	private Employee employee;
	private LocalDateTime createdAt;
	// TODO order_item logic
//	private List<>

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

}
