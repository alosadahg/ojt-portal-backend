``` mermaid
sequenceDiagram
    autonumber
    actor u as User
    participant ec as :EvaluationController
    participant up as :UserPrincipal
    participant es as :EvaluationService
    participant er as :EvaluationRepo
    u->>ec: GET: /get-evaluation-record<br>getEvaluationRecord()
    loop every authority
        alt authority equals ROLE_SUPERVISOR
            ec->>up: getEmail()
            up-->>ec: auth
            ec->>up: getAuthority()
            up-->>ec: user_type
        else
            alt authority equals ROLE_STUDENT
                ec->>up: getEmail()
                up-->>ec: studentEmail
            else 
                ec->>up: getAuthority()
                up-->>ec: user_type
            end
        end
    end
    ec->>es: getEvaluationRecord()
    alt user_type is supervisor
        alt studentEmail is all
            es->>er: findByOjtrecord_Supervisor_User_Email()
        else    
            es->>er: findByOjtrecord_Student_User_EmailAndOjtrecord_Supervisor_User_Email()
        end
    end
    alt studentEmail is all and user_type not student
        es->>er: findAll()
    end
    es->>er: findByOjtrecord_Student_User_Email()
    er-->>es: List<Evaluation>
    es-->>ec: List<Evaluation>
    ec-->>u: ResponseEntity.ok(List<Evaluation>)
```