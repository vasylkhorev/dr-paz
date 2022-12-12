package sk.upjs.drpaz.biznis;

import sk.upjs.drpaz.storage.entities.Product;

public class ProductStatistics {
	
	private Product product;
	private String name;
	private int count;
	private Double total;
	
	public ProductStatistics(Product product, int count, Double total) {
		this.product = product;
		this.name = product.getName();
		this.count = count;
		this.total = total;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}
}
