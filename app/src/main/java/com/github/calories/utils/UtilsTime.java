package com.github.calories.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class UtilsTime {

    public static final String SQL_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DAY_PATTERN = "yyyy-MM-dd";
    public static final String SIMPLE_PATTERN = "HH:mm";

    public static Calendar toCalendar(String str, String pattern) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        cal.setTime(sdf.parse(str));// all done
        return cal;
    }

    public static String formatSQL(Instant instant_start, String timezone) {
        return format(instant_start,timezone, SQL_PATTERN);
    }

    public static String format(Instant instant_start, String timezone, String pattern) {
        ZoneId zone = ZoneId.of(timezone);
        ZonedDateTime date_start = instant_start.atZone(zone);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(pattern);
        return date_start.format(formatter2);
    }
}
