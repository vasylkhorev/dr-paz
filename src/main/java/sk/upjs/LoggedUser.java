package sk.upjs;

import sk.upjs.drpaz.storage.entities.Employee;

public enum LoggedUser {
    INSTANCE;


    private Employee loggedUser;


    public Employee getLoggedUser() {
        return loggedUser;
    }


    public void setLoggedUser(Employee loggedUser) {
        this.loggedUser = loggedUser;
    }
}