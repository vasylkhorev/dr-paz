package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.dao.EmployeeDao;
import sk.upjs.drpaz.storage.entities.Employee;

public class ChangeLoginController {

	private EmployeeDao employeeDao;
	private Employee employeeToEdit;
	//For future update :)
	private Employee currentUser;
	
    @FXML
    private Label nameLabel;
    @FXML
    private MFXTextField newLoginTextField;
    @FXML
    private MFXPasswordField passwordNewConfirmField;
    @FXML
    private MFXPasswordField passwordNewField;
    @FXML
    private MFXButton okButton;
    @FXML
    private MFXButton cancelButton;

    public ChangeLoginController(Employee employeeToEdit, Employee currentUser, MFXLegacyTableView<Employee> employees) {
    	 this.employeeToEdit = employeeToEdit;
    	 //this.currentUser = currentUser;
    	 this.employeeDao = DaoFactory.INSTANCE.getEmployeeDao();
    	 
    }
    
    @FXML
    void cancelClickButton(ActionEvent event) {
    	nameLabel.getScene().getWindow().hide();
    }

    @FXML
    void okButtonClick(ActionEvent event) {
    	boolean check = employeeDao.changePassword(employeeToEdit.getLogin(), employeeToEdit.getPassword(), newLoginTextField.getText(), passwordNewField.getText());
    	nameLabel.getScene().getWindow().hide();
    }

    @FXML
    void initialize() {
    	okButton.setDisable(true);
		nameLabel.setText(employeeToEdit.getName() + " " + employeeToEdit.getSurname());
		newLoginTextField.setText(employeeToEdit.getLogin());
		correctInputListeners();
		
    }
    
    private void correctInputListeners() {
    	newLoginTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
    	passwordNewField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
    	passwordNewConfirmField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
    }
    
    private void checkCorrect() {
    	boolean check = true;
		if (newLoginTextField.getText() == null || newLoginTextField.getText().isEmpty() || newLoginTextField.getText().isBlank() || checkLoginInDB(newLoginTextField.getText())) {
			okButton.setDisable(true);
			newLoginTextField.setStyle("-fx-border-color: red");
			check = false;
		}else {
			newLoginTextField.setStyle("-fx-border-style: none");
		}
		
		if (passwordNewField.getText() == null || passwordNewField.getText().isEmpty() || passwordNewField.getText().isBlank()) {
			okButton.setDisable(true);
			passwordNewField.setStyle("-fx-border-color: red");
			check = false;
		}else {
			passwordNewField.setStyle("-fx-border-style: none");
		}
		
		if (passwordNewConfirmField.getText() == null || passwordNewConfirmField.getText().isEmpty() || passwordNewConfirmField.getText().isBlank() || !passwordNewConfirmField.getText().equals(passwordNewField.getText())) {
			okButton.setDisable(true);
			passwordNewConfirmField.setStyle("-fx-border-color: red");
			check = false;
		}else {
			passwordNewConfirmField.setStyle("-fx-border-style: none");
		}
		
		if (check) {
			okButton.setDisable(false);
		}
    }

	private boolean checkLoginInDB(String text) {
		Employee employee = employeeDao.getByLogin(text);
		if (employeeToEdit.getLogin().equals(text))
			return false;
		if (employee == null) {
			return false;
		}
		return true;
	}
}