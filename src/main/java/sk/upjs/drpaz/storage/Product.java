package sk.upjs.drpaz.storage;

public class Product {

	public Product() {
	}

	public Product(Long id, String name, double price, int quantity, int alertQuantity, String description) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.alertQuantity = alertQuantity;
		this.description = description;
	}

	private Long id;
	private String name;
	private double price;
	private int quantity;
	private int alertQuantity;
	private String description;

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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getAlertQuantity() {
		return alertQuantity;
	}

	public void setAlertQuantity(int alertQuantity) {
		this.alertQuantity = alertQuantity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return this.getName() + " "+  this.getPrice() +" " +  this.getQuantity();
	}
}
