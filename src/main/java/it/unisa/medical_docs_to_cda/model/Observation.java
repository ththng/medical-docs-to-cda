package it.unisa.medical_docs_to_cda.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "observation")
public class Observation {
    private LocalDateTime date;
    private String patient;
    private String encounter;
    private String code;
    private String description;
    private String units;
    private String numeric;
    private float value;
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public String getPatient() {
        return patient;
    }
    public void setPatient(String patient) {
        this.patient = patient;
    }
    public String getEncounter() {
        return encounter;
    }
    public void setEncounter(String encounter) {
        this.encounter = encounter;
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
    public float getValue() {
        return value;
    }
    public void setValue(float value) {
        this.value = value;
    }
    public Observation(LocalDateTime date, String patient, String encounter, String code, String description,
            String units, String numeric, float value) {
        this.date = date;
        this.patient = patient;
        this.encounter = encounter;
        this.code = code;
        this.description = description;
        this.units = units;
        this.numeric = numeric;
        this.value = value;
    }

    
}
