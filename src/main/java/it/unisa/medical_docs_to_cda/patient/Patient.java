package it.unisa.medical_docs_to_cda.patient;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("Patient")
public class Patient {
    @Id
    private String first_name;
    private String last_name;
    private String disease;
    private LocalDate dob;
    private Integer years;

    
    public Patient() {
    }

    public Patient(String first_name,
                    String last_name,
                        String disease,
                            LocalDate dob,
                                Integer years) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.disease = disease;
        this.dob = dob;
        this.years = years;
    }
    public String getFirst_name() {
        return first_name;
    }
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
    public String getLast_name() {
        return last_name;
    }
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
    public String getDisease() {
        return disease;
    }
    public void setDisease(String disease) {
        this.disease = disease;
    }
    public LocalDate getDob() {
        return dob;
    }
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
    public Integer getYears() {
        return years;
    }
    public void setYears(Integer years) {
        this.years = years;
    }

    @Override
    public String toString() {
        return "Patient [first_name=" + first_name + ", last_name=" + last_name + ", disease=" + disease + ", dob="
                + dob + ", years=" + years + "]";
    }

}
