package sk.upjs.drpaz;

import javafx.scene.control.Label;
import sk.upjs.drpaz.storage.entities.Employee;

public enum LoggedUser {
    INSTANCE;


    private Employee loggedUser;
	private Label nameLabel;

    public Employee getLoggedUser() {
        return loggedUser;
    }


    public void setLoggedUser(Employee loggedUser) {
        this.loggedUser = loggedUser;
    }


	public Label getNameLabel() {
		return nameLabel;
	}


	public void setNameLabel(Label nameLabel) {
		this.nameLabel = nameLabel;
	}
}