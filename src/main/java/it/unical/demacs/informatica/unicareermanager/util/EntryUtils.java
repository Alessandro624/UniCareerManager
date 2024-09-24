package it.unical.demacs.informatica.unicareermanager.util;

import com.calendarfx.model.Entry;
import it.unical.demacs.informatica.unicareermanager.view.AgendaHandlerView;

import java.time.ZoneId;

public class EntryUtils {
    public static Entry<?> createEntry(String id, String title, String location, boolean fullday, String start, String end, String rule, String calendar) {
        // helper method for entry creation
        Entry<?> entry = new Entry<>();
        entry.setId(id);
        entry.setTitle(title);
        entry.setLocation(location);
        entry.setFullDay(fullday);
        entry.setInterval(DateUtils.parseDateTime(start), DateUtils.parseDateTime(end));
        entry.setZoneId(ZoneId.systemDefault());
        entry.setRecurrenceRule(rule);
        AgendaHandlerView.getInstance().getCalendarSource().getCalendars().stream().filter(toFindCalendar -> toFindCalendar.getName().equals(calendar)).findFirst().ifPresent(entry::setCalendar);
        return entry;
    }
}
