package it.unisa.medical_docs_to_cda.CDALDO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

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

public class CDALDOEntryObservationTest {

    // Constructor successfully initializes object with all mandatory fields
    // (code, codeSystem, codeSystemName, displayName, entryType)
    @Test
    public void test_constructor_with_mandatory_fields_success() {
        String code = "8646-2";
        String codeSystem = "123.4.5";
        String codeSystemName = "LOINC";
        String displayName = "Diagnosi di Accettazione Ospedaliera";
        String entryType = "entry";

        CDALDOEntryObservation observation = new CDALDOEntryObservation(
                code,
                codeSystem,
                codeSystemName,
                displayName,
                entryType,
                null, // typeCode
                false, // isAnamnesi
                null, // effectiveTimeLow
                null, // effectiveTimeHigh
                null, // dtEsecuzione
                null, // dtRichiesta
                null, // valueCode
                null, // valueCodeSystem
                null, // valueCodeSystemName
                null, // valueDisplayName
                null, // xsiType
                null, // outcome
                0.0f, // value
                null, // unit
                false, // translationNecessary
                null, // entryRelationships
                null, // performers
                null // participants
        );

        assertEquals(code, observation.getCode());
        assertEquals(codeSystem, observation.getCodeSystem());
        assertEquals(codeSystemName, observation.getCodeSystemName());
        assertEquals(displayName, observation.getDisplayName());
        assertEquals(entryType, observation.getEntryType());
    }

    // Constructor throws IllegalArgumentException when mandatory fields are missing
    @Test
    public void test_constructor_throws_exception_for_missing_mandatory_fields() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CDALDOEntryObservation(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        false,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0.0f,
                        null,
                        false,
                        null,
                        null,
                        null));

        String expectedMessage = "Make sure that all the mandatory fields related to the entry observation code are present";
        assertEquals(expectedMessage, exception.getMessage());
    }

    // createEntry() generates complete XML structure for a random observation (with
    // fictional parameters)
    // with all optional fields
    @Test
    public void test_create_entry_with_all_optional_fields() throws ParserConfigurationException, TransformerException {
        Document doc = CDALDOBuilder.createBasicDoc();
        Element section = doc.createElement("section");
        doc.appendChild(section);
        CDALDOEntryObservation observation = new CDALDOEntryObservation(
                "8646-2", "123.4.5", "LOINC", "Diagnosi di Accettazione Ospedaliera", "entry", null, true,
                LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay(),
                LocalDate.now().plusDays(2).atStartOfDay(), LocalDate.now().plusDays(3).atStartOfDay(), "prova",
                "valueCodeSystem",
                "ciao", "ciaooo", "CD", null, 0.0f, null, false,
                Arrays.asList(new CDALDOEntryObservation("24", "32", "loinc", "DW", "entryRelationship", "REFR", false,
                        LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(2).atStartOfDay(),
                        LocalDate.now().plusDays(1).atStartOfDay(), LocalDate.now().plusDays(4).atStartOfDay(), null,
                        null, null, null, "ST", "finito", 0.0f, null, false, null, null, null)),
                Arrays.asList(new CDALDOAuthor(new CDALDOId("id", "extensionId", "name"), "first", "last")),
                Arrays.asList(new CDALDOAuthor(new CDALDOId("iddd", "efxtensionId", "sname"), "first2", "last2")));
        observation.createEntry(doc, section);
        saveDocumentToFile(doc, "entry_observation.xsd");
        assertNotNull(doc.getElementsByTagName("section"));
        assertNotNull(section.getElementsByTagName("entry").item(0), "Missing entry");
        assertNotNull(doc.getElementsByTagName("observation").item(0),
                "Missing observation");
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
