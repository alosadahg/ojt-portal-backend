package com.ojtportal.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ojtportal.api.config.security.UserPrincipal;
import com.ojtportal.api.dto.AssignTrainingPlanDTO;
import com.ojtportal.api.dto.TrainingPlanDTO;
import com.ojtportal.api.entity.TrainingPlan;
import com.ojtportal.api.service.TrainingPlanService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class TrainingPlanController {
    @Autowired
    private final TrainingPlanService trainingPlanService;

    @PreAuthorize("hasAuthority('ROLE_ACTIVE')")
    @GetMapping("/get-training-plans")
    public ResponseEntity<List<TrainingPlan>> getAllTrainingPlansByStudent(@RequestParam String studentEmail, @AuthenticationPrincipal UserPrincipal principal) {
        String email = principal.getEmail();
        String user_type = "student";
        String auth = "";
        for (GrantedAuthority authority : principal.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                email = studentEmail;
                user_type = "admin";
            } 
            if (authority.getAuthority().equals("ROLE_SUPERVISOR")) {
                email = studentEmail;
                user_type = "supervisor";
                auth = principal.getEmail();
            } 
        }
        return ResponseEntity.ok(trainingPlanService.getTrainingPlansByStudent(email, user_type, auth));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/supervisor/assign-training-plan")
    public String assignTrainingPlan(@RequestBody AssignTrainingPlanDTO assignTrainingPlan, @AuthenticationPrincipal UserPrincipal principal) {
        return trainingPlanService.assignTrainingPlan(assignTrainingPlan, principal.getEmail());
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/supervisor/add-training-plan")
    public String addTrainingPlan(TrainingPlanDTO plan,  @AuthenticationPrincipal UserPrincipal principal) {
        return trainingPlanService.addTrainingPlan(plan, principal.getEmail());
    }
}
