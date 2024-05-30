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
import org.springframework.web.bind.annotation.PutMapping;
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
        String user_type = "student";
        String auth = "";
        for (GrantedAuthority authority : principal.getAuthorities()) {
            switch (authority.getAuthority()) {
                case "ROLE_SUPERVISOR":
                    auth = principal.getEmail();
                    user_type = authority.getAuthority().substring(5).toLowerCase();
                    break;
                case "ROLE_STUDENT":
                    studentEmail = principal.getEmail();
                    break;
                case "ROLE_ACTIVE":
                    break;
                default:
                    user_type = authority.getAuthority().substring(5).toLowerCase();
                    break;
            }
        }
        return ResponseEntity.ok(trainingPlanService.getTrainingPlansByStudent(studentEmail, user_type, auth));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/supervisor/assign-training-plan")
    public ResponseEntity<String> assignTrainingPlan(@RequestBody AssignTrainingPlanDTO assignTrainingPlan, @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(trainingPlanService.assignTrainingPlan(assignTrainingPlan, principal.getEmail()));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/supervisor/add-training-plan")
    public ResponseEntity<String> addTrainingPlan(TrainingPlanDTO plan,  @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(trainingPlanService.addTrainingPlan(plan, principal.getEmail()));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PutMapping("/supervisor/update-training-plan")
    public ResponseEntity<String> updateTrainingPlan (TrainingPlanDTO plan, @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(trainingPlanService.updateTrainingPlan(plan, principal.getEmail()));
    }
}
