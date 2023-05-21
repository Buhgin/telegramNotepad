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
import java.time.temporal.ChronoUnit;
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
        return messageRepository.findByUserChatId(userId);
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
    public void deleteMessage(long id) {

        messageRepository.deleteById(id);
    }
   public Reminder actualMessage() {
            List<Reminder> list = messageRepository.findAll();
           LocalDateTime.now();
            for (Reminder reminder : list) {
                if (reminder.getReplyDate().truncatedTo(ChronoUnit.MINUTES).
                        isEqual(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {

                    return reminder;
                }
            }
        return null;}
    //TODO: list of reminders
    }



