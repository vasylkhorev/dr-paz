package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.drpaz.models.PurchaseFxModel;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Purchase;

public class PurchaseTabController {

	private PurchaseFxModel model;
	private Purchase purchase;

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
	private MFXButton refreshButton;

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
		allPurchasesTableView.getItems().addAll(model.getAllPurchasesModel());
	}

	@FXML
	void initialize() {
		setAllColumns();
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
		/* MysqlPurchaseDao,getByDate() -> both dates cannot be null otherwise NullPointerException
		
		if (fromDatePicker.getValue() == null && toDatePicker.getValue() == null) {
			allPurchasesTableView.getItems()
					.addAll(FXCollections.observableArrayList(DaoFactory.INSTANCE.getPurchaseDao().getByDate(
							null, null)));
			return;
		}
		
		*/
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
	}

}
