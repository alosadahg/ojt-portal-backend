package com.ojtportal.api.config.security;

import java.time.Duration;
import java.time.Instant;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtIssuer {
    private final JwtProperties properties;

    public String issue(Request request) {
        var now = Instant.now();

        return JWT.create()
                .withSubject(String.valueOf(request.userid))
                .withIssuedAt(now)
                .withExpiresAt(now.plus(Duration.ofDays(10)))
                .withClaim("email", request.getEmail())
                .withClaim("authority", request.getRoles())
                .sign(Algorithm.HMAC256(properties.getSecretKey()));
    }

    @Getter
    @Builder
    public static class Request {
        private final Long userid;
        private final String email;
        private final List<String> roles;
    }
}
