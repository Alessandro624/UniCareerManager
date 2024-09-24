package it.unical.demacs.informatica.unicareermanager.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserProperties {
    private static UserProperties instance;

    private StringProperty firstName;
    private StringProperty lastName;

    private UserProperties() {
    }

    public static UserProperties getInstance() {
        if (instance == null) {
            instance = new UserProperties();
        }
        return instance;
    }

    public final StringProperty firstNameProperty() {
        if (firstName == null)
            firstName = new SimpleStringProperty("");
        return firstName;
    }

    public final StringProperty lastNameProperty() {
        if (lastName == null)
            lastName = new SimpleStringProperty("");
        return lastName;
    }

    public String getFirstName() {
        return firstNameProperty().get();
    }

    public void setFirstName(String firstName) {
        firstNameProperty().set(firstName);
    }

    public String getLastName() {
        return lastNameProperty().get();
    }

    public void setLastName(String lastName) {
        lastNameProperty().set(lastName);
    }
}
