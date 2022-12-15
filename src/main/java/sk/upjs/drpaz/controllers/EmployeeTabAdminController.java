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
import javafx.scene.control.SplitPane;
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
	private List<String> list = Arrays.asList("Admin", "Predaj");

	@FXML
    private SplitPane splitPane;
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
		edited = null;
		newEmployeeLabel.setText("New employee");
		clearFields();
		unHideLoginPassword();
		setDefaultBorders();
	}

	private void setDefaultBorders() {
		employeeEditNameTextField.requestFocus();
		employeeEditNameTextField.setStyle("-fx-border-style: none");
		employeeEditSurnameTextField.setStyle("-fx-border-style: none");
		employeeEditLoginTextField.setStyle("-fx-border-style: none");
		employeeEditPasswordTextField.setStyle("-fx-border-style: none");
		employeeEditConfirmPasswordTextField.setStyle("-fx-border-style: none");
		roleComboBox.setStyle("-fx-border-style: none");
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

		employeeEditNameTextField.requestFocus();
		employeeEditSurnameTextField.requestFocus();
		employeeEditPhoneTextField.requestFocus();
		employeeEditEmailTextField.requestFocus();
		employeeEditLoginTextField.requestFocus();
		employeeEditPasswordTextField.requestFocus();
		employeeEditConfirmPasswordTextField.requestFocus();
		roleComboBox.requestFocus();

		allEmployeeTableView.requestFocus();
	}

	@FXML
	void saveButtonClick(ActionEvent event) {

		Employee newEmployee = null;
		if (edited == null) {
			newEmployee = new Employee(employeeEditNameTextField.getText(), employeeEditSurnameTextField.getText(),
					employeeEditPhoneTextField.getText(), employeeEditEmailTextField.getText(),
					employeeEditLoginTextField.getText(), employeeEditPasswordTextField.getText(),
					roleComboBox.getSelectedItem());
		} else {
			newEmployee = new Employee(edited.getId(), employeeEditNameTextField.getText(),
					employeeEditSurnameTextField.getText(), employeeEditPhoneTextField.getText(),
					employeeEditEmailTextField.getText(), edited.getLogin(), edited.getPassword(),
					roleComboBox.getSelectedItem());
		}
		newEmployee = DaoFactory.INSTANCE.getEmployeeDao().save(newEmployee);

		int index = model.getAllEmployeesModel().indexOf(edited);
		if (index == -1) {
			model.getAllEmployeesModel().add(newEmployee);
		} else {
			model.getAllEmployeesModel().set(index, newEmployee);
		}

		if (newEmployee.equals(LoggedUser.INSTANCE.getLoggedUser())) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("You must relogin into your account to be changes saved");
			alert.showAndWait();
			employeeEditEmailTextField.getScene().getWindow().hide();

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

		newEmployeeLabel.setText("Edit employee");

		clearFields();
		setDefaultBorders();

		edited = null;
	}

	@FXML
	void initialize() {
		splitPane.widthProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
			setWidth();
		});
		
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

		employeeNameTextField.textProperty().addListener(
				(ChangeListener<String>) (observable, oldValue, newValue) -> employeeSurnameSort(newValue));

		employeeSurnameTextField.textProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> employeeNameSort(newValue));
	}

	private void employeeNameSort(String newValue) {
		allEmployeeTableView.setItems((employeeNameTextField.getText() == null)
				? model.getAllEmployeesModelBySurname(newValue)
				: (employeeNameTextField.getText().isBlank()) ? model.getAllEmployeesModelBySurname(newValue)
						: model.getAllEmployeesModelByNameAndSurname(employeeNameTextField.getText(), newValue));
	}

	private void employeeSurnameSort(String newValue) {
		allEmployeeTableView.setItems((employeeSurnameTextField.getText() == null)
				? model.getAllEmployeesModelByName(newValue)
				: (employeeSurnameTextField.getText().isBlank()) ? model.getAllEmployeesModelByName(newValue)
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
			stage.setTitle("Change login or password");
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
		setWidth();
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

	private void correctInputListener() {
		employeeEditNameTextField.textProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					setSaveButtonOption();
					if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
						employeeEditNameTextField.setStyle("-fx-border-color: red");
					} else {
						employeeEditNameTextField.setStyle("-fx-border-style: none");
					}
				});
		employeeEditSurnameTextField.textProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					setSaveButtonOption();
					if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
						employeeEditSurnameTextField.setStyle("-fx-border-color: red");
					} else {
						employeeEditSurnameTextField.setStyle("-fx-border-style: none");
					}
				});
		employeeEditLoginTextField.textProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					setSaveButtonOption();
					if (newValue == null || newValue.isEmpty() || newValue.isBlank() || checkLoginInDB(newValue)) {
						employeeEditLoginTextField.setStyle("-fx-border-color: red");
					} else {
						employeeEditLoginTextField.setStyle("-fx-border-style: none");
					}
				});
		employeeEditPasswordTextField.textProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					setSaveButtonOption();
					if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
						employeeEditPasswordTextField.setStyle("-fx-border-color: red");
					} else {
						employeeEditPasswordTextField.setStyle("-fx-border-style: none");
					}
				});
		employeeEditConfirmPasswordTextField.textProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					setSaveButtonOption();
					if (newValue == null || newValue.isEmpty() || newValue.isBlank()
							|| !newValue.equals(employeeEditPasswordTextField.getText())) {
						employeeEditPasswordTextField.setStyle("-fx-border-color: red");
						employeeEditConfirmPasswordTextField.setStyle("-fx-border-color: red");
					} else {
						employeeEditConfirmPasswordTextField.setStyle("-fx-border-style: none");
						employeeEditPasswordTextField.setStyle("-fx-border-style: none");
					}
				});

		roleComboBox.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			setSaveButtonOption();
			if (roleComboBox.getSelectedItem() == null || roleComboBox.getSelectedItem().isEmpty()
					|| roleComboBox.getSelectedItem().isBlank()) {
				roleComboBox.setStyle("-fx-border-color: red");
			} else {
				roleComboBox.setStyle("-fx-border-style: none");
			}
		});
	}

	void setSaveButtonOption() {
		if (employeeEditNameTextField.getText() == null || employeeEditNameTextField.getText().isEmpty()
				|| employeeEditNameTextField.getText().isBlank()) {
			saveButton.setDisable(true);
			return;
		}
		if (employeeEditSurnameTextField.getText() == null || employeeEditSurnameTextField.getText().isEmpty()
				|| employeeEditSurnameTextField.getText().isBlank()) {
			saveButton.setDisable(true);
			return;
		}
		if (employeeEditLoginTextField.isVisible())
			if (employeeEditLoginTextField.getText() == null || employeeEditLoginTextField.getText().isEmpty()
					|| employeeEditLoginTextField.getText().isBlank()
					|| checkLoginInDB(employeeEditLoginTextField.getText())) {
				saveButton.setDisable(true);
				return;
			}
		if (employeeEditPasswordTextField.isVisible())
			if (employeeEditPasswordTextField.getText() == null || employeeEditPasswordTextField.getText().isEmpty()
					|| employeeEditPasswordTextField.getText().isBlank()) {
				saveButton.setDisable(true);
				return;
			}
		if (employeeEditConfirmPasswordTextField.isVisible())
			if (employeeEditConfirmPasswordTextField.getText() == null
					|| employeeEditConfirmPasswordTextField.getText().isEmpty()
					|| employeeEditConfirmPasswordTextField.getText().isBlank() || !employeeEditConfirmPasswordTextField
							.getText().equals(employeeEditPasswordTextField.getText())) {
				saveButton.setDisable(true);
				return;
			}

		if (roleComboBox.getSelectedItem() == null || roleComboBox.getSelectedItem().isEmpty()
				|| roleComboBox.getSelectedItem().isBlank()) {
			saveButton.setDisable(true);
			return;
		}
		saveButton.setDisable(false);
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
		newEmployeeLabel.setText("Edit employee");
		roleComboBox.selectItem(edited.getRole());
		employeeEditNameTextField.setText(edited.getName());
		employeeEditSurnameTextField.setText(edited.getSurname());

		if (edited.getPhone() != null) {
			employeeEditPhoneTextField.setText(edited.getPhone());
		} else {
			employeeEditPhoneTextField.clear();
		}
		if (edited.getEmail() != null) {
			employeeEditEmailTextField.setText(edited.getEmail());
		} else {
			employeeEditEmailTextField.clear();
		}
		setSaveButtonOption();
	}

	@FXML
	void cancelButtonClick1(ActionEvent event) {
		roleComboBox.clearSelection();
		roleComboBox.requestFocus();
		allEmployeeTableView.requestFocus();
	}

	void deleteEmployee() {
		Employee selected = allEmployeeTableView.getSelectionModel().getSelectedItem();

		if (currentUser.equals(selected)) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("You can not delete yourself!");
			alert.showAndWait();
			return;
		}

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText("You are going to delete employee!");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == alert.getButtonTypes().get(1)) {
			return;
		}

		DaoFactory.INSTANCE.getEmployeeDao().delete(selected.getId());
		allEmployeeTableView.getItems().remove(selected);
		newButtonClick(null);
	}
	
	private void setWidth() {
		idAllColumn.prefWidthProperty().bind(allEmployeeTableView.widthProperty().multiply(0.10));
		nameAllColumn.prefWidthProperty().bind(allEmployeeTableView.widthProperty().multiply(0.15));
		surnameAllColumn.prefWidthProperty().bind(allEmployeeTableView.widthProperty().multiply(0.15));
		phoneAllColumn.prefWidthProperty().bind(allEmployeeTableView.widthProperty().multiply(0.15));
		emailAllColumn.prefWidthProperty().bind(allEmployeeTableView.widthProperty().multiply(0.20));
		roleAllColumn.prefWidthProperty().bind(allEmployeeTableView.widthProperty().multiply(0.10));
		loginAllColumn.prefWidthProperty().bind(allEmployeeTableView.widthProperty().multiply(0.151));
	}
}
