package it.unical.demacs.informatica.unicareermanager.view;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarEvent;
import javafx.application.Platform;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class AgendaHandlerView {
    private static final AgendaHandlerView instance = new AgendaHandlerView();

    private final CustomCalendarSource calendarSource = new CustomCalendarSource("Eventi");

    private final List<EventHandler<CalendarEvent>> handlers = new ArrayList<>();

    private boolean loaded = false;

    private AgendaHandlerView() {
    }

    public static AgendaHandlerView getInstance() {
        return instance;
    }

    public CustomCalendarSource getCalendarSource() {
        return calendarSource;
    }

    private void addHandlerToCalendars(EventHandler<CalendarEvent> handler) {
        calendarSource.getCalendars().stream().map(calendar -> (Calendar<?>) calendar).forEach(calendar -> calendar.addEventHandler(handler));
    }

    private void removeHandlerToCalendars(EventHandler<CalendarEvent> handler) {
        calendarSource.getCalendars().stream().map(calendar -> (Calendar<?>) calendar).forEach(calendar -> calendar.removeEventHandler(handler));
    }

    public void addEventHandler(EventHandler<CalendarEvent> handler) {
        handlers.add(handler);
        if (loaded)
            addHandlerToCalendars(handler);
    }

    public void loadCompleted() {
        if (!loaded) {
            handlers.forEach(this::addHandlerToCalendars);
            loaded = true;
        }
    }

    public void clearAllEntries() {
        calendarSource.getCalendars().forEach(Calendar::clear);
    }

    public void logout() {
        handlers.forEach(this::removeHandlerToCalendars);
        handlers.clear();
        loaded = false;
        Platform.runLater(this::clearAllEntries);
    }
}
