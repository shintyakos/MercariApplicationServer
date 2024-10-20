package com.stake.mercariapplicationserver.service;

import com.stake.mercariapplicationserver.entity.User;
import com.stake.mercariapplicationserver.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class SignUpService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(String email, String password) {
        Date date = new Date();
        User user = new User();
        user.setUserName(UUID.randomUUID().toString());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsDeleted(false);
        userRepository.save(user);
    }
}
