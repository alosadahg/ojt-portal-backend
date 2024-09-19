``` mermaid
sequenceDiagram
    autonumber
    actor u as User
    participant cc as :CompanyController
    participant sr as :SupervisorRepo
    participant cs as :CompanyService
    participant or as :OjtRecordRepo
    participant cr as :CompanyRepo
    participant o as :OjtRecord
    u->>cc: GET: company/get-all-students<br>getStudentsByCompany()
    loop every authority
        alt authority equals ROLE_SUPERVISOR
            cc->>sr: getCompany()
            sr-->>cc: companyName
        end
    end
    cc->>cs: getStudentsByCompany()
    cs->>or: findByCompany_CompanyName
    or-->>cs: records
    loop every ojtRecord
        cs->>cs: students.add(getStudent())
    end
    cs-->>cc: students:List<Student>
    cc-->>u: Response.Entity(students)
```