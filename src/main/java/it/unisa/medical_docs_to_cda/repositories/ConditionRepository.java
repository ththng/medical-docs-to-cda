package it.unisa.medical_docs_to_cda.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.unisa.medical_docs_to_cda.model.Condition;

@Repository
public interface ConditionRepository extends MongoRepository<Condition, String> {
    List<Condition> findByEncounterId(String encounterId);
}
