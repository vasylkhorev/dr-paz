package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sk.upjs.drpaz.LoggedUser;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Employee;

public class ChangePasswordController {

	private Employee currentUser = LoggedUser.INSTANCE.getLoggedUser();

	@FXML
	private MFXButton cancelButton;

	@FXML
	private MFXButton okButton;

	@FXML
	private MFXPasswordField oldPasswordTextField;

	@FXML
	private MFXPasswordField passwordNewConfirmField;

	@FXML
	private MFXPasswordField passwordNewField;

	@FXML
	void cancelClickButton(ActionEvent event) {
		passwordNewConfirmField.getScene().getWindow().hide();
	}

	@FXML
	void initialize() {
		correctInputListeners();
	}

	@FXML
	void okButtonClick(ActionEvent event) {
		// TODO

		boolean check = DaoFactory.INSTANCE.getEmployeeDao().changePassword(currentUser.getLogin(),
				oldPasswordTextField.getText(), currentUser.getLogin(), passwordNewField.getText());
		if (check) {
			passwordNewConfirmField.getScene().getWindow().hide();
		} else {
			oldPasswordTextField.setStyle("-fx-border-color: red");
		}
	}

	private void correctInputListeners() {
		oldPasswordTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});

		passwordNewField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
			if (passwordNewField.getText() == null || passwordNewField.getText().isEmpty()
					|| passwordNewField.getText().isBlank()
					|| !passwordNewField.getText().equals(passwordNewField.getText())) {

				passwordNewField.setStyle("-fx-border-color: red");
			} else {
				passwordNewField.setStyle("-fx-border-style: none");
			}
		});

		passwordNewConfirmField.textProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					checkCorrect();
					if (passwordNewConfirmField.getText() == null || passwordNewConfirmField.getText().isEmpty()
							|| passwordNewConfirmField.getText().isBlank()
							|| !passwordNewConfirmField.getText().equals(passwordNewField.getText())) {

						passwordNewConfirmField.setStyle("-fx-border-color: red");
					} else {
						passwordNewConfirmField.setStyle("-fx-border-style: none");
					}
				});
	}

	private void checkCorrect() {
		boolean check = false;
		if (oldPasswordTextField.getText() == null || oldPasswordTextField.getText().isEmpty()
				|| oldPasswordTextField.getText().isBlank())
			check = true;

		if (passwordNewField.getText() == null || passwordNewField.getText().isEmpty()
				|| passwordNewField.getText().isBlank()
				|| !passwordNewConfirmField.getText().equals(passwordNewField.getText()))
			check = true;

		if (passwordNewConfirmField.getText() == null || passwordNewConfirmField.getText().isEmpty()
				|| passwordNewConfirmField.getText().isBlank()
				|| !passwordNewConfirmField.getText().equals(passwordNewField.getText()))
			check = true;
		
		okButton.setDisable(check);
	}
}
