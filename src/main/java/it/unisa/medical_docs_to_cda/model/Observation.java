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

}
