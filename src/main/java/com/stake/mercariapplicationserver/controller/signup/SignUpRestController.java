package com.stake.mercariapplicationserver.controller.signup;

import com.stake.mercariapplicationserver.annotation.Authorize;
import com.stake.mercariapplicationserver.annotation.NonAuthorize;
import com.stake.mercariapplicationserver.model.SignUpRequest;
import com.stake.mercariapplicationserver.model.SignUpResponse;
import com.stake.mercariapplicationserver.service.SignUpService;
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
public class SignUpRestController {
    private final SignUpService signUpService;

    @PostMapping("/signup")
    @NonAuthorize
    public ResponseEntity<SignUpResponse> signUp(@Validated @RequestBody SignUpRequest signUpRequest, BindingResult result) {
        if (result.hasErrors()) {
            log.error("Validation error: {}", result.getAllErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation error");
        }

        try {
            SignUpResponse response = new SignUpResponse();
            signUpService.signUp(signUpRequest.getEmail(), signUpRequest.getPassword());
            response.setSuccess(true);

            return ResponseEntity.ok(response);
        } catch(Exception error) {
            log.error("Error: {}", error.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
}
