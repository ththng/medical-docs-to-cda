package it.unisa.medical_docs_to_cda.CDALDO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
