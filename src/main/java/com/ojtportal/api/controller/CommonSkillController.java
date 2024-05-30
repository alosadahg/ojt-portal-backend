package com.ojtportal.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ojtportal.api.entity.CommonSkill;
import com.ojtportal.api.service.CommonSkillService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class CommonSkillController {
    @Autowired
    private final CommonSkillService commonSkillService;

    @GetMapping("get/all-common-skills")
    public List<CommonSkill> getAllCommonSkills() {
        return commonSkillService.getAllSkills();
    }
}
