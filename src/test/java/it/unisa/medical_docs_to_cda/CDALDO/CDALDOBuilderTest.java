package it.unisa.medical_docs_to_cda.CDALDO;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

public class CDALDOBuilderTest {
    
    // DocumentBuilder creation succeeds with default configuration
    @Test
    public void test_document_builder_creation_with_default_configuration() throws ParserConfigurationException {
        Document doc = CDALDOBuilder.createBasicDoc();
        assertNotNull(doc, "Document should not be null");
        assertEquals(0, doc.getChildNodes().getLength(), "Document should have no child nodes initially");
    }

    // Verifies that the header is correctly added to the document with specified attributes
    @Test
    public void test_add_header_with_correct_attributes() throws ParserConfigurationException, TransformerException {
        Document doc = createNewDocument();
        CDALDOId oid = new CDALDOId("2.16.840.1.113883.2.9.2.99999.4.4", "Test123", "Test Authority");
        String status = "completed";
        Date effectiveTimeDate = new Date(); // Use the current date for testing
        String confidentialityCodeValue = "N";
        String versionNumberValue = "1";
        CDALDOAddr Addr =new CDALDOAddr("H","Italy","Campania","Salerno","Vibonati","Vibonati","84079","Via Regina Margherita 1");
        
        // Create a mock patient
        List<CDALDOId> patientIds = new ArrayList<>();
        patientIds.add(oid);
        List<CDALDOAddr> patientAddresses = new ArrayList<>();
        patientAddresses.add(Addr);
        CDALDOPatient patient = new CDALDOPatient(patientIds, 1, patientAddresses, new ArrayList<>(), new ArrayList<>(), "John", "Doe", "M", "City", effectiveTimeDate.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate());

        // Call addHeader with the new patient parameter
        CDALDOBuilder.addHeader(doc, oid, status, effectiveTimeDate, confidentialityCodeValue, oid, versionNumberValue, patient); 

        // Call to addBody method
        List<CDALDONarrativeBlock> narrativeBlocks = new ArrayList<>();
        String[] recoveryReasonsItems = {"Disturbo di panico","Ipertiroidismo","Depressione"};
        narrativeBlocks.add(new CDALDONarrativeBlock("list", recoveryReasonsItems));
        narrativeBlocks.add(new CDALDONarrativeBlock("paragraph", "Paziente in cattivo compenso emodinamico."));

        Map<String, String> formattedContent = new LinkedHashMap<>(); // Use HashMap<>() if the sequence of the elements is not important
        formattedContent.put("Bold", "This is bold text.");
        formattedContent.put("Italics", "This is italicized text.");
        formattedContent.put("Underline", "This is underlined text.");
        narrativeBlocks.add(new CDALDONarrativeBlock("formatted_text", formattedContent));

        CDALDOBuilder.addBody(doc, narrativeBlocks);
        File outputFile = saveDocumentToFile(doc, "test_document.xsd");

        Element root = doc.getDocumentElement();
        assertEquals("ClinicalDocument", root.getTagName(), "Root element should be ClinicalDocument");
        assertEquals("IT", root.getElementsByTagName("realmCode").item(0).getAttributes().getNamedItem("code").getNodeValue(), "Realm code should be IT");
        assertEquals("2.16.840.1.113883.1.3", root.getElementsByTagName("typeId").item(0).getAttributes().getNamedItem("root").getNodeValue(), "Type ID root should match expected value");
        assertEquals("POCD_HD000040", root.getElementsByTagName("typeId").item(0).getAttributes().getNamedItem("extension").getNodeValue(), "Type ID extension should match expected value");
        
        // New assertions for added elements
        assertEquals(status, root.getElementsByTagName("sdtc:statusCode").item(0).getAttributes().getNamedItem("code").getNodeValue(), "Status code should match expected value");
        assertEquals(formatEffectiveTime(effectiveTimeDate), root.getElementsByTagName("effectiveTime").item(0).getAttributes().getNamedItem("value").getNodeValue(), "Effective time should match expected format");
        assertEquals(confidentialityCodeValue, root.getElementsByTagName("confidentialityCode").item(0).getAttributes().getNamedItem("code").getNodeValue(), "Confidentiality code should match expected value");
        assertEquals("it-IT", root.getElementsByTagName("languageCode").item(0).getAttributes().getNamedItem("code").getNodeValue(), "Language code should be it-IT");
        assertEquals(versionNumberValue, root.getElementsByTagName("versionNumber").item(0).getAttributes().getNamedItem("value").getNodeValue(), "Version number should match expected value");
    }

    // Test for handling null document
    @Test
    public void test_add_header_with_null_document() {
        assertThrows(IllegalArgumentException.class, () -> {
            CDALDOBuilder.addHeader(null, new CDALDOId("oid", "ext", "auth"), "status", new Date(), "N", new CDALDOId("oid", "ext", "auth"), "1", null);
        }, "Expected IllegalArgumentException for null document");
    }

    private Document createNewDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
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

    private static String formatEffectiveTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssZ");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }
    
}
