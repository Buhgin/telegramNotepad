package com.boris.telegramnotepad.service;

import com.boris.telegramnotepad.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class messageService {
    private final MessageRepository messageRepository;
}
