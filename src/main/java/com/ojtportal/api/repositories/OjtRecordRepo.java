package com.ojtportal.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.OjtRecord;
import com.ojtportal.api.entity.Supervisor;

@Repository
public interface OjtRecordRepo extends JpaRepository<OjtRecord, String>{
    OjtRecord findByStudent_Userid(Long userid);
    OjtRecord findByStudent_Studentid(String studentID);
    OjtRecord findByStudent_User_Email(String studentEmail);
    List<OjtRecord> findByCompany_CompanyName(String companyName);
    List<OjtRecord> findBySupervisor_User_Email(String supervisorEmail);
    OjtRecord findBySupervisor_User_EmailAndStudent_User_Email(String supervisorEmail, String studentEmail);
    List<OjtRecord> findBySupervisor(Supervisor supervisor);
}
