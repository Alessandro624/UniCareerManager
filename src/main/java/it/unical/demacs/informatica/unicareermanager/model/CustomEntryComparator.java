package it.unical.demacs.informatica.unicareermanager.model;

import com.calendarfx.model.Entry;

import java.util.Comparator;

public class CustomEntryComparator implements Comparator<Entry<?>> {
    // implementation of a custom entry comparator
    @Override
    public int compare(Entry<?> o1, Entry<?> o2) {
        return o1.getId().compareTo(o2.getId());
    }
}
