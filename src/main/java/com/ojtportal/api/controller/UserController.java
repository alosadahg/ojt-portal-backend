package com.ojtportal.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ojtportal.api.config.security.UserPrincipal;
import com.ojtportal.api.entity.UserEntity;
import com.ojtportal.api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("user/get/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') and hasAuthority('ROLE_ACTIVE')")
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("user/get")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') and hasAuthority('ROLE_ACTIVE')")
    public ResponseEntity<String> getAnyUserDetails(@RequestParam String userid) {
        return ResponseEntity.ok(userService.getUserInfo(Long.parseLong(userid)));
    }

    @GetMapping("user/get-own-details")
    @PreAuthorize("hasAuthority('ROLE_ACTIVE')")
    public ResponseEntity<String> getMyUserDetails(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getUserInfo(principal.getUid()));
    }

    @RequestMapping(method = RequestMethod.POST, path = "auth/register-instructor")
    public int registerInstructor(UserEntity newUser) {
        return userService.registerInstructor(newUser);
    }

    @RequestMapping(method = RequestMethod.POST, path = "auth/register-chair")
    public int registerChair(UserEntity newUser) {
        return userService.registerChair(newUser);
    }

    @RequestMapping(method = RequestMethod.POST, path = "auth/register-admin")
    public int registerAdmin(UserEntity newUser) {
        return userService.registerAdmin(newUser);
    }
    
    @RequestMapping(method = RequestMethod.POST, path = "auth/login") 
    public ResponseEntity<String> loginUser(UserEntity existingUser) {
        return ResponseEntity.ok(userService.login(existingUser));
    }

    @PostMapping("auth/logout") 
    public ResponseEntity<String> logoutUser() {
        return ResponseEntity.ok("1");
    }

    @PutMapping("auth/change-password") 
    public ResponseEntity<String> changePassword(Long userID, String oldPassword, String newPassword) {
        return ResponseEntity.ok(userService.changePassword(userID, oldPassword, newPassword));
    }

    @PutMapping("auth/forget-password") 
    public ResponseEntity<Integer> forgetPassword(String email) {
        return ResponseEntity.ok(userService.forgetPassword(email));
    }

    @PutMapping("auth/forget-password/verify") 
    public ResponseEntity<Integer> forgetPassword(String email, String password, String otp) {
        return ResponseEntity.ok(userService.authenticate(email, password, otp, "password-change"));
    }

    @PutMapping("auth/activate")
    public ResponseEntity<Integer> activateUser(String email, String verificationCode) {
        return ResponseEntity.ok(userService.authenticate(email,"", verificationCode, "activation"));
    }
}