package com.stake.mercariapplicationserver.controller.sample;

import com.stake.mercariapplicationserver.annotation.Authorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Authorize
public class HelloWorldRestController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}
