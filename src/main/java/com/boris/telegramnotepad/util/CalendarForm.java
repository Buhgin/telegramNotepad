package com.boris.telegramnotepad.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Component
public class CalendarForm {
    public SendMessage monthCalendar() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        int monthsPerRow = 3;
        int totalMonths = 12;
        int rows = (int) Math.ceil((double) totalMonths / monthsPerRow);

        int monthCount = 1;
        for (int i = 0; i < rows; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < monthsPerRow; j++) {
                if (monthCount <= totalMonths) {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(getMonthName(monthCount - 1));
                    button.setCallbackData("CALENDAR_MONTH_" + monthCount);
                    row.add(button);
                    monthCount++;
                }
            }
            keyboard.add(row);
        }

        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();
        message.setText("Выберите месяц:");
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    public SendMessage sendDay(int entryMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, entryMonth - 1);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row ;


        String[] weekDays = new DateFormatSymbols(Locale.getDefault()).getShortWeekdays();
        row = new ArrayList<>();
        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(weekDays[i]);
            button.setCallbackData("CALENDAR_IGNORE");
            row.add(button);
        }
        keyboard.add(row);

        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int numRows = (int) Math.ceil((daysInMonth + dayOfWeek - 1) / 7.0);

        row = new ArrayList<>();
        for (int i = 1; i <= numRows * 7; i++) {
            if (i >= dayOfWeek && i < dayOfWeek + daysInMonth) {
                int dayOfMonth = i - dayOfWeek + 1;
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(Integer.toString(dayOfMonth));
                button.setCallbackData("CALENDAR_DAY_" + calendar.get(Calendar.YEAR) + "-" + month + "-" + dayOfMonth);
                row.add(button);
            } else {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(" ");
                button.setCallbackData("CALENDAR_IGNORE");
                row.add(button);
            }

            if (i % 7 == 0) {
                keyboard.add(row);
                row = new ArrayList<>();
            }
        }

        keyboardMarkup.setKeyboard(keyboard);
        SendMessage message = new SendMessage();
        message.setText("Выберите дату:");
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }
    public SendMessage sendTime(LocalDate localDate){
        SendMessage message = new SendMessage();
        message.setText(localDate.toString() +" Укажите время в формате HH-mm");
      return message;
    }
    private String getMonthName(int month) {
        DateFormatSymbols symbols = new DateFormatSymbols();
        String[] monthNames = symbols.getMonths();
        return monthNames[month];
    }
}
