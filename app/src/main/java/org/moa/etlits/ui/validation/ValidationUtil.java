package org.moa.etlits.ui.validation;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {
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
        return date.after(dateToCompare);
    }

    public static boolean dateInFuture(Date date) {
        if (date == null) {
            return false;
        }
        return date.after(new Date());
    }
}
