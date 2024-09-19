``` mermaid
sequenceDiagram
    autonumber
    actor u as User
    participant sc as :StudentController
    participant ss as :StudentService
    participant sr as :StudentRepo
    u-->>sc: GET: /get-all-students<br>getAllStudents()
    sc->>ss: getAllStudents()
    ss->>sr: findAll()
    sr-->>ss: List<Student>
    ss-->>sc: List<Student>
    sc-->>u: ResponseEntity.ok(List<Student>)

```