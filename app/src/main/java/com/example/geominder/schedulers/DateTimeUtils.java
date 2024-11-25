package com.example.geominder.schedulers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    public static long parseDateTimeToMillis(String dateTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = sdf.parse(dateTime);
            return date != null ? date.getTime() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
