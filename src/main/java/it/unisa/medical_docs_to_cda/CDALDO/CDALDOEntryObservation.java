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
public class CDALDOEntryObservation implements CDALDOEntry {
    private String code;
    private String codeSystem;
    private String codeSystemName;
    private String displayName;
    private String entryType;
    private String typeCode;
    private boolean isAnamnesi;
    private boolean statusCodeNecessary;
    private LocalDateTime effectiveTimeLow;
    private LocalDateTime effectiveTimeHigh;
    private boolean valuePresent;
    private String valueCode;
    private String valueCodeSystem;
    private String valueCodeSystemName;
    private String valueDisplayName;
    private String xsiType;
    private boolean translationNecessary;
    private boolean effectiveTimeNecessary;
    private List<CDALDOEntry> entryRelationships;

    public CDALDOEntryObservation(String code, String codeSystem, String codeSystemName, String displayName,
            String entryType, String typeCode, boolean isAnamnesi, LocalDateTime effectiveTimeLow,
            LocalDateTime effectiveTimeHigh, boolean value, String valueCode, String valueCodeSystem,
            String valueCodeSystemName, String valueDisplayName, String xsiType, boolean translationNecessary,
            boolean effectiveTimeNecessary, List<CDALDOEntry> entryRelationships) {
        if (code != null && codeSystem != null && codeSystemName != null && displayName != null && entryType != null) {
            this.code = code;
            this.codeSystem = codeSystem;
            this.codeSystemName = codeSystemName;
            this.displayName = displayName;
            this.entryType = entryType;
        } else {
            throw new IllegalArgumentException(
                    "Make sure that all the mandatory fields related to the entry observation code are present");
        }
        this.typeCode = typeCode;
        this.isAnamnesi = isAnamnesi;
        this.effectiveTimeLow = effectiveTimeLow;
        this.effectiveTimeHigh = effectiveTimeHigh;
        this.valueCode = valueCode;
        this.valueCodeSystem = valueCodeSystem;
        this.valueCodeSystemName = valueCodeSystemName;
        this.valueDisplayName = valueDisplayName;
        this.xsiType = xsiType;

        if (valueCode != null && valueCodeSystem != null && valueCodeSystemName != null && valueDisplayName != null
                && xsiType != null)
            this.valuePresent = true;

        this.translationNecessary = translationNecessary;
        if (effectiveTimeLow != null)
            this.effectiveTimeNecessary = true;
        this.entryRelationships = entryRelationships;
    }

    @Override
    public void createEntry(Document doc, Element parent) {
        if (!"entry".equals(entryType) && !"entryRelationship".equals(entryType)) {
            return; // Lancia un'eccezione o altro
        }
        Element entry = doc.createElement(entryType);
        if (entryType == "entryRelationship")
            entry.setAttribute("typeCode", typeCode);
        if ("REFR".equals(typeCode))
            entry.setAttribute("inversionInd", "'false'");

        parent.appendChild(entry);
        Element observation = doc.createElement("observation");
        observation.setAttribute("classCode", "OBS");
        observation.setAttribute("moodCode", "EVN");
        entry.appendChild(observation);
        CDALDOBuilder.addCode(doc, observation, code, codeSystem, codeSystemName, displayName);

        if (statusCodeNecessary) {
            Element statusCode = doc.createElement("statusCode");
            statusCode.setAttribute("code", "Completed");
            observation.appendChild(statusCode);
        }

        if (isAnamnesi) {
            Element effectiveTime = doc.createElement("effectiveTime");
            observation.appendChild(effectiveTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Element low = doc.createElement("low");

            if (effectiveTimeLow != null)
                low.setAttribute("value", this.effectiveTimeLow.format(formatter));
            else
                low.setAttribute("@nullFlavour", "UNK");

            effectiveTime.appendChild(low);
            Element high = doc.createElement("high");
            if (effectiveTimeHigh != null)
                high.setAttribute("value", this.effectiveTimeHigh.format(formatter));
            else
                high.setAttribute("value", "Not available yet");

            effectiveTime.appendChild(high);
        } else {
            if (effectiveTimeNecessary) {
                Element effectiveTime = doc.createElement("effectiveTime");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssZZZZ")
                        .withZone(ZoneId.systemDefault());
                if (this.effectiveTimeHigh != null) { // low da controllare nel costruttore
                    observation.appendChild(effectiveTime);
                    Element low = doc.createElement("low");
                    low.setAttribute("value", this.effectiveTimeLow.format(formatter));
                    Element high = doc.createElement("high");
                    high.setAttribute("value", this.effectiveTimeHigh.format(formatter));
                    effectiveTime.appendChild(low);
                    effectiveTime.appendChild(high);
                } else {
                    effectiveTime.setAttribute("value", this.effectiveTimeLow.format(formatter));
                    observation.appendChild(effectiveTime);
                }
            }
        }

        if (valuePresent)
            CDALDOBuilder.addValue(doc, observation, this.valueCode, this.valueCodeSystem, this.valueCodeSystemName,
                    this.valueDisplayName, this.xsiType);

        if (translationNecessary)
            CDALDOBuilder.addTranslation(doc, observation, code, codeSystem, codeSystemName, displayName);

        if (!entryRelationships.isEmpty()) {
            for (CDALDOEntry entryRelationship : entryRelationships) {
                entryRelationship.createEntry(doc, observation);
            }
        }
    }
}