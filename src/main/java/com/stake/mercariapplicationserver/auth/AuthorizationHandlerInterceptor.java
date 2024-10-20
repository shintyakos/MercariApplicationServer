package com.stake.mercariapplicationserver.auth;

import com.stake.mercariapplicationserver.annotation.Authorize;
import com.stake.mercariapplicationserver.annotation.NonAuthorize;
import com.stake.mercariapplicationserver.entity.User;
import com.stake.mercariapplicationserver.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class AuthorizationHandlerInterceptor implements HandlerInterceptor {

    private final UUID uuid = UUID.randomUUID();

    @Getter
    private final String secretKey = uuid.toString();

    @Autowired
    private UserRepository userRepository;

    public String generateToken(String id, String key) {
        long expirationTimeInMilliseconds = 3600000; // 1時間

        return Jwts
                .builder()
                .setSubject(id)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMilliseconds))
                .signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public String getIdFromToken(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean authorize(HttpServletRequest request) {
        // Authorizationの値を取得
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isEmpty()) {
            return false;
        }

        // Bearer tokenの形式であることをチェック
        if (!authorization.startsWith("Bearer ")) {
            log.error("Authorization header is invalid");
            return false;
        }

        // トークンを取得しidを取得する
        String token = authorization.substring(7);
        try {
            String id = getIdFromToken(token, secretKey);
            User user = userRepository.findByUsername(id);
            if (user == null) {
                return false;
            }

            log.info("User {} is authorized", user.getUserName());
            request.setAttribute("username", user.getUserName());
            return true;
        } catch(Exception error) {
            log.error("トークンの解析に失敗しました。 {}", error.getMessage());
            return false;
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        Method method = ((HandlerMethod) handler).getMethod();
        if (AnnotationUtils.findAnnotation(method, NonAuthorize.class) != null) {
            return true;
        }

        Class<?> controller = method.getDeclaringClass();
        if (AnnotationUtils.findAnnotation(controller, Authorize.class) != null || AnnotationUtils.findAnnotation(method, Authorize.class) != null) {
            if (!authorize(request)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }

        return true;
    }
}
