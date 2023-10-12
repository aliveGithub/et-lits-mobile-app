package org.moa.etlits.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final String ANIMAL_ID_REGEX_PATTERN = "^ET \\d{10}$";

    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isEmpty(Integer value) {
        return value == null || value <= 0;
    }

   public static boolean isEmpty(Date value) {
        return value == null;
    }

    public static boolean isValidAnimalId(String animalId) {
        if (animalId == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(ANIMAL_ID_REGEX_PATTERN);
        Matcher matcher = pattern.matcher(animalId);
        return matcher.matches();
    }

   public static boolean dateIsAfter(Date date, Date dateToCompare) {
        if (date == null || dateToCompare == null) {
            return false;
        }

       Date cDate = stripTimeFromDate(date);
       Date cDateToCompare = stripTimeFromDate(dateToCompare);
       return cDate.after(cDateToCompare);
    }

    private static Date stripTimeFromDate(Date date) {
        Calendar cDate = Calendar.getInstance();
        cDate.setTime(date);
        cDate.set(Calendar.HOUR_OF_DAY, 0);
        cDate.set(Calendar.MINUTE, 0);
        cDate.set(Calendar.SECOND, 0);
        cDate.set(Calendar.MILLISECOND, 0);
        return cDate.getTime();
    }

    public static boolean dateInFuture(Date date) {
        if (date == null) {
            return false;
        }

        Date cDate = stripTimeFromDate(date);
        Date cToday = stripTimeFromDate(new Date());
        return cDate.after(cToday);
    }
}
