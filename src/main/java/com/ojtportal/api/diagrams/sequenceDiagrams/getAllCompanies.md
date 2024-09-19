``` mermaid
sequenceDiagram
    autonumber
    actor u as User
    participant cc as :CompanyController
    participant up as :UserPrincipal
    participant cs as :CompanyService
    participant or as :OjtRecordRepo
    participant cr as :CompanyRepo
    participant o as :OjtRecord
    u->>cc: GET: /get-all-companies<br>getAllCompanies()
    loop every authority
        alt authority equals ROLE_SUPERVISOR
            cc->>up: getEmail()
            up-->>cc: auth
            cc->>up: getAuthority()
            up-->>cc: user_type
        else
            alt authority equals ROLE_STUDENT
                cc->>up: getEmail()
                up-->>cc: studentEmail
            else 
                cc->>up: getAuthority()
                up-->>cc: user_type
            end
        end
    end
    cc->>cs: getAllCompanies()
    alt user_type is student
        cs->>or: findByStudent_User_Email
        or-->>cs: record
    else
        alt user_type is supervisor
            cs->>or: findBySupervisor_User_Email
            or-->>cs: record
        else
            cs->>cr: findAll()
            cr-->>cs: List<Company>
            cs-->>cc: List<Company>
            cc-->>u: Response.Entity(List<Company>)
        end
    end
    cs->>o: getCompany()
    o-->>cs: Company
    cs-->>cc: List.of(Company)
    cc-->>u: Response.Entity(List.of(Company))
```