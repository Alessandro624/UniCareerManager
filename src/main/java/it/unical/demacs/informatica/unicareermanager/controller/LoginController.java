package it.unical.demacs.informatica.unicareermanager.controller;

import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.DBConnection;
import it.unical.demacs.informatica.unicareermanager.model.MainHandler;
import it.unical.demacs.informatica.unicareermanager.model.UniversityDownloaderHandler;
import it.unical.demacs.informatica.unicareermanager.model.User;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.view.AutoCompleteTextField;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;


public class LoginController {

    @FXML
    private Button changeThemeButton;

    @FXML
    private Label errorLoginLabel;

    @FXML
    private Label errorSignupLabel;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private Button hasAccountButton;

    @FXML
    private ImageView imageView;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private Button loginButton;

    @FXML
    private Label loginLabel;

    @FXML
    private ProgressIndicator loginProgressIndicator;

    @FXML
    private VBox loginVBox;

    @FXML
    private VBox logoVbox;

    @FXML
    private TextField matTextFieldLogin;

    @FXML
    private TextField matTextFieldSignup;

    @FXML
    private Button noAccountButton;

    @FXML
    private PasswordField passwordFieldLogin;

    @FXML
    private PasswordField passwordFieldSignup;

    @FXML
    private Button signupButton;

    @FXML
    private Label signupLabel;

    @FXML
    private ProgressIndicator signupProgressIndicator;

    @FXML
    private VBox signupVBox;

    @FXML
    private HBox universityHBoxLogin;

    @FXML
    private HBox universityHBoxSignup;

    private final AutoCompleteTextField universityTextFieldLogin = new AutoCompleteTextField();

    private final AutoCompleteTextField universityTextFieldSignup = new AutoCompleteTextField();

    @FXML
    void handleChangeTheme(ActionEvent event) {
        // handling change theme
        SceneHandler.getInstance().changeTheme();
        setUpImageView();
    }

    private boolean checkPasswordValidity(String password) {
        // checking password validity
        // complete regex ^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$
        // at least 8 characters
        if (password.length() < 8) {
            errorSignupLabel.setText(Message.PASSWORD_TOO_SHORT);
            return false;
        }
        // contains at least an uppercase letter and a lowercase letter
        if (!password.matches(".*[a-z].*") || !password.matches(".*[A-Z].*")) {
            errorSignupLabel.setText(Message.PASSWORD_CHARACTERS_ERROR);
            return false;
        }
        // contains at least a digit
        if (!password.matches(".*\\d.*")) {
            errorSignupLabel.setText(Message.PASSWORD_DIGITS_ERROR);
            return false;
        }
        return true;
    }

    private boolean checkMandatoryFields(List<String> listOfFields) {
        // checking the emptiness of fields
        return listOfFields.stream().anyMatch(String::isBlank);
    }

    private boolean checkNotDigitFields(List<String> listOfFields) {
        // checking the presence of digits on fields
        return listOfFields.stream().anyMatch(field -> field.matches(".*[0-9].*"));
    }

    private void handleFailedTask(boolean isLogin) {
        // handling failed task
        SceneHandler.getInstance().showErrorMessage(isLogin ? Title.LOGIN_ERROR : Title.SIGNUP_ERROR, isLogin ? Message.LOGIN_ERROR : Message.SIGNUP_ERROR, false);
    }

    private void handleChangeScene(String mat, String university, boolean isLogin) {
        // handling change of scene
        Task<User> task = DBConnection.getInstance().getUser(mat, university);
        task.setOnSucceeded(e -> {
            if (task.getValue() != null) {
                MainHandler.getInstance().login(task.getValue());
                SceneHandler.getInstance().showInfoMessage(isLogin ? Title.LOGIN_INFO : Title.SIGNUP_INFO, isLogin ? Message.SUCCESSFUL_LOGIN : Message.SUCCESSFUL_SIGNUP);
                SceneHandler.getInstance().setMainScene();
            } else
                handleFailedTask(isLogin);
        });
        task.setOnFailed(e -> handleFailedTask(isLogin));
        ExecutorServiceManager.getInstance().getExecutorService().submit(task);
    }

    private void handleBinding(boolean isLogin, Task<Boolean> task) {
        // handling binding for task execution
        if (isLogin) {
            loginButton.disableProperty().unbind();
            noAccountButton.disableProperty().unbind();
            loginProgressIndicator.progressProperty().unbind();
            loginProgressIndicator.visibleProperty().unbind();
            loginButton.disableProperty().bind(task.runningProperty());
            noAccountButton.disableProperty().bind(task.runningProperty());
            loginProgressIndicator.progressProperty().bind(task.progressProperty());
            loginProgressIndicator.visibleProperty().bind(task.runningProperty());
        } else {
            signupButton.disableProperty().unbind();
            hasAccountButton.disableProperty().unbind();
            signupProgressIndicator.progressProperty().unbind();
            signupProgressIndicator.visibleProperty().unbind();
            signupButton.disableProperty().bind(task.runningProperty());
            hasAccountButton.disableProperty().bind(task.runningProperty());
            signupProgressIndicator.progressProperty().bind(task.progressProperty());
            signupProgressIndicator.visibleProperty().bind(task.runningProperty());
        }
    }

    @FXML
    void handleLogin(ActionEvent event) {
        // handling login event
        String mat = matTextFieldLogin.getText();
        String university = universityTextFieldLogin.getText();
        String password = passwordFieldLogin.getText();
        if (checkMandatoryFields(List.of(mat, university, password))) {
            errorLoginLabel.setText(Message.ALL_FIELDS_ARE_MANDATORY);
        } else {
            Task<Boolean> task = DBConnection.getInstance().checkPassword(mat, university, password);
            handleBinding(true, task);
            task.setOnSucceeded(e -> {
                if (task.getValue()) {
                    handleChangeScene(mat, university, true);
                } else
                    errorLoginLabel.setText(Message.WRONG_MAT_UNI_PASS);
            });
            task.setOnFailed(e -> {
                // debug
                // task.getException().printStackTrace();
                handleFailedTask(true);
            });
            ExecutorServiceManager.getInstance().getExecutorService().submit(task);
        }
    }

    @FXML
    void handleSignup(ActionEvent event) {
        // handling signup event
        String mat = matTextFieldSignup.getText();
        String university = universityTextFieldSignup.getText();
        String password = passwordFieldSignup.getText();
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        if (checkMandatoryFields(List.of(mat, university, password, firstName, lastName))) {
            errorSignupLabel.setText(Message.ALL_FIELDS_ARE_MANDATORY);
        } else if (checkNotDigitFields(List.of(firstName, lastName, university))) {
            errorSignupLabel.setText(Message.ONLY_LETTERS_SIGNUP_FIELD);
        } else if (checkPasswordValidity(password)) {
            Task<Boolean> task = DBConnection.getInstance().insertUser(mat, university, password, firstName, lastName);
            handleBinding(false, task);
            task.setOnSucceeded(e -> {
                if (task.getValue()) {
                    handleChangeScene(mat, university, false);
                } else
                    errorSignupLabel.setText(Message.ALREADY_USED_MAT_UNI);
            });
            task.setOnFailed(e -> {
                // debug
                // task.getException().printStackTrace();
                handleFailedTask(false);
            });
            ExecutorServiceManager.getInstance().getExecutorService().submit(task);
        }
    }

    @FXML
    void handleTransitionToLogin(ActionEvent event) {
        // handling transition from signup to login
        clearText(true);
        changePane(true);
    }

    @FXML
    void handleTransitionToSignup(ActionEvent event) {
        // handling transition from login to signup
        clearText(false);
        changePane(false);
    }

    private void loadUniversity() {
        // loading universities for auto complete text field
        // loading italian universities
        // Task<List<String>> task = UniversityDownloaderHandler.getInstance().getHipolabsUniversities();
        Task<List<String>> task = UniversityDownloaderHandler.getInstance().getUniversities();
        task.setOnSucceeded(e -> {
            universityTextFieldLogin.setSuggestions(task.getValue());
            universityTextFieldSignup.setSuggestions(task.getValue());
        });
        // debug
        // task.setOnFailed(e -> task.getException().printStackTrace());
        ExecutorServiceManager.getInstance().getExecutorService().submit(task);
    }

    private void changePane(boolean toLogin) {
        // changing pane using a FadeTransition
        VBox pane;
        if (toLogin) {
            pane = loginVBox;
            signupVBox.setVisible(false);
        } else {
            pane = signupVBox;
            loginVBox.setVisible(false);
        }
        FadeTransition ft = new FadeTransition(Duration.seconds(0.7), pane);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        pane.toFront();
        pane.setVisible(true);
        ft.play();
    }

    private void clearText(boolean toLogin) {
        // clearing text from text fields
        if (toLogin) {
            errorSignupLabel.setText("");
            firstNameTextField.clear();
            lastNameTextField.clear();
            matTextFieldSignup.clear();
            universityTextFieldSignup.clear();
            passwordFieldSignup.clear();
        } else {
            errorLoginLabel.setText("");
            matTextFieldLogin.clear();
            universityTextFieldLogin.clear();
            passwordFieldLogin.clear();
        }
    }

    private void setUpAutoCompleteTextField() {
        // setting up auto complete text field
        universityHBoxLogin.getChildren().add(universityTextFieldLogin);
        universityHBoxSignup.getChildren().add(universityTextFieldSignup);
    }

    private void setUpImageView() {
        // setting up image view
        imageView.setImage(SceneHandler.getInstance().getLoginImage());
    }

    private void setUpChangeThemeButton() {
        // setting up change theme button
        changeThemeButton.setGraphic(SceneHandler.getInstance().getIconTheme());
    }

    private void setUpStyle() {
        // setting style classes for labels
        logoVbox.getStyleClass().add("side-pane");
        loginLabel.getStyleClass().add("bold-label");
        signupLabel.getStyleClass().add("bold-label");
        errorLoginLabel.getStyleClass().add("error-label");
        errorSignupLabel.getStyleClass().add("error-label");
    }

    @FXML
    void initialize() {
        setUpStyle();
        setUpChangeThemeButton();
        setUpImageView();
        setUpAutoCompleteTextField();
        clearText(true);
        changePane(true);
        loadUniversity();
    }
}
