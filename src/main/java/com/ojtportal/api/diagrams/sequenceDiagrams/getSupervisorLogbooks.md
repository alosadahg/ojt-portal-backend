``` mermaid
sequenceDiagram 
    autonumber
    actor s as Supervisor
    participant lc as :LogbookController
    participant up as :UserPrincipal
    participant ls as :LogbookService
    participant lr as :LogbookRepo
    s->>ls: GET: /supervisor/get-logbooks<br>getSupervisorLogbooks(supervisorEmail)
    alt getAuthority equals ROLE_STUDENT
        lc->>up: getEmail()
        lc->>lc: supervisorEmail = getEmail()
    end
    lc->>ls: getSupervisorLogbooks(supervisorEmail)
    ls->>lr: findByOjtrecord_Supervisor_User_Email()
    lr-->>ls: List<LogbookEntry>
    ls-->>lc: List<LogbookEntry>
    lc-->>s: ResponseEntity.ok(List<LogbookEntry>)
```