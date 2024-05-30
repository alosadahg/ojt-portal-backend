package com.ojtportal.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ojtportal.api.dto.StudentDTO;
import com.ojtportal.api.entity.Student;
import com.ojtportal.api.entity.UserEntity;
import com.ojtportal.api.service.StudentService;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PostMapping("student/register")
    public int registerNewStudentUser(StudentDTO student) {
        UserEntity user = new UserEntity(student.getEmail(), student.getPassword(), student.getFirstname(),
                student.getLastname());
        return studentService.registerNewUserStudent(user, student.getStudentID(), student.getDegreeProgram());
    }

    @PreAuthorize("hasAuthority('ROLE_ACTIVE') and not hasAuthority('ROLE_STUDENT')")
    @GetMapping("/get-all-students")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }
}
