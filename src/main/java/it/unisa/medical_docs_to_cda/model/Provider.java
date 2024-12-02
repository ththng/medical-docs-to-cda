package it.unisa.medical_docs_to_cda.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

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
