package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import java.time.LocalDateTime;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.drpaz.models.PurchaseFxModel;
import sk.upjs.drpaz.storage.entities.Purchase;

public class PurchaseTabController {
	
	private PurchaseFxModel model;

    @FXML
    private MFXLegacyTableView<Purchase> allPurchasesTableView;
    
    @FXML
    private TableColumn<Purchase, Integer> idAllColumn;

    @FXML
    private TableColumn<Purchase, Integer> employeeAllColumn;

    @FXML
    private TableColumn<Purchase, LocalDateTime> createdAllColumn;
    
    public PurchaseTabController() {
    	model = new PurchaseFxModel();
    }
    
    public PurchaseTabController(Purchase purchase) {
    	model = new PurchaseFxModel(purchase);
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

