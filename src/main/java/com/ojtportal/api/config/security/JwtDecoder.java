package com.ojtportal.api.config.security;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtDecoder {
    private final JwtProperties properties;

    public DecodedJWT decode(String token) {
        var decoded = JWT.require(Algorithm.HMAC256(properties.getSecretKey())).build().verify(token);
        return (DecodedJWT) decoded;
    }
}
