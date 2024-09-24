package it.unical.demacs.informatica.unicareermanager;

import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            SceneHandler.getInstance().init(stage);
        } catch (Exception e) {
            // debug
            // e.printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.ON_START_ERROR, Message.ON_START_ERROR, true);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
