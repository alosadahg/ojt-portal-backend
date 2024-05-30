package com.ojtportal.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.Student;
import com.ojtportal.api.entity.StudentTrainingPlan;
import com.ojtportal.api.entity.TrainingPlan;

@Repository
public interface StudentTrainingPlanRepo extends JpaRepository<StudentTrainingPlan, Integer> {
    StudentTrainingPlan findByStudentAndTrainingPlan(Student student, TrainingPlan trainingPlan);
    List<StudentTrainingPlan> findByStudent(Student student);
    List<StudentTrainingPlan> findByTrainingPlan(TrainingPlan trainingPlan);
}
