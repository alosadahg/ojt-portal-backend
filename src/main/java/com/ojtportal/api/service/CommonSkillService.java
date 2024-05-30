package com.ojtportal.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ojtportal.api.entity.CommonSkill;
import com.ojtportal.api.repositories.CommonSkillsRepo;

@Service
public class CommonSkillService {
    @Autowired
    private CommonSkillsRepo commonSkillsRepo;

    @Cacheable(value = "skills")
    public List<CommonSkill> getAllSkills() {
        return commonSkillsRepo.findAll();
    }

    public List<CommonSkill> getSkillsThatContains() {
        return commonSkillsRepo.findAll();
    }
}
