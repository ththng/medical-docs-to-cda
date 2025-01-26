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
    private LocalDateTime dtEsecuzione;
    private LocalDateTime dtRichiesta;
    private boolean valuePresent;
    private String valueCode;
    private String valueCodeSystem;
    private String valueCodeSystemName;
    private String valueDisplayName;
    private String xsiType;
    private String outcome;
    private float value;
    private String unit;
    private boolean translationNecessary;
    private boolean effectiveTimeNecessary;
    private List<CDALDOEntry> entryRelationships;
    private List<CDALDOAuthor> performers;
    private List<CDALDOAuthor> participants;
    private List<CDALDOAgent> agents;

    // for all the others
    public CDALDOEntryObservation(String code, String codeSystem, String codeSystemName, String displayName,
            String entryType, String typeCode, boolean isAnamnesi, LocalDateTime effectiveTimeLow,
            LocalDateTime effectiveTimeHigh, LocalDateTime dtEsecuzione, LocalDateTime dtRichiesta, String xsiType,
            String valueCode,
            String valueCodeSystem, String valueCodeSystemName, String valueDisplayName,
            boolean translationNecessary,
            List<CDALDOEntry> entryRelationships, List<CDALDOAuthor> performers, List<CDALDOAuthor> participants) {
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

        this.isAnamnesi = isAnamnesi;
        if (isAnamnesi) {
            this.statusCodeNecessary = true;
        }
        this.xsiType = xsiType;
        this.typeCode = typeCode;

        if (xsiType.equals("CD")) {
            if (valueDisplayName == null) {
                throw new IllegalArgumentException(
                        "For the CD xsi:type, valueCode and valueDisplayName cannot be null");
            } else {
                this.valuePresent = true;
                this.valueCode = valueCode;
                this.valueCodeSystem = valueCodeSystem;
                this.valueCodeSystemName = valueCodeSystemName;
                this.valueDisplayName = valueDisplayName;
            }
        }

        this.effectiveTimeLow = effectiveTimeLow;
        this.effectiveTimeHigh = effectiveTimeHigh;
        this.dtEsecuzione = dtEsecuzione;
        this.dtRichiesta = dtRichiesta;

        this.translationNecessary = translationNecessary;
        if (effectiveTimeLow != null) {
            this.effectiveTimeNecessary = true;
        }
        this.entryRelationships = entryRelationships;
        this.performers = performers;
        this.participants = participants;
    }

    // for 4.6 and 4.7
    public CDALDOEntryObservation(String code, String codeSystem, String codeSystemName, String displayName,
            String entryType, String typeCode, boolean isAnamnesi, LocalDateTime effectiveTimeLow,
            LocalDateTime effectiveTimeHigh, LocalDateTime dtEsecuzione, LocalDateTime dtRichiesta, String xsiType,
            String outcome, boolean translationNecessary,
            List<CDALDOEntry> entryRelationships, List<CDALDOAuthor> performers, List<CDALDOAuthor> participants) {
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

        this.isAnamnesi = isAnamnesi;
        if (isAnamnesi) {
            this.statusCodeNecessary = true;
        }
        this.xsiType = xsiType;
        this.typeCode = typeCode;

        if ("ST".equals(xsiType) && outcome == null) {
            throw new IllegalArgumentException(
                    "For the ST xsi:type, the outcome cannot be null");
        } else {
            this.valuePresent = true;
            this.outcome = outcome;
        }

        this.effectiveTimeLow = effectiveTimeLow;
        this.effectiveTimeHigh = effectiveTimeHigh;
        this.dtEsecuzione = dtEsecuzione;
        this.dtRichiesta = dtRichiesta;

        this.translationNecessary = translationNecessary;
        if (effectiveTimeLow != null) {
            this.effectiveTimeNecessary = true;
        }
        this.entryRelationships = entryRelationships;
        this.performers = performers;
        this.participants = participants;
    }

    // For substanceAdministration
    public CDALDOEntryObservation(String code, String codeSystem, String codeSystemName, String displayName,
            String entryType, String typeCode, boolean isAnamnesi, LocalDateTime effectiveTimeLow,
            LocalDateTime effectiveTimeHigh, LocalDateTime dtEsecuzione, LocalDateTime dtRichiesta,
            String xsiType, float value,
            String unit, boolean translationNecessary,
            List<CDALDOEntry> entryRelationships) {

        if (code != null && codeSystem != null && codeSystemName != null /*&& displayName != null*/ && entryType != null) {
            this.code = code;
            this.codeSystem = codeSystem;
            this.codeSystemName = codeSystemName;
            this.displayName = displayName;
            this.entryType = entryType;

        } else {
            throw new IllegalArgumentException(
                    "Make sure that all the mandatory fields related to the entry observation code are present");
        }

        this.isAnamnesi = isAnamnesi;
        if (isAnamnesi) {
            this.statusCodeNecessary = true;
        }
        this.typeCode = typeCode;
        this.xsiType = xsiType;
        this.value = value;
        this.unit = unit;
        this.effectiveTimeLow = effectiveTimeLow;
        this.effectiveTimeHigh = effectiveTimeHigh;
        this.dtEsecuzione = dtEsecuzione;
        this.dtRichiesta = dtRichiesta;

        this.translationNecessary = translationNecessary;
        if (effectiveTimeLow != null) {
            this.effectiveTimeNecessary = true;
        }
        this.entryRelationships = entryRelationships;
    }

    // for Allergies
    public CDALDOEntryObservation(String code, String codeSystem, String codeSystemName, String displayName,
            String entryType, String typeCode, boolean isAnamnesi, LocalDateTime effectiveTimeLow,
            LocalDateTime effectiveTimeHigh, LocalDateTime dtEsecuzione, LocalDateTime dtRichiesta, String valueCode,
            String valueCodeSystem, String valueCodeSystemName, String valueDisplayName,
            String xsiType, boolean translationNecessary,
            List<CDALDOEntry> entryRelationships, List<CDALDOAgent> agents) {

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

        this.isAnamnesi = isAnamnesi;
        this.typeCode = typeCode;
        if (isAnamnesi) {
            this.statusCodeNecessary = true;
        }
        this.xsiType = xsiType;

        if (xsiType.equals("CD")) {
            if (valueCode == null || valueCodeSystem == null || valueCodeSystemName == null
                    || valueDisplayName == null) {
                throw new IllegalArgumentException(
                        "For the CD xsi:type, valueCode, valueCodeSystem, valueCodeSystemName, and valueDisplayName cannot be null");
            } else {
                this.valuePresent = true;
                this.valueCode = valueCode;
                this.valueCodeSystem = valueCodeSystem;
                this.valueCodeSystemName = valueCodeSystemName;
                this.valueDisplayName = valueDisplayName;
            }
        }
        this.effectiveTimeLow = effectiveTimeLow;
        this.effectiveTimeHigh = effectiveTimeHigh;
        this.dtEsecuzione = dtEsecuzione;
        this.dtRichiesta = dtRichiesta;

        this.translationNecessary = translationNecessary;
        if (effectiveTimeLow != null) {
            this.effectiveTimeNecessary = true;
        }
        this.entryRelationships = entryRelationships;
        this.agents = agents;
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
            statusCode.setAttribute("code", "completed");
            observation.appendChild(statusCode);
        }

        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (isAnamnesi) {
            Element effectiveTime = doc.createElement("effectiveTime");
            observation.appendChild(effectiveTime);
            Element low = doc.createElement("low");

            if (effectiveTimeLow != null)
                low.setAttribute("value", this.effectiveTimeLow.format(formatterDate));
            else
                low.setAttribute("@nullFlavour", "UNK");

            effectiveTime.appendChild(low);
            Element high = doc.createElement("high");
            if (effectiveTimeHigh != null)
                high.setAttribute("value", this.effectiveTimeHigh.format(formatterDate));
            else
                high.setAttribute("value", "Not available yet");

            effectiveTime.appendChild(high);
        } else {
            if (effectiveTimeNecessary) {
                Element effectiveTime = doc.createElement("effectiveTime");
                DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyyMMddHHmmssZZZZ")
                        .withZone(ZoneId.systemDefault());
                if (this.effectiveTimeHigh != null) { // low da controllare nel costruttore
                    observation.appendChild(effectiveTime);
                    Element low = doc.createElement("low");
                    low.setAttribute("value",
                            this.effectiveTimeLow.atZone(ZoneId.systemDefault()).format(formatterDateTime));
                    Element high = doc.createElement("high");
                    high.setAttribute("value",
                            this.effectiveTimeHigh.atZone(ZoneId.systemDefault()).format(formatterDateTime));
                    effectiveTime.appendChild(low);
                    effectiveTime.appendChild(high);
                } else {
                    effectiveTime.setAttribute("value",
                            this.effectiveTimeLow.atZone(ZoneId.systemDefault()).format(formatterDateTime));
                    observation.appendChild(effectiveTime);
                }
            }
        }

        if (valuePresent)
            CDALDOBuilder.addValue(doc, observation, valueCode, valueCodeSystem, valueCodeSystemName,
                    valueDisplayName, xsiType, outcome, value, unit);

        // Tag performer (optional)
        if (performers != null && !(performers.isEmpty())) {
            for (CDALDOAuthor perform : performers) {
                Element performer = doc.createElement("performer");
                observation.appendChild(performer);
                if (dtEsecuzione != null) {
                    Element time = doc.createElement("time");
                    time.setAttribute("value", dtEsecuzione.format(formatterDate));
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

        // Tag participant (optional) BUT ID AND NAME ARE MANDATORY !!!!!
        if (participants != null && !(participants.isEmpty())) {
            for (CDALDOAuthor particip : participants) {
                Element participant = doc.createElement("participant");
                observation.appendChild(participant);
                if (dtRichiesta != null) {
                    Element time = doc.createElement("time");
                    time.setAttribute("value", dtRichiesta.format(formatterDate));
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

        // Participant for Allergies
        if (this.typeCode != null && typeCode.equals("SUBJ")) {
            for (CDALDOAgent agent : agents) {
                Element participant = doc.createElement("participant");
                participant.setAttribute("typeCode", "CSM");
                observation.appendChild(participant);
                Element participantRole = doc.createElement("participantRole");
                participantRole.setAttribute("classCode", "MANU");
                participant.appendChild(participantRole);
                Element playingEntity = doc.createElement("playingEntity");
                playingEntity.setAttribute("classCode", "MMAT");
                participantRole.appendChild(playingEntity);
                Element code = doc.createElement("code");
                if (agent.getCode() != null) {
                    code.setAttribute("code", agent.getCode());
                    code.setAttribute("codeSystemName", agent.getCodeSystemName());
                    code.setAttribute("codeSystem", agent.getCodeSystem());
                    code.setAttribute("displayName", agent.getDisplayName());
                } else {
                    code.setAttribute("nullFlavor", "UNK");
                }
                playingEntity.appendChild(code);
            }
        }

        if (translationNecessary)
            CDALDOBuilder.addTranslation(doc, observation, code, codeSystem, codeSystemName, displayName);

        if (entryRelationships != null && (!entryRelationships.isEmpty())) {
            for (CDALDOEntry entryRelationship : entryRelationships) {
                entryRelationship.createEntry(doc, observation);
            }
        }

    }
}
