package it.unisa.medical_docs_to_cda.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.unisa.medical_docs_to_cda.model.Patient;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    List<Patient> findByFirstIgnoreCaseAndLastIgnoreCase(String first, String last);
    @SuppressWarnings("null")
    Page<Patient> findAll(Pageable pageable);
    List<Patient> findByFirstContainingIgnoreCaseAndLastContainingIgnoreCase(String first, String last);
    List<Patient> findBySSN(String SSN);
    List<Patient> findByPassport(String passport);
    List<Patient> findByDrivers(String drivers);
    Optional<Patient> findById(String id); 


}

