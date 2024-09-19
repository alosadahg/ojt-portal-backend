``` mermaid
sequenceDiagram
    autonumber
    actor s as Supervisor
    participant sc as :SupervisorController
    participant ss as :SupervisorService
    participant cs as :CompanyService
    participant cr as :CompanyRepo
    participant ue as :UserEntity
    participant us as :UserService
    participant ur as :UserRepo
    participant sp as :Supervisor
    participant spr as :SupervisorRepo
    s->>sc: POST: /supervisor/register<br>addSupervisor()
    sc->>ss: registerSupervisor()
    ss->>cs: addCompany()
    ss->>cr: findByCompanyName()
    cr-->>ss: company
    ss->>ue: setAccountType(ROLE_SUPERVISOR)
    ss->>us: addUser()
    alt addUser() == 0
        ss->>ur: findByEmail()
        ur-->>ss: user
    end
    ss->>sp: Supervisor()
    sp-->>ss: supervisor
    ss->>spr: findByID()
    spr-->>ss: Supervisor
    alt Supervisor is present
        ss->>cs: addSupervisor()
        ss->>sc: int
        sc->>s: ResponseEntity.ok(registerSupervisor)
    end
```