``` mermaid
sequenceDiagram
    autonumber
    actor s as Supervisor
    participant tpc as :TrainingPlanController
    participant tps as :TrainingPlanService
    participant tpr as :TrainingPlanRepo
    participant tp as :TrainingPlan
    participant stpr as :StudentTrainingPlanRepo
    participant es as :EmailService
    s->>tpc: PUT: /supervisor/update-training-plan<br>updateTrainingPlan()
    tpc->>tps: updateTrainingPlan(trainingPlanDTO)
    tps->>tpr: findById()
    tpr-->>tps: existing
    alt existing not null
        alt existing.supervisor equals supervisor
            break startDate before now
                tps-->>tpc: ERROR: Invalid start date
                tpc-->>s: ERROR: Invalid start date
            end 
            tps->>tp: setStartDate()
            break endDate before startDate
                tps-->>tpc: ERROR: Invalid end date
                tpc-->>s: ERROR: Invalid end date
            end 
            tps->>tp: setEndDate()
            tps->>tp: setDescription()
            tps->>tpr: findBySupervisor_User_Email()
            tpr-->>tps: plans
            loop every plan
                alt plan equals existing
                    alt plan.title equals trainingPlanDTO.title
                        tps-->>tpc: ERROR: Duplicate record
                        tpc-->>s: ERROR: Duplicate record
                    end
                end
            end
            alt noCopies is true
                tps->>tp: setTitle()
            end
            tp-->>tpr: save(existing)
            alt dateIsChanged is true
                tps->>tpr: findById()
                tpr-->>tps: existing
                tps->>stpr: findByTrainingPlan()
                stpr-->>tps: studentTrainingPlans()
                loop every studentTrainingPlan
                    tps->>tps: studentEmails.add(email)
                end
                tps->>tps: sendTrainingPlanUpdateNotification()
                loop every email
                    tps-->>es: sendSimpleEmail()
                end
            end
            tps-->>tpc: existing.toString
            tpc-->>s: ResponseEntity.ok(existing.toString)
        end
        tps-->>tpc: ERROR: Record inaccessible
        tpc-->>s: ERROR: Record inaccessible
    end
    tps-->>tpc: ERROR: No record found
    tpc-->>s: ERROR: No record found
```