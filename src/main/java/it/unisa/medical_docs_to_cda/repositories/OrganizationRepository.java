package it.unisa.medical_docs_to_cda.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.unisa.medical_docs_to_cda.model.Organization;
import it.unisa.medical_docs_to_cda.model.Patient;

@Repository
public interface OrganizationRepository extends MongoRepository<Organization, String>{
    Optional<Organization> findById(String id); 
}
