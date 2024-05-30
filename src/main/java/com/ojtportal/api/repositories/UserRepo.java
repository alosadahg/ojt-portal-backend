package com.ojtportal.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.UserEntity;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    List<UserEntity> findByUserStatus(String user_status);
    List<UserEntity> findByAccountType(String accountType);
    UserEntity findByEmailAndPassword(String email, String password);
}
