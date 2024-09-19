``` mermaid
sequenceDiagram
    autonumber
    actor s as Supervisor
    participant tpc as :TrainingPlanController
    participant tps as :TrainingPlanService
    participant tpr as :TrainingPlanRepo
    participant sr as :SupervisorRepo
    participant tp as :TrainingPlan
    s->>tpc: POST:/supervisor/add-training-plan<br>addTrainingPlan()
    tpc->>tps: addTrainingPlan()
    tps->>sr: findByUser_Email()
    sr-->>tps: supervisor
    tps->>tp: TrainingPlan()
    tp-->>tps: plan
    tps->>tpr: findByTitleAndSupervisor()
    tpr-->>tps: TrainingPlan
    alt TrainingPlan == null
        break getStartDate() is before now
            tps-->>tpc: ERROR: Start date invalid
            tpc-->>s: ERROR: Start date invalid
        end
        alt getEndDate() is after getStartDate()
            tps-->>tpr: save(plan)
            tps-->>tpc: Success
            tpc-->>s: ResponseEntity.ok(Success)
        end
        tps-->>tpc: ERROR: End date invalid
        tpc-->>s: ERROR: End date invalid
    end
    tps-->>tpc: ERROR: Duplicate record
    tpc-->>s: ERROR: Duplicate record
```