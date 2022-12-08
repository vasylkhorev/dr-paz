package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import java.time.LocalDateTime;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.drpaz.models.PurchaseFxModel;
import sk.upjs.drpaz.storage.entities.Purchase;

public class PurchaseTabController {
	
	private PurchaseFxModel model;
	private Purchase purchase;

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
    	}else {
    		model = new PurchaseFxModel();
    	}
    	setAllColumns();
    	// LOOK HERE
    	// its better to use :
    	allPurchasesTableView.getItems().clear();
    	allPurchasesTableView.getItems().addAll(model.getAllPurchasesModel());
    	// than
//    	allPurchasesTableView.setItems(model.getAllPurchasesModel());
    }

    @FXML
    void initialize() {
    	setAllColumns();
    	allPurchasesTableView.setItems(model.getAllPurchasesModel());
    
    }

	private void setAllColumns() {
		idAllColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		employeeAllColumn.setCellValueFactory(new PropertyValueFactory<>("employee"));
		createdAllColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
	}

}

