package sk.upjs.drpaz.models;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Category;
import sk.upjs.drpaz.storage.entities.Product;

public class CategoryFxModel {

	private Long id;
	private StringProperty name = new SimpleStringProperty();
	
	private ObservableList<Category> allCategories;
	private ObservableList<Product> productsWithCategory;
	
	public CategoryFxModel() {
		List<Category> list = DaoFactory.INSTANCE.getCategoryDao().getAll();
		allCategories = FXCollections.observableArrayList(list);
	}
	
	public CategoryFxModel(Category category) {
		this.id = category.getId();
		setName(category.getName());
		
		List<Category> list = DaoFactory.INSTANCE.getCategoryDao().getAll();
		allCategories = FXCollections.observableArrayList(list);
		productsWithCategory = FXCollections.observableArrayList( DaoFactory.INSTANCE.getProductDao().getByCategory(category));
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

	public ObservableList<Category> getAllCategoriesModel() {
		return allCategories;
	}
	
	public List<Category> getAllCategories() {
		return new ArrayList<>(allCategories);
	}
	
	public ObservableList<Product> getAllProductsWithCategoryModel() {
		return productsWithCategory;
	}
	
	public List<Product> getAllProductsWithCategory() {
		return new ArrayList<>(productsWithCategory);
	}
	
	public Category getCategory() {
		return new Category(id, getName());
	}
}
