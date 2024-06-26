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
import com.ojtportal.api.dto.LogbookEntryDTO;
import com.ojtportal.api.entity.LogbookEntry;
import com.ojtportal.api.service.LogbookService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class LogbookController {
    @Autowired
    private final LogbookService logbookService;

    @PreAuthorize("hasAuthority('ROLE_STUDENT') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("student/add-logbook-entry")
    public ResponseEntity<String> addLogbookEntry(@RequestBody LogbookEntryDTO logbookEntryDTO,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(logbookService.addLogbookEntry(logbookEntryDTO, principal.getEmail()));
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT') and hasAuthority('ROLE_ACTIVE')")
    @PutMapping("student/update-logbook-entry")
    public ResponseEntity<String> updateLogbookEntry(@RequestBody LogbookEntryDTO logbookEntryDTO,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(logbookService.updateLogbookEntry(logbookEntryDTO, principal.getEmail()));
    }

    @PreAuthorize("hasAuthority('ROLE_ACTIVE')")
    @GetMapping("/student/get-all-entries")
    public ResponseEntity<List<LogbookEntry>> getAllEntries(@RequestParam String email, @AuthenticationPrincipal UserPrincipal principal) {
        for (GrantedAuthority authority : principal.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_STUDENT")) {
                email = principal.getEmail();
            }
        }
        return ResponseEntity.ok(logbookService.getStudentLogbook(email));
    }

    @PreAuthorize("hasAuthority('ROLE_ACTIVE') and not hasAuthority('ROLE_STUDENT')")
    @GetMapping("/supervisor/get-logbooks")
    public ResponseEntity<List<LogbookEntry>> getSupervisorLogbooks(@RequestParam String supervisorEmail, @AuthenticationPrincipal UserPrincipal principal) {
        for (GrantedAuthority authority : principal.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_SUPERVISOR")) {
                supervisorEmail = principal.getEmail();
            }
        }
        return ResponseEntity.ok(logbookService.getSupervisorLogbooks(supervisorEmail));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/supervisor/reject-logbook")
    public ResponseEntity<String> rejectLogbook(int entryID, String remarks,  @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(logbookService.rejectLogbookEntry(entryID, remarks, principal.getEmail()));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/supervisor/approve-logbook")
    public ResponseEntity<String> approveLogbook(int entryID, String remarks,  @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(logbookService.approveLogbookEntry(entryID, remarks, principal.getEmail()));
    }
}
