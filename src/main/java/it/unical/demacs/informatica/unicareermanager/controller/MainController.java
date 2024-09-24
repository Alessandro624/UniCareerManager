package it.unical.demacs.informatica.unicareermanager.controller;

import it.unical.demacs.informatica.unicareermanager.costants.HelpMessages;
import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Settings;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.CentralPanesModel;
import it.unical.demacs.informatica.unicareermanager.model.LoadCentralPanesHandler;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.HashMap;
import java.util.Map;

public class MainController {

    @FXML
    private Button accountButton;

    @FXML
    private FontIcon accountIcon;

    @FXML
    private Button agendaButton;

    @FXML
    private FontIcon agendaIcon;

    @FXML
    private AnchorPane centerPane;

    @FXML
    private Button changeThemeButton;

    @FXML
    private Button chartsButton;

    @FXML
    private FontIcon chartsIcon;

    @FXML
    private Button helpButton;

    @FXML
    private Button homeButton;

    @FXML
    private FontIcon homeIcon;

    @FXML
    private Button menuButton;

    @FXML
    private FontIcon menuIcon;

    @FXML
    private Button previsionButton;

    @FXML
    private FontIcon previsionIcon;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private BorderPane progressIndicatorPane;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button settingsButton;

    @FXML
    private FontIcon settingsIcon;

    @FXML
    private AnchorPane sideBarPane;

    @FXML
    private VBox sideBarVbox;

    @FXML
    private StackPane stackPane;

    @FXML
    private Button studentCardButton;

    @FXML
    private FontIcon studentCardIcon;

    @FXML
    private Button subjectButton;

    @FXML
    private FontIcon subjectIcon;

    @FXML
    private Button taxButton;

    @FXML
    private FontIcon taxIcon;

    @FXML
    private HBox utilityButtonBox;

    private Map<CentralPanesModel.PaneType, AnchorPane> panes = new HashMap<>();

    private AnchorPane oldPane = null;

    private CentralPanesModel.PaneType frontPane = CentralPanesModel.PaneType.HOME;

    private boolean isStretched = true;

    @FXML
    void handleChangeTheme(ActionEvent event) {
        // handling change theme
        SceneHandler.getInstance().changeTheme();
    }

    @FXML
    void handleHelp(ActionEvent event) {
        SceneHandler.getInstance().showHelpMessage(Title.HELP_REQUEST, HelpMessages.HEADER, CentralPanesModel.getPaneHelp(frontPane));
    }

    private void changeFrontPane(CentralPanesModel.PaneType type) {
        // setting the old front pane invisible to prevent focus problems
        if (oldPane != null)
            oldPane.setVisible(false);
        // changing front main pane using a TranslateTransition
        AnchorPane pane = panes.get(type);
        if (pane == null)
            return;
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.7), centerPane);
        slide.setToY(0);
        centerPane.setTranslateY(-100);
        frontPane = type;
        pane.toFront();
        utilityButtonBox.toFront();
        pane.setVisible(true);
        slide.play();
        oldPane = pane;
    }

    private void setContentDisplaySideBarButton(ContentDisplay type) {
        // setting content display of sidebar buttons (GRAFIC_ONLY or LEFT)
        sideBarVbox.getChildren().stream()
                .filter(node -> node instanceof VBox)
                .map(node -> (VBox) node)
                .forEach(vBox -> vBox.getChildren().stream()
                        .filter(node -> node instanceof Button)
                        .map(node -> (Button) node)
                        .forEach(button -> button.setContentDisplay(type)));
    }

    private void stretchSideBar() {
        // stretching sidebar using a transition according to the actual size of the sidebar
        double startWidth = sideBarPane.getPrefWidth();
        double endWidth = isStretched ? Settings.MAIN_VIEW_SIDEBAR_SIZE : Settings.MAIN_VIEW_STRETCHED_SIZE;
        Transition stretchTransition = new Transition() {
            {
                setCycleDuration(Duration.seconds(0.7)); // Imposta la durata dell'animazione
            }

            @Override
            protected void interpolate(double frac) {
                double newWidth = startWidth + (endWidth - startWidth) * frac;
                sideBarPane.setPrefWidth(newWidth);
            }
        };
        stretchTransition.play();
        setContentDisplaySideBarButton(isStretched ? ContentDisplay.LEFT : ContentDisplay.GRAPHIC_ONLY);
        isStretched = !isStretched;
    }

    @FXML
    void handleMenuBar(ActionEvent event) {
        // handling stretching of the sidebar
        stretchSideBar();
    }

    @FXML
    void handleToAccount(ActionEvent event) {
        // handling to account pane
        if (frontPane != CentralPanesModel.PaneType.ACCOUNT)
            changeFrontPane(CentralPanesModel.PaneType.ACCOUNT);
    }

    @FXML
    void handleToCharts(ActionEvent event) {
        // handling to charts pane
        if (frontPane != CentralPanesModel.PaneType.CHARTS)
            changeFrontPane(CentralPanesModel.PaneType.CHARTS);
    }

    @FXML
    void handleToHome(ActionEvent event) {
        // handling to home pane
        if (frontPane != CentralPanesModel.PaneType.HOME)
            changeFrontPane(CentralPanesModel.PaneType.HOME);
    }

    @FXML
    void handleToAgenda(ActionEvent event) {
        // handling to agenda pane
        if (frontPane != CentralPanesModel.PaneType.AGENDA)
            changeFrontPane(CentralPanesModel.PaneType.AGENDA);
    }

    @FXML
    void handleToPrevision(ActionEvent event) {
        // handling to prevision pane
        if (frontPane != CentralPanesModel.PaneType.PREVISION)
            changeFrontPane(CentralPanesModel.PaneType.PREVISION);
    }

    @FXML
    void handleToSettings(ActionEvent event) {
        // handling to settings pane
        if (frontPane != CentralPanesModel.PaneType.SETTINGS)
            changeFrontPane(CentralPanesModel.PaneType.SETTINGS);
    }

    @FXML
    void handleToStudentCard(ActionEvent event) {
        // handling to student card pane
        if (frontPane != CentralPanesModel.PaneType.STUDENT_CARD)
            changeFrontPane(CentralPanesModel.PaneType.STUDENT_CARD);
    }

    @FXML
    void handleToSubject(ActionEvent event) {
        // handling to subject pane
        if (frontPane != CentralPanesModel.PaneType.SUBJECT)
            changeFrontPane(CentralPanesModel.PaneType.SUBJECT);
    }

    @FXML
    void handleToTax(ActionEvent event) {
        // handling to tax pane
        if (frontPane != CentralPanesModel.PaneType.TAX)
            changeFrontPane(CentralPanesModel.PaneType.TAX);
    }

    private void setIcon(FontIcon icon, String literal, int size) {
        // setting literal, size and style of icons
        icon.setIconLiteral(literal);
        icon.setIconSize(size);
        icon.getStyleClass().add("icons-color");
    }

    private void setSideBarIcons() {
        // setting icon literal and size
        setIcon(accountIcon, "fas-user-graduate", 20);
        setIcon(chartsIcon, "mdi2c-chart-bar-stacked", 25);
        setIcon(homeIcon, "mdi2h-home", 25);
        setIcon(agendaIcon, "mdi2c-calendar-month-outline", 25);
        setIcon(menuIcon, "mdi2m-menu", 25);
        setIcon(previsionIcon, "mdi2b-book-search-outline", 25);
        setIcon(settingsIcon, "fas-tools", 20);
        setIcon(studentCardIcon, "mdi2b-book-education-outline", 25);
        setIcon(subjectIcon, "mdi2b-book-open-page-variant-outline", 25);
        setIcon(taxIcon, "far-money-bill-alt", 20);
    }

    private void setSideBarButtonStyle() {
        // setting style class
        sideBarVbox.getChildren().stream()
                .filter(node -> node instanceof VBox)
                .map(node -> (VBox) node)
                .forEach(vBox -> vBox.getChildren().stream()
                        .filter(node -> node instanceof Button)
                        .map(node -> (Button) node)
                        .forEach(button -> button.getStyleClass().add("side-bar-button")));
    }

    private void setPaneStyle() {
        // setting style class
        sideBarPane.getStyleClass().add("side-pane");
        sideBarVbox.getStyleClass().add("transparent-node");
        // setting style class using stream
        sideBarVbox.getChildren().stream()
                .filter(node -> node instanceof VBox)
                .map(node -> (VBox) node)
                .forEach(vBox -> vBox.getStyleClass().add("transparent-node"));
    }

    private void handleBinding(Task<?> task) {
        // handling binding of progress indicator
        progressIndicator.progressProperty().bind(task.progressProperty());
        progressIndicator.visibleProperty().bind(task.runningProperty());
    }

    private void initMainPage() {
        // initializing main page
        stackPane.getChildren().forEach(node -> node.setVisible(false));
        changeFrontPane(frontPane);
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), progressIndicatorPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> progressIndicatorPane.setVisible(false));
        fadeOut.play();
    }

    private void loadPanes() {
        // load all panes
        Task<Map<CentralPanesModel.PaneType, AnchorPane>> task = LoadCentralPanesHandler.getInstance().loadPanes();
        handleBinding(task);
        progressIndicatorPane.setVisible(true);
        task.setOnSucceeded(e -> {
            panes = task.getValue();
            stackPane.getChildren().addAll(panes.values());
            initMainPage();
        });
        task.setOnFailed(e -> {
            // debug
            // task.getException().printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.ON_START_ERROR, Message.ON_START_ERROR, true);
        });
        ExecutorServiceManager.getInstance().getExecutorService().submit(task);
    }

    private void setUpHelpButton() {
        FontIcon helpIcon = new FontIcon();
        setIcon(helpIcon, "mdi2h-help-circle-outline", 25);
        helpButton.setGraphic(helpIcon);
    }

    private void setUpChangeThemeButton() {
        // setting up change theme button
        changeThemeButton.setGraphic(SceneHandler.getInstance().getIconTheme());
    }

    private void setUpCloseRequest() {
        // setting up closing event
        SceneHandler.getInstance().setOnCloseAction(e -> {
            e.consume();
            if (SceneHandler.getInstance().showConfirmationMessage(Title.CLOSE_CONFIRMATION, Message.CLOSE_CONFIRMATION)) {
                ((Stage) rootPane.getScene().getWindow()).close();
                SceneHandler.getInstance().defaultCloseOperations();
            }
        });
    }

    @FXML
    void initialize() {
        setUpCloseRequest();
        loadPanes();
        setUpChangeThemeButton();
        setUpHelpButton();
        setPaneStyle();
        setSideBarButtonStyle();
        setSideBarIcons();
        stretchSideBar();
    }
}
