package com.ojtportal.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Task> getAllSkillsByStudent(@RequestParam String studentEmail, @AuthenticationPrincipal UserPrincipal principal) {
        for (GrantedAuthority authority : principal.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_STUDENT")) {
                studentEmail = principal.getEmail();
            }
        }
        return taskService.getAllSkillsByStudent(studentEmail);
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR') and hasAuthority('ROLE_ACTIVE')")
    @PostMapping("/supervisor/add-task")
    public String addTasks(@RequestBody TrainingPlanTaskDTO taskDetails, @AuthenticationPrincipal UserPrincipal principal) {
        return taskService.addTask(taskDetails, principal.getEmail());
    }
}
