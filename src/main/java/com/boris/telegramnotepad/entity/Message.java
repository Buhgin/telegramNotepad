package com.boris.telegramnotepad.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "messages")
public class Message {
    @Id
    @Column(name = "id")
    private Long chatId;

    @Column(name = "text")
    private String text;
    @Column(name = "reply_date")
    private LocalDateTime replyDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
