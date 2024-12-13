package it.unisa.medical_docs_to_cda.CDALDO;

import org.w3c.dom.*;


public class CDALDOEntryProcedure extends CDALDOEntry {

    private String statusProcedure;

    @Override
    public void createEntry(Document doc, Element section, String code, String codeSystem, String codeSystemName, String displayName) {
        // TODO Auto-generated method stub
        super.createEntry(doc, section, code, codeSystem, codeSystemName, displayName);
        Element procedure = doc.createElement("procedure");
        procedure.setAttribute("classCode", "PROC");
        procedure.setAttribute("moodCode", "EVN");
        section.appendChild(procedure);
        CDALDOBuilder.addCode(doc, procedure, code, codeSystem, codeSystemName, displayName);
        Element statusCode = doc.createElement("statusCode");
        statusCode.setAttribute("code", this.statusProcedure);
        procedure.appendChild(statusCode);
        
    }


}
