package com.ojtportal.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.Supervisor;

@Repository
public interface SupervisorRepo extends JpaRepository<Supervisor, Long>{
    Supervisor findByUser_Email(String email);
}
