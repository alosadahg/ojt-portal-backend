package com.ojtportal.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.Student;

@Repository
public interface StudentRepo extends JpaRepository<Student, Long>{
    Student findByStudentid(String studentID);
    Student findByStudentidAndUserid(String studentID, Long uid);
    Student findByUser_Email(String email);
}
