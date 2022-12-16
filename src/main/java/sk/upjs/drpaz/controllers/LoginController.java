package sk.upjs.drpaz.controllers;

import java.io.IOException;

import org.springframework.security.crypto.bcrypt.BCrypt;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import sk.upjs.drpaz.LoggedUser;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Employee;

public class LoginController {
	public static Stage stage = new Stage();
	private Employee currentUser;
	@FXML
	private Label wrongCredentialsLabel;
	@FXML
	private MFXButton loginButton;
	@FXML
	private MFXTextField loginTextField;
	@FXML
	private MFXPasswordField passwordField;
	@FXML
	void onLoginButtonClick(ActionEvent event) throws Exception {
		currentUser = DaoFactory.INSTANCE.getEmployeeDao().getByLogin(loginTextField.getText());
		if (currentUser == null || !BCrypt.checkpw(passwordField.getText(), currentUser.getPassword())) {
			passwordField.setStyle("-fx-border-color: red");
			wrongCredentialsLabel.setVisible(true);
		} else {
			if (currentUser.getRole().equals("Admin")) {
				passwordField.getScene().getWindow().hide();
				showEditSubject("MainTabPaneAdmin.fxml");
				
			}else if (currentUser.getRole().equals("Predaj")) {
				passwordField.getScene().getWindow().hide();
				showEditSubject("MainTabPaneNoAdmin.fxml");
			}
		}
	}
	@FXML
	void onKeyPressed(KeyEvent event) throws Exception {
		if (event.getCode().equals(KeyCode.ENTER)) {
			onLoginButtonClick(null);
		}
	}
	@FXML
	void onActionTextField(ActionEvent event) {
		passwordField.setStyle("-fx-border-color: black");
		wrongCredentialsLabel.setVisible(false);
	}
	void inputChangedTextField(TextField textField) {
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				passwordField.setStyle("-fx-border-style: none;");
				wrongCredentialsLabel.setVisible(false);
			}
		});
	}
	@FXML
	void initialize() {
		inputChangedTextField(passwordField);
		inputChangedTextField(loginTextField);
	}

	void showEditSubject(String filepath) {
		try {
			LoggedUser.INSTANCE.setLoggedUser(currentUser);
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(filepath));
			Parent parent = fxmlLoader.load();
			Scene scene = new Scene(parent);
			stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Dr. PAZ");
			stage.getIcons().add(new Image("icons/icon.png"));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}