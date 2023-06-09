package com.boris.telegramnotepad.repository;

import com.boris.telegramnotepad.entity.Reminder;
import com.boris.telegramnotepad.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByUserChatId(long chatId);

}
