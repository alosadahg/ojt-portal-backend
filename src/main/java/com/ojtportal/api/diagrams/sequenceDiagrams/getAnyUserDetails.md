``` mermaid
sequenceDiagram
    autonumber
    actor a as Admin
    participant uc as :UserController
    participant us as :UserService
    participant ur as :UserRepo
    a->>uc: GET: user/get<br>getAnyUserDetails()
    uc->>us: getUserInfo()
    us->>us: getUserEntity()
    us->>ur: findById()
    ur->>us: UserEntity
    us-->>uc: UserEntity.toString
    uc-->>a: ResponseEntity.ok(String)
```