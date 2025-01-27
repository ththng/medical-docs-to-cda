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
 * Controller class responsible for converting Encounter objects into CDA
 * documents.
 * Utilizes various repositories to retrieve necessary data for the conversion
 * process.
 * Autowires multiple repositories to access patient, allergy, careplan,
 * medication,
 * observation, procedure, condition, imaging study, immunization, provider, and
 * organization data.
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

                setPatient(cdaldoBuilder, patient);
                setId(cdaldoBuilder);
                CDALDOAuthor author = setAuthor(cdaldoBuilder);

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

                String reasonCode = encounter.getReasonCode() != null ? encounter.getReasonCode() : "63320";

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
                                reasonCode,
                                "2.16.840.1.113883.6.103",
                                "ICD9CM",
                                content1,
                                "CD",
                                false,
                                null,
                                null);

                cdaldoBuilder.setEntries(List.of(observation1), "1");

                // Section 2: Inquadramento clinico iniziale
                List<CDALDONarrativeBlock> narrativeBlocks2 = new ArrayList<>();
                if (conditions != null && !conditions.isEmpty()) {
                        conditions.forEach(condition -> narrativeBlocks2.add(
                                        new CDALDONarrativeBlock("paragraph", condition.getDescription())));
                }
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
                if (observations != null && !observations.isEmpty()) {
                        observations.forEach(observation -> observations2.add(new CDALDOEntryObservation("75326-9",
                                        "2.16.840.1.113883.6.1",
                                        "LOINC", "Problem", "entry", null, true,
                                        observation.getDate(), null, null, null, "CD", observation.getCode(),
                                        "2.16.840.1.113883.6.1", "LOINC",
                                        observation.getDescription(), false, null, null, null)));
                }
                // Terapia farmacologica 2.4
                String[] meds = medications.stream().map(Medication::getDescription).toArray(String[]::new);
                if (meds.length > 0)
                        narrativeBlocks2.add(new CDALDONarrativeBlock("terapiaFarmacologica", "list", meds));
                if (!narrativeBlocks2.isEmpty())
                        cdaldoBuilder.setNarrativeBlocks(narrativeBlocks2, "2");
                if (!observations2.isEmpty())
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
                @SuppressWarnings("null")
                String conditionsText = conditions.stream().map(Condition::getDescription)
                                .collect(Collectors.joining(", "));
                String proceduresText = procedures.stream().map(Procedure::getDescription)
                                .collect(Collectors.joining(", "));
                if (conditionsText != null && proceduresText != null && !conditionsText.isEmpty()
                                && !proceduresText.isEmpty()) {
                        String date = encounter.getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        String narrative = String.format(
                                        "On %s, a diagnostic evaluation was performed, which identified %s requiring intervention. The following procedures were subsequently recommended and carried out: %s",
                                        date, conditionsText, proceduresText);
                        narrativeBlocks5.add(new CDALDONarrativeBlock("paragraph", narrative));
                        narrativeBlocks5.add(new CDALDONarrativeBlock("list", proceduresText.split(", ")));
                }
                if (!narrativeBlocks5.isEmpty())
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
                }
                if (!narrativeBlocks6.isEmpty())
                        cdaldoBuilder.setNarrativeBlocks(narrativeBlocks6, "6");
                if (!observations6.isEmpty())
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
                }
                if (!narrativeBlocks7.isEmpty())
                        cdaldoBuilder.setNarrativeBlocks(narrativeBlocks7, "7");
                // cdaldoBuilder.setEntries(null, "7");

                // Section 8: Procedure
                List<CDALDONarrativeBlock> narrativeBlocks8 = new ArrayList<>();
                List<CDALDOEntry> observation8 = new ArrayList<>();

                if (proceduresText != null && !proceduresText.isEmpty()) {
                        narrativeBlocks8.add(new CDALDONarrativeBlock("paragraph", "On date "
                                        + encounter.getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                        + " the following procedures were performed: "
                                        + proceduresText + ". Postoperative course was regular."));
                }
                if (procedures != null && !procedures.isEmpty()) {
                        procedures.forEach(procedure -> observation8.add(new CDALDOEntryProcedure(procedure.getCode(),
                                        "2.16.840.1.113883.6.96", "SNOMED CT", procedure.getDescription(),
                                        "entry", null, procedure.getDate().atTime(LocalTime.now()), null, null,
                                        List.of(new CDALDOEntryObservation(procedure.getCode()
                                        /*
                                         * CodeSearchManager.searchICD9ByTerm(
                                         * procedure.getReasonDescription())
                                         */,
                                                        "2.16.840.1.113883.6.103", "ICD-9CM (diagnosis codes)",
                                                        procedure.getReasonDescription(),
                                                        "entryRelationship", "RSON", false, null,
                                                        null, null, null,
                                                        null, 0.0f, null, false, null)))));
                }

                if (!narrativeBlocks8.isEmpty())
                        cdaldoBuilder.setNarrativeBlocks(narrativeBlocks8, "8");
                if (!observation8.isEmpty())
                        cdaldoBuilder.setEntries(observation8, "8");

                // Section 9: Allergie
                List<CDALDONarrativeBlock> narrativeBlocks9 = new ArrayList<>();
                List<CDALDOEntry> observation9 = new ArrayList<>();

                String[] allergens = allergies.stream()
                                .map(Allergy::getDescription)
                                .toArray(String[]::new);
                if (allergens.length > 0) {
                        narrativeBlocks9.add(new CDALDONarrativeBlock("list", allergens));
                }
                if (allergies != null && !allergies.isEmpty()) {
                        allergies.forEach(allergy -> observation9.add(new CDALDOEntryAct("entry", "completed",
                                        allergy.getStart(),
                                        allergy.getStop(), "ALG", "Allergy",
                                        allergy.getStart().atTime(LocalTime.now()),
                                        allergy.getStop().atTime(LocalTime.now()),
                                        null,
                                        List.of(new CDALDOAgent(allergy.getCode(), "2.16.840.1.113883.6.96",
                                                        "SNOMED CT",
                                                        allergy.getDescription())))));
                }
                if (!narrativeBlocks9.isEmpty())
                        cdaldoBuilder.setNarrativeBlocks(narrativeBlocks9, "9");
                if (!observation9.isEmpty())
                        cdaldoBuilder.setEntries(observation9, "9");

                // Section 10: farmaci durante il ricovero
                List<CDALDONarrativeBlock> narrativeBlocks10 = new ArrayList<>();
                List<CDALDOEntry> observation10 = new ArrayList<>();

                if (meds.length > 0) {
                        narrativeBlocks10.add(new CDALDONarrativeBlock("list", meds));
                }

                if (medications != null && !medications.isEmpty())
                        medications.forEach(medication -> observation10.add(new CDALDOEntrySubstanceAdm("entry",
                                        medication.getCode(),
                                        medication.getDescription(), "completed", medication.getStart().toLocalDate(),
                                        null)));

                if (!narrativeBlocks10.isEmpty())
                        cdaldoBuilder.setNarrativeBlocks(narrativeBlocks10, "10");
                if (!observation10.isEmpty())
                        cdaldoBuilder.setEntries(observation10, "10");

                // Section 11: condizione paziente alla dimissione
                List<CDALDONarrativeBlock> narrativeBlocks11 = new ArrayList<>();
                List<CDALDOEntry> observation11 = new ArrayList<>();

                narrativeBlocks11.add(new CDALDONarrativeBlock("paragraph",
                                "The patient conditions at discharge are: "
                                                + conditionsText + " requiring intervention."
                                                + " The following diagnosis was made: "
                                                + content1));
                if (conditions != null && !conditions.isEmpty()) {
                        conditions.forEach(condition -> {
                                LocalDateTime startDate = condition.getStart() != null
                                                ? condition.getStart().atTime(LocalTime.now())
                                                : null;
                                LocalDateTime stopDate = condition.getStop() != null
                                                ? condition.getStop().atTime(LocalTime.now())
                                                : null;

                                observation11.add(new CDALDOEntryObservation(
                                                "8651-2",
                                                "“2.16.840.1.113883.6.1” ",
                                                "LOINC",
                                                "Diagnosi di dimissione ospedaliera",
                                                "entry",
                                                null,
                                                false,
                                                startDate,
                                                stopDate,
                                                null,
                                                null,
                                                "CD",
                                                CodeSearchManager.searchICD9ByTerm(condition.getDescription()),
                                                "2.16.840.1.113883.6.103",
                                                "ICD9CM",
                                                condition.getDescription(),
                                                false,
                                                null,
                                                null,
                                                null));
                        });
                }

                cdaldoBuilder.setNarrativeBlocks(narrativeBlocks11, "11");
                if (!observation11.isEmpty())
                        cdaldoBuilder.setEntries(observation11, "11");

                // Section 12 (Optional section, omitted due to not enough data in dataset)

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

                // cdaldoBuilder.setEntries(null, "13"); --> No entries for this section

                return cdaldoBuilder;
        }

        /**
         * 
         * The `setPatient` method initializes a CDALDOPatient object with the patient's
         * details, including SSN, a randomly generated ANA code, and address
         * information.
         * 
         */

        private void setPatient(CDALDO cdaldoBuilder, Patient patient) {
                // we are using ssn as italian CF and random genereted ANA code

                CDALDOPatient cdaldoPatient = new CDALDOPatient(
                                List.of(
                                                new CDALDOId("2.16.840.1.113883.2.9.4.3.2", patient.getSSN(),
                                                                "Ministero Economia e Finanze"),
                                                new CDALDOId("2.16.840.1.113883.2.9.4.3.17", generateCodiceANA(),
                                                                "Campania")),
                                2,
                                List.of(createCDALDOAddr(patient.getAddress(), patient.getCity(), patient.getState(),
                                                patient.getCounty(), patient.getZip())),
                                null, null, patient.getFirst(), patient.getLast(), patient.getGender(),
                                patient.getBirthPlace(), patient.getBirthDate());
                cdaldoBuilder.setPatient(cdaldoPatient);
        }
        /*
         * The `setId` method assigns a unique document ID, status, effective time, set
         * ID,
         * version number, and confidentiality code to the CDALDO object, assuming all
         * organizations are part of ASL NAPOLI 1.
         */

        private void setId(CDALDO cdaldoBuilder) {
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
        }

        /**
         * <p>
         * The `setAuthor` method creates and assigns an author to the CDALDO object,
         * including telecom information and author identification.
         * 
         * @param cdaldoBuilder The CDALDO object to be configured.
         * @param patient       The patient whose information is to be set in the CDALDO
         *                      object.
         * @return The configured CDALDOAuthor object.
         */
        private CDALDOAuthor setAuthor(CDALDO cdaldoBuilder) {
                CDALDOAuthor author = new CDALDOAuthor();
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
                return author;
        }

        /**
         * Generates a unique codice ANA by concatenating a fixed prefix "CAM"
         * with a randomly generated 12-digit numeric string.
         *
         * @return A string representing the generated codice ANA.
         */
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

        /**
         * Creates a new instance of CDALDOAddr with the specified address details.
         *
         * @param address the street address
         * @param city    the city name
         * @param state   the state name
         * @param county  the county name
         * @param zip     the ZIP code
         * @return a CDALDOAddr object initialized with the provided address details
         */

        public CDALDOAddr createCDALDOAddr(String address, String city, String state, String county, String zip) {
                String use = "H";

                return new CDALDOAddr(use, "US", state, county, city, null, zip, address);
        }

}
