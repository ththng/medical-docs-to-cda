package it.unisa.medical_docs_to_cda.CDALDO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.time.LocalDate;
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

public class CDALDOEntrySubstanceAdmTest {

    // Constructor successfully initializes object with all mandatory fields
    // (entryType, medicineCode, medicineDescription, statusCode)
    @Test
    public void test_constructor_with_mandatory_fields_success() {
        String entryType = "entry";
        String medicineCode = "123";
        String medicineDescription = "Test Medicine";
        String statusCode = "active";

        CDALDOEntrySubstanceAdm substanceAdm = new CDALDOEntrySubstanceAdm(
                entryType,
                medicineCode,
                medicineDescription,
                null,
                statusCode,
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
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        assertEquals(entryType, substanceAdm.getEntryType());
        assertEquals(medicineCode, substanceAdm.getMedicineCode());
        assertEquals(medicineDescription, substanceAdm.getMedicineDescription());
        assertEquals(statusCode, substanceAdm.getStatusCode());
    }

    // Constructor throws IllegalArgumentException when mandatory fields are missing
    @Test
    public void test_constructor_throws_exception_for_missing_mandatory_fields() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CDALDOEntrySubstanceAdm(
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
                        null,
                        null));

        String expectedMessage = "Missing mandatory fields: entryType, medicineCode, medicineDescription, statusCode";
        assertEquals(expectedMessage, exception.getMessage());
    }

    // createEntry() generates complete XML structure for substance administration
    // with all optional fields
    @Test
    public void test_create_entry_with_all_optional_fields() throws ParserConfigurationException, TransformerException {
        Document doc = CDALDOBuilder.createBasicDoc();
        Element section = doc.createElement("section");
        doc.appendChild(section);
        CDALDOEntrySubstanceAdm entry = new CDALDOEntrySubstanceAdm(
                "entry", "medCode", "medDesc", "ref", "active",
                LocalDate.now(), LocalDate.now().plusDays(1), "routeCode", "routeDisplayName",
                "approachSiteCode", "approachSiteCodeSystem", "approachSiteCodeSystemName", "approachSiteDisplayName",
                "1", "2", "mg", "1", "2", "ml",
                Arrays.asList(new CDALDOAuthor(new CDALDOId("id", "extensionId", "name"), "first", "last")),
                Arrays.asList(new CDALDOAuthor(new CDALDOId("iddd", "efxtensionId", "sname"), "first2", "last2")),
                null);
        entry.createEntry(doc, section);
        saveDocumentToFile(doc, "entry_substanceAdm.xsd");
        assertNotNull(doc.getElementsByTagName("section"));
        assertNotNull(doc.getElementsByTagName("substanceAdministration").item(0),
                "Missing substanceAdministration");
        assertNotNull(section.getElementsByTagName("routeCode").item(0), "Missing routeCode");
        assertNotNull(section.getElementsByTagName("approachSiteCode").item(0), "Missing approachSiteCode");
        assertNotNull(section.getElementsByTagName("doseQuantity").item(0), "Missing doseQuantity");
        assertNotNull(section.getElementsByTagName("rateQuantity").item(0), "Missing rateQuantity");

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
