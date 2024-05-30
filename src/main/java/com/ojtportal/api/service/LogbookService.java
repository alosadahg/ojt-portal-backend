package com.ojtportal.api.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ojtportal.api.dto.LogbookEntryDTO;
import com.ojtportal.api.dto.SkillDTO;
import com.ojtportal.api.entity.LogbookEntry;
import com.ojtportal.api.entity.OjtRecord;
import com.ojtportal.api.entity.Skill;
import com.ojtportal.api.entity.SkillTrend;
import com.ojtportal.api.entity.Student;
import com.ojtportal.api.entity.StudentSkillProficiency;
import com.ojtportal.api.entity.StudentTask;
import com.ojtportal.api.entity.StudentTrainingPlan;
import com.ojtportal.api.entity.Task;
import com.ojtportal.api.entity.UserEntity;
import com.ojtportal.api.model.LogbookStatus;
import com.ojtportal.api.model.TaskStatus;
import com.ojtportal.api.repositories.LogbookEntryRepo;
import com.ojtportal.api.repositories.OjtRecordRepo;
import com.ojtportal.api.repositories.SkillRepo;
import com.ojtportal.api.repositories.SkillTrendRepo;
import com.ojtportal.api.repositories.StudentRepo;
import com.ojtportal.api.repositories.StudentSkillProficiencyRepo;
import com.ojtportal.api.repositories.StudentTaskRepo;
import com.ojtportal.api.repositories.StudentTrainingPlanRepo;
import com.ojtportal.api.repositories.TaskRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogbookService {
    private final LogbookEntryRepo logbookEntryRepo;
    private final StudentTaskRepo studentTaskRepo;
    private final StudentTrainingPlanRepo studentTrainingPlanRepo;
    private final TaskRepo taskRepo;
    private final SkillService skillService;
    private final SkillRepo skillRepo;
    private final StudentRepo studentRepo;
    private final StudentSkillProficiencyRepo studentSkillProficiencyRepo;
    private final OjtRecordRepo ojtRecordRepo;
    private final EmailService emailService;
    private final SkillTrendRepo skillTrendRepo;

    public String addLogbookEntry(LogbookEntryDTO entry, String studentEmail) {
        // Finding student and OJT record
        OjtRecord record = ojtRecordRepo.findByStudent_User_Email(studentEmail);
        if (record == null) {
            return "ERROR: No OJT record found.";
        }
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime now = ldt.atZone(ZoneId.of("Asia/Manila"));
        // System.out.println("NOW: "+ now.format(DateTimeFormatter.ofPattern("EEEE,
        // MMMM dd, yyyy, hh:mm a")));
        LocalDateTime timeIn = entry.getEntry().getTimeIn();
        LocalDateTime timeOut = entry.getEntry().getTimeOut();
        // Validating time entries
        if (!timeOut.isAfter(timeIn)) {
            return "ERROR: Time in must be earlier than the time out.";
        }
        if (timeIn.isAfter(now.toLocalDateTime()) || timeOut.isAfter(now.toLocalDateTime())) {
            return "ERROR: Time must not be beyond the current date and time: " + now.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy, hh:mm a")) + ".";
        }
        // Checking for overlapping logbook entries
        List<LogbookEntry> existingEntries = record.getLogbookEntries();
        for (LogbookEntry existing : existingEntries) {
            if (existing.getTimeout().isAfter(timeIn)) {
                return "ERROR: Time in cannot be before the time out in recent logbook entry record on " + existing.getTimeout().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy, hh:mm a")) + ".";
            }
        }
        LogbookEntry newEntry = new LogbookEntry(timeIn, timeOut, entry.getEntry().getActivities(), record);
        // Associate tasks with the new logbook entry
        if (entry.getTaskIDs() != null) {
            List<Task> tasks = new ArrayList<Task>();
            for (Integer taskID : entry.getTaskIDs()) {
                Optional<Task> taskOpt = taskRepo.findById(taskID);
                if (taskOpt.isPresent()) {
                    Task task = taskOpt.get();
                    StudentTask studentTask = studentTaskRepo.findByStudentAndTask(record.getStudent(), task);
                    if (studentTask == null) {
                        return "ERROR: Invalid Task. Supervisor has not assigned the student with this task ID: " + taskID;
                    }
                    studentTask.setStatus(TaskStatus.COMPLETED);
                    studentTaskRepo.save(studentTask);
                    // Update the completedTasks count for the StudentTrainingPlan
                    StudentTrainingPlan studentTrainingPlan = studentTrainingPlanRepo
                            .findByStudentAndTrainingPlan(record.getStudent(), task.getTrainingplan());
                    studentTrainingPlan.setCompletedTasks(studentTrainingPlan.getCompletedTasks() + 1);
                    studentTrainingPlanRepo.save(studentTrainingPlan);
                    tasks.add(task);
                }
            }
            newEntry.setTasks(tasks);
        }
        // Handling skills associated with the logbook entry
        if (entry.getSkills() != null) {
            for (SkillDTO skillDTO : entry.getSkills()) {
                Skill added = skillService.addSkill(skillDTO.getSkill_name(), skillDTO.getDomain());
                Skill skill = skillRepo.findBySkillNameAndDomain(skillDTO.getSkill_name(), skillDTO.getDomain());
                StudentSkillProficiency proficiency = studentSkillProficiencyRepo.findBySkillAndStudent(skill,
                        record.getStudent());
                if (added == null) {
                    SkillTrend trend = skillTrendRepo.findBySkill(skill);
                    trend.setSkillFrequency(trend.getSkillFrequency() + 1);
                    skillTrendRepo.save(trend);
                }
                if (proficiency == null) {
                    proficiency = new StudentSkillProficiency(record.getStudent(), skill);
                    proficiency.setFrequencyOfUse(1);
                } else {
                    proficiency.setFrequencyOfUse(proficiency.getFrequencyOfUse() + 1);
                }
                studentSkillProficiencyRepo.save(proficiency);
            }
        }
        // Saving the OJT record and return the new logbook entry
        existingEntries.add(newEntry);
        record.setLogbookEntries(existingEntries);
        logbookEntryRepo.save(newEntry);
        ojtRecordRepo.save(record);
        return logbookEntryRepo.findByTimein(timeIn).toString();
    }

    public String approveLogbookEntry(int entry_id, String remarks, String supervisor_email) {
        Optional<LogbookEntry> entryOpt = logbookEntryRepo.findById(entry_id);
        if (entryOpt.isEmpty())
            return "ERROR: Record for entry ID: " + entry_id + " not found.";

        if(!entryOpt.get().getOjtrecord().getSupervisor().getUser().getEmail().equals(supervisor_email)) {
            return "ERROR: Supervisor has no access to this logbook entry ID.";
        }
        LogbookEntry entry = entryOpt.get();
        if (!entry.getStatus().equals(LogbookStatus.APPROVED)) {
            entry.setStatus(LogbookStatus.APPROVED);
            entry.setRemarks(remarks);
            entry.getOjtrecord().setRenderedHours(entry.getOjtrecord().getRenderedHours() + entry.getTotalHrs());
            ojtRecordRepo.save(entry.getOjtrecord());
            logbookEntryRepo.save(entry);

            UserEntity student = entry.getOjtrecord().getStudent().getUser();
            UserEntity supervisor = entry.getOjtrecord().getSupervisor().getUser();
            String emailRemarks = (remarks.isEmpty()) ? "" : "Remarks: " + remarks;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy, hh:mm a");

            emailService.sendSimpleMail(student.getEmail(), "Approved Logbook Entry",
                    "Great job " + student.getFirstname() + "! 🏆" +
                            "\n\nYour logbook entry has been approved by " + supervisor.getFirstname() + " "
                            + supervisor.getLastname() +
                            "\n\nDetails:\nTime in: " + entry.getTimein().format(formatter) + "\n" +
                            "Time out: " + entry.getTimeout().format(formatter) + "\n" +
                            "Activities: " + entry.getActivities() + "\n" +
                            emailRemarks);
            return "Successfully approved logbook.";
        }
        return "ERROR: Logbook entry is already approved";
    }

    public String rejectLogbookEntry(int entry_id, String remarks, String supervisor_email) {
        Optional<LogbookEntry> entryOpt = logbookEntryRepo.findById(entry_id);
        if (entryOpt.isEmpty())
        return "ERROR: Record for entry ID: " + entry_id + " not found.";

        if(!entryOpt.get().getOjtrecord().getSupervisor().getUser().getEmail().equals(supervisor_email)) {
            return "ERROR: Supervisor has no access to this logbook entry ID.";
        }

        if (remarks.isBlank() || remarks.isEmpty()) {
            return "ERROR: Remarks should not be empty.";
        }

        LogbookEntry entry = entryOpt.get();
        if(entry.getStatus().equals(LogbookStatus.APPROVED)) {
            return "ERROR: Logbook entry is already approved. Cannot proceed with further transactions.";
        }
        if (entry.getStatus().equals(LogbookStatus.PENDING)) {
            entry.setRemarks(remarks);
            entry.setStatus(LogbookStatus.REJECTED);
            logbookEntryRepo.save(entry);

            UserEntity student = entry.getOjtrecord().getStudent().getUser();
            UserEntity supervisor = entry.getOjtrecord().getSupervisor().getUser();
            String emailRemarks = "Remarks: " + remarks;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy, hh:mm a");

            emailService.sendSimpleMail(student.getEmail(), "Logbook Entry For Revisions",
                    "There has been some suggestions in your logbook entry " + student.getFirstname() + "." +
                            "\n\nYour logbook entry has been reviewed by " + supervisor.getFirstname() + " "
                            + supervisor.getLastname() +
                            " and there have been a few corrections given to your entry." +
                            "\n\nDetails:\nTime in: " + entry.getTimein().format(formatter) + "\n" +
                            "Time out: " + entry.getTimeout().format(formatter) + "\n" +
                            "Activities: " + entry.getActivities() + "\n" +
                            emailRemarks);
            return "Logbook entry is now rejected. Student is notified for revisions.";
        }
        return "ERROR: Logbook entry is already rejected.";
    }

    public List<LogbookEntry> getStudentLogbook(String email) {
        return logbookEntryRepo.findByOjtrecord_Student_User_Email(email);
    }

    public List<LogbookEntry> getSupervisorLogbooks(String supervisorEmail) {
        return logbookEntryRepo.findByOjtrecord_Supervisor_User_Email(supervisorEmail);
    }

    public String updateLogbookEntry(LogbookEntryDTO logbookEntryDTO, String email) {
        LogbookEntry logbookEntry = logbookEntryRepo.findById(logbookEntryDTO.getEntry().getEntryID()).get();
        if (logbookEntry != null) {
            if(!logbookEntry.getOjtrecord().getStudent().getUser().getEmail().equals(email)) {
                return "ERROR: Invalid logbook entry ID: " + logbookEntryDTO.getEntry().getEntryID();
            }
            if(logbookEntry.getStatus().equals(LogbookStatus.REJECTED)){
                logbookEntry.setStatus(LogbookStatus.PENDING);
            }
            if (logbookEntry.getStatus().equals(LogbookStatus.APPROVED)) {
                return "ERROR: Logbook is already approved. Update is prohibited";
            }
            Student student = studentRepo.findByUser_Email(email);
            logbookEntry.setActivities(logbookEntryDTO.getEntry().getActivities());
            List<SkillDTO> skills = logbookEntryDTO.getSkills();
            List<Skill> logbookSkills = new ArrayList<Skill>();
            for (SkillDTO skillDTO : skills) {
                Skill skill = skillRepo.findBySkillNameAndDomain(skillDTO.getSkill_name(), skillDTO.getDomain());
                if (skill == null) {
                    skill = skillService.addSkill(skillDTO.getSkill_name(), skillDTO.getDomain());
                    SkillTrend trend = skillTrendRepo.findBySkill(skill);
                    trend.setSkillFrequency(trend.getSkillFrequency() + 1);
                    skillTrendRepo.save(trend);
                }
                if (!logbookSkills.contains(skill)) {
                    StudentSkillProficiency proficiency = studentSkillProficiencyRepo.findBySkillAndStudent(skill,
                            student);
                    if (proficiency == null) {
                        proficiency = new StudentSkillProficiency(student, skill);
                        proficiency.setFrequencyOfUse(1);
                    } else {
                        proficiency.setFrequencyOfUse(proficiency.getFrequencyOfUse() + 1);
                    }
                    studentSkillProficiencyRepo.save(proficiency);
                    logbookSkills.add(skill);
                }
            }
            logbookEntry.setSkills(logbookSkills);
            List<Task> existingTasks = logbookEntry.getTasks();
            for (Task task : existingTasks) {
                StudentTask studentTask = studentTaskRepo.findByStudentAndTask(student, task);
                studentTask.setStatus(TaskStatus.ONGOING);
                studentTaskRepo.save(studentTask);
                StudentTrainingPlan studentTrainingPlan = studentTrainingPlanRepo
                        .findByStudentAndTrainingPlan(student, task.getTrainingplan());
                studentTrainingPlan.setCompletedTasks(studentTrainingPlan.getCompletedTasks() - 1);
                studentTrainingPlanRepo.save(studentTrainingPlan);
            }
            logbookEntry.setTasks(existingTasks);
            if (logbookEntryDTO.getTaskIDs() != null) {
                List<Task> tasks = new ArrayList<Task>();
                for (Integer taskID : logbookEntryDTO.getTaskIDs()) {
                    Optional<Task> taskOpt = taskRepo.findById(taskID);
                    if (taskOpt.isPresent()) {
                        Task task = taskOpt.get();
                        StudentTask studentTask = studentTaskRepo.findByStudentAndTask(student, task);
                        if (studentTask == null) {
                            return "ERROR: Invalid Task. Supervisor has not assigned the student with this task";
                        }
                        studentTask.setStatus(TaskStatus.COMPLETED);
                        studentTaskRepo.save(studentTask);
                        // Update the completedTasks count for the StudentTrainingPlan
                        StudentTrainingPlan studentTrainingPlan = studentTrainingPlanRepo
                                .findByStudentAndTrainingPlan(student, task.getTrainingplan());
                        studentTrainingPlan.setCompletedTasks(studentTrainingPlan.getCompletedTasks() + 1);
                        studentTrainingPlanRepo.save(studentTrainingPlan);
                        tasks.add(task);
                    }
                }
                logbookEntry.setTasks(tasks);
                logbookEntryRepo.save(logbookEntry);
            }
            return logbookEntry.toString();
        }
        return "ERROR: Logbook entry not found";
    }
}
