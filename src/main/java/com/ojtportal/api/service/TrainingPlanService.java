package com.ojtportal.api.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojtportal.api.dto.AssignTrainingPlanDTO;
import com.ojtportal.api.dto.TrainingPlanDTO;
import com.ojtportal.api.entity.OjtRecord;
import com.ojtportal.api.entity.StudentTask;
import com.ojtportal.api.entity.StudentTrainingPlan;
import com.ojtportal.api.entity.Supervisor;
import com.ojtportal.api.entity.Task;
import com.ojtportal.api.entity.TrainingPlan;
import com.ojtportal.api.repositories.OjtRecordRepo;
import com.ojtportal.api.repositories.StudentTaskRepo;
import com.ojtportal.api.repositories.StudentTrainingPlanRepo;
import com.ojtportal.api.repositories.SupervisorRepo;
import com.ojtportal.api.repositories.TrainingPlanRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainingPlanService {
    @Autowired
    private TrainingPlanRepo trainingPlanRepo;

    @Autowired
    private StudentTrainingPlanRepo studentTrainingPlanRepo;

    @Autowired
    private OjtRecordRepo ojtRecordRepo;

    @Autowired
    private SupervisorRepo supervisorRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StudentTaskRepo studentTaskRepo;

    public String addTrainingPlan(TrainingPlanDTO trainingPlanDTO, String supervisorEmail) {
        Supervisor supervisor = supervisorRepo.findByUser_Email(supervisorEmail);
        TrainingPlan plan = new TrainingPlan(trainingPlanDTO.getTitle(), trainingPlanDTO.getDescription(),
                trainingPlanDTO.getStartDate(), trainingPlanDTO.getEndDate(), supervisor);
        if (trainingPlanRepo.findByTitleAndSupervisor(trainingPlanDTO.getTitle(), supervisor)==null) {
            if (trainingPlanDTO.getStartDate().isBefore(LocalDate.now())) {
                return "ERROR: Training plan start date cannot be before today's date.";
            }
            if (trainingPlanDTO.getEndDate().isAfter(trainingPlanDTO.getStartDate())) {
                trainingPlanRepo.save(plan);
                return "Training plan successfully added";
            }
            return "ERROR: Training plan end date must be after the start date";
        }
        return "ERROR: There is an existing training plan with that title";
    }

    public String assignTrainingPlan(AssignTrainingPlanDTO assignTrainingPlan, String supervisorEmail) {
        Supervisor supervisor = supervisorRepo.findByUser_Email(supervisorEmail);
        Optional<TrainingPlan> optionalPlan = trainingPlanRepo.findById(assignTrainingPlan.getTrainingPlanID());
        if (optionalPlan.isEmpty()) {
            return "ERROR: There is no existing training plan with this ID.";
        }
        TrainingPlan plan = optionalPlan.get();
        if (!plan.getSupervisor().equals(supervisor)) {
            return "ERROR: Unauthorized access. This training plan ID is not associated with this supervisor.";
        }
        List<String> invalidEmails = new ArrayList<>();
        List<String> alreadyAssignedEmails = new ArrayList<>();
        List<String> assignedInternEmails = new ArrayList<>();
    
        for (String studentEmail : assignTrainingPlan.getInternEmails()) {
            OjtRecord record = ojtRecordRepo.findByStudent_User_Email(studentEmail);
            if (record == null || !record.getSupervisor().equals(supervisor)) {
                invalidEmails.add(studentEmail);
                continue;
            }
            StudentTrainingPlan studentTrainingPlan = studentTrainingPlanRepo.findByStudentAndTrainingPlan(record.getStudent(), plan);
            if(studentTrainingPlan == null) {
                StudentTrainingPlan trainingPlan = new StudentTrainingPlan(record.getStudent(), plan);
                studentTrainingPlanRepo.save(trainingPlan);
            } else {
                alreadyAssignedEmails.add(studentEmail);
                continue;
            }
            List<Task> tasks = plan.getTasks();
            for (Task task : tasks) {
                if(studentTaskRepo.findByStudentAndTask(record.getStudent(), task)==null) {
                    StudentTask newStudentTask = new StudentTask(record.getStudent(), task);
                    studentTaskRepo.save(newStudentTask);
                }
            }
            assignedInternEmails.add(studentEmail);
        }
        StringBuilder returnMessage = new StringBuilder();
        if (!assignedInternEmails.isEmpty()) {
            returnMessage.append("Successfully assigned " + plan.getTitle() + " to: " + assignedInternEmails + "\n");
            sendAssignmentEmails(assignedInternEmails, supervisor, plan);
        }
        if(!alreadyAssignedEmails.isEmpty()) {
            returnMessage.append("NOTICE: These interns have already been assigned with the training plan " + alreadyAssignedEmails + "\n");
        }
        if (!invalidEmails.isEmpty()) {
            returnMessage.append("ERROR: No ojt records found from these emails " + invalidEmails + "\n"); 
        }
        return returnMessage.toString();
    }
    
    private void sendAssignmentEmails(List<String> assignedInternEmails, Supervisor supervisor, TrainingPlan plan) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        String messageTemplate = "Ding Dong! 🔔 \n\n%s %s has assigned you with a training plan.\n\n" +
                "Training Plan: %s\nDescription: %s\nStart Date: %s\nEnd Date: %s\nNumber of Tasks: %d";

        String message = String.format(messageTemplate,
                supervisor.getUser().getFirstname(),
                supervisor.getUser().getLastname(),
                plan.getTitle(),
                plan.getDescription(),
                plan.getStartDate().format(formatter),
                plan.getEndDate().format(formatter),
                plan.getTotalTasks());

        for (String email : assignedInternEmails) {
            emailService.sendSimpleMail(email, "Training Plan Assignment", message);
        }
    }

    public List<TrainingPlan> getAllTrainingPlanBySupervisor(String supervisorEmail) {
        return trainingPlanRepo.findBySupervisor_User_Email(supervisorEmail);
    }

    public String getAllTrainingPlansByStudent(String studentEmail) {
        OjtRecord ojtRecord = ojtRecordRepo.findByStudent_User_Email(studentEmail);
        if (ojtRecord == null) {
            return "ERROR: No OJT record found for the student.";
        }
        List<StudentTrainingPlan> studentTrainingPlans = studentTrainingPlanRepo.findByStudent(ojtRecord.getStudent());
        List<TrainingPlan> plans = new ArrayList<TrainingPlan>();
        for (StudentTrainingPlan studentTrainingPlan : studentTrainingPlans) {
            plans.add(studentTrainingPlan.getTrainingPlan());
        }
        return plans.toString();
    }

    public List<TrainingPlan> getAllTrainingPlansListByStudent(String studentEmail) {
        OjtRecord ojtRecord = ojtRecordRepo.findByStudent_User_Email(studentEmail);
        List<StudentTrainingPlan> studentTrainingPlans = studentTrainingPlanRepo.findByStudent(ojtRecord.getStudent());
        List<TrainingPlan> plans = new ArrayList<TrainingPlan>();
        for (StudentTrainingPlan studentTrainingPlan : studentTrainingPlans) {
            plans.add(studentTrainingPlan.getTrainingPlan());
        }
        return plans;
    }

    public List<TrainingPlan> getTrainingPlansBySupervisor(String supervisorEmail) {
        return trainingPlanRepo.findBySupervisor_User_Email(supervisorEmail);
    }

    public List<TrainingPlan> getTrainingPlansByStudent(String studentEmail, String user_type, String auth) {
        if(studentEmail.equals("all") && user_type.equals("admin")) {
            return trainingPlanRepo.findAll();
        }
        if(user_type.equals("supervisor")) {
            if(studentEmail.equals("all"))
                return trainingPlanRepo.findBySupervisor_User_Email(auth);
            else
                return trainingPlanRepo.findBySupervisor_User_EmailAndStudentTrainingPlans_Student_User_Email(auth, studentEmail);
        } 
        return trainingPlanRepo.findByStudentTrainingPlans_Student_User_Email(studentEmail);
    }
}
