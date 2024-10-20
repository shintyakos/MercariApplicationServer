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

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        LoginResponse response = new LoginResponse();

        if (result.hasErrors()) {
            log.error("Validation error: {}", result);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String username = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
        if (username != null && !username.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 24);
            log.error("expiration Date: {}", sdf.format(calendar.getTime()));

            String token = authorizationHandlerInterceptor.generateToken(username, calendar.getTime(), authorizationHandlerInterceptor.getSecretKey());
            log.error("token: {}", token);

            response.setToken(token);
        }

        return ResponseEntity.ok(response);
    }
}
