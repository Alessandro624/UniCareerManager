package it.unical.demacs.informatica.unicareermanager.model;

import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.view.AgendaHandlerView;
import it.unical.demacs.informatica.unicareermanager.view.ExamHandlerView;
import it.unical.demacs.informatica.unicareermanager.view.SubjectHandlerView;
import it.unical.demacs.informatica.unicareermanager.view.TaxHandlerView;
import javafx.concurrent.Task;

public class LogoutHandler {
    private static final LogoutHandler instance = new LogoutHandler();

    private LogoutHandler() {

    }

    public static LogoutHandler getInstance() {
        return instance;
    }

    public Task<Void> logout() {
        // a task to handle logout
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            MainHandler.getInstance().logout();
            TaxHandlerView.getInstance().logout();
            ExamHandlerView.getInstance().logout();
            SubjectHandlerView.getInstance().logout();
            AgendaHandlerView.getInstance().logout();
            return null;
        });
    }
}
