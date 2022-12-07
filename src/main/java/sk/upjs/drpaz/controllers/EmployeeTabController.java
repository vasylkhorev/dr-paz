package sk.upjs.drpaz.controllers;


import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.drpaz.LoggedUser;
import sk.upjs.drpaz.models.EmployeeFxModel;
import sk.upjs.drpaz.storage.entities.Employee;

public class EmployeeTabController {

	private EmployeeFxModel model;
	
	private Employee currentUser;
	
    @FXML
    private TableView<Employee> allEmployeeTableView;
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
    

    public EmployeeTabController() {
    	model = new EmployeeFxModel();
    }
    
    public EmployeeTabController(Employee employee) {
    	model = new EmployeeFxModel(employee);
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
    
    void setAllColumns() {
    	idAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("id"));
    	nameAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
    	surnameAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("surname"));
    	phoneAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("phone"));
    	emailAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("email"));
    	
    }

}
