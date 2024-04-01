package com.caspo.settingsautomationserver.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
public class DateUtil {

    public final static String CORRECT_FORMAT = "yyyy-MM-dd HH:mm";

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

    public static String formatDateFromKafkaPushFeed(String oldDate) {
        try {
            final String KAFKA_PUSH_FEED_FORMAT = "dd/MM/yyyy HH:mm";
            SimpleDateFormat kafkaFormat = new SimpleDateFormat(KAFKA_PUSH_FEED_FORMAT);
            Date kafkaFormatDate = kafkaFormat.parse(oldDate);
            kafkaFormat.applyPattern(CORRECT_FORMAT);
            return kafkaFormat.format(kafkaFormatDate);
        } catch (ParseException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

}
