package com.ojtportal.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ojtportal.api.dto.SupervisorDTO;
import com.ojtportal.api.entity.UserEntity;
import com.ojtportal.api.service.SupervisorService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class SupervisorController {
    @Autowired
    private SupervisorService supervisorService;

    @PostMapping("/supervisor/register")
    public int addSupervisor(SupervisorDTO supervisorDTO) {
        UserEntity user = new UserEntity(supervisorDTO.getEmail(), supervisorDTO.getPassword(), supervisorDTO.getFirstname(), supervisorDTO.getLastname());
        return supervisorService.registerSupervisor(user, supervisorDTO.getCompany_name(), supervisorDTO.getCompany_email(), supervisorDTO.getCompany_contactNo(), supervisorDTO.getCompany_location(), supervisorDTO.getPosition());
    }
}
