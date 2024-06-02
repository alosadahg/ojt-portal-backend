package com.ojtportal.api.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ojtportal.api.model.RecordVisibility;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer taskId;
    private String title;
    private String description;
    private String objective;

    @Enumerated(EnumType.STRING)
    private RecordVisibility visibility = RecordVisibility.VISIBLE;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_plan", nullable = false)
    private TrainingPlan trainingplan;

    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentTask> studentTasks;

    @ManyToMany
    @JoinTable(name = "task_skill", joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id"))
    List<Skill> skills;    
    
    public Task(Integer taskId) {
        this.taskId = taskId;
    }

    public Task(String title, String desription, String objective, TrainingPlan trainingPlan) {
        this.title = title;
        this.description = desription;
        this.objective = objective;
        this.trainingplan = trainingPlan;
    }



    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        // Hibernate5Module hibernateModule = new Hibernate5Module();
        // mapper.registerModule(hibernateModule);

        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        try {
            return writer.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Transaction failed.";
        }
    }
    
}
