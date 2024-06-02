package com.ojtportal.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.ojtportal.api.dto.SkillTrendInputDTO;
import com.ojtportal.api.dto.SkillTrendResponseDTO;
import com.ojtportal.api.entity.SkillTrend;
import com.ojtportal.api.repositories.SkillTrendRepo;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class SkillTrendService {
    private final SkillTrendRepo skillTrendRepo;

    public List<SkillTrend> getLogbookTrend() {
        return skillTrendRepo.findAll();
    }

    public SkillTrendResponseDTO getGlobalSkillTrend(SkillTrendInputDTO skillInput) {
        WebClient webClient = WebClient.create("https://skill-trend-api.onrender.com/");

        Mono<ResponseEntity<SkillTrendResponseDTO>> skillReport = webClient.post()
                .uri("/skill-trend-report")
                .body(BodyInserters.fromFormData("skill", skillInput.getSkill())
                        .with("startDate", skillInput.getStartDate())
                        .with("endDate", skillInput.getEndDate()))
                .retrieve()
                .toEntity(SkillTrendResponseDTO.class);

        SkillTrendResponseDTO body = skillReport.block().getBody();
        if (skillTrendRepo.findBySkill_SkillName(skillInput.getSkill()) != null) {
            SkillTrend existing = skillTrendRepo.findBySkill_SkillName(body.getSkill());
            existing.setDemandChange(body.getAvgDemandChange());
            existing.setTrend(body.getTrend());
            skillTrendRepo.save(existing);
        }

        return body;
    }
}
