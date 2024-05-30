package com.ojtportal.api.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ojtportal.api.model.OJTStatus;
import com.ojtportal.api.model.RecordVisibility;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "evaluation")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer evalId;
    private String supervisorRemark = "";
    private String instructorRemark = "";
    private String studentFeedback = "";
    private double supervisorgivenGrade = 0.0;
    private double instructorgivenGrade = 0.0;
    private double overallGrade = 0.0;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ojt_record", referencedColumnName = "recordNo")
    private OjtRecord ojtrecord;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private RecordVisibility visibility = RecordVisibility.VISIBLE;

    public Evaluation(OjtRecord ojtrecord) {
        this.ojtrecord = ojtrecord;
    }

    public void calculateTotalGrade() {
        if(supervisorgivenGrade > 0 && instructorgivenGrade > 0) {
            this.overallGrade = (supervisorgivenGrade * 0.6) + (instructorgivenGrade * 0.4);
        }
        if(overallGrade>0) {
            this.ojtrecord.setStatus(OJTStatus.COMPLETED);
        }
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
