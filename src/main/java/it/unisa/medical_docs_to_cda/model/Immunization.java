package it.unisa.medical_docs_to_cda.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document(collection = "immunization")
public class Immunization {
    @Field("DATE")
    private LocalDateTime date;
    @Field("PATIENT")
    private String patientId;
    @Field("ENCOUNTER")
    private String encounterId;
    @Field("CODE")
    private String code;
    @Field("DESCRIPTION")
    private String description;

    
    public Immunization(LocalDateTime date, String patientId, String encounterId, String code, String description) {
        this.date = date;
        this.patientId = patientId;
        this.encounterId = encounterId;
        this.code = code;
        this.description = description;
    }

}
