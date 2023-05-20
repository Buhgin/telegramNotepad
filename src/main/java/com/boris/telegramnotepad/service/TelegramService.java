package com.boris.telegramnotepad.service;

import com.boris.telegramnotepad.entity.Reminder;
import com.boris.telegramnotepad.entity.User;
import com.boris.telegramnotepad.exeption.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


@Service
@RequiredArgsConstructor
public class TelegramService {
    private final UserService userService;
    private final MessageService messageService;

    public SendMessage startCommandReceived(Update update) {
        String firstName = update.getMessage().getFrom().getFirstName();
        long chatId = update.getMessage().getChatId();
        String text = String.format("Hello, %s! This is a bot for saving your notes. " +
                "To save a note, send it to me. " +
                "To view all notes, send /all. " +
                "To view notes by date, send /date. To view notes by tag, send /tag.", firstName);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        registerUser(update.getMessage());
        return message;
    }

    public SendMessage createMessageFull(long chatId, String text, LocalDateTime replyDate, Update update) {
        registerUser(update.getMessage());
        User user = userService.getUserByChatId(chatId);
        Reminder reminder = new Reminder();
        reminder.setText(text);
        reminder.setReplyDate(replyDate);
        reminder.setUser(user);
        messageService.saveMessage(reminder);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

   return message;
    }




    private LocalDateTime parseDate(String s) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return LocalDateTime.parse(s, formatter);
    }

    private void registerUser(Message msg) {
        if (userService.getUserByChatIdIsEmpty(msg.getChatId())) {
            Long chatId = msg.getChatId();
            Chat chat = msg.getChat();
            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            userService.createUser(user);
        }
    }

}