package it.unisa.medical_docs_to_cda.CDALDO;

import org.w3c.dom.*;


public class CDALDOEntryAct implements CDALDOEntry {

    @Override
    public void createEntry(Document doc, Element section, String code, String codeSystem, String codeSystemName, String displayName) {
        Element entry = doc.createElement("entry");
        section.appendChild(entry);
    }

    

}
