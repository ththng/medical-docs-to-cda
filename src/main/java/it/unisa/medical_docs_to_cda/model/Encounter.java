package it.unisa.medical_docs_to_cda.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "encounter")
public class Encounter {
    @MongoId
    private String id;
    private LocalDateTime start;
    private LocalDateTime stop;
    private String patient;
    private String organization;
    private String provider;
    private String encounterClass;
    private String code;
    private String description;
    private String reasonDescription;
    private String reasonCode;
    
    public Encounter(String id, LocalDateTime start, LocalDateTime stop, String patient, String organization,
            String provider, String encounterClass, String code, String description, String reasonDescription,
            String reasonCode) {
        this.id = id;
        this.start = start;
        this.stop = stop;
        this.patient = patient;
        this.organization = organization;
        this.provider = provider;
        this.encounterClass = encounterClass;
        this.code = code;
        this.description = description;
        this.reasonDescription = reasonDescription;
        this.reasonCode = reasonCode;
    }

}
