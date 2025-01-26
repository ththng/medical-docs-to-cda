package it.unisa.medical_docs_to_cda.fhir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.hl7.fhir.r5.model.Address;
import org.hl7.fhir.r5.model.AllergyIntolerance;
import org.hl7.fhir.r5.model.CarePlan;
import org.hl7.fhir.r5.model.Condition;
import org.hl7.fhir.r5.model.Encounter;
import org.hl7.fhir.r5.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r5.model.ImagingStudy;
import org.hl7.fhir.r5.model.Immunization;
import org.hl7.fhir.r5.model.Immunization.ImmunizationStatusCodes;
import org.hl7.fhir.r5.model.MedicationStatement;
import org.hl7.fhir.r5.model.Observation;
import org.hl7.fhir.r5.model.Patient;
import org.hl7.fhir.r5.model.Procedure;
import org.hl7.fhir.r5.model.StringType;
import org.junit.jupiter.api.Test;


public class FhirAdapterTest {
    @Test
    public void test_create_fhir_patient_with_all_parameters() {
        // Mock the patient model
        it.unisa.medical_docs_to_cda.model.Patient patientModel = mock(it.unisa.medical_docs_to_cda.model.Patient.class);
    
        // Set up the mock behavior
        when(patientModel.getId()).thenReturn("12345");
        when(patientModel.getSSN()).thenReturn("123-45-6789");
        when(patientModel.getDrivers()).thenReturn("D1234567");
        when(patientModel.getPassport()).thenReturn("P1234567");
        when(patientModel.getLast()).thenReturn("Doe");
        when(patientModel.getFirst()).thenReturn("John");
        when(patientModel.getMaiden()).thenReturn("Smith");
        when(patientModel.getBirthDate()).thenReturn(java.time.LocalDate.of(1980, 1, 1));
        when(patientModel.getDeathDate()).thenReturn(java.time.LocalDate.of(2020, 1, 1));
        when(patientModel.getGender()).thenReturn("male");
        when(patientModel.getCity()).thenReturn("New York");
        when(patientModel.getState()).thenReturn("NY");
        when(patientModel.getCounty()).thenReturn("USA");
        when(patientModel.getZip()).thenReturn("10001");
        when(patientModel.getAddress()).thenReturn("123 Main St");
        when(patientModel.getBirthPlace()).thenReturn("New York, USA");

        // Call the method under test
        Patient fhirPatient = FhirAdapter.createFhirPatient(patientModel);

        // Verify the FHIR Patient object
        assertEquals("12345", fhirPatient.getId());
        assertEquals(3, fhirPatient.getIdentifier().size());
        assertEquals("http://hl7.org/fhir/sid/us-ssn", fhirPatient.getIdentifier().get(0).getSystem());
        assertEquals("123-45-6789", fhirPatient.getIdentifier().get(0).getValue());
        assertEquals("http://example.org/fhir/sid/drivers-license", fhirPatient.getIdentifier().get(1).getSystem());
        assertEquals("D1234567", fhirPatient.getIdentifier().get(1).getValue());
        assertEquals("http://example.org/fhir/sid/passport", fhirPatient.getIdentifier().get(2).getSystem());
        assertEquals("P1234567", fhirPatient.getIdentifier().get(2).getValue());
    
        assertEquals("Doe", fhirPatient.getNameFirstRep().getFamily());
        assertEquals("John", fhirPatient.getNameFirstRep().getGiven().get(0).getValue());
        assertEquals("Smith", fhirPatient.getNameFirstRep().getGiven().get(1).getValue());

        assertEquals(java.util.Date.from(java.time.LocalDate.of(1980, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()), fhirPatient.getBirthDate());
        assertTrue(fhirPatient.hasDeceasedDateTimeType());
    
        assertEquals(AdministrativeGender.MALE, fhirPatient.getGender());

        Address address = fhirPatient.getAddressFirstRep();
        assertEquals("New York", address.getCity());
        assertEquals("NY", address.getState());
        assertEquals("USA", address.getCountry());
        assertEquals("10001", address.getPostalCode());
        assertEquals("123 Main St", address.getLine().get(0).getValue());

        assertTrue(fhirPatient.hasExtension());
    }
    @Test
    public void test_create_fhir_allergy_with_details_with_mock() {
        // Arrange
        it.unisa.medical_docs_to_cda.model.Allergy allergyModel = mock(it.unisa.medical_docs_to_cda.model.Allergy.class);
        when(allergyModel.getStart()).thenReturn(java.time.LocalDate.of(2020, 1, 1));
        when(allergyModel.getStop()).thenReturn(java.time.LocalDate.of(2020, 12, 31));
        when(allergyModel.getPatientId()).thenReturn("patient-123");
        when(allergyModel.getEncounterId()).thenReturn("encounter-456");
        when(allergyModel.getCode()).thenReturn("allergy-code");
        when(allergyModel.getDescription()).thenReturn("Peanut Allergy");

        // Act
        FhirAdapter adapter = new FhirAdapter();
        AllergyIntolerance result = adapter.createFhirAllergy(allergyModel);

        // Assert
        assertEquals("2020-01-01T00:00:00+01:00", result.getOnsetDateTimeType().getValueAsString());
        assertEquals("2020-12-31T00:00:00+01:00", result.getRecordedDateElement().getValueAsString());
        assertEquals("Patient/patient-123", result.getPatient().getReference());
        assertEquals("Encounter/encounter-456", result.getEncounter().getReference());
        assertEquals("allergy-code", result.getCode().getCodingFirstRep().getCode());
        assertEquals("Peanut Allergy", result.getCode().getCodingFirstRep().getDisplay());
        assertEquals("http://snomed.info/sct", result.getCode().getCodingFirstRep().getSystem());
        assertEquals("confirmed", result.getVerificationStatus().getCodingFirstRep().getCode());
    }

    @Test
    public void test_create_fhir_careplan_with_details_with_mock() {
        // Arrange
        it.unisa.medical_docs_to_cda.model.Careplan carePlanModel = mock(it.unisa.medical_docs_to_cda.model.Careplan.class);
        when(carePlanModel.getStart()).thenReturn(java.time.LocalDate.of(2021, 1, 1));
        when(carePlanModel.getStop()).thenReturn(java.time.LocalDate.of(2021, 12, 31));
        when(carePlanModel.getPatientId()).thenReturn("patient-789");
        when(carePlanModel.getEncounterId()).thenReturn("encounter-101");
        when(carePlanModel.getCode()).thenReturn("careplan-code");
        when(carePlanModel.getDescription()).thenReturn("Diabetes Management");
        when(carePlanModel.getReasonCode()).thenReturn("reason-code");
        when(carePlanModel.getReasonDescription()).thenReturn("Routine Checkup");

        // Act
        FhirAdapter adapter = new FhirAdapter();
        CarePlan result = adapter.createFhirCarePlan(carePlanModel);

        // Assert
        assertEquals("2021-01-01T00:00:00+01:00", result.getPeriod().getStartElement().getValueAsString());
        assertEquals("2021-12-31T00:00:00+01:00", result.getPeriod().getEndElement().getValueAsString());
        assertEquals("Patient/patient-789", result.getSubject().getReference());
        assertEquals("Encounter/encounter-101", result.getSupportingInfoFirstRep().getReference());
        assertEquals("careplan-code", result.getActivityFirstRep().getPerformedActivityFirstRep().getConcept().getCodingFirstRep().getCode());
        assertEquals("Diabetes Management", result.getActivityFirstRep().getPerformedActivityFirstRep().getConcept().getCodingFirstRep().getDisplay());
        assertEquals("http://snomed.info/sct", result.getActivityFirstRep().getPerformedActivityFirstRep().getConcept().getCodingFirstRep().getSystem());
        assertEquals("reason-code", result.getCategoryFirstRep().getCodingFirstRep().getCode());
        assertEquals("Routine Checkup", result.getCategoryFirstRep().getCodingFirstRep().getDisplay());
    }

         @Test
        public void test_create_fhir_condition_with_details_with_mock() {
            // Arrange
            it.unisa.medical_docs_to_cda.model.Condition conditionModel = mock(it.unisa.medical_docs_to_cda.model.Condition.class);
            when(conditionModel.getStart()).thenReturn(java.time.LocalDate.of(2022, 1, 1));
            when(conditionModel.getStop()).thenReturn(java.time.LocalDate.of(2022, 12, 31));
            when(conditionModel.getPatientId()).thenReturn("patient-123");
            when(conditionModel.getEncounterId()).thenReturn("encounter-456");
            when(conditionModel.getCode()).thenReturn("condition-code");
            when(conditionModel.getDescription()).thenReturn("Hypertension");
    
            // Act
            FhirAdapter adapter = new FhirAdapter();
            Condition result = adapter.createFhirCondition(conditionModel);
    
            // Assert
            assertEquals("2022-01-01T00:00:00+01:00", result.getOnsetPeriod().getStartElement().getValueAsString());
            assertEquals("2022-12-31T00:00:00+01:00", result.getOnsetPeriod().getEndElement().getValueAsString());
            assertEquals("Patient/patient-123", result.getSubject().getReference());
            assertEquals("Encounter/encounter-456", result.getEncounter().getReference());
            assertEquals("condition-code", result.getCode().getCodingFirstRep().getCode());
            assertEquals("Hypertension", result.getCode().getCodingFirstRep().getDisplay());
            assertEquals("http://snomed.info/sct", result.getCode().getCodingFirstRep().getSystem());
        }

     @Test
    public void test_create_fhir_encounter_with_details_with_mock() {
        // Arrange
        it.unisa.medical_docs_to_cda.model.Encounter encounterModel = mock(it.unisa.medical_docs_to_cda.model.Encounter.class);
        when(encounterModel.getId()).thenReturn("encounter-789");
        when(encounterModel.getStart()).thenReturn(java.time.LocalDateTime.of(LocalDate.of(2022, 1, 1),LocalTime.of(00,00)));
        when(encounterModel.getStop()).thenReturn(java.time.LocalDateTime.of(LocalDate.of(2022, 12, 31),LocalTime.of(00,00)));
        when(encounterModel.getPatientId()).thenReturn("patient-456");
        when(encounterModel.getOrganizationId()).thenReturn("org-123");
        when(encounterModel.getProviderId()).thenReturn("provider-789");
        when(encounterModel.getEncounterClass()).thenReturn("AMB");
        when(encounterModel.getCode()).thenReturn("encounter-code");
        when(encounterModel.getDescription()).thenReturn("Routine Checkup");
        when(encounterModel.getReasonCode()).thenReturn("reason-code");
        when(encounterModel.getReasonDescription()).thenReturn("Follow-up");

        // Act
        FhirAdapter adapter = new FhirAdapter();
        Encounter result = adapter.createFhirEncounter(encounterModel);

        // Assert
        assertEquals("encounter-789", result.getId());
        assertEquals("2022-01-01T00:00:00+01:00", result.getActualPeriod().getStartElement().getValueAsString());
        assertEquals("2022-12-31T00:00:00+01:00", result.getActualPeriod().getEndElement().getValueAsString());
        assertEquals("Patient/patient-456", result.getSubject().getReference());
        assertEquals("Organization/org-123", result.getServiceProvider().getReference());
        assertEquals("Practitioner/provider-789", result.getParticipantFirstRep().getActor().getReference());
        assertEquals("encounter-code", result.getTypeFirstRep().getCodingFirstRep().getCode());
        assertEquals("Routine Checkup", result.getTypeFirstRep().getCodingFirstRep().getDisplay());
        assertEquals("http://snomed.info/sct", result.getTypeFirstRep().getCodingFirstRep().getSystem());
        assertEquals("reason-code", result.getReasonFirstRep().getUseFirstRep().getCodingFirstRep().getCode());
        assertEquals("Follow-up", result.getReasonFirstRep().getUseFirstRep().getCodingFirstRep().getDisplay());
    }


       @Test
        public void test_create_fhir_imaging_study_with_details_with_mock() {
            // Arrange
            it.unisa.medical_docs_to_cda.model.ImagingStudy imagingStudyModel = mock(it.unisa.medical_docs_to_cda.model.ImagingStudy.class);
            when(imagingStudyModel.getId()).thenReturn("imaging-study-123");
            when(imagingStudyModel.getDate()).thenReturn(java.time.LocalDateTime.of(LocalDate.of(2022, 1, 1),LocalTime.of(00,00)));
            when(imagingStudyModel.getPatientId()).thenReturn("patient-456");
            when(imagingStudyModel.getEncounterId()).thenReturn("encounter-789");
            when(imagingStudyModel.getModalityCode()).thenReturn("CT");
            when(imagingStudyModel.getModalityDescription()).thenReturn("CT Scan");
            when(imagingStudyModel.getSopCode()).thenReturn("1.2.840.10008.5.1.4.1.1.2");
            when(imagingStudyModel.getSopDescription()).thenReturn("CT Image Storage");
    
            // Act
            FhirAdapter adapter = new FhirAdapter();
            ImagingStudy result = adapter.createFhirImagingStudy(imagingStudyModel);
    
            // Assert
            assertEquals("imaging-study-123", result.getId());
            assertEquals("2022-01-01T00:00:00+01:00", result.getStartedElement().getValueAsString());
            assertEquals("Patient/patient-456", result.getSubject().getReference());
            assertEquals("Encounter/encounter-789", result.getEncounter().getReference());
            assertEquals("CT", result.getModalityFirstRep().getCodingFirstRep().getCode());
            assertEquals("CT Scan", result.getModalityFirstRep().getCodingFirstRep().getDisplay());
            assertEquals("http://terminology.hl7.org/CodeSystem/imaging-modality", result.getModalityFirstRep().getCodingFirstRep().getSystem());
            assertEquals("1.2.840.10008.5.1.4.1.1.2", result.getModality().get(1).getCodingFirstRep().getCode());
            assertEquals("CT Image Storage", result.getModality().get(1).getCodingFirstRep().getDisplay());
            assertEquals("http://dicom.nema.org/resources/ontology/DCM", result.getModality().get(1).getCodingFirstRep().getSystem());
        }

              @Test
    public void test_create_fhir_immunization_with_details_with_mock() {
        // Arrange
        it.unisa.medical_docs_to_cda.model.Immunization immunizationModel = mock(it.unisa.medical_docs_to_cda.model.Immunization.class);
        when(immunizationModel.getDate()).thenReturn(java.time.LocalDateTime.of(LocalDate.of(2022, 1, 1),LocalTime.of(00,00)));
        when(immunizationModel.getPatientId()).thenReturn("patient-456");
        when(immunizationModel.getEncounterId()).thenReturn("encounter-789");
        when(immunizationModel.getCode()).thenReturn("12345");
        when(immunizationModel.getDescription()).thenReturn("COVID-19 Vaccine");

        // Act
        FhirAdapter adapter = new FhirAdapter();
        Immunization result = adapter.createFhirImmunization(immunizationModel);

        // Assert
        assertEquals("2022-01-01T00:00:00+01:00", result.getOccurrenceDateTimeType().getValueAsString());
        assertEquals("Patient/patient-456", result.getPatient().getReference());
        assertEquals("Encounter/encounter-789", result.getEncounter().getReference());
        assertEquals("12345", result.getVaccineCode().getCodingFirstRep().getCode());
        assertEquals("COVID-19 Vaccine", result.getVaccineCode().getCodingFirstRep().getDisplay());
        assertEquals("http://hl7.org/fhir/sid/cvx", result.getVaccineCode().getCodingFirstRep().getSystem());
        assertEquals(ImmunizationStatusCodes.COMPLETED, result.getStatus());
    }


    @Test
    public void test_create_fhir_medication_statement_with_details_with_mock() {
        // Arrange
        it.unisa.medical_docs_to_cda.model.Medication medicationModel = mock(it.unisa.medical_docs_to_cda.model.Medication.class);
        when(medicationModel.getPatientId()).thenReturn("patient-123");
        when(medicationModel.getEncounterId()).thenReturn("encounter-456");
        when(medicationModel.getStart()).thenReturn(java.time.LocalDateTime.of(LocalDate.of(2022, 1, 1),LocalTime.of(00,00)));
        when(medicationModel.getStop()).thenReturn(java.time.LocalDateTime.of(LocalDate.of(2022, 12, 31),LocalTime.of(00,00)));
        when(medicationModel.getCode()).thenReturn("med-789");
        when(medicationModel.getDescription()).thenReturn("Aspirin");
        when(medicationModel.getReasonCode()).thenReturn("reason-101");
        when(medicationModel.getReasonDescription()).thenReturn("Headache");

        // Act
        FhirAdapter adapter = new FhirAdapter();
        MedicationStatement result = adapter.createFhirMedicationStatement(medicationModel);

        // Assert
        assertEquals("Patient/patient-123", result.getSubject().getReference());
        assertEquals("Encounter/encounter-456", result.getEncounter().getReference());
        assertEquals("2022-01-01T00:00:00+01:00", result.getEffectivePeriod().getStartElement().getValueAsString());
        assertEquals("2022-12-31T00:00:00+01:00", result.getEffectivePeriod().getEndElement().getValueAsString());
        assertEquals("med-789", result.getMedication().getConcept().getCodingFirstRep().getCode());
        assertEquals("Aspirin", result.getMedication().getConcept().getCodingFirstRep().getDisplay());
        assertEquals("http://www.nlm.nih.gov/research/umls/rxnorm", result.getMedication().getConcept().getCodingFirstRep().getSystem());
    }
        // Create FHIR Observation with details using mock data
        @Test
        public void test_create_fhir_observation_with_details_with_mock() {
            // Arrange
            it.unisa.medical_docs_to_cda.model.Observation observationModel = mock(it.unisa.medical_docs_to_cda.model.Observation.class);
            when(observationModel.getPatientId()).thenReturn("patient-123");
            when(observationModel.getEncounterId()).thenReturn("encounter-456");
            when(observationModel.getDate()).thenReturn(java.time.LocalDateTime.of(LocalDate.of(2022, 1, 1),LocalTime.of(00,00)));
            when(observationModel.getCode()).thenReturn("obs-789");
            when(observationModel.getDescription()).thenReturn("Blood Pressure");
            when(observationModel.getValue()).thenReturn("120/80");
            when(observationModel.getUnits()).thenReturn("mmHg");
    
            // Act
            FhirAdapter adapter = new FhirAdapter();
            Observation result = adapter.createFhirObservation(observationModel);
    
            // Assert
            assertEquals("Patient/patient-123", result.getSubject().getReference());
            assertEquals("Encounter/encounter-456", result.getEncounter().getReference());
            assertEquals("2022-01-01T00:00:00+01:00", result.getEffectiveDateTimeType().getValueAsString());
            assertEquals("obs-789", result.getCode().getCodingFirstRep().getCode());
            assertEquals("Blood Pressure", result.getCode().getCodingFirstRep().getDisplay());
            assertEquals("http://loinc.org", result.getCode().getCodingFirstRep().getSystem());
            assertEquals("120/80", ((StringType) result.getValue()).getValue());
        }


            // Create FHIR Procedure with details using mock data
    @Test
    public void test_create_fhir_procedure_with_details_with_mock() {
        // Arrange
        it.unisa.medical_docs_to_cda.model.Procedure procedureModel = mock(it.unisa.medical_docs_to_cda.model.Procedure.class);
        when(procedureModel.getPatientId()).thenReturn("patient-123");
        when(procedureModel.getEncounterId()).thenReturn("encounter-456");
        when(procedureModel.getDate()).thenReturn(java.time.LocalDate.of(2023, 1, 1));
        when(procedureModel.getCode()).thenReturn("proc-789");
        when(procedureModel.getDescription()).thenReturn("Appendectomy");
        when(procedureModel.getReasonCode()).thenReturn("reason-101");
        when(procedureModel.getReasonDescription()).thenReturn("Acute Appendicitis");

        // Act
        FhirAdapter adapter = new FhirAdapter();
        Procedure result = adapter.createFhirProcedure(procedureModel);

        // Assert
        assertEquals("Patient/patient-123", result.getSubject().getReference());
        assertEquals("Encounter/encounter-456", result.getEncounter().getReference());
        assertEquals("proc-789", result.getCode().getCodingFirstRep().getCode());
        assertEquals("Appendectomy", result.getCode().getCodingFirstRep().getDisplay());
        assertEquals("http://snomed.info/sct", result.getCode().getCodingFirstRep().getSystem());
        assertEquals("reason-101", result.getReasonFirstRep().getConcept().getCodingFirstRep().getCode());
        assertEquals("Acute Appendicitis", result.getReasonFirstRep().getConcept().getCodingFirstRep().getDisplay());
    }


}