package com.boris.telegramnotepad.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Parse {
    public static LocalDate parseDateToInt(String data) {
        String[] parts = data.split("_");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

        return LocalDate.of(year, month+1, day);
    }
    public static LocalDateTime parseTimeToInt(String data, String time) {
        String[] parts = data.split("_");
        String[] partsTime = time.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        int hour = Integer.parseInt(partsTime[0]);
        int minute = Integer.parseInt(partsTime[1]);

        return LocalDateTime.of(year, month+1, day, hour, minute);
    }
    public static boolean isNumeric(String strNum) {
        String[] partsTime = strNum.split("-");
        try {
            for (String part : partsTime) {
                Integer.parseInt(part);
            }

        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


}
