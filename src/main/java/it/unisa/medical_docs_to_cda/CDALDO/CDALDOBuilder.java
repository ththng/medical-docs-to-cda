package it.unisa.medical_docs_to_cda.CDALDO;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;

public class CDALDOBuilder {

    public static void addCode(Document doc, Element parent, String code, String codeSystem, String codeSystemName, String displayName) {
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

    public static void addElement(Document doc, Element parent, String elementName, String textContent, String[] attributes, String[] values) {
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
                                LocalDate effectiveTimeDate,String confidentialityCodeValue, CDALDOId setOid, String versionNumberValue,
                                CDALDOPatient patient, CDALDOPatient guardian, CDALDOAuthor author ) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        Element root = doc.createElement( "ClinicalDocument");
        root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", "urn:hl7-org:v3 CDA.xsd");

        doc.appendChild(root);

        Element realmCode = doc.createElement( "realmCode");
        realmCode.setAttribute("code", "IT");
        root.appendChild(realmCode);

        Element typeId = doc.createElement( "typeId");
        typeId.setAttribute("root", "2.16.840.1.113883.1.3");
        typeId.setAttribute("extension", "POCD_HD000040");
        root.appendChild(typeId);

        Element templateId = doc.createElement("templateId");
        templateId.setAttribute("root", "2.16.840.1.113883.2.9.10.1.5");
        templateId.setAttribute("extension", "1.2");
        root.appendChild(templateId);

        Element id = doc.createElement( "id");
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
        if(confidentialityCodeValue=="V"){
            confidentialityCode.setAttribute("displayName", "Very restricted");
        }else if(confidentialityCodeValue=="N"){
         confidentialityCode.setAttribute("displayName", "Normal");
        }
        root.appendChild(confidentialityCode);

        Element languageCode = doc.createElement("languageCode");
        languageCode.setAttribute("code", "it-IT");
        root.appendChild(languageCode);

        Element setId = doc.createElement( "setId");
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
        
        List<CDALDOId> patientId= patient.getIds();
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
        List<String> telecomValues = patient.getTelecoms();
        List<String> telecomUses = patient.getTelecomUses();
        if (!telecomValues.isEmpty() && !telecomUses.isEmpty()) {
            for (int i = 0; i < telecomValues.size(); i++) {
                Element telecomElement = doc.createElement("telecom");
                telecomElement.setAttribute("use", telecomUses.get(i));
                telecomElement.setAttribute("value", telecomValues.get(i));
                patientRole.appendChild(telecomElement);
            }
        }   
      
        
        Element patientElement = doc.createElement("patient");
        patientRole.appendChild(patientElement);

        Element nameElement = doc.createElement("name");
        patientElement.appendChild(nameElement);

        Element familyElement = doc.createElement("family");
        familyElement.setTextContent(patient.getLastName());
        nameElement.appendChild(familyElement);

        Element givenElement = doc.createElement("given");
        givenElement.setTextContent(patient.getFirstName());
        nameElement.appendChild(givenElement);

        Element administrativeGenderCodeElement = doc.createElement("administrativeGenderCode");
        if(patient.getGender().equals("F")){
            administrativeGenderCodeElement.setAttribute("code", patient.getGender());
            administrativeGenderCodeElement.setAttribute("codeSystem", "2.16.840.1.113883.5.1");
            administrativeGenderCodeElement.setAttribute("codeSystemName", "HL7 AdministrativeGender");
            administrativeGenderCodeElement.setAttribute("displayName", "FEMMINA");
        }else if(patient.getGender().equals("M")){
            administrativeGenderCodeElement.setAttribute("code", patient.getGender());
            administrativeGenderCodeElement.setAttribute("codeSystem", "2.16.840.1.113883.5.1");
            administrativeGenderCodeElement.setAttribute("codeSystemName", "HL7 AdministrativeGender");
            administrativeGenderCodeElement.setAttribute("displayName", "MASCHIO");
        }
            patientElement.appendChild(administrativeGenderCodeElement);

        Element birthTimeElement = doc.createElement("birthTime");
        birthTimeElement.setAttribute("value", formatEffectiveTime(patient.getBirthDate()));
        patientElement.appendChild(birthTimeElement);

        Element birthplaceElement = doc.createElement("birthplace");
        birthplaceElement.setTextContent(patient.getBirthPlace());
        patientElement.appendChild(birthplaceElement);
       // guradian
        if(guardian != null){
        Element guardianPerson = doc.createElement("guardian");
        patientElement.appendChild(guardianPerson);

        Element guardianName = doc.createElement("name");
        guardianPerson.appendChild(guardianName);

        Element guardianFamily = doc.createElement("family");
        guardianFamily.setTextContent(guardian.getLastName());
        guardianName.appendChild(guardianFamily);

        Element guardianGiven = doc.createElement("given");
        guardianGiven.setTextContent(guardian.getFirstName());
        guardianName.appendChild(guardianGiven);


        Element guardianBirthTime = doc.createElement("birthTime");
        guardianBirthTime.setAttribute("value", formatEffectiveTime(guardian.getBirthDate()));
        guardianPerson.appendChild(guardianBirthTime);

        Element guardianBirthplace = doc.createElement("birthplace");
        guardianBirthplace.setTextContent(guardian.getBirthPlace());
        guardianPerson.appendChild(guardianBirthplace);

        List<CDALDOAddr> guardianAddrs = guardian.getAddresses();
        if (!guardianAddrs.isEmpty()) {
            for (CDALDOAddr addr : guardianAddrs) {
                Element addrElement = doc.createElement("addr");
                addrElement.setAttribute("use", addr.getUse());
                guardianPerson.appendChild(addrElement);
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
        List<String> telecomGuardianValues = guardian.getTelecoms();
        List<String> telecomGuardianUses = guardian.getTelecomUses();
        if (!telecomGuardianValues.isEmpty() && !telecomUses.isEmpty()) {
            for (int i = 0; i < telecomValues.size(); i++) {
                Element telecomGuardianElement = doc.createElement("telecom");
                telecomGuardianElement.setAttribute("use", telecomGuardianUses.get(i));
                telecomGuardianElement.setAttribute("value", telecomGuardianValues.get(i));
                guardianPerson.appendChild(telecomGuardianElement);
            }
        }  
    }
}

   private static String formatEffectiveTime(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssZ");
    return date.atStartOfDay(ZoneId.systemDefault()).format(formatter);
}
}

