package com.ojtportal.api.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.CommonSkill;

@Repository
public interface CommonSkillsRepo extends JpaRepository<CommonSkill, Integer> {
    CommonSkill findBySkillAndDomain(String skill, String domain);
    List<CommonSkill> findByDomain(String domain);
}
