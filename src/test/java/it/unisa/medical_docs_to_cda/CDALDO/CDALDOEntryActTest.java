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
        String allergyValueCode = "123";
        String allergyDisplayName = "Peanut Allergy";
        LocalDateTime allergyStartDate = LocalDateTime.now();
        LocalDateTime allergyEndDate = null;
        List<CDALDOEntry> entryRelationships = new ArrayList<>();

        CDALDOEntryAct act = new CDALDOEntryAct(
                entryType,
                statusCode,
                effectiveTimeLow,
                effectiveTimeHigh,
                allergyValueCode,
                allergyDisplayName,
                allergyStartDate,
                allergyEndDate,
                entryRelationships);

        assertEquals(entryType, act.getEntryType());
        assertEquals(statusCode, act.getStatusCode());
        assertEquals(effectiveTimeLow, act.getEffectiveTimeLow());
        assertNull(act.getEffectiveTimeHigh());
        assertEquals(allergyValueCode, act.getAllergyValueCode());
        assertEquals(allergyDisplayName, act.getAllergyDisplayName());
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
        String allergyValueCode = "123";
        String allergyDisplayName = "Peanut Allergy";
        LocalDateTime allergyStartDate = LocalDateTime.now();
        LocalDateTime allergyEndDate = null;
        List<CDALDOEntry> entryRelationships = new ArrayList<>();

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
                        entryRelationships));

        assertTrue(exception.getMessage().contains("entryType"));
    }


        // createEntry method adds elements to the Document and saves it to a file
    @Test
    public void test_create_entry_saves_document_to_file() throws Exception {
        String entryType = "entry";
        String statusCode = "active";
        LocalDate effectiveTimeLow = LocalDate.now();
        LocalDate effectiveTimeHigh = null;
        String allergyValueCode = "123";
        String allergyDisplayName = "Peanut Allergy";
        LocalDateTime allergyStartDate = LocalDateTime.now();
        LocalDateTime allergyEndDate = null;
        List<CDALDOEntry> entryRelationships = new ArrayList<>();

        CDALDOEntryAct act = new CDALDOEntryAct(
            entryType,
            statusCode, 
            effectiveTimeLow,
            effectiveTimeHigh,
            allergyValueCode,
            allergyDisplayName,
            allergyStartDate,
            allergyEndDate,
            entryRelationships
        );

        Document doc = CDALDOBuilder.createBasicDoc();
        Element section = doc.createElement("section");
        doc.appendChild(section);

        // Act
        act.createEntry(doc, section);
        File savedFile = saveDocumentToFile(doc, "entry_act_test.xsd");

        // Assert
        assertTrue(savedFile.exists());
    }
}
