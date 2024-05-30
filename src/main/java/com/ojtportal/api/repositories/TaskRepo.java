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
}
