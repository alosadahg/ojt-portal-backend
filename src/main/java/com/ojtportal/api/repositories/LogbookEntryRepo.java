package com.ojtportal.api.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.LogbookEntry;

@Repository
public interface LogbookEntryRepo extends JpaRepository<LogbookEntry, Integer> {
    LogbookEntry findByTimein(LocalDateTime timeIn);
    List<LogbookEntry> findByOjtrecord_RecordNo(String recordNo);
    List<LogbookEntry> findByOjtrecord_Supervisor_User_Email(String email);
    List<LogbookEntry> findByOjtrecord_Student_User_Email(String email);
}
