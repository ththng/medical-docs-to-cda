package it.unisa.medical_docs_to_cda.model;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document(collection = "condition")
public class Condition {
    @Field("START")
    private LocalDate start;
    @Field("STOP")
    private LocalDate stop;
    @Field("PATIENT")
    private String patientId;
    @Field("ENCOUNTER")
    private String encounterId;
    @Field("CODE")
    private String code;
    @Field("DESCRIPTION")
    private String description;
    
    public Condition(LocalDate start, LocalDate stop, String patientId, String encounterId, String code,
            String description) {
        this.start = start;
        this.stop = stop;
        this.patientId = patientId;
        this.encounterId = encounterId;
        this.code = code;
        this.description = description;
    }

}
