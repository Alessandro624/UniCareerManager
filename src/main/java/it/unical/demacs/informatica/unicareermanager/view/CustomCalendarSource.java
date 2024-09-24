package it.unical.demacs.informatica.unicareermanager.view;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import it.unical.demacs.informatica.unicareermanager.costants.Settings;

public class CustomCalendarSource extends CalendarSource {

    public CustomCalendarSource(String name) {
        super(name);
        addCalendars();
    }

    private void addCalendars() {
        for (int i = 0; i < Settings.CALENDARS_NAME.length; i++) {
            Calendar<?> calendar = new Calendar<>(Settings.CALENDARS_NAME[i]);
            calendar.setStyle("style" + (i + 1));
            this.getCalendars().add(calendar);
        }
    }
}
