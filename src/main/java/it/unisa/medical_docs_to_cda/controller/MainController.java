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
import org.w3c.dom.Document;

import it.unisa.medical_docs_to_cda.CDALDO.CDALDO;
import it.unisa.medical_docs_to_cda.fhir.LoaderFhir;
import it.unisa.medical_docs_to_cda.model.*;
import it.unisa.medical_docs_to_cda.repositories.*;

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
     * Handles the root URL mapping for the patients section.
     * 
     * @return the name of the HTML file to be rendered, without the extension.
     */
    @GetMapping("/")
    public String home() {
        return "home"; // Nome del file HTML senza estensione (es: home.html)
    }

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

        boolean hasImagingStudies = false;
        for (Encounter encounter : encounterPage) {
            String encounterId = encounter.getId();
            observations.put(encounterId, observationRepo.findByEncounterId(encounterId));
            allergies.put(encounterId, allergyRepo.findByEncounterId(encounterId));
            conditions.put(encounterId, conditionRepo.findByEncounterId(encounterId));
            careplans.put(encounterId, careplanRepo.findByEncounterId(encounterId));
            imagingStudies.put(encounterId, imagingStudyRepo.findByEncounterId(encounterId));
            if (!imagingStudies.isEmpty())
                hasImagingStudies = true;
            immunizations.put(encounterId, immunizationRepo.findByEncounterId(encounterId));
            medications.put(encounterId, medicationRepo.findByEncounterId(encounterId));
            procedures.put(encounterId, procedureRepo.findByEncounterId(encounterId));
        }

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

    public CDALDO generateCDA(Encounter encounter) {
        if (encounter == null) {
            throw new IllegalArgumentException("Encounter cannot be null");
        }
        return cdaController.EncounterToCDA(encounter);
    }

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

    @PostMapping("/check")
    public ResponseEntity<Map<String, String>> checkData(@RequestBody Map<String, String> request) {
        try {
            // Ensure the request contains an encounter ID
            String encounterId = request.get("encounterId");
            if (encounterId == null || encounterId.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Encounter ID is required"));
            }

            // Find the encounter by ID
            Optional<Encounter> optionalEncounter = encounterRepo.findById(encounterId);
            if (optionalEncounter.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Encounter not found"));
            }

            // Generate CDA and check for errors
            CDALDO cdaldo = generateCDA(optionalEncounter.get());

            List<String> errors = cdaldo.check();

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", String.join(", ", errors)));
            }

            // Success response
            return ResponseEntity.ok(Map.of("message", "Data is valid!"));
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            // Return a generic error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    private String documentToString(Document doc) throws TransformerException {
        // Usa un StringWriter per raccogliere l'output
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Impostiamo l'indentazione per una formattazione leggibile
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        // Eseguiamo la trasformazione da Document a String
        transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));

        // Restituiamo la stringa
        return stringWriter.toString();
    }

    @GetMapping("/{id}/view-cda")
    public String viewCDA(@PathVariable("id") String encounterId, Model model)
            throws ParserConfigurationException, TransformerException {

        Encounter encounter = encounterRepo.findById(encounterId).get();
        CDALDO cdaldo = generateCDA(encounter);
        Document cdaXml = cdaldo.getCDA();
        boolean outcome = loaderFhir.loadEncounter(encounter);
        model.addAttribute("loadedFhir", outcome);
        model.addAttribute("cdaXml", documentToString(cdaXml));
        model.addAttribute("encounterId", encounterId);
        return "cda";
    }

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