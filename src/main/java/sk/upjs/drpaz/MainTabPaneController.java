package sk.upjs.drpaz;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class MainTabPaneController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Tab sellingTab;
    
    @FXML
    private SellingTabController sellingTabController;
    
    @FXML
    private Tab addingTab;
    
    @FXML
    private AddingTabController addingTabController;
    
    @FXML
    private Tab employeeTab;
    
    @FXML
    private EmployeeTabController employeeTabController;
    
    @FXML
    void initialize(URL location, ResourceBundle resources) {
        
    }

}
