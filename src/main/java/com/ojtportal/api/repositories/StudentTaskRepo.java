package com.ojtportal.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.Student;
import com.ojtportal.api.entity.StudentTask;
import com.ojtportal.api.entity.Task;

@Repository
public interface StudentTaskRepo extends JpaRepository<StudentTask, Integer> {
    StudentTask findByStudentAndTask(Student student, Task task);
    List<StudentTask> findByStudent(Student student);
    List<StudentTask> findByTask(Task task);
    List<StudentTask> findByStudent_User_Email(String studentEmail);
}
