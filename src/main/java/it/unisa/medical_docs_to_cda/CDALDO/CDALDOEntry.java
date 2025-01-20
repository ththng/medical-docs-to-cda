package it.unisa.medical_docs_to_cda.CDALDO;

import org.w3c.dom.*;


public interface CDALDOEntry {

    public void createEntry(Document doc, Element section, String code, String codeSystem, String codeSystemName, String displayName);


}
