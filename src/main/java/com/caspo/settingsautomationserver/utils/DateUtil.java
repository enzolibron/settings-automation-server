package com.caspo.settingsautomationserver.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
public class DateUtil {

    public static String formatDate(Date dd, String format) {
        DateFormat RecordDate = new SimpleDateFormat(format);
        String newdate = RecordDate.format(dd);
        return newdate;
    }

    public static Date add12HoursToDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, 12);
        return calendar.getTime();
    }

    public static Date minusHoursToDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, -hours);
        return calendar.getTime();
    }

}
