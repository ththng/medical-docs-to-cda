package it.unisa.medical_docs_to_cda.CDALDO;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents an author in the CDALDO model.
 */
public class CDALDOAuthor {
    private CDALDOId id;
    private CDALDOId regionalId;
    private List<String> telecoms;
    private List<String> telecomUses;
    private String firstName;
    private String lastName;
    private String prefix;





   
    /**
     * Constructs a new CDALDOAuthor instance.
     *
     * @param id           the author's ID
     * @param regionalId   the author's regional ID
     * @param telecoms     the author's telecoms
     * @param telecomUses  the author's telecom uses
     * @param firstName    the author's first name
     * @param lastName     the author's last name
     * @param prefix       the author's prefix
     */
    public CDALDOAuthor(CDALDOId id, CDALDOId regionalId, List<String> telecoms, List<String> telecomUses, String firstName, String lastName, String prefix) {
        hasRequiredTelecoms(telecoms);
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.regionalId = regionalId;
        this.telecoms = Objects.requireNonNull(telecoms, "telecoms cannot be null");
        this.telecomUses = Objects.requireNonNull(telecomUses, "telecomUses cannot be null");
        this.firstName = Objects.requireNonNull(firstName, "firstName cannot be null");
        this.lastName = Objects.requireNonNull(lastName, "lastName cannot be null");
        this.prefix = prefix;
    }
    public boolean hasRequiredTelecoms(List<String> telecoms) {
        Objects.requireNonNull(telecoms, "telecoms cannot be null");

        boolean hasPhoneNumber = false;
        boolean hasEmail = false;
        boolean hasPEC = false;

        Pattern phonePattern = Pattern.compile("^tel:\\+?[0-9. ()-]{7,}$");
        Pattern emailPattern = Pattern.compile("^mailto:[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        Pattern pecPattern = Pattern.compile("^mailto:[A-Za-z0-9+_.-]+@pec\\.[A-Za-z0-9.-]+$");

        for (String telecom : telecoms) {
            if (!hasPhoneNumber && phonePattern.matcher(telecom).matches()) {
                hasPhoneNumber = true;
            }
            if (!hasEmail && emailPattern.matcher(telecom).matches()) {
                hasEmail = true;
            }
            if (!hasPEC && pecPattern.matcher(telecom).matches()) {
                hasPEC = true;
            }
            if (hasPhoneNumber && hasEmail && hasPEC) {
                return true;
            }
        }

        return false;
    }


    

    /**
     * Gets the author's ID.
     *
     * @return the author's ID
     */
    public CDALDOId getId() {
        return id;
    }

    /**
     * Sets the author's ID.
     *
     * @param id the author's ID
     */
    public void setId(CDALDOId id) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
    }

    /**
     * Gets the author's regional ID.
     *
     * @return the author's regional ID
     */
    public CDALDOId getRegionalId() {
        return regionalId;
    }

    /**
     * Sets the author's regional ID.
     *
     * @param regionalId the author's regional ID
     */
    public void setRegionalId(CDALDOId regionalId) {
        this.regionalId = Objects.requireNonNull(regionalId, "regionalId cannot be null");
    }

    /**
     * Gets the author's telecoms.
     *
     * @return the author's telecoms
     */
    public List<String> getTelecoms() {
        return telecoms;
    }

    /**
     * Sets the author's telecoms.
     *
     * @param telecoms the author's telecoms
     */
    public void setTelecoms(List<String> telecoms) {
        this.telecoms = Objects.requireNonNull(telecoms, "telecoms cannot be null");
    }

    /**
     * Gets the author's telecom uses.
     *
     * @return the author's telecom uses
     */
    public List<String> getTelecomUses() {
        return telecomUses;
    }

    /**
     * Sets the author's telecom uses.
     *
     * @param telecomUses the author's telecom uses
     */
    public void setTelecomUses(List<String> telecomUses) {
        this.telecomUses = Objects.requireNonNull(telecomUses, "telecomUses cannot be null");
    }

    /**
     * Gets the author's first name.
     *
     * @return the author's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the author's first name.
     *
     * @param firstName the author's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the author's last name.
     *
     * @return the author's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the author's last name.
     *
     * @param lastName the author's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the author's prefix.
     *
     * @return the author's prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the author's prefix.
     *
     * @param prefix the author's prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CDALDOAuthor that = (CDALDOAuthor) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(regionalId, that.regionalId) &&
                Objects.equals(telecoms, that.telecoms) &&
                Objects.equals(telecomUses, that.telecomUses) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(prefix, that.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, regionalId, telecoms, telecomUses, firstName, lastName, prefix);
    }

    @Override
    public String toString() {
        return "CDALDOAuthor{" +
                "id=" + id +
                ", regionalId=" + regionalId +
                ", telecoms=" + telecoms +
                ", telecomUses=" + telecomUses +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", prefix='" + prefix + '\'' +
                '}';
    }
    public boolean hasRegionalId() {
    return regionalId != null;
}
}