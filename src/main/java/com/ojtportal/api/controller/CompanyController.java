package com.ojtportal.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ojtportal.api.config.security.UserPrincipal;
import com.ojtportal.api.entity.Company;
import com.ojtportal.api.entity.Student;
import com.ojtportal.api.entity.Supervisor;
import com.ojtportal.api.repositories.SupervisorRepo;
import com.ojtportal.api.service.CompanyService;

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
    public int addCompanyFullInfo(String company_name, String contactNo, String email, String address) {
        return companyService.addCompany(company_name, contactNo, email, address);
    }

    @PostMapping("/add-by-student")
    @Secured({"ROLE_STUDENT"})
    public int addCompany(String company_name) {
        return companyService.addCompany(company_name);
    }

    @GetMapping("/get-all-companies")
    @PreAuthorize("hasAuthority('ROLE_ACTIVE')")
    public List<Company> getAllCompanies(@AuthenticationPrincipal UserPrincipal principal) {
        String email = "";
        String user_type = "ADMIN";
        for (GrantedAuthority authority : principal.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_STUDENT")) {
                email = principal.getEmail();
                user_type = "STUDENT";
            } 
            if (authority.getAuthority().equals("ROLE_SUPERVISOR")) {
                email = principal.getEmail();
                user_type = "SUPERVISOR";
            } 
        }
        return companyService.getAllCompanies(email, user_type);
    }

    @GetMapping("/get-all-students")
    @PreAuthorize("hasAuthority('ROLE_ACTIVE') and not hasAuthority('ROLE_STUDENT')")
    public List<Student> getStudentsByCompany(@RequestParam String companyName, @AuthenticationPrincipal UserPrincipal principal) {
        for (GrantedAuthority authority : principal.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_SUPERVISOR")) {
                companyName = supervisorRepo.findByUser_Email(principal.getEmail()).getCompany().getCompanyName();
            } 
        }
        return companyService.getStudentsByCompany(companyName);
    }

    @GetMapping("/get-all-supervisors")
    @PreAuthorize("hasAuthority('ROLE_ACTIVE') and not hasAuthority('ROLE_STUDENT')")
    public List<Supervisor> getSupervisorsByCompany(@RequestParam String companyName, @AuthenticationPrincipal UserPrincipal principal) {
        for (GrantedAuthority authority : principal.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_SUPERVISOR")) {
                companyName = supervisorRepo.findByUser_Email(principal.getEmail()).getCompany().getCompanyName();
            } 
        }
        return companyService.getSupervisorsByCompany(companyName);
    }
}
