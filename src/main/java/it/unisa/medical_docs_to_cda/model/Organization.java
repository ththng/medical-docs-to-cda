package it.unisa.medical_docs_to_cda.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "organization")
public class Organization {
    @Id
    private String id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String phone;
    
    public Organization(String id, String name, String address, String city, String state, String zip, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phone = phone;
    }

    

}
