package it.unisa.medical_docs_to_cda.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection = "medication")
public class Medication {
    @Field("START")
    private LocalDateTime start;
    @Field("STOP")
    private LocalDateTime stop;
    @Field("PATIENT")
    private String patientId;
    @Field("ENCOUNTER")
    private String encounterId;
    @Field("CODE")
    private String code;
    @Field("DESCRIPTION")
    private String description;
    @Field("REASONCODE")
    private String reasonCode;
    @Field("REASONDESCRIPTION")
    private String reasonDescription;
   
    public Medication(LocalDateTime start, LocalDateTime stop, String patientId, String encounterId, String code,
            String description, String reasonCode, String reasonDescription) {
        this.start = start;
        this.stop = stop;
        this.patientId = patientId;
        this.encounterId = encounterId;
        this.code = code;
        this.description = description;
        this.reasonCode = reasonCode;
        this.reasonDescription = reasonDescription;
    }

    





}
