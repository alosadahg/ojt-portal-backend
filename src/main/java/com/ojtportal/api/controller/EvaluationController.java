package com.ojtportal.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ojtportal.api.config.security.UserPrincipal;
import com.ojtportal.api.entity.Evaluation;
import com.ojtportal.api.service.EvaluationService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class EvaluationController {
    private final EvaluationService evaluationService;

    @PreAuthorize("hasAuthority('ROLE_ACTIVE')")
    @GetMapping("/get-evaluation-record")
    public ResponseEntity<List<Evaluation>> getEvaluationRecord(String studentEmail, @AuthenticationPrincipal UserPrincipal principal) {
        String user_type = "";
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
        return ResponseEntity.ok(evaluationService.getEvaluationRecord(studentEmail, auth, user_type));
    }

    @PreAuthorize("hasAuthority('ROLE_ACTIVE') and hasAuthority('ROLE_STUDENT')")
    @PostMapping("/student/give-ojt-feedback")
    public ResponseEntity<Integer> addStudentFeedback(String feedback, @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(evaluationService.addStudentFeedback(feedback, principal.getEmail()));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/supervisor/evaluate-intern")
    public ResponseEntity<String> addSupervisorFeedback(double grade, String feedback, String studentEmail, @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(evaluationService.addSupervisorFeedback(feedback, grade, studentEmail, principal.getEmail()));
    }

    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/instructor/evaluate-intern")
    public ResponseEntity<String> addInstructorFeedback(double grade, String feedback, String studentEmail, @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(evaluationService.addInstructorFeedback(feedback, grade, studentEmail));
    }

}
