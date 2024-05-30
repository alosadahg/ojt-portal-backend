package com.ojtportal.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ojtportal.api.config.security.UserPrincipal;
import com.ojtportal.api.service.EvaluationService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class EvaluationController {
    private final EvaluationService evaluationService;

    @PreAuthorize("hasAuthority('ROLE_ACTIVE') and hasAuthority('ROLE_STUDENT')")
    @PostMapping("/student/give-ojt-feedback")
    public int addStudentFeedback(String feedback, @AuthenticationPrincipal UserPrincipal principal) {
        return evaluationService.addStudentFeedback(feedback, principal.getEmail());
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/supervisor/evaluate-intern")
    public String addSupervisorFeedback(double grade, String feedback, String studentEmail, @AuthenticationPrincipal UserPrincipal principal) {
        return evaluationService.addSupervisorFeedback(feedback, grade, studentEmail, principal.getEmail());
    }

    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/instructor/evaluate-intern")
    public String addInstructorFeedback(double grade, String feedback, String studentEmail, @AuthenticationPrincipal UserPrincipal principal) {
        return evaluationService.addInstructorFeedback(feedback, grade, studentEmail);
    }
}
