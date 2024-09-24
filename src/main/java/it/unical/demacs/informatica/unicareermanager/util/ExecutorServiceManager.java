package it.unical.demacs.informatica.unicareermanager.util;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ExecutorServiceManager {
    // helper class for service/task management
    private static final ExecutorServiceManager instance = new ExecutorServiceManager();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private ExecutorServiceManager() {
    }

    public static ExecutorServiceManager getInstance() {
        return instance;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public <T> Task<T> asyncCall(Callable<T> callable) {
        // using an asyncronized call on the Callable parameter
        return new Task<>() {
            @Override
            protected T call() throws Exception {
                return callable.call();
            }
        };
    }

    public void close() {
        executorService.shutdown();
        scheduledExecutorService.shutdown();
    }
}
