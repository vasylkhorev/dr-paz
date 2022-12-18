package sk.upjs.drpaz.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import sk.upjs.drpaz.models.ProductFxModel;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Category;
import sk.upjs.drpaz.storage.entities.Product;

public class AddingTabController {

	private ProductFxModel model = new ProductFxModel();
	private Product edited;

	@FXML
	private MFXButton saveButton;
	@FXML
	private Label newProductLabel;
	@FXML
	private TableColumn<Product, String> nameColumn;
	@FXML
	private TableColumn<Product, String> priceColumn;
	@FXML
	private TableColumn<Product, String> quantityColumn;
	@FXML
	private MFXTextField alertQuantityTextField;

	@FXML
	private MFXFilterComboBox<Category> category1ComboBox;

	@FXML
	private MFXFilterComboBox<Category> category2ComboBox;

	@FXML
	private MFXFilterComboBox<Category> category3ComboBox;

	@FXML
	private TextArea desctiptionTextArea;

	@FXML
	private MFXTextField nameTextField;

	@FXML
	private MFXTextField priceTextField;

	@FXML
	private MFXLegacyTableView<Product> productsTableView;

	@FXML
	private MFXTextField quantityTextField;

	@FXML
	void deleteButtonClick(ActionEvent event) {
		deleteProduct();
	}

	@FXML
	void editButtonClick(ActionEvent event) {
		editSelectedProduct();
	}

	@FXML
	void newButtonClick(ActionEvent event) {
		edited = null;
		newProductLabel.setText("New product");
		clearFields();
		nameTextField.requestFocus();
	}

	@FXML
	void saveButtonClick(ActionEvent event) {
		Product newProduct = null;
		if (edited == null) {
			newProduct = new Product(null, nameTextField.getText(), Double.parseDouble(priceTextField.getText()),
					Integer.parseInt(quantityTextField.getText()), Integer.parseInt(alertQuantityTextField.getText()),
					desctiptionTextArea.getText());
		} else {
			newProduct = new Product(edited.getId(), nameTextField.getText(),
					Double.parseDouble(priceTextField.getText()), Integer.parseInt(quantityTextField.getText()),
					Integer.parseInt(alertQuantityTextField.getText()), desctiptionTextArea.getText());
		}
		newProduct = DaoFactory.INSTANCE.getProductDao().save(newProduct);

		List<Category> list = new ArrayList<>();
		list.add(category1ComboBox.getSelectedItem());
		list.add(category2ComboBox.getSelectedItem());
		list.add(category3ComboBox.getSelectedItem());
		list = list.stream().distinct().collect(Collectors.toList());
		list.remove(null);
		DaoFactory.INSTANCE.getCategoryDao().deleteByProduct(newProduct);
		for (Category category : list) {
			DaoFactory.INSTANCE.getCategoryDao().addCategoryToProduct(category, newProduct);
		}

		int index = model.getAllProductsModel().indexOf(edited);
		if (index == -1) {
			model.getAllProductsModel().add(newProduct);
		} else {
			model.getAllProductsModel().set(index, newProduct);
		}

		edited = null;
		newProductLabel.setText("New product");


		clearFields();
	}

	@FXML
	void initialize() {
		setCategoryItem();
		addColumns();
		correctInputListener();

		productsListener();

		productsTableView.setItems(model.getAllProductsModel());
	}

	private void setCategoryItem() {
		ObservableList<Category> all = FXCollections.observableArrayList(DaoFactory.INSTANCE.getCategoryDao().getAll());

		category1ComboBox.setItems(all);
		category2ComboBox.setItems(all);
		category3ComboBox.setItems(all);
	}

	private void correctInputListener() {
		nameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});

		priceTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});

		quantityTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});

	}

	private void checkCorrect() {
		if (nameTextField.getText() == null || nameTextField.getText().isEmpty() || nameTextField.getText().isBlank()) {
			saveButton.setDisable(true);
			return;
		}
		try {
			Double.parseDouble(priceTextField.getText());
		} catch (Exception e) {
			saveButton.setDisable(true);
			return;
		}
		try {
			Integer.parseInt(quantityTextField.getText());
		} catch (Exception e) {
			saveButton.setDisable(true);
			return;
		}
		saveButton.setDisable(false);
	}

	private void productsListener() {
		productsTableView.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				editSelectedProduct();
			}
			if (event.getButton().equals(MouseButton.SECONDARY)) {
				MenuItem editItem = new MenuItem("Edit");
				MenuItem deleteItem = new MenuItem("Delete");
				ContextMenu contextMenu = new ContextMenu(editItem, deleteItem);
				contextMenu.setX(event.getScreenX());
				contextMenu.setY(event.getScreenY());
				contextMenu.show(productsTableView.getScene().getWindow());
				editItem.setOnAction(e -> {
					editSelectedProduct();
				});
				deleteItem.setOnAction(event1 -> {
					deleteProduct();
				});
			}
		});
	}

	private void editSelectedProduct() {
		cancelButtonClick1(null);
		cancelButtonClick2(null);
		cancelButtonClick3(null);

		edited = productsTableView.getSelectionModel().getSelectedItem();
		if(edited == null) {
			return;
		}
		newProductLabel.setText("Edit product");

		nameTextField.setText(edited.getName());
		priceTextField.setText(edited.getPrice() + "");
		quantityTextField.setText(edited.getQuantity() + "");
		alertQuantityTextField.setText(edited.getAlertQuantity() + "");
		desctiptionTextArea.setText(edited.getDescription());

		List<Category> list = DaoFactory.INSTANCE.getCategoryDao().getByProduct(edited);

		List<MFXFilterComboBox<Category>> listComboBoxs = new ArrayList<>();
		listComboBoxs.add(category1ComboBox);
		listComboBoxs.add(category2ComboBox);
		listComboBoxs.add(category3ComboBox);

		for (int i = 0; i < list.size(); i++) {
			listComboBoxs.get(i).selectItem(list.get(i));
		}
	}

	private void addColumns() {
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
	}

	public void clearFields() {
		nameTextField.clear();
		priceTextField.clear();
		quantityTextField.clear();
		alertQuantityTextField.clear();
		desctiptionTextArea.clear();

		category1ComboBox.clearSelection();
		category2ComboBox.clearSelection();
		category3ComboBox.clearSelection();

		category1ComboBox.requestFocus();
		category2ComboBox.requestFocus();
		category3ComboBox.requestFocus();

		productsTableView.requestFocus();
	}

	@FXML
	void cancelButtonClick1(ActionEvent event) {
		category1ComboBox.clearSelection();
		category1ComboBox.requestFocus();
		productsTableView.requestFocus();
	}

	@FXML
	void cancelButtonClick2(ActionEvent event) {
		category2ComboBox.clearSelection();
		category2ComboBox.requestFocus();
		productsTableView.requestFocus();
	}

	@FXML
	void cancelButtonClick3(ActionEvent event) {
		category3ComboBox.clearSelection();
		category3ComboBox.requestFocus();
		productsTableView.requestFocus();
	}

	void deleteProduct() {
		Product selected = productsTableView.getSelectionModel().getSelectedItem();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText("You are going to delete product!");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == alert.getButtonTypes().get(1)) {
			return;
		}

		if(DaoFactory.INSTANCE.getProductDao().checkIfCanDelete(selected.getId())) {
			DaoFactory.INSTANCE.getProductDao().delete(selected.getId());
			productsTableView.getItems().remove(selected);
			clearFields();
			edited = null;
			newProductLabel.setText("New product");
		}else {
			Alert alert2 = new Alert(AlertType.WARNING);
			alert2.setContentText("You can not delete Product that already is in a purchase!");
			alert2.showAndWait();
			return;
		}
	}
}
