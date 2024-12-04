package it.unisa.medical_docs_to_cda.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getStop() {
        return stop;
    }

    public void setStop(LocalDateTime stop) {
        this.stop = stop;
    }

    public String getpatientId() {
        return patientId;
    }

    public void setpatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getEncounterClass() {
        return encounterClass;
    }

    public void setEncounterClass(String encounterClass) {
        this.encounterClass = encounterClass;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

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
