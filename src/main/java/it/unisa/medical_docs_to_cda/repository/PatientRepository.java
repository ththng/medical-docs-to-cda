package it.unisa.medical_docs_to_cda.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.unisa.medical_docs_to_cda.patient.Patient;

public interface PatientRepository extends MongoRepository<Patient, String> {
    @Query("{name:'?0'}")
	Patient findItemByName(String name);
	
	@Query(value="{disease:'?0'}", fields="{'name' : 1, 'years' : 1}")
	List<Patient> findAll(String disease);
	
	public long count();

}
