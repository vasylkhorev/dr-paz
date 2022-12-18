package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import sk.upjs.drpaz.models.CategoryFxModel;
import sk.upjs.drpaz.storage.entities.Category;
import sk.upjs.drpaz.storage.entities.Product;

public class CategoryTabController {

    @FXML
    private MFXLegacyTableView<Category> allCategoryTableView;
    @FXML
    private MFXLegacyTableView<Product> allProductTableView;
    @FXML
    private Label categoryLabel;
    @FXML
    private MFXTextField categoryEditNameTextField;
    @FXML
    private TableColumn<Product, String> descriptionProductColumn;
    @FXML
    private MFXTextField employeeNameTextField;
    @FXML
    private TableColumn<Category, Integer> idCategoryColumn;
    @FXML
    private TableColumn<Product, Integer> idProductColumn;
    @FXML
    private TableColumn<Category, String> nameCategoryColumn;
    @FXML
    private TableColumn<Product, String> nameProductColumn;
    @FXML
    private TableColumn<Product, Double> priceProductColumn;
    @FXML
    private TableColumn<Product, Integer> quantityProductColumn;
    @FXML
    private MFXButton saveButton;

    private CategoryFxModel model = new CategoryFxModel();
    private Category edited;

    @FXML
    void saveButtonClick(ActionEvent event) {

    }
    
    @FXML
    void deleteButtonClick(ActionEvent event) {
    	deleteCategory();
    }

	@FXML
    void editButtonClick(ActionEvent event) {
    	selectedCategory();
    }

    @FXML
    void newButtonClick(ActionEvent event) {
    	edited = null;
    	categoryEditNameTextField.clear();
    	categoryLabel.setText("New Category");
    }

    @FXML
    void initialize() { 
    	setAllColumns();
    	allCategoryTableView.getItems().addAll(model.getAllCategoriesModel());
    	categoryListener();
    }

	private void categoryListener() {
		allCategoryTableView.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				selectedCategory();
			}
		});
	}

	private void selectedCategory() {
		Category category = allCategoryTableView.getSelectionModel().getSelectedItem();
		if (category == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Select category to delete!");
			alert.showAndWait();
			return;
		}
		
		categoryLabel.setText("Edit Category");
		allProductTableView.getItems().clear();
		categoryEditNameTextField.clear();
		
		CategoryFxModel modelCategory = new CategoryFxModel(category);
		allProductTableView.getItems().addAll(modelCategory.getAllProductsWithCategoryModel());
		categoryEditNameTextField.setText(category.getName());
	}

	private void setAllColumns() {
		idCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		nameCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		idProductColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		nameProductColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		quantityProductColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		priceProductColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		descriptionProductColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
	}
	
	private void deleteCategory() {
		Category selected = allCategoryTableView.getSelectionModel().getSelectedItem();
		if (selected == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Select category to delete!");
			alert.showAndWait();
			return;
		}	
	}
}

