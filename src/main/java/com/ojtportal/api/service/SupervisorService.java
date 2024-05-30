package com.ojtportal.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.*;

import com.ojtportal.api.entity.Company;
import com.ojtportal.api.entity.Supervisor;
import com.ojtportal.api.entity.UserEntity;
import com.ojtportal.api.model.Role;
import com.ojtportal.api.repositories.CompanyRepo;
import com.ojtportal.api.repositories.SupervisorRepo;
import com.ojtportal.api.repositories.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@EnableCaching
@RequiredArgsConstructor
public class SupervisorService {
    @Autowired
    UserService userService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    SupervisorRepo supervisorRepo;
    @Autowired
    CompanyService companyService;
    @Autowired
    CompanyRepo companyRepo;

    public int registerSupervisor(UserEntity user, String company_name, String work_email, String work_contactNo,
            String location, String designation) {
        companyService.addCompany(company_name, work_contactNo, work_email, location);
        Company company = companyRepo.findByCompanyName(company_name);
        user.setAccountType(Role.ROLE_SUPERVISOR);
        if(userService.addUser(user)==0) {
            user = userRepo.findByEmail(user.getEmail());
        }
        Supervisor supervisor = new Supervisor(designation, work_email, work_contactNo, location, user, company);
        if(!supervisorRepo.findById(user.getUid()).isPresent()) {
            return companyService.addSupervisor(company_name, supervisor);
        }
        return 0;
    }


}
