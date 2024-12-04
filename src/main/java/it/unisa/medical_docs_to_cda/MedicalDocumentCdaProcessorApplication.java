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

@SpringBootApplication
@EnableMongoRepositories
public class MedicalDocumentCdaProcessorApplication {
	public static void main(String[] args) {
		SpringApplication.run(MedicalDocumentCdaProcessorApplication.class, args);
	}

}
