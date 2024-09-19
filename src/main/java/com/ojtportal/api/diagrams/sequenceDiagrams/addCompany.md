``` mermaid
sequenceDiagram
    autonumber
    actor u as User
    participant cc as :CompanyController
    participant cs as :CompanyService
    participant cr as :CompanyRepo
    participant c as :Company
    u->>cc: POST: company/add<br>addCompanyFullInfo()
    cc->>cs: addCompany()
    cs->>cr: findByCompanyName()
    cr-->>cs: existing:Company
    cs->>c: getEmails()
    c-->>cs: emails
    cs->>c: getContactNos()
    c-->>cs: contactNos
    cs->>c: getLocations()
    c-->>cs: addresses
    alt email not contains(email)
        cs->>cs: emails.add(email)
    end
    alt contactNos not contains(contactNo)
        cs->>cs: contactNos.add(contactNo)
    end
    alt addresses not contains(address)
        cs->>cs: addresses.add(address)
    end
    cs->>c: setEmails()
    cs->>c: setContactNos()
    cs->>c: setLocations()
    c-->>cr: save()
    cs-->>cc: int
    cc-->>u: ResponseEntity.ok(int)
```