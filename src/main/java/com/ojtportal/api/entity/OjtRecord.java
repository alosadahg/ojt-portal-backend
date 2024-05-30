package com.ojtportal.api.entity;

import java.io.Serializable;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ojtportal.api.model.OJTStatus;
import com.ojtportal.api.model.RecordVisibility;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Entity
@Table(name = "ojt_record")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class OjtRecord implements Serializable {
    @Id
    @GeneratedValue(generator = "ojt-record-pk")
    @GenericGenerator(name = "ojt-record-pk", strategy = "com.ojtportal.api.config.generator.OJTRecordGenerator")
    private String recordNo;
    private String designation;
    private String department;
    private double ojtHours;
    private double renderedHours = 0.0;
    
    @Enumerated(EnumType.STRING)
    private OJTStatus status;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private RecordVisibility visibility = RecordVisibility.VISIBLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonManagedReference
    private Company company;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "fk_ojtrecord_student"), nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = false)
    @JsonManagedReference
    private Supervisor supervisor;

    @JsonIgnore
    @OneToMany(mappedBy = "ojtrecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogbookEntry> logbookEntries;

    public OjtRecord(String designation, String department, double ojtHours, Company company, Student student, Supervisor supervisor) {
        this.designation = designation;
        this.department = department;
        this.ojtHours = ojtHours;
        this.company = company;
        this.status = OJTStatus.PENDING;
        this.student = student;
        this.supervisor = supervisor;
    }



    @Override
    public String toString() {
        ObjectWriter mapper = (new ObjectMapper()).writerWithDefaultPrettyPrinter();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Transaction failed.";
        }
    }
    
}
