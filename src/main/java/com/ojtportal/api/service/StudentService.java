package com.ojtportal.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import com.ojtportal.api.entity.*;
import com.ojtportal.api.model.Role;
import com.ojtportal.api.repositories.*;

import lombok.RequiredArgsConstructor;

@Service
@EnableCaching
@RequiredArgsConstructor
public class StudentService {
    @Autowired
    private final UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private final UserRepo userRepo;


    @Autowired
    private UnregisteredInternRepo unregisteredInternRepo;

    @Autowired
    private final StudentRepo studentRepo;

    public int registerNewUserStudent(UserEntity user, String studentID, String department) {
        user.setAccountType(Role.ROLE_STUDENT);
        // if(userService.addUser(user)==0){
        //     return registerUserToStudent(userRepo.findByEmail(user.getEmail()).getUid(), studentID, department);
        // }
        UserEntity savedUser = userService.getUserEntity(user.getUid());
        if(unregisteredInternRepo.existsById(user.getEmail())) {
            UnregisteredIntern intern = unregisteredInternRepo.findById(user.getEmail()).get();
            UserEntity supervisor = userRepo.findById(intern.getSupervisor().getUserid()).get();
            Company company = intern.getSupervisor().getCompany();
            emailService.sendSimpleMail(supervisor.getEmail(), "An intern has joined the portal", 
                "Invite them again to " + company.getCompanyName() + "! 🥳\n\n" +
                "An intern you have previously invited has now joined the portal.\n\n" +
                "Register " + user.getEmail() + " again in your team and we will send them a warm welcome 🤗"
            );
        }
        if (studentRepo.findByStudentid(studentID)==null && !studentRepo.existsById(user.getUid())) {
            Student student = new Student(studentID, department, savedUser);
            studentRepo.save(student);
            return 1;
        }
        return 0;
    }

    public int registerUserToStudent(Long userid, String studentID, String department) {
        if (userService.getUserEntity(userid) != null) {
            Student student = new Student(studentID, department, userService.getUserEntity(userid));
            UserEntity user = userService.getUserEntity(userid);
            student.setUser(user);
            user.setAccountType(Role.ROLE_STUDENT);
            if(studentRepo.findByStudentid(studentID)==null && !studentRepo.existsById(user.getUid())) {
                userRepo.save(user);
                studentRepo.save(student);
                return 1;
            }
        }
        return 0;
    }

    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }

    public Student getStudentByStudentID(String studentID) {
        return studentRepo.findByStudentid(studentID);
    }

}
