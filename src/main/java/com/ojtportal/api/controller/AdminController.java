package com.ojtportal.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import com.ojtportal.api.config.security.UserPrincipal;
import com.ojtportal.api.entity.UserEntity;
import com.ojtportal.api.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@CrossOrigin
@RequiredArgsConstructor
public class AdminController {
    @Autowired
    private final UserService userService;

    @GetMapping("/admin/dashboard") 
    @PreAuthorize("hasAuthority('ROLE_ADMIN') and hasAuthority('ROLE_ACTIVE')")
    public String getDashboard(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        List<UserEntity> users = userService.getAllUsers();
        UserEntity user = userService.getUserEntity(principal.getUid());
        String name = user.getFirstname() + " " + user.getLastname();
        model.addAttribute("name", name);
        model.addAttribute("users", users);
        return "admin-dashboard";
    }

    @PostMapping("/admin/activate")
    public String activateUser(@RequestParam(value = "userId") Long userId) {
        userService.activateUser(userId);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/restrict")
    public String restrictUser(@RequestParam(value = "userId") Long userId) {
        userService.restrictUser(userId);
        return "redirect:/admin/dashboard";
    }

}
