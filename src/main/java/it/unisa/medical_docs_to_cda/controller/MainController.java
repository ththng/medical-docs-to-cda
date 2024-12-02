package it.unisa.medical_docs_to_cda.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import it.unisa.medical_docs_to_cda.model.Patient;
import it.unisa.medical_docs_to_cda.repositories.PatientRepository;

@Controller
@RequestMapping("/patients")
public class MainController {
    
    @Autowired
    private PatientRepository patientRepo;

    @GetMapping("/list")
    public String getList(Model model) {
        List<Patient> patients = patientRepo.findAll();

        patients.forEach(patient -> {
            // Process patient data if needed
        });

        model.addAttribute("patients", patients);

        // Returns the template's name
        return "patients";
    }

    @GetMapping("/search")
    public String showSearchForm(Model model) {
        return "search-patients";
    }

    @PostMapping("/search")
    public String searchPatient(@RequestParam String firstName, @RequestParam String lastName, Model model) {

        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            model.addAttribute("error", "First name and last name must not be empty");
            return "search-patients";
        }

        List<Patient> patients = patientRepo.findByFirstIgnoreCaseAndLastIgnoreCase(firstName, lastName);

        model.addAttribute("patients", patients);

        return "patient-results";
    }

    
}
