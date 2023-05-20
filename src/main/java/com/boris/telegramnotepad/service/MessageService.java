package com.boris.telegramnotepad.service;

import com.boris.telegramnotepad.entity.Reminder;
import com.boris.telegramnotepad.entity.User;
import com.boris.telegramnotepad.exeption.ResourceNotFoundException;
import com.boris.telegramnotepad.repository.MessageRepository;
import com.boris.telegramnotepad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public void saveMessage(Reminder reminder) {
        System.out.println(reminder.toString());
       messageRepository.save(reminder);

    }
    public List<Reminder> getAllMessagesByUserId(long userId) {
        return messageRepository.findByIdOrderByReplyDateAsc(userId);
    }
   public List<Reminder> getAllMessagesByReplyDate(long userId, LocalDate replyDate ) {
        LocalTime startOfDay = LocalTime.MIN;
        LocalTime endOfDay = LocalTime.MAX;
        LocalDateTime start = LocalDateTime.of(replyDate, startOfDay);
        LocalDateTime end = LocalDateTime.of(replyDate, endOfDay);
        User user = userRepository.findById(userId).orElseThrow(()
                -> new ResourceNotFoundException("User", "chatId", userId));
        return messageRepository.findAllByUserAndReplyDateBetween(user, start, end);
    }
    public void deleteMessage(Reminder reminder) {
        messageRepository.delete(reminder);
    }
   /* private void actualMessage() {
        while (true) {
            List<Message> list = messageRepository.findAll();
           LocalDateTime.now();
            if (list.size() == 0) {
                return;
            }
            for (Message message : list) {
                if (message.getReplyDate().isEqual(LocalDateTime.now())) {
                    String messageText = message.getText();
                    long chatId = message.getChatId();
                    sendMessage(messageText, chatId);
                }
            }
        } //TODO: 1. Добавить таймер, который будет проверять время отправки сообщения
             //TODO: решить откуда будет работать данный метод
    }*/

}
