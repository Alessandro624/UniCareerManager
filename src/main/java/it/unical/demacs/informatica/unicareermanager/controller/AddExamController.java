package it.unical.demacs.informatica.unicareermanager.controller;

import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Settings;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.DBConnection;
import it.unical.demacs.informatica.unicareermanager.model.Exam;
import it.unical.demacs.informatica.unicareermanager.model.MainHandler;
import it.unical.demacs.informatica.unicareermanager.model.Subject;
import it.unical.demacs.informatica.unicareermanager.util.DateUtils;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.view.AutoCompleteTextField;
import it.unical.demacs.informatica.unicareermanager.view.ExamHandlerView;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import it.unical.demacs.informatica.unicareermanager.view.SubjectHandlerView;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.List;

public class AddExamController implements AddEditController {

    @FXML
    private Button addExamButton;

    @FXML
    private TextField creditsField;

    @FXML
    private DatePicker dateField;

    @FXML
    private Label errorLabel;

    @FXML
    private HBox examBox;

    @FXML
    private ComboBox<String> gradeComboBox;

    private final AutoCompleteTextField examNameTextField = new AutoCompleteTextField();

    private Exam toEditExam = null;

    @FXML
    void handleAddExam(ActionEvent event) {
        // handling the add/edit exam action
        if (!validateInput())
            return;
        String examName = examNameTextField.getText();
        String date = DateUtils.formatDate(dateField.getValue());
        double credits = Double.parseDouble(creditsField.getText());
        String grade = gradeComboBox.getValue();
        if (toEditExam != null && !modifiedInput(examName, date, credits, grade))
            return;
        Task<Exam> task;
        if (toEditExam == null)
            task = DBConnection.getInstance().insertExam(examName, date, credits, grade, MainHandler.getInstance().getUser());
        else if (SceneHandler.getInstance().showConfirmationMessage(Title.EDIT_CONFIRMATION, Message.EDIT_EXAM_CONFIRMATION))
            task = DBConnection.getInstance().updateExam(toEditExam.id(), examName, date, credits, grade);
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

    private boolean modifiedInput(String examName, String date, double credits, String grade) {
        // checking if at least one field has been modified
        if (examName.equals(toEditExam.examName()) && date.equals(toEditExam.date()) && credits == toEditExam.credits() && grade.equals(toEditExam.grade())) {
            errorLabel.setText(Message.NOT_EDITED);
            return false;
        }
        return true;
    }

    private boolean validateInput() {
        // checking the validity of the fields
        if (examNameTextField.getText().isBlank() || dateField.getValue() == null || creditsField.getText().isBlank() || gradeComboBox.getValue() == null) {
            errorLabel.setText(Message.ALL_FIELDS_ARE_MANDATORY);
            return false;
        }
        try {
            double credits = Double.parseDouble(creditsField.getText());
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

    private void handleTaskSuccess(Exam exam) {
        if (exam == null) {
            handleTaskFailure();
            return;
        }
        // handling success of a task
        // toEditExam == null --> insert a new exam
        // toEditExam != null --> edit the passed exam
        if (toEditExam == null) {
            ExamHandlerView.getInstance().addExam(exam);
            clearFields();
        } else {
            ExamHandlerView.getInstance().removeExam(toEditExam);
            ExamHandlerView.getInstance().addExam(exam);
            toEditExam = exam;
            errorLabel.setText("");
        }
        SceneHandler.getInstance().showInfoMessage((toEditExam == null) ? Title.ADD_INFO : Title.EDIT_INFO, (toEditExam == null) ? Message.ADDED_EXAM : Message.EDITED_EXAM);
    }

    private void handleTaskFailure() {
        // handling task failure
        SceneHandler.getInstance().showErrorMessage(Title.STUDENT_CARD_ERROR, (toEditExam == null) ? Message.ADD_EXAM_ERROR : Message.EDIT_EXAM_ERROR, false);
    }

    @Override
    public void setEditItem(Object item) throws Exception {
        // to transform the add exam scene in edit mode with the information of the exam to edit
        if (item instanceof Exam exam) {
            toEditExam = exam;
            examNameTextField.setText(exam.examName());
            dateField.setValue(DateUtils.parseDate(exam.date()));
            creditsField.setText(exam.credits().toString());
            gradeComboBox.setValue(exam.grade());
            addExamButton.setText("Modifica esame");
        } else
            throw new Exception("Item must be an istance of Exam");
    }

    private void setUpAutoCompleteTextField() {
        // setting up auto complete text field
        List<String> suggestions = SubjectHandlerView.getInstance().getSubjects().stream().map(Subject::subjectName).toList();
        examNameTextField.setSuggestions(suggestions);
        examBox.getChildren().add(examNameTextField);
    }

    private void setUpDatePicker() {
        // handling problem with focus/hover/action
        dateField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER && !dateField.isShowing()) {
                dateField.show();
            }
        });
        // setting the dateField to hide dates after the current date
        dateField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
    }

    private void setUpComboBox() {
        // initializing combo box
        gradeComboBox.getItems().add("Idoneità");
        for (int i = Settings.MIN_GRADE; i <= Settings.MAX_GRADE; i++) {
            gradeComboBox.getItems().add(String.valueOf(i));
        }
        gradeComboBox.getItems().add("30 e lode");
        // handling problem with focus/hover/action
        gradeComboBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !gradeComboBox.isShowing()) {
                gradeComboBox.show();
            }
        });
    }

    private void clearFields() {
        // clearing text from fields
        errorLabel.setText("");
        examNameTextField.clear();
        dateField.setValue(null);
        creditsField.clear();
        gradeComboBox.setValue(null);
    }

    private void setUpStyle() {
        // styling the error label
        errorLabel.getStyleClass().add("error-label");
    }

    @FXML
    void initialize() {
        setUpStyle();
        clearFields();
        setUpComboBox();
        setUpDatePicker();
        setUpAutoCompleteTextField();
    }
}
