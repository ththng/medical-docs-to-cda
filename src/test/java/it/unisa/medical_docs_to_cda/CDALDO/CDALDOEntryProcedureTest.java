package it.unisa.medical_docs_to_cda.CDALDO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
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
import org.w3c.dom.Element;

public class CDALDOEntryProcedureTest {

    // Constructor successfully creates object with all mandatory fields provided
    @Test
    public void test_constructor_with_valid_mandatory_fields() {
        // Arrange
        String code = "123";
        String codeSystem = "2.16.840.1.113883.6.1";
        String codeSystemName = "LOINC";
        String displayName = "Test Procedure";
        String entryType = "entry";
        String typeCode = "COMP";
        LocalDateTime effectiveTimeLow = LocalDateTime.now();
        LocalDateTime effectiveTimeHigh = LocalDateTime.now().plusHours(1);
        String refProcedure = "#proc1";
        List<CDALDOEntryObservation> relationshipEntries = new ArrayList<>();

        // Act
        CDALDOEntryProcedure procedure = new CDALDOEntryProcedure(
                code, codeSystem, codeSystemName, displayName, entryType,
                typeCode, effectiveTimeLow, effectiveTimeHigh, refProcedure, relationshipEntries);

        // Assert
        assertNotNull(procedure);
        assertEquals("completed", procedure.getStatusProcedure());
        assertTrue(procedure.isEffectiveTime());
    }

    // createEntry generates valid XML structure for entry type 'entry'
    @Test
    public void test_create_entry_generates_valid_xml() throws TransformerException, ParserConfigurationException {
        Document doc = CDALDOBuilder.createBasicDoc();
        Element root = doc.createElement("ClinicalDocument");
        root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation",
                "urn:hl7-org:v3 CDA.xsd");
        root.setAttribute("xmlns",
                "urn:hl7-org:v3");
        doc.appendChild(root);
        Element section = doc.createElement("section");
        root.appendChild(section);

        List<CDALDOEntryObservation> entries = new ArrayList<>();
        String code = "12345";
        String codeSystem = "2.16.840.1.113883.6.1";
        String codeSystemName = "LOINC";
        String displayName = "Example Observation";
        String entryType = "entryRelationship";
        String typeCode = "COMP";
        boolean isAnamnesi = false;
        LocalDateTime effectiveTimeLow = LocalDateTime.now().minusDays(1);
        LocalDateTime effectiveTimeHigh = LocalDateTime.now();
        String valueCode = "A123";
        String valueCodeSystem = "2.16.840.1.113883.6.96";
        String valueCodeSystemName = "LOINC";
        String valueDisplayName = "Positive";
        String xsiType = "CD";
        boolean translationNecessary = false;
    
        
        CDALDOEntryObservation observation = new CDALDOEntryObservation(
                code, codeSystem, codeSystemName, displayName,
                entryType, typeCode, isAnamnesi, effectiveTimeLow,
                effectiveTimeHigh, null, null, valueCode, valueCodeSystem,
                valueCodeSystemName, valueDisplayName, xsiType, null, 0.0f, null, translationNecessary, null, null, null);

        entries.add(observation);

        CDALDOEntryProcedure entryProcedure = new CDALDOEntryProcedure(
                "code", "codeSystem", "codeSystemName", "displayName",
                "entry", "typeCode", LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                null, entries);
        entryProcedure.createEntry(doc, section);
        saveDocumentToFile(doc, "entry_procedure_test.xsd");
        assertNotNull(doc.getElementsByTagName("section"));
        assertNotNull(doc.getElementsByTagName("procedure").item(0),
                "Missing procedure");
        assertNotNull(section.getElementsByTagName("code").item(0), "Missing code");

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
