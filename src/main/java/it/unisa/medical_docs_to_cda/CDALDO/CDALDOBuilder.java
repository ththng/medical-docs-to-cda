package it.unisa.medical_docs_to_cda.CDALDO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;

public class CDALDOBuilder {

    public static void addCode(Document doc, Element parent, String code, String codeSystem, String codeSystemName,
            String displayName) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        if (parent == null) {
            throw new IllegalArgumentException("Parent element cannot be null");
        }
        if (code == null || codeSystem == null || codeSystemName == null || displayName == null) {
            throw new IllegalArgumentException("Code, codeSystem, codeSystemName, and displayName cannot be null");
        }

        Element codeElement = doc.createElement("code");
        codeElement.setAttribute("code", code);
        codeElement.setAttribute("codeSystem", codeSystem);
        codeElement.setAttribute("codeSystemName", codeSystemName);
        codeElement.setAttribute("displayName", displayName);
        parent.appendChild(codeElement);
    }

    public static void addTitle(Document doc, Element parent, String title) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        if (parent == null) {
            throw new IllegalArgumentException("Parent element cannot be null");
        }
        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }

        Element titleElement = doc.createElement("title");
        titleElement.setTextContent(title);
        parent.appendChild(titleElement);
    }

    public static void addText(Document doc, Element parent, String text) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        if (parent == null) {
            throw new IllegalArgumentException("Parent element cannot be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        Element textElement = doc.createElement("text");
        textElement.setTextContent(text);
        parent.appendChild(textElement);
    }

    public static void addElement(Document doc, Element parent, String elementName, String textContent,
            String[] attributes, String[] values) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        if (parent == null) {
            throw new IllegalArgumentException("Parent element cannot be null");
        }
        if (elementName == null || elementName.isEmpty()) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (attributes != null && values != null && attributes.length != values.length) {
            throw new IllegalArgumentException("Attributes and values arrays must have the same length");
        }

        Element element = doc.createElement(elementName);

        if (textContent != null) {
            element.setTextContent(textContent);
        }

        if (attributes != null && values != null) {
            for (int i = 0; i < attributes.length; i++) {
                if (attributes[i] != null && values[i] != null) {
                    element.setAttribute(attributes[i], values[i]);
                }
            }
        }

        parent.appendChild(element);
    }

    public static Document createBasicDoc() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        return doc;
    }

    public static void addHeader(Document doc, CDALDOId oid, String status,
            Date effectiveTimeDate, String confidentialityCodeValue, CDALDOId setOid, String versionNumberValue,
            CDALDOPatient patient) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        Element root = doc.createElement("ClinicalDocument");
        root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", "urn:hl7-org:v3 CDA.xsd");

        doc.appendChild(root);

        Element realmCode = doc.createElement("realmCode");
        realmCode.setAttribute("code", "IT");
        root.appendChild(realmCode);

        Element typeId = doc.createElement("typeId");
        typeId.setAttribute("root", "2.16.840.1.113883.1.3");
        typeId.setAttribute("extension", "POCD_HD000040");
        root.appendChild(typeId);

        Element templateId = doc.createElement("templateId");
        templateId.setAttribute("root", "2.16.840.1.113883.2.9.10.1.5");
        templateId.setAttribute("extension", "1.2");
        root.appendChild(templateId);

        Element id = doc.createElement("id");
        id.setAttribute("root", oid.getOid());
        id.setAttribute("extension", oid.getExtensionId());
        id.setAttribute("assigningAuthorityName", oid.getAssigningAuthorityName());
        root.appendChild(id);

        addCode(doc, root, "34105-7", "2.16.840.1.113883.6.1", "LOINC", "Lettera di dimissione ospedaliera");

        addTitle(doc, root, "LETTERA DI DIMISSIONE OSPEDALIERA");

        Element statusCode = doc.createElementNS("urn:hl7-org:sdtc", "sdtc:statusCode");
        statusCode.setAttribute("code", status);
        root.appendChild(statusCode);

        Element effectiveTime = doc.createElement("effectiveTime");
        effectiveTime.setAttribute("value", formatEffectiveTime(effectiveTimeDate));
        root.appendChild(effectiveTime);

        Element confidentialityCode = doc.createElement("confidentialityCode");
        confidentialityCode.setAttribute("code", confidentialityCodeValue);
        confidentialityCode.setAttribute("codeSystem", "2.16.840.1.113883.5.25");
        confidentialityCode.setAttribute("codeSystemName", "HL7 Confidentiality");
        if (confidentialityCodeValue == "V") {
            confidentialityCode.setAttribute("displayName", "Very restricted");
        } else if (confidentialityCodeValue == "N") {
            confidentialityCode.setAttribute("displayName", "Normal");
        }
        root.appendChild(confidentialityCode);

        Element languageCode = doc.createElement("languageCode");
        languageCode.setAttribute("code", "it-IT");
        root.appendChild(languageCode);

        Element setId = doc.createElement("setId");
        setId.setAttribute("root", setOid.getOid());
        setId.setAttribute("extension", setOid.getExtensionId());
        setId.setAttribute("assigningAuthorityName", setOid.getAssigningAuthorityName());
        root.appendChild(setId);

        Element versionNumber = doc.createElement("versionNumber");
        versionNumber.setAttribute("value", versionNumberValue);
        root.appendChild(versionNumber);

        Element recordTarget = doc.createElement("recordTarget");
        root.appendChild(recordTarget);

        Element patientRole = doc.createElement("patientRole");

        recordTarget.appendChild(patientRole);

        List<CDALDOId> patientId = patient.getIds();
        if (!patientId.isEmpty()) {
            for (CDALDOId paId : patientId) {
                Element patId = doc.createElement("id");
                patId.setAttribute("root", paId.getOid());
                patId.setAttribute("extension", paId.getExtensionId());
                patId.setAttribute("assigningAuthorityName", paId.getAssigningAuthorityName());
                patientRole.appendChild(patId);
            }
        }
        List<CDALDOAddr> patientAddrs = patient.getAddresses();
        if (!patientAddrs.isEmpty()) {
            for (CDALDOAddr addr : patientAddrs) {
                Element addrElement = doc.createElement("addr");
                addrElement.setAttribute("use", addr.getUse());
                patientRole.appendChild(addrElement);
                Element country = doc.createElement("country");
                country.setTextContent(addr.getCountry());
                addrElement.appendChild(country);
                Element state = doc.createElement("state");
                state.setTextContent(addr.getState());
                addrElement.appendChild(state);
                Element county = doc.createElement("county");
                county.setTextContent(addr.getCounty());
                addrElement.appendChild(county);
                Element city = doc.createElement("city");
                city.setTextContent(addr.getCity());
                addrElement.appendChild(city);
                Element censusTract = doc.createElement("censusTract");
                censusTract.setTextContent(addr.getCensusTract());
                addrElement.appendChild(censusTract);
                Element postalCode = doc.createElement("postalCode");
                postalCode.setTextContent(addr.getPostalCode());
                addrElement.appendChild(postalCode);
                Element street = doc.createElement("street");
                street.setTextContent(addr.getStreet());
                addrElement.appendChild(street);
            }
        }

    }

    private static String formatEffectiveTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssZ");
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static Element createSection(Document doc, Element structuredBody, String typeCode,
        String sectionClassCode, String sectionMoodCode,
        String code, String codeSystem, String codeSystemName,
        String displayName, String titleText, List<CDALDONarrativeBlock> narrativeBlocks) {

        // Section component
        Element component = doc.createElement("component");
        component.setAttribute("typeCode", typeCode);

        structuredBody.appendChild(component);

        // Section element
        Element section = doc.createElement("section");
        section.setAttribute("classCode", sectionClassCode);
        section.setAttribute("moodCode", sectionMoodCode);
        // Append the section to the component
        component.appendChild(section);

        // Section code
        addCode(doc, section, code, codeSystem, codeSystemName, displayName);

        // Section title
        Element title = doc.createElement("title");
        title.setTextContent(titleText);
        section.appendChild(title);

        // Section text  
        createText(doc, section, narrativeBlocks);

        return component;
    }

    public static void createText(Document doc, Element section, List<CDALDONarrativeBlock> narrativeBlocks) {
        Element text = doc.createElement("text");
        section.appendChild(text);
        int count = 0;
        for(CDALDONarrativeBlock block: narrativeBlocks){
            String textType = block.getNarrativeType();
            Object textContent = block.getContent();
            switch(textType) {
                case "paragraph":
                    if (textContent instanceof String) {
                        Element paragraph = doc.createElement("paragraph");
                        text.appendChild(paragraph);

                        // Split the text according to the \n escape sequence
                        String[] fragments = ((String) textContent).split("\\n");

                        for (int i = 0; i < fragments.length; i++) {
                            paragraph.appendChild(doc.createTextNode(fragments[i]));

                            // Add <br> tag until the last split element
                            if (i < fragments.length - 1) {
                                Element br = doc.createElement("br");
                                paragraph.appendChild(br);
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Content for 'paragraph' narrative type must be a String object.");
                    }
                    break;

                case "list":
                    if(textContent instanceof String[]){
                        Element list = doc.createElement("list");
                        text.appendChild(list);
                        count = 0;
                        for (String item: (String[]) textContent) {
                            Element listItem = doc.createElement("item");
                            Element content = doc.createElement("content");
                            content.setAttribute("ID", "DIAG-" + (count + 1)); // Dynamic ID
                            count ++;
                            content.setTextContent(item);
            
                            listItem.appendChild(content);
                            list.appendChild(listItem);
                        }
                    }
                    else{
                        throw new IllegalArgumentException("Content for 'list' narrative type must be a String array.");
                    }
                    break;

                case "formatted_text":
                    if(textContent instanceof Map){
                        @SuppressWarnings("unchecked")
                        Map<String, String> formattedData = ((Map <String, String>) textContent);
                        for (Map.Entry<String, String> entry: formattedData.entrySet()) {
                            Element content = doc.createElement("content");
                            content.setAttribute("styleCode", entry.getKey()); // styleCode is now the type of format to use on the data
                            content.setTextContent(entry.getValue());
                            text.appendChild(content);            
                        }
                    }
                    else{
                        throw new IllegalArgumentException("Content for 'formatted_text' narrative type must be a Map");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported narrative type" + textType);
            } 
        }
    }

    public static void createAnamnesiSection(Document doc, Element section, String code, String codeSystem, String codeSystemName,
            String displayName, String titleText, List<CDALDONarrativeBlock> narrativeBlocks) {

    }

    public static void createObjectiveExaminationSection(Document doc, Element section, String code, String codeSystem, String codeSystemName,
    String displayName, String titleText, List<CDALDONarrativeBlock> narrativeBlocks){

    }

    public static void createPharmacologicalTherapySection(Document doc, Element section, String code, String codeSystem, String codeSystemName,
    String displayName, String titleText, List<CDALDONarrativeBlock> narrativeBlocks){

    }

    public static void addBody(Document doc, List<CDALDONarrativeBlock> narrativeBlocks) { 

        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        Element component = doc.createElement("component");
        doc.getElementsByTagName("ClinicalDocument").item(0).appendChild(component);

        Element structuredBody = doc.createElement("structuredBody");
        structuredBody.setAttribute("classCode", "DOCBODY");
        structuredBody.setAttribute("moodCode", "EVN");

        component.appendChild(structuredBody);

        Element section1 = createSection(doc, structuredBody, "COMP", "DOCSECT", "EVN",
                "46241-6", "2.16.840.1.113883.6.1",
                "LOINC", "Diagnosi di Accettazione",
                "Motivo del ricovero", narrativeBlocks);
        structuredBody.appendChild(section1);

    }
}