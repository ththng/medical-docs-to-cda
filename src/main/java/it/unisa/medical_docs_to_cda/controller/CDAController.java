package it.unisa.medical_docs_to_cda.controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import it.unisa.medical_docs_to_cda.CDALDO.*;
import it.unisa.medical_docs_to_cda.codes.finder.CodeSearchManager;
import it.unisa.medical_docs_to_cda.model.*;
import it.unisa.medical_docs_to_cda.repositories.*;

/**
 * This class is responsible for converting Encounter objects to CDA documents.
 */
@Controller
public class CDAController {
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
        @Autowired
        private ProviderRepository providerRepo;
        @Autowired
        private OrganizationRepository organizationRepo;

        /**
         * Converts an Encounter object to a CDA document.
         * 
         * @param encounter the Encounter object to convert
         * @return the CDA document, or null if the input is null
         */
        public CDALDO EncounterToCDA(Encounter encounter) {
                if (encounter == null) {
                        return null;
                }
                Patient patient = patientRepo.findById(encounter.getPatientId()).get();
                Provider provider = providerRepo.findById(encounter.getProviderId()).get();
                Organization organization = organizationRepo.findById(encounter.getOrganizationId()).get();
                String encounterId = encounter.getId();
                List<Observation> observations = observationRepo.findByEncounterId(encounterId);
                List<Allergy> allergies = allergyRepo.findByEncounterId(encounterId);
                List<Condition> conditions = conditionRepo.findByEncounterId(encounterId);
                List<Careplan> careplans = careplanRepo.findByEncounterId(encounterId);
                List<ImagingStudy> imagingStudies = imagingStudyRepo.findByEncounterId(encounterId);
                List<Immunization> immunizations = immunizationRepo.findByEncounterId(encounterId);
                List<Medication> medications = medicationRepo.findByEncounterId(encounterId);
                List<Procedure> procedures = procedureRepo.findByEncounterId(encounterId);
                List<Procedure> pastProcedures = procedureRepo.findByPatientId(patient.getId());
                CDALDO cdaldoBuilder = new CDALDO();
                // we are using ssn as italian CF and random genereted ANA code
                CDALDOPatient cdaldoPatient;
                cdaldoPatient = new CDALDOPatient(
                                List.of(new CDALDOId("2.16.840.1.113883.2.9.4.3.2", patient.getSSN(),
                                                "Ministero Economia e Finanze"),
                                                new CDALDOId("2.16.840.1.113883.2.9.4.3.17",
                                                                generateCodiceANA(), "Campania")),
                                2,
                                List.of(createCDALDOAddr(patient.getAddress(), patient.getCity(),
                                                patient.getState(),
                                                patient.getCounty(), patient.getZip())),
                                null, null, patient.getFirst(), patient.getLast(), patient.getGender(),
                                patient.getBirthPlace(),
                                patient.getBirthDate());
                cdaldoBuilder.setPatient(cdaldoPatient);

                // we are taking all the organization like they are part of the ASL NAPOLI 1
                CDALDOId oid = new CDALDOId("2.16.840.1.113883.2.9.4.3.1",
                                DocumentIdGenerator.generateDocumentId("DOC"),
                                "ASL NAPOLI 1");
                CDALDOId setOid = oid;
                String versionNumberValue = "1.0";
                String confidentialityCodeValue = "V";
                cdaldoBuilder.setOid(oid);
                cdaldoBuilder.setStatus("complete");
                cdaldoBuilder.setEffectiveTimeDate(LocalDateTime.now());
                cdaldoBuilder.setSetId(setOid);
                cdaldoBuilder.setVersion(versionNumberValue);
                cdaldoBuilder.setConfidentialityCode(confidentialityCodeValue);
                CDALDOAuthor author;

                author = new CDALDOAuthor();
                CDALDOId authorId = new CDALDOId();

                author.setTelecoms(
                                List.of("tel:+391389218301289", "mailto:ciao@prova.it", "mailto:ciao@pec.it"));
                author.setTelecomUses(List.of("H", "H", "H"));
                authorId.setOid("2.16.840.1.113883.2.9.4.3.2");
                authorId.setRoot("AUTH123456789");

                author.setId(authorId);
                author.setFirstName("Jane");
                author.setLastName("Smith");

                cdaldoBuilder.setAuthor(author);

                CDALDOId custodianId = new CDALDOId("2.16.840.1.113883.2.9.4.1.2", "MGH-20250123-001",
                                "Massachusetts General Hospital");

                CDALDOAddr custodianAddress = new CDALDOAddr("H", "US", organization.getState(), null,
                                organization.getCity(),
                                null, organization.getZip(), organization.getAddress());

                String custodianOrgName = organization.getName();
                String custodianPhone = "tel:" + organization.getPhone();

                String ramoAziendale = "MA.HOSPITAL.NOSOLOGICI";
                String numeroNosologico = "123456";
                String nomeAzienda = "Massachusetts General Hospital";

                List<String> repartoIds = List.of("123456");
                List<String> repartoNames = List.of("Reparto di Ostetricia e Ginecologia");
                List<String> ministerialCodes = List.of("Code1");
                List<String> facilityNames = List.of("Reparto di Ostetricia e Ginecologia");
                List<String> facilityTelecoms = List.of("tel:" + organization.getPhone());

                cdaldoBuilder.setComponentOf(
                                ramoAziendale,
                                numeroNosologico,
                                nomeAzienda,
                                encounter.getStart(),
                                encounter.getStop(),
                                repartoIds,
                                repartoNames,
                                ministerialCodes,
                                facilityNames,
                                facilityTelecoms);

                cdaldoBuilder.setCustodian(custodianId, custodianOrgName, custodianAddress, custodianPhone);

                cdaldoBuilder.setRapresentedOrganization(
                                new CDALDOId("2.16.840.1.113883.2.9.4.1.1", "13289038091283",
                                                "ministero della pastina"));

                cdaldoBuilder.setCompiler(author);

                cdaldoBuilder.setLegalAuthenticator(author);

                cdaldoBuilder.setFullfillmentId("RX-20250123-001");

                // Section 1: Motivo del ricovero
                String content1 = encounter.getReasonDescription() != null ? encounter.getReasonDescription()
                                : encounter.getDescription();

                cdaldoBuilder.setNarrativeBlocks(List.of(new CDALDONarrativeBlock("paragraph", content1)), "1");

                CDALDOEntryObservation observation1 = new CDALDOEntryObservation(
                                "8646-2",
                                "2.16.840.1.113883.6.1",
                                "LOINC",
                                "Diagnosi di Accettazione Ospedaliera",
                                "entry",
                                null,
                                false,
                                encounter.getStart(),
                                encounter.getStop(),
                                null,
                                null,
                                CodeSearchManager.searchICD9ByTerm(encounter.getReasonDescription()),
                                "2.16.840.1.113883.6.103",
                                "ICD9CM",
                                encounter.getReasonDescription(),
                                "CD",
                                false,
                                null,
                                null);

                cdaldoBuilder.setEntries(List.of(observation1), "1");

                // Section 2: Inquadramento clinico iniziale
                List<CDALDONarrativeBlock> narrativeBlocks2 = new ArrayList<>();

                conditions.forEach(
                                condition -> narrativeBlocks2.add(
                                                new CDALDONarrativeBlock("paragraph", condition.getDescription())));

                // Anamnesi 2.2
                String[] procedureList = pastProcedures.stream().map(Procedure::getDescription).toArray(String[]::new);
                String[] allObservations = observations.stream()
                                .map(observation -> observation.getDescription() + " - " + observation.getValue() + " "
                                                + observation.getUnits())
                                .toArray(String[]::new);
                narrativeBlocks2.add(new CDALDONarrativeBlock("anamnesi", "paragraph", "List of observations: "));
                narrativeBlocks2.add(new CDALDONarrativeBlock("anamnesi", "list", allObservations));
                narrativeBlocks2.add(new CDALDONarrativeBlock("anamnesi", "paragraph", "List of past procedures: "));
                narrativeBlocks2.add(new CDALDONarrativeBlock("anamnesi", "list", procedureList));

                List<CDALDOEntry> observations2 = new ArrayList<>();
                observations.forEach(observation -> observations2.add(new CDALDOEntryObservation("75326-9",
                                "2.16.840.1.113883.6.1",
                                "LOINC", "Problem", "entry", null, true,
                                observation.getDate(), null, null, null, "CD", observation.getCode(),
                                "2.16.840.1.113883.6.1", "LOINC",
                                observation.getDescription(), false, null, null, null)));

                // Terapia farmacologica 2.4
                String[] meds = medications.stream().map(Medication::getDescription).toArray(String[]::new);
                narrativeBlocks2.add(new CDALDONarrativeBlock("terapiaFarmacologica", "list", meds));

                cdaldoBuilder.setNarrativeBlocks(narrativeBlocks2, "2");
                cdaldoBuilder.setEntries(observations2, "2");

                // Section 3: Decorso ospedaliero
                List<CDALDONarrativeBlock> narrativeBlocks3 = new ArrayList<>();
                narrativeBlocks3.add(new CDALDONarrativeBlock(
                                "paragraph",
                                "The patient came to our attention due to " + content1 +
                                                ". During hospitalization, the condition was managed and stabilization was achieved through intensive pharmacological treatment."));

                cdaldoBuilder.setNarrativeBlocks(narrativeBlocks3, "3");
                // cdaldoBuilder.setEntries(null, "3");

                // Section 4: Complicanze (optional, not enough data in dataset)

                // Section 5: Riscontri ed accertamenti significativi
                List<CDALDONarrativeBlock> narrativeBlocks5 = new ArrayList<>();
                String conditionsText = conditions.stream().map(Condition::getDescription)
                                .collect(Collectors.joining(", "));
                String proceduresText = procedures.stream().map(Procedure::getDescription)
                                .collect(Collectors.joining(", "));
                narrativeBlocks5.add(new CDALDONarrativeBlock("paragraph",
                                "On " + encounter.getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                + ", a diagnostic evaluation was performed, which identified "
                                                + conditionsText + " requiring intervention."
                                                + " The following procedures were subsequently recommended and carried out: "
                                                + proceduresText));
                cdaldoBuilder.setNarrativeBlocks(narrativeBlocks5, "5");
                // cdaldoBuilder.setEntries(null, "5");

                // Section 6: Consulenza
                List<CDALDONarrativeBlock> narrativeBlocks6 = new ArrayList<>();
                List<CDALDOEntry> observations6 = new ArrayList<>();
                String[] immuns = immunizations.stream().map(Immunization::getDescription).toArray(String[]::new);
                if (immuns.length > 0) {
                        narrativeBlocks6
                                        .add(new CDALDONarrativeBlock("paragraph",
                                                        "The following vaccinations were administered: "));
                        narrativeBlocks6.add(new CDALDONarrativeBlock("list", immuns));
                        immunizations.forEach(immunization -> observations6.add(new CDALDOEntryObservation(
                                        immunization.getCode(),
                                        "2.16.840.1.113883.12.292", "CVX", immunization.getDescription(), "entry", null,
                                        false,
                                        immunization.getDate(),
                                        null, immunization.getDate(), null, "ST", "done", false, null,
                                        List.of(new CDALDOAuthor(new CDALDOId("2.16.840.1.113883.2.9.4.3.2", "", "MEF"),
                                                        provider.getName().split(" ")[0],
                                                        provider.getName().split(" ")[1])),
                                        null)));
                } else {
                        narrativeBlocks6.add(
                                        new CDALDONarrativeBlock("paragraph", "No vaccinations were administered"));
                }

                cdaldoBuilder.setNarrativeBlocks(narrativeBlocks6, "6");
                cdaldoBuilder.setEntries(observations6, "6");

                // Section 7: Esami eseguiti durante il ricovero
                // imaging studies metto solo come lista

                List<CDALDONarrativeBlock> narrativeBlocks7 = new ArrayList<>();
                String[] imagingSt = imagingStudies.stream()
                                .map(imagingStudy -> imagingStudy.getSopDescription() + " of "
                                                + imagingStudy.getBodySiteDescription())
                                .toArray(String[]::new);
                if (imagingSt.length > 0) {
                        narrativeBlocks7
                                        .add(new CDALDONarrativeBlock("paragraph",
                                                        "The following exams were administered: "));
                        narrativeBlocks7.add(new CDALDONarrativeBlock("list", imagingSt));
                } else {
                        narrativeBlocks7.add(new CDALDONarrativeBlock("paragraph", "No exams were administered"));
                }

                cdaldoBuilder.setNarrativeBlocks(narrativeBlocks7, "7");
                // cdaldoBuilder.setEntries(null, "7");

                // Section 8: Procedure
                List<CDALDONarrativeBlock> narrativeBlocks8 = new ArrayList<>();
                List<CDALDOEntry> observation8 = new ArrayList<>();

                narrativeBlocks8.add(new CDALDONarrativeBlock("paragraph", "On date "
                                + encounter.getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                + " the following procedures were performed: "
                                + proceduresText + ". Postoperative course was regular."));

                procedures.forEach(procedure -> observation8.add(new CDALDOEntryProcedure(procedure.getCode(),
                                "2.16.840.1.113883.6.96", "SNOMED CT", procedure.getDescription(),
                                "entry", null, procedure.getDate().atTime(LocalTime.now()), null, null,
                                List.of(new CDALDOEntryObservation(
                                                CodeSearchManager.searchICD9ByTerm(procedure.getReasonDescription()),
                                                "2.16.840.1.113883.6.103", "ICD-9CM (diagnosis codes)",
                                                procedure.getReasonDescription(),
                                                "entryRelationship", "RSON", false, null,
                                                null, null, null,
                                                null, 0.0f, null, false, null)))));

                cdaldoBuilder.setNarrativeBlocks(narrativeBlocks8, "8");
                cdaldoBuilder.setEntries(observation8, "8");

                // Section 9: Allergie
                List<CDALDONarrativeBlock> narrativeBlocks9 = new ArrayList<>();
                List<CDALDOEntry> observation9 = new ArrayList<>();

                String[] allergens = allergies.stream()
                                .map(Allergy::getDescription)
                                .toArray(String[]::new);
                if (allergens.length > 0) {
                        narrativeBlocks9.add(new CDALDONarrativeBlock("list", allergens));
                } else {
                        narrativeBlocks9.add(new CDALDONarrativeBlock("paragraph", "No allergies were found"));
                }

                allergies.forEach(allergy -> observation9.add(new CDALDOEntryAct("entry", "completed",
                                allergy.getStart(),
                                allergy.getStop(), "ALG", "Allergy",
                                allergy.getStart().atTime(LocalTime.now()), allergy.getStop().atTime(LocalTime.now()),
                                null,
                                List.of(new CDALDOAgent(allergy.getCode(), "2.16.840.1.113883.6.96", "SNOMED CT",
                                                allergy.getDescription())))));
                cdaldoBuilder.setNarrativeBlocks(narrativeBlocks9, "9");
                cdaldoBuilder.setEntries(observation9, "9");

                // Section 10: farmaci durante il ricovero
                List<CDALDONarrativeBlock> narrativeBlocks10 = new ArrayList<>();
                List<CDALDOEntry> observation10 = new ArrayList<>();

                if (meds.length > 0) {
                        narrativeBlocks10.add(new CDALDONarrativeBlock("list", meds));
                } else {
                        narrativeBlocks10.add(new CDALDONarrativeBlock("paragraph", "No medications were found"));
                }
                medications.forEach(medication -> observation10.add(new CDALDOEntrySubstanceAdm("entry",
                                medication.getCode(),
                                medication.getDescription(), "completed", medication.getStart().toLocalDate(), null)));

                cdaldoBuilder.setNarrativeBlocks(narrativeBlocks10, "10");
                cdaldoBuilder.setEntries(observation10, "10");

                // Section 11: condizione paziente alla dimissione
                List<CDALDONarrativeBlock> narrativeBlocks11 = new ArrayList<>();
                List<CDALDOEntry> observation11 = new ArrayList<>();

                narrativeBlocks11.add(new CDALDONarrativeBlock("paragraph",
                                "The patient conditions at discharge are: "
                                                + conditionsText + " requiring intervention."
                                                + " The following diagnosis was made: "
                                                + content1));

                conditions.forEach(condition -> observation11.add(new CDALDOEntryObservation("8651-2”",
                                "“2.16.840.1.113883.6.1” ", "LOINC", "Diagnosi di dimissione ospedaliera",
                                "entry", null, false,
                                condition.getStart().atTime(LocalTime.now()),
                                condition.getStop().atTime(LocalTime.now()),
                                null, null, "CD", CodeSearchManager.searchICD9ByTerm(condition.getDescription()),
                                "2.16.840.1.113883.6.103", "ICD9CM", condition.getDescription(), false, null, null,
                                null)));

                cdaldoBuilder.setNarrativeBlocks(narrativeBlocks11, "11");
                cdaldoBuilder.setEntries(observation11, "11");

                // Section 12 (Optional, not enough data in dataset)

                // Section 13 : Istruzioni di follow up
                List<CDALDONarrativeBlock> narrativeBlocks13 = new ArrayList<>();

                if (careplans != null && !careplans.isEmpty()) {
                        String[] followup = careplans.stream().map(Careplan::getDescription).toArray(String[]::new);
                        if (followup.length > 0) {
                                narrativeBlocks13.add(new CDALDONarrativeBlock("paragraph",
                                                "The following instructions/therapies are recommended for the patient: "));
                                narrativeBlocks13.add(new CDALDONarrativeBlock("list", followup));
                        }
                        cdaldoBuilder.setNarrativeBlocks(narrativeBlocks13, "13");
                }

                // cdaldoBuilder.setEntries(null, "13");

                return cdaldoBuilder;
        }

        private String generateCodiceANA() {

                String prefisso = "CAM";

                Random random = new Random();

                StringBuilder codiceNumerico = new StringBuilder();
                for (int i = 0; i < 12; i++) {
                        int cifra = random.nextInt(10);
                        codiceNumerico.append(cifra);
                }

                return prefisso + codiceNumerico.toString();
        }

        public CDALDOAddr createCDALDOAddr(String address, String city, String state, String county, String zip) {
                // Puoi personalizzare il "use" (ad esempio, "H", "T", etc.)
                String use = "H";

                return new CDALDOAddr(use, "US", state, county, city, null, zip, address);
        }

}
