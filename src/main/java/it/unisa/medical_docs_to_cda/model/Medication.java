package it.unisa.medical_docs_to_cda.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    public String getPatientId() {
        return patientId;
    }
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    public String getEncounterId() {
        return encounterId;
    }
    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
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
    public String getReasonCode() {
        return reasonCode;
    }
    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }
    public String getReasonDescription() {
        return reasonDescription;
    }
    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

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
