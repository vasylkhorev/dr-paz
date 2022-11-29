package sk.upjs.drpaz;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sk.upjs.drpaz.storage.DaoFactory;
import sk.upjs.drpaz.storage.Employee;
import sk.upjs.drpaz.storage.Product;
import sk.upjs.drpaz.storage.Purchase;

public class PurchaseFxModel {

	private Long id;
	private ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
	private ObjectProperty<Employee> employee = new SimpleObjectProperty<>();
	
	private ObservableList<Purchase> allPurchases;
	private ObservableList<Product> productsInPurchase;
	
	
	public PurchaseFxModel() {
		List<Purchase> list = DaoFactory.INSTANCE.getPurchaseDao().getAll();
		allPurchases = FXCollections.observableArrayList(list);
		productsInPurchase = FXCollections.observableArrayList();
	}
	
	public PurchaseFxModel(Purchase purchase) {
		this.id = purchase.getId();
		setCreatedAt(purchase.getCreatedAt());
		setEmployee(purchase.getEmployee());
		
		List<Purchase> list1 = DaoFactory.INSTANCE.getPurchaseDao().getAll();
		allPurchases = FXCollections.observableArrayList(list1);
		List<Product> list2 = DaoFactory.INSTANCE.getPurchaseDao().getProductsByPurchaseId(purchase.getId());
		productsInPurchase = FXCollections.observableArrayList(list2);
	}
	
	
	public ObjectProperty<LocalDateTime> createdAtProperty() {
		return createdAt;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt.get();
	}

	public void setCreatedAt(LocalDateTime localDateTime) {
		this.createdAt.set(localDateTime);
	}
	
	public ObjectProperty<Employee> employeeProperty() {
		return employee;
	}
	
	public Employee getEmployee() {
		return employee.get();
	}

	public void setEmployee(Employee employee) {
		this.employee.set(employee);
	}
	
	public ObservableList<Purchase> getAllPurchasesModel() {
		return allPurchases;
	}
	
	public List<Purchase> getAllPurchases() {
		return new ArrayList<>(allPurchases);
	}
	
	public ObservableList<Product> getAllProductsInPurchaseModel() {
		return productsInPurchase;
	}
	
	public List<Product> getAllProductsInPurchase() {
		return new ArrayList<>(productsInPurchase);
	}
	
	public Purchase getPurchase() {
		return new Purchase(id, getEmployee(), getCreatedAt(), getAllProductsInPurchase());
	}
}
