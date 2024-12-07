package it.unisa.medical_docs_to_cda;
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
