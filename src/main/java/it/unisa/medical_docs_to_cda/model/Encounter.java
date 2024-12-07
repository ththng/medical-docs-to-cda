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
@Document(collection = "encounter")
public class Encounter {
    @Field("Id")
    private String id;
    @Field("START")
    private LocalDateTime start;
    @Field("STOP")
    private LocalDateTime stop;
    @Field("PATIENT")
    private String patientId;
    @Field("ORGANIZATION")
    private String organizationId;
    @Field("PROVIDER")
    private String providerId;
    @Field("ENCOUNTERCLASS")
    private String encounterClass;
    @Field("CODE")
    private String code;
    @Field("DESCRIPTION")
    private String description;
    @Field("REASONDESCRIPTION")
    private String reasonDescription;
    @Field("REASONCODE")
    private String reasonCode;

    public Encounter(String id, LocalDateTime start, LocalDateTime stop, String patientId, String organizationId,
            String providerId, String encounterClass, String code, String description, String reasonDescription,
            String reasonCode) {
        this.id = id;
        this.start = start;
        this.stop = stop;
        this.patientId = patientId;
        this.organizationId = organizationId;
        this.providerId = providerId;
        this.encounterClass = encounterClass;
        this.code = code;
        this.description = description;
        this.reasonDescription = reasonDescription;
        this.reasonCode = reasonCode;
    }

}
