package sk.upjs.drpaz.controllers;

import java.io.IOException;

import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.upjs.drpaz.models.ProductFxModel;
import sk.upjs.drpaz.storage.entities.Product;

public class SellingTabController {

	private ProductFxModel model;

	@FXML
	private MFXLegacyTableView<Product> allProductsTableView = new MFXLegacyTableView<>();;

	@FXML
	private MFXLegacyTableView<Product> productsInPurchaseTableView = new MFXLegacyTableView<>();

	@FXML
	private TextField productNameTextField;

	@FXML
	private TableColumn<Product, String> nameColumn;
	@FXML
	private TableColumn<Product, String> priceColumn;
	@FXML
	private TableColumn<Product, String> quantityColumn;
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
	void initialize() {
		addColumnsToAllProducts();
		addColumnsToPurchase();

		allProductsAddListener();
		productsInPurchaseListener();

		productNameTextField.textProperty().bindBidirectional(model.nameProperty());

		productsInPurchaseTableView.setItems(model.getProductsInPurchaseModel());
		allProductsTableView.setItems(model.getAllProductsModel());

		productNameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue,
				newValue) -> allProductsTableView.setItems(model.getAllProductsModelByName(newValue)));
	}

	private void addColumnsToPurchase() {
		nameColumnPurchase.setCellValueFactory(new PropertyValueFactory<>("name"));
		priceColumnPurchase.setCellValueFactory(new PropertyValueFactory<>("price"));
		quantityColumnPurchase.setCellValueFactory(new PropertyValueFactory<>("quantity"));

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

					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Dialog.fxml"));
					DialogController dialogController = new DialogController(p);
					fxmlLoader.setController(dialogController);
					Parent parent = null;
					try {
						parent = fxmlLoader.load();
					} catch (IOException e1) {
					}
					Scene scene = new Scene(parent);
					Stage stage = new Stage();
					stage.setScene(scene);
					stage.initModality(Modality.APPLICATION_MODAL);
					stage.setTitle("Edit quantity");
					stage.showAndWait();

					if (p.getQuantity() <= 0) {
						model.getProductsInPurchaseModel().remove(p);
					} else {
						model.getProductsInPurchaseModel().set(index, null);
						model.getProductsInPurchaseModel().set(index, p);
					}System.out.println(model.getProductsInPurchaseModel());
					System.out.println(productsInPurchaseTableView.getItems());
					setTotal();
				});
			}

		});

	}

	private void allProductsAddListener() {
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

	private void addProductToPurchase() {
		Product product = allProductsTableView.getSelectionModel().getSelectedItem();
		boolean flag = false;
		for (Product p : model.getProductsInPurchaseModel()) {
			if (p.getId() == product.getId()) {
				int index = model.getProductsInPurchaseModel().indexOf(p);
				Product temp = new Product(p.getId(), p.getName(), p.getPrice(), p.getQuantity() + 1,
						p.getAlertQuantity(), p.getDescription());
				model.getProductsInPurchaseModel().set(index, null);
				model.getProductsInPurchaseModel().set(index, temp);
				flag = true;
			}
		}
		if (!flag) {
			Product temp = new Product(product.getId(), product.getName(), product.getPrice(), 1,
					product.getAlertQuantity(), product.getDescription());
			model.getProductsInPurchaseModel().add(temp);
		}
		setTotal();	
	}

	private void setTotal() {
		double total = 0;
		for (Product product : model.getProductsInPurchase())
			total += product.getPrice() * product.getQuantity();
		totalLabel.setText("Total: " + String.format("%.02f", total));
	}

}