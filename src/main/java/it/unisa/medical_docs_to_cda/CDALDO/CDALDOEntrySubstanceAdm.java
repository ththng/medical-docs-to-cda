package it.unisa.medical_docs_to_cda.CDALDO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CDALDOEntrySubstanceAdm implements CDALDOEntry {

    private String entryType;
    private String medicineCode;
    private String medicineDescription;
    private String ref;
    private String statusCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private String routeCode;
    private String routeCodeSystem;
    private String routeCodeSystemName;
    private String routeDisplayName;
    private String approachSiteCode;
    private String approachSiteCodeSystem;
    private String approachSiteCodeSystemName;
    private String approachSiteDisplayName;
    private String doseQuantityMin;
    private String doseQuantityMax;
    private String doseQuantityUnit;
    private String rateQuantityMin;
    private String rateQuantityMax;
    private String rateQuantityUnit;
    private List<CDALDOAuthor> performers;
    private List<CDALDOAuthor> participants;
    private List<CDALDOEntryObservation> observations;

    public CDALDOEntrySubstanceAdm(String entryType, String medicineCode, String medicineDescription,
            String statusCode, LocalDate startDate, LocalDate endDate) {
        if (entryType == null || medicineCode == null || medicineDescription == null || statusCode == null) {
            throw new IllegalArgumentException("Missing field in CDALDOEntrySubstanceAdm");
        } else {
            this.entryType = entryType;
            this.medicineCode = medicineCode;
            this.medicineDescription = medicineDescription;
            this.statusCode = statusCode;
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public CDALDOEntrySubstanceAdm(String entryType, String medicineCode, String medicineDescription, String ref,
            String statusCode, LocalDate startDate, LocalDate endDate, String routeCode, String routeDisplayName,
            String approachSiteCode, String approachSiteCodeSystem,
            String approachSiteCodeSystemName, String approachSiteDisplayName, String doseQuantityMin,
            String doseQuantityMax, String doseQuantityUnit, String rateQuantityMin, String rateQuantityMax,
            String rateQuantityUnit, List<CDALDOAuthor> performers, List<CDALDOAuthor> participants,
            List<CDALDOEntryObservation> observations) {

        StringBuilder missingFields = new StringBuilder();

        if (entryType == null) {
            missingFields.append("entryType, ");
        }
        if (medicineCode == null) {
            missingFields.append("medicineCode, ");
        }
        if (medicineDescription == null) {
            missingFields.append("medicineDescription, ");
        }
        if (statusCode == null || !Arrays.asList("active", "suspended", "completed", "aborted").contains(statusCode)) {
            missingFields.append("statusCode, ");
        }

        if (missingFields.length() > 0) {
            missingFields.setLength(missingFields.length() - 2);
            throw new IllegalArgumentException("Missing mandatory fields: " + missingFields.toString());
        } else {
            this.entryType = entryType;
            this.medicineCode = medicineCode;
            this.medicineDescription = medicineDescription;
            this.statusCode = statusCode;

        }

        this.ref = ref;
        this.startDate = startDate;
        this.endDate = endDate;
        this.routeCode = routeCode;
        this.routeCodeSystem = "2.16.840.1.113883.5.112";
        this.routeCodeSystemName = "HL7 RouteOfAdministration";
        this.routeDisplayName = routeDisplayName;
        this.approachSiteCode = approachSiteCode;
        this.approachSiteCodeSystem = approachSiteCodeSystem;
        this.approachSiteCodeSystemName = approachSiteCodeSystemName;
        this.approachSiteDisplayName = approachSiteDisplayName;
        this.doseQuantityMin = doseQuantityMin;
        this.doseQuantityMax = doseQuantityMax;
        this.doseQuantityUnit = doseQuantityUnit;
        this.rateQuantityMin = rateQuantityMin;
        this.rateQuantityMax = rateQuantityMax;
        this.rateQuantityUnit = rateQuantityUnit;
        this.performers = performers;
        this.participants = participants;
        this.observations = observations;
    }

    @Override
    public void createEntry(Document doc, Element section) {
        if (entryType != "entry" && entryType != "entryRelationship") {
            return;
        } else {
            Element entry = doc.createElement(this.entryType);
            section.appendChild(entry);

            // Tag substanceAdministration
            Element substanceAdministration = doc.createElement("substanceAdministration");
            substanceAdministration.setAttribute("classCode", "SBADM");
            substanceAdministration.setAttribute("moodCode", "EVN");
            entry.appendChild(substanceAdministration);

            // Tag consumable
            Element consumable = doc.createElement("consumable");
            substanceAdministration.appendChild(consumable);

            // Tag manufacturedProduct
            Element manufactured = doc.createElement("manufacturedProduct");
            manufactured.setAttribute("classCode", "MANU");
            consumable.appendChild(manufactured);
            Element manufactMaterial = doc.createElement("manufacturedMaterial");
            manufactured.appendChild(manufactMaterial);
            CDALDOBuilder.addCode(doc, manufactMaterial, medicineCode, "2.16.840.1.113883.2.9.6.1.5", "AIC",
                    medicineDescription);

            // Tag text and reference with URI (optional)
            if (this.ref != null) {
                Element text = doc.createElement("text");
                substanceAdministration.appendChild(text);
                Element reference = doc.createElement("reference");
                reference.setAttribute("value", ref);
                text.appendChild(reference);
            }

            // Tag statusCode
            Element statusCode = doc.createElement("statusCode");
            statusCode.setAttribute("code", this.statusCode);
            substanceAdministration.appendChild(statusCode);

            // Tag effectiveTime
            Element effectiveTime = doc.createElement("effectiveTime");
            substanceAdministration.appendChild(effectiveTime);
            Element low = doc.createElement("low");
            Element high = doc.createElement("high");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                    .withZone(ZoneId.systemDefault());
            if (startDate == null) {
                low.setAttribute("nullFlavor", "UNK");
                effectiveTime.appendChild(low);
            } else {
                low.setAttribute("value", startDate.format(formatter));
                if (this.statusCode.equals("completed") || this.statusCode.equals("aborted")) {
                    if (this.endDate != null) {
                        high.setAttribute("value", endDate.format(formatter));
                    } else {
                        high.setAttribute("nullFlavor", "UNK");
                    }
                    effectiveTime.appendChild(high);
                }
                effectiveTime.appendChild(low);
            }

            // Tag routeCode (optional)
            if (this.routeCode != null) {
                Element route = doc.createElement("routeCode");
                route.setAttribute("code", this.routeCode);
                route.setAttribute("codeSystem", routeCodeSystem);
                route.setAttribute("codeSystemName", routeCodeSystemName);
                route.setAttribute("displayName", routeDisplayName);
                substanceAdministration.appendChild(route);
            }

            // Tag approachSiteCode (optional)
            if (this.approachSiteCode != null) {
                Element approachSite = doc.createElement("approachSiteCode");
                approachSite.setAttribute("code", approachSiteCode);
                approachSite.setAttribute("codeSystem", approachSiteCodeSystem);
                approachSite.setAttribute("codeSystemName", approachSiteCodeSystemName);
                approachSite.setAttribute("displayName", approachSiteDisplayName);
                substanceAdministration.appendChild(approachSite);
            }

            // Tag doseQuantity (optional)
            if (this.doseQuantityMin != null) {
                Element doseQuantity = doc.createElement("doseQuantity");
                substanceAdministration.appendChild(doseQuantity);
                Element lowQuantity = doc.createElement("low");
                Element highQuantity = doc.createElement("high");
                lowQuantity.setAttribute("value", this.doseQuantityMin);
                lowQuantity.setAttribute("unit", this.doseQuantityUnit);
                if (this.doseQuantityMax != doseQuantityMin || this.doseQuantityMax != null) {
                    highQuantity.setAttribute("value", doseQuantityMax);
                    highQuantity.setAttribute("unit", doseQuantityUnit);
                } else {
                    highQuantity.setAttribute("value", doseQuantityMin);
                    highQuantity.setAttribute("unit", doseQuantityUnit);
                }
                doseQuantity.appendChild(lowQuantity);
                doseQuantity.appendChild(highQuantity);
            }

            // Tag rateQuantity (optional)
            if (this.rateQuantityMin != null) {
                Element rateQuantity = doc.createElement("rateQuantity");
                substanceAdministration.appendChild(rateQuantity);
                Element lowQuantity = doc.createElement("low");
                Element highQuantity = doc.createElement("high");
                lowQuantity.setAttribute("value", this.rateQuantityMin);
                lowQuantity.setAttribute("unit", this.rateQuantityUnit);
                if (this.rateQuantityMax != rateQuantityMin || this.rateQuantityMax != null) {
                    highQuantity.setAttribute("value", rateQuantityMax);
                } else {
                    highQuantity.setAttribute("value", rateQuantityMin);
                }
                highQuantity.setAttribute("unit", rateQuantityUnit);
                rateQuantity.appendChild(lowQuantity);
                rateQuantity.appendChild(highQuantity);
            }

            // Tag performer (optional)
            if (performers != null && !(performers.isEmpty())) {
                for (CDALDOAuthor perform : performers) {
                    Element performer = doc.createElement("performer");
                    substanceAdministration.appendChild(performer);
                    if (startDate != null) {
                        Element time = doc.createElement("time");
                        time.setAttribute("value", startDate.format(formatter));
                        performer.appendChild(time);
                    }
                    Element assignedEntity = doc.createElement("assignedEntity");
                    performer.appendChild(assignedEntity);
                    Element id = doc.createElement("id");
                    id.setAttribute("root", perform.getId().getOid());
                    id.setAttribute("extension", perform.getId().getExtensionId());
                    id.setAttribute("assigningAuthorityName", perform.getId().getAssigningAuthorityName());
                    assignedEntity.appendChild(id);
                    Element assignedPerson = doc.createElement("assignedPerson");
                    assignedEntity.appendChild(assignedPerson);
                    Element name = doc.createElement("name");
                    assignedPerson.appendChild(name);
                    Element family = doc.createElement("family");
                    family.setTextContent(perform.getLastName());
                    Element given = doc.createElement("given");
                    given.setTextContent(perform.getFirstName());
                    name.appendChild(family);
                    name.appendChild(given);
                }
            }

            // Tag participant (optional)
            if (participants != null && !(participants.isEmpty())) {
                for (CDALDOAuthor particip : participants) {
                    Element participant = doc.createElement("participant");
                    substanceAdministration.appendChild(participant);
                    if (startDate != null) {
                        Element time = doc.createElement("time");
                        time.setAttribute("value", startDate.format(formatter));
                        participant.appendChild(time);
                    }
                    Element participantRole = doc.createElement("participantRole");
                    participant.appendChild(participantRole);
                    Element id = doc.createElement("id");
                    id.setAttribute("root", particip.getId().getOid());
                    id.setAttribute("extension", particip.getId().getExtensionId());
                    id.setAttribute("assigningAuthorityName", particip.getId().getAssigningAuthorityName());
                    participantRole.appendChild(id);
                    Element playingEntity = doc.createElement("playingEntity");
                    participantRole.appendChild(playingEntity);
                    Element name = doc.createElement("name");
                    playingEntity.appendChild(name);
                    Element family = doc.createElement("family");
                    family.setTextContent(particip.getLastName());
                    Element given = doc.createElement("given");
                    given.setTextContent(particip.getFirstName());
                    name.appendChild(family);
                    name.appendChild(given);
                }
            }

            // Tags entryRelationship
            if (observations != null && (!observations.isEmpty())) {
                for (CDALDOEntryObservation observation : observations) {
                    observation.setEntryType("entryRelationship");
                    observation.setTypeCode("COMP");
                    observation.setXsiType("REAL");
                    observation.createEntry(doc, substanceAdministration);
                }
            }
        }
    }
}
