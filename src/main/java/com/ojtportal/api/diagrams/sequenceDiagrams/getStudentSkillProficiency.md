``` mermaid
sequenceDiagram
    autonumber
    actor u as User
    participant spc as :StudentSkillProficiencyController
    participant up as :UserPrincipal
    participant sps as :StudentSkillProficiencyService
    participant or as :OjtRecordRepo
    participant o as :OjtRecord
    participant spr as :StudentSkillProficiencyRepo
    participant sr as :StudentRepo
    u->>spc: GET: /get-student-proficiency<br>getStudentProficiency(studentEmail, principal)
    loop every authority
        alt authority equals ROLE_SUPERVISOR
            spc->>up: getEmail()
            up-->>spc: auth
            spc->>up: getAuthority()
            up-->>spc: user_type
        else
            alt authority equals ROLE_STUDENT
                spc->>up: getEmail()
                up-->>spc: studentEmail
            else 
                spc->>up: getAuthority()
                up-->>spc: user_type
            end
        end
    end
    spc->>sps: getStudentProficiency(studentEmail, auth, user_type)
    alt studentEmail is all
        alt user_type is admin or chair or instructor
            sps->>or: findAll()
            or-->>sps: records
            loop every record
                sps->>o: getStudent()
                o-->>sps: student
                alt student not null
                    sps->>sps: add(student)
                end
            end
            loop every student
                sps->>spr: findByStudent()
                spr-->>sps: studentProficiencies
                sps->>sps: put(student, studentProficiencies)
            end
        end    
    end
    alt user_type is supervisor
        alt studentEmail is all
            sps->>or: findBySupervisor_User_Email() 
            or-->>sps: records
            loop every record
                sps->>o: getStudent()
                o-->>sps: student
                alt student not null
                    sps->>sps: add(student)
                end
            end
            loop every student
                sps->>spr: findByStudent()
                spr-->>sps: studentProficiencies
                sps->>sps: put(student, studentProficiencies)
            end
        else
            sps->>or: findBySupervisor_User_EmailAndStudent_User_Email()
            or-->>sps: records
            alt records not null
                sps->>spr: findByStudent()
                spr-->>sps: studentProficiencies
                sps->>sps: put(student, studentProficiencies)
            else
                sps-->>spc: ERROR: Record unaccessible
                spc-->>u: ERROR: Record unaccessible
            end
        end
    end
    sps->>sr: findByUser_Email()
    sr-->>sps: student
    alt student not null
        sps->>spr: findByStudent()
        spr-->>sps: studentProficiencies
        sps->>sps: put(student, studentProficiencies)
    end
    sps-->>spc: proficiencies.toString()
    spc-->>u: ResponseEntity.ok(proficiencies.toString())
```