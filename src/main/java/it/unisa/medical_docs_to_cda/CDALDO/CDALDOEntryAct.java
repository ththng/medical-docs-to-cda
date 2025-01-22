package it.unisa.medical_docs_to_cda.CDALDO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.w3c.dom.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class CDALDOEntryAct implements CDALDOEntry {

    private String entryType;
    private String statusCode;
    private LocalDate effectiveTimeLow;
    private LocalDate effectiveTimeHigh;
    private CDALDOEntryObservation entryObservation;
    private String allergyDisplayName;
    private String allergyValueCode;
    private LocalDateTime allergyStartDate;
    private LocalDateTime allergyEndDate;
    private List<CDALDOEntry> entryRelationships;

    public CDALDOEntryAct(String entryType, String statusCode, LocalDate effectiveTimeLow, LocalDate effectiveTimeHigh,
            String allergyValueCode, String allergyDisplayName, LocalDateTime allergyStartDate,
            LocalDateTime allergyEndDate,
            List<CDALDOEntry> entryRelationships) {

        StringBuilder missingFields = new StringBuilder();

        if (entryType == null) {
            missingFields.append("entryType, ");
        }
        if (statusCode == null || !Arrays.asList("active", "suspended", "completed", "aborted").contains(statusCode)) {
            missingFields.append("statusCode, ");
        }

        if (statusCode != null && (statusCode.equals("completed") || statusCode.equals("aborted"))
                && effectiveTimeHigh == null) {
            missingFields.append("effectiveTimeHigh, ");
        }

        if (missingFields.length() > 0) {
            missingFields.setLength(missingFields.length() - 2);
            throw new IllegalArgumentException("Missing mandatory fields: " + missingFields.toString());
        } else {
            this.entryType = entryType;
            this.statusCode = statusCode;
            this.effectiveTimeHigh = effectiveTimeHigh;
        }

        this.allergyValueCode = allergyValueCode;
        this.allergyDisplayName = allergyDisplayName;
        this.allergyStartDate = allergyStartDate;
        this.allergyEndDate = allergyEndDate;
        this.effectiveTimeLow = effectiveTimeLow;
        this.entryRelationships = entryRelationships;
        this.entryObservation = new CDALDOEntryObservation("52473-6", "2.16.840.1.113883.6.1",
                "LOINC", "Allergia o causa della reazione", "entryRelationship", "SUBJ",
                false, allergyStartDate, allergyEndDate, true, allergyValueCode, "2.16.840.1.113883.5.4",
                "ObservationIntoleranceType", allergyDisplayName, "CD", false, true, entryRelationships);

    }

    @Override
    public void createEntry(Document doc, Element section) {
        if (entryType != "entry" || entryType != "entryRelationship") {
            return;
        } else {
            Element entry = doc.createElement(entryType);
            section.appendChild(entry);

            // Tag act
            Element act = doc.createElement("act");
            act.setAttribute("classCode", "ACT");
            act.setAttribute("moodCode", "EVN");
            entry.appendChild(act);

            // Tag code
            Element code = doc.createElement("code");
            code.setAttribute("nullFlavor", "NA");
            act.appendChild(code);

            // Tag statusCode. Status: active, suspended, aborted, completed
            Element statusCode = doc.createElement("statusCode");
            statusCode.setAttribute("code", this.statusCode);
            act.appendChild(statusCode);

            // Tag effectiveTime
            Element effectiveTime = doc.createElement("effectiveTime");
            act.appendChild(effectiveTime);
            Element low = doc.createElement("low");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                    .withZone(ZoneId.systemDefault());
            if (this.effectiveTimeLow == null) {
                low.setAttribute("nullFlavor", "UNK");
            } else {
                low.setAttribute("value", this.effectiveTimeLow.format(formatter));
            }

            if (this.effectiveTimeHigh != null) {
                Element high = doc.createElement("high");
                high.setAttribute("value", this.effectiveTimeHigh.format(formatter));
            }

            // Tag entryRelationship with observation
            if (this.entryObservation != null) {
                entryObservation.createEntry(doc, act);
            }

            if (this.entryRelationships != null && (!entryRelationships.isEmpty())) {
                // Tags entryRelationship inside the first entryRelationship
                for (CDALDOEntry entryRel : this.entryRelationships) {
                    entryRel.createEntry(doc, (Element) act.getElementsByTagName("entryRelationship").item(0));
                }
            }

        }
    }

}
