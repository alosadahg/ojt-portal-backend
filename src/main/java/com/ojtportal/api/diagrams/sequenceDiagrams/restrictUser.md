``` mermaid
sequenceDiagram
    autonumber
    actor a as Admin
    participant uc as :UserController
    participant us as :UserService
    participant ur as :UserRepo
    participant ue as :UserEntity
    a->>uc: PUT: /admin/restrict-user<br>restrictUser()
    uc->>us: restrictUser()
    alt user not null and user.userStatus not RESTRICTED
        us->>ur: findByEmail()
        ur-->>us: user
        us->>ue: setUserStatus(RESTRICTED)
        ue-->>ur: save()
    end
    us-->>uc: int
    uc-->>a: ResponseEntity.ok<Integer>
```