package it.unisa.medical_docs_to_cda.CDALDO;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

public class CDALDOTest {


    // Setting valid patient data with required IDs (CF, ENI, ANA) and complete personal information
    @Test
    public void test_set_valid_patient_data() {
        CDALDO cdaldo = new CDALDO();
        CDALDOPatient patient = new CDALDOPatient();
        List<CDALDOId> ids = new ArrayList<>();

        CDALDOId cfId = new CDALDOId();
        cfId.setOid("2.16.840.1.113883.2.9.4.3.2");
        cfId.setExtensionId("ABCDEF12G34H567I");

        CDALDOId eniId = new CDALDOId(); 
        eniId.setOid("2.16.840.1.113883.2.9.4.3.18");
        eniId.setExtensionId("ENI123456789");

        CDALDOId anaId = new CDALDOId();
        anaId.setOid("2.16.840.1.113883.2.9.4.3.17");
        anaId.setExtensionId("ANA987654321");

        ids.add(cfId);
        ids.add(eniId);
        ids.add(anaId);

        patient.setIds(ids);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        CDALDOAddr addr = new CDALDOAddr("H", "IT", "CAMPANIA", "SA", "FISCIANO", "FISCIANO", "84084","via università");
        patient.setAddresses(List.of(addr));
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setBirthPlace("Rome");
        patient.setGender("M");
        patient.setTelecoms(List.of("tel: +22414012948129"),List.of("H"));

        assertDoesNotThrow(() -> cdaldo.setPatient(patient));
    }

    // Setting patient without required ID types (CF/ENI and ANA)
    @Test
    public void test_set_patient_missing_required_ids() {
        CDALDO cdaldo = new CDALDO();
        CDALDOPatient patient = new CDALDOPatient();
        List<CDALDOId> ids = new ArrayList<>();

        CDALDOId otherId = new CDALDOId();
        otherId.setOid("1.2.3.4.5");
        otherId.setRoot("OTHER123");
        ids.add(otherId);

        patient.setIds(ids);
        patient.setFirstName("John");
        patient.setLastName("Doe"); 
        CDALDOAddr addr = new CDALDOAddr("H", "IT", "CAMPANIA", "SA", "FISCIANO", "FISCIANO", "84084","via università");
        patient.setAddresses(List.of(addr));
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setBirthPlace("Rome");
        patient.setGender("M");
        patient.setTelecoms(List.of("tel: +22414012948129"),List.of("H"));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> cdaldo.setPatient(patient)
        );
        assertEquals("atleast one id needs to be a Codice Fiscale or an ENI code", exception.getMessage());
    }

    // Setting author with valid CF, name and telecom information
    @Test
    public void test_set_valid_author_data() {
        CDALDO cdaldo = new CDALDO();
        CDALDOAuthor author = new CDALDOAuthor();
        CDALDOId authorId = new CDALDOId();
    
        authorId.setOid("2.16.840.1.113883.2.9.4.3.2");
        authorId.setRoot("AUTH123456789");
    
        author.setId(authorId);
        author.setFirstName("Jane");
        author.setLastName("Smith");
        List<String> telecoms = new ArrayList<>();
        telecoms.add("123456789");
        author.setTelecoms(telecoms);
    
        assertDoesNotThrow(() -> cdaldo.setAuthor(author));
    }

    // Setting adult guardian with valid CF and complete personal information for minor patients
    @Test
    public void test_set_valid_guardian_for_minor_patient() {
        CDALDO cdaldo = new CDALDO();
        CDALDOPatient guardian = new CDALDOPatient();
        List<CDALDOId> guardianIds = new ArrayList<>();

        CDALDOId guardianCfId = new CDALDOId();
        guardianCfId.setOid("2.16.840.1.113883.2.9.4.3.2");
        guardianCfId.setExtensionId("GHIJKL34M56N789O");

        guardianIds.add(guardianCfId);

        guardian.setIds(guardianIds);
        guardian.setFirstName("Jane");
        guardian.setLastName("Smith");
        guardian.setBirthDate(LocalDate.of(1980, 5, 15));
        guardian.setBirthPlace("Milan");
        guardian.setGender("F");

        assertDoesNotThrow(() -> cdaldo.setGuardian(guardian));
    }

    // Setting custodian with valid organization details and correct ID type (FLS11/HSP11)
    @Test
    public void test_set_valid_custodian_data() {
        CDALDO cdaldo = new CDALDO();
        CDALDOId custodianId = new CDALDOId();
        custodianId.setOid("2.16.840.1.113883.2.9.4.1.1");
        custodianId.setExtensionId("FLS1100000001");

        CDALDOAddr custodianAddress = new CDALDOAddr();
        custodianAddress.setStreet("123 Main St");
        custodianAddress.setCity("Naples");
        custodianAddress.setPostalCode("80100");

        String custodianOrgName = "Health Organization";
        String custodianPhone = "+390812345678";

        assertDoesNotThrow(() -> cdaldo.setCustodian(custodianId, custodianOrgName, custodianAddress, custodianPhone));
    }

    // Setting component details with valid facility and department information
    @Test
    public void test_set_component_of_with_valid_data() {
        CDALDO cdaldo = new CDALDO();
        String ramoAziendale = "Ramo Aziendale";
        String numeroNosologico = "123456";
        String nomeAzienda = "Nome Azienda";
        LocalDateTime lowTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime highTime = LocalDateTime.of(2023, 12, 31, 23, 59);
        List<String> repartoIds = List.of("Reparto1", "Reparto2");
        List<String> repartoNames = List.of("Reparto Name 1", "Reparto Name 2");
        List<String> ministerialCodes = List.of("Code1", "Code2");
        List<String> facilityNames = List.of("Facility1", "Facility2");
        List<String> facilityTelecoms = List.of("Telecom1", "Telecom2");

        assertDoesNotThrow(() -> cdaldo.setComponentOf(
            ramoAziendale,
            numeroNosologico,
            nomeAzienda,
            lowTime,
            highTime,
            repartoIds,
            repartoNames,
            ministerialCodes,
            facilityNames,
            facilityTelecoms
        ));
    }

    // Check method returns list of all missing required fields
    @Test
    public void test_check_method_missing_fields() {
        CDALDO cdaldo = new CDALDO();
        List<String> missingFields = cdaldo.check();
    
        List<String> expectedMissingFields = List.of(
            "oid", "status", "effectiveTimeDate", "setOid", "versionNumberValue",
            "patient", "author", "authorTime", "rapresentedOrganization", "compiler",
            "compilerTime", "custodianId", "custodianOrgazationName", 
            "custodianOrganizationAddress", "custodianPhoneNumber", 
            "legalAuthTime", "legalAuthenticator", "fulfillmentId", "ramoAziendale", 
            "numeroNosologico", "nomeAzienda", "lowTime", "highTime",
            "repartoIds", "repartoNames", "ministerialCodes", 
            "facilityNames", "facilityTelecoms"
        );
    
        assertEquals(expectedMissingFields, missingFields);
    }

// Returns valid Document when all required fields are properly set
@Test
public void test_returns_valid_document_with_required_fields() throws ParserConfigurationException {
    CDALDO cdaldo = new CDALDO();
    CDALDOId id=new CDALDOId("2.16.840.1.113883.2.9.2.99.4.4","129380193812","ministero della provola");
    cdaldo.setOid(id);
    cdaldo.setStatus("completed");
    cdaldo.setEffectiveTimeDate(LocalDateTime.now());
    cdaldo.setConfidentialityCode("N");
    cdaldo.setSetId(id);
    cdaldo.setVersion("1");


    CDALDOPatient patient = new CDALDOPatient();
    List<CDALDOId> ids = new ArrayList<>();

    CDALDOId cfId = new CDALDOId();
    cfId.setOid("2.16.840.1.113883.2.9.4.3.2");
    cfId.setExtensionId("ABCDEF12G34H567I");

    CDALDOId eniId = new CDALDOId(); 
    eniId.setOid("2.16.840.1.113883.2.9.4.3.18");
    eniId.setExtensionId("ENI123456789");

    CDALDOId anaId = new CDALDOId();
    anaId.setOid("2.16.840.1.113883.2.9.4.3.17");
    anaId.setExtensionId("ANA987654321");

    CDALDOAddr addr = new CDALDOAddr("H", "IT", "CAMPANIA", "SA", "FISCIANO", "FISCIANO", "84084","via università");
    patient.setAddresses(List.of(addr));
    ids.add(cfId);
    ids.add(eniId);
    ids.add(anaId);

    patient.setIds(ids);
    patient.setFirstName("John");
    patient.setLastName("Doe");
    patient.setBirthDate(LocalDate.of(1990, 1, 1));
    patient.setBirthPlace("Rome");
    patient.setGender("M");
    patient.setTelecoms(List.of("tel: +22414012948129"),List.of("H"));

    cdaldo.setPatient(patient); 

    CDALDOPatient guardian = new CDALDOPatient();
    List<CDALDOId> guardianIds = new ArrayList<>();

    CDALDOId guardianCfId = new CDALDOId();
    guardianCfId.setOid("2.16.840.1.113883.2.9.4.3.2");
    guardianCfId.setExtensionId("GHIJKL34M56N789O");

    guardianIds.add(guardianCfId);

    guardian.setIds(guardianIds);
    guardian.setFirstName("Jane");
    guardian.setLastName("Smith");
    guardian.setBirthDate(LocalDate.of(1980, 5, 15));
    guardian.setBirthPlace("Milan");
    guardian.setGender("F");
    


    CDALDOAuthor author = new CDALDOAuthor();
    CDALDOId authorId = new CDALDOId();

    author.setTelecoms(List.of("tel:+391389218301289","mailto:ciao@prova.it","mailto:ciao@pec.it"));
    author.setTelecomUses(List.of("H","H","H"));
    authorId.setOid("2.16.840.1.113883.2.9.4.3.2");
    authorId.setRoot("AUTH123456789");

    author.setId(authorId);
    author.setFirstName("Jane");
    author.setLastName("Smith");
    List<String> telecoms = new ArrayList<>();
    telecoms.add("123456789");
    author.setTelecoms(telecoms);

    cdaldo.setAuthor(author);

    CDALDOId custodianId = new CDALDOId();
    custodianId.setOid("2.16.840.1.113883.2.9.4.1.1");
    custodianId.setExtensionId("FLS1100000001");

    CDALDOAddr custodianAddress = new CDALDOAddr();
    custodianAddress.setStreet("123 Main St");
    custodianAddress.setCity("Naples");
    custodianAddress.setPostalCode("80100");

    String custodianOrgName = "Health Organization";
    String custodianPhone = "+390812345678";


    String ramoAziendale = "Ramo Aziendale";
    String numeroNosologico = "123456";
    String nomeAzienda = "Nome Azienda";
    LocalDateTime lowTime = LocalDateTime.of(2023, 1, 1, 0, 0);
    LocalDateTime highTime = LocalDateTime.of(2023, 12, 31, 23, 59);
    List<String> repartoIds = List.of("Reparto1", "Reparto2");
    List<String> repartoNames = List.of("Reparto Name 1", "Reparto Name 2");
    List<String> ministerialCodes = List.of("Code1", "Code2");
    List<String> facilityNames = List.of("Facility1", "Facility2");
    List<String> facilityTelecoms = List.of("Telecom1", "Telecom2");

    cdaldo.setComponentOf(
        ramoAziendale,
        numeroNosologico,
        nomeAzienda,
        lowTime,
        highTime,
        repartoIds,
        repartoNames,
        ministerialCodes,
        facilityNames,
        facilityTelecoms
    );
    cdaldo.setCustodian(custodianId, custodianOrgName, custodianAddress, custodianPhone);
    System.err.println(cdaldo.check());
    cdaldo.setRapresentedOrganization(new CDALDOId(        "2.16.840.1.113883.2.9.4.1.1","13289038091283","ministero della pastina"));

    cdaldo.setCompiler(author);

    cdaldo.setLegalAuthenticator(author);
    cdaldo.setFullfillmentId("rpqwioprwqirp");
    Document result = cdaldo.getCDA();
    try {
        saveDocumentToFile(result, "risultato.xsd");
    } catch (TransformerException e) {
        System.err.println("Error saving document to file: " + e.getMessage());

    }
    assertNotNull(result);
    assertEquals("ClinicalDocument", result.getDocumentElement().getNodeName());
}
private File saveDocumentToFile(Document doc, String fileName) throws TransformerException {
    File outputDir = new File("test_output");
    if (!outputDir.exists()) {
        outputDir.mkdirs();
    }
    File outputFile = new File(outputDir, fileName);
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(outputFile);
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(source, result);
    return outputFile;
}

}