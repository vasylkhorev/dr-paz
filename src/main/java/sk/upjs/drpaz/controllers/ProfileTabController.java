package sk.upjs.drpaz.controllers;

import java.io.IOException;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.upjs.drpaz.LoggedUser;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Employee;

public class ProfileTabController {

	private Employee currentUser = LoggedUser.INSTANCE.getLoggedUser();

	@FXML
	private MFXTextField editEmailTextField;

	@FXML
	private MFXTextField editNameTextField;

	@FXML
	private MFXTextField editPhoneTextField;

	@FXML
	private MFXTextField editSurnameTextField;

	@FXML
	private MFXTextField editRoleTextField;
	@FXML
	private MFXButton saveButton;

	@FXML
	void changePassword(ActionEvent event) {
		ChangeLoginController1 controller1 = new ChangeLoginController1();
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../controllers/ChangeLogin1.fxml"));
			fxmlLoader.setController(controller1);

			Stage stage = new Stage();
			Parent parent = fxmlLoader.load();
			Scene scene = new Scene(parent);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(scene);
			stage.setTitle("Change password");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void logoutClick(ActionEvent event) {
		editEmailTextField.getScene().getWindow().hide();

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../Login.fxml"));
			LoginController loginController = new LoginController();
			fxmlLoader.setController(loginController);

			Stage stage = new Stage();
			Parent parent = fxmlLoader.load();
			Scene scene = new Scene(parent);
			stage.setMinWidth(380);
			stage.setMinHeight(300);
			stage.setScene(scene);
			stage.setTitle("Login");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void saveClick(ActionEvent event) {
		Employee newEmployee = new Employee(currentUser.getId(), editNameTextField.getText(), editSurnameTextField.getText(),
				editPhoneTextField.getText(), editEmailTextField.getText(), currentUser.getLogin(), currentUser.getPassword(),
				currentUser.getRole());
		newEmployee = DaoFactory.INSTANCE.getEmployeeDao().save(newEmployee);
		System.out.println(newEmployee);
	}

	@FXML
	void initialize() {

		editNameTextField.setText(currentUser.getName());
		editSurnameTextField.setText(currentUser.getSurname());
		editPhoneTextField.setText(currentUser.getPhone());
		editEmailTextField.setText(currentUser.getEmail());
		editRoleTextField.setText(currentUser.getRole());
	}

	private void correctInputListener() {
		editNameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			setSaveButtonOption();
			if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
				editNameTextField.setStyle("-fx-border-color: red");
			} else {
				editNameTextField.setStyle("-fx-border-style: none");
			}
		});
		editSurnameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			setSaveButtonOption();
			if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
				editSurnameTextField.setStyle("-fx-border-color: red");
			} else {
				editSurnameTextField.setStyle("-fx-border-style: none");
			}
		});

	}

	void setSaveButtonOption() {
		if (editNameTextField.getText() == null || editNameTextField.getText().isEmpty()
				|| editNameTextField.getText().isBlank()) {
			saveButton.setDisable(true);
			return;
		}
		if (editSurnameTextField.getText() == null || editSurnameTextField.getText().isEmpty()
				|| editSurnameTextField.getText().isBlank()) {
			saveButton.setDisable(true);
			return;
		}

		saveButton.setDisable(false);
	}
}
