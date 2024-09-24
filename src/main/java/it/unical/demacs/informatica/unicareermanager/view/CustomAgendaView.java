package it.unical.demacs.informatica.unicareermanager.view;

import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.AgendaView;

import java.time.LocalDate;
import java.time.LocalTime;

public class CustomAgendaView extends AgendaView {

    public CustomAgendaView(CalendarSource calendarSource) {
        this.getCalendarSources().setAll(calendarSource);
        disableButtons();
        setUpStyle();
        initTime();
    }

    private void setUpStyle() {
        this.setMinSize(400, 450);
        this.getStyleClass().setAll("calendar");
    }

    private void disableButtons() {
        this.setEnableTimeZoneSupport(false);
        this.setEnableHyperlinks(false);
        this.setShowDetailsUponEntryCreation(false);
        this.setFocusTraversable(false);
        this.getListView().setFocusTraversable(false);
    }

    private void initTime() {
        this.setRequestedTime(LocalTime.now());
        this.setDate(LocalDate.now());
    }
}
