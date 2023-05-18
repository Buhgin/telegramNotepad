package com.boris.telegramnotepad.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "usersDataTable")
public class User {
    @Id
    @Column(name = "id")
    private Long chatId;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
    @OneToMany(mappedBy = "user")
    private List<Message> messages;
}
