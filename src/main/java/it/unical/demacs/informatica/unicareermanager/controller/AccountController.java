package it.unical.demacs.informatica.unicareermanager.controller;

import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.DBConnection;
import it.unical.demacs.informatica.unicareermanager.model.LogoutHandler;
import it.unical.demacs.informatica.unicareermanager.model.MainHandler;
import it.unical.demacs.informatica.unicareermanager.model.User;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AccountController {

    @FXML
    private Label accountLabel;

    @FXML
    private TextField cdsTextField;

    @FXML
    private Button changeImageButton;

    @FXML
    private Button deleteChangeButton;

    @FXML
    private Button deleteImageButton;

    @FXML
    private TextField departmentTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private Button logoutButton;

    @FXML
    private TextField matTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Button saveButton;

    @FXML
    private ImageView studentImage;

    @FXML
    private TextField universityTextField;

    private User user = null;

    private File currentImageFile = null;

    private boolean deleted = false;

    @FXML
    void handleChangeImage(ActionEvent event) {
        // handling image change
        File chosenFile = SceneHandler.getInstance().showFileChooser();
        if (chosenFile != null) {
            try {
                studentImage.setImage(new Image(chosenFile.toURI().toString()));
                currentImageFile = chosenFile;
                deleted = false;
            } catch (Exception e) {
                // debug
                // e.printStackTrace();
                errorLabel.setText(Message.LOAD_IMAGE_ERROR);
            }
        }
    }

    @FXML
    void handleDeleteChange(ActionEvent event) {
        // reset fields
        setUpFields();
    }

    @FXML
    void handleDeleteImage(ActionEvent event) {
        // deleting image
        if (!deleted && SceneHandler.getInstance().showConfirmationMessage(Title.REMOVE_CONFIRMATION, Message.REMOVE_IMAGE_CONFIRMATION)) {
            setUpImage();
            currentImageFile = null;
            deleted = true;
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        // handling logout
        if (SceneHandler.getInstance().showConfirmationMessage(Title.LOGOUT_CONFIRMATION, Message.LOGOUT_CONFIRMATION)) {
            Task<Void> task = LogoutHandler.getInstance().logout();
            handleBinding(task);
            task.setOnSucceeded(e -> {
                SceneHandler.getInstance().setLoginScene();
                SceneHandler.getInstance().showInfoMessage(Title.LOGOUT_INFO, Message.SUCCESSFUL_LOGOUT);
            });
            task.setOnFailed(e -> {
                SceneHandler.getInstance().showErrorMessage(Title.LOGOUT_ERROR, Message.LOGOUT_ERROR, true);
                // debug
                // task.getException().printStackTrace();
            });
            // on void task can be used execute
            ExecutorServiceManager.getInstance().getExecutorService().execute(task);
        }
    }

    private void handleOnSaveError() {
        // handling save errors
        SceneHandler.getInstance().showErrorMessage(Title.ACCOUNT_ERROR, Message.EDIT_USER_ERROR, false);
    }

    private byte[] convertImageToByteArray(File imageFile) {
        // converting image
        try {
            return Files.readAllBytes(imageFile.toPath());
        } catch (IOException e) {
            handleOnSaveError();
        }
        return null;
    }

    private boolean checkNotDigitFields(List<String> listOfFields) {
        // checking the presence of digits on fields
        return listOfFields.stream().anyMatch(field -> field.matches(".*[0-9].*"));
    }

    private boolean checkModified(String firstName, String lastName, byte[] image, String department, String cds) {
        // checking if any of the fields have been modified
        return !firstName.equals(user.firstName()) || !lastName.equals(user.lastName()) || !Arrays.equals(image, user.image()) ||
                !Objects.equals(department, user.department()) || !Objects.equals(cds, user.cds());
    }

    private void handleBinding(Task<?> task) {
        saveButton.disableProperty().unbind();
        deleteChangeButton.disableProperty().unbind();
        logoutButton.disableProperty().unbind();
        changeImageButton.disableProperty().unbind();
        deleteImageButton.disableProperty().unbind();
        progressIndicator.progressProperty().unbind();
        progressIndicator.visibleProperty().unbind();
        saveButton.disableProperty().bind(task.runningProperty());
        deleteChangeButton.disableProperty().bind(task.runningProperty());
        logoutButton.disableProperty().bind(task.runningProperty());
        changeImageButton.disableProperty().bind(task.runningProperty());
        deleteImageButton.disableProperty().bind(task.runningProperty());
        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
    }

    @FXML
    void handleSave(ActionEvent event) {
        // handling saving
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String department = departmentTextField.getText();
        String cds = cdsTextField.getText();
        if (firstName.isBlank() || lastName.isBlank()) {
            errorLabel.setText(Message.EDIT_USER_NOT_EMPTY_FIELD);
        } else if (checkNotDigitFields(List.of(firstName, lastName, department != null ? department : "", cds != null ? cds : ""))) {
            errorLabel.setText(Message.ONLY_LETTERS_EDIT_USER_FIELD);
        } else {
            byte[] image = user.image();
            if (currentImageFile != null)
                image = convertImageToByteArray(currentImageFile);
            else if (deleted)
                image = null;
            if (checkModified(firstName, lastName, image, department, cds)) {
                if (SceneHandler.getInstance().showConfirmationMessage(Title.EDIT_CONFIRMATION, Message.EDIT_USER_CONFIRMATION)) {
                    Task<User> task = DBConnection.getInstance().updateUser(firstName, lastName, image, department, cds, user);
                    handleBinding(task);
                    task.setOnSucceeded(e -> {
                        User newUser = task.getValue();
                        if (newUser != null) {
                            try {
                                MainHandler.getInstance().login(newUser);
                                user = newUser;
                                setUpFields();
                                SceneHandler.getInstance().showInfoMessage(Title.EDIT_INFO, Message.EDITED_USER);
                            } catch (Exception exception) {
                                handleOnSaveError();
                            }
                        } else {
                            handleOnSaveError();
                        }
                    });
                    task.setOnFailed(e -> {
                        // debug
                        // task.getException().printStackTrace();
                        handleOnSaveError();
                    });
                    ExecutorServiceManager.getInstance().getExecutorService().submit(task);
                }
            } else {
                errorLabel.setText(Message.NOT_EDITED);
            }
        }
    }

    private void setUpImage() {
        // setting up image
        studentImage.setImage(SceneHandler.getInstance().getDefaultProfileImage());
    }

    private void setUpFields() {
        // setting up fields
        errorLabel.setText("");
        firstNameTextField.setText(user.firstName());
        lastNameTextField.setText(user.lastName());
        matTextField.setText(user.mat());
        universityTextField.setText(user.university());
        departmentTextField.setText(user.department());
        cdsTextField.setText(user.cds());
        currentImageFile = null;
        deleted = false;
        if (user.image() != null)
            studentImage.setImage(new Image(new ByteArrayInputStream(user.image())));
        else
            setUpImage();
    }

    private void initUser() {
        // initializing user
        user = MainHandler.getInstance().getUser();
    }

    private void setUpStyle() {
        // setting up labels
        accountLabel.getStyleClass().add("bold-label");
        errorLabel.getStyleClass().add("error-label");
    }

    @FXML
    void initialize() {
        setUpStyle();
        initUser();
        setUpFields();
    }
}
