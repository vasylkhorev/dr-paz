package sk.upjs.drpaz;


import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.drpaz.storage.entities.Employee;

public class EmployeeTabController {

	private EmployeeFxModel model;
	
    @FXML
    private TableView<Employee> allEmployeeTableView;

    @FXML
    private TextField employeeNameTextField;

    @FXML
    private TextField employeeSurnameTextField;

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
    	employeeNameTextField.textProperty().bindBidirectional(model.nameProperty());
    	employeeSurnameTextField.textProperty().bindBidirectional(model.surnameProperty());
    	setAllColumns();
    	
    	allEmployeeTableView.setItems(model.getAllEmployeesModel());
    	
    	employeeNameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue,
				newValue) -> allEmployeeTableView.setItems(model.getAllEmployeesModelByName(newValue)));
    	
    	employeeSurnameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue,
				newValue) -> allEmployeeTableView.setItems(model.getAllEmployeesModelBySurname(newValue)));
    	
    	//TODO my peanut brain need to finish this for when name or surname is empty
    	
    }
    
    void setAllColumns() {
    	nameAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
    	surnameAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("surname"));
    	phoneAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("phone"));
    	emailAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("email"));
    	
    }

}
