``` mermaid
sequenceDiagram
    autonumber
    actor u as User
    participant uc as :UserController
    participant us as :UserService
    participant ur as :UserRepo
    participant ue as :UserEntity
    u->>uc: PUT: auth/activate<br>authenticateUser()
    uc->>us: authenticate()
    alt verificationCode equals this.verification
        us->>ur: findByEmail()
        ur-->>us: user
        alt type is activation
            us->>ue: setUserStatus()
        else
            alt type is password-change
                us->>ue: setPassword()
            end
        end
        ue-->>ur: save()
    end
    us-->>uc: int
    uc-->>u: ResponseEntity.ok<Integer>
```