``` mermaid
sequenceDiagram
    autonumber
    actor s as Supervisor
    participant oc as :OJTRecordController
    participant os as :OjtRecordService
    participant ur as :UserRepo
    participant sr as :StudentRepo
    participant spr as :SupervisorRepo
    participant cr as :CompanyRepo
    participant or as :OjtRecordRepo
    participant uir as :UnregisteredInternRepo
    participant ojr as :OjtRecord
    participant es as :EmailService
    s->>oc: POST: supervisor/add-intern<br>addIntern()
    oc->>os: addOjtRecord()
    os->>ur: findByEmail()
    ur-->>os: existing
    alt existion not null
        os->>sr: findById()
        sr-->>os: student
        os->>spr: findByUser_Email()
        spr-->>os: supervisor
        os->>ur: findById()
        ur-->>os: supervisorUser
        os->>cr: findById()
        alt existing not null and student not null
            os->>or: findByStudent_Userid()
            or-->>os: existingRecord
            alt existingRecord is null
                os->>uir: existsById()
                alt existsById() is true
                    os->>uir: deleteById()
                end
                os->>ojr: OjtRecord()
                ojr-->>os: newRecord
                ojr-->>or: save(newRecord)
            end
            os-->>es: sendSimpleEmail()
        end
    end
    os->>oc: notification
    oc->>s: ResponseEntity.ok(notification)

```