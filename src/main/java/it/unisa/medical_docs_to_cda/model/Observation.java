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
    @Field("TYPE")
    private String type;
    @Field("VALUE")
    private String value;
  
    public Observation(LocalDateTime date, String patientId, String encounterId, String code, String description,
            String units, String type, String value) {
        this.date = date;
        this.patientId = patientId;
        this.encounterId = encounterId;
        this.code = code;
        this.description = description;
        this.units = units;
        this.type = type;
        this.value = value;
    }

    
}
