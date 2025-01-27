package it.unisa.medical_docs_to_cda.CDALDO;

import org.w3c.dom.*;

/**
 * Represents an entry in a CDA (Clinical Document Architecture) document.
 * 
 * <p>This interface defines a method for creating an entry within a specified
 * section of a CDA document. Implementations of this interface should provide
 * the logic to populate the given section element with the appropriate data.
 * 
 * @param doc the Document object representing the entire CDA document.
 * @param section the Element object representing the section within the CDA document
 *                where the entry should be created.
 */
public interface CDALDOEntry {

    public void createEntry(Document doc, Element section);


}
