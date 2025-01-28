package it.unisa.medical_docs_to_cda.codes.finder;

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.ValueSet;
import org.hl7.fhir.r5.model.ValueSet.ConceptSetComponent;
import org.hl7.fhir.r5.model.ValueSet.ConceptReferenceComponent;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;

/**
 * Utility class for finding codes in LOINC and ICD9 notations.
 */
public class CodeSearchManager {

    private static final String ICD9_API_URL = "https://clinicaltables.nlm.nih.gov/api/icd9cm_dx/v3/search";
    private static final String LOINC_SERVER_URL = "https://fhir.loinc.org";
    private static final String USERNAME = "********";// TODO inserire LOINC username e password
    private static final String PASSWORD = "********";

    private static CodeSearchManager instance;

    private CodeSearchManager() {
    }

    public static synchronized CodeSearchManager getInstance() {
        if (instance == null) {
            instance = new CodeSearchManager();
        }
        return instance;
    }

    public static String searchICD9ByTerm(String term) {
        Coding result = null;

        try {
            if (term != null && !term.isEmpty()) {
                String encodedTerm = URLEncoder.encode(term, "UTF-8");

                String apiUrl = ICD9_API_URL + "?terms=" + encodedTerm;
                URL url = new URL(apiUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    System.err.println("Errore nella richiesta HTTP. Codice di risposta: " + responseCode);
                    return null;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();

                JSONArray responseArray = new JSONArray(responseBuilder.toString());
                JSONArray codes = responseArray.getJSONArray(3);

                String shortestDescription = null;
                String shortestCode = null;

                for (int i = 0; i < codes.length(); i++) {
                    JSONArray codeEntry = codes.getJSONArray(i);
                    String code = codeEntry.getString(0);
                    String description = codeEntry.getString(1);

                    if (shortestDescription == null || description.length() < shortestDescription.length()) {
                        shortestDescription = description;
                        shortestCode = code;
                    }
                }

                if (shortestCode != null) {
                    result = new Coding();
                    result.setSystem("http://hl7.org/fhir/sid/icd-9-cm");
                    result.setCode(shortestCode);
                    result.setDisplay(shortestDescription);
                }
            }
        } catch (java.net.SocketTimeoutException e) {
            System.err.println("La connessione è scaduta. Verifica la disponibilità del server.");
        } catch (Exception e) {
            System.err.println("Errore durante l'esecuzione della richiesta: " + e.getMessage());
            e.printStackTrace();
        }
        if (result != null) {
            return result.getCode();
        } else
            return "unknown";
    }

    public String searchLoincByName(String name) {
        String shortestCode = null;
        try {

            FhirContext fhirContext = FhirContext.forR5();

            IGenericClient loincClient = fhirContext.newRestfulGenericClient(LOINC_SERVER_URL);

            BasicAuthInterceptor authInterceptor = new BasicAuthInterceptor(USERNAME, PASSWORD);
            loincClient.registerInterceptor(authInterceptor);

            Bundle results = loincClient.search()
                    .forResource(ValueSet.class)
                    .where(ValueSet.NAME.matches().value(name))
                    .returnBundle(Bundle.class)
                    .execute();

            results.getLink().forEach(link -> System.out.println("Link: " + link.getUrl()));
            System.out.println("Risultati trovati: " + results.getTotal());

            for (var entry : results.getEntry()) {
                if (entry.getResource() instanceof ValueSet) {
                    ValueSet valueSet = (ValueSet) entry.getResource();

                    int shortestName = Integer.MAX_VALUE;
                    for (ConceptSetComponent include : valueSet.getCompose().getInclude()) {
                        for (ConceptReferenceComponent concept : include.getConcept()) {
                            if (concept.getDisplay().length() < shortestName) {
                                shortestName = concept.getDisplay().length();
                                shortestCode = concept.getCode();
                                System.err.println(concept.getDisplay());
                            }

                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return shortestCode;
    }
}
