package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.upjs.drpaz.LoggedUser;
import sk.upjs.drpaz.models.EmployeeFxModel;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.dao.EmployeeDao;
import sk.upjs.drpaz.storage.entities.Employee;

public class EmployeeTabAdminController {

	private EmployeeDao employeeDao;
	private Employee currentUser;
	private EmployeeFxModel model = new EmployeeFxModel();	
	private Employee edited;
	private List<String> list = Arrays.asList("Admin","Predaj");
	
	@FXML
    private MFXTextField employeeNameTextField;
    @FXML
    private MFXTextField employeeSurnameTextField;
    
    @FXML
    private MFXTextField employeeEditNameTextField;
    @FXML
    private MFXTextField employeeEditSurnameTextField;
    @FXML
    private MFXTextField employeeEditPhoneTextField;
    @FXML
    private MFXTextField employeeEditEmailTextField;
    @FXML
    private MFXFilterComboBox<String> roleComboBox;
    @FXML
    private MFXTextField employeeEditLoginTextField;
    @FXML
    private MFXPasswordField employeeEditPasswordTextField;
    @FXML
    private MFXPasswordField employeeEditConfirmPasswordTextField;
    
    @FXML
    private MFXLegacyTableView<Employee> allEmployeeTableView;
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
    private TableColumn<Employee, String> loginAllColumn;
    
    @FXML
    private Label newEmployeeLabel;
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
    	newButtonClick(null);
    }

    @FXML
    void editButtonClick(ActionEvent event) {
    	editSelectedEmployee();
    }

    @FXML
    void newButtonClick(ActionEvent event) {
    	edited= null;
    	newEmployeeLabel.setText("New Employee");
    	clearFields();
    	unHideLoginPassword();
    	employeeEditNameTextField.requestFocus();
    }
    
    private void hideLoginPassword() {
    	employeeEditLoginTextField.setVisible(false);
		employeeEditPasswordTextField.setVisible(false);
		employeeEditConfirmPasswordTextField.setVisible(false);
	}
    
    private void unHideLoginPassword() {
		employeeEditLoginTextField.setVisible(true);
		employeeEditPasswordTextField.setVisible(true);
		employeeEditConfirmPasswordTextField.setVisible(true);
	}
	
	private void clearFields() {
        employeeEditNameTextField.clear();
        employeeEditSurnameTextField.clear();
        employeeEditPhoneTextField.clear();
        employeeEditEmailTextField.clear();
        employeeEditLoginTextField.clear();
        employeeEditPasswordTextField.clear();
        employeeEditConfirmPasswordTextField.clear();

        roleComboBox.clearSelection();
        
        roleComboBox.requestFocus();
        
        allEmployeeTableView.requestFocus();
	}

	@FXML
    void saveButtonClick(ActionEvent event) {
		
		Employee newEmployee = null;
		if (edited == null) {
			newEmployee = new Employee(
					employeeEditNameTextField.getText(),
					employeeEditSurnameTextField.getText(),
					employeeEditPhoneTextField.getText(),
					employeeEditEmailTextField.getText(),
					employeeEditLoginTextField.getText(),
					employeeEditPasswordTextField.getText(),
					roleComboBox.getSelectedItem());
		}else {
			newEmployee = new Employee(
					edited.getId(),
					employeeEditNameTextField.getText(),
					employeeEditSurnameTextField.getText(),
					employeeEditPhoneTextField.getText(),
					employeeEditEmailTextField.getText(),
					edited.getLogin(),
					edited.getPassword(),
					edited.getRole());
		}
		newEmployee = DaoFactory.INSTANCE.getEmployeeDao().save(newEmployee);
			
		int index = model.getAllEmployeesModel().indexOf(edited);
		if (index == -1) {
			model.getAllEmployeesModel().add(newEmployee);
		} else {
			model.getAllEmployeesModel().set(index, newEmployee);
		}
		
		newEmployeeLabel.setText("Edit Employee");
		
		clearFields();
		
		edited=null;
    }

    @FXML
    void initialize() {
    	employeeDao = DaoFactory.INSTANCE.getEmployeeDao();
    	currentUser = LoggedUser.INSTANCE.getLoggedUser();
    	saveButton.setDisable(true);
    	employeeNameTextField.textProperty().bindBidirectional(model.nameProperty());
    	employeeSurnameTextField.textProperty().bindBidirectional(model.surnameProperty());
    	setRoleItem();
    	setAllColumns();
    	correctInputListener();

		employeesListener();
    	
    	allEmployeeTableView.setItems(model.getAllEmployeesModel());
    	
    	employeeNameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue,
				newValue) -> employeeSurnameSort(newValue));
    	
    	employeeSurnameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue,
				newValue) -> employeeNameSort(newValue));
    }
    private void employeeNameSort(String newValue) {
    	allEmployeeTableView.setItems((employeeNameTextField.getText() == null)
				? model.getAllEmployeesModelBySurname(newValue)
				: (employeeNameTextField.getText().isBlank())
					? model.getAllEmployeesModelBySurname(newValue)
					: model.getAllEmployeesModelByNameAndSurname(employeeNameTextField.getText(), newValue));
    }
    
    private void employeeSurnameSort(String newValue) {
    	allEmployeeTableView.setItems((employeeSurnameTextField.getText() == null)
				? model.getAllEmployeesModelByName(newValue)
				: (employeeSurnameTextField.getText().isBlank())
					? model.getAllEmployeesModelByName(newValue)
					: model.getAllEmployeesModelByNameAndSurname(newValue, employeeSurnameTextField.getText()));
    }

	private void employeesListener() {
		allEmployeeTableView.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				editSelectedEmployee();
			}
			if (event.getButton().equals(MouseButton.SECONDARY)) {
				MenuItem editItem = new MenuItem("Edit");
				MenuItem deleteItem = new MenuItem("Delete");
				MenuItem changeLogin = new MenuItem("Change Login/Password");
				ContextMenu contextMenu = new ContextMenu(editItem, deleteItem, changeLogin);
				contextMenu.setX(event.getScreenX());
				contextMenu.setY(event.getScreenY());
				contextMenu.show(allEmployeeTableView.getScene().getWindow());
				editItem.setOnAction(e -> {
					editSelectedEmployee();
				});
				deleteItem.setOnAction(event1 -> {
					deleteEmployee();
				});
				changeLogin.setOnAction(e -> {
					changeLogin();
				});
			}	
		});
		
	}

	private void changeLogin() {
		Employee e = allEmployeeTableView.getSelectionModel().getSelectedItem();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../controllers/ChangeLogin.fxml"));
		ChangeLoginController changeLoginController = new ChangeLoginController(e, currentUser, allEmployeeTableView);
		fxmlLoader.setController(changeLoginController);
		Parent parent;
		try {
			parent = fxmlLoader.load();
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Change Login/Password");
			stage.showAndWait();
			refresh();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	private void refresh() {
		model = new EmployeeFxModel();
		saveButton.setDisable(true);
    	setAllColumns();
		allEmployeeTableView.getItems().clear();
		allEmployeeTableView.getItems().addAll(model.getAllEmployeesModel());
	}

	private void setAllColumns() {
		idAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("id"));
    	nameAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
    	surnameAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("surname"));
    	phoneAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("phone"));
    	emailAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("email"));
    	roleAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("role"));
    	loginAllColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("login"));
	}
	
	private void setRoleItem() {
		ObservableList<String> all = FXCollections.observableArrayList(list);
		
		roleComboBox.setItems(all);
	}
	
	private boolean checkLoginInDB(String s) {
		Employee employee = employeeDao.getByLogin(s);
		if (employee == null) {
			return false;
		}
		return true;
	}
	
	private void checkCorrect() {
		//TODO 
		boolean check = true;
		if (employeeEditNameTextField.getText() == null || employeeEditNameTextField.getText().isEmpty() || employeeEditNameTextField.getText().isBlank()) {
			saveButton.setDisable(true);
			employeeEditNameTextField.setStyle("-fx-border-color: red");
			check = false;
		}else {
			employeeEditNameTextField.setStyle("-fx-border-color: none");
		}
		
		if (employeeEditSurnameTextField.getText() == null || employeeEditSurnameTextField.getText().isEmpty() || employeeEditSurnameTextField.getText().isBlank()) {
			saveButton.setDisable(true);
			employeeEditSurnameTextField.setStyle("-fx-border-color: red");
			check = false;;
		}else {
			employeeEditSurnameTextField.setStyle("-fx-border-color: none");
		}
		
		if(edited == null) {
			if (roleComboBox.getSelectedItem() == null || roleComboBox.getSelectedItem().isEmpty() || roleComboBox.getSelectedItem().isBlank()) {
				saveButton.setDisable(true);
				roleComboBox.setStyle("-fx-border-color: red");
				check = false;
			}else {
				roleComboBox.setStyle("-fx-border-color: none");
			}
			
			if (employeeEditLoginTextField.getText() == null || employeeEditLoginTextField.getText().isEmpty() || employeeEditLoginTextField.getText().isBlank() || checkLoginInDB(employeeEditLoginTextField.getText())) {
				employeeEditLoginTextField.setStyle("-fx-border-color: red");
				saveButton.setDisable(true);
				check = false;
			}else {	
				employeeEditLoginTextField.setStyle("-fx-border-color: none");
			}
			
			if (employeeEditPasswordTextField.getText() == null || employeeEditPasswordTextField.getText().isEmpty() || employeeEditPasswordTextField.getText().isBlank()) {
				employeeEditPasswordTextField.setStyle("-fx-border-color: red");
				saveButton.setDisable(true);
				check = false;
			}else {
				employeeEditPasswordTextField.setStyle("-fx-border-color: none");
			}
			
			//check if confirm password is null, empty, blank and not equals with password
			if (employeeEditConfirmPasswordTextField.getText() == null || employeeEditConfirmPasswordTextField.getText().isEmpty() || employeeEditConfirmPasswordTextField.getText().isBlank() || !employeeEditConfirmPasswordTextField.getText().equals(employeeEditPasswordTextField.getText())) {
				employeeEditConfirmPasswordTextField.setStyle("-fx-border-color: red");
				saveButton.setDisable(true);
				check = false;
			}else {
				employeeEditConfirmPasswordTextField.setStyle("-fx-border-color: none");
			}
		}
		
		
		if (check) {
			saveButton.setDisable(false);
		}
	}
	
	private void correctInputListener() {
		employeeEditNameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
		employeeEditSurnameTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
		
		employeeEditLoginTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
		employeeEditPasswordTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
		employeeEditConfirmPasswordTextField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			checkCorrect();
		});
		
		roleComboBox.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if(edited == null)
				checkCorrect();
		});
		
	}
	
	private void editSelectedEmployee() {
		hideLoginPassword();
		edited = allEmployeeTableView.getSelectionModel().getSelectedItem();
		cancelButtonClick1(null);
		clearFields();
		
		if (edited == null) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText("Select employee for editing!");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == alert.getButtonTypes().get(1)) {
				return;
			}
		}
		roleComboBox.selectItem(edited.getRole());
		//? at this point we know that role is not null because of edited 
		roleComboBox.setStyle("-fx-border-color: none");
		saveButton.setDisable(true);
		
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

