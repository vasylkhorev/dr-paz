package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sk.upjs.drpaz.LoggedUser;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Employee;

public class ChangePasswordController{
	
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
		 
		boolean check = DaoFactory.INSTANCE.getEmployeeDao().changePassword(currentUser.getLogin(), oldPasswordTextField.getText(), currentUser.getLogin(), passwordNewField.getText());
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
		
		passwordNewConfirmField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
		
		passwordNewField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
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
				|| passwordNewField.getText().isBlank()
				|| !passwordNewConfirmField.getText().equals(passwordNewField.getText())) {
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
