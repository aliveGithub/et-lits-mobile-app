package org.moa.etlits.utils;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
    public static long daysBetweenDates(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        long diffInMilliseconds = endDate.getTime() - startDate.getTime();
        return diffInMilliseconds / (1000 * 60 * 60 * 24);
    }

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }
}
