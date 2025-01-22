package it.unisa.medical_docs_to_cda.CDALDO;

public class CDALDOId {
    private String oid;
    private String extensionId;
    private String assigningAuthorityName;

    // Default constructor
    public CDALDOId() {
    }

    // Parameterized constructor
    public CDALDOId(String oid, String extensionId, String assigningAuthorityName) {
        this.oid = oid;
        this.extensionId = extensionId;
        this.assigningAuthorityName = assigningAuthorityName;
    }

    // Getter and Setter for oid
    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    // Getter and Setter for extensionId
    public String getExtensionId() {
        return extensionId;
    }

    public void setExtensionId(String extensionId) {
        this.extensionId = extensionId;
    }

    // Getter and Setter for assigningAuthorityName
    public String getAssigningAuthorityName() {
        return assigningAuthorityName;
    }

    public void setAssigningAuthorityName(String assigningAuthorityName) {
        this.assigningAuthorityName = assigningAuthorityName;
    }

    public void setRoot(String root) {
        this.oid = root;
    }
}
