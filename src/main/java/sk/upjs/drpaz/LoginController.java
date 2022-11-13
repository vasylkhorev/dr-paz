package sk.upjs.drpaz;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import sk.upjs.drpaz.storage.DaoFactory;
import sk.upjs.drpaz.storage.Employee;

public class LoginController {
	private Stage stage;

	@FXML
	private TextField loginTextField;

	@FXML
	private TextField passwordTextField;

	@FXML
	private Label wrongCredentialsLabel;

	public LoginController(Stage stage) {
		this.stage = stage;
	}

	@FXML
	void onKeyPressed(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			loginButtonClick(null);
		}
		if (event.getCode().equals(KeyCode.TAB)) {
			if (stage.getScene().getFocusOwner() == loginTextField)
				passwordTextField.requestFocus();
			else
				loginTextField.requestFocus();
		}
	}

	@FXML
	void ActionTextField(ActionEvent event) {
		passwordTextField.setStyle("-fx-border-color: black");
		wrongCredentialsLabel.setVisible(false);
	}

	@FXML
	void loginButtonClick(ActionEvent event) {
		// TODO ??????
		Employee currentUser = DaoFactory.INSTANCE.getEmployeeDao().getByLoginAndPassword(loginTextField.getText(),
				passwordTextField.getText());
		if (currentUser == null) {
			passwordTextField.setStyle("-fx-border-color: red");
			wrongCredentialsLabel.setVisible(true);

		} else {
			System.out.println("GOOD");
		}
	}

	void inputChangedTextField(TextField textField) {
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				passwordTextField.setStyle("-fx-border-color: black");
				wrongCredentialsLabel.setVisible(false);
			}
		});
	}

	@FXML
	void initialize() {
		inputChangedTextField(passwordTextField);
		inputChangedTextField(loginTextField);
	}

}
