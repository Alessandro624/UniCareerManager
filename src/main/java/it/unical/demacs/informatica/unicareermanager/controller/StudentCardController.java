package it.unical.demacs.informatica.unicareermanager.controller;

import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.*;
import it.unical.demacs.informatica.unicareermanager.util.DateUtils;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.view.ButtonTableCell;
import it.unical.demacs.informatica.unicareermanager.view.ExamHandlerView;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class StudentCardController {

    @FXML
    private Button addExamButton;

    @FXML
    private Label avgLabel;

    @FXML
    private TableColumn<Exam, String> creditsColumn;

    @FXML
    private TableColumn<Exam, String> dateColumn;

    @FXML
    private TableColumn<Exam, Void> deleteEditColumn;

    @FXML
    private TableColumn<Exam, String> examNameColumn;

    @FXML
    private TableColumn<Exam, String> gradesColumn;

    @FXML
    private TableView<Exam> examTableView;

    @FXML
    private ChoiceBox<String> filterChoiceBox;

    @FXML
    private TextField filterTextField;

    @FXML
    private Label pAvgLabel;

    @FXML
    private Label studentCardLabel;

    @FXML
    private Label totCreditsLabel;

    private final ExamStatisticsService service = new ExamStatisticsService();

    private static final String EXAM = "Esame";
    private static final String DATE = "Data";
    private static final String CREDITS = "Crediti";
    private static final String GRADE = "Voti";

    @FXML
    void handleAddExamButton(ActionEvent event) {
        // opening the add exam scene
        try {
            SceneHandler.getInstance().setAddEditScene("add-exam-view.fxml", Title.ADD_EXAM_TITLE, null);
        } catch (Exception e) {
            // debug
            // e.printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.STUDENT_CARD_ERROR, Message.ADD_EXAM_ERROR, false);
        }
    }

    private void setUpLabels() {
        // updating values in the labels
        avgLabel.textProperty().bind(ExamStatisticsProperties.getInstance().avgProperty().asString(Locale.US, "Media: %.2f"));
        pAvgLabel.textProperty().bind(ExamStatisticsProperties.getInstance().pAvgProperty().asString(Locale.US, "Media ponderata: %.2f"));
        totCreditsLabel.textProperty().bind(ExamStatisticsProperties.getInstance().totalCreditsProperty().asString(Locale.US, "Crediti acquisiti: %.2f"));
    }

    private void setFilteredItems() {
        // setting up filters
        FilteredList<Exam> filteredList = new FilteredList<>(ExamHandlerView.getInstance().getExams(), p -> true);
        filterTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            String lowerCaseFilter = (newValue == null || newValue.isBlank()) ? "" : newValue.toLowerCase();
            filteredList.setPredicate(exam -> {
                // dislpay all if filter is empty
                if (lowerCaseFilter.isBlank())
                    return true;
                switch (filterChoiceBox.getValue()) {
                    case EXAM -> {
                        return exam.examName().toLowerCase().contains(lowerCaseFilter);
                    }
                    case DATE -> {
                        return exam.date().toLowerCase().contains(lowerCaseFilter);
                    }
                    case CREDITS -> {
                        return String.format(Locale.US, "%.2f", exam.credits()).startsWith(lowerCaseFilter);
                    }
                    case GRADE -> {
                        return exam.grade().startsWith(lowerCaseFilter);
                    }
                    default -> {
                        return false;
                    }
                }
            });
        }));
        SortedList<Exam> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(examTableView.comparatorProperty());
        examTableView.setItems(sortedList);
    }

    private void update() {
        // updating labels
        service.setExams(ExamHandlerView.getInstance().getExams());
        service.restart();
    }

    private void loadExams() {
        // load exams
        Task<List<Exam>> task = DBConnection.getInstance().getExams(MainHandler.getInstance().getUser());
        task.setOnSucceeded(event -> {
            for (Exam e : task.getValue()) {
                ExamHandlerView.getInstance().addExam(e);
            }
            update();
            ExamHandlerView.getInstance().addListener((ListChangeListener<? super Exam>) change -> {
                update();
                if (change.next()) {
                    if (change.wasAdded()) {
                        if (!examTableView.getItems().isEmpty())
                            examTableView.getSelectionModel().select(examTableView.getItems().getLast());
                    }
                    if (change.wasRemoved()) {
                        examTableView.getSelectionModel().clearSelection();
                    }
                }
            });
            setFilteredItems();
            ExamHandlerView.getInstance().loadCompleted();
            // to prevent strange positioning of the scrollbar
            examTableView.autosize();
        });
        task.setOnFailed(e -> {
            // debug
            // task.getException().printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.STUDENT_CARD_ERROR, Message.LOAD_EXAM_ERROR, true);
        });
        ExecutorServiceManager.getInstance().getExecutorService().submit(task);
    }

    private void setUpService() {
        service.setOnSucceeded(e -> {
            examTableView.refresh();
            if (e.getSource().getValue() instanceof ExamStatistics examStatistics) {
                ExamStatisticsProperties.getInstance().setTotalCredits(examStatistics.totalCredits());
                ExamStatisticsProperties.getInstance().setAvg(examStatistics.avg());
                ExamStatisticsProperties.getInstance().setPAvg(examStatistics.pAvg());
            }
        });
        service.setOnFailed(e -> {
            // debug
            // e.getSource().getException().printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.STUDENT_CARD_ERROR, Message.UPDATE_ERROR, true);
        });
    }

    private void handleClearSelection(MouseEvent event) {
        // clearing selection
        if (!(event.getTarget() instanceof TableRow)) {
            examTableView.getSelectionModel().clearSelection();
        }
    }

    private void setUpTableView() {
        // setting up table view items and handling action on add/edit/delete of items
        examTableView.setPlaceholder(new Label("Nessun esame superato disponibile"));
        examTableView.sceneProperty().addListener(((observable, oldScene, newScene) -> {
            if (newScene != null)
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleClearSelection);
        }));
    }

    private void setUpDateComparator(TableColumn<Exam, String> column) {
        // local date comparator
        column.setComparator((String value1, String value2) -> {
            try {
                LocalDate dateValue1 = DateUtils.parseDate(value1);
                LocalDate dateValue2 = DateUtils.parseDate(value2);
                return dateValue1.compareTo(dateValue2);
            } catch (Exception e) {
                return value1.compareTo(value2);
            }
        });
    }

    private void setUpCreditsComparator(TableColumn<Exam, String> column) {
        // double comparator for credits
        column.setComparator((String value1, String value2) -> {
            try {
                double doubleValue1 = Double.parseDouble(value1);
                double doubleValue2 = Double.parseDouble(value2);
                return Double.compare(doubleValue1, doubleValue2);
            } catch (NumberFormatException e) {
                return value1.compareTo(value2);
            }
        });
    }

    private void deleteItem(Exam exam) {
        // handling the delete action of an exam
        if (SceneHandler.getInstance().showConfirmationMessage(Title.REMOVE_CONFIRMATION, Message.REMOVE_EXAM_CONFIRMATION)) {
            Task<Boolean> task = DBConnection.getInstance().removeExam(exam.id());
            task.setOnSucceeded(e -> {
                if (task.getValue())
                    ExamHandlerView.getInstance().removeExam(exam);
                else
                    SceneHandler.getInstance().showErrorMessage(Title.STUDENT_CARD_ERROR, Message.REMOVE_EXAM_ERROR, false);
            });
            task.setOnFailed(e -> {
                // debug
                // task.getException().printStackTrace();
                SceneHandler.getInstance().showErrorMessage(Title.STUDENT_CARD_ERROR, Message.REMOVE_EXAM_ERROR, false);
            });
            ExecutorServiceManager.getInstance().getExecutorService().submit(task);
        }
    }

    private void editItem(Exam exam) {
        // handling the edit action of an exam (using the add exam scene with a parameter)
        try {
            SceneHandler.getInstance().setAddEditScene("add-exam-view.fxml", Title.EDIT_EXAM_TITLE, exam);
        } catch (Exception e) {
            // debug
            // e.printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.STUDENT_CARD_ERROR, Message.EDIT_EXAM_ERROR, false);
        }
    }

    private void setUpDeleteEditButtonCell(TableColumn<Exam, Void> column) {
        // setting up delete and edit column (CellFactory)
        column.setCellFactory(param -> {
            ButtonTableCell<Exam> deleteEditCell = new ButtonTableCell<>();
            deleteEditCell.setOnDeleteAction(event -> deleteItem(deleteEditCell.getSelectedItem()));
            deleteEditCell.setOnEditAction(event -> editItem(deleteEditCell.getSelectedItem()));
            return deleteEditCell;
        });
    }

    private void setUpColumns() {
        // setting up columns with exam values
        examNameColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().examName()));
        dateColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().date()));
        creditsColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(String.format(Locale.US, "%.2f", s.getValue().credits())));
        gradesColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().grade()));
        setUpDeleteEditButtonCell(deleteEditColumn);
        setUpCreditsComparator(creditsColumn);
        setUpDateComparator(dateColumn);
    }

    private void setUpChoiceBox() {
        // setting up choice box
        filterChoiceBox.getItems().addAll(EXAM, DATE, CREDITS, GRADE);
        filterChoiceBox.setValue(EXAM);
        filterChoiceBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !filterChoiceBox.isShowing()) {
                filterChoiceBox.show();
            }
        });
    }

    private void applyStyles() {
        // styling labels
        studentCardLabel.getStyleClass().add("bold-label");
        avgLabel.getStyleClass().add("bold-label");
        pAvgLabel.getStyleClass().add("bold-label");
        totCreditsLabel.getStyleClass().add("bold-label");
    }

    @FXML
    void initialize() {
        applyStyles();
        setUpChoiceBox();
        setUpColumns();
        setUpTableView();
        setUpService();
        loadExams();
        setUpLabels();
    }
}
