package it.unisa.medical_docs_to_cda.CDALDO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CDALDOEntryActTest {

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

    // Constructor creates valid CDALDOEntryAct with all required fields
    @Test
    public void test_constructor_creates_valid_entry_act() {
        String entryType = "entry";
        String statusCode = "active";
        LocalDate effectiveTimeLow = LocalDate.now();
        LocalDate effectiveTimeHigh = null;
        String allergyValueCode = "FALG";
        String allergyDisplayName = "Food Allergy";
        LocalDateTime allergyStartDate = LocalDateTime.now();
        LocalDateTime allergyEndDate = null;
        List<CDALDOEntry> entryRelationships = new ArrayList<>();
        List<CDALDOAgent> agents = new ArrayList<>();
        CDALDOAgent agent = new CDALDOAgent("123", "2.16.840.1.113883.2.9.5.2.8", "LOINC", "agrumi");
        agents.add(agent);

        CDALDOEntryAct act = new CDALDOEntryAct(
                entryType,
                statusCode,
                effectiveTimeLow,
                effectiveTimeHigh,
                allergyValueCode,
                allergyDisplayName,
                allergyStartDate,
                allergyEndDate,
                entryRelationships,
                agents);

        assertEquals(entryType, act.getEntryType());
        assertEquals(statusCode, act.getStatusCode());
        assertEquals(effectiveTimeLow, act.getEffectiveTimeLow());
        assertNull(act.getEffectiveTimeHigh());
        assertEquals(allergyValueCode, act.getAllergyTypeValueCode());
        assertEquals(allergyDisplayName, act.getAllergyTypeDisplayName());
        assertEquals(allergyStartDate, act.getAllergyStartDate());
        assertNull(act.getAllergyEndDate());
        assertEquals(entryRelationships, act.getEntryRelationships());
    }

    // Constructor throws IllegalArgumentException for null entryType
    @Test
    public void test_constructor_throws_exception_for_null_entry_type() {
        String entryType = null;
        String statusCode = "active";
        LocalDate effectiveTimeLow = LocalDate.now();
        LocalDate effectiveTimeHigh = null;
        String allergyValueCode = "FALG";
        String allergyDisplayName = "Food Allergy";
        LocalDateTime allergyStartDate = LocalDateTime.now();
        LocalDateTime allergyEndDate = null;
        List<CDALDOEntry> entryRelationships = new ArrayList<>();
        List<CDALDOAgent> agents = new ArrayList<>();
        CDALDOAgent agent = new CDALDOAgent("123", "2.16.840.1.113883.2.9.5.2.8", "LOINC", "fragole");
        agents.add(agent);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CDALDOEntryAct(
                        entryType,
                        statusCode,
                        effectiveTimeLow,
                        effectiveTimeHigh,
                        allergyValueCode,
                        allergyDisplayName,
                        allergyStartDate,
                        allergyEndDate,
                        entryRelationships, agents));

        assertTrue(exception.getMessage().contains("entryType"));
    }

    // createEntry method adds elements to the Document and saves it to a file
    @Test
    public void test_create_entry_saves_document_to_file() throws Exception {
        String entryType = "entry";
        String statusCode = "active";
        LocalDate effectiveTimeLow = LocalDate.now();
        LocalDate effectiveTimeHigh = null;
        String allergyValueCode = "FALG";
        String allergyDisplayName = "Food Allergy";
        LocalDateTime allergyStartDate = LocalDateTime.now();
        LocalDateTime allergyEndDate = null;
        List<CDALDOEntry> entryRelationships = new ArrayList<>();
        List<CDALDOAgent> agents = new ArrayList<>();
        CDALDOAgent agent = new CDALDOAgent("123", "2.16.840.1.113883.2.9.5.2.8", "LOINC", "aspartame");
        agents.add(agent);

        CDALDOEntryAct act = new CDALDOEntryAct(
                entryType,
                statusCode,
                effectiveTimeLow,
                effectiveTimeHigh,
                allergyValueCode,
                allergyDisplayName,
                allergyStartDate,
                allergyEndDate,
                entryRelationships,
                agents);

        Document doc = CDALDOBuilder.createBasicDoc();
        Element root = doc.createElement("ClinicalDocument");
        root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation",
                "urn:hl7-org:v3 CDA.xsd");
        root.setAttribute("xmlns",
                "urn:hl7-org:v3");
        doc.appendChild(root);
        Element section = doc.createElement("section");
        root.appendChild(section);

        // Act
        act.createEntry(doc, section);
        File savedFile = saveDocumentToFile(doc, "entry_act_test.xsd");

        // Assert
        assertTrue(savedFile.exists());
    }
}
