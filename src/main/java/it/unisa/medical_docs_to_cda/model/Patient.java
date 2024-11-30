package it.unisa.medical_docs_to_cda.model;

import java.time.LocalDate;

import org.springframework.boot.actuate.endpoint.annotation.FilteredEndpoint;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

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


    public String getId() {
        return id;
    }


    public LocalDate getBirthDate() {
        return birthDate;
    }


    public LocalDate getDeathDate() {
        return deathDate;
    }


    public String getSSN() {
        return SSN;
    }


    public String getDrivers() {
        return drivers;
    }


    public String getPassport() {
        return passport;
    }


    public String getFirst() {
        return first;
    }


    public String getLast() {
        return last;
    }


    public String getMaiden() {
        return maiden;
    }


    public String getGender() {
        return gender;
    }


    public String getBirthPlace() {
        return birthPlace;
    }


    public String getAddress() {
        return address;
    }


    public String getCity() {
        return city;
    }


    public String getState() {
        return state;
    }


    public String getCounty() {
        return county;
    }


    public String getZip() {
        return zip;
    }


    @Override
    public String toString() {
        return "Patient [id=" + id + ", birthDate=" + birthDate + ", deathDate=" + deathDate + ", SSN=" + SSN
                + ", drivers=" + drivers + ", passport=" + passport + ", first=" + first + ", last=" + last
                + ", maiden=" + maiden + ", gender=" + gender + ", birthPlace=" + birthPlace + ", address=" + address
                + ", city=" + city + ", state=" + state + ", county=" + county + ", zip=" + zip + "]";
    }
  
    
}
