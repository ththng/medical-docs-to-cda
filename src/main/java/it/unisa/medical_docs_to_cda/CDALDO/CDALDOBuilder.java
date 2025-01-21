package it.unisa.medical_docs_to_cda.CDALDO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;

public class CDALDOBuilder {
    public final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            .withZone(ZoneId.systemDefault());
    public final static DateTimeFormatter effectiveTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssZ")
            .withZone(ZoneId.systemDefault());
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
            LocalDateTime effectiveTimeDate, String confidentialityCodeValue, CDALDOId setOid,
            String versionNumberValue,
            CDALDOPatient patient, CDALDOPatient guardian, CDALDOAuthor author, LocalDateTime authorTime,
            CDALDOId rapresentedOrganization, CDALDOAuthor compiler, LocalDateTime compilerTime, CDALDOId custodianId,
            String custodianOrgazationName, CDALDOAddr custodianOrganizationAddress, String custodianPhoneNumber,
            CDALDOId informationRecipientId, String informationRecipientName ,LocalDateTime legalAuthTime,
            CDALDOAuthor legalAuthenticator, List<CDALDOAuthor> participants, String fulfillmentId,
            String ramoAziendale, String numeroNosologico, String nomeAzienda,LocalDateTime lowTime,
            LocalDateTime highTime, List<String> repartoIds, List<String> repartoNames,List<String> ministerialCodes,
             List<String> facilityNames, List<String> facilityTelecoms) {
        
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        
        Element root = doc.createElement("ClinicalDocument");
        root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation",
                "urn:hl7-org:v3 CDA.xsd");
        root.setAttribute( "xmlns",
                "urn:hl7-org:v3");
        doc.appendChild(root);

        createRealmCode(doc, root);
        createTypeId(doc, root);
        createTemplateId(doc, root);
        
        Element id = createIdElement(doc, oid.getOid(), oid.getExtensionId(), oid.getAssigningAuthorityName());
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
        if (confidentialityCodeValue.equals("V")) {
            confidentialityCode.setAttribute("displayName", "Very restricted");
        } else if (confidentialityCodeValue.equals("N")) {
            confidentialityCode.setAttribute("displayName", "Normal");
        }
        root.appendChild(confidentialityCode);

        Element languageCode = doc.createElement("languageCode");
        languageCode.setAttribute("code", "it-IT");
        root.appendChild(languageCode);

        Element setId = createIdElement(doc, setOid.getOid(), setOid.getExtensionId(), setOid.getAssigningAuthorityName());
        root.appendChild(setId);

        Element versionNumber = doc.createElement("versionNumber");
        versionNumber.setAttribute("value", versionNumberValue);
        root.appendChild(versionNumber);

        Element recordTarget = doc.createElement("recordTarget");
        root.appendChild(recordTarget);
        
        addPatientRole(doc, recordTarget, patient);
        
        if (guardian != null) {
            addGuardian(doc, recordTarget, guardian);
        }

        if (author != null) {
            addAuthor(doc, root, author, authorTime);
        }
        if(compiler!=null){
            addDataEnterer(doc, root, compiler, compilerTime);
        }
        if(custodianId != null){

        addCustodian(doc, root, custodianId, custodianOrgazationName, custodianOrganizationAddress, custodianPhoneNumber);
        }
        if (informationRecipientId != null && informationRecipientName != null) {
            addInformationRecipient(doc, root, informationRecipientId, informationRecipientName);
        }

        if (legalAuthenticator != null) {
            addLegalAuthenticator(doc, root, legalAuthTime, legalAuthenticator);
        }

        if (participants != null && !participants.isEmpty()) {
            addParticipants(doc, root, participants);
        }

        if (fulfillmentId != null) {
            addInFulfillmentOf(doc, root, fulfillmentId);
        }

        addComponentOf(doc, root, ramoAziendale, numeroNosologico, nomeAzienda, lowTime, highTime, repartoIds, repartoNames, ministerialCodes, facilityNames, facilityTelecoms);
    }

    private static void createRealmCode(Document doc, Element root) {
        Element realmCode = doc.createElement("realmCode");
        realmCode.setAttribute("code", "IT");
        root.appendChild(realmCode);
    }

    private static void createTypeId(Document doc, Element root) {
        Element typeId = doc.createElement("typeId");
        typeId.setAttribute("root", "2.16.840.1.113883.1.3");
        typeId.setAttribute("extension", "POCD_HD000040");
        root.appendChild(typeId);
    }

    private static void createTemplateId(Document doc, Element root) {
        Element templateId = doc.createElement("templateId");
        templateId.setAttribute("root", "2.16.840.1.113883.2.9.10.1.5");
        templateId.setAttribute("extension", "1.2");
        root.appendChild(templateId);
    }

    private static void addPatientRole(Document doc, Element recordTarget, CDALDOPatient patient) {
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

        createAddressElements(doc, patientRole, patient.getAddresses());

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
        if (patient.getGender().equals("F")) {
            administrativeGenderCodeElement.setAttribute("code", patient.getGender());
            administrativeGenderCodeElement.setAttribute("codeSystem", "2.16.840.1.113883.5.1");
            administrativeGenderCodeElement.setAttribute("codeSystemName", "HL7 AdministrativeGender");
            administrativeGenderCodeElement.setAttribute("displayName", "FEMMINA");
        } else if (patient.getGender().equals("M")) {
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

    }

    private static void addGuardian(Document doc, Element parent, CDALDOPatient guardian) {
        Element guardianPerson = doc.createElement("guardian");
        parent.appendChild(guardianPerson);

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

        createAddressElements(doc, guardianPerson, guardian.getAddresses());

        List<String> telecomGuardianValues = guardian.getTelecoms();
        List<String> telecomGuardianUses = guardian.getTelecomUses();
        if (!telecomGuardianValues.isEmpty() && !telecomGuardianUses.isEmpty()) {
            for (int i = 0; i < telecomGuardianValues.size(); i++) {
                Element telecomGuardianElement = doc.createElement("telecom");
                telecomGuardianElement.setAttribute("use", telecomGuardianUses.get(i));
                telecomGuardianElement.setAttribute("value", telecomGuardianValues.get(i));
                guardianPerson.appendChild(telecomGuardianElement);
            }
        }
    }

    private static void addAuthor(Document doc, Element root, CDALDOAuthor author, LocalDateTime authorTime) {
        Element authorElement = doc.createElement("author");
        root.appendChild(authorElement);

        Element timeElement = doc.createElement("time");
        timeElement.setAttribute("value", authorTime.format(formatter));
        authorElement.appendChild(timeElement);

        Element assignedAuthorElement = doc.createElement("assignedAuthor");
        authorElement.appendChild(assignedAuthorElement);

        Element idAuthorElement = createIdElement(doc, author.getId().getOid(), author.getId().getExtensionId(), author.getId().getAssigningAuthorityName());
        assignedAuthorElement.appendChild(idAuthorElement);

        if (author.hasRegionalId()) {
            Element regionalIdAuthorElement = createIdElement(doc, author.getRegionalId().getOid(), author.getRegionalId().getExtensionId(), author.getRegionalId().getAssigningAuthorityName());
            assignedAuthorElement.appendChild(regionalIdAuthorElement);
        }

        List<String> telecomAuthorValues = author.getTelecoms();
        List<String> telecomAuthorUses = author.getTelecomUses();
        if (!telecomAuthorValues.isEmpty() && !telecomAuthorUses.isEmpty()) {
            for (int i = 0; i < telecomAuthorValues.size(); i++) {
                Element telecomElement = doc.createElement("telecom");
                telecomElement.setAttribute("use", telecomAuthorUses.get(i));
                telecomElement.setAttribute("value", telecomAuthorValues.get(i));
                assignedAuthorElement.appendChild(telecomElement);
            }
        }

        Element assignedPersonElement = doc.createElement("assignedPerson");
        assignedAuthorElement.appendChild(assignedPersonElement);
        if (author.getFirstName() != null) {
            Element nameAuthorElement = doc.createElement("name");
            assignedPersonElement.appendChild(nameAuthorElement);

            Element familyAuthorElement = doc.createElement("family");
            familyAuthorElement.setTextContent(author.getLastName());
            nameAuthorElement.appendChild(familyAuthorElement);

            Element givenAuthorElement = doc.createElement("given");
            givenAuthorElement.setTextContent(author.getFirstName());
            nameAuthorElement.appendChild(givenAuthorElement);

            Element prefixElement = doc.createElement("prefix");
            prefixElement.setTextContent(author.getPrefix());
            nameAuthorElement.appendChild(prefixElement);
        }
    }

    private static void addCustodian(Document doc, Element root, CDALDOId custodianId, String custodianOrgazationName, CDALDOAddr custodianOrganizationAddress, String custodianPhoneNumber) {
        Element custodian = doc.createElement("custodian");
        root.appendChild(custodian);

        Element assignedCustodian = doc.createElement("assignedCustodian");
        custodian.appendChild(assignedCustodian);

        Element representedCustodianOrganization = doc.createElement("representedCustodianOrganization");
        assignedCustodian.appendChild(representedCustodianOrganization);

        Element custodianElement = createIdElement(doc, custodianId.getOid(), custodianId.getExtensionId(), custodianId.getAssigningAuthorityName());
        representedCustodianOrganization.appendChild(custodianElement);

        Element custodianName = doc.createElement("name");
        custodianName.setTextContent(custodianOrgazationName);
        representedCustodianOrganization.appendChild(custodianName);

        if (custodianPhoneNumber != null) {
            Element custodianTelecom = doc.createElement("telecom");
            custodianTelecom.setAttribute("use", "HP");
            custodianTelecom.setAttribute("value", "tel:" + custodianPhoneNumber);
            representedCustodianOrganization.appendChild(custodianTelecom);
        }

        createAddressElements(doc, representedCustodianOrganization, List.of(custodianOrganizationAddress));
    }

    private static void addInformationRecipient(Document doc, Element root, CDALDOId informationRecipientId, String informationRecipientName) {
        Element informationRecipient = doc.createElement("informationRecipient");
        root.appendChild(informationRecipient);

        Element intendedRecipient = doc.createElement("intendedRecipient");
        informationRecipient.appendChild(intendedRecipient);

        Element idElement = createIdElement(doc, informationRecipientId.getOid(), informationRecipientId.getExtensionId(), informationRecipientId.getAssigningAuthorityName());
        intendedRecipient.appendChild(idElement);

        Element recipientName = doc.createElement("name");
        recipientName.setTextContent(informationRecipientName);
        intendedRecipient.appendChild(recipientName);
    }

    private static void addLegalAuthenticator(Document doc, Element root, LocalDateTime legalAuthTime, CDALDOAuthor legalAuthenticator) {
        Element legalAuthenticatorElement = doc.createElement("legalAuthenticator");
        root.appendChild(legalAuthenticatorElement);

        Element timeElement = doc.createElement("time");
        timeElement.setAttribute("value", legalAuthTime.format(formatter));
        legalAuthenticatorElement.appendChild(timeElement);

        Element signatureCode = doc.createElement("signatureCode");
        signatureCode.setAttribute("code", "S");
        legalAuthenticatorElement.appendChild(signatureCode);

        Element assignedEntity = doc.createElement("assignedEntity");
        legalAuthenticatorElement.appendChild(assignedEntity);

        Element idElement = createIdElement(doc, legalAuthenticator.getId().getOid(), legalAuthenticator.getId().getExtensionId(), legalAuthenticator.getId().getAssigningAuthorityName());
        assignedEntity.appendChild(idElement);

        if (legalAuthenticator.hasRegionalId()) {
            Element regionalIdElement = createIdElement(doc, legalAuthenticator.getRegionalId().getOid(), legalAuthenticator.getRegionalId().getExtensionId(), legalAuthenticator.getRegionalId().getAssigningAuthorityName());
            assignedEntity.appendChild(regionalIdElement);
        }

        List<String> legalTelecomValues = legalAuthenticator.getTelecoms();
        List<String> legalTelecomUses = legalAuthenticator.getTelecomUses();
        if (!legalTelecomValues.isEmpty() && !legalTelecomUses.isEmpty()) {
            for (int i = 0; i < legalTelecomValues.size(); i++) {
                Element legalTelecomElement = doc.createElement("telecom");
                legalTelecomElement.setAttribute("use", legalTelecomUses.get(i));
                legalTelecomElement.setAttribute("value", legalTelecomValues.get(i));
                assignedEntity.appendChild(legalTelecomElement);
            }
        }

        Element assignedPerson = doc.createElement("assignedPerson");
        assignedEntity.appendChild(assignedPerson);

        Element legalNameElement = doc.createElement("name");
        assignedPerson.appendChild(legalNameElement);

        Element legalFamilyElement = doc.createElement("family");
        legalFamilyElement.setTextContent(legalAuthenticator.getLastName());
        legalNameElement.appendChild(legalFamilyElement);

        Element legalGivenElement = doc.createElement("given");
        legalGivenElement.setTextContent(legalAuthenticator.getFirstName());
        legalNameElement.appendChild(legalGivenElement);

        Element legalPrefixElement = doc.createElement("prefix");
        legalPrefixElement.setTextContent(legalAuthenticator.getPrefix());
        legalNameElement.appendChild(legalPrefixElement);
    }

    private static void addParticipants(Document doc, Element root, List<CDALDOAuthor> participants) {
        for (CDALDOAuthor participant : participants) {
            Element participantElement = doc.createElement("participant");
            participantElement.setAttribute("typeCode", "REF");
            root.appendChild(participantElement);

            Element associatedEntity = doc.createElement("associatedEntity");
            associatedEntity.setAttribute("classCode", "PROV");
            participantElement.appendChild(associatedEntity);

            Element idElement = createIdElement(doc, "2.16.840.1.113883.2.9.4.3.2", participant.getId().getExtensionId(), "MEF");
            associatedEntity.appendChild(idElement);

            Element associatedPerson = doc.createElement("associatedPerson");
            associatedEntity.appendChild(associatedPerson);

            Element nameElement = doc.createElement("name");
            associatedPerson.appendChild(nameElement);

            Element givenElement = doc.createElement("given");
            givenElement.setTextContent(participant.getFirstName());
            nameElement.appendChild(givenElement);

            Element familyElement = doc.createElement("family");
            familyElement.setTextContent(participant.getLastName());
            nameElement.appendChild(familyElement);

            Element prefixElement = doc.createElement("prefix");
            prefixElement.setTextContent(participant.getPrefix());
            nameElement.appendChild(prefixElement);
        }
    }

    private static void addInFulfillmentOf(Document doc, Element root, String fulfillmentId) {
        Element inFulfillmentOf = doc.createElement("inFulfillmentOf");
        root.appendChild(inFulfillmentOf);

        Element order = doc.createElement("order");
        order.setAttribute("classCode", "ACT");
        order.setAttribute("moodCode", "RQO");
        inFulfillmentOf.appendChild(order);

        Element idElement = createIdElement(doc, "2.16.840.1.113883.2.9.4.3.9", fulfillmentId, "Ministero delle Finanze");
        order.appendChild(idElement);

        Element priorityCode = doc.createElement("priorityCode");
        priorityCode.setAttribute("code", "R");
        priorityCode.setAttribute("codeSystem", "2.16.840.1.113883.5.7");
        priorityCode.setAttribute("codeSystemName", "HL7 ActPriority");
        priorityCode.setAttribute("displayName", "Normale");
        order.appendChild(priorityCode);
    }

    private static Element createIdElement(Document doc, String root, String extension, String assigningAuthorityName) {
        Element idElement = doc.createElement("id");
        idElement.setAttribute("root", root);
        idElement.setAttribute("extension", extension);
        if (assigningAuthorityName != null) {
            idElement.setAttribute("assigningAuthorityName", assigningAuthorityName);
        }
        return idElement;
    }

    private static String formatEffectiveTime(LocalDateTime effectiveTimeDate) {
        return effectiveTimeDate.atZone(ZoneId.systemDefault()).format(effectiveTimeFormatter);
    }

    private static String formatEffectiveTime(LocalDate birthDate) {
        return birthDate.atStartOfDay(ZoneId.systemDefault()).format(effectiveTimeFormatter);
    }


    public static void addComponentOf(Document doc, Element root, String ramoAziendale, String numeroNosologico, String nomeAzienda,LocalDateTime lowTime,LocalDateTime highTime, List<String> repartoIds, List<String> repartoNames,List<String> ministerialCodes, List<String> facilityNames, List<String> facilityTelecoms) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        if (root == null) {
            throw new IllegalArgumentException("Root element cannot be null");
        }
        if (ramoAziendale == null || ramoAziendale.isEmpty()) {
            throw new IllegalArgumentException("Ramo aziendale cannot be null or empty");
        }
        if (numeroNosologico == null || numeroNosologico.isEmpty()) {
            throw new IllegalArgumentException("Numero nosologico cannot be null or empty");
        }
        if (nomeAzienda == null || nomeAzienda.isEmpty()) {
            throw new IllegalArgumentException("Nome azienda cannot be null or empty");
        }
        if(lowTime== null || highTime==null){
            throw new IllegalArgumentException("time is not specified");
        }
        if(repartoIds.isEmpty()||repartoNames.isEmpty()){
            throw new IllegalArgumentException("no repartos is specified");
    }
    
        Element componentOf = doc.createElement("componentOf");
        root.appendChild(componentOf);
    
        Element encompassingEncounter = doc.createElement("encompassingEncounter");
        componentOf.appendChild(encompassingEncounter);
    
        Element idElement = createIdElement(doc, "2.16.840.1.113883.2.9.2." + ramoAziendale + ".4.6", numeroNosologico, nomeAzienda);
        encompassingEncounter.appendChild(idElement);
        addEffectiveTime(doc, encompassingEncounter, lowTime, highTime);
        addLocations(doc, root, repartoIds, repartoNames,ministerialCodes,facilityNames,facilityTelecoms);

    }



    public static void addLocations(Document doc, Element root, List<String> repartoIds, List<String> repartoNames, List<String> ministerialCodes, List<String> facilityNames, List<String> facilityTelecoms) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        if (root == null) {
            throw new IllegalArgumentException("Root element cannot be null");
        }
        if (repartoIds == null || repartoNames == null || repartoIds.size() != repartoNames.size()) {
            throw new IllegalArgumentException("Reparto IDs and names must be non-null and of the same size");
        }
        if (ministerialCodes == null || facilityNames == null || facilityTelecoms == null ||
            ministerialCodes.size() != repartoIds.size() || facilityNames.size() != repartoIds.size() || facilityTelecoms.size() != repartoIds.size()) {
            throw new IllegalArgumentException("Ministerial codes, facility names, and telecoms must be non-null and match the size of reparto IDs");
        }
    
        for (int i = 0; i < repartoIds.size(); i++) {
            Element location = doc.createElement("location");
            root.appendChild(location);
    
            Element healthCareFacility = doc.createElement("healthCareFacility");
            location.appendChild(healthCareFacility);
    
            if (repartoIds.get(i) != null) {
                Element idElement = createIdElement(doc, "2.16.840.1.113883.2.9.4.1.6", repartoIds.get(i), null);
                healthCareFacility.appendChild(idElement);
            }
    
            Element locationName = doc.createElement("location");
            healthCareFacility.appendChild(locationName);
    
            if (repartoNames.get(i) != null) {
                Element nameElement = doc.createElement("name");
                nameElement.setTextContent(repartoNames.get(i));
                locationName.appendChild(nameElement);
            }
    
            if (ministerialCodes.get(i) != null) {
                Element ministerialIdElement = createIdElement(doc, "2.16.840.1.113883.2.9.4.1.2", ministerialCodes.get(i), "Ministero della Salute");
                healthCareFacility.appendChild(ministerialIdElement);
            }
    
            if (facilityNames.get(i) != null) {
                Element facilityNameElement = doc.createElement("name");
                facilityNameElement.setTextContent(facilityNames.get(i));
                healthCareFacility.appendChild(facilityNameElement);
            }
    
            if (facilityTelecoms.get(i) != null) {
                Element telecomElement = doc.createElement("telecom");
                telecomElement.setAttribute("value", "tel:" + facilityTelecoms.get(i));
                healthCareFacility.appendChild(telecomElement);
            }
        }
    }
    public static void addEffectiveTime(Document doc, Element parent, LocalDateTime lowTime, LocalDateTime highTime) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        if (parent == null) {
            throw new IllegalArgumentException("Parent element cannot be null");
        }
        if (lowTime == null || highTime == null) {
            throw new IllegalArgumentException("Low and high times cannot be null");
        }
    
        Element effectiveTime = doc.createElement("effectiveTime");
        parent.appendChild(effectiveTime);
    
        Element lowElement = doc.createElement("low");
        lowElement.setAttribute("value", formatEffectiveTime(lowTime));
        effectiveTime.appendChild(lowElement);
    
        Element highElement = doc.createElement("high");
        highElement.setAttribute("value", formatEffectiveTime(highTime));
        effectiveTime.appendChild(highElement);
    }
    private static void createAddressElements(Document doc, Element parent, List<CDALDOAddr> addresses) {
        for (CDALDOAddr addr : addresses) {
            Element addrElement = doc.createElement("addr");
            addrElement.setAttribute("use", addr.getUse());
            parent.appendChild(addrElement);
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

    private static void addDataEnterer(Document doc, Element root, CDALDOAuthor author, LocalDateTime compilerTime) {
        Element dataEnterer = doc.createElement("dataEnterer");
        dataEnterer.setAttribute("typeCode", "ENT");

        Element time = doc.createElement("time");
        time.setAttribute("value", compilerTime.format(formatter));

        Element assignedEntity = doc.createElement("assignedEntity");

        Element id = doc.createElement("id");
        id.setAttribute("root", "2.16.840.1.113883.2.9.4.3.2");
        id.setAttribute("extension", author.getId().getExtensionId());
        id.setAttribute("assignedAuthorityName", "MEF");

        Element assignedPerson = doc.createElement("assignedPerson");
        Element name = doc.createElement("name");

        Element family = doc.createElement("family");
        family.setTextContent(author.getLastName());

        Element given = doc.createElement("given");
        given.setTextContent(author.getFirstName());

        root.appendChild(dataEnterer);
        dataEnterer.appendChild(time);
        dataEnterer.appendChild(assignedEntity);
        assignedEntity.appendChild(assignedPerson);
        assignedPerson.appendChild(name);
        name.appendChild(given);
        name.appendChild(family);
        assignedEntity.appendChild(id);

    }

}
