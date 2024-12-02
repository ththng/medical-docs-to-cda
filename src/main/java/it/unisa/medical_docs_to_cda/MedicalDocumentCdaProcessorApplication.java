package it.unisa.medical_docs_to_cda;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import it.unisa.medical_docs_to_cda.patient.Patient;
import it.unisa.medical_docs_to_cda.repository.PatientRepository;

@SpringBootApplication
@EnableMongoRepositories
public class MedicalDocumentCdaProcessorApplication implements CommandLineRunner {

	@Autowired
	PatientRepository patientItemRepo;
	
	List<Patient> itemList = new ArrayList<Patient>();
	public static void main(String[] args) {
		SpringApplication.run(MedicalDocumentCdaProcessorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("-------------CREATE PATIENT ITEMS-------------------------------\n");
		
		createPatientItems();
		showAllGroceryItems();
	}
	void createPatientItems() {
		System.out.println("Data creation started...");

		patientItemRepo.save(new Patient("Marco", "Carta", "Stroke", LocalDate.of(2003, Month.MARCH, 24), 21));
		patientItemRepo.save(new Patient("Luca", "Biscuit", "CHD", LocalDate.of(2000, Month.FEBRUARY, 13), 24));
		patientItemRepo.save(new Patient("Gino", "Fastidio", "Cancer", LocalDate.of(1998, Month.MAY, 22), 26));
		patientItemRepo.save(new Patient("Antonio", "Apicella", "Hepatitis B", LocalDate.of(2010, Month.OCTOBER, 6), 14));
		patientItemRepo.save(new Patient("Luisa", "Acanfora", "Common cold", LocalDate.of(2015, Month.APRIL, 11), 9));
		
		System.out.println("Data creation complete...");
	}
 
	public void showAllGroceryItems() {
		itemList = patientItemRepo.findAll();
		itemList.forEach(item -> System.out.println(getItemDetails(item)));
	}

	public String getItemDetails(Patient item) {

		System.out.println(
		"Patient Name: " + item.getFirst_name() + 
		", \nPatient Surname: " + item.getLast_name() + 
		", \nPatient Disease: " + item.getDisease()
		);
			 return "";
	}

}
