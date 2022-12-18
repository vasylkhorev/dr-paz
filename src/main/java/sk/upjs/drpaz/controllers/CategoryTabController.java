package sk.upjs.drpaz.controllers;

import java.util.Optional;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import sk.upjs.drpaz.models.CategoryFxModel;
import sk.upjs.drpaz.storage.dao.DaoFactory;
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
    private MFXTextField categoryNameTextField;
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
    @FXML
    private SplitPane splitPane;

    private CategoryFxModel model = new CategoryFxModel();
    private Category edited;

    @FXML
    void saveButtonClick(ActionEvent event) {
    	Category newCategory = null;
    	if (edited == null) {
    		newCategory = new Category();
    		newCategory.setName(categoryEditNameTextField.getText());
    	} else {
    		newCategory = new Category(edited.getId(), categoryEditNameTextField.getText());
    	}
    	
    	newCategory = DaoFactory.INSTANCE.getCategoryDao().save(newCategory);
    	
    	refresh();
    	
    	categoryLabel.setText("New Category");
    	categoryEditNameTextField.requestFocus();
    	categoryEditNameTextField.setStyle("-fx-border-style: none");
    }
    
    private void refresh() {
    	model = new CategoryFxModel();
		saveButton.setDisable(true);
		setAllColumns();
    	allCategoryTableView.getItems().clear();
    	allCategoryTableView.getItems().addAll(model.getAllCategoriesModel());
    	edited = null;
    	categoryEditNameTextField.clear();
    	categoryLabel.setText("New Category");
		
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
    	refresh();
    	categoryEditNameTextField.requestFocus();
    }

    @FXML
    void initialize() { 
    	setAllColumns();
    	refresh();
    	categoryListener();
    	
    	categoryNameTextField.textProperty().bindBidirectional(model.nameProperty());
    	categoryNameTextField.textProperty().addListener(
				(ChangeListener<String>) (observable, oldValue, newValue) -> categoryNameSort(newValue));
    	
    	categoryEditNameTextField.textProperty().addListener(
    			(ChangeListener<String>) (observable, oldValue, newValue) -> {
					setSaveButtonOption();
					if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
						categoryEditNameTextField.setStyle("-fx-border-color: red");
					} else {
						categoryEditNameTextField.setStyle("-fx-border-style: none");
					}
				});
    	
    	splitPane.widthProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
			setWidth();
		});
    }

	private void setSaveButtonOption() {
		if (categoryEditNameTextField.getText() == null || categoryEditNameTextField.getText().isEmpty()
				|| categoryEditNameTextField.getText().isBlank()) {
			saveButton.setDisable(true);
			return;
		}
		saveButton.setDisable(false);
		
	}

	private void categoryNameSort(String newValue) {
		edited = null;
		categoryLabel.setText("New Category");
		allProductTableView.getItems().clear();
		categoryEditNameTextField.clear();
		allCategoryTableView.setItems(model.getAllCategoriesModelByName(newValue));
	}

	private void categoryListener() {
		allCategoryTableView.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				selectedCategory();
			}
		});
	}

	private void selectedCategory() {
		edited = allCategoryTableView.getSelectionModel().getSelectedItem();
		if (edited == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Select category to delete!");
			alert.showAndWait();
			return;
		}
		
		categoryLabel.setText("Edit Category");
		allProductTableView.getItems().clear();
		categoryEditNameTextField.clear();
		
		CategoryFxModel modelCategory = new CategoryFxModel(edited);
		allProductTableView.getItems().addAll(modelCategory.getAllProductsWithCategoryModel());
		categoryEditNameTextField.setText(edited.getName());
	}

	private void setAllColumns() {
		idCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		nameCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		idProductColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		nameProductColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		quantityProductColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		priceProductColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		descriptionProductColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
		setWidth();
	}
	
	private void deleteCategory() {
		Category selected = allCategoryTableView.getSelectionModel().getSelectedItem();
		if (selected == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Select category to delete!");
			alert.showAndWait();
			return;
		}	
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText("You are going to delete category!");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == alert.getButtonTypes().get(0)) {
			DaoFactory.INSTANCE.getCategoryDao().delete(selected.getId());
			allCategoryTableView.getItems().remove(selected);
			newButtonClick(null);
		}
	}
	
	private void setWidth() {
		idCategoryColumn.prefWidthProperty().bind(allCategoryTableView.widthProperty().multiply(0.25));
		nameCategoryColumn.prefWidthProperty().bind(allCategoryTableView.widthProperty().multiply(0.746));
		
		idProductColumn.prefWidthProperty().bind(allProductTableView.widthProperty().multiply(0.10));
		nameProductColumn.prefWidthProperty().bind(allProductTableView.widthProperty().multiply(0.22));
		quantityProductColumn.prefWidthProperty().bind(allProductTableView.widthProperty().multiply(0.14));
		priceProductColumn.prefWidthProperty().bind(allProductTableView.widthProperty().multiply(0.14));
		descriptionProductColumn.prefWidthProperty().bind(allProductTableView.widthProperty().multiply(0.399));
	}
}

