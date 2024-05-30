package com.ojtportal.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojtportal.api.entity.Skill;
import com.ojtportal.api.entity.SkillTrend;
import com.ojtportal.api.repositories.SkillRepo;
import com.ojtportal.api.repositories.SkillTrendRepo;

@Service
public class SkillService {
    @Autowired
    private SkillRepo skillRepo;
    @Autowired
    private SkillTrendRepo skillTrendRepo;

    public Skill addSkill(String skillName, String domain) {
        if(skillRepo.findBySkillNameAndDomain(skillName, domain)==null) {
            Skill skill = new Skill(skillName, domain);
            SkillTrend trend = new SkillTrend(skill);
            trend.setSkillFrequency(1);
            skillTrendRepo.save(trend);
            skillRepo.save(skill);
            return skillRepo.findBySkillNameAndDomain(skillName, domain);
        }
        return null;
    }
}
