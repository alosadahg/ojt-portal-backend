package com.ojtportal.api.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
public class JwtProperties {
    @Value("${security.jwt.secretKey}") private String secretKey;
}
