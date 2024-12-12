package it.unisa.medical_docs_to_cda.CDALDO;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
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
    assertEquals(null, doc.getDocumentElement(), "Document element should not be null");
    
}

@Test
public void test_add_header_with_correct_attributes() throws ParserConfigurationException, TransformerException {
    Document doc = createNewDocument();
    CDALDOId oid = new CDALDOId("2.16.840.1.113883.2.9.2.99999.4.4", "Test123", "Test Authority");
    String status = "completed";
    LocalDateTime effectiveTimeDate = LocalDateTime.now(); 
    String confidentialityCodeValue = "N";
    String versionNumberValue = "1";
    List<String> telecoms = new ArrayList<>();
    telecoms.add("tel:+1-123-456-7890");
    telecoms.add("mailto:prova@prova.it");
    telecoms.add("mailto:prova@pec.it");
    List<String> telecomsUse = new ArrayList<>();
    telecomsUse.add("H");
    telecomsUse.add("H");
    telecomsUse.add("H");
   CDALDOAuthor author = new CDALDOAuthor(oid,oid,telecoms,telecomsUse,"John", "Doe","doc");
    CDALDOAddr Addr = new CDALDOAddr("H","Italy","Campania","Salerno","Vibonati","Vibonati","84079","Via Regina Margherita 1");
    
    List<CDALDOId> patientIds = new ArrayList<>();
    patientIds.add(oid);
    List<CDALDOAddr> patientAddresses = new ArrayList<>();
    patientAddresses.add(Addr);
    CDALDOPatient patient = new CDALDOPatient(patientIds, 1, patientAddresses, new ArrayList<>(), new ArrayList<>(), "John", "Doe", "M", "City", LocalDate.now());

  CDALDOBuilder.addHeader(doc, oid, status, effectiveTimeDate, confidentialityCodeValue, oid, versionNumberValue, patient, null, author, LocalDateTime.now(), oid, author, LocalDateTime.now(),oid,"ospedale",Addr,"34252552534",oid,"organizazione bella",LocalDateTime.now(),author,List.of(author,author),"id fulfillment","ramo dell'azienda","numero nosologico","nomeAzienda",LocalDateTime.now(),LocalDateTime.now(),List.of("ciao","ciao"),List.of("ciao2","ciao2"),List.of("ciao3","ciao3"),List.of("ciao4","ciao4"),List.of("ciao5","ciao5"));
    File outputFile = saveDocumentToFile(doc, "test_document.xsd");

    Element root = doc.getDocumentElement();
    assertEquals("ClinicalDocument", root.getTagName(), "Root element should be ClinicalDocument");
    assertEquals("IT", root.getElementsByTagName("realmCode").item(0).getAttributes().getNamedItem("code").getNodeValue(), "Realm code should be IT");
    assertEquals("2.16.840.1.113883.1.3", root.getElementsByTagName("typeId").item(0).getAttributes().getNamedItem("root").getNodeValue(), "Type ID root should match expected value");
    assertEquals("POCD_HD000040", root.getElementsByTagName("typeId").item(0).getAttributes().getNamedItem("extension").getNodeValue(), "Type ID extension should match expected value");
    
    assertEquals(status, root.getElementsByTagName("sdtc:statusCode").item(0).getAttributes().getNamedItem("code").getNodeValue(), "Status code should match expected value");
    assertEquals(formatEffectiveTime(effectiveTimeDate), root.getElementsByTagName("effectiveTime").item(0).getAttributes().getNamedItem("value").getNodeValue(), "Effective time should match expected format");
    assertEquals(confidentialityCodeValue, root.getElementsByTagName("confidentialityCode").item(0).getAttributes().getNamedItem("code").getNodeValue(), "Confidentiality code should match expected value");
    assertEquals("it-IT", root.getElementsByTagName("languageCode").item(0).getAttributes().getNamedItem("code").getNodeValue(), "Language code should be it-IT");
    assertEquals(versionNumberValue, root.getElementsByTagName("versionNumber").item(0).getAttributes().getNamedItem("value").getNodeValue(), "Version number should match expected value");
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

private static String formatEffectiveTime(LocalDateTime effectiveTimeDate) {
    return effectiveTimeDate.atZone(ZoneId.systemDefault()).format(CDALDOBuilder.effectiveTimeFormatter);
}

}
