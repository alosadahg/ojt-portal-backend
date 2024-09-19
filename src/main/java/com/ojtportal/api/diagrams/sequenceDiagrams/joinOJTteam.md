``` mermaid
sequenceDiagram
    autonumber
    actor s as Student
    participant oc as :OJTRecordController
    participant os as :OjtRecordService
    participant or as :OjtRecordRepo
    participant o as :OjtRecord
    s->>oc: PUT: student/join-team<br>joinTeam(code)
    oc->>os: joinTeam(studentEmail, code)
    os->>or: findById()
    or-->>os: record
    alt record.student equals studentEmail
        os->>o: setStatus()
        o-->>or: save()
    end
    os-->>oc: int
    oc-->>s: ResponseEntity<Integer>
```