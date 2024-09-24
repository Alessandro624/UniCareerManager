package it.unical.demacs.informatica.unicareermanager.view;

import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;

import java.time.LocalDate;
import java.time.LocalTime;


public class CustomCalendarView extends CalendarView {

    public CustomCalendarView(CalendarSource calendarSource) {
        this.getCalendarSources().setAll(calendarSource);
        setPages();
        disableButtons();
        setUpStyle();
        initTime();
    }

    private void setPages() {
        this.getAvailablePages().setAll(Page.DAY, Page.WEEK);
        this.showWeekPage();
    }

    private void initTime() {
        this.setRequestedTime(LocalTime.now());
        this.setDate(LocalDate.now());
    }

    private void setUpStyle() {
        this.getStyleClass().add("calendar");
        this.setMinSize(800, 450);
    }

    private void disableButtons() {
        this.setEnableTimeZoneSupport(false);
        this.setShowPrintButton(false);
        this.setShowAddCalendarButton(false);
        this.setShowPageToolBarControls(false);
        this.setShowSourceTrayButton(false);
        this.setShowSourceTray(false);
        this.setEnableHyperlinks(false);
    }
}
