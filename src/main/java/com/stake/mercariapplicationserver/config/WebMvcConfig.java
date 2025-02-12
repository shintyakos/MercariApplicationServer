package com.stake.mercariapplicationserver.config;

import com.stake.mercariapplicationserver.auth.AuthorizationHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Bean
    public AuthorizationHandlerInterceptor authorizationHandlerInterceptor() {
        return new AuthorizationHandlerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationHandlerInterceptor()).addPathPatterns("/**");
    }
}
