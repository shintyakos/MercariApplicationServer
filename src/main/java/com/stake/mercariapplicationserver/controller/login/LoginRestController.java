package com.stake.mercariapplicationserver.controller.login;

import com.stake.mercariapplicationserver.annotation.Authorize;
import com.stake.mercariapplicationserver.annotation.NonAuthorize;
import com.stake.mercariapplicationserver.auth.AuthorizationHandlerInterceptor;
import com.stake.mercariapplicationserver.model.LoginRequest;
import com.stake.mercariapplicationserver.model.LoginResponse;
import com.stake.mercariapplicationserver.service.LoginService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@Slf4j
@Authorize
public class LoginRestController {
    private final LoginService loginService;
    private final AuthorizationHandlerInterceptor authorizationHandlerInterceptor;

    @PostMapping("/login")
    @NonAuthorize
    public ResponseEntity<LoginResponse> login(@Validated @RequestBody LoginRequest loginRequest, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Validation error occurred: {}", result.getAllErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        }

        try {
            String username = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
            String token = authorizationHandlerInterceptor.generateToken(username, authorizationHandlerInterceptor.getSecretKey());

            log.info("Login successful for user: {}", username);

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            return ResponseEntity.ok(response);
        } catch (Exception error) {
            log.warn("Login failed: {}", error.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }
}
