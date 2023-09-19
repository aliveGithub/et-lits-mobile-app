package org.moa.etlits.utils;

import java.util.Date;

public class DateUtils {
    public static long daysBetweenDates(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        long diffInMilliseconds = endDate.getTime() - startDate.getTime();
        return diffInMilliseconds / (1000 * 60 * 60 * 24);
    }
}
