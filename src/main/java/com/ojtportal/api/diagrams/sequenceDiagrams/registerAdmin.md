``` mermaid
sequenceDiagram
    autonumber
    actor a as Admin
    participant uc as :UserController
    participant us as :UserService
    participant u as :UserEntity
    participant ur as :UserRepo
    participant es as :EmailService
    a->>uc: POST: auth/register-admin<br>registerAdmin()
    uc->>us: registerAdmin()
    us->>u: setAccountType(ROLE_ADMIN)
    us->>us: addUser()
    alt user not null
        us->>ur: findByEmail()
        ur-->>us: UserEntity 
        alt UserEntity is null
            us->>u: setPassword()
            us->>u: setUserStatus()
            u-->>ur: save()
            us->>es: sendSimpleEmail()
        end
    end
    us-->>uc: String
    uc-->>a: ResponseEntity.ok(String)
```