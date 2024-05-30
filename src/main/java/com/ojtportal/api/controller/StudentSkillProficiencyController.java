package com.ojtportal.api.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ojtportal.api.config.security.UserPrincipal;
import com.ojtportal.api.service.StudentSkillProficiencyService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class StudentSkillProficiencyController {
    @Autowired
    private final StudentSkillProficiencyService studentSkillProficiencyService;

    @PreAuthorize("hasAuthority('ROLE_ACTIVE')")
    @GetMapping(path = "/get-student-proficiency") 
    public ResponseEntity<String> getStudentProficiency(@RequestParam String studentEmail, 
        @AuthenticationPrincipal UserPrincipal principal) {
        String auth = "";
        String user_type = "student";
        for(GrantedAuthority authority: principal.getAuthorities()) {
            switch (authority.getAuthority()) {
                case "ROLE_SUPERVISOR":
                    auth = principal.getEmail();
                    user_type = authority.getAuthority().substring(5);
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
        return ResponseEntity.ok(studentSkillProficiencyService.getStudentProficiency(studentEmail, auth, user_type));
    }
}
