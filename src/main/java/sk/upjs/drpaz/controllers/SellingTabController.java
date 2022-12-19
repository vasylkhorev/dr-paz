package sk.upjs.drpaz.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mysql.cj.conf.StringProperty;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.upjs.drpaz.LoggedUser;
import sk.upjs.drpaz.models.ProductFxModel;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Category;
import sk.upjs.drpaz.storage.entities.Product;
import sk.upjs.drpaz.storage.entities.Purchase;

public class SellingTabController {
	
    @FXML
    private SplitPane splitPane;
	private ProductFxModel model;
	@FXML
	private MFXButton cancelButton;
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
		if(model.getProductsInPurchaseModel().size() == 0){
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText("Please, add at least one product to purchase");
			alert.show();
			return;
		}
		List<Product> productsInPurchase = model.getProductsInPurchaseModel().stream().collect(Collectors.toList());
		Purchase purchase = new Purchase(null, LoggedUser.INSTANCE.getLoggedUser(), LocalDateTime.now(),
				productsInPurchase);
		DaoFactory.INSTANCE.getPurchaseDao().save(purchase);
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText("You are going to sell purchase!");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == alert.getButtonTypes().get(1)) {
			return;
		}
		model.getProductsInPurchaseModel().clear();
		for (Product product : allProductsTableView.getItems()) {
			if (productsInPurchase.contains(product))
				DaoFactory.INSTANCE.getProductDao().save(product);
		}
		model = new ProductFxModel();
		productsInPurchaseTableView.setItems(model.getProductsInPurchaseModel());
		allProductsTableView.getItems().setAll(model.getAllProductsModel());
		totalLabel.setText("Total: 0.00");
	}

	@FXML
	void cancelButtonClick(ActionEvent event) {
		categoryComboBox.clearSelection();
		categoryComboBox.requestFocus();
		cancelButton.requestFocus();
	}

	@FXML
	void initialize() {
		
		splitPane.widthProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
						setWidth();
		});
		
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

		nameColumnPurchase.prefWidthProperty().bind(productsInPurchaseTableView.widthProperty().multiply(0.49));
		priceColumnPurchase.prefWidthProperty().bind(productsInPurchaseTableView.widthProperty().multiply(0.25));
		quantityColumnPurchase.prefWidthProperty().bind(productsInPurchaseTableView.widthProperty().multiply(0.245));

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
		allProductsTableView.getItems().addAll(FXCollections.observableArrayList(collected));
	}
	

	private void addColumnsToAllProducts() {
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		alertQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("alertQuantity"));
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
		setWidth();

	}

	private void setWidth() {
		nameColumn.prefWidthProperty().bind(allProductsTableView.widthProperty().multiply(0.196));
		priceColumn.prefWidthProperty().bind(allProductsTableView.widthProperty().multiply(0.1));
		quantityColumn.prefWidthProperty().bind(allProductsTableView.widthProperty().multiply(0.10));
		alertQuantityColumn.prefWidthProperty().bind(allProductsTableView.widthProperty().multiply(0.15));
		descriptionColumn.prefWidthProperty().bind(allProductsTableView.widthProperty().multiply(0.4465));
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
					deleteFromPurchase();
				});

				quantityItem.setOnAction(event1 -> {
					Product p = productsInPurchaseTableView.getSelectionModel().getSelectedItem();
					int index = model.getProductsInPurchaseModel().indexOf(p);

					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../controllers/Dialog.fxml"));
					DialogController dialogController = new DialogController(p, allProductsTableView);
					fxmlLoader.setController(dialogController);
					Parent parent;
					try {
						parent = fxmlLoader.load();
						Scene scene = new Scene(parent);
						Stage stage = new Stage();
						stage.setScene(scene);
						stage.initModality(Modality.APPLICATION_MODAL);
						stage.setTitle("Edit quantity");
						stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/icon.png")));
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
				deleteFromPurchase();
			}
		});

	}
	private void deleteFromPurchase() {
		Product p = productsInPurchaseTableView.getSelectionModel().getSelectedItem();
		model.getProductsInPurchaseModel().remove(p);
		int index = allProductsTableView.getItems().indexOf(p);
		int quantity = allProductsTableView.getItems().get(index).getQuantity();
		allProductsTableView.getItems().set(index, null);
		Product product = new Product(p.getId(), p.getName(), p.getPrice(), p.getQuantity() + quantity, p.getAlertQuantity(), p.getDescription());
		allProductsTableView.getItems().set(index, product);
		setTotal();
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
				MenuItem detailsItem = new MenuItem("Details");
				ContextMenu contextMenu = new ContextMenu(addItem,detailsItem);
				contextMenu.setX(event.getScreenX());
				contextMenu.setY(event.getScreenY());
				contextMenu.show(allProductsTableView.getScene().getWindow());
				addItem.setOnAction(e -> {
					addProductToPurchase();
				});
				detailsItem.setOnAction(event1 -> {
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../controllers/InfoTab.fxml"));
					InfoTabController controller = new InfoTabController(allProductsTableView.getSelectionModel().getSelectedItem());
					fxmlLoader.setController(controller);
					Parent parent;
					try {
						parent = fxmlLoader.load();
						Scene scene = new Scene(parent);
						Stage stage = new Stage();
						stage.setScene(scene);
						stage.initModality(Modality.APPLICATION_MODAL);
						stage.setTitle("Details");
						stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/icon.png")));
						stage.showAndWait();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
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
		if (product.getQuantity() < product.getAlertQuantity()+1) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Stock is getting low!");
			alert.show();
		}
		if (product.getQuantity() <= 0) {
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
				product.getPrice(), product.getQuantity() - 1, product.getAlertQuantity(), product.getDescription()));

		setTotal();

	}

	private void setTotal() {
		double total = 0;
		for (Product product : model.getProductsInPurchase())
			total += product.getPrice() * product.getQuantity();
		totalLabel.setText("Total: " + String.format("%.02f", total));
	}

}