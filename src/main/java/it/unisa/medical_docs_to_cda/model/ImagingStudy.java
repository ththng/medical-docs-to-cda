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
@Document(collection = "imagingstudy")
public class ImagingStudy {
    @Field("ID")
    private String id;
    @Field("DATE")
    private LocalDateTime date;
    @Field("PATIENT")
    private String patientId;
    @Field("ENCOUNTER")
    private String encounterId;
    @Field("BODYSITE_CODE")
    private String bodySiteCode;
    @Field("BODYSITE_DESCRIPTION")
    private String bodySiteDescription;
    @Field("MODALITY_CODE")
    private String modalityCode;
    @Field("MODALITY_DESCRIPTION")
    private String modalityDescription;
    @Field("SOP_CODE")
    private String sopCode;
    @Field("SOP_DESCRIPTION")
    private String sopDescription;
   
    public ImagingStudy(String id, LocalDateTime date, String patientId, String encounterId, String bodySiteCode,
            String bodySiteDescription, String modalityCode, String modalityDescription, String sopCode,
            String sopDescription) {
        this.id = id;
        this.date = date;
        this.patientId = patientId;
        this.encounterId = encounterId;
        this.bodySiteCode = bodySiteCode;
        this.bodySiteDescription = bodySiteDescription;
        this.modalityCode = modalityCode;
        this.modalityDescription = modalityDescription;
        this.sopCode = sopCode;
        this.sopDescription = sopDescription;
    }

    


}
