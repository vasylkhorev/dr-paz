package sk.upjs.drpaz;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Product;

public class ProductFxModel {
	
	private Long id;
	private StringProperty name = new SimpleStringProperty();
	private DoubleProperty price = new SimpleDoubleProperty();
	private IntegerProperty quantity = new SimpleIntegerProperty();
	private IntegerProperty alertQuantity = new SimpleIntegerProperty();
	private StringProperty description = new SimpleStringProperty();

	private ObservableList<Product> allProducts;
	private ObservableList<Product> productsInPurchase;

	public ProductFxModel() {
		List<Product> list = DaoFactory.INSTANCE.getProductDao().getAll();
		allProducts = FXCollections.observableArrayList(list);
		productsInPurchase = FXCollections.observableArrayList();
	}
	

	public ProductFxModel(Product product) {
		this.id = product.getId();
		setName(product.getName());
		setPrice(product.getPrice());
		setQuantity(product.getQuantity());
		setAlertQuantity(product.getAlertQuantity());

		
		List<Product> list = DaoFactory.INSTANCE.getProductDao().getAll();
		allProducts = FXCollections.observableArrayList(list);

		
	}

	public StringProperty nameProperty() {
		return name;
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public DoubleProperty priceProperty() {
		return this.price;
	}

	public double getPrice() {
		return this.priceProperty().get();
	}

	public void setPrice(final double price) {
		this.priceProperty().set(price);
	}

	public IntegerProperty quantityProperty() {
		return this.quantity;
	}

	public int getQuantity() {
		return this.quantityProperty().get();
	}

	public void setQuantity(final int quantity) {
		this.quantityProperty().set(quantity);
	}

	public IntegerProperty alertQuantityProperty() {
		return this.alertQuantity;
	}

	public int getAlertQuantity() {
		return this.alertQuantityProperty().get();
	}

	public void setAlertQuantity(final int alertQuantity) {
		this.alertQuantityProperty().set(alertQuantity);
	}

	public StringProperty descriptionProperty() {
		return this.description;
	}

	public String getDescription() {
		return this.descriptionProperty().get();
	}

	public void setDescription(final String description) {
		this.descriptionProperty().set(description);
	}
	
	public ObservableList<Product> getProductsInPurchaseModel() {
		return productsInPurchase;
	}
	public List<Product> getProductsInPurchase() {
		return new ArrayList<>(productsInPurchase);
	}

	public ObservableList<Product> getAllProductsModel() {
		return allProducts;
	}
	
	public ObservableList<Product> getAllProductsModelByName(String name) {
		List<Product> list = DaoFactory.INSTANCE.getProductDao().getByName(name);
		allProducts = FXCollections.observableArrayList(list);
		return allProducts;
	}
	

	public List<Product> getAllProducts() {
		return new ArrayList<>(allProducts);
	}

	public Product getProduct() {
		return new Product(id, getName(), getPrice(), getQuantity(), getAlertQuantity(), getDescription());
	}

}
