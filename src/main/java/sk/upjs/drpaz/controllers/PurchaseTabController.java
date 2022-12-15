package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import java.time.LocalDateTime;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import sk.upjs.drpaz.models.PurchaseFxModel;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Product;
import sk.upjs.drpaz.storage.entities.Purchase;

public class PurchaseTabController {

	private PurchaseFxModel model;
	private Purchase purchase;

	@FXML
    private SplitPane splitPane;
	@FXML
	private MFXDatePicker fromDatePicker;
	@FXML
	private MFXDatePicker toDatePicker;
	@FXML
	private MFXLegacyTableView<Purchase> allPurchasesTableView;
	@FXML
	private TableColumn<Purchase, Integer> idAllColumn;
	@FXML
	private TableColumn<Purchase, Integer> employeeAllColumn;
	@FXML
	private TableColumn<Purchase, LocalDateTime> createdAllColumn;
	@FXML
    private TableColumn<Product, Double> totalPriceAllColumn;
	@FXML
	private MFXButton refreshButton;
	
	@FXML
    private Label totalLabel;
	@FXML
    private MFXLegacyTableView<Product> allProductTableView;
    @FXML
    private TableColumn<Product, String> nameProductAllColumn;
    @FXML
    private TableColumn<Product, Double> priceProductAllColumn;
    @FXML
    private TableColumn<Product, Integer> quantityProductAllColumn;
    
	public PurchaseTabController() {
		model = new PurchaseFxModel();
	}

	public PurchaseTabController(Purchase purchase) {
		model = new PurchaseFxModel(purchase);
		this.purchase = purchase;
	}

	@FXML
	void refreshButtonClick(ActionEvent event) {
		if (purchase != null) {
			model = new PurchaseFxModel(purchase);
		} else {
			model = new PurchaseFxModel();
		}
		fromDatePicker.setValue(null);
		toDatePicker.setValue(null);
		setAllColumns();
		allPurchasesTableView.getItems().clear();
		allProductTableView.getItems().clear();
		allPurchasesTableView.getItems().addAll(model.getAllPurchasesModel());
	}

	@FXML
	void initialize() {
		
		splitPane.widthProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
			setWidth();
		});
		
		setAllColumns();
		purchasesListener();
		allPurchasesTableView.getItems().addAll(model.getAllPurchasesModel());

	}

	@FXML
	void onFromDatePickerClick(ActionEvent event) {
		refreshDates();
	}

	@FXML
	void onToDatePickerClick(ActionEvent event) {
		refreshDates();
		
	}

	private void refreshDates() {
		allPurchasesTableView.getItems().clear();

		if (fromDatePicker.getValue() != null && toDatePicker.getValue() == null) {
			allPurchasesTableView.getItems()
					.addAll(FXCollections.observableArrayList(DaoFactory.INSTANCE.getPurchaseDao().getByDate(
							fromDatePicker.getValue().atStartOfDay(), null)));
			return;
		}
		if (fromDatePicker.getValue() == null && toDatePicker.getValue() != null) {
			allPurchasesTableView.getItems()
					.addAll(FXCollections.observableArrayList(DaoFactory.INSTANCE.getPurchaseDao().getByDate(
							null,  toDatePicker.getValue().plusDays(1).atStartOfDay())));
			return;
		}
		if (fromDatePicker.getValue() != null && toDatePicker.getValue() != null) {
			allPurchasesTableView.getItems().addAll(FXCollections.observableArrayList(DaoFactory.INSTANCE.getPurchaseDao()
					.getByDate(fromDatePicker.getValue().atStartOfDay(), toDatePicker.getValue().plusDays(1).atStartOfDay())));
		}
	}

	private void setAllColumns() {
		idAllColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		employeeAllColumn.setCellValueFactory(new PropertyValueFactory<>("employee"));
		createdAllColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
		//TODO total price, its late i will finish it on friday.
		
		nameProductAllColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		quantityProductAllColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		priceProductAllColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		setWidth();
	}

	private void purchasesListener() {
		allPurchasesTableView.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				selectedPurchase();
			}
		});	
	}

	private void selectedPurchase() {
		allProductTableView.getItems().clear();
		Purchase purchase = allPurchasesTableView.getSelectionModel().getSelectedItem();
		if (purchase == null) {
			return;
		}
		PurchaseFxModel modelPurchase = new PurchaseFxModel(purchase);
		allProductTableView.getItems().addAll(modelPurchase.getAllProductsInPurchaseModel());
		setTotalLabel();	
	}

	private void setTotalLabel() {
		//TODO totalLabel to display totalPrice, its late i will finish it on friday.
	}		
	
	private void setWidth() {
		idAllColumn.prefWidthProperty().bind(allPurchasesTableView.widthProperty().multiply(0.15));
		employeeAllColumn.prefWidthProperty().bind(allPurchasesTableView.widthProperty().multiply(0.35));
		createdAllColumn.prefWidthProperty().bind(allPurchasesTableView.widthProperty().multiply(0.35));
		totalPriceAllColumn.prefWidthProperty().bind(allPurchasesTableView.widthProperty().multiply(0.151));
		
		nameProductAllColumn.prefWidthProperty().bind(allProductTableView.widthProperty().multiply(0.50));
		quantityProductAllColumn.prefWidthProperty().bind(allProductTableView.widthProperty().multiply(0.25));
		priceProductAllColumn.prefWidthProperty().bind(allProductTableView.widthProperty().multiply(0.251));
	}
}
