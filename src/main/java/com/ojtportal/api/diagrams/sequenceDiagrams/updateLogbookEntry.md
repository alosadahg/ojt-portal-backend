``` mermaid
sequenceDiagram
    autonumber
    actor s as Student
    participant lc as :LogbookController
    participant ls as :LogbookService
    participant or as :OjtRecordRepo
    participant o as :OjtRecord
    participant le as :LogbookEntry
    participant sr as :StudentRepo
    participant lr as :LogbookEntryRepo
    participant ld as :LogbookEntryDTO
    participant skr as :SkillRepo
    participant sks as :SkillService
    participant sktr as :SkillTrendRepo
    participant skt as :SkillTrend
    participant sspr as :StudentSkillProficiencyRepo
    participant ssp as :StudentSkillProficiency
    participant str as :StrudenTaskRepo
    participant st as :StudentTask
    participant stpr as :StudentTrainingPlanRepo
    participant stp as :StudentTrainingPlan
    participant tr as :TaskRepo
    participant t as :Task
    s->>lc: PUT: student/update-logbook-entry<br>updateLogbookEntry()
    lc->>ls: updateLogbookEntry()
    ls->>lr: findById()
    lr-->>ls: logbookEntry
    alt logbookEntry not null
        break logbookEntry.student not equals student
            ls-->>lc: ERROR: Unauthorized access
            lc-->>s: ERROR: Unauthorized access
        end
        alt logbookEntry.status equals REJECTED
            ls->>le: setStatus(PENDING)
        end
        alt logbookEntry.status equals APPROVED
            ls-->>lc: ERROR: Update not allowed for approved
            lc-->>s: Update not allowed for approved
        end
        ls->>sr: findByUser_Email()
        sr-->>ls: student
        ls->>le: setActivities()
        ls->>ld: getSkills()
        ld-->>ls: skills
        loop every skillDTO
            ls->>skr: findBySkillNameAndDomain()
            skr-->>ls: skill
            alt skill is null
                ls->>sks: addSkill()
                sks-->>ls: skill
                ls->>sktr: findBySkill()
                sktr-->>ls: trend
                ls->>skt: setSkillFrequency()
                skt-->>sktr: save()
            end
            alt logbookSkills not contains skill
                ls->>sspr: findBySkillAndStudent()
                sspr-->>ls: proficiency
                alt proficiency is null
                    ls->>ssp: StudentSkillProficiency()
                    ssp-->>ls: proficiency
                    ls->>ssp: setFrequencyOfUse()
                else
                    ls->>ssp: setFrequencyOfUse()
                end
                ssp-->>sspr: save(proficiency)
                ls->>ls: logbookSkills.add(skill)
            end
        end
        ls->>le: setSkills()
        ls->>le: getTasks()
        le-->>ls: existingTasks
        loop every task
            ls->>str: findByStudentAndTask()
            str-->>ls: studenTask
            ls->>st: setStatus()
            st-->>str: save(studentTask)
            ls->>stpr: findByStudentAndTrainingPlan()
            stpr-->>ls: studentTrainingPlan
        end
        ls->>le: setTasks()
        alt logbookEntryDTO.taskIDs not null
            loop every taskID
                ls->>tr: findById()
                tr-->>ls: taskOpt
                alt taskOpt is present
                    ls->>t: get()
                    t-->>ls: task
                    ls->>str: findByStudentAndTask()
                    str-->>ls: studentTask
                    break studentTask is null
                        ls-->>lc: ERROR: Unassigned task
                        lc-->>s: ERROR: Unassigned Task
                    end
                    ls->>st: setStatus(COMPLETED)
                    st-->>str: save(studentTask)
                    ls->>stpr: findByStudentAndTrainingPlan()
                    stpr-->>ls: studentTrainingPlan
                    ls->>stp: setCompletedTasks()
                    stp-->>stpr: save(studentTrainingPlan)
                end
            end
            ls->>le: setTasks()
            le-->>lr: save()
        end
        ls-->>lc: LogbookEntry to string
        lc-->>s: ResponseEntity.ok(LogbookEntry.toString)
    end
    ls-->>lc: ERROR: No record found
    lc-->>s: ERROR: No record found
```