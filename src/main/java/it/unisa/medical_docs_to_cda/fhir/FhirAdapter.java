package it.unisa.medical_docs_to_cda.fhir;

import org.hl7.fhir.r5.model.Patient;
import org.hl7.fhir.r5.model.Period;
import org.hl7.fhir.r5.model.Procedure;
import org.hl7.fhir.r5.model.Quantity;
import org.hl7.fhir.r5.model.Reference;
import org.hl7.fhir.r5.model.Encounter.ReasonComponent;
import org.hl7.fhir.r5.model.Address;
import org.hl7.fhir.r5.model.AllergyIntolerance;
import org.hl7.fhir.r5.model.CarePlan;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.CodeableReference;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Condition;
import org.hl7.fhir.r5.model.Encounter;
import org.hl7.fhir.r5.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r5.model.Enumerations.ObservationStatus;
import org.hl7.fhir.r5.model.Immunization.ImmunizationStatusCodes;
import org.hl7.fhir.r5.model.Observation;
import org.hl7.fhir.utilities.StandardsStatus;
import org.hl7.fhir.r5.model.MedicationStatement;
import org.hl7.fhir.r5.model.ImagingStudy;
import org.hl7.fhir.r5.model.Immunization;
import java.time.ZoneId;
import java.util.Date;

public class FhirAdapter {
    public static Patient createFhirPatient(it.unisa.medical_docs_to_cda.model.Patient patientModel) {
        Patient fhirPatient = new Patient();

        // Imposta l'ID del paziente come metadato (non come identificatore FHIR)
        fhirPatient.setId(patientModel.getId());

        // Identificatori: SSN, Drivers, Passport
        if (patientModel.getSSN() != null) {
            fhirPatient.addIdentifier()
                    .setSystem("http://hl7.org/fhir/sid/us-ssn") // Sistema standard per SSN
                    .setValue(patientModel.getSSN());
        }

        if (patientModel.getDrivers() != null) {
            fhirPatient.addIdentifier()
                    .setSystem("http://example.org/fhir/sid/drivers-license") // Sistema per patente
                    .setValue(patientModel.getDrivers());
        }

        if (patientModel.getPassport() != null) {
            fhirPatient.addIdentifier()
                    .setSystem("http://example.org/fhir/sid/passport") // Sistema per passaporto
                    .setValue(patientModel.getPassport());
        }

        // Nome
        fhirPatient.addName()
                .setFamily(patientModel.getLast())
                .addGiven(patientModel.getFirst())
                .addGiven(patientModel.getMaiden() != null ? patientModel.getMaiden() : "");

        // Data di nascita
        if (patientModel.getBirthDate() != null) {
            fhirPatient.setBirthDate(java.util.Date.from(patientModel.getBirthDate()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        // Data di morte (se presente)
        if (patientModel.getDeathDate() != null) {
            fhirPatient.setDeceased(new org.hl7.fhir.r5.model.DateTimeType(
                    java.util.Date.from(patientModel.getDeathDate().atStartOfDay(ZoneId.systemDefault()).toInstant())));
        }

        // Genere
        if (patientModel.getGender() != null) {
            fhirPatient.setGender(
                    "male".equalsIgnoreCase(patientModel.getGender()) ? AdministrativeGender.MALE
                            : "female".equalsIgnoreCase(patientModel.getGender()) ? AdministrativeGender.FEMALE
                                    : AdministrativeGender.UNKNOWN);
        }

        // Indirizzo
        Address address = new Address();
        address.setCity(patientModel.getCity())
                .setState(patientModel.getState())
                .setCountry(patientModel.getCounty())
                .setPostalCode(patientModel.getZip())
                .addLine(patientModel.getAddress());
        fhirPatient.addAddress(address);

        // Luogo di nascita (come estensione)
        if (patientModel.getBirthPlace() != null) {
            fhirPatient.addExtension()
                    .setUrl("http://hl7.org/fhir/StructureDefinition/patient-birthPlace")
                    .setValue(new org.hl7.fhir.r5.model.StringType(patientModel.getBirthPlace()));
        }

        return fhirPatient;
    }

    public static AllergyIntolerance createFhirAllergy(it.unisa.medical_docs_to_cda.model.Allergy allergyModel) {
        AllergyIntolerance fhirAllergy = new AllergyIntolerance();

        // Imposta il periodo di inizio (start) e fine (stop)
        if (allergyModel.getStart() != null) {
            fhirAllergy.setOnset(new org.hl7.fhir.r5.model.DateTimeType(
                    java.util.Date.from(allergyModel.getStart().atStartOfDay(ZoneId.systemDefault()).toInstant())));
        }
        if (allergyModel.getStop() != null) {
            fhirAllergy.setRecordedDate(java.util.Date.from(
                    allergyModel.getStop().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        // Imposta il paziente come riferimento
        if (allergyModel.getPatientId() != null) {
            fhirAllergy.setPatient(new Reference("Patient/" + allergyModel.getPatientId()));
        }

        // Imposta l'incontro (encounter) come riferimento
        if (allergyModel.getEncounterId() != null) {
            fhirAllergy.setEncounter(new Reference("Encounter/" + allergyModel.getEncounterId()));
        }

        // Imposta il codice dell'allergia
        if (allergyModel.getCode() != null) {
            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.addCoding()
                    .setSystem("http://snomed.info/sct") // Sistema di codici, esempio SNOMED CT
                    .setCode(allergyModel.getCode())
                    .setDisplay(allergyModel.getDescription() != null ? allergyModel.getDescription() : "Unknown");
            fhirAllergy.setCode(codeableConcept);
        }

        // Imposta la descrizione (se presente)
        if (allergyModel.getDescription() != null && fhirAllergy.getCode() == null) {
            fhirAllergy.setCode(new CodeableConcept().setText(allergyModel.getDescription()));
        }

        // Stato dell'allergia
        CodeableConcept verificationStatus = new CodeableConcept();
        verificationStatus.addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/allergyintolerance-verification")
                .setCode("confirmed");
        fhirAllergy.setVerificationStatus(verificationStatus);

        return fhirAllergy;
    }

    public static CarePlan createFhirCarePlan(it.unisa.medical_docs_to_cda.model.Careplan carePlanModel) {
        CarePlan fhirCarePlan = new CarePlan();

        // Imposta il periodo (start e stop)
        if (carePlanModel.getStart() != null || carePlanModel.getStop() != null) {
            Period period = new Period();
            if (carePlanModel.getStart() != null) {
                period.setStart(
                        java.util.Date.from(carePlanModel.getStart().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            if (carePlanModel.getStop() != null) {
                period.setEnd(
                        java.util.Date.from(carePlanModel.getStop().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            fhirCarePlan.setPeriod(period);
        }

        // Imposta il paziente come riferimento
        if (carePlanModel.getPatientId() != null) {
            fhirCarePlan.setSubject(new Reference("Patient/" + carePlanModel.getPatientId()));
        }

        // Imposta l'incontro (encounter) come riferimento
        if (carePlanModel.getEncounterId() != null) {
            fhirCarePlan.addSupportingInfo(new Reference("Encounter/" + carePlanModel.getEncounterId()));
        }

        // Imposta l'attività del care plan
        if (carePlanModel.getCode() != null || carePlanModel.getDescription() != null) {
            CarePlan.CarePlanActivityComponent activity = new CarePlan.CarePlanActivityComponent();

            // Crea il codice dell'attività
            CodeableReference activityCodeReference = new CodeableReference();
            CodeableConcept activityCode = new CodeableConcept();
            activityCode.addCoding()
                    .setSystem("http://snomed.info/sct") // Sistema di codici personalizzato
                    .setCode(carePlanModel.getCode())
                    .setDisplay(carePlanModel.getDescription() != null ? carePlanModel.getDescription() : "Unknown");
            activityCodeReference.setConcept(activityCode);
            // Imposta il codice dell'attività
            activity.addPerformedActivity(activityCodeReference);
            // Aggiungi l'attività al care plan
            fhirCarePlan.addActivity(activity);
        }
if (carePlanModel.getReasonCode() != null || carePlanModel.getReasonDescription() != null) {
    CodeableConcept reasonCode = new CodeableConcept();
    reasonCode.addCoding()
            .setSystem("http://snomed.info/sct") // Sistema di codici personalizzato
            .setCode(carePlanModel.getReasonCode())
            .setDisplay(carePlanModel.getReasonDescription() != null ? carePlanModel.getReasonDescription() : "Unknown");
    fhirCarePlan.addCategory(reasonCode);
}
        
        
  

        return fhirCarePlan;
    }

    public static Condition createFhirCondition(it.unisa.medical_docs_to_cda.model.Condition conditionModel) {
        // Creazione di un oggetto FHIR Condition
        Condition fhirCondition = new Condition();
    
        // Impostazione del periodo (start e stop)
        if (conditionModel.getStart() != null || conditionModel.getStop() != null) {
            Period period = new Period();
            if (conditionModel.getStart() != null) {
                period.setStart(java.util.Date.from(conditionModel.getStart().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            if (conditionModel.getStop() != null) {
                period.setEnd(java.util.Date.from(conditionModel.getStop().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            fhirCondition.setOnset(period);
        }
    
        // Impostazione del paziente come riferimento
        if (conditionModel.getPatientId() != null) {
            fhirCondition.setSubject(new Reference("Patient/" + conditionModel.getPatientId()));
        }
    
        // Impostazione dell'incontro (encounter) come riferimento
        if (conditionModel.getEncounterId() != null) {
            fhirCondition.setEncounter(new Reference("Encounter/" + conditionModel.getEncounterId()));
        }
    
        // Impostazione del codice della condizione con sistemi standard
        if (conditionModel.getCode() != null || conditionModel.getDescription() != null) {
            CodeableConcept conditionCode = new CodeableConcept();
    
            // Aggiunta di codici standardizzati
            conditionCode.addCoding()
                .setSystem("http://snomed.info/sct") // Sistema SNOMED CT
                .setCode(conditionModel.getCode()) // Il codice dovrebbe essere un codice SNOMED valido
                .setDisplay(conditionModel.getDescription() != null ? conditionModel.getDescription() : "Unknown");
            fhirCondition.setCode(conditionCode);
        }
    
        return fhirCondition;
    }




    public static Encounter createFhirEncounter(it.unisa.medical_docs_to_cda.model.Encounter encounterModel) {
    // Creazione di un oggetto FHIR Encounter
    Encounter fhirEncounter = new Encounter();

    // Impostazione dell'ID
    if (encounterModel.getId() != null) {
        fhirEncounter.setId(encounterModel.getId());
    }

    // Impostazione del periodo (start e stop)
    if (encounterModel.getStart() != null || encounterModel.getStop() != null) {
        Period period = new Period();
        if (encounterModel.getStart() != null) {
            period.setStart(Date.from(encounterModel.getStart().atZone(ZoneId.systemDefault()).toInstant()));
        }
        if (encounterModel.getStop() != null) {
            period.setEnd(Date.from(encounterModel.getStop().atZone(ZoneId.systemDefault()).toInstant()));
        }
        fhirEncounter.setActualPeriod(period);
    }

    // Impostazione del paziente
    if (encounterModel.getPatientId() != null) {
        fhirEncounter.setSubject(new Reference("Patient/" + encounterModel.getPatientId()));
    }

    // Impostazione dell'organizzazione
    if (encounterModel.getOrganizationId() != null) {
        fhirEncounter.setServiceProvider(new Reference("Organization/" + encounterModel.getOrganizationId()));
    }

    // Impostazione del provider
    if (encounterModel.getProviderId() != null) {
        fhirEncounter.addParticipant()
            .setActor(new Reference("Practitioner/" + encounterModel.getProviderId()));
    }

    // Impostazione della classe dell'incontro
    if (encounterModel.getEncounterClass() != null) {
        fhirEncounter.addClass_(new CodeableConcept().addCoding(new Coding()
            .setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode") // Sistema HL7 ActCode per le classi di incontro
            .setCode(encounterModel.getEncounterClass())
            .setDisplay("Encounter Class Description"))); // Puoi sostituire con una descrizione appropriata
    }

    // Impostazione del codice dell'incontro
    if (encounterModel.getCode() != null || encounterModel.getDescription() != null) {
        CodeableConcept code = new CodeableConcept();
        code.addCoding(new Coding()
            .setSystem("http://snomed.info/sct") // Sistema SNOMED CT
            .setCode(encounterModel.getCode())
            .setDisplay(encounterModel.getDescription() != null ? encounterModel.getDescription() : "Unknown"));
        fhirEncounter.setType(java.util.Collections.singletonList(code));
    }

    // Impostazione del motivo dell'incontro
    if (encounterModel.getReasonCode() != null || encounterModel.getReasonDescription() != null) {
        ReasonComponent reason = new ReasonComponent();
        reason.addUse(new CodeableConcept().addCoding(new Coding()
            .setSystem("http://snomed.info/sct") // Sistema SNOMED CT per i motivi
            .setCode(encounterModel.getReasonCode())
            .setDisplay(encounterModel.getReasonDescription() != null ? encounterModel.getReasonDescription() : "Unknown")));
        fhirEncounter.addReason(reason);
    }

    return fhirEncounter;
}



public static ImagingStudy createFhirImagingStudy(it.unisa.medical_docs_to_cda.model.ImagingStudy imagingStudyModel) {
    ImagingStudy fhirImagingStudy = new ImagingStudy();

    // Impostazione dell'ID
    if (imagingStudyModel.getId() != null) {
        fhirImagingStudy.setId(imagingStudyModel.getId());
    }

    // Impostazione della data dello studio
    if (imagingStudyModel.getDate() != null) {
     fhirImagingStudy.setStarted(java.util.Date.from(imagingStudyModel.getDate().atZone(ZoneId.systemDefault()).toInstant()));
    }
    // Impostazione del paziente
    if (imagingStudyModel.getPatientId() != null) {
        fhirImagingStudy.setSubject(new Reference("Patient/" + imagingStudyModel.getPatientId()));
    }

    // Impostazione dell'incontro
    if (imagingStudyModel.getEncounterId() != null) {
        fhirImagingStudy.setEncounter(new Reference("Encounter/" + imagingStudyModel.getEncounterId()));
    }


    // Impostazione della modalità (modality)
    if (imagingStudyModel.getModalityCode() != null || imagingStudyModel.getModalityDescription() != null) {
        CodeableConcept modality = new CodeableConcept();
        modality.addCoding(new Coding()
            .setSystem("http://terminology.hl7.org/CodeSystem/imaging-modality")
            .setCode(imagingStudyModel.getModalityCode())
            .setDisplay(imagingStudyModel.getModalityDescription() != null ? imagingStudyModel.getModalityDescription() : "Unknown"));
        fhirImagingStudy.addModality(modality);
    }

    // Impostazione del codice SOP (SOP code)
    if (imagingStudyModel.getSopCode() != null || imagingStudyModel.getSopDescription() != null) {
        CodeableConcept sop = new CodeableConcept();
        sop.addCoding(new Coding()
            .setSystem("http://dicom.nema.org/resources/ontology/DCM")
            .setCode(imagingStudyModel.getSopCode())
            .setDisplay(imagingStudyModel.getSopDescription() != null ? imagingStudyModel.getSopDescription() : "Unknown"));
        fhirImagingStudy.addModality(sop);
    }

    return fhirImagingStudy;
}


public static Immunization createFhirImmunization(it.unisa.medical_docs_to_cda.model.Immunization immunizationModel) {
    Immunization fhirImmunization = new Immunization();

    // Impostazione della data
    if (immunizationModel.getDate() != null) {
        fhirImmunization.setOccurrence(new org.hl7.fhir.r5.model.DateTimeType(
            Date.from(immunizationModel.getDate().atZone(ZoneId.systemDefault()).toInstant())));
    }

    // Impostazione del paziente
    if (immunizationModel.getPatientId() != null) {
        fhirImmunization.setPatient(new Reference("Patient/" + immunizationModel.getPatientId()));
    }

    // Impostazione dell'incontro
    if (immunizationModel.getEncounterId() != null) {
        fhirImmunization.setEncounter(new Reference("Encounter/" + immunizationModel.getEncounterId()));
    }

    // Impostazione del codice e della descrizione dell'immunizzazione
    if (immunizationModel.getCode() != null || immunizationModel.getDescription() != null) {
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding(new Coding()
            .setSystem("http://hl7.org/fhir/sid/cvx")
            .setCode(immunizationModel.getCode())
            .setDisplay(immunizationModel.getDescription() != null ? immunizationModel.getDescription() : "Unknown"));
        fhirImmunization.setVaccineCode(codeableConcept);
    }

    // Impostazione dello stato (di default "completed")
    fhirImmunization.setStatus(ImmunizationStatusCodes.COMPLETED);

    return fhirImmunization;
}


public static MedicationStatement createFhirMedicationStatement(it.unisa.medical_docs_to_cda.model.Medication medicationModel) {
    MedicationStatement fhirMedicationStatement = new MedicationStatement();

 

    // Impostazione del paziente
    if (medicationModel.getPatientId() != null) {
        fhirMedicationStatement.setSubject(new Reference("Patient/" + medicationModel.getPatientId()));
    }

    // Impostazione dell'incontro
    if (medicationModel.getEncounterId() != null) {
        fhirMedicationStatement.setEncounter(new Reference("Encounter/" + medicationModel.getEncounterId()));
    }

    // Impostazione della data di inizio e fine
    if (medicationModel.getStart() != null || medicationModel.getStop() != null) {
        Period period = new Period();
        if (medicationModel.getStart() != null) {
            period.setStart(Date.from(medicationModel.getStart().atZone(ZoneId.systemDefault()).toInstant()));
        }
        if (medicationModel.getStop() != null) {
            period.setEnd(Date.from(medicationModel.getStop().atZone(ZoneId.systemDefault()).toInstant()));
        }
        fhirMedicationStatement.setEffective(period);
    }

    // Impostazione del codice del farmaco
    if (medicationModel.getCode() != null || medicationModel.getDescription() != null) {
        CodeableReference medication =new CodeableReference();
        CodeableConcept medicationCodeableConcept = new CodeableConcept();
        medicationCodeableConcept.addCoding(new Coding()
            .setSystem("http://www.nlm.nih.gov/research/umls/rxnorm")
            .setCode(medicationModel.getCode())
            .setDisplay(medicationModel.getDescription() != null ? medicationModel.getDescription() : "Unknown"));
            medication.setConcept(medicationCodeableConcept);
    fhirMedicationStatement.setMedication( medication)  ;
    }
    // Impostazione del motivo (reason)
    if (medicationModel.getReasonCode() != null || medicationModel.getReasonDescription() != null) {
        CodeableReference reason = new CodeableReference();
        CodeableConcept reasonCodeableConcept = new CodeableConcept();
        reasonCodeableConcept.addCoding(new Coding()
            .setSystem("http://snomed.info/sct")
            .setCode(medicationModel.getReasonCode())
            .setDisplay(medicationModel.getReasonDescription() != null ? medicationModel.getReasonDescription() : "Unknown"));
        reason.setConcept(reasonCodeableConcept);
            fhirMedicationStatement.addReason();
    }

    return fhirMedicationStatement;
}


public static Observation createFhirObservation(it.unisa.medical_docs_to_cda.model.Observation observationModel) {
    Observation fhirObservation = new Observation();

    // Impostazione dello stato (di default "final")
    fhirObservation.setStatus(ObservationStatus.FINAL);

    // Impostazione del paziente
    if (observationModel.getPatientId() != null) {
        fhirObservation.setSubject(new Reference("Patient/" + observationModel.getPatientId()));
    }

    // Impostazione dell'incontro
    if (observationModel.getEncounterId() != null) {
        fhirObservation.setEncounter(new Reference("Encounter/" + observationModel.getEncounterId()));
    }

    // Impostazione della data
    if (observationModel.getDate() != null) {
        fhirObservation.setEffective(new org.hl7.fhir.r5.model.DateTimeType(
            Date.from(observationModel.getDate().atZone(ZoneId.systemDefault()).toInstant())));
    }

    // Impostazione del codice
    if (observationModel.getCode() != null || observationModel.getDescription() != null) {
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding(new Coding()
            .setSystem("http://loinc.org") // Sistema standard per le osservazioni
            .setCode(observationModel.getCode())
            .setDisplay(observationModel.getDescription() != null ? observationModel.getDescription() : "Unknown"));
        fhirObservation.setCode(codeableConcept);
    }

    // Impostazione del valore
    if (observationModel.getValue() != null) {
        try {
            // Se il valore è numerico
            double value = Double.parseDouble(observationModel.getValue());
            Quantity quantity = new Quantity();
            quantity.setValue(value);
            if (observationModel.getUnits() != null) {
                quantity.setUnit(observationModel.getUnits());
            }
            fhirObservation.setValue(quantity);
        } catch (NumberFormatException e) {
            // Se il valore è testuale
            fhirObservation.setValue(new org.hl7.fhir.r5.model.StringType(observationModel.getValue()));
        }
    }

    return fhirObservation;
}


public static Procedure createFhirProcedure(it.unisa.medical_docs_to_cda.model.Procedure procedureModel) {
    Procedure fhirProcedure = new Procedure();

    // Impostazione del paziente
    if (procedureModel.getPatientId() != null) {
        fhirProcedure.setSubject(new Reference("Patient/" + procedureModel.getPatientId()));
    }

    // Impostazione dell'incontro
    if (procedureModel.getEncounterId() != null) {
        fhirProcedure.setEncounter(new Reference("Encounter/" + procedureModel.getEncounterId()));
    }


    // Impostazione del codice della procedura
    if (procedureModel.getCode() != null || procedureModel.getDescription() != null) {
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding(new Coding()
            .setSystem("http://snomed.info/sct") // Sistema di codifica standard SNOMED
            .setCode(procedureModel.getCode())
            .setDisplay(procedureModel.getDescription() != null ? procedureModel.getDescription() : "Unknown"));
        fhirProcedure.setCode(codeableConcept);
    }

    // Impostazione del motivo della procedura
if (procedureModel.getReasonCode() != null || procedureModel.getReasonDescription() != null) {
    CodeableReference reason = new CodeableReference();
    CodeableConcept reasonConcept = new CodeableConcept();
    Coding reasonCoding = new Coding()
        .setSystem("http://snomed.info/sct") // Sistema di codifica per il motivo
        .setCode(procedureModel.getReasonCode())
        .setDisplay(procedureModel.getReasonDescription() != null ? procedureModel.getReasonDescription() : "Unknown");
    reasonConcept.addCoding(reasonCoding);
    fhirProcedure.addReason(reason.setConcept(reasonConcept));
}

    return fhirProcedure;
}
}