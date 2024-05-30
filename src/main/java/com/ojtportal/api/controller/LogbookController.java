package com.ojtportal.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    public String addLogbookEntry(@RequestBody LogbookEntryDTO logbookEntryDTO,
            @AuthenticationPrincipal UserPrincipal principal) {
        return logbookService.addLogbookEntry(logbookEntryDTO, principal.getEmail());
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT') and hasAuthority('ROLE_ACTIVE')")
    @PutMapping("student/update-logbook-entry")
    public String updateLogbookEntry(@RequestBody LogbookEntryDTO logbookEntryDTO,
            @AuthenticationPrincipal UserPrincipal principal) {
        return logbookService.updateLogbookEntry(logbookEntryDTO, principal.getEmail());
    }

    @PreAuthorize("hasAuthority('ROLE_ACTIVE')")
    @GetMapping("student/get-all-entries")
    public List<LogbookEntry> getAllEntries(@RequestParam String email, @AuthenticationPrincipal UserPrincipal principal) {
        for (GrantedAuthority authority : principal.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_STUDENT")) {
                email = principal.getEmail();
            }
        }
        return logbookService.getStudentLogbook(email);
    }

    @PreAuthorize("hasAuthority('ROLE_ACTIVE') and not hasAuthority('ROLE_STUDENT')")
    @GetMapping("/supervisor/get-logbooks")
    public List<LogbookEntry> getAllLogbooks(@RequestParam String supervisorEmail, @AuthenticationPrincipal UserPrincipal principal) {
        for (GrantedAuthority authority : principal.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_SUPERVISOR")) {
                supervisorEmail = principal.getEmail();
            }
        }
        return logbookService.getSupervisorLogbooks(supervisorEmail);
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/supervisor/reject-logbook")
    public String rejectLogbook(int entryID, String remarks,  @AuthenticationPrincipal UserPrincipal principal) {
        return logbookService.rejectLogbookEntry(entryID, remarks, principal.getEmail());
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/supervisor/approve-logbook")
    public String approveLogbook(int entryID, String remarks,  @AuthenticationPrincipal UserPrincipal principal) {
        return logbookService.approveLogbookEntry(entryID, remarks, principal.getEmail());
    }
}
