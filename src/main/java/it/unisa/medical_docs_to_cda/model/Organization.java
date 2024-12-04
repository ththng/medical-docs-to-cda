package it.unisa.medical_docs_to_cda.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document(collection = "organization")
public class Organization {
    @Field("Id")
    private String id;
    @Field("NAME")
    private String name;
    @Field("ADDRESS")
    private String address;
    @Field("CITY")
    private String city;
    @Field("STATE")
    private String state;
    @Field("ZIP")
    private String zip;
    @Field("PHONE")
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
