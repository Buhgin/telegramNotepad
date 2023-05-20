package com.boris.telegramnotepad.util.payload;

import lombok.Data;

@Data
public  class CalendarModel {
private static int year;
private static int month;
private static int day;
private static int hour;
private static int minute;

    public static void setYear(int year) {
        CalendarModel.year = year;
    }

    public static void setMonth(int month) {
        CalendarModel.month = month;
    }

    public static void setDay(int day) {
        CalendarModel.day = day;
    }

    public static void setHour(int hour) {
        CalendarModel.hour = hour;
    }

    public static void setMinute(int minute) {
        CalendarModel.minute = minute;
    }
}
