package it.unical.demacs.informatica.unicareermanager.controller;

import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.Exam;
import it.unical.demacs.informatica.unicareermanager.model.ExamPrevision;
import it.unical.demacs.informatica.unicareermanager.model.ExamPrevisionService;
import it.unical.demacs.informatica.unicareermanager.model.ExamStatisticsProperties;
import it.unical.demacs.informatica.unicareermanager.view.ExamHandlerView;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Locale;

public class PrevisionController {

    @FXML
    private Label actualAvgLabel;

    @FXML
    private Label actualPAvgLabel;

    @FXML
    private TableColumn<ExamPrevision, String> avgTableColumn;

    @FXML
    private Button calculationButton;

    @FXML
    private TextField creditsTextField;

    @FXML
    private TableColumn<ExamPrevision, String> gradeTableColumn;

    @FXML
    private TableColumn<ExamPrevision, String> pAvgTableColumn;

    @FXML
    private Label previsionLabel;

    @FXML
    private TableView<ExamPrevision> previsionTableView;

    @FXML
    private ProgressIndicator progressIndicator;

    private final ExamPrevisionService service = new ExamPrevisionService();

    private void updatePrevisionTable(double credits) {
        // computing prevision values
        previsionTableView.getItems().clear();
        service.setPrevisionCredits(credits);
        service.setExams(ExamHandlerView.getInstance().getExams());
        service.restart();
    }

    @FXML
    void handleCalculation(ActionEvent event) {
        // handling calculation request
        String creditsText = creditsTextField.getText();
        if (isValidCredits(creditsText)) {
            double credits = Double.parseDouble(creditsText);
            updatePrevisionTable(credits);
        }
    }

    private boolean isValidCredits(String creditsText) {
        // validity check
        try {
            double credits = Double.parseDouble(creditsText);
            if (credits <= 0) {
                SceneHandler.getInstance().showErrorMessage(Title.PREVISION_ERROR, Message.CREDITS_GREATER_THAN_ZERO, false);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            SceneHandler.getInstance().showErrorMessage(Title.PREVISION_ERROR, Message.INVALID_FORMAT_CREDITS, false);
        }
        return false;
    }

    private void handleBinding() {
        // handling binding for task completion
        progressIndicator.progressProperty().bind(service.progressProperty());
        progressIndicator.visibleProperty().bind(service.runningProperty());
        calculationButton.disableProperty().bind(service.runningProperty());
    }

    private void setUpService() {
        // setting up service
        service.setOnSucceeded(e -> {
            if (e.getSource().getValue() instanceof List<?> result && result.stream().allMatch(o -> o instanceof ExamPrevision)) {
                previsionTableView.getItems().setAll(result.stream().map(o -> (ExamPrevision) o).toList());
                previsionTableView.autosize();
            }
        });
        service.setOnFailed(e -> {
            // debug
            // e.getSource().getException().printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.PREVISION_ERROR, Message.LOAD_PREVISION_ERROR, false);
        });
    }

    private void setUpColumns() {
        // setting up columns with prevision values
        gradeTableColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().grade()));
        avgTableColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(String.format(Locale.US, "%.2f", s.getValue().avg())));
        pAvgTableColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(String.format(Locale.US, "%.2f", s.getValue().pAvg())));
    }

    public void handleClearSelection(MouseEvent event) {
        // clearing selection
        if (!(event.getTarget() instanceof TableRow)) {
            previsionTableView.getSelectionModel().clearSelection();
        }
    }

    private void setUpTableView() {
        // setting up table view items and handling action on add/edit/delete of items
        previsionTableView.setPlaceholder(new Label("Crediti del prossimo esame non inseriti"));
        previsionTableView.sceneProperty().addListener(((observable, oldScene, newScene) -> {
            if (newScene != null)
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleClearSelection);
        }));
        ExamHandlerView.getInstance().addListener((ListChangeListener<? super Exam>) change -> previsionTableView.getItems().clear());
    }

    private void setUpLabels() {
        // updating values in the labels
        actualAvgLabel.textProperty().bind(ExamStatisticsProperties.getInstance().avgProperty().asString(Locale.US, "Media attuale: %.2f"));
        actualPAvgLabel.textProperty().bind(ExamStatisticsProperties.getInstance().pAvgProperty().asString(Locale.US, "Media ponderata attuale: %.2f"));
    }

    private void setUpStyle() {
        // setting up style
        previsionLabel.getStyleClass().add("bold-label");
        actualAvgLabel.getStyleClass().add("bold-label");
        actualPAvgLabel.getStyleClass().add("bold-label");
    }

    @FXML
    void initialize() {
        setUpStyle();
        setUpLabels();
        setUpTableView();
        setUpColumns();
        setUpService();
        handleBinding();
    }
}
