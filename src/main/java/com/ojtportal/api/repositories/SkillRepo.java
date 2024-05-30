package com.ojtportal.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.Skill;

@Repository
public interface SkillRepo extends JpaRepository<Skill, Integer>{
    Skill findBySkillNameAndDomain(String skill, String domain);
}
