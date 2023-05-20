package com.boris.telegramnotepad.controller;

import com.boris.telegramnotepad.config.BotConfig;
import com.boris.telegramnotepad.service.CalendarService;
import com.boris.telegramnotepad.util.CalendarForm;
import com.boris.telegramnotepad.service.TelegramService;
import com.boris.telegramnotepad.util.Parse;
import com.boris.telegramnotepad.util.payload.CalendarModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private static String selectedTime;
    private static String text;
    private final CalendarForm calendarForm;
    private final CalendarService calendarService;

    private final TelegramService telegramService;
    private final BotConfig config;

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId;
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            if ("/save".equals(messageText)) {
                SendMessage message = calendarForm.monthCalendar();
                message.setChatId(String.valueOf(chatId));
                sendMessage(message);
            }else if (messageText instanceof String) {
                    if (Parse.isNumeric(messageText)) {
                    LocalDateTime localDateTime = Parse.parseTimeToInt(selectedTime, messageText);
                    SendMessage message = new SendMessage();
                    message.setChatId(String.valueOf(chatId));
                    message.setText("You have selected time: " + localDateTime);
                    sendMessage(message);
                    telegramService.createMessageFull(chatId, text, localDateTime,update);
                    return;
                }
                text = messageText;
                SendMessage message = calendarForm.monthCalendar();
                message.setChatId(String.valueOf(chatId));
                sendMessage(message);
            }

        }

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            if (data.startsWith("CALENDAR_MONTH_")) {
                String previousMonth = data.substring("CALENDAR_MONTH_".length());
                SendMessage message = calendarForm.sendDay(Integer.parseInt(previousMonth));
                message.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                sendMessage(message);
            }
            if (data.startsWith("CALENDAR_DAY_")) {
                String day = data.substring("CALENDAR_DAY_".length());
                LocalDate localDate = Parse.parseDateToInt(day);
                SendMessage message = calendarForm.sendTime(localDate);
                message.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                sendMessage(message);
                selectedTime= day;
            }
        }
    }

    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}