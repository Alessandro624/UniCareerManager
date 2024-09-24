package it.unical.demacs.informatica.unicareermanager.controller;

import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.DBConnection;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.model.MainHandler;
import it.unical.demacs.informatica.unicareermanager.model.Subject;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import it.unical.demacs.informatica.unicareermanager.view.SubjectHandlerView;
import it.unical.demacs.informatica.unicareermanager.view.SubjectListCell;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.List;

public class SubjectController {

    @FXML
    private Button addSubjectButton;

    @FXML
    private Label emptyLabel;

    @FXML
    private HBox filterBox;

    @FXML
    private ChoiceBox<String> filterChoiceBox;

    @FXML
    private TextField filterTextField;

    @FXML
    private Label subjectLabel;

    @FXML
    private ListView<Subject> subjectListView;

    private static final String SUBJECT = "Materia";
    private static final String PROFESSOR = "Professore";

    @FXML
    void handleAddSubject(ActionEvent event) {
        // opening the add subject scene
        try {
            SceneHandler.getInstance().setAddEditScene("add-subject-view.fxml", Title.ADD_SUBJECT_TITLE, null);
        } catch (Exception e) {
            // debug
            // e.printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.SUBJECT_ERROR, Message.ADD_SUBJECT_ERROR, false);
        }
    }

    private void editSubject(Subject subject) {
        // handling the edit action of a subject (using the add subject scene with a parameter)
        try {
            SceneHandler.getInstance().setAddEditScene("add-subject-view.fxml", Title.EDIT_SUBJECT_TITLE, subject);
        } catch (Exception e) {
            SceneHandler.getInstance().showErrorMessage(Title.SUBJECT_ERROR, Message.EDIT_SUBJECT_ERROR, false);
        }
    }

    private void deleteSubject(Subject subject) {
        // handling the delete action of a subject
        if (SceneHandler.getInstance().showConfirmationMessage(Title.REMOVE_CONFIRMATION, Message.REMOVE_SUBJECT_CONFIRMATION)) {
            Task<Boolean> task = DBConnection.getInstance().removeSubject(subject.subjectName(), MainHandler.getInstance().getUser());
            task.setOnSucceeded(e -> {
                if (task.getValue())
                    SubjectHandlerView.getInstance().removeSubject(subject);
                else
                    SceneHandler.getInstance().showErrorMessage(Title.SUBJECT_ERROR, Message.REMOVE_SUBJECT_ERROR, false);
            });
            task.setOnFailed(e -> {
                // debug
                // task.getException().printStackTrace();
                SceneHandler.getInstance().showErrorMessage(Title.SUBJECT_ERROR, Message.REMOVE_SUBJECT_ERROR, false);
            });
            ExecutorServiceManager.getInstance().getExecutorService().submit(task);
        }
    }

    private void setVisibility() {
        // visibility of empty label
        boolean isEmpty = SubjectHandlerView.getInstance().getSubjects().isEmpty();
        emptyLabel.setVisible(isEmpty);
        filterBox.setVisible(!isEmpty);
    }

    private void handleClearSelection(MouseEvent event) {
        // clearing selection
        if (!(event.getTarget() instanceof ListCell)) {
            subjectListView.getSelectionModel().clearSelection();
        }
    }

    private void setUpListView() {
        // setting up list view
        subjectListView.setCellFactory(param -> {
            SubjectListCell cell = new SubjectListCell();
            cell.setOnDeleteAction(event -> deleteSubject(cell.getSelectedSubject()));
            cell.setOnEditAction(event -> editSubject(cell.getSelectedSubject()));
            return cell;
        });
        subjectListView.sceneProperty().addListener(((observable, oldScene, newScene) -> {
            if (newScene != null)
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleClearSelection);
        }));
    }

    private void setUpChoiceBox() {
        // setting up choice box
        filterChoiceBox.getItems().addAll(SUBJECT, PROFESSOR);
        filterChoiceBox.setValue(SUBJECT);
        filterChoiceBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !filterChoiceBox.isShowing()) {
                filterChoiceBox.show();
            }
        });
    }

    private void setFilteredItems() {
        // setting up filters
        FilteredList<Subject> filteredList = new FilteredList<>(SubjectHandlerView.getInstance().getSubjects(), p -> true);
        filterTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            String lowerCaseFilter = (newValue == null || newValue.isBlank()) ? "" : newValue.toLowerCase();
            filteredList.setPredicate(subject -> {
                // dislpay all if filter is empty
                if (lowerCaseFilter.isBlank())
                    return true;
                switch (filterChoiceBox.getValue()) {
                    case SUBJECT -> {
                        return subject.subjectName().toLowerCase().contains(lowerCaseFilter);
                    }
                    case PROFESSOR -> {
                        return subject.professor().toLowerCase().contains(lowerCaseFilter);
                    }
                    default -> {
                        return false;
                    }
                }
            });
        }));
        SortedList<Subject> sortedList = new SortedList<>(filteredList);
        subjectListView.setItems(sortedList);
    }

    private void loadSubjects() {
        // load subjects
        Task<List<Subject>> task = DBConnection.getInstance().getSubjects(MainHandler.getInstance().getUser());
        task.setOnSucceeded(event -> {
            for (Subject s : task.getValue()) {
                SubjectHandlerView.getInstance().addSubject(s);
            }
            setVisibility();
            SubjectHandlerView.getInstance().addListener((ListChangeListener<? super Subject>) change -> {
                setVisibility();
                if (change.next()) {
                    if (change.wasAdded()) {
                        if (!subjectListView.getItems().isEmpty())
                            subjectListView.getSelectionModel().select(subjectListView.getItems().getLast());
                    }
                    if (change.wasRemoved()) {
                        subjectListView.getSelectionModel().clearSelection();
                    }
                }
            });
            setFilteredItems();
            SubjectHandlerView.getInstance().loadCompleted();
            // to prevent strange positioning of the scrollbar
            subjectListView.autosize();
        });
        task.setOnFailed(e -> {
            // debug
            // task.getException().printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.SUBJECT_ERROR, Message.LOAD_SUBJECT_ERROR, true);
        });
        ExecutorServiceManager.getInstance().getExecutorService().submit(task);
    }

    private void setUpStyle() {
        // setting up style
        subjectLabel.getStyleClass().add("bold-label");
    }

    @FXML
    void initialize() {
        setUpStyle();
        setUpChoiceBox();
        loadSubjects();
        setUpListView();
    }
}
