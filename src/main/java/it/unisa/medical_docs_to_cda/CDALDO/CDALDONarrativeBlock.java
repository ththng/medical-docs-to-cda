package it.unisa.medical_docs_to_cda.CDALDO;

import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CDALDONarrativeBlock {

    private String section;
    private String narrativeType;
    private Object content;

    public CDALDONarrativeBlock(String narrativeType, Object content) {
        if (!Arrays.asList("paragraph", "list", "formatted_text").contains(narrativeType)) {
            throw new IllegalArgumentException("Invalid narrativeType");
        } else {
            this.narrativeType = narrativeType;
        }

        this.content = content;
    }

    public CDALDONarrativeBlock(String section, String narrativeType, Object content) {
        if (!Arrays.asList("anamnesi", "esameObiettivo", "terapiaFarmacologica").contains(section)) {
            throw new IllegalArgumentException("Invalid section");
        } else {
            this.section = section;
        }

        if (!Arrays.asList("paragraph", "list", "formatted_text").contains(narrativeType)) {
            throw new IllegalArgumentException("Invalid narrativeType");
        } else {
            this.narrativeType = narrativeType;
        }
        this.content = content;
    }

}
