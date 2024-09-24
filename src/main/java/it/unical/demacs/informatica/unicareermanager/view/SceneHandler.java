package it.unical.demacs.informatica.unicareermanager.view;

import it.unical.demacs.informatica.unicareermanager.controller.AddEditController;
import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Settings;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.DBConnection;
import it.unical.demacs.informatica.unicareermanager.model.ThemeModel;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SceneHandler {
    private final static String RESOURCE_PATH = "/it/unical/demacs/informatica/unicareermanager/";
    private final static String VIEW_PATH = RESOURCE_PATH + "view/";
    private final static String CSS_PATH = RESOURCE_PATH + "css/";
    private final static String FONTS_PATH = RESOURCE_PATH + "fonts/";
    private final static String LOGOS_PATH = RESOURCE_PATH + "logos/";
    private final static String IMAGES_PATH = RESOURCE_PATH + "images/";

    private static SceneHandler instance = null;

    private final Alert alert;
    private final FontIcon errorIcon = new FontIcon("mdi2a-alert-circle");
    private final FontIcon confirmationIcon = new FontIcon("mdi2a-alert");
    private final FontIcon infoIcon = new FontIcon("mdi2i-information-outline");
    private final FontIcon helpIcon = new FontIcon("mdi2h-help-circle-outline");
    private final FontIcon themeIcon = new FontIcon();
    private final Image myLogo;

    private Scene scene;
    private Stage stage;

    private ThemeModel theme = Settings.DEFAULT_THEME;

    private boolean applicationCloseRequest = false;

    private SceneHandler() {
        // initializing logo
        myLogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream(LOGOS_PATH + "logo.png")));
        // initializing icons and the alert
        alert = new Alert(Alert.AlertType.NONE);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().setAll(myLogo);
        setUpIcons();

    }

    private void setUpIcons() {
        errorIcon.getStyleClass().add("icons-color");
        confirmationIcon.getStyleClass().add("icons-color");
        infoIcon.getStyleClass().add("icons-color");
        themeIcon.getStyleClass().add("icons-color");
        helpIcon.getStyleClass().add("icons-color");
        errorIcon.setIconSize(30);
        confirmationIcon.setIconSize(30);
        infoIcon.setIconSize(30);
        helpIcon.setIconSize(30);
        themeIcon.setIconSize(25);
    }

    public static SceneHandler getInstance() {
        if (instance == null)
            instance = new SceneHandler();
        return instance;
    }

    public void defaultCloseOperations() {
        DBConnection.getInstance().close();
        ExecutorServiceManager.getInstance().close();
    }

    public void setOnCloseAction(EventHandler<WindowEvent> handler) {
        if (stage != null)
            stage.setOnCloseRequest(handler);
    }

    public void init(Stage primaryStage) {
        if (stage != null)
            return;
        stage = primaryStage;
        stage.setTitle(Title.APPLICATION_TITLE);
        stage.getIcons().setAll(myLogo);
        setCurrentRoot("login-view.fxml");
        stage.setScene(scene);
        stage.setWidth(Settings.LOGIN_VIEW_WIDTH);
        stage.setHeight(Settings.LOGIN_VIEW_HEIGHT);
        stage.setResizable(false);
        loadFonts();
        changedTheme();
        stage.show();
    }

    public void setLoginScene() {
        setCurrentRoot("login-view.fxml");
        stage.hide();
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.setWidth(Settings.LOGIN_VIEW_WIDTH);
        stage.setHeight(Settings.LOGIN_VIEW_HEIGHT);
        stage.show();
    }

    public void setMainScene() {
        setCurrentRoot("main-view.fxml");
        stage.hide();
        stage.setResizable(true);
        stage.setWidth(Settings.MAIN_VIEW_WIDTH);
        stage.setHeight(Settings.MAIN_VIEW_HEIGHT);
        stage.show();
    }

    public <T> void setAddEditScene(String filename, String title, Object item) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_PATH + filename));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(loader.load(), Settings.ADD_EDIT_WIDTH, Settings.ADD_EDIT_HEIGHT);
        setCSSForScene(scene);
        stage.setResizable(false);
        stage.setMaximized(false);
        stage.setTitle(title);
        stage.setScene(scene);
        // setting the add scene according to the parameter
        // item == null --> add  mode scene
        // item != null --> edit mode scene
        T controller = loader.getController();
        if (item != null && controller instanceof AddEditController) {
            ((AddEditController) controller).setEditItem(item);
        }
        stage.getIcons().setAll(myLogo);
        // setAlertOwner(stage);
        stage.showAndWait();
    }

    public <T> T addToMainScene(String filename) throws Exception {
        // loading front panes of the main scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_PATH + filename));
        return loader.load();
    }

    private void setCurrentRoot(String filename) {
        try {
            setOnCloseAction(e -> defaultCloseOperations());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_PATH + filename));
            if (scene != null)
                scene.setRoot(loader.load());
            else
                scene = new Scene(loader.load());
        } catch (Exception e) {
            // debug
            // e.printStackTrace();
            showErrorMessage(Title.ON_START_ERROR, Message.ON_START_ERROR, true);
        }
    }

    public Image getLoginImage() {
        return new Image(Objects.requireNonNull(getClass().getResource(IMAGES_PATH + theme.getName() + "-uni-career-manager-image.png")).toExternalForm());
    }

    public Image getDefaultProfileImage() {
        return new Image(Objects.requireNonNull(getClass().getResource(IMAGES_PATH + "Default_Profile_Picture.png")).toExternalForm());
    }

    public FontIcon getIconTheme() {
        return themeIcon;
    }

    private void loadFonts() {
        for (String font : List.of(FONTS_PATH + "Roboto/Roboto-Regular.ttf", FONTS_PATH + "Roboto/Roboto-Bold.ttf", FONTS_PATH + "Roboto/Roboto-Italic.ttf")) {
            Font.loadFont(Objects.requireNonNull(SceneHandler.class.getResource(font)).toExternalForm(), 10);
        }
    }

    private List<String> loadCSS() {
        List<String> resources = new ArrayList<>();
        for (String style : List.of(CSS_PATH + theme.getName() + ".css", CSS_PATH + "fonts.css", CSS_PATH + "my-style.css", CSS_PATH + "my-calendar.css")) {
            String resource = Objects.requireNonNull(SceneHandler.class.getResource(style)).toExternalForm();
            resources.add(resource);
        }
        return resources;
    }

    private void setCSSForScene(Scene scene) {
        Objects.requireNonNull(scene, "Scene cannot be null");
        List<String> resources = loadCSS();
        scene.getStylesheets().clear();
        for (String resource : resources)
            scene.getStylesheets().add(resource);
    }

    private void setCSSForAlert(Alert alert) {
        Objects.requireNonNull(alert, "Alert cannot be null");
        List<String> resources = loadCSS();
        alert.getDialogPane().getStylesheets().clear();
        for (String resource : resources)
            alert.getDialogPane().getStylesheets().add(resource);
    }

    public void changeTheme() {
        if (theme == ThemeModel.DARK)
            theme = ThemeModel.LIGHT;
        else
            theme = ThemeModel.DARK;
        changedTheme();
    }

    public void changeThemeByRequest(ThemeModel requestedTheme) {
        if (theme != requestedTheme) {
            theme = requestedTheme;
            changedTheme();
        }
    }

    private void setIconThemeLiteral() {
        if (isDarkTheme())
            themeIcon.setIconLiteral("mdi2w-white-balance-sunny");
        else
            themeIcon.setIconLiteral("far-moon");
    }

    private void changedTheme() {
        setCSSForScene(scene);
        setCSSForAlert(alert);
        setIconThemeLiteral();
    }

    public boolean isDarkTheme() {
        return ThemeModel.DARK == theme;
    }

    public void showErrorMessage(String title, String text, boolean closeApplication) {
        if (closeApplication && !applicationCloseRequest) {
            if (alert.isShowing())
                alert.close();
            applicationCloseRequest = true;
            showAlert(title, text, Alert.AlertType.ERROR, errorIcon, true);
        } else
            showAlert(title, text, Alert.AlertType.ERROR, errorIcon, false);
    }

    public void showInfoMessage(String title, String text) {
        showAlert(title, text, Alert.AlertType.INFORMATION, infoIcon, false);
    }

    private void showAlert(String title, String text, Alert.AlertType type, FontIcon icon, boolean closeAfter) {
        if (!alert.isShowing()) {
            alert.setGraphic(icon);
            alert.setAlertType(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(text);
            alert.getDialogPane().setContent(null);
            alert.showAndWait();
            if (closeAfter)
                System.exit(1);
        }
    }

    public boolean showConfirmationMessage(String title, String text) {
        if (!alert.isShowing()) {
            alert.setAlertType(Alert.AlertType.CONFIRMATION);
            alert.setGraphic(confirmationIcon);
            alert.setTitle(title);
            alert.setHeaderText(text);
            alert.setContentText(null);
            alert.getDialogPane().setContent(null);
            alert.setOnShowing(event -> alert.setResult(null));
            alert.showAndWait();
            return alert.getResult() == ButtonType.OK;
        }
        return false;
    }

    public void showHelpMessage(String title, String header, String text) {
        if (!alert.isShowing()) {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setGraphic(helpIcon);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(null);
            alert.getDialogPane().setContent(new CustomHelpTextArea(text));
            alert.showAndWait();
        }
    }

    public File showFileChooser() {
        FileChooser f = new FileChooser();
        f.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
                // , new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File chosenFile = f.showOpenDialog(stage);
        if (chosenFile == null || chosenFile.isDirectory())
            return null;
        return chosenFile;
    }
}
