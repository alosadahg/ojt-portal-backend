package com.ojtportal.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.Supervisor;
import com.ojtportal.api.entity.TrainingPlan;

@Repository
public interface TrainingPlanRepo extends JpaRepository<TrainingPlan, Integer> {
    TrainingPlan findByTitle(String title);
    TrainingPlan findByTitleAndSupervisor(String title, Supervisor supervisor);
    List<TrainingPlan> findBySupervisor_User_Email(String supervisorEmail);
    List<TrainingPlan> findBySupervisor_User_EmailAndStudentTrainingPlans_Student_User_Email(String supervisorEmail, String studentEmail);
    List<TrainingPlan> findByStudentTrainingPlans_Student_User_Email(String studentEmail);
}
