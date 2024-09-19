``` mermaid
sequenceDiagram
    autonumber
    actor u as User
    participant tc as :TaskController
    participant up as :UserPrincipal
    participant ts as :TaskService
    participant tr as :TaskRepo
    participant t as :Task
    u->>tc: GET: /get-student-allTasks<br>getAllTasksByStudent()
    loop every authority
        alt authority equals ROLE_SUPERVISOR
            tc->>up: getEmail()
            up-->>tc: auth
            tc->>up: getAuthority()
            up-->>tc: user_type
        else
            alt authority equals ROLE_STUDENT
                tc->>up: getEmail()
                up-->>tc: studentEmail
            else 
                tc->>up: getAuthority()
                up-->>tc: user_type
            end
        end
    end
    tc->>ts: getAllTasksByStudent(studentEmail, auth, user_type)
    ts->>tr: findByStudentTasks_Student_User_Email()
    tr-->>ts: tasks
    alt user_type is supervisor
        alt tasks not empty
            ts->>t: getSupervisor 
            t-->>ts: Supervisor
            alt Supervisor.email equals auth
                ts-->>tc: tasks
                tc-->>u: ResponseEntity.ok(tasks)
            end
        end
    end
    ts-->>tc: tasks
    tc-->>u: ResponseEntity.ok(tasks)
```