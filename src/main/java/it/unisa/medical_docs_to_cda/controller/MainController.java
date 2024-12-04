package it.unisa.medical_docs_to_cda.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
    public String getList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "last") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        Sort sortObj = Sort.by(Sort.Direction.valueOf(direction.toUpperCase()), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Patient> patientsPage = patientRepo.findAll(pageable);

        model.addAttribute("patients", patientsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", patientsPage.getTotalPages());
        //model.addAttribute("sort", sort);
        //model.addAttribute("direction", direction);
        //model.addAttribute("reverseDirection", direction.equals("asc") ? "desc" : "asc");

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

        model.addAttribute("patients", patients);
        return "results";
    }

}
