package com.ojtportal.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojtportal.api.dto.OJTRecordDTO;
import com.ojtportal.api.entity.Company;
import com.ojtportal.api.entity.Evaluation;
import com.ojtportal.api.entity.OjtRecord;
import com.ojtportal.api.entity.Student;
import com.ojtportal.api.entity.Supervisor;
import com.ojtportal.api.entity.UnregisteredIntern;
import com.ojtportal.api.entity.UserEntity;
import com.ojtportal.api.model.OJTStatus;
import com.ojtportal.api.repositories.CompanyRepo;
import com.ojtportal.api.repositories.EvaluationRepo;
import com.ojtportal.api.repositories.OjtRecordRepo;
import com.ojtportal.api.repositories.StudentRepo;
import com.ojtportal.api.repositories.SupervisorRepo;
import com.ojtportal.api.repositories.UnregisteredInternRepo;
import com.ojtportal.api.repositories.UserRepo;

@Service
public class OjtRecordService {
    @Autowired
    private OjtRecordRepo ojtRecordRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private SupervisorRepo supervisorRepo;
    @Autowired
    private CompanyRepo companyRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UnregisteredInternRepo unregisteredInternRepo;
    @Autowired
    private EvaluationRepo evaluationRepo;

    public String addOjtRecord(OJTRecordDTO record, String supervisorEmail) {
        String notif = "You have already invited this intern";
        UserEntity existing = userRepo.findByEmail(record.getStudentEmail());
        Student student = null;
        boolean existsInDB = false;
        if (existing != null)
            student = studentRepo.findById(existing.getUid()).get();
            Supervisor supervisor = supervisorRepo.findByUser_Email(supervisorEmail);
            if (supervisor == null) return "ERROR: Supervisor record does not exist";
            UserEntity supervisorUser = userRepo.findById(supervisor.getUserid()).get();
            Company company = companyRepo.findById(supervisor.getCompany().getId()).get();
            String recordPK = "";
            if (existing != null && student != null) {
                OjtRecord existingRecord = ojtRecordRepo.findByStudent_Userid(student.getUserid());
                if (existingRecord == null) {
                    if(unregisteredInternRepo.existsById(record.getStudentEmail())) {
                        unregisteredInternRepo.deleteById( record.getStudentEmail());
                    }
                    OjtRecord newRecord = new OjtRecord(record.getDesignation(), record.getDepartment(), record.getOjtHours(), company, student,
                            supervisor);
                    Evaluation eval = new Evaluation(newRecord);
                    evaluationRepo.save(eval);
                    ojtRecordRepo.save(newRecord);
                    recordPK = ojtRecordRepo.findByStudent_Userid(student.getUserid()).getRecordNo();
                    notif = "Intern is now invited to join the team.";
                }
                else if (existingRecord.getStatus()==OJTStatus.PENDING) {
                    notif = "NOTICE: Intern is already invited, awaiting their confirmation.";
                    existsInDB = true;
                } else {
                    notif = (existingRecord.getSupervisor().getUser().getEmail().equals(supervisorEmail)) 
                        ? "ERROR: Intern is already registered by another supervisor."
                        : "NOTICE: Intern is already in your team.";
                    existsInDB = true;
                }
            } else {
                unregisteredInternRepo.save(new UnregisteredIntern(record.getStudentEmail(), supervisor));
                notif = "NOTICE: Intern will still have to create an account in the portal. We will notify you once they join the portal.";
            }
            String message = (student != null)
                    ? "Please enter this code in the portal to join: " + recordPK
                    : "Please create a student account in the portal to proceed.";
            if(!existsInDB) {
                emailService.sendSimpleMail(record.getStudentEmail(), "OJT Team Invitation",
                        "Congratulations in starting your internship! 🚀" +
                                "\n\n" + supervisorUser.getFirstname() + " " + supervisorUser.getLastname() +
                                " is inviting you to join their team of interns in " + company.getCompanyName()
                                + ".\n\n" + message);
            }
            return notif;
    }

    public int joinTeam(String studentEmail, String ojt_code) {
        OjtRecord record = ojtRecordRepo.findById(ojt_code).get();
        if(record.getStudent().getUser().getEmail().equals(studentEmail)) {
            record.setStatus(OJTStatus.ONGOING);
            ojtRecordRepo.save(record);
            return 1;
        }
        return 0;
    }

    public List<OjtRecord> getAllOjtRecords(String studentEmail, String user_type, String auth) {
        if(studentEmail.equals("all") && user_type.equals("admin")) {
            return ojtRecordRepo.findAll();
        }
        if(user_type.equals("supervisor")) {
            if(studentEmail.equals("all"))
                return ojtRecordRepo.findBySupervisor_User_Email(auth);
            else
                return List.of(ojtRecordRepo.findBySupervisor_User_EmailAndStudent_User_Email(auth, studentEmail));
        } 
        return List.of(ojtRecordRepo.findByStudent_User_Email(studentEmail));
    }
}
