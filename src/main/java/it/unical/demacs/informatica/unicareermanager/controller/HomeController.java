package it.unical.demacs.informatica.unicareermanager.controller;

import com.calendarfx.view.AgendaView;
import it.unical.demacs.informatica.unicareermanager.costants.Settings;
import it.unical.demacs.informatica.unicareermanager.model.ExamStatisticsProperties;
import it.unical.demacs.informatica.unicareermanager.model.StudentSettingsProperties;
import it.unical.demacs.informatica.unicareermanager.model.TaxStatisticsProperties;
import it.unical.demacs.informatica.unicareermanager.model.UserProperties;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.view.AgendaHandlerView;
import it.unical.demacs.informatica.unicareermanager.view.CustomAgendaView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeController {

    @FXML
    private VBox agendaBox;

    @FXML
    private Text avgLabel;

    @FXML
    private Text avgValueLabel;

    @FXML
    private VBox cardsVBox;

    @FXML
    private Text degreeGradeLabel;

    @FXML
    private ProgressBar degreeGradeProgressBar;

    @FXML
    private Text degreeGradeValueLabel;

    @FXML
    private Label homeLabel;

    @FXML
    private Text pAvgLabel;

    @FXML
    private Text pAvgValueLabel;

    @FXML
    private Text paidTaxesLabel;

    @FXML
    private Text paidTaxesValueLabel;

    @FXML
    private Label studentStatisticsLabel;

    @FXML
    private Text toPayTaxesLabel;

    @FXML
    private Text toPayTaxesValueLabel;

    @FXML
    private Text totalCreditsLabel;

    @FXML
    private Text totalCreditsValueLabel;

    @FXML
    private Label welcomeLabel;

    private final AgendaView agendaView = new CustomAgendaView(AgendaHandlerView.getInstance().getCalendarSource());

    private void bindValues() {
        // binding values to label
        welcomeLabel.textProperty().bind(Bindings.createStringBinding(() ->
                        String.format("Benvenuto/a %s %s",
                                UserProperties.getInstance().getFirstName(),
                                UserProperties.getInstance().getLastName()),
                UserProperties.getInstance().firstNameProperty(),
                UserProperties.getInstance().lastNameProperty()));
        avgValueLabel.textProperty().bind(ExamStatisticsProperties.getInstance().avgProperty().asString(Locale.US, "%.2f"));
        pAvgValueLabel.textProperty().bind(ExamStatisticsProperties.getInstance().pAvgProperty().asString(Locale.US, "%.2f"));
        totalCreditsValueLabel.textProperty().bind(ExamStatisticsProperties.getInstance().totalCreditsProperty().asString(Locale.US, "%.2f"));
        paidTaxesValueLabel.textProperty().bind(Bindings.createStringBinding(() ->
                        String.format(Locale.US, "%.2f %s",
                                TaxStatisticsProperties.getInstance().getTotalPaid(),
                                StudentSettingsProperties.getInstance().getCurrency()),
                TaxStatisticsProperties.getInstance().totalPaidProperty(),
                StudentSettingsProperties.getInstance().currencyProperty()));
        toPayTaxesValueLabel.textProperty().bind(Bindings.createStringBinding(() ->
                        String.format(Locale.US, "%.2f %s",
                                TaxStatisticsProperties.getInstance().getTotalToPay(),
                                StudentSettingsProperties.getInstance().getCurrency()),
                TaxStatisticsProperties.getInstance().totalToPayProperty(),
                StudentSettingsProperties.getInstance().currencyProperty()));
        DoubleBinding progressBinding = Bindings.createDoubleBinding(() ->
                        ExamStatisticsProperties.getInstance().getPAvg() * Settings.MAX_DEGREE_GRADE / Settings.MAX_GRADE,
                ExamStatisticsProperties.getInstance().pAvgProperty());
        degreeGradeProgressBar.progressProperty().bind(progressBinding.divide(Settings.MAX_DEGREE_GRADE));
        degreeGradeValueLabel.textProperty().bind(
                Bindings.createStringBinding(() -> String.format(Locale.US, "%.2f/%d",
                                progressBinding.getValue(), Settings.MAX_DEGREE_GRADE),
                        progressBinding));
    }

    private void updateTime() {
        // updating timeline
        Platform.runLater(() -> {
            agendaView.setToday(LocalDate.now());
            agendaView.setDate(LocalDate.now());
        });
    }

    private void initAgenda() {
        // initializing agenda
        agendaBox.getChildren().addFirst(agendaView);
        VBox.setVgrow(agendaView, Priority.ALWAYS);
        ExecutorServiceManager.getInstance().getScheduledExecutorService().scheduleAtFixedRate(this::updateTime, 0, 45, TimeUnit.SECONDS);
    }

    private void setUpCardsStyle() {
        // setting up cards style
        cardsVBox.getChildren().stream()
                .filter(node -> node instanceof HBox)
                .map(node -> (HBox) node)
                .forEach(hBox -> hBox.getChildren().stream()
                        .filter(node -> node instanceof VBox)
                        .map(node -> (VBox) node)
                        .forEach(vBox -> {
                            vBox.getStyleClass().add("information-cards");
                            vBox.getChildren().stream()
                                    .filter(node -> node instanceof Text)
                                    .map(node -> (Text) node)
                                    .forEach(text -> text.getStyleClass().add("bold-label"));
                        }));
    }

    private void setUpStyle() {
        // setting up style
        setUpCardsStyle();
        homeLabel.getStyleClass().add("bold-label");
        studentStatisticsLabel.getStyleClass().add("bold-label");
        welcomeLabel.getStyleClass().add("welcome-label");
    }

    @FXML
    void initialize() {
        setUpStyle();
        initAgenda();
        bindValues();
    }
}
