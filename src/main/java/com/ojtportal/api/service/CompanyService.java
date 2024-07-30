package com.ojtportal.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ojtportal.api.entity.Company;
import com.ojtportal.api.entity.OjtRecord;
import com.ojtportal.api.entity.Student;
import com.ojtportal.api.entity.Supervisor;
import com.ojtportal.api.repositories.CompanyRepo;
import com.ojtportal.api.repositories.OjtRecordRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepo companyRepo;
    private final OjtRecordRepo ojtRecordRepo;

    public int addCompany(String company_name, String contactNo, String email, String address) {
        Company existing = companyRepo.findByCompanyName(company_name);
        if (existing == null) {
            existing = new Company(company_name);
        }
        List<String> emails = existing.getEmails();
        List<String> contactNos = existing.getContactNos();
        List<String> addresses = existing.getLocations();
        if (!emails.contains(email))
            emails.add(email);
        if (!contactNos.contains(contactNo))
            contactNos.add(contactNo);
        if (!addresses.contains(address))
            addresses.add(address);
        existing.setEmails(emails);
        existing.setContactNos(contactNos);
        existing.setLocations(addresses);
        companyRepo.save(existing);
        return 1;
    }

    public List<Student> getStudentsByCompany(String companyName) {
        List<OjtRecord> records = ojtRecordRepo.findByCompany_CompanyName(companyName);
        List<Student> students = new ArrayList<Student>();
        for (OjtRecord ojtRecord : records) {
            students.add(ojtRecord.getStudent());
        }
        return students;
    }

    public List<Supervisor> getSupervisorsByCompany(String companyName) {
        List<OjtRecord> records = ojtRecordRepo.findByCompany_CompanyName(companyName);
        List<Supervisor> supervisors = new ArrayList<Supervisor>();
        for (OjtRecord ojtRecord : records) {
            if(!supervisors.contains(ojtRecord.getSupervisor())) 
                supervisors.add(ojtRecord.getSupervisor());
        }
        return supervisors;
    }

    public int addCompany(String name) {
        if(companyRepo.findByCompanyName(name)==null) {
            companyRepo.save(new Company(name));
        }
        return 1;
    }

    public int addSupervisor(String company_name, Supervisor supervisor) {
        Company company = companyRepo.findByCompanyName(company_name);
        List<Supervisor> supervisors = company.getSupervisors();
        if(!supervisors.contains(supervisor)) {
            supervisors.add(supervisor);
            company.setSupervisors(supervisors);
            companyRepo.save(company);
            return 1;
        }
        return 0;
    }

    public List<Company> getAllCompanies(String email, String user_type) {
        OjtRecord record = null;
        if(user_type.equalsIgnoreCase("student")) {
            record = ojtRecordRepo.findByStudent_User_Email(email);
        } else if(user_type.equalsIgnoreCase("supervisor")) {
            record = ojtRecordRepo.findBySupervisor_User_Email(email).get(0);
        } else {
            return companyRepo.findAll();
        }
        return List.of(record.getCompany());
    }
}
