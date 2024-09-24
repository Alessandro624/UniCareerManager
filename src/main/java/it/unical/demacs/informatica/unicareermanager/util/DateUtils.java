package it.unical.demacs.informatica.unicareermanager.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    // helper methods for date formatting (LocalDate and LocalDateTime)
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static String formatDate(LocalDate date) {
        return date != null ? dateFormatter.format(date) : "";
    }

    public static LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, dateFormatter);
    }

    public static String formatDateTime(LocalDateTime date) {
        return date != null ? dateTimeFormatter.format(date) : "";
    }

    public static LocalDateTime parseDateTime(String dateString) {
        return LocalDateTime.parse(dateString, dateTimeFormatter);
    }
}
