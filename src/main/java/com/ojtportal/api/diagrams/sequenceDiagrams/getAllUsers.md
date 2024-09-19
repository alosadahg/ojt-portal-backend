``` mermaid
sequenceDiagram
    autonumber
    actor a as Admin
    participant uc as :UserController
    participant us as :UserService
    participant ur as :UserRepo
    a->>uc: GET: user/get/all<br>getAllUsers()
    uc->>us: getAllUsers()
    us->>ur: findAll()
    ur-->>us: List<UserEntity>
    us-->>uc: List<UserEntity>
    uc-->>a: ResponseEntity.ok(List<UserEntity>)
```