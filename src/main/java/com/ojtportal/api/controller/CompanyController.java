package com.ojtportal.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ojtportal.api.config.security.UserPrincipal;
import com.ojtportal.api.entity.Company;
import com.ojtportal.api.entity.Student;
import com.ojtportal.api.repositories.SupervisorRepo;
import com.ojtportal.api.service.CompanyService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {
    @Autowired
    private final CompanyService companyService;
    @Autowired
    private final SupervisorRepo supervisorRepo;

    @PostMapping("/add")
    @Secured({"ROLE_SUPERVISOR", "ROLE_ADMIN"})
    public ResponseEntity<Integer> addCompanyFullInfo(String company_name, String contactNo, String email, String address) {
        return ResponseEntity.ok(companyService.addCompany(company_name, contactNo, email, address));
    }

    // @PostMapping("/add-by-student")
    // @Secured({"ROLE_STUDENT"})
    // public ResponseEntity<Integer> addCompany(String company_name) {
    //     return ResponseEntity.ok(companyService.addCompany(company_name));
    // }

    @GetMapping("/get-all-companies")
    @PreAuthorize("hasAuthority('ROLE_ACTIVE')")
    public ResponseEntity<List<Company>> getAllCompanies(@AuthenticationPrincipal UserPrincipal principal) {
        String email = "";
        String user_type = "student";
        for (GrantedAuthority authority : principal.getAuthorities()) {
            switch (authority.getAuthority()) {
                case "ROLE_SUPERVISOR":
                    email = principal.getEmail();
                    user_type = authority.getAuthority().substring(5).toLowerCase();
                    break;
                case "ROLE_STUDENT":
                    email = principal.getEmail();
                    break;
                case "ROLE_ACTIVE":
                    break;
                default:
                    user_type = authority.getAuthority().substring(5).toLowerCase();
                    break;
            }
        }
        return ResponseEntity.ok(companyService.getAllCompanies(email, user_type));
    }

    @Operation(summary = "Get all students by company name")
    @GetMapping("/get-all-students")
    @PreAuthorize("hasAuthority('ROLE_ACTIVE') and not hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<Student>> getStudentsByCompany(@RequestParam String companyName, @AuthenticationPrincipal UserPrincipal principal) {
        for (GrantedAuthority authority : principal.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_SUPERVISOR")) {
                companyName = supervisorRepo.findByUser_Email(principal.getEmail()).getCompany().getCompanyName();
            } 
        }
        return ResponseEntity.ok(companyService.getStudentsByCompany(companyName));
    }

    // @GetMapping("/get-all-supervisors")
    // @PreAuthorize("hasAuthority('ROLE_ACTIVE') and not hasAuthority('ROLE_STUDENT')")
    // public ResponseEntity<List<Supervisor>> getSupervisorsByCompany(@RequestParam String companyName, @AuthenticationPrincipal UserPrincipal principal) {
    //     for (GrantedAuthority authority : principal.getAuthorities()) {
    //         if (authority.getAuthority().equals("ROLE_SUPERVISOR")) {
    //             companyName = supervisorRepo.findByUser_Email(principal.getEmail()).getCompany().getCompanyName();
    //         } 
    //     }
    //     return ResponseEntity.ok(companyService.getSupervisorsByCompany(companyName));
    // }
}
