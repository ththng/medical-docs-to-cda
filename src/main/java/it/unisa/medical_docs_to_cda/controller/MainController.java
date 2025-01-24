package it.unisa.medical_docs_to_cda.controller;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.unisa.medical_docs_to_cda.model.Allergy;
import it.unisa.medical_docs_to_cda.model.Careplan;
import it.unisa.medical_docs_to_cda.model.Condition;
import it.unisa.medical_docs_to_cda.model.Encounter;
import it.unisa.medical_docs_to_cda.model.ImagingStudy;
import it.unisa.medical_docs_to_cda.model.Immunization;
import it.unisa.medical_docs_to_cda.model.Medication;
import it.unisa.medical_docs_to_cda.model.Observation;
import it.unisa.medical_docs_to_cda.model.Patient;
import it.unisa.medical_docs_to_cda.model.Procedure;
import it.unisa.medical_docs_to_cda.repositories.AllergyRepository;
import it.unisa.medical_docs_to_cda.repositories.CareplanRepository;
import it.unisa.medical_docs_to_cda.repositories.ConditionRepository;
import it.unisa.medical_docs_to_cda.repositories.EncounterRepository;
import it.unisa.medical_docs_to_cda.repositories.ImagingStudyRepository;
import it.unisa.medical_docs_to_cda.repositories.ImmunizationRepository;
import it.unisa.medical_docs_to_cda.repositories.MedicationRepository;
import it.unisa.medical_docs_to_cda.repositories.ObservationRepository;
import it.unisa.medical_docs_to_cda.repositories.PatientRepository;
import it.unisa.medical_docs_to_cda.repositories.ProcedureRepository;

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

    @GetMapping("/list")
    public String getList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Sort sortObj = Sort.by(Sort.Direction.ASC, "last");
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
    public String getPatientObservations(@PathVariable("id") String patientId, Model model) {
        List<Encounter> encounters = encounterRepo.findByPatientId(patientId);
        encounters.sort(Comparator.comparing(Encounter::getStart).reversed());

        if (encounters.isEmpty()) {
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

        for (Encounter encounter : encounters) {
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

        model.addAttribute("patientId", patientId);
        model.addAttribute("encounters", encounters);
        model.addAttribute("allergies", allergies);
        model.addAttribute("conditions", conditions);
        model.addAttribute("careplans", careplans);
        model.addAttribute("imagingStudies", imagingStudies);
        model.addAttribute("immunizations", immunizations);
        model.addAttribute("medications", medications);
        model.addAttribute("procedures", procedures);
        model.addAttribute("observations", observations);

        return "report";
    }

    // TODO: adjust to our data model
    @PostMapping("/check")
    public ResponseEntity<Map<String, String>> checkData(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");

        Map<String, String> response = new HashMap<>();
        if (name.isEmpty() || email.isEmpty()) {
            response.put("message", "Name and email are required!");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("message", "Data is valid! Welcome, " + name);
        return ResponseEntity.ok(response);
    }
}
