package it.unisa.medical_docs_to_cda.CDALDO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.w3c.dom.*;
import lombok.Getter;

@Getter
public class CDALDOEntryObservation implements CDALDOEntry {
    private String valueCode;
    private String valueCodeSystem;
    private String valueCodeSystemName;
    private String valueDisplayName;
    private String xsiType;
    private LocalDateTime effectiveTime;

    @Override
    public void createEntry(Document doc, Element section, String code, String codeSystem, String codeSystemName, String displayName) {
        Element entry = doc.createElement("entry");
        section.appendChild(entry);
        Element observation = doc.createElement("observation");
        observation.setAttribute("classCode", "OBS");
        observation.setAttribute("moodCode", "EVN");
        entry.appendChild(observation);
        CDALDOBuilder.addCode(doc, observation, code, codeSystem, codeSystemName, displayName);
        CDALDOBuilder.addValue(doc, observation, this.valueCode, this.valueCodeSystem, this.valueCodeSystemName, this.valueDisplayName, this.xsiType);
        Element effectiveTime = doc.createElement("effectiveTime"); 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssZZZZ")
            .withZone(ZoneId.systemDefault());
        effectiveTime.setAttribute("value", this.effectiveTime.format(formatter)); 
        
    }

    

}
