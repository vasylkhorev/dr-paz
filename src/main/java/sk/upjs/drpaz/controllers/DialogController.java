package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import sk.upjs.drpaz.storage.entities.Product;

public class DialogController {
	private MFXLegacyTableView<Product> products;
	private Product productInPurchase;
	private Product product;

	@FXML
	private Label nameLabel;

	@FXML
	private MFXButton okButton;

	@FXML
	private MFXButton cancelButton;

	@FXML
	private MFXTextField quantityTextField;

	DialogController(Product product, MFXLegacyTableView<Product> products) {
		this.productInPurchase = product;
		this.products = products;
		if (products.getItems().contains(product)) {
			this.product = products.getItems().get(products.getItems().indexOf(product));
		}
	}

	@FXML
	void cancelClickButton(ActionEvent event) {
		nameLabel.getScene().getWindow().hide();
	}

	@FXML
	void okButtonClick(ActionEvent event) {
		int quantity = Integer.parseInt(quantityTextField.getText());
		if (product.getQuantity() < quantity - productInPurchase.getQuantity()) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("There is no more product in warehouse");
			alert.show();
			return;
		}
		int index = products.getItems().indexOf(product);
		products.getItems().set(index,
				new Product(product.getId(), product.getName(), product.getPrice(),
						product.getQuantity() - quantity + productInPurchase.getQuantity(), product.getAlertQuantity(),
						product.getDescription()));
		productInPurchase.setQuantity(quantity);
		nameLabel.getScene().getWindow().hide();
	}

	@FXML
	void initialize() {
		okButton.setDisable(true);
		nameLabel.setText(productInPurchase.getName());
		quantityTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (newValue.isBlank() || newValue.isEmpty()) {
				okButton.setDisable(true);
				return;
			}
			try {
				int quantity = Integer.parseInt(newValue);
				if (quantity < 0) {
					okButton.setDisable(true);
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
