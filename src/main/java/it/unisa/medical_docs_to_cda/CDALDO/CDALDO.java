package it.unisa.medical_docs_to_cda.CDALDO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

public class CDALDO {
    private CDALDOId oid;
    private String status;
    private LocalDateTime effectiveTimeDate;
    private String confidentialityCodeValue;
    private CDALDOId setOid;
    private String versionNumberValue;

    private CDALDOPatient patient;
    private CDALDOPatient guardian;
    private CDALDOAuthor author;
    private LocalDateTime authorTime;

    private CDALDOId rapresentedOrganization;
    private CDALDOAuthor compiler;
    private LocalDateTime compilerTime;

    private CDALDOId custodianId;
    private String custodianOrgazationName;
    private CDALDOAddr custodianOrganizationAddress;
    private String custodianPhoneNumber;

    private CDALDOId informationRecipientId;
    private String informationRecipientName;
    private LocalDateTime legalAuthTime;

    private CDALDOAuthor legalAuthenticator;
    private List<CDALDOAuthor> participants;
    private String fulfillmentId;

    private String ramoAziendale;
    private String numeroNosologico;
    private String nomeAzienda;

    private LocalDateTime lowTime;
    private LocalDateTime highTime;

    private List<String> repartoIds;
    private List<String> repartoNames;
    private List<String> ministerialCodes;

    private List<String> facilityNames;
    private List<String> facilityTelecoms;

    private Map<String, List<CDALDONarrativeBlock>> narrativeBlocks;
    private Map<String, List<CDALDOEntry>> entries;

    public void setOid(CDALDOId oid) {
        if (oid == null) {
            throw new NullPointerException("oid can't be null");
        }
        this.oid = oid;
    }

    public void setStatus(String status) {
        if (status == null) {
            throw new NullPointerException("status can't be null");
        }
        this.status = status;
    }

    public void setEffectiveTimeDate(LocalDateTime effectivTimeDate) {
        if (effectivTimeDate == null) {
            throw new NullPointerException("effectiveTimeDate can't be null");
        }
        this.effectiveTimeDate = effectivTimeDate;
    }

    public void setSetId(CDALDOId setOid) {
        if (setOid == null) {
            throw new NullPointerException("setOid can't be null");
        }
        if (!setOid.equals(this.oid)) {
            System.err.println("Related documents are not implemented");
            throw new IllegalArgumentException("Set OID does not match the current OID");
        }
        this.setOid = setOid;
    }

    public void setVersion(String versionNumberValue) {
        if (versionNumberValue == null) {
            throw new NullPointerException("versionNumberValue can't be null");
        }
        this.versionNumberValue = versionNumberValue;
    }

    public void setConfidentialityCode(String confidentialityCodeValue) {
        if (confidentialityCodeValue == null) {
            throw new NullPointerException("confidentialityCodeValue can't be null");
        }
        if (confidentialityCodeValue == "N" || confidentialityCodeValue == "V") {
            this.confidentialityCodeValue = confidentialityCodeValue;
        } else {
            throw new IllegalArgumentException("this value is unaccettanble");
        }
    }

    public void setPatient(CDALDOPatient patient) {
        if (patient == null) {
            throw new NullPointerException("patient can't be null");
        }
        if (patient.getIds() == null || patient.getIds().isEmpty()) {
            throw new IllegalArgumentException("patient IDs can't be empty");
        }
        if (patient.getAddresses().isEmpty()) {
            throw new IllegalArgumentException("patient addresses can't be empty");
        }
        boolean patientCF = false;
        boolean patientEni = false;
        boolean patientAna = false;
        for (CDALDOId id : patient.getIds()) {
            if (id.getOid().equals("2.16.840.1.113883.2.9.4.3.2")) {
                patientCF = true;
            }
            if (id.getOid().equals( "2.16.840.1.113883.2.9.4.3.18")) {
                patientEni = true;
            }
            if (id.getOid().equals("2.16.840.1.113883.2.9.4.3.17")) {
                patientAna = true;
            }
        }
        if (!(patientCF || patientEni))
            throw new IllegalArgumentException("at least one id needs to be a Codice Fiscale or an ENI code");
        if (!patientAna)
            throw new IllegalArgumentException("an id needs to be an ANA code");
        if (patient.getLastName() == null || patient.getFirstName() == null || patient.getBirthDate() == null
                || patient.getBirthPlace() == null || patient.getGender() == null) {
            throw new IllegalArgumentException("patient data is incomplete");
        }
        Period period = Period.between(patient.getBirthDate(), LocalDate.now());
        if (period.getYears() < 18 && this.guardian == null) {
            throw new IllegalArgumentException("Patient is a minor and there isnt a Guardian");
        }
        this.patient = patient;

    }

    public void setAuthor(CDALDOAuthor author) {
        if (author == null) {
            throw new NullPointerException("author can't be null");
        }
        if (author.getFirstName() == null || author.getLastName() == null || author.getTelecoms().isEmpty()) {
            throw new IllegalArgumentException("author data is incomplete");
        }
        if (author.getId().getOid() == "2.16.840.1.113883.2.9.4.3.2")
            throw new IllegalArgumentException("author id needs to be a CF");
        this.author = author;
        this.authorTime = LocalDateTime.now();
    }

    public void setGuardian(CDALDOPatient guardian) {
        if (guardian == null) {
            throw new NullPointerException("guardian can't be null");
        }
        if (guardian.getIds() == null || guardian.getIds().isEmpty()) {
            throw new IllegalArgumentException("guardian IDs can't be empty");
        }
        boolean guardianCF = false;
        for (CDALDOId id : guardian.getIds()) {
            if (id.getOid().equals("2.16.840.1.113883.2.9.4.3.2")) {
                guardianCF = true;
            }
        }
        if (!guardianCF)
            throw new IllegalArgumentException("at least one id needs to be a Codice Fiscale");
        if (guardian.getLastName() == null || guardian.getFirstName() == null || guardian.getBirthDate() == null
                || guardian.getBirthPlace() == null || guardian.getGender() == null) {
            throw new IllegalArgumentException("guardian data is incomplete");
        }
        Period period = Period.between(guardian.getBirthDate(), LocalDate.now());
        if (period.getYears() < 18) {
            throw new IllegalArgumentException("Guardian is a minor");
        }
        this.guardian = guardian;
    }

    public void setCompiler(CDALDOAuthor compiler) {
        if (compiler == null) {
            throw new NullPointerException("compiler can't be null");
        }
        if (compiler.getFirstName() == null || compiler.getLastName() == null) {
            throw new IllegalArgumentException("compiler data is incomplete");
        }
        if (compiler.getId().getOid() == "2.16.840.1.113883.2.9.4.3.2")
            throw new IllegalArgumentException("compiler id needs to be a CF");
        this.compiler = compiler;
        this.compilerTime = LocalDateTime.now();
    }

    public void setCustodian(CDALDOId custodianId, String custodianOrgazationName,
            CDALDOAddr custodianOrganizationAddress, String custodianPhoneNumber) {
        if (custodianId == null) {
            throw new NullPointerException("custodianId can't be null");
        }
        if (custodianOrgazationName == null || custodianOrgazationName.isEmpty()) {
            throw new IllegalArgumentException("custodianOrgazationName can't be empty");
        }
        if (custodianOrganizationAddress == null) {
            throw new NullPointerException("custodianOrganizationAddress can't be null");
        }
        if (custodianPhoneNumber == null || custodianPhoneNumber.isEmpty()) {
            throw new IllegalArgumentException("custodianPhoneNumber can't be empty");
        }
        if (!(custodianId.getOid().equals("2.16.840.1.113883.2.9.4.1.1")
                || custodianId.getOid().equals("2.16.840.1.113883.2.9.4.1.2"))) {
            throw new IllegalArgumentException("custodianId needs to be a FLS11 or HSP11");
        }
        this.custodianId = custodianId;
        this.custodianOrgazationName = custodianOrgazationName;
        this.custodianOrganizationAddress = custodianOrganizationAddress;
        this.custodianPhoneNumber = custodianPhoneNumber;
    }

    public void setLegalAuthenticator(CDALDOAuthor legalAuthenticator) {
        if (legalAuthenticator == null) {
            throw new NullPointerException("legalAuthenticator can't be null");
        }
        if (legalAuthenticator.getFirstName() == null || legalAuthenticator.getLastName() == null) {
            throw new IllegalArgumentException("legalAuthenticator data is incomplete");
        }
        if (legalAuthenticator.getId().getOid() == "2.16.840.1.113883.2.9.4.3.2") {
            throw new IllegalArgumentException("legalAuthenticator id needs to be a CF");
        }
        this.legalAuthenticator = legalAuthenticator;
        this.legalAuthTime = LocalDateTime.now();
    }

    public void setComponentOf(String ramoAziendale, String numeroNosologico, String nomeAzienda, LocalDateTime lowTime,
            LocalDateTime highTime, List<String> repartoIds, List<String> repartoNames, List<String> ministerialCodes,
            List<String> facilityNames, List<String> facilityTelecoms) {
        if (ramoAziendale == null || ramoAziendale.isEmpty()) {
            throw new IllegalArgumentException("ramoAziendale can't be empty");
        }
        if (numeroNosologico == null || numeroNosologico.isEmpty()) {
            throw new IllegalArgumentException("numeroNosologico can't be empty");
        }
        if (nomeAzienda == null || nomeAzienda.isEmpty()) {
            throw new IllegalArgumentException("nomeAzienda can't be empty");
        }
        if (lowTime == null) {
            throw new NullPointerException("lowTime can't be null");
        }
        if (highTime == null) {
            throw new NullPointerException("highTime can't be null");
        }
        if (repartoIds == null || repartoIds.isEmpty()) {
            throw new IllegalArgumentException("repartoIds can't be empty");
        }
        if (repartoNames == null || repartoNames.isEmpty()) {
            throw new IllegalArgumentException("repartoNames can't be empty");
        }
        if (ministerialCodes == null || ministerialCodes.isEmpty()) {
            throw new IllegalArgumentException("ministerialCodes can't be empty");
        }
        if (facilityNames == null || facilityNames.isEmpty()) {
            throw new IllegalArgumentException("facilityNames can't be empty");
        }
        if (facilityTelecoms == null || facilityTelecoms.isEmpty()) {
            throw new IllegalArgumentException("facilityTelecoms can't be empty");
        }
        this.ramoAziendale = ramoAziendale;
        this.numeroNosologico = numeroNosologico;
        this.nomeAzienda = nomeAzienda;
        this.lowTime = lowTime;
        this.highTime = highTime;
        this.repartoIds = repartoIds;
        this.repartoNames = repartoNames;
        this.ministerialCodes = ministerialCodes;
        this.facilityNames = facilityNames;
        this.facilityTelecoms = facilityTelecoms;
    }

    public void setParticipant(CDALDOAuthor participant) {
        if (participant == null) {
            throw new NullPointerException("participant can't be null");
        }
        if (participant.getFirstName() == null || participant.getLastName() == null) {
            throw new IllegalArgumentException("participant data is incomplete");
        }
        if (participant.getId().getOid() != "2.16.840.1.113883.2.9.4.3.2") {
            throw new IllegalArgumentException("participant id needs to be a CF");
        }
        if (this.participants == null) {
            this.participants = new ArrayList<>();
        }
        this.participants.add(participant);
    }

    public CDALDO() {
        this.entries = new HashMap<>();
        this.narrativeBlocks = new HashMap<>();
    }

    public void setInformationRecipient(CDALDOId informationRecipientId, String informationRecipientName) {
        if (informationRecipientId == null) {
            throw new NullPointerException("informationRecipientId can't be null");
        }
        if (informationRecipientName == null || informationRecipientName.isEmpty()) {
            throw new IllegalArgumentException("informationRecipientName can't be empty");
        }
        this.informationRecipientId = informationRecipientId;
        this.informationRecipientName = informationRecipientName;
    }

    public void setRapresentedOrganization(CDALDOId rappresentedOrganization) {
        if (rappresentedOrganization == null) {
            throw new NullPointerException("rappresentedOrganization can't be null");
        }
        if (!(rappresentedOrganization.getOid().equals("2.16.840.1.113883.2.9.4.1.1")
                || rappresentedOrganization.getOid().equals("2.16.840.1.113883.2.9.4.1.2"))) {
            throw new IllegalArgumentException("rappresentedOrganization needs to be a FLS11 or HSP11");
        }
        this.rapresentedOrganization = rappresentedOrganization;
    }

    public void setNarrativeBlocks(List<CDALDONarrativeBlock> narrativeBlocks, String sectionNumber) {
        if (narrativeBlocks == null) {
            throw new NullPointerException("narrativeBlocks of" + sectionNumber +" can't be null");
        }
        if (narrativeBlocks.isEmpty()) {
            throw new IllegalArgumentException("Narrative blocks of" + sectionNumber +" can't be empty");
        }
        Integer sectionNumb = Integer.parseInt(sectionNumber);
        if (sectionNumb < 1 || sectionNumb > 13) {
            throw new IllegalArgumentException("Invalid section number. Section number must be between 1 and 13.");
        }
        this.narrativeBlocks.put(sectionNumber, narrativeBlocks);
    }

    public void setEntries(List<CDALDOEntry> entries, String sectionNumber) {
        if (entries == null) {
            throw new NullPointerException("entries of "+ sectionNumber +" can't be null");
        }
      /*   if (entries.isEmpty()) {
            throw new IllegalArgumentException("entries of" + sectionNumber+ "can't be empty");
        }*/
        Integer sectionNumb = Integer.parseInt(sectionNumber);
        if (sectionNumb < 1 || sectionNumb > 12 || sectionNumb == 3 || sectionNumb == 5 || sectionNumb == 13) {
            throw new IllegalArgumentException("Invalid section number. Section number must be between 1 and 12, excluding 3, 5, and 13.");
        }        
        this.entries.put(sectionNumber, entries);
    }

    public List<String> check() {
        List<String> errors = new ArrayList<>();
        if (this.oid == null)
            errors.add("oid");
        if (this.status == null)
            errors.add("status");
        if (this.effectiveTimeDate == null)
            errors.add("effectiveTimeDate");
        if (this.setOid == null)
            errors.add("setOid");
        if (this.versionNumberValue == null)
            errors.add("versionNumberValue");
        if (this.patient == null)
            errors.add("patient");
        if (this.author == null)
            errors.add("author");
        if (this.authorTime == null)
            errors.add("authorTime");
        if (this.rapresentedOrganization == null)
            errors.add("rapresentedOrganization");
        if (this.compiler == null)
            errors.add("compiler");
        if (this.compilerTime == null)
            errors.add("compilerTime");
        if (this.custodianId == null)
            errors.add("custodianId");
        if (this.custodianOrgazationName == null)
            errors.add("custodianOrgazationName");
        if (this.custodianOrganizationAddress == null)
            errors.add("custodianOrganizationAddress");
        if (this.custodianPhoneNumber == null)
            errors.add("custodianPhoneNumber");
        if (this.legalAuthTime == null)
            errors.add("legalAuthTime");
        if (this.legalAuthenticator == null)
            errors.add("legalAuthenticator");
        if (this.fulfillmentId == null)
            errors.add("fulfillmentId");
        if (this.ramoAziendale == null)
            errors.add("ramoAziendale");
        if (this.numeroNosologico == null)
            errors.add("numeroNosologico");
        if (this.nomeAzienda == null)
            errors.add("nomeAzienda");
        if (this.lowTime == null)
            errors.add("lowTime");
        if (this.highTime == null)
            errors.add("highTime");
        if (this.repartoIds == null || this.repartoIds.isEmpty())
            errors.add("repartoIds");
        if (this.repartoNames == null || this.repartoNames.isEmpty())
            errors.add("repartoNames");
        if (this.ministerialCodes == null || this.ministerialCodes.isEmpty())
            errors.add("ministerialCodes");
        if (this.facilityNames == null || this.facilityNames.isEmpty())
            errors.add("facilityNames");
        if (this.facilityTelecoms == null || this.facilityTelecoms.isEmpty())
            errors.add("facilityTelecoms");
        if (this.narrativeBlocks == null || this.narrativeBlocks.isEmpty()) {
            errors.add("narrativeBlocksSection1");
            errors.add("narrativeBlocksSection3");
            errors.add("narrativeBlocksSection11");
        } else {
            if (this.narrativeBlocks.get("1") == null || this.narrativeBlocks.get("1").isEmpty())
                errors.add("narrativeBlocksSection1");
            if (this.narrativeBlocks.get("3") == null || this.narrativeBlocks.get("3").isEmpty())
                errors.add("narrativeBlocksSection3");
            if (this.narrativeBlocks.get("11") == null || this.narrativeBlocks.get("11").isEmpty())
                errors.add("narrativeBlocksSection11");
        }
        return errors;
    }

    public void setFullfillmentId(String fulfillmentId) {
        if (fulfillmentId == null || fulfillmentId.isEmpty()) {
            throw new IllegalArgumentException("fulfillmentId can't be empty");
        }
        this.fulfillmentId = fulfillmentId;
    }

    public Document getCDA() throws ParserConfigurationException {
        Document doc = CDALDOBuilder.createBasicDoc();
        if (!this.check().isEmpty()) {
            throw new IllegalArgumentException("CDALDO object is not valid");
        }
        CDALDOBuilder.addHeader(doc, this.oid, this.status, this.effectiveTimeDate, this.confidentialityCodeValue,
                this.setOid, this.versionNumberValue,
                this.patient, this.guardian, this.author, this.authorTime, this.rapresentedOrganization,
                this.compiler, this.compilerTime, this.custodianId, this.custodianOrgazationName,
                this.custodianOrganizationAddress, this.custodianPhoneNumber, this.informationRecipientId,
                this.informationRecipientName,
                this.legalAuthTime, this.legalAuthenticator, this.participants, this.fulfillmentId,
                this.ramoAziendale, this.numeroNosologico, this.nomeAzienda, this.lowTime, this.highTime,
                this.repartoIds, this.repartoNames, this.ministerialCodes, this.facilityNames, this.facilityTelecoms);

        CDALDOBuilder.addBody(doc, narrativeBlocks, entries);

        return doc;
    }

}
