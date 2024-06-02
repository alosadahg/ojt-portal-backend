package com.ojtportal.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojtportal.api.entity.Evaluation;
import com.ojtportal.api.entity.OjtRecord;
import com.ojtportal.api.entity.Supervisor;
import com.ojtportal.api.repositories.EvaluationRepo;
import com.ojtportal.api.repositories.OjtRecordRepo;
import com.ojtportal.api.repositories.SupervisorRepo;

@Service
public class EvaluationService {
    @Autowired
    private OjtRecordRepo ojtRecordRepo;
    @Autowired
    private EvaluationRepo evaluationRepo;
    @Autowired
    private SupervisorRepo supervisorRepo;

    public List<Evaluation> getEvaluationRecord(String studentEmail, String auth, String user_type) {
        if(user_type.equals("supervisor")) {
            if(studentEmail.equals("all"))
                return evaluationRepo.findByOjtrecord_Supervisor_User_Email(auth);
            else
                return List.of(evaluationRepo.findByOjtrecord_Student_User_EmailAndOjtrecord_Supervisor_User_Email(studentEmail, auth));
        } 
        if(studentEmail.equals("all") && !user_type.equals("student")) {
            return evaluationRepo.findAll();
        }
        return List.of(evaluationRepo.findByOjtrecord_Student_User_Email(studentEmail));
    }

    public String addInstructorFeedback(String remarks, double grade, String student_email) {
        OjtRecord record = ojtRecordRepo.findByStudent_User_Email(student_email);
        if (record != null) {
            Evaluation evaluation = evaluationRepo.findByOjtrecord_Student_User_Email(student_email);
            if (evaluation == null) {
                evaluation = new Evaluation(record);
            }
            if(!evaluation.getInstructorRemark().isEmpty() || !evaluation.getInstructorRemark().isBlank() || evaluation.getInstructorgivenGrade()>0) {
                return "ERROR: Instructor have already evaluated the student.";
            }
            if (evaluation.getInstructorRemark().isEmpty() || evaluation.getInstructorRemark().isBlank())
                evaluation.setInstructorRemark(remarks);
            if (evaluation.getInstructorgivenGrade() == 0)
                evaluation.setInstructorgivenGrade(grade);
            evaluation.calculateTotalGrade();
            evaluationRepo.save(evaluation);
            return "Instructor Evaluation successfully saved!";
        }
        return "ERROR: No record found.";
    }

    public String addSupervisorFeedback(String remarks, double grade, String student_email, String supervisorEmail) {
        OjtRecord record = ojtRecordRepo.findByStudent_User_Email(student_email);
        if (record != null) {
            Supervisor supervisor = supervisorRepo.findByUser_Email(supervisorEmail);
            Evaluation evaluation = evaluationRepo.findByOjtrecord_Student_User_Email(student_email);
            if (evaluation == null) {
                evaluation = new Evaluation(record);
            }
            if(!evaluation.getSupervisorRemark().isEmpty() || !evaluation.getSupervisorRemark().isBlank() || evaluation.getSupervisorgivenGrade()>0) {
                return "ERROR: Supervisor have already evaluated the student.";
            }
            List<OjtRecord> records = ojtRecordRepo.findBySupervisor(supervisor);
            if (records.contains(record)) {
                if (evaluation.getSupervisorRemark().isEmpty() || evaluation.getSupervisorRemark().isBlank())
                    evaluation.setSupervisorRemark(remarks);
                if (evaluation.getSupervisorgivenGrade() == 0)
                    evaluation.setSupervisorgivenGrade(grade);
                evaluation.calculateTotalGrade();
                evaluationRepo.save(evaluation);
                return "Supervisor Evaluation successfully saved!";
            }
        }
        return "ERROR: No record found.";
    }

    public int addStudentFeedback(String feedback, String email) {
        OjtRecord record = ojtRecordRepo.findByStudent_User_Email(email);
        if (record != null) {
            Evaluation evaluation = evaluationRepo.findByOjtrecord(record);
            evaluation.setStudentFeedback(feedback);
            evaluationRepo.save(evaluation);
        }
        return 0;
    }
}
