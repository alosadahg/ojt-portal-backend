``` mermaid
sequenceDiagram
    autonumber
    actor u as User
    participant lc as :LogbookController
    participant up as UserPrincipal
    participant ls as :LogbookService
    participant lr as :LogbookRepo
    u->>lc: GET: /student/get-all-entries<br>getAllEntries(email, principal)
    lc->>up: getAuthority()
    up-->>lc: GrantedAuthority
    alt getAuthority equals ROLE_STUDENT
        lc->>up: getEmail()
        lc->>lc: email = getEmail()
    end
    lc->>ls: getStudentLogbook(email)
    ls->>lr: findByfindByOjtrecord_Student_User_Email(email)
    lr-->>ls: List<LogbookEntry>
    ls-->>lc: List<LogbookEntry>
    lc-->>u: ResponseEntity.ok(List<LogbookEntry>)
```