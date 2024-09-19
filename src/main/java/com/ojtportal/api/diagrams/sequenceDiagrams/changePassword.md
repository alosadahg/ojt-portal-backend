``` mermaid
sequenceDiagram
    autonumber
    actor u as User
    participant uc as :UserController
    participant us as :UserService
    participant ur as :UserRepo
    participant ue as :UserEntity
    u->>uc: PUT: auth/change-password<br>changePassword()
    uc->>us: changePassword()
    us->>us: getUserEntity()
    us->>ur: findById()
    ur-->>us: UserEntity
    alt user not null
        alt oldPassword matches newPassword
            us->>ue: setPassword()
            ue-->>ur: save()
            us-->>uc: Success
            uc-->>u: ResponseEntity.ok(Success)
        end
        us-->>uc: ERROR: Passwords do not match
        uc-->>u: ERROR: Passwords do not match
    end
    us-->>uc: ERROR: Record do not exist
    uc-->>u: ERROR: Record do not exist

```