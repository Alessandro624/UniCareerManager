package it.unical.demacs.informatica.unicareermanager.controller;

import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Settings;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.*;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.view.CurrencyHandlerView;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.Locale;
import java.util.Objects;

public class SettingsController {

    @FXML
    private ComboBox<String> currencyComboBox;

    @FXML
    private Button deleteChangeButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Spinner<Integer> gradeValueSpinner;

    @FXML
    private TextField maxCreditsTextField;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Button restoreDefaultButton;

    @FXML
    private Button saveChangeButton;

    @FXML
    private Label settingsLabel;

    @FXML
    private ChoiceBox<ThemeModel> themeChoiceBox;

    private StudentSettings actualSettings = new StudentSettings(Settings.DEFAULT_MAX_CREDITS, Settings.DEFAULT_GRADE_VALUE, Settings.DEFAULT_CURRENCY, Settings.DEFAULT_THEME);

    private void handleBinding(Task<?> task) {
        // handling bindings for task completion
        saveChangeButton.disableProperty().unbind();
        deleteChangeButton.disableProperty().unbind();
        restoreDefaultButton.disableProperty().unbind();
        progressIndicator.progressProperty().unbind();
        progressIndicator.visibleProperty().unbind();
        saveChangeButton.disableProperty().bind(task.runningProperty());
        deleteChangeButton.disableProperty().bind(task.runningProperty());
        restoreDefaultButton.disableProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
        progressIndicator.visibleProperty().bind(task.runningProperty());
    }

    private void setValues() {
        // set values of fields
        errorLabel.setText("");
        maxCreditsTextField.setText(String.format(Locale.US, "%.2f", actualSettings.maxCredits()));
        gradeValueSpinner.getValueFactory().setValue(actualSettings.gradeValue());
        CurrencyHandlerView.getInstance().getCurrencies().stream()
                .filter(currency -> currency.split(" +")[1].equals(actualSettings.currency()))
                .findFirst().ifPresent(currencyComboBox::setValue);
        themeChoiceBox.setValue(actualSettings.theme());
    }

    private void setStudentSettingsValues(StudentSettings settings) {
        // settings new values for Student Settings properties
        StudentSettingsProperties.getInstance().setMaxCredits(settings.maxCredits());
        StudentSettingsProperties.getInstance().setGradeValue(settings.gradeValue());
        StudentSettingsProperties.getInstance().setCurrency(settings.currency());
        StudentSettingsProperties.getInstance().setTheme(settings.theme());
        SceneHandler.getInstance().changeThemeByRequest(settings.theme());
    }

    @FXML
    void handleDelete(ActionEvent event) {
        // reset fields
        setValues();
    }

    private boolean modifiedInput(Double maxCredits, Integer gradeValue, String currency, ThemeModel theme) {
        // checking if at least one field has been modified
        if (Objects.equals(maxCredits, actualSettings.maxCredits()) && Objects.equals(gradeValue, actualSettings.gradeValue()) && currency.equals(actualSettings.currency()) && theme == actualSettings.theme()) {
            errorLabel.setText(Message.NOT_EDITED);
            return false;
        }
        return true;
    }

    @FXML
    void handleRestoreDefault(ActionEvent event) {
        // handle restoring request
        if (modifiedInput(Settings.DEFAULT_MAX_CREDITS, Settings.DEFAULT_GRADE_VALUE, Settings.DEFAULT_CURRENCY, Settings.DEFAULT_THEME))
            if (SceneHandler.getInstance().showConfirmationMessage(Title.RESTORE_CONFIRMATION, Message.RESTORE_SETTINGS_CONFIRMATION)) {
                Task<StudentSettings> task = DBConnection.getInstance().updateSettings(Settings.DEFAULT_MAX_CREDITS, Settings.DEFAULT_GRADE_VALUE, Settings.DEFAULT_CURRENCY, Settings.DEFAULT_THEME, MainHandler.getInstance().getUser());
                handleBinding(task);
                task.setOnSucceeded(e -> {
                    if (task.getValue() != null) {
                        StudentSettings settings = task.getValue();
                        setStudentSettingsValues(settings);
                        actualSettings = settings;
                        setValues();
                        SceneHandler.getInstance().showInfoMessage(Title.RESTORE_INFO, Message.RESTORED_SETTINGS);
                    } else {
                        SceneHandler.getInstance().showErrorMessage(Title.SETTINGS_ERROR, Message.RESTORE_SETTINGS_ERROR, false);
                    }
                });
                task.setOnFailed(e -> {
                    // debug
                    // task.getException().printStackTrace();
                    SceneHandler.getInstance().showErrorMessage(Title.SETTINGS_ERROR, Message.RESTORE_SETTINGS_ERROR, false);
                });
                ExecutorServiceManager.getInstance().getExecutorService().submit(task);
            }
    }

    private boolean validateInput() {
        // checking the validity of the max credits field
        try {
            double credits = Double.parseDouble(maxCreditsTextField.getText());
            if (credits <= 0) {
                errorLabel.setText(Message.CREDITS_GREATER_THAN_ZERO);
                return false;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText(Message.INVALID_FORMAT_CREDITS);
            return false;
        }
        return true;
    }

    @FXML
    void handleSave(ActionEvent event) {
        // handling save request
        if (validateInput()) {
            Double maxCredits = Double.parseDouble(maxCreditsTextField.getText());
            Integer gradeValue = gradeValueSpinner.getValue();
            String currency = currencyComboBox.getValue().split(" +")[1];
            ThemeModel theme = themeChoiceBox.getValue();
            if (modifiedInput(maxCredits, gradeValue, currency, theme)) {
                if (SceneHandler.getInstance().showConfirmationMessage(Title.EDIT_CONFIRMATION, Message.EDIT_SETTINGS_CONFIRMATION)) {
                    Task<StudentSettings> task = DBConnection.getInstance().updateSettings(maxCredits, gradeValue, currency, theme, MainHandler.getInstance().getUser());
                    handleBinding(task);
                    task.setOnSucceeded(e -> {
                        if (task.getValue() != null) {
                            StudentSettings settings = task.getValue();
                            setStudentSettingsValues(settings);
                            actualSettings = settings;
                            setValues();
                            SceneHandler.getInstance().showInfoMessage(Title.EDIT_INFO, Message.EDITED_SETTINGS);
                        } else {
                            SceneHandler.getInstance().showErrorMessage(Title.SETTINGS_ERROR, Message.EDIT_SETTINGS_ERROR, false);
                        }
                    });
                    task.setOnFailed(e -> {
                        // debug
                        // task.getException().printStackTrace();
                        SceneHandler.getInstance().showErrorMessage(Title.SETTINGS_ERROR, Message.EDIT_SETTINGS_ERROR, false);
                    });
                    ExecutorServiceManager.getInstance().getExecutorService().submit(task);
                }
            }
        }
    }

    private void loadSettings() {
        // loading settings
        Task<StudentSettings> task = DBConnection.getInstance().getSettings(MainHandler.getInstance().getUser());
        task.setOnSucceeded(e -> {
            if (task.getValue() != null) {
                StudentSettings settings = task.getValue();
                setStudentSettingsValues(settings);
                actualSettings = settings;
                setValues();
            } else {
                SceneHandler.getInstance().showErrorMessage(Title.SETTINGS_ERROR, Message.LOAD_SETTINGS_ERROR, true);
            }
        });
        task.setOnFailed(e -> {
            // debug
            // task.getException().printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.SETTINGS_ERROR, Message.LOAD_SETTINGS_ERROR, true);
        });
        ExecutorServiceManager.getInstance().getExecutorService().submit(task);
    }

    private void setUpGradeValueSpinner() {
        // setting up grade value spinner
        gradeValueSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 100));
    }

    private void setUpThemeChoiceBox() {
        // setting up theme choice box
        themeChoiceBox.getItems().setAll(ThemeModel.DARK, ThemeModel.LIGHT);
        // handling problem with focus/hover/action
        themeChoiceBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !themeChoiceBox.isShowing()) {
                themeChoiceBox.show();
            }
        });
    }

    private void setUpCurrencyComboBox() {
        // setting up currency choice box
        currencyComboBox.setItems(CurrencyHandlerView.getInstance().getCurrencies());
        // handling problem with focus/hover/action
        currencyComboBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !currencyComboBox.isShowing()) {
                currencyComboBox.show();
            }
        });
    }

    private void setUpStyle() {
        // setting up style
        settingsLabel.getStyleClass().add("bold-label");
        errorLabel.getStyleClass().add("error-label");
    }

    @FXML
    void initialize() {
        setUpStyle();
        setUpCurrencyComboBox();
        setUpThemeChoiceBox();
        setUpGradeValueSpinner();
        setValues();
        loadSettings();
    }
}
