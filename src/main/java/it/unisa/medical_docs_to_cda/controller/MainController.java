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
        Iterable<Patient> patients = patientRepo.findAll();

        patients.forEach(patient -> {
            System.out.println(patient); 
        });
        

        model.addAttribute("patients", patients);

        // Ritorna il nome del template
        return "patients";
    }


    @GetMapping("/search")
    public String showSearchForm(Model model) {
        return "search-patients";
    }

    @PostMapping("/search")
    public String searchPatient(@RequestParam String firstName, @RequestParam String lastName, Model model) {

        List<Patient> patients = patientRepo.findByFirstIgnoreCaseAndLastIgnoreCase(firstName, lastName);

        model.addAttribute("patients", patients);

        return "patient-results";
    }
}
