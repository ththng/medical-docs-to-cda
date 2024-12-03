package it.unisa.medical_docs_to_cda.CDALDO;

/**
 * Represents an address in the CDALDO format.
 */
public class CDALDOAddr {
    private String use;
    private String country;
    private String state;
    private String city;
    private String county;
    private String censusTract;
    private String postalCode;
    private String street;

    /**
     * Default constructor.
     */
    public CDALDOAddr() {}

    /**
     * Constructor with all address fields.
     * 
     * @param use        the use of the address (e.g., "home", "work")
     * @param country    the country of the address
     * @param state      the state or province of the address
     * @param city       the city of the address
     * @param postalCode the postal code of the address
     * @param street     the street address
     */
    public CDALDOAddr(String use, String country, String state,String county, String city,String censusTract, String postalCode, String street) {
        this.use = use;
        this.country = country;
        this.state = state;
        this.county=county;
        this.city = city;
        this.censusTract=censusTract;
        this.postalCode = postalCode;
        this.street = street;
    }

    /**
     * Gets the use of the address.
     * 
     * @return the use of the address
     */
    public String getUse() {
        return use;
    }

    /**
     * Sets the use of the address.
     * 
     * @param use the use of the address
     */
    public void setUse(String use) {
        this.use = use;
    }

    /**
     * Gets the country of the address.
     * 
     * @return the country of the address
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country of the address.
     * 
     * @param country the country of the address
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the state or province of the address.
     * 
     * @return the state or province of the address
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state or province of the address.
     * 
     * @param state the state or province of the address
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the city of the address.
     * 
     * @return the city of the address
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city of the address.
     * 
     * @param city the city of the address
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the CensusTract of the address.
     * 
     * @return the CensusTract of the address
     */
    public String getCensusTract() {
        return censusTract;
    }

    /**
     * Sets the CensusTract of the address.
     * 
     * @param censusTract the CensusTract of the address
     */
    public void setCensusTract(String censusTract) {
        this.censusTract = censusTract;
    }
    /**
     * Gets the postal code of the address.
     * 
     * @return the postal code of the address
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postal code of the address.
     * 
     * @param postalCode the postal code of the address
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets the street address.
     * 
     * @return the street address
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the street address.
     * 
     * @param street the street address
     */
    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public String toString() {
        return "CDALDOAddr{" +
                "use='" + use + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", street='" + street + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CDALDOAddr that = (CDALDOAddr) o;

        if (use != null ? !use.equals(that.use) : that.use != null) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null) return false;
        return street != null ? street.equals(that.street) : that.street == null;
    }

    public String getCounty() {
        return county;
    }


    public void setCounty(String county) {
       this.county=county;
    }


}