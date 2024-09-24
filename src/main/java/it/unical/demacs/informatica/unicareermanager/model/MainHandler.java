package it.unical.demacs.informatica.unicareermanager.model;

import java.util.Objects;

public class MainHandler {
    private User user = null;
    private static MainHandler instance = null;

    private MainHandler() {
    }

    public static MainHandler getInstance() {
        if (instance == null)
            instance = new MainHandler();
        return instance;
    }

    public void login(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        logout();
        this.user = user;
        UserProperties.getInstance().setFirstName(user.firstName());
        UserProperties.getInstance().setLastName(user.lastName());
    }

    public User getUser() {
        return user;
    }

    public void logout() {
        this.user = null;
    }
}
