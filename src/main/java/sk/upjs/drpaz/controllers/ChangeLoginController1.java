package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sk.upjs.drpaz.LoggedUser;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Employee;

public class ChangeLoginController1 {
	
	private Employee currentUser = LoggedUser.INSTANCE.getLoggedUser();

	@FXML
	private MFXButton cancelButton;

	@FXML
	private MFXButton okButton;

	@FXML
	private MFXTextField oldPasswordTextField;

	@FXML
	private MFXPasswordField passwordNewConfirmField;

	@FXML
	private MFXPasswordField passwordNewField;

	@FXML
	void cancelClickButton(ActionEvent event) {
		passwordNewConfirmField.getScene().getWindow().hide();
	}

	@FXML
	void okButtonClick(ActionEvent event) {
		boolean check = DaoFactory.INSTANCE.getEmployeeDao().changePassword(currentUser.getLogin(), oldPasswordTextField.getText(), currentUser.getLogin(), passwordNewField.getText());
    	passwordNewConfirmField.getScene().getWindow().hide();
	}

	private void checkCorrect() {
		boolean check = true;
		if (oldPasswordTextField.getText() == null || oldPasswordTextField.getText().isEmpty()
				|| oldPasswordTextField.getText().isBlank()) {
			okButton.setDisable(true);
			oldPasswordTextField.setStyle("-fx-border-color: red");
			check = false;
		} else {
			oldPasswordTextField.setStyle("-fx-border-style: none");
		}

		if (passwordNewField.getText() == null || passwordNewField.getText().isEmpty()
				|| passwordNewField.getText().isBlank()) {
			okButton.setDisable(true);
			passwordNewField.setStyle("-fx-border-color: red");
			check = false;
		} else {
			passwordNewField.setStyle("-fx-border-style: none");
		}

		if (passwordNewConfirmField.getText() == null || passwordNewConfirmField.getText().isEmpty()
				|| passwordNewConfirmField.getText().isBlank()
				|| !passwordNewConfirmField.getText().equals(passwordNewField.getText())) {
			okButton.setDisable(true);
			passwordNewConfirmField.setStyle("-fx-border-color: red");
			check = false;
		} else {
			passwordNewConfirmField.setStyle("-fx-border-style: none");
		}

		if (check) {
			okButton.setDisable(false);
		}
	}
}
