package com.boris.telegramnotepad.controller;

import com.boris.telegramnotepad.config.BotConfig;
import com.boris.telegramnotepad.service.TelegramService;
import com.boris.telegramnotepad.util.CalendarForm;
import com.boris.telegramnotepad.util.Parse;
import com.boris.telegramnotepad.util.ReminderPojo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final static Map<Long, ReminderPojo> reminderMap = new HashMap<>();
    private final CalendarForm calendarForm;
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            long chatId = message.getChatId();
            if ("/start".equals(messageText)) {
                SendMessage startMessage = telegramService.startCommandReceived(update);
                startMessage.setChatId(String.valueOf(chatId));
                sendMessage(startMessage);
                return;
            }

            if ("/all".equals(messageText)) {
                SendMessage allMessage = telegramService.allCommandReceived(update);
                allMessage.setChatId(String.valueOf(chatId));
                sendMessage(allMessage);
                return;
            }
            if (Parse.isNumeric(messageText)) {
                String selectedTime = reminderMap.get(chatId).getReplyDatePojo().toString();//reminderMap.get(chatId).getReplyDatePojo() возвращает StringBuilder
                LocalDateTime localDateTime = Parse.parseTimeToInt(selectedTime, messageText);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");
                String formattedDateTime = localDateTime.format(formatter);
                SendMessage timeSelectedMessage = new SendMessage();
                timeSelectedMessage.setChatId(String.valueOf(chatId));
                timeSelectedMessage.setText("Вы выбрали время :" + formattedDateTime);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(localDateTime);
                String text = reminderMap.get(chatId).getTextPojo();
                ReminderPojo reminderPojofinal = new ReminderPojo(text, stringBuilder);
                reminderMap.put(chatId, reminderPojofinal);

                sendMessage(timeSelectedMessage);
                SendMessage saveReminder = telegramService.createMessageFull(chatId,
                        reminderMap.get(chatId).getTextPojo(),
                        localDateTime, update);
                saveReminder.setChatId(String.valueOf(chatId));
                sendMessage(saveReminder);
                return;
            } else {
                ReminderPojo reminderPojo = reminderMap.getOrDefault(chatId, new ReminderPojo());
                reminderPojo.setTextPojo(messageText);
                reminderMap.put(chatId, reminderPojo);
                SendMessage monthCalendarMessage = calendarForm.monthCalendar();
                monthCalendarMessage.setChatId(String.valueOf(chatId));
                sendMessage(monthCalendarMessage);
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
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(day);
                LocalDate localDate = Parse.parseDateToInt(day);
                SendMessage message = calendarForm.sendTime(localDate);
                message.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                sendMessage(message);
                ReminderPojo reminderPojo = reminderMap.get(callbackQuery.getMessage().getChatId());
                reminderPojo.setReplyDatePojo(stringBuilder);
                reminderMap.put(callbackQuery.getMessage().getChatId(), reminderPojo);

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

    @Scheduled(cron = "10 *  * * * * ")
    private void sendReminder() {
        try {
            if (telegramService.getActualReminder() != null) {
                List<SendMessage> sendMessageList = telegramService.getActualReminder();
                for (SendMessage sendMessage : sendMessageList) {
                    execute(sendMessage);
                }
                telegramService.deleteOldReminder();
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    //TODO добавить логирование
    //TODO добавить горячие команды
}