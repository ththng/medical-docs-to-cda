package it.unisa.medical_docs_to_cda.model;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "provider")
public class Provider {
    @MongoId
    private String id;
    private String organization;
    private String name;
    private String gender;
    private String speciality;
    private String address;
    private String city;
    private String state;
    private String zip;

    public Provider(String id, String organization, String name, String gender, String speciality, String address,
            String city, String state, String zip) {
        this.id = id;
        this.organization = organization;
        this.name = name;
        this.gender = gender;
        this.speciality = speciality;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }



}
