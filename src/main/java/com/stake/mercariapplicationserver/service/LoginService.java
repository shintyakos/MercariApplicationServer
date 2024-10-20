package com.stake.mercariapplicationserver.service;

import com.stake.mercariapplicationserver.entity.User;
import com.stake.mercariapplicationserver.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String login(String email, String password) {
        User user = userRepository.findAllByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user.getUserName();
        }

        return null;
    }
}
