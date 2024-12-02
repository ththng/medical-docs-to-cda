package it.unisa.medical_docs_to_cda.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
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
    public String getBodySiteCode() {
        return bodySiteCode;
    }
    public void setBodySiteCode(String bodySiteCode) {
        this.bodySiteCode = bodySiteCode;
    }
    public String getBodySiteDescription() {
        return bodySiteDescription;
    }
    public void setBodySiteDescription(String bodySiteDescription) {
        this.bodySiteDescription = bodySiteDescription;
    }
    public String getModalityCode() {
        return modalityCode;
    }
    public void setModalityCode(String modalityCode) {
        this.modalityCode = modalityCode;
    }
    public String getModalityDescription() {
        return modalityDescription;
    }
    public void setModalityDescription(String modalityDescription) {
        this.modalityDescription = modalityDescription;
    }
    public String getSopCode() {
        return sopCode;
    }
    public void setSopCode(String sopCode) {
        this.sopCode = sopCode;
    }
    public String getSopDescription() {
        return sopDescription;
    }
    public void setSopDescription(String sopDescription) {
        this.sopDescription = sopDescription;
    }
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
