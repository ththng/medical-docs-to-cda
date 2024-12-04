package it.unisa.medical_docs_to_cda.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document(collection = "provider")
public class Provider {
    @Field("Id")
    private String id;
    @Field("ORGANIZATION")
    private String organizationId;
    @Field("NAME")
    private String name;
    @Field("GENDER")
    private String gender;
    @Field("SPECIALITY")
    private String speciality;
    @Field("ADDRESS")
    private String address;
    @Field("CITY")
    private String city;
    @Field("STATE")
    private String state;
    @Field("ZIP")
    private String zip;
    

    public Provider(String id, String organizationId, String name, String gender, String speciality, String address,
            String city, String state, String zip) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.gender = gender;
        this.speciality = speciality;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

}
