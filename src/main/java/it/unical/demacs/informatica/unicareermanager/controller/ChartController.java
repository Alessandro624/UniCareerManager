package it.unical.demacs.informatica.unicareermanager.controller;

import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Settings;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.ChartsHandler;
import it.unical.demacs.informatica.unicareermanager.model.Exam;
import it.unical.demacs.informatica.unicareermanager.model.ExamStatisticsProperties;
import it.unical.demacs.informatica.unicareermanager.model.StudentSettingsProperties;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.view.ExamHandlerView;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;

import java.util.*;

public class ChartController {

    @FXML
    private LineChart<String, Number> avgChart;

    @FXML
    private Label chartsLabel;

    @FXML
    private Label creditsLabel;

    @FXML
    private ProgressIndicator creditsProgressIndicator;

    @FXML
    private BarChart<String, Number> examPerGradeChart;

    @FXML
    private PieChart gradeChart;

    @FXML
    private Label noExamLabel;

    @FXML
    private LineChart<String, Number> pAvgChart;

    @FXML
    private Label percentageLabel;

    @FXML
    private TabPane tabPane;

    private final XYChart.Series<String, Number> avgSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> pAvgSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> examPerGradeSeries = new XYChart.Series<>();

    private void setUpListener() {
        // setting up change listener
        ExamHandlerView.getInstance().addListener((ListChangeListener<? super Exam>) change -> updateCharts());
    }

    private void setUpCreditsProgress() {
        // setting up bindings for credits progress indicator
        DoubleBinding progressBinding = Bindings.createDoubleBinding(() ->
                        ExamStatisticsProperties.getInstance().getTotalCredits() / StudentSettingsProperties.getInstance().getMaxCredits(),
                ExamStatisticsProperties.getInstance().totalCreditsProperty(),
                StudentSettingsProperties.getInstance().maxCreditsProperty()
        );
        creditsProgressIndicator.progressProperty().bind(progressBinding);
        StringBinding creditsBinding = Bindings.createStringBinding(() ->
                        String.format(Locale.US, "Crediti acquisiti: %.2f/%.2f",
                                ExamStatisticsProperties.getInstance().getTotalCredits(),
                                StudentSettingsProperties.getInstance().getMaxCredits()),
                ExamStatisticsProperties.getInstance().totalCreditsProperty(),
                StudentSettingsProperties.getInstance().maxCreditsProperty()
        );
        creditsLabel.textProperty().bind(creditsBinding);
        StringBinding percentageBinding = Bindings.createStringBinding(() ->
                        String.format(Locale.US, "%.2f%%",
                                ExamStatisticsProperties.getInstance().getTotalCredits() * 100 / StudentSettingsProperties.getInstance().getMaxCredits()),
                ExamStatisticsProperties.getInstance().totalCreditsProperty(),
                StudentSettingsProperties.getInstance().maxCreditsProperty()
        );
        percentageLabel.textProperty().bind(percentageBinding);
    }

    private void handleFailedTask(Task<?> task) {
        // handling failed task
        task.setOnFailed(e -> {
            // debug
            // task.getException().printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.CHART_ERROR, Message.LOAD_CHART_ERROR, true);
        });
    }

    private void setUpGradeChart(Map<String, Integer> gradeCountMap) {
        // setting up grade chart
        gradeChart.getData().clear();
        int sum = gradeCountMap.values().stream().mapToInt(Integer::intValue).sum();
        gradeCountMap.forEach((grade, count) -> {
            PieChart.Data data = new PieChart.Data(grade, count);
            gradeChart.getData().add(data);
            double perch = Math.min((double) count / sum * 100, Settings.MAX_DERIVE_PIE_CHART);
            data.getNode().setStyle("-fx-background-color: derive(secondarySideColor, -" + perch + "%);");
            Tooltip tooltip = new Tooltip(String.format("%.2f%%", perch));
            Tooltip.install(data.getNode(), tooltip);
        });
        noExamLabel.setVisible(gradeCountMap.isEmpty());
        // without this labels might change their position
        gradeChart.layout();
    }

    private void setUpExamPerGradeChart(Map<String, Integer> gradeCountMap) {
        // setting up exam per grade chart
        examPerGradeSeries.getData().clear();
        gradeCountMap.forEach((grade, count) ->
                examPerGradeSeries.getData().add(new XYChart.Data<>(grade, count)));
    }

    private void setUpAvgChart(boolean avg) {
        // setting up avg chart
        Task<Map<String, Double>> task = ChartsHandler.getInstance().getAverageGradesOverTime(avg, ExamHandlerView.getInstance().getExams().stream().toList());
        task.setOnSucceeded(e -> {
            XYChart.Series<String, Number> series = avg ? avgSeries : pAvgSeries;
            series.getData().clear();
            task.getValue().forEach((date, avgGrade) -> series.getData().add(new XYChart.Data<>(date, avgGrade)));
        });
        handleFailedTask(task);
        ExecutorServiceManager.getInstance().getExecutorService().submit(task);
    }

    private void updateCharts() {
        // normal avg
        setUpAvgChart(true);
        // weighted average
        setUpAvgChart(false);
        // exam per grade and grade chart
        Task<Map<String, Integer>> task = ChartsHandler.getInstance().getGradeCount(ExamHandlerView.getInstance().getExams().stream().toList());
        task.setOnSucceeded(e -> {
            setUpExamPerGradeChart(task.getValue());
            setUpGradeChart(task.getValue());
        });
        handleFailedTask(task);
        ExecutorServiceManager.getInstance().getExecutorService().submit(task);
    }

    private void setUpCharts() {
        // setting up charts
        avgChart.getXAxis().setLabel("Data");
        avgChart.getYAxis().setLabel("Media");
        avgSeries.setName("Media degli Esami");
        avgChart.getData().add(avgSeries);
        pAvgChart.getXAxis().setLabel("Data");
        pAvgChart.getYAxis().setLabel("Media ponderata");
        pAvgSeries.setName("Media ponderata degli Esami");
        pAvgChart.getData().add(pAvgSeries);
        examPerGradeChart.getXAxis().setLabel("Voto");
        examPerGradeChart.getYAxis().setLabel("Numero");
        examPerGradeSeries.setName("Numero di Esami per Voto");
        examPerGradeChart.getData().add(examPerGradeSeries);
    }

    private void setUpTabs() {
        // setting up dimensions of tabs
        tabPane.tabMinWidthProperty()
                .bind(tabPane.widthProperty()
                        .divide(tabPane.getTabs()
                                .size()).subtract(20));
    }

    private void setUpStyle() {
        // setting up style
        chartsLabel.getStyleClass().add("bold-label");
        creditsLabel.getStyleClass().add("bold-label");
    }

    @FXML
    void initialize() {
        setUpStyle();
        setUpTabs();
        setUpCharts();
        updateCharts();
        setUpCreditsProgress();
        setUpListener();
    }
}
