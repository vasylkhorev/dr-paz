package sk.upjs.drpaz.controllers;

import java.util.ArrayList;
import java.util.List;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Category;
import sk.upjs.drpaz.storage.entities.Product;

public class InfoTabController {

	private Product selectedProduct;

	@FXML
	private MFXButton close;
	
	@FXML
	private Label details;

	@FXML
	private MFXTextField alertQuantityTextField;

	@FXML
	private MFXFilterComboBox<Category> category1ComboBox;

	@FXML
	private MFXFilterComboBox<Category> category2ComboBox;

	@FXML
	private MFXFilterComboBox<Category> category3ComboBox;

	@FXML
	private TextArea desctiptionTextArea;

	@FXML
	private MFXTextField nameTextField;

	@FXML
	private MFXTextField priceTextField;

	@FXML
	private MFXTextField quantityTextField;

	public InfoTabController(Product product) {
		selectedProduct = product;
	}

	@FXML
	void initialize() {
		nameTextField.setText(selectedProduct.getName());
		priceTextField.setText(selectedProduct.getPrice() + "");
		quantityTextField.setText(selectedProduct.getQuantity() + "");
		alertQuantityTextField.setText(selectedProduct.getAlertQuantity() + "");
		desctiptionTextArea.setText(selectedProduct.getDescription());

		List<Category> list = DaoFactory.INSTANCE.getCategoryDao().getByProduct(selectedProduct);

		List<MFXFilterComboBox<Category>> listComboBoxs = new ArrayList<>();
		listComboBoxs.add(category1ComboBox);
		listComboBoxs.add(category2ComboBox);
		listComboBoxs.add(category3ComboBox);

		for (int i = 0; i < Math.min(list.size(), 3); i++) {
			listComboBoxs.get(i).setItems(FXCollections.observableArrayList(list));
			listComboBoxs.get(i).selectItem(list.get(i));
		}
	}

	@FXML
	void closeButton(ActionEvent event) {
		close.getScene().getWindow().hide();
	}
}
