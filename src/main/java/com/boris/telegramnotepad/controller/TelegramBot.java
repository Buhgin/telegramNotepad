package com.boris.telegramnotepad.controller;

import com.boris.telegramnotepad.config.BotConfig;
import com.boris.telegramnotepad.repository.MessageRepository;
import com.boris.telegramnotepad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
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
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (message) {
                case "/start":
                    startCommandReceived(chatId,update.getMessage().getChat().getFirstName());
                    break;
            }
        }
    }

    private void startCommandReceived(long chatId, String firstName) {
      String text = String.format("Hello, %s! This is a bot for saving your notes. " +
              "To save a note, send it to me. " +
              "To view all notes, send /all. " +
              "To view notes by date, send /date. To view notes by tag, send /tag.", firstName);
      sendMessage(chatId, text);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        }
        catch (TelegramApiException e) {
          e.printStackTrace();
        }
    }
}