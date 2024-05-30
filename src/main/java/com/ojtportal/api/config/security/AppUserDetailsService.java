package com.ojtportal.api.config.security;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ojtportal.api.entity.UserEntity;
import com.ojtportal.api.model.Role;
import com.ojtportal.api.repositories.UserRepo;

@Configuration
public class AppUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = Optional.of(userRepo.findByEmail(username)).orElseThrow();
        List<SimpleGrantedAuthority> authList = getAuthList(user);
        return UserPrincipal.builder()
                .uid(user.getUid())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authList)
                .build();
    }
    
    private List<SimpleGrantedAuthority> getAuthList(UserEntity user) {
        Role r = user.getAccountType();
        String s = "ROLE_" + user.getUserStatus();
        return List.of(new SimpleGrantedAuthority(r.toString()), new SimpleGrantedAuthority(s));
    }
}
