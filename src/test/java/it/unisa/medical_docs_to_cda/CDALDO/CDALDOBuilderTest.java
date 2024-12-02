package it.unisa.medical_docs_to_cda.CDALDO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        assertNotNull(doc);
        assertEquals(0, doc.getChildNodes().getLength());
    }

        // Verifies that the header is correctly added to the document with specified attributes
@Test
public void test_add_header_with_correct_attributes() throws ParserConfigurationException, TransformerException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.newDocument();

    String oid = "2.16.840.1.113883.2.9.2.99999.4.4";
    String extensionId = "Test123";
    String assigningAuthorityName = "Test Authority";
    String status = "completed";
    Date effectiveTimeDate = new Date(); // Use the current date for testing
    String confidentialityCodeValue = "N";
    String languageCodeValue = "it-IT";
    String versionNumberValue = "1";
    CDALDOBuilder.addHeader( doc, oid, extensionId, assigningAuthorityName, status,effectiveTimeDate, confidentialityCodeValue, oid,extensionId, assigningAuthorityName, versionNumberValue); 
    File outputDir = new File("test_output");
    if (!outputDir.exists()) {
        outputDir.mkdirs();
    }

    File outputFile = new File(outputDir, "test_document.xsd");
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(outputFile);
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(source, result);

    Element root = doc.getDocumentElement();
    assertEquals("ClinicalDocument", root.getTagName());
    assertEquals("IT", root.getElementsByTagName("realmCode").item(0).getAttributes().getNamedItem("code").getNodeValue());
    assertEquals("2.16.840.1.113883.1.3", root.getElementsByTagName("typeId").item(0).getAttributes().getNamedItem("root").getNodeValue());
    assertEquals("POCD_HD000040", root.getElementsByTagName("typeId").item(0).getAttributes().getNamedItem("extension").getNodeValue());
    assertEquals(oid, root.getElementsByTagName("id").item(0).getAttributes().getNamedItem("root").getNodeValue());
    assertEquals(extensionId, root.getElementsByTagName("id").item(0).getAttributes().getNamedItem("extension").getNodeValue());
    assertEquals(assigningAuthorityName, root.getElementsByTagName("id").item(0).getAttributes().getNamedItem("assigningAuthorityName").getNodeValue());

    // New assertions for added elements
    assertEquals(status, root.getElementsByTagName("sdtc:statusCode").item(0).getAttributes().getNamedItem("code").getNodeValue());
    assertEquals(formatEffectiveTime(effectiveTimeDate), root.getElementsByTagName("effectiveTime").item(0).getAttributes().getNamedItem("value").getNodeValue());
    assertEquals(confidentialityCodeValue, root.getElementsByTagName("confidentialityCode").item(0).getAttributes().getNamedItem("code").getNodeValue());
    assertEquals(languageCodeValue, root.getElementsByTagName("languageCode").item(0).getAttributes().getNamedItem("code").getNodeValue());
    assertEquals(versionNumberValue, root.getElementsByTagName("versionNumber").item(0).getAttributes().getNamedItem("value").getNodeValue());
}

private static String formatEffectiveTime(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssZ");
    sdf.setTimeZone(TimeZone.getDefault());
    return sdf.format(date);
}

}
