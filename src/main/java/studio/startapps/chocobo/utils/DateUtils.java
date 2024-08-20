package studio.startapps.chocobo.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface DateUtils {

    static LocalDateTime now() {
        return LocalDateTime.now();
    }

    static LocalDate today() {
        return LocalDate.now();
    }

    static String formatDateTimeISO(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        return localDateTime.format(dateTimeFormatter);
    }

    static LocalDateTime parseDateTimeISO(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);
    }
}
