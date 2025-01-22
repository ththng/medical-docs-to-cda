package it.unisa.medical_docs_to_cda.CDALDO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CDALDOEntrySubstanceAdm implements CDALDOEntry {
    private String code;
    private String codeSystem;
    private String codeSystemName;
    private String displayName;
    @Override
    public void createEntry(Document doc, Element section) {
        Element entry = doc.createElement("entry");
        section.appendChild(entry);
    }

   

}
