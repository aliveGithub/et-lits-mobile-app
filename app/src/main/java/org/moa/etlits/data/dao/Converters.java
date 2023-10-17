package org.moa.etlits.data.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public Set<String> fromString(String value) {
        return new HashSet<>(Arrays.asList(value.split(",")));
    }

    @TypeConverter
    public String fromList(Set<String> list) {
        StringBuilder str = new StringBuilder();
        for (String s : list) {
            str.append(s).append(",");
        }
        return str.toString();
    }
}
