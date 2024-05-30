package com.ojtportal.api.config.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JwtToPrincipal {
    public UserPrincipal convert(DecodedJWT jwt) {
        var authorityList = getClaimOrEmpty(jwt, "authority").stream()
            .map(SimpleGrantedAuthority::new)
            .toList();

        return UserPrincipal.builder()
            .uid(Long.parseLong(jwt.getSubject()))
            .email(jwt.getClaim("email").asString())
            .authorities(authorityList)
            .build();
    }

    private List<String> getClaimOrEmpty(DecodedJWT jwt, String claim) {
        if(jwt.getClaim(claim).isNull()) return List.of();
        return jwt.getClaim(claim).asList(String.class);
    }
}
