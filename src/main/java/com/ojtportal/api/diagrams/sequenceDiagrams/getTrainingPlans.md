``` mermaid
sequenceDiagram
    autonumber
    actor u as User
    participant tpc as :TrainingPlanController
    participant up as :UserPrincipal
    participant tps as :TrainingPlanService
    participant tpr as :TrainingPlanRepo
    u->>tpc: GET: /get-training-plans<br>getAllTrainingPlansByStudent()
    loop every authority
        alt authority equals ROLE_SUPERVISOR
            tpc->>up: getEmail()
            up-->>tpc: auth
            tpc->>up: getAuthority()
            up-->>tpc: user_type
        else
            alt authority equals ROLE_STUDENT
                tpc->>up: getEmail()
                up-->>tpc: studentEmail
            else 
                tpc->>up: getAuthority()
                up-->>tpc: user_type
            end
        end
    end
    tpc->>tps: getTrainingPlansByStudent(studentEmail, user_type, auth)
    alt user_type is supervisor
        alt studentEmail is all
            tps->>tpr: findBySupervisor_User_Email()
            tpr-->>tps: List<TrainingPlan>
        else
            tps->>tpr: findBySupervisor_User_EmailAndStudentTrainingPlans_Student_User_Email()
            tpr-->>tps: List<TrainingPlan>
        end
    end
    alt user_type is not student
        alt studentEmail is all
            tps->>tpr: findAll()
            tpr-->>tps: List<TrainingPlan>
        end
    end
    tps->>tpr: findByStudentTrainingPlans_Student_User_Email()
    tpr-->>tps: List<TrainingPlan>
    tps-->>tpc: List<TrainingPlan>
    tpc-->>u: ResponseEntity.ok(List<TrainingPlan>)
```