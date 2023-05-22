package com.boris.telegramnotepad.controller;

import com.boris.telegramnotepad.config.BotConfig;
import com.boris.telegramnotepad.service.TelegramService;
import com.boris.telegramnotepad.util.CalendarForm;
import com.boris.telegramnotepad.util.Parse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private static String selectedTime;
    private static String text;
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
        long chatId;
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            if ("/start".equals(messageText)) {
                SendMessage message = telegramService.startCommandReceived(update);
                message.setChatId(String.valueOf(chatId));
                sendMessage(message);
                return;
            }
            if ("/all".equals(messageText)) {
                SendMessage message = telegramService.allCommandReceived(update);
                message.setChatId(String.valueOf(chatId));
                sendMessage(message);
                return;
            }
                if (Parse.isNumeric(messageText)) {
                    LocalDateTime localDateTime = Parse.parseTimeToInt(selectedTime, messageText);
                    SendMessage message = new SendMessage();
                    message.setChatId(String.valueOf(chatId));
                    message.setText("Вы выбрали время :" + localDateTime);
                    sendMessage(message);
                     SendMessage saveReminder =  telegramService.createMessageFull(chatId, text, localDateTime, update);
                      saveReminder.setChatId(String.valueOf(chatId));
                      sendMessage(saveReminder);
                     return;
                }
                else {
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
                selectedTime = day;
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
            if(telegramService.getActualReminder() != null){
                List<SendMessage> sendMessageList = telegramService.getActualReminder();
                for (SendMessage sendMessage : sendMessageList){
                      execute(sendMessage);}
            telegramService.deleteOldReminder();
            }

        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }


    //TODO добавить логирование
    //TODO добавить горячие команды
}