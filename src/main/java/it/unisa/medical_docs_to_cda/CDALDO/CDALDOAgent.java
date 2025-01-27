package it.unisa.medical_docs_to_cda.CDALDO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Represents a CDALDOAgent for an entry with tag "act", with properties for
 * code, code system, code system
 * name, and display name.
 * <p>
 * This class uses Lombok annotations to automatically generate getter and
 * setter methods for its fields.
 * 
 */
public class CDALDOAgent {
    private String code;
    private String codeSystem;
    private String codeSystemName;
    private String displayName;

    public CDALDOAgent(String code, String codeSystem, String codeSystemName, String displayName) {
        this.code = code;
        this.codeSystem = codeSystem;
        this.codeSystemName = codeSystemName;
        this.displayName = displayName;
    }

}
