package com.ojtportal.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.UnregisteredIntern;

@Repository
public interface UnregisteredInternRepo extends JpaRepository<UnregisteredIntern, String> {
}
