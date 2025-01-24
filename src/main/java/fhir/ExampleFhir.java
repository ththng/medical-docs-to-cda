package fhir;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Patient;
import org.hl7.fhir.r5.model.Bundle.BundleEntryComponent;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class ExampleFhir {
    public static void createPatient() {
        // We're connecting to a R5 compliant server in this example
        FhirContext ctx = FhirContext.forR5();
        String serverBase = "https://hapi.fhir.org/baseR5";

        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

        Patient patient = new Patient();
        // ..populate the patient object..

        patient.addIdentifier().setSystem("urn:system").setValue("112233");
        patient.addName().setFamily("Alighieri").addGiven("Dante");

        // Invoke the server create method (and send pretty-printed JSON
        // encoding to the server
        // instead of the default which is non-pretty printed XML)
        MethodOutcome outcome = client.create()
                .resource(patient)
                .prettyPrint()
                .encodedJson()
                .execute();

        // The MethodOutcome object will contain information about the
        // response from the server, including the ID of the created
        // resource, the OperationOutcome response, etc. (assuming that
        // any of these things were provided by the server! They may not
        // always be)
        IIdType id = outcome.getId();
        System.out.println("Got ID: " + id.getValue());

        // Perform a search by family name
        Bundle response = client.search()
                .forResource(Patient.class)
                .where(Patient.FAMILY.matchesExactly().value("Alighieri"))
                .returnBundle(Bundle.class)
                .execute();

        System.out.println("Found " + response.getEntry().size() + " with last name Alighieri'");

        for (BundleEntryComponent c : response.getEntry()) {
            Patient p = (Patient) c.getResource();
            System.out.println("ID= " + p.getIdentifier().get(0).getValue() + "\t Name= "
                    + p.getName().get(0).getNameAsSingleString());
        }

    }
}