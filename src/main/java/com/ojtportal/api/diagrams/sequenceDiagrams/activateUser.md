``` mermaid
sequenceDiagram
    autonumber
    actor a as Admin
    participant uc as :UserController
    participant us as :UserService
    participant ur as :UserRepo
    participant ue as :UserEntity
    a->>uc: PUT: /admin/activate-user<br>activateUser()
    uc->>us: activateUser()
    alt user not null and user.userStatus not ACTIVE
        us->>ur: findByEmail()
        ur-->>us: user
        us->>ue: setUserStatus()
        ue-->>ur: save()
    end
    us-->>uc: int
    uc-->>a: ResponseEntity.ok<Integer>
```