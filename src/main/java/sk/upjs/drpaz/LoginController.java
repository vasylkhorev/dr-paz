package sk.upjs.drpaz;

import org.springframework.security.crypto.bcrypt.BCrypt;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sk.upjs.drpaz.storage.DaoFactory;
import sk.upjs.drpaz.storage.Employee;

public class LoginController {
	private Employee currentUser;
	@FXML
	private TextField loginTextField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Label wrongCredentialsLabel;
	@FXML
	private Button loginButton;


	@FXML
	void onLoginButtonClick(ActionEvent event) {
		// TODO ??????
		currentUser = DaoFactory.INSTANCE.getEmployeeDao().getByLogin(loginTextField.getText());
		if (currentUser == null || !BCrypt.checkpw(passwordField.getText(), currentUser.getPassword())) {
			passwordField.setStyle("-fx-border-color: red");
			wrongCredentialsLabel.setVisible(true);

		} else {
			System.out.println("GOOD");
		}
	}

	@FXML
	void onKeyPressed(KeyEvent event) {
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

}
