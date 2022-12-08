package sk.upjs.drpaz.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainTabPaneAdminController {
	
	private Map<String, String> tabMap = new HashMap<String, String>() {{
		put("sellingTab", "SellingTab");
		put("addingTab", "AddingTab");
		put("employeeTab", "EmployeeTabAdmin");
		put("purchaseTab", "PurchaseTab");
	}};
	
	@FXML
	private TabPane tabPane;

	@FXML
	void onSellingTabClicked() {
	}
	@FXML
	void onAddingTabClicked() {
	}

	@FXML
	void initialize() {
		
		tabPane.getSelectionModel().clearSelection();
		
		tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
					try {
						Parent root = FXMLLoader.load(this.getClass().getResource(tabMap.get(newValue.getId()) + ".fxml"));
						
						newValue.setContent(root);

					}catch (IOException ex) {
						ex.printStackTrace();
					}
			}
		});
		tabPane.getSelectionModel().selectFirst();
	}

}
