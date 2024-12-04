package it.unisa.medical_docs_to_cda.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.unisa.medical_docs_to_cda.model.Patient;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    List<Patient> findByFirstIgnoreCaseAndLastIgnoreCase(String first, String last);


}

