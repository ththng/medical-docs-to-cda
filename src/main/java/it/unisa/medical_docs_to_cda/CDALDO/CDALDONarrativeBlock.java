package it.unisa.medical_docs_to_cda.CDALDO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CDALDONarrativeBlock {

        private String narrativeType;
        private Object content;

        public CDALDONarrativeBlock(String narrativeType, Object content) {
            this.narrativeType = narrativeType;
            this.content = content;
        }
}
