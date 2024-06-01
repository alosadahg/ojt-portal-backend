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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ojtportal.api.config.security.UserPrincipal;
import com.ojtportal.api.dto.OJTRecordDTO;
import com.ojtportal.api.entity.OjtRecord;
import com.ojtportal.api.service.OjtRecordService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class OJTRecordController {
    @Autowired
    private final OjtRecordService ojtRecordService;

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("supervisor/add-intern")
    public ResponseEntity<String> addIntern(OJTRecordDTO record, @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ojtRecordService.addOjtRecord(record, principal.getEmail()));
    }
    
    @PreAuthorize("hasAuthority('ROLE_STUDENT') and hasAuthority('ROLE_ACTIVE')")
    @PutMapping("student/join-team")
    public ResponseEntity<Integer> joinTeam(String teamCode, @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ojtRecordService.joinTeam(principal.getEmail(), teamCode));
    }

    @PreAuthorize("hasAuthority('ROLE_ACTIVE')")
    @GetMapping("/get-ojt-records")
    public ResponseEntity<List<OjtRecord>> getAllOjtRecords(@RequestParam String studentEmail, @AuthenticationPrincipal UserPrincipal principal) {
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
        return ResponseEntity.ok(ojtRecordService.getAllOjtRecords(studentEmail, user_type, auth));
    }
}
