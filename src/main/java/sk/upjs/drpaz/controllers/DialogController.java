package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sk.upjs.drpaz.storage.entities.Product;

public class DialogController {

	private Product product;

	@FXML
	private Label nameLabel;

	@FXML
	private MFXButton okButton;

	@FXML
	private MFXButton cancelButton;

	@FXML
	private MFXTextField quantityTextField;

	DialogController(Product product) {
		this.product = product;
	}

	@FXML
	void cancelClickButton(ActionEvent event) {
		nameLabel.getScene().getWindow().hide();
	}

	@FXML
	void okButtonClick(ActionEvent event) {
		int quantity = Integer.parseInt(quantityTextField.getText());
		product.setQuantity(quantity);
		nameLabel.getScene().getWindow().hide();
	}

	@FXML
	void initialize() {
		okButton.setDisable(true);
		nameLabel.setText(product.getName());
		quantityTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (newValue.isBlank() || newValue.isEmpty()) {
				okButton.setDisable(true);
				return;
			}
			try {
				int quantity = Integer.parseInt(newValue);
				if(quantity < 0) {
					okButton.setDisable(false);
					return;
				}
				
			} catch (NumberFormatException nfe) {
				okButton.setDisable(true);
				return;
			}
			okButton.setDisable(false);
		});
	}

}
