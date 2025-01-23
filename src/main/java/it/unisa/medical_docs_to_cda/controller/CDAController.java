package it.unisa.medical_docs_to_cda.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import it.unisa.medical_docs_to_cda.CDALDO.*;
import it.unisa.medical_docs_to_cda.model.*;
import it.unisa.medical_docs_to_cda.repositories.AllergyRepository;
import it.unisa.medical_docs_to_cda.repositories.CareplanRepository;
import it.unisa.medical_docs_to_cda.repositories.ConditionRepository;
import it.unisa.medical_docs_to_cda.repositories.EncounterRepository;
import it.unisa.medical_docs_to_cda.repositories.ImagingStudyRepository;
import it.unisa.medical_docs_to_cda.repositories.ImmunizationRepository;
import it.unisa.medical_docs_to_cda.repositories.MedicationRepository;
import it.unisa.medical_docs_to_cda.repositories.ObservationRepository;
import it.unisa.medical_docs_to_cda.repositories.OrganizationRepository;
import it.unisa.medical_docs_to_cda.repositories.PatientRepository;
import it.unisa.medical_docs_to_cda.repositories.ProcedureRepository;
import it.unisa.medical_docs_to_cda.repositories.ProviderRepository;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * This class is responsible for converting Encounter objects to CDA documents.
 */
public class CDAController {
    @Autowired
    private static PatientRepository patientRepo;
    @Autowired
    private static EncounterRepository encounterRepo;
    @Autowired
    private static AllergyRepository allergyRepo;
    @Autowired
    private static CareplanRepository careplanRepo;
    @Autowired
    private static MedicationRepository medicationRepo;
    @Autowired
    private static ObservationRepository observationRepo;
    @Autowired
    private static ProcedureRepository procedureRepo;
    @Autowired
    private static ConditionRepository conditionRepo;
    @Autowired
    private static ImagingStudyRepository imagingStudyRepo;
    @Autowired
    private static ImmunizationRepository immunizationRepo;
    @Autowired
    private static ProviderRepository providerRepo;
    @Autowired
    private static OrganizationRepository organizationRepo;

    /**
     * Converts an Encounter object to a CDA document.
     * 
     * @param encounter the Encounter object to convert
     * @return the CDA document, or null if the input is null
     */
    public static CDALDO EncounterToCDA(Encounter encounter) {
        if (encounter == null) {
            return null;
        }
        Patient patient = patientRepo.findById(encounter.getPatientId()).get();
        Provider provider = providerRepo.findById(encounter.getProviderId()).get();
        Organization organization = organizationRepo.findById(encounter.getOrganizationId()).get();
        String encounterId = encounter.getId();
        List<Observation> observation = observationRepo.findByEncounterId(encounterId);
        List<Allergy> allergies = allergyRepo.findByEncounterId(encounterId);
        List<Condition> conditions = conditionRepo.findByEncounterId(encounterId);
        List<Careplan> careplans = careplanRepo.findByEncounterId(encounterId);
        List<ImagingStudy> imagingStudies = imagingStudyRepo.findByEncounterId(encounterId);
        List<Immunization> immunizations = immunizationRepo.findByEncounterId(encounterId);
        List<Medication> medications = medicationRepo.findByEncounterId(encounterId);
        List<Procedure> procedures = procedureRepo.findByEncounterId(encounterId);
        CDALDO cdaldoBuilder = new CDALDO();
        // we are using ssn as italian CF and random genereted ANA code
        try {
            CDALDOPatient cdaldoPatient = new CDALDOPatient(
                    List.of(new CDALDOId("2.16.840.1.113883.2.9.4.3.2", patient.getSSN(),
                            "Ministero Economia e Finanze"),
                            new CDALDOId("2.16.840.1.113883.2.9.4.3.15", generateCodiceANA(), "Campania")),
                    2,
                    List.of(createCDALDOAddr(patient.getAddress(), patient.getCity(), patient.getState(),
                            patient.getCounty(), patient.getZip())),
                    null, null, patient.getFirst(), patient.getLast(), patient.getGender(), patient.getBirthPlace(),
                    patient.getBirthDate());
            cdaldoBuilder.setPatient(cdaldoPatient);
        } catch (Exception e) {
            CDALDOPatient cdaldoPatient = null;
        }

        // we are taking all the organization like they are part of the ASL NAPOLI 1
        CDALDOId oid = new CDALDOId("2.16.840.1.113883.2.9.4.3.1", DocumentIdGenerator.generateDocumentId("DOC"),
                "ASL NAPOLI 1");
        CDALDOId setOid = oid;
        String versionNumberValue = "1.0";
        String confidentialityCodeValue = "V";
        cdaldoBuilder.setOid(oid);
        cdaldoBuilder.setSetId(setOid);
        cdaldoBuilder.setVersion(versionNumberValue);
        cdaldoBuilder.setConfidentialityCode(confidentialityCodeValue);
        CDALDOAuthor author =null;
        try {
             author = new CDALDOAuthor();
            CDALDOId authorId = new CDALDOId();

            author.setTelecoms(List.of("tel:+391389218301289", "mailto:ciao@prova.it", "mailto:ciao@pec.it"));
            author.setTelecomUses(List.of("H", "H", "H"));
            authorId.setOid("2.16.840.1.113883.2.9.4.3.2");
            authorId.setRoot("AUTH123456789");

            author.setId(authorId);
            author.setFirstName("Jane");
            author.setLastName("Smith");

            cdaldoBuilder.setAuthor(author);
        } catch (Exception e) {
            author = null;
        }
        CDALDOId custodianId = new CDALDOId("2.16.840.1.113883.2.9.4.1.2","MGH-20250123-001","Massachusetts General Hospital");

        CDALDOAddr custodianAddress = new CDALDOAddr("H", "US",organization.getState(), null, organization.getCity(), null, organization.getZip(),  organization.getAddress());

        String custodianOrgName = organization.getName();
        String custodianPhone = "tel:"+organization.getPhone();

        String ramoAziendale = "MA.HOSPITAL.NOSOLOGICI";
        String numeroNosologico = "123456";
        String nomeAzienda = "Massachusetts General Hospital";
       
        List<String> repartoIds = List.of("123456");
        List<String> repartoNames = List.of("Reparto di Ostetricia e Ginecologia");
        List<String> ministerialCodes = List.of("Code1");
        List<String> facilityNames = List.of("Reparto di Ostetricia e Ginecologia");
        List<String> facilityTelecoms = List.of("tel:"+organization.getPhone());

        try {
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
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        try {
            cdaldoBuilder.setCustodian(custodianId, custodianOrgName, custodianAddress, custodianPhone);
        } catch (Exception e) {
           
            e.printStackTrace();
        }
        try {
            cdaldoBuilder.setRapresentedOrganization(
                    new CDALDOId("2.16.840.1.113883.2.9.4.1.1", "13289038091283", "ministero della pastina"));
        } catch (Exception e) {
            
            e.printStackTrace();
        }

        try {
            cdaldoBuilder.setCompiler(author);
        } catch (Exception e) {
            
            e.printStackTrace();
        }

        try {
            cdaldoBuilder.setLegalAuthenticator(author);
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        try {
            cdaldoBuilder.setFullfillmentId("RX-20250123-001");
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        
                return cdaldoBuilder;
    }

    private static String generateCodiceANA() {

        String prefisso = "CAM";

        Random random = new Random();

        StringBuilder codiceNumerico = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            int cifra = random.nextInt(10);
            codiceNumerico.append(cifra);
        }

        return prefisso + codiceNumerico.toString();
    }

    public static CDALDOAddr createCDALDOAddr(String address, String city, String state, String county, String zip) {
        // Puoi personalizzare il "use" (ad esempio, "H", "T", etc.)
        String use = "H";

        return new CDALDOAddr(use, "US", state, county, city, null, zip, address);
    }

}
