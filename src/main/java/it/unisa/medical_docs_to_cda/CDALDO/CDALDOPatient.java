package it.unisa.medical_docs_to_cda.CDALDO;

import java.time.LocalDate;
import java.util.List;

public class CDALDOPatient {
    private final List<CDALDOId> ids;
    private final int idNumber;
    private final List<CDALDOAddr> addresses;
    private final List<String> telecoms;
    private final List<String> telecomUses;
    private final String firstName;
    private final String lastName;
    private final String gender;
    private final String birthPlace;
    private final LocalDate birthDate;

    public CDALDOPatient(List<CDALDOId> ids, int idNumber, List<CDALDOAddr> addresses, List<String> telecoms, List<String> telecomUses, String firstName, String lastName, String gender, String birthPlace, LocalDate birthDate) {
        this.ids = ids;
        this.idNumber = idNumber;
        this.addresses = addresses;
        this.telecoms = telecoms;
        this.telecomUses = telecomUses;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthPlace = birthPlace;
        this.birthDate = birthDate;
    }

    public List<CDALDOId> getIds() {
        return ids;
    }

    public int getIdNumber() {
        return idNumber;
    }

    public List<CDALDOAddr> getAddresses() {
        return addresses;
    }


    public List<String> getTelecoms() {
        return telecoms;
    }

    public List<String> getTelecomUses() {
        return telecomUses;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public String toString() {
        return "CDALDOPatient{" +
                "ids=" + ids +
                ", idNumber=" + idNumber +
                ", addresses=" + addresses +
                ", telecoms=" + telecoms +
                ", telecomUses=" + telecomUses +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CDALDOPatient that = (CDALDOPatient) o;

        if (idNumber != that.idNumber) return false;
        if (!ids.equals(that.ids)) return false;
        if (!addresses.equals(that.addresses)) return false;
        if (!telecoms.equals(that.telecoms)) return false;
        if (!telecomUses.equals(that.telecomUses)) return false;
        if (!firstName.equals(that.firstName)) return false;
        if (!lastName.equals(that.lastName)) return false;
        if (!gender.equals(that.gender)) return false;
        if (!birthPlace.equals(that.birthPlace)) return false;
        return birthDate.equals(that.birthDate);
    }
}