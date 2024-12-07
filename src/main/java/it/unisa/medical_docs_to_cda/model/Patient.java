package it.unisa.medical_docs_to_cda.model;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection = "patient")
public class Patient {
    @Field("Id")
    private String id;
    @Field("BIRTHDATE")
    private LocalDate birthDate;
    @Field("DEATHDATE")
    private LocalDate deathDate;
    private String SSN;
    @Field("DRIVERS")  
    private String drivers;
    @Field("PASSPORT") 
    private String passport;
    @Field("FIRST") 
    private String first;
    @Field("LAST")
    private String last;
    @Field("MAIDEN")
    private String maiden;
    @Field("GENDER")
    private String gender;
    @Field("BIRTHPLACE") 
    private String birthPlace;
    @Field("ADDRESS") 
    private String address;
    @Field("CITY") 
    private String city;
    @Field("STATE")
    private String state;
    @Field("COUNTY")
    private String county;
    @Field("ZIP")
    private String zip;


    public Patient(String id, LocalDate birthDate, String SSN, String drivers, String first, String last, String gender,
            String birthPlace, String address, String city, String state, String county, String zip) {
        this.id = id;
        this.birthDate = birthDate;
        this.SSN = SSN;
        this.drivers = drivers;
        this.first = first;
        this.last = last;
        this.gender = gender;
        this.birthPlace = birthPlace;
        this.address = address;
        this.city = city;
        this.state = state;
        this.county = county;
        this.zip = zip;
    }
    
}
