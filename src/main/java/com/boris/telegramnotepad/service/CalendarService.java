package com.boris.telegramnotepad.service;

import com.boris.telegramnotepad.util.payload.CalendarModel;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {

    public  void  saveYear(int year) {
    CalendarModel.setYear(year);
    }
    public void saveMonth(int month) {
        CalendarModel.setMonth(month);
    }
    public void saveDay(int day) {
        CalendarModel.setDay(day);
    }
    public void saveHour(int hour) {
        CalendarModel.setHour(hour);
    }
    public void saveMinute(int minute) {
        CalendarModel.setMinute(minute);
    }

}
