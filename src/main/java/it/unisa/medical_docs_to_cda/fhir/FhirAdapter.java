package it.unisa.medical_docs_to_cda.fhir;

import org.hl7.fhir.r5.model.Patient;
import org.hl7.fhir.r5.model.Period;
import org.hl7.fhir.r5.model.Procedure;
import org.hl7.fhir.r5.model.Quantity;
import org.hl7.fhir.r5.model.Reference;
import org.hl7.fhir.r5.model.CarePlan.CarePlanIntent;
import org.hl7.fhir.r5.model.Encounter.EncounterLocationComponent;
import org.hl7.fhir.r5.model.Address;
import org.hl7.fhir.r5.model.AllergyIntolerance;
import org.hl7.fhir.r5.model.CarePlan;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.CodeableReference;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Condition;
import org.hl7.fhir.r5.model.Encounter;
import org.hl7.fhir.r5.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r5.model.Enumerations.EncounterStatus;
import org.hl7.fhir.r5.model.Enumerations.EventStatus;
import org.hl7.fhir.r5.model.Enumerations.ObservationStatus;
import org.hl7.fhir.r5.model.Immunization.ImmunizationProgramEligibilityComponent;
import org.hl7.fhir.r5.model.Immunization.ImmunizationStatusCodes;
import org.hl7.fhir.r5.model.MedicationStatement.MedicationStatementStatusCodes;
import org.hl7.fhir.r5.model.Observation;
import org.hl7.fhir.r5.model.MedicationStatement;
import org.hl7.fhir.r5.model.ImagingStudy;
import org.hl7.fhir.r5.model.Immunization;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * FhirAdapter is a utility class that provides methods to convert various model
 * objects
 * into their corresponding FHIR (Fast Healthcare Interoperability Resources)
 * representations.
 * This class includes methods for creating FHIR Patient, AllergyIntolerance,
 * CarePlan,
 * Condition, Encounter, ImagingStudy, Immunization, MedicationStatement,
 * Observation, and Procedure
 * resources from the respective model classes.
 */
public class FhirAdapter {

    /**
     * Creates a FHIR Patient resource from a given Patient model.
     *
     * @param patientModel the Patient model to convert
     * @return the corresponding FHIR Patient resource
     * @throws IllegalArgumentException if the patient ID, name, gender, or birth
     *                                  date is missing
     */
    public static Patient createFhirPatient(it.unisa.medical_docs_to_cda.model.Patient patientModel) {
        Patient fhirPatient = new Patient();

        if (patientModel.getId() == null) {
            throw new IllegalArgumentException("Patient ID is mandatory.");
        }
        if (patientModel.getFirst() == null && patientModel.getLast() == null) {
            throw new IllegalArgumentException("At least one name (first or last) is mandatory.");
        }
        if (patientModel.getGender() == null) {
            throw new IllegalArgumentException("Gender is mandatory.");
        }
        if (patientModel.getBirthDate() == null) {
            throw new IllegalArgumentException("Birth date is mandatory.");
        }
        fhirPatient.setId(patientModel.getId());

        // Identifiers: SSN, Drivers, Passport
        if (patientModel.getSSN() != null) {
            fhirPatient.addIdentifier()
                    .setSystem("http://hl7.org/fhir/sid/us-ssn")
                    .setValue(patientModel.getSSN());
        }

        if (patientModel.getDrivers() != null) {
            fhirPatient.addIdentifier()
                    .setSystem("http://example.org/fhir/sid/drivers-license")
                    .setValue(patientModel.getDrivers());
        }

        if (patientModel.getPassport() != null) {
            fhirPatient.addIdentifier()
                    .setSystem("http://example.org/fhir/sid/passport")
                    .setValue(patientModel.getPassport());
        }

        fhirPatient.addName()
                .setFamily(patientModel.getLast())
                .addGiven(patientModel.getFirst())
                .addGiven(patientModel.getMaiden() != null ? patientModel.getMaiden() : "");

        if (patientModel.getBirthDate() != null) {
            fhirPatient.setBirthDate(java.util.Date.from(patientModel.getBirthDate()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        if (patientModel.getDeathDate() != null) {
            fhirPatient.setDeceased(new org.hl7.fhir.r5.model.DateTimeType(
                    java.util.Date.from(patientModel.getDeathDate().atStartOfDay(ZoneId.systemDefault()).toInstant())));
        }

        if (patientModel.getGender() != null) {
            fhirPatient.setGender(
                    "male".equalsIgnoreCase(patientModel.getGender()) ? AdministrativeGender.MALE
                            : "female".equalsIgnoreCase(patientModel.getGender()) ? AdministrativeGender.FEMALE
                                    : AdministrativeGender.UNKNOWN);
        }

        Address address = new Address();
        address.setCity(patientModel.getCity())
                .setState(patientModel.getState())
                .setCountry(patientModel.getCounty())
                .setPostalCode(patientModel.getZip())
                .addLine(patientModel.getAddress());
        fhirPatient.addAddress(address);

        return fhirPatient;
    }

    /**
     * Creates a FHIR AllergyIntolerance resource from a given Allergy model.
     *
     * @param allergyModel the Allergy model to convert
     * @return the corresponding FHIR AllergyIntolerance resource
     */
    public static AllergyIntolerance createFhirAllergy(it.unisa.medical_docs_to_cda.model.Allergy allergyModel) {
        AllergyIntolerance fhirAllergy = new AllergyIntolerance();

        if (allergyModel.getStart() != null) {
            fhirAllergy.setOnset(new org.hl7.fhir.r5.model.DateTimeType(
                    java.util.Date.from(allergyModel.getStart().atStartOfDay(ZoneId.systemDefault()).toInstant())));
        }
        if (allergyModel.getStop() != null) {
            fhirAllergy.setRecordedDate(java.util.Date.from(
                    allergyModel.getStop().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        if (allergyModel.getPatientId() != null) {
            fhirAllergy.setPatient(new Reference("Patient/" + allergyModel.getPatientId()));
        }

        if (allergyModel.getEncounterId() != null) {
            fhirAllergy.setEncounter(new Reference("Encounter/" + allergyModel.getEncounterId()));
        }

        if (allergyModel.getCode() != null) {
            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.addCoding()
                    .setSystem("http://snomed.info/sct") // Sistema di codici, esempio SNOMED CT
                    .setCode(allergyModel.getCode())
                    .setDisplay(allergyModel.getDescription() != null ? allergyModel.getDescription() : "Unknown");
            fhirAllergy.setCode(codeableConcept);
        }

        CodeableConcept verificationStatus = new CodeableConcept();
        verificationStatus.addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/allergyintolerance-verification")
                .setCode("confirmed");
        fhirAllergy.setVerificationStatus(verificationStatus);

        return fhirAllergy;
    }

    /**
     * Creates a FHIR CarePlan resource from a given Careplan model.
     *
     * @param carePlanModel the Careplan model to convert
     * @return the corresponding FHIR CarePlan resource
     */
    public static CarePlan createFhirCarePlan(it.unisa.medical_docs_to_cda.model.Careplan carePlanModel) {
        CarePlan fhirCarePlan = new CarePlan();

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

        if (carePlanModel.getPatientId() != null) {
            fhirCarePlan.setSubject(new Reference("Patient/" + carePlanModel.getPatientId()));
        }

        if (carePlanModel.getEncounterId() != null) {
            fhirCarePlan.addSupportingInfo(new Reference("Encounter/" + carePlanModel.getEncounterId()));
        }

        if (carePlanModel.getCode() != null || carePlanModel.getDescription() != null) {
            CarePlan.CarePlanActivityComponent activity = new CarePlan.CarePlanActivityComponent();

            CodeableReference activityCodeReference = new CodeableReference();
            CodeableConcept activityCode = new CodeableConcept();
            activityCode.addCoding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(carePlanModel.getCode())
                    .setDisplay(carePlanModel.getDescription() != null ? carePlanModel.getDescription() : "Unknown");
            activityCodeReference.setConcept(activityCode);
            activity.addPerformedActivity(activityCodeReference);
            fhirCarePlan.addActivity(activity);
            fhirCarePlan.setStatus(org.hl7.fhir.r5.model.Enumerations.RequestStatus.COMPLETED);
            fhirCarePlan.setIntent(CarePlanIntent.DIRECTIVE);
        }

        return fhirCarePlan;
    }

    /**
     * Creates a FHIR Condition resource from a given Condition model.
     *
     * @param conditionModel the Condition model to convert
     * @return the corresponding FHIR Condition resource
     */
    public static Condition createFhirCondition(it.unisa.medical_docs_to_cda.model.Condition conditionModel) {
        Condition fhirCondition = new Condition();

        if (conditionModel.getStart() != null || conditionModel.getStop() != null) {
            Period period = new Period();
            if (conditionModel.getStart() != null) {
                period.setStart(java.util.Date
                        .from(conditionModel.getStart().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            if (conditionModel.getStop() != null) {
                period.setEnd(
                        java.util.Date.from(conditionModel.getStop().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            fhirCondition.setOnset(period);
        }

        if (conditionModel.getPatientId() != null) {
            fhirCondition.setSubject(new Reference("Patient/" + conditionModel.getPatientId()));
        }

        if (conditionModel.getEncounterId() != null) {
            fhirCondition.setEncounter(new Reference("Encounter/" + conditionModel.getEncounterId()));
        }

        if (conditionModel.getCode() != null || conditionModel.getDescription() != null) {
            CodeableConcept conditionCode = new CodeableConcept();

            conditionCode.addCoding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(conditionModel.getCode())
                    .setDisplay(conditionModel.getDescription() != null ? conditionModel.getDescription() : "Unknown");
            fhirCondition.setCode(conditionCode);
            fhirCondition.setClinicalStatus(conditionCode);
        }

        return fhirCondition;
    }

    /**
     * Creates a FHIR Encounter resource from a given Encounter model.
     *
     * @param encounterModel the Encounter model to convert
     * @return the corresponding FHIR Encounter resource
     */
    public static Encounter createFhirEncounter(it.unisa.medical_docs_to_cda.model.Encounter encounterModel) {
        Encounter fhirEncounter = new Encounter();

        if (encounterModel.getId() != null) {
            fhirEncounter.setId(encounterModel.getId());
        }

        fhirEncounter.setStatus(EncounterStatus.COMPLETED);
        fhirEncounter.addLocation(
                new EncounterLocationComponent(new Reference("Location/" + encounterModel.getOrganizationId())));

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

        if (encounterModel.getPatientId() != null) {
            fhirEncounter.setSubject(new Reference("Patient/" + encounterModel.getPatientId()));
        }

        if (encounterModel.getCode() != null || encounterModel.getDescription() != null) {
            CodeableConcept code = new CodeableConcept();
            code.addCoding(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(encounterModel.getCode())
                    .setDisplay(encounterModel.getDescription() != null ? encounterModel.getDescription() : "Unknown"));
            fhirEncounter.setType(java.util.Collections.singletonList(code));
        }

        return fhirEncounter;
    }

    /**
     * Creates a FHIR ImagingStudy resource from a given ImagingStudy model.
     *
     * @param imagingStudyModel the ImagingStudy model to convert
     * @return the corresponding FHIR ImagingStudy resource
     */
    public static ImagingStudy createFhirImagingStudy(
            it.unisa.medical_docs_to_cda.model.ImagingStudy imagingStudyModel) {
        ImagingStudy fhirImagingStudy = new ImagingStudy();

        if (imagingStudyModel.getId() != null) {
            fhirImagingStudy.setId(imagingStudyModel.getId());
        }

        if (imagingStudyModel.getDate() != null) {
            fhirImagingStudy.setStarted(
                    java.util.Date.from(imagingStudyModel.getDate().atZone(ZoneId.systemDefault()).toInstant()));
        }

        if (imagingStudyModel.getPatientId() != null) {
            fhirImagingStudy.setSubject(new Reference("Patient/" + imagingStudyModel.getPatientId()));
        }

        if (imagingStudyModel.getEncounterId() != null) {
            fhirImagingStudy.setEncounter(new Reference("Encounter/" + imagingStudyModel.getEncounterId()));
        }

        if (imagingStudyModel.getModalityCode() != null || imagingStudyModel.getModalityDescription() != null) {
            CodeableConcept modality = new CodeableConcept();
            modality.addCoding(new Coding()
                    .setSystem("http://terminology.hl7.org/CodeSystem/imaging-modality")
                    .setCode(imagingStudyModel.getModalityCode())
                    .setDisplay(imagingStudyModel.getModalityDescription() != null
                            ? imagingStudyModel.getModalityDescription()
                            : "Unknown"));
            fhirImagingStudy.addModality(modality);
        }

        if (imagingStudyModel.getSopCode() != null || imagingStudyModel.getSopDescription() != null) {

            CodeableConcept sop = new CodeableConcept();
            sop.addCoding(new Coding()
                    .setSystem("http://dicom.nema.org/resources/ontology/DCM")
                    .setCode(imagingStudyModel.getSopCode())
                    .setDisplay(imagingStudyModel.getSopDescription() != null ? imagingStudyModel.getSopDescription()
                            : "Unknown"));
            fhirImagingStudy.addModality(sop);
        }

        return fhirImagingStudy;
    }

    /**
     * Creates a FHIR Immunization resource from a given Immunization model.
     *
     * @param immunizationModel the Immunization model to convert
     * @return the corresponding FHIR Immunization resource
     */
    public static Immunization createFhirImmunization(
            it.unisa.medical_docs_to_cda.model.Immunization immunizationModel) {
        Immunization fhirImmunization = new Immunization();

        if (immunizationModel.getDate() != null) {
            fhirImmunization.setOccurrence(new org.hl7.fhir.r5.model.DateTimeType(
                    Date.from(immunizationModel.getDate().atZone(ZoneId.systemDefault()).toInstant())));
        }

        if (immunizationModel.getPatientId() != null) {
            fhirImmunization.setPatient(new Reference("Patient/" + immunizationModel.getPatientId()));
        }

        if (immunizationModel.getEncounterId() != null) {
            fhirImmunization.setEncounter(new Reference("Encounter/" + immunizationModel.getEncounterId()));
        }

        if (immunizationModel.getCode() != null || immunizationModel.getDescription() != null) {
            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.addCoding(new Coding()
                    .setSystem("http://hl7.org/fhir/sid/cvx")
                    .setCode(immunizationModel.getCode())
                    .setDisplay(immunizationModel.getDescription() != null ? immunizationModel.getDescription()
                            : "Unknown"));
            fhirImmunization.setVaccineCode(codeableConcept);
        }

        ImmunizationProgramEligibilityComponent ele = new ImmunizationProgramEligibilityComponent();
        ele.setProgram(new CodeableConcept().addCoding(new Coding().setCode(immunizationModel.getCode())
                .setSystem("http://hl7.org/fhir/sid/cvx").setDisplay(
                        immunizationModel.getDescription() != null ? immunizationModel.getDescription() : "Unknown")))
                .setProgramStatus(new CodeableConcept().addCoding(new Coding().setCode(immunizationModel.getCode())
                        .setSystem("http://hl7.org/fhir/sid/cvx")
                        .setDisplay(immunizationModel.getDescription() != null ? immunizationModel.getDescription()
                                : "Unknown")));

        fhirImmunization.setProgramEligibility(List.of(ele));

        fhirImmunization.setStatus(ImmunizationStatusCodes.COMPLETED);

        return fhirImmunization;
    }

    /**
     * Creates a FHIR MedicationStatement resource from a given Medication model.
     *
     * @param medicationModel the Medication model to convert
     * @return the corresponding FHIR MedicationStatement resource
     */
    public static MedicationStatement createFhirMedicationStatement(
            it.unisa.medical_docs_to_cda.model.Medication medicationModel) {
        MedicationStatement fhirMedicationStatement = new MedicationStatement();

        if (medicationModel.getPatientId() != null) {
            fhirMedicationStatement.setSubject(new Reference("Patient/" + medicationModel.getPatientId()));
        }

        if (medicationModel.getEncounterId() != null) {
            fhirMedicationStatement.setEncounter(new Reference("Encounter/" + medicationModel.getEncounterId()));
        }

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

        if (medicationModel.getCode() != null || medicationModel.getDescription() != null) {
            CodeableReference medication = new CodeableReference();
            CodeableConcept medicationCodeableConcept = new CodeableConcept();
            medicationCodeableConcept.addCoding(new Coding()
                    .setSystem("http://www.nlm.nih.gov/research/umls/rxnorm")
                    .setCode(medicationModel.getCode())
                    .setDisplay(
                            medicationModel.getDescription() != null ? medicationModel.getDescription() : "Unknown"));
            medication.setConcept(medicationCodeableConcept);
            fhirMedicationStatement.setMedication(medication);
        }

        fhirMedicationStatement.setStatus(MedicationStatementStatusCodes.RECORDED);

        return fhirMedicationStatement;
    }

    /**
     * Creates a FHIR Observation resource from a given Observation model.
     *
     * @param observationModel the Observation model to convert
     * @return the corresponding FHIR Observation resource
     */
    public static Observation createFhirObservation(it.unisa.medical_docs_to_cda.model.Observation observationModel) {
        Observation fhirObservation = new Observation();

        fhirObservation.setStatus(ObservationStatus.FINAL);

        if (observationModel.getPatientId() != null) {
            fhirObservation.setSubject(new Reference("Patient/" + observationModel.getPatientId()));
        }

        if (observationModel.getEncounterId() != null) {
            fhirObservation.setEncounter(new Reference("Encounter/" + observationModel.getEncounterId()));
        }

        if (observationModel.getDate() != null) {
            fhirObservation.setEffective(new org.hl7.fhir.r5.model.DateTimeType(
                    Date.from(observationModel.getDate().atZone(ZoneId.systemDefault()).toInstant())));
        }

        if (observationModel.getCode() != null || observationModel.getDescription() != null) {
            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.addCoding(new Coding()
                    .setSystem("http://loinc.org")
                    .setCode(observationModel.getCode())
                    .setDisplay(
                            observationModel.getDescription() != null ? observationModel.getDescription() : "Unknown"));
            fhirObservation.setCode(codeableConcept);
        }

        if (observationModel.getValue() != null) {
            try {
                double value = Double.parseDouble(observationModel.getValue());
                Quantity quantity = new Quantity();
                quantity.setValue(value);
                if (observationModel.getUnits() != null) {
                    quantity.setUnit(observationModel.getUnits());
                }
                fhirObservation.setValue(quantity);
            } catch (NumberFormatException e) {
                fhirObservation.setValue(new org.hl7.fhir.r5.model.StringType(observationModel.getValue()));
            }
        }

        return fhirObservation;
    }

    /**
     * Creates a FHIR Procedure resource from a given Procedure model.
     *
     * @param procedureModel the Procedure model to convert
     * @return the corresponding FHIR Procedure resource
     */
    public static Procedure createFhirProcedure(it.unisa.medical_docs_to_cda.model.Procedure procedureModel) {
        Procedure fhirProcedure = new Procedure();

        if (procedureModel.getPatientId() != null) {
            fhirProcedure.setSubject(new Reference("Patient/" + procedureModel.getPatientId()));
        }

        if (procedureModel.getEncounterId() != null) {
            fhirProcedure.setEncounter(new Reference("Encounter/" + procedureModel.getEncounterId()));
        }

        if (procedureModel.getCode() != null || procedureModel.getDescription() != null) {
            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.addCoding(new Coding()
                    .setSystem("http://snomed.info/sct")
                    .setCode(procedureModel.getCode())
                    .setDisplay(procedureModel.getDescription() != null ? procedureModel.getDescription() : "Unknown"));
            fhirProcedure.setCode(codeableConcept);
        }
        fhirProcedure.setStatus(EventStatus.COMPLETED);

        return fhirProcedure;
    }
}
