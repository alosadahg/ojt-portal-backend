package com.ojtportal.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.Evaluation;
import com.ojtportal.api.entity.OjtRecord;

@Repository
public interface EvaluationRepo extends JpaRepository<Evaluation, Integer>{
    Evaluation findByOjtrecord_RecordNo(String ojt_record_no);
    Evaluation findByOjtrecord(OjtRecord record);
    Evaluation findByOjtrecord_Student_User_Email(String email);
    List<Evaluation> findByOjtrecord_Supervisor_User_Email(String email);
    Evaluation findByOjtrecord_Student_User_EmailAndOjtrecord_Supervisor_User_Email(String student, String supervisor);
}
