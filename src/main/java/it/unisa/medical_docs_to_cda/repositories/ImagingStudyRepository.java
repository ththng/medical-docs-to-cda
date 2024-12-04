package it.unisa.medical_docs_to_cda.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.unisa.medical_docs_to_cda.model.ImagingStudy;

@Repository
public interface ImagingStudyRepository extends MongoRepository<ImagingStudy, String>{

}
