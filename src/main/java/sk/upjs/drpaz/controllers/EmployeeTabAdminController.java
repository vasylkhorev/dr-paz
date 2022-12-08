package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.drpaz.LoggedUser;
import sk.upjs.drpaz.models.EmployeeFxModel;
import sk.upjs.drpaz.storage.entities.Employee;

public class EmployeeTabAdminController {

	private Employee currentUser;
	private EmployeeFxModel model;
    @FXML
    private MFXLegacyTableView<Employee> allEmployeeTableView;
    @FXML
    private MFXTextField employeeEditEmailTextField;
    @FXML
    private MFXTextField employeeEditNameTextField;
    @FXML
    private MFXTextField employeeEditPhoneTextField;
    @FXML
    private MFXTextField employeeEditSurnameTextField;
    @FXML
    private MFXTextField employeeNameTextField;
    @FXML
    private MFXTextField employeeSurnameTextField;
    @FXML
    private TableColumn<Employee, Integer> idAllColumn;
    @FXML
    private TableColumn<Employee, String> nameAllColumn;
    @FXML
    private TableColumn<Employee, String> surnameAllColumn;
    @FXML
    private TableColumn<Employee, String> phoneAllColumn;
    @FXML
    private TableColumn<Employee, String> emailAllColumn;
    @FXML
    private TableColumn<Employee, String> roleAllColumn;
    @FXML
    private Label newEmployeeLabel;
    @FXML
    private MFXFilterComboBox<String> roleComboBox;
    @FXML
    private MFXButton saveButton;

    public EmployeeTabAdminController() {
    	model = new EmployeeFxModel();
    }
    
    public EmployeeTabAdminController(Employee employee) {
    	model = new EmployeeFxModel(employee);
    }

    @FXML
    void deleteButtonClick(ActionEvent event) {

    }

    @FXML
    void editButtonClick(ActionEvent event) {

    }

    @FXML
    void newButtonClick(ActionEvent event) {

    }

    @FXML
    void saveButtonClick(ActionEvent event) {

    }

    @FXML
    void initialize() {
    	currentUser = LoggedUser.INSTANCE.getLoggedUser();
    	employeeNameTextField.textProperty().bindBidirectional(model.nameProperty());
    	employeeSurnameTextField.textProperty().bindBidirectional(model.surnameProperty());
    	setAllColumns();
    	
    	allEmployeeTableView.setItems(model.getAllEmployeesModel());
    	
    	employeeNameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue,
				newValue) -> allEmployeeTableView.setItems((employeeSurnameTextField.getText() == null)
															? model.getAllEmployeesModelByName(newValue)
															: (employeeSurnameTextField.getText().isBlank())
																? model.getAllEmployeesModelByName(newValue)
																: model.getAllEmployeesModelByNameAndSurname(newValue, employeeSurnameTextField.getText())));
    	
    	employeeSurnameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue,
				newValue) -> allEmployeeTableView.setItems((employeeNameTextField.getText() == null)
															? model.getAllEmployeesModelBySurname(newValue)
															: (employeeNameTextField.getText().isBlank())
																? model.getAllEmployeesModelBySurname(newValue)
    															: model.getAllEmployeesModelByNameAndSurname(employeeNameTextField.getText(), newValue)));
    }

	private void setAllColumns() {
		idAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("id"));
    	nameAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
    	surnameAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("surname"));
    	phoneAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("phone"));
    	emailAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("email"));
    	roleAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("role"));
	}

}

