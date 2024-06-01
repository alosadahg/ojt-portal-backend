package com.ojtportal.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.Task;
import com.ojtportal.api.entity.TrainingPlan;

@Repository
public interface TaskRepo extends JpaRepository<Task, Integer>{
    Task findByTitleAndTrainingplan_Trainingplanid(String title, int trainingPlanID);
    Task findByTitleAndTrainingplan(String title, TrainingPlan plan);
    List<Task> findByTrainingplan(TrainingPlan plan);
    List<Task> findByTrainingplan_Supervisor_User_Email(String supervisorEmail);
    List<Task> findByTrainingplan_Supervisor_User_EmailAndStudentTasks_Student_User_Email(String supervisorEmail, String studentEmail);
    List<Task> findByStudentTasks_Student_User_Email(String studentEmail);
}
