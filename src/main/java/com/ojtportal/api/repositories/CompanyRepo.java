package com.ojtportal.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.Company;

@Repository
public interface CompanyRepo extends JpaRepository<Company, Integer> {
    Company findByCompanyName(String company_name);
}
