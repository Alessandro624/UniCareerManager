package it.unical.demacs.informatica.unicareermanager.controller;

import it.unical.demacs.informatica.unicareermanager.util.ColorUtils;
import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.DBConnection;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.model.MainHandler;
import it.unical.demacs.informatica.unicareermanager.model.Subject;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import it.unical.demacs.informatica.unicareermanager.view.SubjectHandlerView;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class AddSubjectController implements AddEditController {

    @FXML
    private Button addSubjectButton;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField professorTextField;

    @FXML
    private TextField subjectNameTextField;

    private Subject toEditSubject = null;

    @FXML
    void handleAddSubject(ActionEvent event) {
        // handling the add/edit subject action
        if (!validateInput())
            return;
        String subjectName = subjectNameTextField.getText();
        String professor = professorTextField.getText();
        String color = ColorUtils.toHexString(colorPicker.getValue());
        if (toEditSubject != null && !modifiedInput(subjectName, professor, color))
            return;
        Task<Subject> task;
        if (toEditSubject == null)
            task = DBConnection.getInstance().insertSubject(subjectName, professor, color, MainHandler.getInstance().getUser());
        else if (SceneHandler.getInstance().showConfirmationMessage(Title.EDIT_CONFIRMATION, Message.EDIT_SUBJECT_CONFIRMATION))
            task = DBConnection.getInstance().updateSubject(subjectName, toEditSubject.subjectName(), professor, color, MainHandler.getInstance().getUser());
        else
            task = null;
        if (task != null) {
            task.setOnSucceeded(e -> handleTaskSuccess(task.getValue()));
            task.setOnFailed(e -> {
                // debug
                // task.getException().printStackTrace();
                handleTaskFailure();
            });
            ExecutorServiceManager.getInstance().getExecutorService().submit(task);
        }
    }

    private boolean modifiedInput(String subjectName, String professor, String color) {
        // checking if at least one field has been modified
        if (subjectName.equals(toEditSubject.subjectName()) && professor.equals(toEditSubject.professor()) && color.equals(toEditSubject.color())) {
            errorLabel.setText(Message.NOT_EDITED);
            return false;
        }
        return true;
    }

    private boolean validateInput() {
        // checking the validity of the fields
        if (subjectNameTextField.getText().isBlank() || professorTextField.getText().isBlank() || colorPicker.getValue() == null) {
            errorLabel.setText(Message.ALL_FIELDS_ARE_MANDATORY);
            return false;
        }
        if (professorTextField.getText().matches(".*[0-9].*")) {
            errorLabel.setText(Message.ONLY_LETTERS_PROFESSOR_FIELD);
            return false;
        }
        return true;
    }

    private void handleTaskSuccess(Subject subject) {
        if (subject == null) {
            handleTaskFailure();
            return;
        }// handling success of a task
        // toEditSubject == null --> insert a new subject
        // toEditSubject != null --> edit the passed subject
        if (toEditSubject == null) {
            SubjectHandlerView.getInstance().addSubject(subject);
            clearFields();
        } else {
            SubjectHandlerView.getInstance().removeSubject(toEditSubject);
            SubjectHandlerView.getInstance().addSubject(subject);
            toEditSubject = subject;
            errorLabel.setText("");
        }
        SceneHandler.getInstance().showInfoMessage((toEditSubject == null) ? Title.ADD_INFO : Title.EDIT_INFO, (toEditSubject == null) ? Message.ADDED_SUBJECT : Message.EDITED_SUBJECT);
    }

    private void handleTaskFailure() {
        // handling task failure
        SceneHandler.getInstance().showErrorMessage(Title.SUBJECT_ERROR, (toEditSubject == null) ? Message.ADD_SUBJECT_ERROR : Message.EDIT_SUBJECT_ERROR, false);
    }

    @Override
    public void setEditItem(Object item) throws Exception {
        // to transform the add subject scene in edit mode with the information of the subject to edit
        if (item instanceof Subject subject) {
            toEditSubject = subject;
            subjectNameTextField.setText(subject.subjectName());
            professorTextField.setText(subject.professor());
            colorPicker.setValue(Color.web(subject.color()));
            addSubjectButton.setText("Modifica materia");
        } else
            throw new Exception("Item must be an istance of Subject");
    }

    private void setUpColorPicker() {
        // handling problem with focus/hover/action
        colorPicker.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !colorPicker.isShowing()) {
                colorPicker.show();
            }
        });
    }

    private void clearFields() {
        // clearing text from fields
        errorLabel.setText("");
        subjectNameTextField.clear();
        professorTextField.clear();
    }

    private void setUpStyle() {
        // styling the error label
        errorLabel.getStyleClass().add("error-label");
    }

    @FXML
    void initialize() {
        setUpStyle();
        clearFields();
        setUpColorPicker();
    }
}
