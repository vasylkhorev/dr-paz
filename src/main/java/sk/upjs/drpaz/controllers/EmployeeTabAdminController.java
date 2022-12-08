package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import sk.upjs.drpaz.LoggedUser;
import sk.upjs.drpaz.models.EmployeeFxModel;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Employee;

public class EmployeeTabAdminController {

	private Employee currentUser;
	private EmployeeFxModel model = new EmployeeFxModel();	
	private Employee edited;
	private List<String> list = Arrays.asList("Admin","Predaj");
	
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
    	deleteEmployee();
    }

    @FXML
    void editButtonClick(ActionEvent event) {

    }

    @FXML
    void newButtonClick(ActionEvent event) {
    	edited= null;
    	newEmployeeLabel.setText("New Employee");
    	clearFields();
    	employeeEditNameTextField.requestFocus();
    }

    private void clearFields() {
        employeeEditNameTextField.clear();
        employeeEditSurnameTextField.clear();
        employeeEditPhoneTextField.clear();
        employeeEditEmailTextField.clear();

        roleComboBox.clearSelection();
        
        roleComboBox.requestFocus();
        
        allEmployeeTableView.requestFocus();
	}

	@FXML
    void saveButtonClick(ActionEvent event) {
		//TODO
		Employee newEmployee = null;
		if (edited == null) {
			//TODO
		}else {
			newEmployee = new Employee(edited.getName(), edited.getSurname(), edited.getPhone(), edited.getEmail(), edited.getLogin(), edited.getPassword(), edited.getRole());
		}
		newEmployee = DaoFactory.INSTANCE.getEmployeeDao().save(newEmployee);
			
		
		int index = model.getAllEmployeesModel().indexOf(edited);
		if (index == -1) {
			model.getAllEmployeesModel().add(newEmployee);
		} else {
			model.getAllEmployeesModel().set(index, newEmployee);
		}
		
		edited = null;
		newEmployeeLabel.setText("Edit Employee");
		
		clearFields();
    }

    @FXML
    void initialize() {
    	currentUser = LoggedUser.INSTANCE.getLoggedUser();
    	employeeNameTextField.textProperty().bindBidirectional(model.nameProperty());
    	employeeSurnameTextField.textProperty().bindBidirectional(model.surnameProperty());
    	setRoleItem();
    	setAllColumns();
    	correctInputListener();

		employeesListener();
    	
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

	private void employeesListener() {
		allEmployeeTableView.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				editSelectedEmployee();
			}
			if (event.getButton().equals(MouseButton.SECONDARY)) {
				MenuItem editItem = new MenuItem("Edit");
				MenuItem deleteItem = new MenuItem("Delete");
				ContextMenu contextMenu = new ContextMenu(editItem, deleteItem);
				contextMenu.setX(event.getScreenX());
				contextMenu.setY(event.getScreenY());
				contextMenu.show(allEmployeeTableView.getScene().getWindow());
				editItem.setOnAction(e -> {
					editSelectedEmployee();
				});
				deleteItem.setOnAction(event1 -> {
					deleteEmployee();
				});
			}	
		});
		
	}

	private void setAllColumns() {
		idAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("id"));
    	nameAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
    	surnameAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("surname"));
    	phoneAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("phone"));
    	emailAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("email"));
    	roleAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("role"));
	}
	
	private void setRoleItem() {
		ObservableList<String> all = FXCollections.observableArrayList(list);
		
		roleComboBox.setItems(all);
	}
	
	private void checkCorrect() {
		//TODO 
		if (employeeEditNameTextField.getText() == null || employeeEditNameTextField.getText().isEmpty() || employeeEditNameTextField.getText().isBlank()) {
			saveButton.setDisable(true);
			return;
		}
		if (employeeEditSurnameTextField.getText() == null || employeeEditSurnameTextField.getText().isEmpty() || employeeEditSurnameTextField.getText().isBlank()) {
			saveButton.setDisable(true);
			return;
		}
		
		if (roleComboBox.getText() == null || roleComboBox.getText().isEmpty() || roleComboBox.getText().isBlank()) {
			saveButton.setDisable(true);
			return;
		}
	}
	
	private void correctInputListener() {
		employeeEditNameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
		employeeEditSurnameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
		/*
		employeeEditPhoneTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
		employeeEditEmailTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
		*/
	}
	
	private void editSelectedEmployee() {
		cancelButtonClick1(null);
		
		edited = allEmployeeTableView.getSelectionModel().getSelectedItem();
		newEmployeeLabel.setText("Edit Employee");
		
		employeeEditNameTextField.setText(edited.getName());
		employeeEditSurnameTextField.setText(edited.getSurname());
		
		if(edited.getPhone()!=null) {
			employeeEditPhoneTextField.setText(edited.getPhone());
		}else {
			employeeEditPhoneTextField.clear();
		}
		if(edited.getEmail()!=null) {
			employeeEditEmailTextField.setText(edited.getEmail());
		}else {
			employeeEditEmailTextField.clear();
		}
		
		roleComboBox.selectItem(edited.getRole());
		
	}
	
	@FXML
	void cancelButtonClick1(ActionEvent event) {
		roleComboBox.clearSelection();
		roleComboBox.requestFocus();
		allEmployeeTableView.requestFocus();
	}
	
	void deleteEmployee() {
		Employee selected = allEmployeeTableView.getSelectionModel().getSelectedItem();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText("You are going to delete Employee!");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == alert.getButtonTypes().get(1)) {
			return;
		}
		
		DaoFactory.INSTANCE.getEmployeeDao().delete(selected.getId());
		allEmployeeTableView.getItems().remove(selected);
		clearFields();
		newEmployeeLabel.setText("New Employee");
	}
}

