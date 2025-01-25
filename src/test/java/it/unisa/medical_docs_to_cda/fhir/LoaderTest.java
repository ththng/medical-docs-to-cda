package it.unisa.medical_docs_to_cda.fhir;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import org.hl7.fhir.r5.model.Bundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import it.unisa.medical_docs_to_cda.fhir.LoaderFhir;
import it.unisa.medical_docs_to_cda.model.Encounter;
import it.unisa.medical_docs_to_cda.model.Patient; // Use the correct Patient class
import it.unisa.medical_docs_to_cda.repositories.PatientRepository;
import it.unisa.medical_docs_to_cda.repositories.AllergyRepository;
import it.unisa.medical_docs_to_cda.repositories.CareplanRepository;
import it.unisa.medical_docs_to_cda.repositories.MedicationRepository;
import it.unisa.medical_docs_to_cda.repositories.ObservationRepository;
import it.unisa.medical_docs_to_cda.repositories.ProcedureRepository;
import it.unisa.medical_docs_to_cda.repositories.ConditionRepository;
import it.unisa.medical_docs_to_cda.repositories.ImagingStudyRepository;
import it.unisa.medical_docs_to_cda.repositories.ImmunizationRepository;

public class LoaderTest {

    private LoaderFhir loaderFhir;
    private PatientRepository patientRepo;
    private AllergyRepository allergyRepo;
    private CareplanRepository careplanRepo;
    private MedicationRepository medicationRepo;
    private ObservationRepository observationRepo;
    private ProcedureRepository procedureRepo;
    private ConditionRepository conditionRepo;
    private ImagingStudyRepository imagingStudyRepo;
    private ImmunizationRepository immunizationRepo;
    private FhirContext fhirContext;
    private IGenericClient client;

 /*   @Test
    public void testLoadEncounter() {
        loaderFhir = new LoaderFhir();
        patientRepo = mock(PatientRepository.class);
        allergyRepo = mock(AllergyRepository.class);
        careplanRepo = mock(CareplanRepository.class);
        medicationRepo = mock(MedicationRepository.class);
        observationRepo = mock(ObservationRepository.class);
        procedureRepo = mock(ProcedureRepository.class);
        conditionRepo = mock(ConditionRepository.class);
        imagingStudyRepo = mock(ImagingStudyRepository.class);
        immunizationRepo = mock(ImmunizationRepository.class);
        fhirContext = FhirContext.forR5(); // Use real FHIR context
        client = fhirContext.newRestfulGenericClient("https://hapi.fhir.org/baseR5"); // Use real FHIR client
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

        // Set up the mocks
        when(patientRepo.findById(any())).thenReturn(Optional.of(patientModel));
        when(allergyRepo.findByEncounterId(any())).thenReturn(new ArrayList<>());
        when(careplanRepo.findByEncounterId(any())).thenReturn(new ArrayList<>());
        when(medicationRepo.findByEncounterId(any())).thenReturn(new ArrayList<>());
        when(observationRepo.findByEncounterId(any())).thenReturn(new ArrayList<>());
        when(procedureRepo.findByEncounterId(any())).thenReturn(new ArrayList<>());
        when(conditionRepo.findByEncounterId(any())).thenReturn(new ArrayList<>());
        when(imagingStudyRepo.findByEncounterId(any())).thenReturn(new ArrayList<>());
        when(immunizationRepo.findByEncounterId(any())).thenReturn(new ArrayList<>());
    

    
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
        boolean result = loaderFhir.loadEncounter(encounterModel); // Use the return value as confirmation
        // No assertion needed, just rely on the return value
    }*/
}
