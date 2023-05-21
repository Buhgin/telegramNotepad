package com.boris.telegramnotepad.service;

import com.boris.telegramnotepad.entity.Reminder;
import com.boris.telegramnotepad.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TelegramService {
    private final UserService userService;
    private final MessageService messageService;

    public SendMessage startCommandReceived(Update update) {
        String firstName = update.getMessage().getFrom().getFirstName();
        long chatId = update.getMessage().getChatId();
        String text = String.format("Здравствуйте, %s! Это бот для сохранения ваших заметок." +
                "Чтобы сохранить заметку, пришлите ее мне." +
                "Чтобы просмотреть все заметки, отправьте /all." +
                "Для просмотра заметок по дате отправьте /date. Чтобы просмотреть заметки по тегу, отправьте /tag.", firstName);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        registerUser(update.getMessage());
        return message;
    }
    public SendMessage allCommandReceived(Update update) {
        SendMessage message = new SendMessage();
        String firstName = update.getMessage().getFrom().getFirstName();
        long chatId = update.getMessage().getChatId();
        if (!userService.getUserByChatIdIsEmpty(chatId)){
        List<Reminder> reminders = messageService.getAllMessagesByUserId(chatId);
        StringBuilder text = new StringBuilder();
        text.append(String.format("Здравствуйте, %s! Ваши заметки: ", firstName));
        for (Reminder reminder : reminders) {
            text.append(String.format("\n%s + дата %s", reminder.getText(), reminder.getReplyDate().toString()));
        }
        message.setChatId(String.valueOf(chatId));
        message.setText(text.toString());
        return message;}
        message.setChatId(String.valueOf(chatId));
        message.setText("Вы не зарегистрированы. Для регистрации отправьте /start.");
    return message;}

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

    public SendMessage getActualReminder() {
        Reminder reminder = messageService.actualMessage();
        if (reminder == null) {
            return null;
        } else {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(reminder.getUser().getChatId()));
            String text = String.format("Привет %s напоминаю  %s",reminder.getUser().getFirstName(),
                    reminder.getText());
            message.setText(text);

            return message;
        }
    }
    public void deleteReminder(Message reminder) {

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