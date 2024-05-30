package com.ojtportal.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
// import org.springframework.cache.annotation.CacheEvict;
// import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ojtportal.api.config.generator.PKGenerator;
import com.ojtportal.api.config.security.JwtIssuer;
import com.ojtportal.api.config.security.UserPrincipal;
import com.ojtportal.api.dto.OJTRecordResponseDTO;
import com.ojtportal.api.dto.StudentDTO;
import com.ojtportal.api.dto.StudentFullDTO;
import com.ojtportal.api.dto.SupervisorDTO;
import com.ojtportal.api.dto.SupervisorFullDTO;
import com.ojtportal.api.dto.TaskListDTO;
import com.ojtportal.api.dto.TaskResponseDTO;
import com.ojtportal.api.dto.TrainingPlanResponseDTO;
import com.ojtportal.api.dto.UserDTO;
import com.ojtportal.api.entity.OjtRecord;
import com.ojtportal.api.entity.Student;
import com.ojtportal.api.entity.StudentTask;
import com.ojtportal.api.entity.StudentTrainingPlan;
import com.ojtportal.api.entity.Supervisor;
import com.ojtportal.api.entity.Task;
import com.ojtportal.api.entity.TrainingPlan;
import com.ojtportal.api.entity.UserEntity;
import com.ojtportal.api.model.AccountStatus;
import com.ojtportal.api.model.LoginResponse;
import com.ojtportal.api.model.Role;
import com.ojtportal.api.repositories.OjtRecordRepo;
import com.ojtportal.api.repositories.StudentRepo;
import com.ojtportal.api.repositories.StudentTaskRepo;
import com.ojtportal.api.repositories.StudentTrainingPlanRepo;
import com.ojtportal.api.repositories.SupervisorRepo;
import com.ojtportal.api.repositories.TaskRepo;
import com.ojtportal.api.repositories.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@EnableCaching
@RequiredArgsConstructor
public class UserService {
    private final TrainingPlanService trainingPlanService;
    private final TaskRepo taskRepo;
    private final OjtRecordRepo ojtRecordRepo;
    private final StudentRepo studentRepo;
    private final UserRepo userRepo;
    private final JwtIssuer issuer;
    private final StudentTrainingPlanRepo studentTrainingPlanRepo;
    private final StudentTaskRepo studentTaskRepo;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final SupervisorRepo supervisorRepo;
    private UserPrincipal principal;

    private String verification;

    public List<UserEntity> getAllUsers() {
        return userRepo.findAll();
    }

    public int addUser(UserEntity user) {
        if (user != null) {
            if (userRepo.findByEmail(user.getEmail()) == null) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPwd = encoder.encode(user.getPassword());
                user.setPassword(encodedPwd);
                if(user.getAccountType().equals(Role.ROLE_CHAIR) || user.getAccountType().equals(Role.ROLE_INSTRUCTOR)) {
                    user.setUserStatus(AccountStatus.ACTIVE);
                } else {
                    user.setUserStatus(AccountStatus.PENDING);
                }
                userRepo.save(user);
                return 1;
            }
        }
        return 0;
    }

    public UserEntity getUserEntity(Long uid) {
        return Optional.of(userRepo.findById(uid).get()).orElse(null);
    }

    public String login(UserEntity user) {
        UserEntity existingUser = userRepo.findByEmail(user.getEmail());
        if (existingUser != null) {
            getUserEntity(existingUser.getUid());
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(user.getPassword(), existingUser.getPassword())) {
                switch (existingUser.getUserStatus()) {
                    case ACTIVE:
                        Authentication auth = authenticationManager
                                .authenticate(
                                        new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        principal = (UserPrincipal) auth.getPrincipal();

                        String token = issuer.issue(JwtIssuer.Request.builder()
                                .userid(principal.getUid())
                                .email(principal.getEmail())
                                .roles(principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                                .build());

                        if (existingUser.getAccountType().equals(Role.ROLE_STUDENT)) {
                            StudentFullDTO studentFullDTO = getStudentFullInfo(existingUser.getEmail());
                            studentFullDTO.setAccessToken(token);
                            return studentFullDTO.toString();
                        }

                        if (existingUser.getAccountType().equals(Role.ROLE_SUPERVISOR)) {
                            SupervisorFullDTO supervisor = getSupervisorInfo(existingUser.getEmail());
                            supervisor.setAccessToken(token);
                            return supervisor.toString();
                        }

                        return LoginResponse.builder().userInfo(existingUser).accessToken(token).build().toString();
                    case PENDING:
                        verification = PKGenerator.generate("verification");
                        String body = "Account activation code: " + verification;

                        emailService.sendSimpleMail(existingUser.getEmail(), "OJT Portal Account Activation", body);
                        return "ERROR: Account is still pending verification. Check email for the account activation code.";
                    case RESTRICTED:
                        return "ERROR: Account is restricted.";
                }
            }
            return "ERROR: Invalid password";
        }
        return "ERROR: User does not exist";
    }

    public String getUserInfo(Long uid) {
        try {
            UserEntity user = getUserEntity(uid);
            if (user != null) {
                return user.toString();
            }
        } catch (Exception e) {
            return "0";
        }
        return "-1";
    }

    public int authenticate(String email, String password, String verificationCode, String type) {
        if (verificationCode.equals(this.verification)) {
            UserEntity user = userRepo.findByEmail(email);
            if (type.equals("activation")) {
                user.setUserStatus(AccountStatus.ACTIVE);
            } else if (type.equals("password-change")) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPwd = encoder.encode(password);
                user.setPassword(encodedPwd);
            }
            userRepo.save(user);
            return 1;
        }
        return 0;
    }

    public void activateUser(Long uid) {
        UserEntity user = getUserEntity(uid);
        user.setUserStatus(AccountStatus.ACTIVE);
        userRepo.save(user);
    }

    public void restrictUser(Long uid) {
        UserEntity user = getUserEntity(uid);
        user.setUserStatus(AccountStatus.RESTRICTED);
        userRepo.save(user);
    }

    public int registerAdmin(UserEntity user) {
        user.setAccountType(Role.ROLE_ADMIN);
        return addUser(user);
    }

    public int registerInstructor(UserEntity user) {
        user.setAccountType(Role.ROLE_INSTRUCTOR);
        return addUser(user);
    }

    public int registerChair(UserEntity user) {
        user.setAccountType(Role.ROLE_CHAIR);
        return addUser(user);
    }

    public int forgetPassword(String email) {
        UserEntity existing = userRepo.findByEmail(email);
        if (existing != null) {
            verification = PKGenerator.generate("otp");
            String body = "Password change OTP: " + verification;

            emailService.sendSimpleMail(email, "Password Change OTP", body);
            return 1;
        }
        return 0;
    }

    public String changePassword(Long uid, String oldPassword, String newPassword) {
        UserEntity user = getUserEntity(uid);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (user != null) {
            if (encoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(encoder.encode(newPassword));
                userRepo.save(user);
                return "Password changed successfully!";
            }
            return "ERROR: Old Password does not match with existing password.";
        }
        return "ERROR: User record does not exist.";
    }

    public StudentFullDTO getStudentFullInfo(String studentEmail) {
        Student student = studentRepo.findByUser_Email(studentEmail);
        StudentDTO studentDTO = new StudentDTO(student.getUser().getEmail(),
                student.getUser().getFirstname(), student.getUser().getLastname(), student.getStudentid(),
                student.getDegreeProgram());
        OjtRecord record = ojtRecordRepo.findByStudent_User_Email(studentEmail);
        if (record != null) {
            Supervisor supervisor = Optional.of(record.getSupervisor()).orElse(null);
            SupervisorDTO supervisorDTO = new SupervisorDTO(supervisor.getUser().getEmail(),
                    supervisor.getUser().getFirstname(), supervisor.getUser().getLastname(),
                    supervisor.getWork_email(), supervisor.getWork_contactNo(), supervisor.getWork_address(),
                    supervisor.getCompany().getCompanyName(), supervisor.getPosition());
            List<TrainingPlan> trainingPlans = trainingPlanService.getAllTrainingPlansListByStudent(studentEmail);
            List<TrainingPlanResponseDTO> trainingPlanDTOs = new ArrayList<TrainingPlanResponseDTO>();
            for (TrainingPlan trainingPlan : trainingPlans) {
                StudentTrainingPlan studentTrainingPlan = studentTrainingPlanRepo.findByStudentAndTrainingPlan(student, trainingPlan);
                TrainingPlanResponseDTO trainingPlanDTO = new TrainingPlanResponseDTO(trainingPlan.getTitle(),
                        trainingPlan.getDescription(), trainingPlan.getStartDate(), trainingPlan.getEndDate(), trainingPlan.getTotalTasks(), studentTrainingPlan.getCompletedTasks());
                trainingPlanDTOs.add(trainingPlanDTO);
            }
            OJTRecordResponseDTO ojtRecordResponseDTO = new OJTRecordResponseDTO(record.getDesignation(), record.getDepartment(),
                    record.getOjtHours(), record.getRenderedHours(), record.getStatus());
            List<TaskListDTO> taskListDTOs = new ArrayList<TaskListDTO>();
            for (TrainingPlan trainingPlan : trainingPlans) {
                List<Task> tasks = taskRepo.findByTrainingplan(trainingPlan);
                List<TaskResponseDTO> taskDTOs = new ArrayList<TaskResponseDTO>();
                for (Task task : tasks) {
                    StudentTask studentTask = studentTaskRepo.findByStudentAndTask(student, task);
                    if(studentTask!=null) {
                        TaskResponseDTO taskDTO = new TaskResponseDTO(task.getTitle(), task.getDescription(), task.getObjective(), studentTask.getStatus());
                        taskDTOs.add(taskDTO);
                    }
                }
                TaskListDTO taskListDTO = new TaskListDTO(trainingPlan.getTitle(), taskDTOs);
                taskListDTOs.add(taskListDTO);
            }
            StudentFullDTO studentFullDTO = new StudentFullDTO(studentDTO, supervisorDTO, trainingPlanDTOs,
                    ojtRecordResponseDTO, taskListDTOs, "", Role.ROLE_STUDENT);
            return studentFullDTO;
        } 
        StudentFullDTO studentFullDTO = new StudentFullDTO(studentDTO, new SupervisorDTO(),new ArrayList<TrainingPlanResponseDTO>(),new OJTRecordResponseDTO(),new ArrayList<TaskListDTO>(), "", Role.ROLE_STUDENT);
        return studentFullDTO;
    }

    private SupervisorFullDTO getSupervisorInfo(String supervisorEmail) {
        Supervisor supervisor = supervisorRepo.findByUser_Email(supervisorEmail);
        UserDTO userDTO = new UserDTO(supervisor.getUser().getEmail(), supervisor.getUser().getFirstname(), supervisor.getUser().getLastname());
        SupervisorFullDTO supervisorDTO = new SupervisorFullDTO(userDTO, supervisor.getWork_email(), supervisor.getWork_contactNo(), 
            supervisor.getWork_address(), supervisor.getCompany().getCompanyName(), supervisor.getPosition());
        supervisorDTO.setAccountType(Role.ROLE_SUPERVISOR);
        supervisorDTO.setUserStatus(supervisor.getUser().getUserStatus());
        return supervisorDTO;
    }
}
