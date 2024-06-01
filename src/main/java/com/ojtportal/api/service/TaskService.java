package com.ojtportal.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojtportal.api.dto.SkillDTO;
import com.ojtportal.api.dto.TaskDTO;
import com.ojtportal.api.dto.TrainingPlanTaskDTO;
import com.ojtportal.api.entity.Skill;
import com.ojtportal.api.entity.SkillTrend;
import com.ojtportal.api.entity.Student;
import com.ojtportal.api.entity.StudentTask;
import com.ojtportal.api.entity.StudentTrainingPlan;
import com.ojtportal.api.entity.Task;
import com.ojtportal.api.entity.TrainingPlan;
import com.ojtportal.api.repositories.SkillRepo;
import com.ojtportal.api.repositories.SkillTrendRepo;
import com.ojtportal.api.repositories.StudentTaskRepo;
import com.ojtportal.api.repositories.StudentTrainingPlanRepo;
import com.ojtportal.api.repositories.TaskRepo;
import com.ojtportal.api.repositories.TrainingPlanRepo;

@Service
public class TaskService {
    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private SkillRepo skillRepo;

    @Autowired
    private StudentTrainingPlanRepo studentTrainingPlanRepo;

    @Autowired
    private SkillService skillService;

    @Autowired
    private StudentTaskRepo studentTaskRepo;

    @Autowired
    private SkillTrendRepo skillTrendRepo;

    @Autowired
    private TrainingPlanRepo trainingPlanRepo;

    public String addTask(TrainingPlanTaskDTO trainingPlanTaskDTO, String supervisorEmail) {
        Optional<TrainingPlan> planOptional = trainingPlanRepo.findById(trainingPlanTaskDTO.getTrainingPlanID());
        if (planOptional.isEmpty()) {
            return "ERROR: There is no existing training plan with an ID of " + trainingPlanTaskDTO.getTrainingPlanID() + " for this supervisor";
        }
        TrainingPlan plan = planOptional.get();
        if (!plan.getSupervisor().getUser().getEmail().equals(supervisorEmail)) {
            return "ERROR: Unauthorized access. This training plan ID is not associated with this supervisor";
        }    
        TaskDTO taskDTO = trainingPlanTaskDTO.getTask();
        if (taskRepo.findByTitleAndTrainingplan_Trainingplanid(taskDTO.getTitle(), trainingPlanTaskDTO.getTrainingPlanID()) != null) {
            return "ERROR: There is an existing task in Training Plan: " + plan.getTitle() + " with the same task title.";
        }
        List<StudentTrainingPlan> studentTrainingPlans = studentTrainingPlanRepo.findByTrainingPlan(plan);
        List<Student> assignedStudents = new ArrayList<Student>();
        for (StudentTrainingPlan studentTrainingPlan : studentTrainingPlans) {
            assignedStudents.add(studentTrainingPlan.getStudent());
        }
        Task newTask = new Task(taskDTO.getTitle(), taskDTO.getDescription(), taskDTO.getObjective(), plan);
        taskRepo.save(newTask);
        newTask = taskRepo.findByTitleAndTrainingplan(newTask.getTitle(), plan);
        for (Student student : assignedStudents) {
            StudentTask newStudentTask = new StudentTask(student, newTask);
            studentTaskRepo.save(newStudentTask);
        }
        newTask.setStudentTasks(studentTaskRepo.findByTask(newTask));
        List<Skill> taskSkills = new ArrayList<>();
        if(trainingPlanTaskDTO.getSkills()!=null) {
            for (SkillDTO skillDTO : trainingPlanTaskDTO.getSkills()) {
                Skill existingSkill = skillRepo.findBySkillNameAndDomain(skillDTO.getSkill_name(), skillDTO.getDomain());
                if (existingSkill == null) {
                    skillService.addSkill(skillDTO.getSkill_name(), skillDTO.getDomain());
                    Skill skill = skillRepo.findBySkillNameAndDomain(skillDTO.getSkill_name(), skillDTO.getDomain());
                    taskSkills.add(skill);
                } else {
                    if (!existingSkill.getTasks().contains(newTask)) {
                        existingSkill.getTasks().add(newTask);
                        skillRepo.save(existingSkill);
                        SkillTrend trend = skillTrendRepo.findBySkill(existingSkill);
                        trend.setSkillFrequency(trend.getSkillFrequency() + 1);
                        skillTrendRepo.save(trend);
                    }
                    taskSkills.add(existingSkill);
                }
            }
            newTask.setSkills(taskSkills);
        }
        plan.setTotalTasks(plan.getTotalTasks()+1);
        trainingPlanRepo.save(plan);
        taskRepo.save(newTask);
    
        return "Successfully added the task in the training plan";
    }

    public List<Task> getAllTasksByStudent(String studentEmail, String auth, String user_type) {
        List<Task> tasks = taskRepo.findByStudentTasks_Student_User_Email(studentEmail);
        if(user_type.equalsIgnoreCase("supervisor")) {
            if(!tasks.isEmpty()) {
                if(tasks.get(0).getTrainingplan().getSupervisor().getUser().getEmail().equalsIgnoreCase(auth)) {
                    return tasks;
                }
                return Collections.emptyList();
            }
        } 
        return tasks;
    }
}
