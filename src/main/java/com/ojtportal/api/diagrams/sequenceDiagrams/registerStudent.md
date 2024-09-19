``` mermaid
sequenceDiagram
    autonumber
    actor s as Student
    participant sc as :StudentController
    participant ss as :StudentService
    participant ue as :UserEntity
    participant us as :UserService
    participant es as :EmailService
    participant st as :Student
    participant sr as :StudentRepo
    s->>sc: POST: student/register<br>registerNewStudentUser()
    sc->>ss: registerNewUserStudent()
    ss->>ue: setAccountType(ROLE_STUDENT)
    ss->>us: getUserEntity()
    us-->>ss: savedUser
    alt existsById() is true
        ss->>es: sendSimpleMail()
    end
    alt findByStudentid == null and not existsById
        ss->>st: Student()
        st-->>ss: student
        st-->>sr: save(student)
    end
    ss-->>sc: int
    sc-->>s: ResponseEntity.ok(int)
```