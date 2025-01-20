package it.unisa.medical_docs_to_cda.CDALDO;

import org.w3c.dom.*;


public class CDALDOEntryProcedure implements CDALDOEntry {

    private String statusProcedure;

    @Override
    public void createEntry(Document doc, Element section, String code, String codeSystem, String codeSystemName, String displayName) {
        Element entry = doc.createElement("entry");
        section.appendChild(entry);
        Element procedure = doc.createElement("procedure");
        procedure.setAttribute("classCode", "PROC");
        procedure.setAttribute("moodCode", "EVN");
        entry.appendChild(procedure);
        CDALDOBuilder.addCode(doc, procedure, code, codeSystem, codeSystemName, displayName);
        Element statusCode = doc.createElement("statusCode");
        statusCode.setAttribute("code", this.statusProcedure);
        procedure.appendChild(statusCode);
        
    }


}
