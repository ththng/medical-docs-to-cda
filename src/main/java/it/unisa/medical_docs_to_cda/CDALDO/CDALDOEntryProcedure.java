package it.unisa.medical_docs_to_cda.CDALDO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.w3c.dom.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CDALDOEntryProcedure implements CDALDOEntry {

    private String code;
    private String codeSystem;
    private String codeSystemName;
    private String displayName;
    private String entryType;
    private String typeCode;
    private String statusProcedure;
    private boolean isEffectiveTime = false;
    private LocalDateTime effectiveTimeLow;
    private LocalDateTime effectiveTimeHigh;
    private String refProcedure;
    private List<CDALDOEntryObservation> relationshipEntries;

    public CDALDOEntryProcedure(String code, String codeSystem, String codeSystemName, String displayName,
            String entryType, String typeCode, LocalDateTime effectiveTimeLow,
            LocalDateTime effectiveTimeHigh, String refProcedure, List<CDALDOEntryObservation> relationshipEntries) {

        StringBuilder missingFields = new StringBuilder();

        if (code == null) {
            missingFields.append("code, ");
        }
        if (codeSystem == null) {
            missingFields.append("codeSystem, ");
        }
        if (codeSystemName == null) {
            missingFields.append("codeSystemName, ");
        }
        if (displayName == null) {
            missingFields.append("displayName, ");
        }
        if (entryType == null) {
            missingFields.append("entryType, ");
        }

        if (missingFields.length() > 0) {
            missingFields.setLength(missingFields.length() - 2);
            throw new IllegalArgumentException("Missing mandatory fields: " + missingFields.toString());
        } else {
            this.code = code;
            this.codeSystem = codeSystem;
            this.codeSystemName = codeSystemName;
            this.displayName = displayName;
            this.entryType = entryType;
        }

        this.typeCode = typeCode;
        this.statusProcedure = "completed";
        this.effectiveTimeLow = effectiveTimeLow;
        this.effectiveTimeHigh = effectiveTimeHigh;
        this.refProcedure = refProcedure;
        this.relationshipEntries = relationshipEntries;

        if (effectiveTimeLow != null) {
            isEffectiveTime = true;
        }

    }

    @Override
    public void createEntry(Document doc, Element section) {
        if (entryType != "entry" && entryType != "entryRelationship") {
            return;
        } else {
            // Tag entry
            Element entry = doc.createElement(this.entryType);
            if (entryType == "entryRelationship")
                entry.setAttribute("typeCode", this.typeCode);
            section.appendChild(entry);

            // Tag procedure
            Element procedure = doc.createElement("procedure");
            procedure.setAttribute("classCode", "PROC");
            procedure.setAttribute("moodCode", "EVN");
            entry.appendChild(procedure);

            // Tag code and statusCode
            CDALDOBuilder.addCode(doc, procedure, code, codeSystem, codeSystemName, displayName);
            Element statusCode = doc.createElement("statusCode");
            statusCode.setAttribute("code", this.statusProcedure);
            procedure.appendChild(statusCode);

            // Tag effectiveTime
            if (isEffectiveTime) {
                Element effectiveTime = doc.createElement("effectiveTime");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssZZZZ")
                        .withZone(ZoneId.systemDefault());
                if (this.effectiveTimeHigh != null) {
                    procedure.appendChild(effectiveTime);
                    Element low = doc.createElement("low");
                    low.setAttribute("value", effectiveTimeLow.atZone(ZoneId.systemDefault()).format(formatter));
                    Element high = doc.createElement("high");
                    high.setAttribute("value", effectiveTimeHigh.atZone(ZoneId.systemDefault()).format(formatter));
    
                    effectiveTime.appendChild(low);
                    effectiveTime.appendChild(high);
                } else {
                    effectiveTime.setAttribute("value", this.effectiveTimeLow.atZone(ZoneId.systemDefault()).format(formatter));
                    procedure.appendChild(effectiveTime);
                }
            }
            // Tag text and reference
            // Riferimento incrociato alla descrizione dell’intero elemento (procedura)
            // all’interno della parte narrativa (URI).
            if (this.refProcedure != null) {
                Element text = doc.createElement("text");
                procedure.appendChild(text);
                Element reference = doc.createElement("reference");
                reference.setAttribute("value", this.refProcedure);
                text.appendChild(reference);
            }

            // Tag entryRelationship
            if (relationshipEntries != null && !this.relationshipEntries.isEmpty()) {
                for (CDALDOEntryObservation entryRelationship : this.relationshipEntries) {
                    entryRelationship.setEntryType("entryRelationship");
                    entryRelationship.setTypeCode("RSON");
                    entryRelationship.createEntry(doc, procedure);
                }
            }
        }
    }

}
