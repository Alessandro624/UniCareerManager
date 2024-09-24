package it.unical.demacs.informatica.unicareermanager.controller;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.Entry;
import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.CustomEntryComparator;
import it.unical.demacs.informatica.unicareermanager.model.DBConnection;
import it.unical.demacs.informatica.unicareermanager.model.EntrySet;
import it.unical.demacs.informatica.unicareermanager.model.MainHandler;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.view.AgendaHandlerView;
import it.unical.demacs.informatica.unicareermanager.view.CustomCalendarView;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class AgendaController {

    @FXML
    private VBox centralBox;

    @FXML
    private Label agendaLabel;

    @FXML
    private Button deleteAllButton;

    @FXML
    private ProgressIndicator progressIndicator;

    private final CustomCalendarView calendarView = new CustomCalendarView(AgendaHandlerView.getInstance().getCalendarSource());

    private final EntrySet entries = new EntrySet(new TreeSet<>(new CustomEntryComparator()));

    private void handleBinding(Task<?> task) {
        deleteAllButton.disableProperty().unbind();
        calendarView.disableProperty().unbind();
        progressIndicator.progressProperty().unbind();
        progressIndicator.visibleProperty().unbind();
        deleteAllButton.disableProperty().bind(task.runningProperty());
        calendarView.disableProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
        progressIndicator.visibleProperty().bind(task.runningProperty());
    }

    @FXML
    void handleDeleteAllButton(ActionEvent event) {
        if (entries.entrySet().isEmpty()) {
            SceneHandler.getInstance().showErrorMessage(Title.AGENDA_ERROR, Message.EMPTY_EVENTS, false);
            return;
        }
        if (SceneHandler.getInstance().showConfirmationMessage(Title.REMOVE_CONFIRMATION, Message.DELETE_ALL_EVENT_CONFIRMATION)) {
            Task<Boolean> task = DBConnection.getInstance().deleteAllEntry(MainHandler.getInstance().getUser());
            handleBinding(task);
            task.setOnSucceeded(e -> {
                if (task.getValue()) {
                    entries.entrySet().clear();
                    AgendaHandlerView.getInstance().clearAllEntries();
                } else
                    handleDeleteErrors(task, null, null, false);
                calendarView.refreshData();
            });
            task.setOnFailed(e -> handleDeleteErrors(task, null, null, true));
            ExecutorServiceManager.getInstance().getExecutorService().submit(task);
        }
    }

    private void handleInsertUpdateErrors(Task<?> ignoredTask, Entry<?> entry, boolean ignoredFailed, boolean inserted) {
        // if (ignoredFailed)
        //      debug
        //      ignoredTask.getException().printStackTrace();
        if (entry != null)
            entry.setCalendar(null);
        SceneHandler.getInstance().showErrorMessage(Title.AGENDA_ERROR, inserted ? Message.EVENT_INSERT_ERROR : Message.EVENT_UPDATE_ERROR, false);
    }

    private void handleDeleteErrors(Task<?> ignoredTask, Entry<?> entry, Calendar<?> oldCalendar, boolean ignoredFailed) {
        // if (ignoredFailed)
        //        debug
        //        ignoredTask.getException().printStackTrace();
        if (entry != null && oldCalendar != null)
            entry.setCalendar(oldCalendar);
        SceneHandler.getInstance().showErrorMessage(Title.AGENDA_ERROR, Message.EVENT_DELETE_ERROR, false);
    }

    private void insertEntry(CalendarEvent evt) {
        // inserting an event
        Entry<?> entry = evt.getEntry();
        Calendar<?> calendar = evt.getCalendar();
        Task<Boolean> task = DBConnection.getInstance().insertEntry(entry, calendar.getName(), MainHandler.getInstance().getUser());
        handleBinding(task);
        task.setOnSucceeded(e -> {
            if (task.getValue())
                entries.entrySet().add(entry);
            else
                handleInsertUpdateErrors(task, entry, false, true);
            // to avoid entry size problems
            calendarView.applyCss();
        });
        task.setOnFailed(e -> handleInsertUpdateErrors(task, entry, true, true));
        ExecutorServiceManager.getInstance().getExecutorService().submit(task);
    }

    private void deleteEntry(CalendarEvent evt) {
        // removing an event
        Entry<?> entry = evt.getEntry();
        Calendar<?> calendar = evt.getOldCalendar();
        Task<Boolean> task = DBConnection.getInstance().removeEntry(entry.getId(), MainHandler.getInstance().getUser());
        handleBinding(task);
        task.setOnSucceeded(e -> {
            if (task.getValue())
                entries.entrySet().remove(entry);
            else
                handleDeleteErrors(task, entry, calendar, false);
            calendarView.refreshData();
        });
        task.setOnFailed(e -> handleDeleteErrors(task, entry, calendar, true));
        ExecutorServiceManager.getInstance().getExecutorService().submit(task);
    }

    private void updateEntry(CalendarEvent evt) {
        // updating an event
        Entry<?> entry = evt.getEntry();
        Calendar<?> calendar = evt.getCalendar();
        Task<Boolean> task = DBConnection.getInstance().updateEntry(entry, calendar.getName(), MainHandler.getInstance().getUser());
        handleBinding(task);
        task.setOnSucceeded(e -> {
            if (!task.getValue())
                handleInsertUpdateErrors(task, entry, false, false);
            // to avoid entry size problems
            calendarView.applyCss();
        });
        task.setOnFailed(e -> handleInsertUpdateErrors(task, entry, true, false));
        ExecutorServiceManager.getInstance().getExecutorService().submit(task);
    }

    private boolean requiresInsertEntry(CalendarEvent evt) {
        // can be replaced by evt.isEntryAdded()
        // checking an insert event request
        if (evt != null && evt.getEventType().equals(CalendarEvent.ENTRY_CALENDAR_CHANGED) && evt.getCalendar() != null && evt.getOldCalendar() == null) {
            Entry<?> entry = evt.getEntry();
            if (entry != null && !entry.isRecurrence()) {
                return !entries.entrySet().contains(entry);
            }
        }
        return false;
    }

    private boolean requiresDeleteEntry(CalendarEvent evt) {
        // can be replaced by evt.isEntryRemoved()
        // checking a delete event request
        if (evt != null && evt.getEventType().equals(CalendarEvent.ENTRY_CALENDAR_CHANGED) && evt.getCalendar() == null && evt.getOldCalendar() != null) {
            Entry<?> entry = evt.getEntry();
            if (entry != null && !entry.isRecurrence()) {
                return entries.entrySet().contains(entry);
            }
        }
        return false;
    }

    private boolean requiresUpdateEntry(CalendarEvent evt) {
        // checking an update event request
        if (evt != null && evt.getEventType().getSuperType().equals(CalendarEvent.ENTRY_CHANGED)) {
            Entry<?> entry = evt.getEntry();
            if (entry != null && !entry.isRecurrence()) {
                return entries.entrySet().contains(entry);
            }
        }
        return false;
    }

    public void handleEvents(CalendarEvent evt) {
        // handling events
        if (requiresInsertEntry(evt)) {
            insertEntry(evt);
        } else if (requiresDeleteEntry(evt)) {
            deleteEntry(evt);
        } else if (requiresUpdateEntry(evt)) {
            updateEntry(evt);
        }
    }

    private void loadEntries() {
        // loading events
        Task<List<Entry<?>>> task = DBConnection.getInstance().getEntries(MainHandler.getInstance().getUser());
        handleBinding(task);
        task.setOnSucceeded(e -> {
            List<Entry<?>> entryList = task.getValue();
            entries.entrySet().addAll(entryList);
            AgendaHandlerView.getInstance().addEventHandler(this::handleEvents);
            AgendaHandlerView.getInstance().loadCompleted();
        });
        task.setOnFailed(e -> {
            // debug
            // task.getException().printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.AGENDA_ERROR, Message.LOAD_ENTRY_ERROR, true);
        });
        ExecutorServiceManager.getInstance().getExecutorService().submit(task);
    }

    private void updateTime() {
        // updating timeline
        Platform.runLater(() -> {
            calendarView.setToday(LocalDate.now());
            calendarView.setTime(LocalTime.now());
        });
    }

    private void initAgenda() {
        // initializing agenda
        centralBox.getChildren().add(calendarView);
        VBox.setVgrow(calendarView, Priority.ALWAYS);
        ExecutorServiceManager.getInstance().getScheduledExecutorService().scheduleAtFixedRate(this::updateTime, 0, 20, TimeUnit.SECONDS);
    }

    private void setUpStyle() {
        // setting up style
        agendaLabel.getStyleClass().add("bold-label");
    }

    @FXML
    void initialize() {
        setUpStyle();
        initAgenda();
        loadEntries();
    }
}
