``` mermaid
sequenceDiagram
    autonumber
    actor u as User
    participant uc as :UserController
    participant us as :UserService
    participant ur as :UserRepo
    participant es as :EmailService
    u->>uc: PUT: auth/forget-password<br>forgetPassword()
    uc->>us: forgetPassword()
    us->>ur: findByEmail()
    ur-->>us: existing:UserEntity
    alt existing not null
        us->>es: sendSimpleMail()
    end
    us-->>uc: int
    uc-->>u: ResponseEntity.ok<Integer>

```