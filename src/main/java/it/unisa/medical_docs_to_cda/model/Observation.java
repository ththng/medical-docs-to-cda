package it.unisa.medical_docs_to_cda.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "observation")
public class Observation {
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
    @Field("UNITS")
    private String units;
    @Field("NUMERIC")
    private String numeric;
    @Field("VALUE")
    private String value;
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
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
    public String getUnits() {
        return units;
    }
    public void setUnits(String units) {
        this.units = units;
    }
    public String getNumeric() {
        return numeric;
    }
    public void setNumeric(String numeric) {
        this.numeric = numeric;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public Observation(LocalDateTime date, String patientId, String encounterId, String code, String description,
            String units, String numeric, String value) {
        this.date = date;
        this.patientId = patientId;
        this.encounterId = encounterId;
        this.code = code;
        this.description = description;
        this.units = units;
        this.numeric = numeric;
        this.value = value;
    }

    
}
