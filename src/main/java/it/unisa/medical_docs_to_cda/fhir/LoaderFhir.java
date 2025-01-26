package it.unisa.medical_docs_to_cda.fhir;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r5.model.AllergyIntolerance;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.hl7.fhir.r5.model.MedicationStatement;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;

import it.unisa.medical_docs_to_cda.model.*;
import it.unisa.medical_docs_to_cda.repositories.*;
@Controller
public class LoaderFhir {

    @Autowired
    private PatientRepository patientRepo;
    @Autowired
    private AllergyRepository allergyRepo;
    @Autowired
    private CareplanRepository careplanRepo;
    @Autowired
    private MedicationRepository medicationRepo;
    @Autowired
    private ObservationRepository observationRepo;
    @Autowired
    private ProcedureRepository procedureRepo;
    @Autowired
    private ConditionRepository conditionRepo;
    @Autowired
    private ImagingStudyRepository imagingStudyRepo;
    @Autowired
    private ImmunizationRepository immunizationRepo;
  
    public boolean loadEncounter(it.unisa.medical_docs_to_cda.model.Encounter encounterModel) {

        it.unisa.medical_docs_to_cda.model.Patient patient = patientRepo.findById(encounterModel.getPatientId()).get();
        String encounterId = encounterModel.getId();
        List<Observation> observation = observationRepo.findByEncounterId(encounterId);
        List<Allergy> allergies = allergyRepo.findByEncounterId(encounterId);
        List<Condition> conditions = conditionRepo.findByEncounterId(encounterId);
        List<Careplan> careplans = careplanRepo.findByEncounterId(encounterId);
        List<ImagingStudy> imagingStudies = imagingStudyRepo.findByEncounterId(encounterId);
        List<Immunization> immunizations = immunizationRepo.findByEncounterId(encounterId);
        List<Medication> medications = medicationRepo.findByEncounterId(encounterId);
        List<Procedure> procedures = procedureRepo.findByEncounterId(encounterId);

        FhirContext ctx = FhirContext.forR5();
        String serverBase = "https://server.fire.ly/r5";

        IGenericClient client = ctx.newRestfulGenericClient(serverBase);
        Patient fhirPatient = FhirAdapter.createFhirPatient(patient);
        org.hl7.fhir.r5.model.Encounter fhirEncounter = FhirAdapter.createFhirEncounter(encounterModel);

        List<AllergyIntolerance> fhirAllergies = new ArrayList<>();

        for (Allergy allergy : allergies) {
            fhirAllergies.add(FhirAdapter.createFhirAllergy(allergy));
        }
        List<org.hl7.fhir.r5.model.Condition> fhirConditions = new ArrayList<>();
        for (Condition condition : conditions) {
            fhirConditions.add(FhirAdapter.createFhirCondition(condition));
        }
        List<MedicationStatement> fhirMedications = new ArrayList<>();
        for (Medication medication : medications) {
            fhirMedications.add(FhirAdapter.createFhirMedicationStatement(medication));
        }
        List<org.hl7.fhir.r5.model.Procedure> fhirProcedures = new ArrayList<>();
        for (Procedure procedure : procedures) {
            fhirProcedures.add(FhirAdapter.createFhirProcedure(procedure));
        }
        List<org.hl7.fhir.r5.model.Observation> fhirObservations = new ArrayList<>();
        for (Observation observation1 : observation) {
            fhirObservations.add(FhirAdapter.createFhirObservation(observation1));
        }
        List<org.hl7.fhir.r5.model.CarePlan> fhirCareplans = new ArrayList<>();
        for (Careplan careplan : careplans) {
            fhirCareplans.add(FhirAdapter.createFhirCarePlan(careplan));
        }
        List<org.hl7.fhir.r5.model.ImagingStudy> fhirImagingStudies = new ArrayList<>();
        for (ImagingStudy imagingStudy : imagingStudies) {
            fhirImagingStudies.add(FhirAdapter.createFhirImagingStudy(imagingStudy));
        }
        List<org.hl7.fhir.r5.model.Immunization> fhirImmunizations = new ArrayList<>();
        for (Immunization immunization : immunizations) {
            fhirImmunizations.add(FhirAdapter.createFhirImmunization(immunization));
        }

        Bundle response = client.search()
                .forResource(Patient.class)
                .where(Patient.FAMILY.matchesExactly().value(patient.getLast()))
                .where(Patient.GIVEN.matchesExactly().value(patient.getFirst()))
                .where(Patient.ADDRESS_CITY.matchesExactly().value(patient.getCity()))
                .returnBundle(Bundle.class)
                .execute();
        Bundle responseEncounter = client.search()
                .forResource(org.hl7.fhir.r5.model.Encounter.class)
                .where(org.hl7.fhir.r5.model.Encounter.IDENTIFIER.exactly().code(encounterModel.getId()))
                .returnBundle(Bundle.class)
                .execute();
        List<MethodOutcome> outcome = new ArrayList<>();
        try{if (true) {
            MethodOutcome outcomeTemp = client.create()
                    .resource(fhirPatient)
                    .prettyPrint()
                    .encodedJson()
                    .execute();
            outcome.add(outcomeTemp);

        }
        if (true) {
            MethodOutcome outcomeTemp;
            outcomeTemp = client.create()
                    .resource(fhirEncounter)
                    .prettyPrint()
                    .encodedJson()
                    .execute();
            outcome.add(outcomeTemp);

            for (AllergyIntolerance allergyTemp : fhirAllergies) {
                outcomeTemp = client.create()
                        .resource(allergyTemp)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                outcome.add(outcomeTemp);
            }
            for (org.hl7.fhir.r5.model.Condition conditionTemp : fhirConditions) {
                outcomeTemp = client.create()
                        .resource(conditionTemp)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                outcome.add(outcomeTemp);
            }
            for (MedicationStatement medicationTemp : fhirMedications) {
                outcomeTemp = client.create()
                        .resource(medicationTemp)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                outcome.add(outcomeTemp);
            }
            for (org.hl7.fhir.r5.model.Procedure procedure : fhirProcedures) {
                outcomeTemp = client.create()
                        .resource(procedure)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                outcome.add(outcomeTemp);
            }
            for (org.hl7.fhir.r5.model.Observation observationTemp : fhirObservations) {
                outcomeTemp = client.create()
                        .resource(observationTemp)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                outcome.add(outcomeTemp);
            }
            for (org.hl7.fhir.r5.model.CarePlan careplanTemp : fhirCareplans) {
                outcomeTemp = client.create()
                        .resource(careplanTemp)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                outcome.add(outcomeTemp);
            }
            for (org.hl7.fhir.r5.model.Immunization immunizationTemp : fhirImmunizations) {
                outcomeTemp = client.create()
                        .resource(immunizationTemp)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                outcome.add(outcomeTemp);
            }
        
        
        }}catch (Exception e) {

            e.printStackTrace();
            System.err.println(e.getLocalizedMessage());
            System.err.println(e);
        }
            
        if (!outcome.isEmpty()) {
            for (MethodOutcome methodOutcome : outcome) {
                if (methodOutcome.getOperationOutcome() != null) {
                    System.out.println("Operation Outcome: " + methodOutcome.getOperationOutcome());
                } else {
                    System.out.println("Created Resource ID: " + methodOutcome.getId());
                }
            }
            return true;
        }
        return false;
       

    }
}