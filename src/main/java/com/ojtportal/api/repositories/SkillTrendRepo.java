package com.ojtportal.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.Skill;
import com.ojtportal.api.entity.SkillTrend;

@Repository
public interface SkillTrendRepo extends JpaRepository<SkillTrend, Integer> {
    SkillTrend findBySkill(Skill skill);
}
