package it.unisa.medical_docs_to_cda.model;

import java.time.LocalDateTime;

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

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
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
