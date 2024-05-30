package com.ojtportal.api.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="skill")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer skillId;
    private String skillName;
    private String domain;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private RecordVisibility visibility = RecordVisibility.VISIBLE;

    @JsonIgnore
    @ManyToMany(mappedBy = "skills")
    List<LogbookEntry> logbookEntries = new ArrayList<LogbookEntry>();

    @JsonIgnore
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentSkillProficiency> studentSkillFrequencies = new ArrayList<StudentSkillProficiency>();
    
    @JsonIgnore
    @ManyToMany(mappedBy = "skills")
    private List<Task> tasks = new ArrayList<Task>();

    public Skill(String skillName, String domain) {
        this.skillName = skillName;
        this.domain = domain;
    }

    public Skill(String skillName, String domain, List<Task> tasks) {
        this.skillName = skillName;
        this.domain = domain;
        this.tasks = tasks;
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
