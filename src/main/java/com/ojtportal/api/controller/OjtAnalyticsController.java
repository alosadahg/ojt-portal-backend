package com.ojtportal.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ojtportal.api.dto.SkillTrendInputDTO;
import com.ojtportal.api.dto.SkillTrendResponseDTO;
import com.ojtportal.api.entity.SkillTrend;
import com.ojtportal.api.service.SkillTrendService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class OjtAnalyticsController {
    @Autowired
    private final SkillTrendService skillTrendService;

    @GetMapping("/get-trend-summary")
    private ResponseEntity<List<SkillTrend>> getTrendSummary() {
        return ResponseEntity.ok(skillTrendService.getLogbookTrend());
    }

    @PostMapping(path = "/get-skill-trend-report")
    private ResponseEntity<SkillTrendResponseDTO> getSkillTrend(SkillTrendInputDTO input) {
        return ResponseEntity.ok(skillTrendService.getGlobalSkillTrend(input));
    }
}
