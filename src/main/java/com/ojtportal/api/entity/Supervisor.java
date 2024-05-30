package com.ojtportal.api.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "supervisor")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Supervisor {
    @Id
    @Column(name = "user_id")
    private long userid;
    private String position;
    private String work_email;
    private String work_contactNo;
    private String work_address;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "user_id", referencedColumnName = "uid", foreignKey = @ForeignKey(name = "fk_supervisor_user"))
    private UserEntity user;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @JsonBackReference
    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    private List<OjtRecord> ojtRecords;

    @JsonBackReference
    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    private List<TrainingPlan> trainingPlans;

    public Supervisor(String position, String work_email, String work_contactNo, String work_address, UserEntity user,
            Company company) {
        this.position = position;
        this.work_email = work_email;
        this.work_contactNo = work_contactNo;
        this.work_address = work_address;
        this.user = user;
        this.company = company;
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
