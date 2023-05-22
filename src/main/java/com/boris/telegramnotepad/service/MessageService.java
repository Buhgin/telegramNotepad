package com.boris.telegramnotepad.service;

import com.boris.telegramnotepad.entity.Reminder;
import com.boris.telegramnotepad.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;


    public void saveMessage(Reminder reminder) {
        System.out.println(reminder.toString());
       messageRepository.save(reminder);

    }
    public List<Reminder> getAllMessagesByUserId(long userId) {
        return messageRepository.findByUserChatId(userId);
    }

public List<Reminder> actualMessages() {
    List<Reminder> list = messageRepository.findAll();
    List<Reminder> foundMessages = new ArrayList<>();

    for (Reminder reminder : list) {
        if  (reminder.getReplyDate().truncatedTo(ChronoUnit.MINUTES).
                isEqual(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
            foundMessages.add(reminder);
        }
    }

    return foundMessages;
}
public void deleteOldMessage(){
    List<Reminder> outdatedReminders = new ArrayList<>();
    List<Reminder> reminders = messageRepository.findAll();
    LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

    for (Reminder reminder : reminders) {
        LocalDateTime replyDate = reminder.getReplyDate().truncatedTo(ChronoUnit.MINUTES);
        if (replyDate.isBefore(now)) {
            outdatedReminders.add(reminder);
        }
    }
    messageRepository.deleteAll(outdatedReminders);
    }
    }



