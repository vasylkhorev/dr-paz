package sk.upjs.drpaz.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import sk.upjs.drpaz.storage.entities.Employee;

public class EditEmployeeController {

	private Employee employee;
	private MFXLegacyTableView<Employee> employees;
	
    @FXML
    private MFXButton cancelButton;
    @FXML
    private MFXTextField emailEmployeeTextField;
    @FXML
    private MFXTextField nameEmployeeTextField;
    @FXML
    private MFXButton okButton;
    @FXML
    private MFXTextField phoneTextField;
    @FXML
    private MFXTextField roleEmployeeTextField;
    @FXML
    private MFXTextField surnameEmployeeTextField;
    
    public EditEmployeeController(Employee employee, TableView<Employee> allEmployeeTableView) {
		
	}
    
    @FXML
    void cancelClickButton(ActionEvent event) {

    }

    @FXML
    void okButtonClick(ActionEvent event) {

    }

    @FXML
    void initialize() {
    }

}
