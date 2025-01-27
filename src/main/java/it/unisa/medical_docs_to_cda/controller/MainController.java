package it.unisa.medical_docs_to_cda.controller;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;

import it.unisa.medical_docs_to_cda.CDALDO.CDALDO;
import it.unisa.medical_docs_to_cda.fhir.LoaderFhir;
import it.unisa.medical_docs_to_cda.model.*;
import it.unisa.medical_docs_to_cda.repositories.*;

/**
 * MainController is a Spring MVC controller responsible for handling HTTP
 * requests
 * related to patient resources. It is mapped to the "/patients" URL path and
 * uses
 * various repositories to perform CRUD operations on patient-related data such
 * as
 * encounters, allergies, care plans, medications, observations, procedures,
 * conditions,
 * imaging studies, and immunizations. The controller also integrates with the
 * CDAController
 * and LoaderFhir for additional functionalities.
 */
@Controller
@RequestMapping("/patients")
public class MainController {

    @Autowired
    private PatientRepository patientRepo;
    @Autowired
    private EncounterRepository encounterRepo;
    @Autowired
    private AllergyRepository allergyRepo;
    @Autowired
    private CareplanRepository careplanRepo;
    @Autowired
    private MedicationRepository medicationRepo;
    @Autowired
    private ObservationRepository observationRepo;
    @Autowired
    private ProcedureRepository procedureRepo;
    @Autowired
    private ConditionRepository conditionRepo;
    @Autowired
    private ImagingStudyRepository imagingStudyRepo;
    @Autowired
    private ImmunizationRepository immunizationRepo;
    @Autowired
    private CDAController cdaController;
    @Autowired
    private LoaderFhir loaderFhir;

    private final String dicomDirectory = "src/main/resources/dicom/";

    /**
     * Handles the root URL mapping.
     * 
     * @return the name of the HTML file to be rendered, without the extension.
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }

    /**
     * Retrieves a paginated list of patients and adds it to the model.
     *
     * @param page  the page number to retrieve, defaults to 0.
     * @param size  the number of records per page, defaults to 10.
     * @param model the model to which the list of patients and pagination details
     *              are added.
     * @return the name of the HTML file to be rendered, without the extension.
     */
    @GetMapping("/list")
    public String getList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Sort sortObj = Sort.by(Sort.Direction.ASC, "first");
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Patient> patientsPage = patientRepo.findAll(pageable);

        model.addAttribute("patients", patientsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", patientsPage.getTotalPages());

        return "patients";
    }

    /**
     * Searches for patients based on the specified type and query, and adds the
     * results to the model.
     *
     * @param type  the type of search to perform (e.g., name, ssn, passport,
     *              drivers).
     * @param query the search query string.
     * @param model the model to which the search results are added.
     * @return the name of the HTML file to be rendered, without the extension.
     */
    @GetMapping("/results")
    public String searchPatients(
            @RequestParam String type,
            @RequestParam String query,
            Model model) {
        List<Patient> patients;

        switch (type.toLowerCase()) {
            case "name":
                String[] nameParts = query.split(" ", 2);
                String firstName = nameParts.length > 0 ? nameParts[0] : "";
                String lastName = nameParts.length > 1 ? nameParts[1] : "";
                patients = patientRepo.findByFirstContainingIgnoreCaseAndLastContainingIgnoreCase(firstName, lastName);
                break;
            case "ssn":
                patients = patientRepo.findBySSN(query);
                break;
            case "passport":
                patients = patientRepo.findByPassport(query);
                break;
            case "drivers":
                patients = patientRepo.findByDrivers(query);
                break;
            default:
                patients = List.of(); // Empty list for invalid type
                break;
        }

        model.addAttribute("patients", patients != null ? patients : List.of());
        return "results";
    }

    /**
     * Retrieves encounters for a specific patient and adds them to the model.
     *
     * @param patientId the ID of the patient whose encounters are to be retrieved.
     * @param page      the page number to retrieve, defaults to 0.
     * @param size      the number of records per page, defaults to 3.
     * @param model     the model to which the encounters and related data are
     *                  added.
     * @return the name of the HTML file to be rendered, without the extension.
     */
    @GetMapping("/{id}/report")
    public String getPatientEncounters(@PathVariable("id") String patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "start"));
        Page<Encounter> encounterPage = encounterRepo.findByPatientId(patientId, pageable);
        String patientName = patientRepo.findById(patientId).get().getFirst() + " "
                + patientRepo.findById(patientId).get().getLast();

        if (encounterPage.isEmpty()) {
            model.addAttribute("error", "No encounters found for the given ID.");
            return "error";
        }

        Map<String, List<Observation>> observations = new HashMap<>();
        Map<String, List<Allergy>> allergies = new HashMap<>();
        Map<String, List<Condition>> conditions = new HashMap<>();
        Map<String, List<Careplan>> careplans = new HashMap<>();
        Map<String, List<ImagingStudy>> imagingStudies = new HashMap<>();
        Map<String, List<Immunization>> immunizations = new HashMap<>();
        Map<String, List<Medication>> medications = new HashMap<>();
        Map<String, List<Procedure>> procedures = new HashMap<>();

        String fileName = patientName.replace(" ", "_") + ".dcm";
        File dicomFile = new File(dicomDirectory + fileName);
        boolean hasImagingStudies = false;

        if (dicomFile.exists()) {
            hasImagingStudies = true;
        }

        for (Encounter encounter : encounterPage) {
            String encounterId = encounter.getId();
            observations.put(encounterId, observationRepo.findByEncounterId(encounterId));
            allergies.put(encounterId, allergyRepo.findByEncounterId(encounterId));
            conditions.put(encounterId, conditionRepo.findByEncounterId(encounterId));
            careplans.put(encounterId, careplanRepo.findByEncounterId(encounterId));
            imagingStudies.put(encounterId, imagingStudyRepo.findByEncounterId(encounterId));
            immunizations.put(encounterId, immunizationRepo.findByEncounterId(encounterId));
            medications.put(encounterId, medicationRepo.findByEncounterId(encounterId));
            procedures.put(encounterId, procedureRepo.findByEncounterId(encounterId));
        }

        model.addAttribute("hasImagingStudies", hasImagingStudies);
        model.addAttribute("patientName", patientName);
        model.addAttribute("patientId", patientId);
        model.addAttribute("encounters", encounterPage.getContent());
        model.addAttribute("allergies", allergies);
        model.addAttribute("conditions", conditions);
        model.addAttribute("careplans", careplans);
        model.addAttribute("imagingStudies", imagingStudies);
        model.addAttribute("immunizations", immunizations);
        model.addAttribute("medications", medications);
        model.addAttribute("procedures", procedures);
        model.addAttribute("observations", observations);
        model.addAttribute("totalPages", encounterPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("hasImagingStudies", hasImagingStudies);

        return "report";
    }

    /**
     * Generates a CDA document for a given encounter.
     *
     * @param encounter the encounter for which the CDA document is to be generated.
     * @return the generated CDA document.
     * @throws IllegalArgumentException if the encounter is null.
     */
    public CDALDO generateCDA(Encounter encounter) {
        if (encounter == null) {
            throw new IllegalArgumentException("Encounter cannot be null");
        }
        return cdaController.EncounterToCDA(encounter);
    }

    /**
     * Retrieves a DICOM file as a downloadable resource.
     *
     * @param fileName the name of the DICOM file to retrieve.
     * @return a ResponseEntity containing the DICOM file resource.
     */
    @GetMapping("/dicom/{fileName}")
    public ResponseEntity<Resource> getDicomFile(@PathVariable String fileName) {
        Path dicomPath = Paths.get(dicomDirectory, fileName);

        if (!dicomPath.toFile().exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FileSystemResource(new File("error.html")));
        }

        Resource resource = new FileSystemResource(dicomPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/dicom"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }

    /**
     * Checks the validity of data for a given encounter.
     *
     * @param request a map containing the encounter ID.
     * @return a ResponseEntity containing a message about the validity of the data.
     */
    @PostMapping("/check")
    public ResponseEntity<Map<String, String>> checkData(@RequestBody Map<String, String> request) {
        try {
            String encounterId = request.get("encounterId");
            if (encounterId == null || encounterId.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Encounter ID is required"));
            }

            Optional<Encounter> optionalEncounter = encounterRepo.findById(encounterId);
            if (optionalEncounter.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Encounter not found"));
            }

            CDALDO cdaldo = generateCDA(optionalEncounter.get());
            List<String> errors = cdaldo.check();

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", String.join(", ", errors)));
            }
            return ResponseEntity.ok(Map.of("message", "Data is valid!"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Converts a Document object to a string representation.
     *
     * @param doc the Document object to convert.
     * @return the string representation of the Document.
     * @throws TransformerException if an error occurs during transformation.
     */
    private String documentToString(Document doc) throws TransformerException {
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
        return stringWriter.toString();
    }

    /**
     * Displays the CDA document for a specific encounter.
     *
     * @param encounterId the ID of the encounter whose CDA document is to be
     *                    displayed.
     * @param model       the model to which the CDA XML is added.
     * @return the name of the HTML file to be rendered, without the extension.
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created.
     * @throws TransformerException         if an error occurs during
     *                                      transformation.
     */
    @GetMapping("/{id}/view-cda")
    public String viewCDA(@PathVariable("id") String encounterId, Model model)
            throws ParserConfigurationException, TransformerException {

        Encounter encounter = encounterRepo.findById(encounterId).get();
        CDALDO cdaldo = generateCDA(encounter);
        Document cdaXml = cdaldo.getCDA();
        model.addAttribute("cdaXml", documentToString(cdaXml));
        model.addAttribute("encounterId", encounterId);
        return "cda";
    }

    /**
     * Loads the CDA data on patient's electronic health record for a specific
     * encounter.
     *
     * @param encounterId the ID of the encounter whose EHR data is to be loaded.
     * @return a map containing the outcome of the EHR loading process.
     */
    @GetMapping("/{id}/ehr")
    @ResponseBody
    public Map<String, Object> loadEHR(@PathVariable("id") String encounterId) {
        boolean outcome = loaderFhir.loadEncounter(encounterRepo.findById(encounterId).get());
        Map<String, Object> response = new HashMap<>();
        response.put("loadedFhir", outcome);
        return response;
    }

    /**
     * Downloads the CDA document for a specific encounter.
     *
     * @param encounterId the ID of the encounter whose CDA document is to be
     *                    downloaded.
     * @return a ResponseEntity containing the CDA document as a string.
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created.
     * @throws TransformerException         if an error occurs during
     *                                      transformation.
     */
    @GetMapping("/{id}/cda")
    public ResponseEntity<String> downloadCDA(@PathVariable("id") String encounterId)
            throws ParserConfigurationException, TransformerException {
        Optional<Encounter> optionalEncounter = encounterRepo.findById(encounterId);
        if (optionalEncounter.isEmpty()) {
            return ResponseEntity.badRequest().body("Encounter not found");
        }
        Encounter encounter = optionalEncounter.get();

        CDALDO cdaldo = generateCDA(encounter);
        Document cdaXml = cdaldo.getCDA();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cda-" + encounterId + ".xml")
                .contentType(MediaType.APPLICATION_XML)
                .body(documentToString(cdaXml));
    }

}