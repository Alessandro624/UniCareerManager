package it.unical.demacs.informatica.unicareermanager.model;

import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import javafx.concurrent.Task;

import java.util.HashMap;
import java.util.Map;

public class LoadCentralPanesHandler {

    private static final LoadCentralPanesHandler instance = new LoadCentralPanesHandler();

    private LoadCentralPanesHandler() {

    }

    public static LoadCentralPanesHandler getInstance() {
        return instance;
    }

    public <T> Task<Map<CentralPanesModel.PaneType, T>> loadPanes() {
        // a task to load all center panes
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            Map<CentralPanesModel.PaneType, T> panes = new HashMap<>();
            for (CentralPanesModel.PaneType p : CentralPanesModel.PaneType.values()) {
                panes.put(p, SceneHandler.getInstance().addToMainScene(CentralPanesModel.getPaneFileName(p)));
            }
            return panes;
        });
    }
}
