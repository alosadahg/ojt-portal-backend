package com.ojtportal.api.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@Table(name = "company")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String companyName;
    @Builder.Default
    @ElementCollection
    private List<String>  contactNos = List.of();
    @Builder.Default
    @ElementCollection
    private List<String>  emails = List.of();
    @Builder.Default
    @ElementCollection
    private List<String>  locations = List.of();    

    @JsonBackReference
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Supervisor> supervisors;

    @JsonBackReference
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<OjtRecord> ojtRecords;

    public Company(String companyName) {
        this.companyName = companyName;
        this.contactNos = new ArrayList<>();
        this.emails = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.supervisors = new ArrayList<>();
        this.ojtRecords = new ArrayList<>();
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
