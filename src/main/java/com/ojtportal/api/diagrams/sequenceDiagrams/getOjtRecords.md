``` mermaid
sequenceDiagram
    autonumber
    actor u as User
    participant oc as :OJTRecordController
    participant up as :UserPrincipal
    participant os as :OjtRecordService
    participant or as :OjtRecordRepo
    u->>oc: GET: /get-ojt-records<br>getAllOjtRecords(principal)
    loop every authority
        alt authority equals ROLE_SUPERVISOR
            oc->>up: getEmail()
            up-->>oc: email
            oc->>up: getAuthority
            up-->>oc: user_type
        else 
            alt authority equals ROLE_STUDENT
                oc->>up: getEmail()
                up-->>oc: studentEmail
            end
        else 
            oc->>up: getAuthority
            up-->>oc: user_type
        end
    end
    oc->>os: getAllOjtRecords(studentEmail, user_type, auth)
    alt user_type equals supervisor
        alt studentEmail equals all
            os->>or: findBySupervisor_User_Email()
            or-->>os: List<OjtRecord>
        else
            os->>or: findBySupervisor_User_EmailAndStudent_User_Email()
            or-->>os: List.of(OjtRecord)
        end
    end
    alt studentEmail equals all and user_type not student
        os->>or: findAll()
        or-->>os: List<OjtRecord>
    end
    os-->>oc: List<OjtRecord>
    oc-->>u: ResponseEntity.ok(List<OjtRecord>)
```