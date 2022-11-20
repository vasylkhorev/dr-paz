package sk.upjs.drpaz;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.IntegerStringConverter;
import sk.upjs.drpaz.storage.Product;

public class SellerController {

	private ProductFxModel model;
	@FXML
	private TableColumn<Product, Integer> alertQuantiyAllColumn;

	@FXML
	private TableView<Product> allProductsTableView;

	@FXML
	private TableColumn<Product, String> descriptionAllColumn;

	@FXML
	private TableColumn<Product, String> nameAllColumn;

	@FXML
	private TableColumn<Product, Double> priceAllColumn;

	@FXML
	private TextField productNameTextField;

	@FXML
	private Label totalPrice;

	@FXML
	private TableView<Product> productsInPurchaseTableView;

	@FXML
	private TableColumn<Product, Integer> quantityAllColumn;

	@FXML
	private TableColumn<Product, String> namePurchaseColumn;
	@FXML
	private TableColumn<Product, Double> pricePurchaseColumn;
	@FXML
	private TableColumn<Product, Integer> quantityPurchaseColumn;

	public SellerController() {
		model = new ProductFxModel();
	}

	public SellerController(Product product) {
		model = new ProductFxModel(product);
	}

	@FXML
	void sellClickButton(ActionEvent event) {

	}

	@FXML
	void initialize() {
		productNameTextField.textProperty().bindBidirectional(model.nameProperty());
		setAllColumns();
		allProductsTableView.setItems(model.getAllProductsModel());
		productsInPurchaseTableView.setItems(model.getProductsInPurchaseModel());

		// set listener on allProductsTable
		allProductsTableView.setOnMouseClicked((MouseEvent event) -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				Product product = allProductsTableView.getSelectionModel().getSelectedItem();
				product.setQuantity(1);
				boolean flag = false;
				for (Product p : model.getProductsInPurchase()) {
					if (p.getId() == product.getId()) {
						int index = model.getProductsInPurchaseModel().indexOf(p);
						Product temp = new Product(p.getId(), p.getName(), p.getPrice(), p.getQuantity() + 1,
								p.getAlertQuantity(), p.getDescription());
						model.getProductsInPurchaseModel().set(index, null);
						model.getProductsInPurchaseModel().set(index, temp);
						flag = true;
						;
					}
				}
				if (!flag)
					model.getProductsInPurchaseModel().add(product);
				double total = 0;
				for (Product p : model.getProductsInPurchase())
					total += p.getPrice() * p.getQuantity();
				totalPrice.setText(total + "");
			}
		});

	}

	void setAllColumns() {
		nameAllColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
		priceAllColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		quantityAllColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		alertQuantiyAllColumn.setCellValueFactory(new PropertyValueFactory<>("alertQuantity"));
		descriptionAllColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

		namePurchaseColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		pricePurchaseColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

		quantityPurchaseColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		quantityPurchaseColumn
				.setCellFactory(TextFieldTableCell.<Product, Integer>forTableColumn(new IntegerStringConverter()));

		
		quantityPurchaseColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Product,Integer>>() {
			
			@Override
			public void handle(CellEditEvent<Product, Integer> e) {
				Product p = e.getTableView().getItems().get(e.getTablePosition().getRow());
				int index = model.getProductsInPurchaseModel().indexOf(p);
				model.getProductsInPurchaseModel().set(index, null);
				Product temp = new Product(p.getId(), p.getName(), p.getPrice(), e.getNewValue(), p.getAlertQuantity(),
						p.getDescription());
				model.getProductsInPurchaseModel().set(index, temp);
				
				double total = 0;
				for (Product product : model.getProductsInPurchase())
					total += product.getPrice() * product.getQuantity();
				totalPrice.setText(total + "");
				
			}
		});

	}

}
