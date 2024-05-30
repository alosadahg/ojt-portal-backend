package com.ojtportal.api.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.ojtportal.api.entity.OjtRecord;
import com.ojtportal.api.entity.Student;
import com.ojtportal.api.entity.StudentSkillProficiency;
import com.ojtportal.api.repositories.OjtRecordRepo;
import com.ojtportal.api.repositories.StudentRepo;
import com.ojtportal.api.repositories.StudentSkillProficiencyRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentSkillProficiencyService {
    private final StudentRepo studentRepo;
    private final StudentSkillProficiencyRepo studentSkillProficiencyRepo;
    private final OjtRecordRepo ojtRecordRepo;

    public String getStudentProficiency(String studentEmail, String auth, String user_type) {
        Map<Student, List<StudentSkillProficiency>> proficiencies = new HashMap<>();
        List<Student> students = new ArrayList<Student>();
        List<OjtRecord> records = new ArrayList<>();
        
            if (studentEmail.equalsIgnoreCase("all")) {
                if (user_type.equalsIgnoreCase("admin") || user_type.equalsIgnoreCase("chair") || user_type.equalsIgnoreCase("instructor")) {
                    records = ojtRecordRepo.findAll();
                    for (OjtRecord record : records) {
                        Student student = record.getStudent();
                        if (student != null) {
                            students.add(student);
                        }
                    }
                    for (Student student : students) {
                        List<StudentSkillProficiency> studentProficiencies = studentSkillProficiencyRepo.findByStudent(student);
                        if (studentProficiencies == null) {
                            studentProficiencies = new ArrayList<>();
                        }
                        proficiencies.put(student, studentProficiencies);
                    }
                    return proficiencies.toString();
                }
            }
            if (user_type.equalsIgnoreCase("supervisor")) {
                if (studentEmail.equalsIgnoreCase("all")) {
                    records = ojtRecordRepo.findBySupervisor_User_Email(auth);
                    for (OjtRecord record : records) {
                        Student student = record.getStudent();
                        if (student != null) {
                            students.add(student);
                        }
                    }
                    for (Student student : students) {
                        List<StudentSkillProficiency> studentProficiencies = studentSkillProficiencyRepo.findByStudent(student);
                        if (studentProficiencies == null) {
                            studentProficiencies = new ArrayList<>();
                        }
                        proficiencies.put(student, studentProficiencies);
                    }
                    return proficiencies.toString();
                } 
                else {
                    OjtRecord record = ojtRecordRepo.findBySupervisor_User_EmailAndStudent_User_Email(auth, studentEmail);
                    if (record != null) {
                        List<StudentSkillProficiency> studentProficiencies = studentSkillProficiencyRepo.findByStudent(record.getStudent());
                        if (studentProficiencies == null) {
                            studentProficiencies = new ArrayList<>();
                        }
                        proficiencies.put(record.getStudent(), studentProficiencies);
                        return proficiencies.toString();
                    }
                    return "ERROR: Record for " + studentEmail + " is unaccessible.";
                }
            }
            Student student = studentRepo.findByUser_Email(studentEmail);
            if (student != null) {
                List<StudentSkillProficiency> studentProficiencies = studentSkillProficiencyRepo.findByStudent(student);
                if (studentProficiencies == null) {
                    studentProficiencies = new ArrayList<>();
                }
                proficiencies.put(student, studentProficiencies);
            }
            return proficiencies.toString();
    }
}
