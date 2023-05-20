package com.boris.telegramnotepad.service;

import com.boris.telegramnotepad.entity.User;
import com.boris.telegramnotepad.exeption.ResourceNotFoundException;
import com.boris.telegramnotepad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
   public void createUser(User user) {
        userRepository.save(user);
    }
  public User getUserByChatId(long chatId) {
        return userRepository.findById(chatId).orElseThrow(()
                -> new ResourceNotFoundException("User", "chatId", chatId));
    }
    public boolean getUserByChatIdIsEmpty(long chatId) {
        return userRepository.findById(chatId).isEmpty();
    }
}
