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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ojtportal.api.config.security.UserPrincipal;
import com.ojtportal.api.dto.TrainingPlanTaskDTO;
import com.ojtportal.api.entity.Task;
import com.ojtportal.api.service.TaskService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PreAuthorize("hasAuthority('ROLE_ACTIVE')")
    @GetMapping("/get-student-allTasks")
    public ResponseEntity<List<Task>> getAllTasksByStudent(@RequestParam String studentEmail, @AuthenticationPrincipal UserPrincipal principal) {
        String auth = "";
        String user_type = "student";
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
        return ResponseEntity.ok(taskService.getAllTasksByStudent(studentEmail, auth, user_type));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/supervisor/add-task")
    public ResponseEntity<String> addTasks(@RequestBody TrainingPlanTaskDTO taskDetails, @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(taskService.addTask(taskDetails, principal.getEmail()));
    }
}
