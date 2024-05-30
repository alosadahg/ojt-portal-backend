package com.ojtportal.api.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ojtportal.api.model.LogbookStatus;
import com.ojtportal.api.model.RecordVisibility;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "logbook_entry")
public class LogbookEntry {
    @Id
    @JsonProperty("entryId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer entryId;

    @JsonProperty("timeIn")
    @Column(name = "time_in")
    private LocalDateTime timein;

    @JsonProperty("timeOut")
    @Column(name = "time_out")
    private LocalDateTime timeout;

    @JsonProperty("totalHrs")
    private double totalHrs;

    @JsonProperty("activities")
    private String activities;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    private LogbookStatus status = LogbookStatus.PENDING;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private RecordVisibility visibility = RecordVisibility.VISIBLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ojt_record_id", nullable = false)
    private OjtRecord ojtrecord;

    @ManyToMany
    @JoinTable(name = "logbook_task", joinColumns = @JoinColumn(name = "entry_id"),
        inverseJoinColumns = @JoinColumn(name = "task_id"))
    private List<Task> tasks = new ArrayList<Task>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "logbook_skill", joinColumns = @JoinColumn(name = "entry_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills = new ArrayList<Skill>();

    public LogbookEntry(LocalDateTime timeIn, LocalDateTime timeOut, String activities, OjtRecord record) {
        this.timein = timeIn;
        this.timeout = timeOut;
        this.activities = activities;
        this.ojtrecord = record;
        this.totalHrs = getHoursRendered(timeIn, timeOut);
    }

    public LogbookEntry(LocalDateTime timein, LocalDateTime timeout, String activities, OjtRecord ojtrecord,
            List<Task> tasks) {
        this.timein = timein;
        this.timeout = timeout;
        this.activities = activities;
        this.ojtrecord = ojtrecord;
        this.tasks = tasks;
        this.totalHrs = getHoursRendered(timein, timeout);
    }

    private double getHoursRendered(LocalDateTime timeIn, LocalDateTime timeOut) {
        Duration duration = Duration.between(timeIn, timeOut);
        double hours = duration.toHours() + (duration.toMinutesPart() / 60.0);
        BigDecimal bd = new BigDecimal(hours).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public String toString() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            return objectWriter.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "ERROR: Failed to convert object to JSON. Details: " + e.getMessage();
        }
    }
}
