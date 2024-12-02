package it.unisa.medical_docs_to_cda.CDALDO;

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
public static void addHeader(Document doc, String oid, String extensionId, String assigningAuthorityName) {
    if (doc == null) {
        throw new IllegalArgumentException("Document cannot be null");
    }


    Element root = doc.createElement("ClinicalDocument");
    root.setAttribute("xsi:schemaLocation", "urn:hl7-org:v3 CDA.xsd");
    root.setAttribute("xmlns", "urn:hl7-org:v3");
    root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    
    doc.appendChild(root);



    Element realmCode = doc.createElement("realmCode");
    realmCode.setAttribute("code", "IT"); // Example attribute, adjust as needed
    root.appendChild(realmCode);

    Element typeId = doc.createElement("typeId");
    typeId.setAttribute("root", "2.16.840.1.113883.1.3"); // Example attribute, adjust as needed
    typeId.setAttribute("extension", "POCD_HD000040"); // Example attribute, adjust as needed
    root.appendChild(typeId);

    Element templateId = doc.createElement("templateId");
    templateId.setAttribute("root", "2.16.840.1.113883.2.9.10.1.5" );
    templateId.setAttribute("extension", "1.2"); // Example attribute, adjust as needed
    root.appendChild(templateId);

    Element id = doc.createElement("id");
    id.setAttribute("root", oid);
    id.setAttribute("extension", extensionId);
    id.setAttribute("assigningAuthorityName", assigningAuthorityName);
    root.appendChild(id);

    addCode(doc, root, "34105-7", "2.16.840.1.113883.6.1", "LOINC", "Lettera di dimissione ospedaliera");
    
    addTitle(doc, root,"LETTERA DI DIMISSIONE OSPEDALIERA");
}

}