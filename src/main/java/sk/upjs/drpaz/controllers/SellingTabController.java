package sk.upjs.drpaz.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.mysql.cj.conf.StringProperty;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.upjs.LoggedUser;
import sk.upjs.drpaz.models.ProductFxModel;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Category;
import sk.upjs.drpaz.storage.entities.Employee;
import sk.upjs.drpaz.storage.entities.Product;

public class SellingTabController {

	private ProductFxModel model;
	private Employee currentEmployee;
	
	@FXML
	private MFXLegacyTableView<Product> allProductsTableView = new MFXLegacyTableView<>();;
	@FXML
	private MFXLegacyTableView<Product> productsInPurchaseTableView = new MFXLegacyTableView<>();
	@FXML
	private MFXTextField productNameTextField;
	@FXML
	private MFXComboBox<Category> categoryComboBox;
	@FXML
	private TableColumn<Product, String> nameColumn;
	@FXML
	private TableColumn<Product, String> priceColumn;
	@FXML
	private TableColumn<Product, StringProperty> quantityColumn;
	@FXML
	private TableColumn<Product, String> alertQuantityColumn;
	@FXML
	private TableColumn<Product, String> descriptionColumn;
	@FXML
	private TableColumn<Product, String> nameColumnPurchase;
	@FXML
	private TableColumn<Product, String> priceColumnPurchase;
	@FXML
	private TableColumn<Product, Integer> quantityColumnPurchase;
	@FXML
	private Label totalLabel;

	public SellingTabController() {
		model = new ProductFxModel();
	}

	public SellingTabController(Product product) {
		model = new ProductFxModel(product);
	}

	@FXML
	void sellButtonClick(ActionEvent event) {

	}

	@FXML
	void cancelButtonClick(ActionEvent event) {
		categoryComboBox.clearSelection();
	}

	@FXML
	void initialize() {
		currentEmployee = LoggedUser.INSTANCE.getLoggedUser();
		addColumnsToAllProducts();
		addColumnsToPurchase();

		allProductsAddListener();
		productsInPurchaseListener();
		categoryComboBoxLogic();
		
		productNameTextField.textProperty().bindBidirectional(model.nameProperty());

		productsInPurchaseTableView.setItems(model.getProductsInPurchaseModel());
		allProductsTableView.getItems().addAll(model.getAllProductsModel());
		categoryComboBox.setItems(FXCollections.observableArrayList(DaoFactory.INSTANCE.getCategoryDao().getAll()));

		productNameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue,
				newValue) -> refreshAllProducts(newValue, categoryComboBox.getSelectedItem()));

	}

	private void categoryComboBoxLogic() {
		categoryComboBox.selectedItemProperty().addListener(new ChangeListener<Category>() {

			@Override
			public void changed(ObservableValue<? extends Category> observable, Category oldValue, Category newValue) {
				refreshAllProducts(productNameTextField.getText(), newValue);
			}
		});
	}

	private void addColumnsToPurchase() {
		nameColumnPurchase.setCellValueFactory(new PropertyValueFactory<>("name"));
		priceColumnPurchase.setCellValueFactory(new PropertyValueFactory<>("price"));
		quantityColumnPurchase.setCellValueFactory(new PropertyValueFactory<>("quantity"));

	}

	private void refreshAllProducts(String newValue, Category category) {
		List<Product> collected = new ArrayList<>();
		allProductsTableView.getItems().clear();
		if (category == null && (newValue == null || newValue.isEmpty() || newValue.isBlank())) {
			collected = model.getAllProductsModel().stream().collect(Collectors.toList());
		}
		if (category == null && newValue != null && !newValue.isEmpty() && !newValue.isBlank()) {
			collected = model.getAllProductsModel().stream()
					.filter(t -> t.getName().trim().toLowerCase().contains(newValue.toLowerCase().trim()))
					.collect(Collectors.toList());
		}
		if (category != null && (newValue == null || newValue.isEmpty() || newValue.isBlank())) {
			List<Product> products = DaoFactory.INSTANCE.getProductDao().getByCategory(category);
			collected = model.getAllProductsModel().stream().filter(t -> products.contains(t))
					.collect(Collectors.toList());
		}
		if (category != null && newValue != null && !newValue.isEmpty() && !newValue.isBlank()) {
			// intersection
			Collection<Product> col = DaoFactory.INSTANCE.getProductDao().getByCategory(category);
			collected = model.getAllProductsModel().stream()
					.filter(t -> t.getName().trim().toLowerCase().contains(newValue.toLowerCase().trim()))
					.collect(Collectors.toList());
			collected.retainAll(col);
		}
		allProductsTableView.setItems(FXCollections.observableArrayList(collected));
	}

	private void addColumnsToAllProducts() {
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		alertQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("alertQuantity"));
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
	}

	private void productsInPurchaseListener() {

		productsInPurchaseTableView.setOnMouseClicked(event -> {

			if (event.getButton().equals(MouseButton.SECONDARY)) {
				MenuItem addItem = new MenuItem("Delete");
				MenuItem quantityItem = new MenuItem("Change quantity");
				ContextMenu contextMenu = new ContextMenu(addItem, quantityItem);
				contextMenu.setX(event.getScreenX());
				contextMenu.setY(event.getScreenY());
				contextMenu.show(allProductsTableView.getScene().getWindow());
				addItem.setOnAction(e -> {
					model.getProductsInPurchaseModel()
							.remove(productsInPurchaseTableView.getSelectionModel().getSelectedItem());
					setTotal();
				});

				quantityItem.setOnAction(event1 -> {
					Product p = productsInPurchaseTableView.getSelectionModel().getSelectedItem();
					int index = model.getProductsInPurchaseModel().indexOf(p);

					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../controllers/Dialog.fxml"));
					DialogController dialogController = new DialogController(p,
							allProductsTableView);
					fxmlLoader.setController(dialogController);
					Parent parent;
					try {
						parent = fxmlLoader.load();
						Scene scene = new Scene(parent);
						Stage stage = new Stage();
						stage.setScene(scene);
						stage.initModality(Modality.APPLICATION_MODAL);
						stage.setTitle("Edit quantity");
						stage.showAndWait();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					if (p.getQuantity() <= 0) {
						model.getProductsInPurchaseModel().remove(p);
					} else {
						model.getProductsInPurchaseModel().set(index, null);
						model.getProductsInPurchaseModel().set(index, p);
					}
					setTotal();
				});
			}

		});
		productsInPurchaseTableView.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.DELETE)) {
				Product p = productsInPurchaseTableView.getSelectionModel().getSelectedItem();
				model.getProductsInPurchaseModel().remove(p);
			}
		});

	}

	private void allProductsAddListener() {
		allProductsTableView.getItems().addListener(new ListChangeListener<Product>() {

			@Override
			public void onChanged(Change<? extends Product> c) {
				allProductsTableView.refresh();
			}
		});

		allProductsTableView.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				addProductToPurchase();
			}
			if (event.getButton().equals(MouseButton.SECONDARY)) {
				MenuItem addItem = new MenuItem("Add");
				ContextMenu contextMenu = new ContextMenu(addItem);
				contextMenu.setX(event.getScreenX());
				contextMenu.setY(event.getScreenY());
				contextMenu.show(allProductsTableView.getScene().getWindow());
				addItem.setOnAction(e -> {
					addProductToPurchase();
				});
			}
		});

		allProductsTableView.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				addProductToPurchase();
			}
		});

	}

	public void addProductToPurchase() {
		Product product = allProductsTableView.getSelectionModel().getSelectedItem();
		if (product == null)
			return;
		if(product.getQuantity() <= 0) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("There is no more product in warehouse");
			alert.show();
			return;
		}

		int indexOf = model.getProductsInPurchaseModel().indexOf(product);
		if (indexOf == -1) {
			Product p = new Product(product.getId(), product.getName(), product.getPrice(), 1,
					product.getAlertQuantity(), product.getDescription());
			model.getProductsInPurchaseModel().add(p);
		} else {
			int quantity = model.getProductsInPurchaseModel().get(indexOf).getQuantity();
			Product p = new Product(product.getId(), product.getName(), product.getPrice(), quantity + 1,
					product.getAlertQuantity(), product.getDescription());
			model.getProductsInPurchaseModel().set(indexOf, p);
		}

		int indexInAll = allProductsTableView.getItems().indexOf(product);
		allProductsTableView.getItems().set(indexInAll, new Product(product.getId(), product.getName(),
				product.getPrice(), product.getQuantity()-1, product.getAlertQuantity(), product.getDescription()));

		setTotal();

	}

	private void setTotal() {
		double total = 0;
		for (Product product : model.getProductsInPurchase())
			total += product.getPrice() * product.getQuantity();
		totalLabel.setText("Total: " + String.format("%.02f", total));
	}

}