package it.unisa.medical_docs_to_cda.controller;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class DocumentIdGenerator {

    private static final AtomicInteger counter = new AtomicInteger(1); 


    public static String generateDocumentId(String prefix) {
        // Ottieni la data odierna nel formato YYYYMMDD
        String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        // Ottieni il prossimo numero sequenziale
        int sequenceNumber = counter.getAndIncrement();

        // Formatta l'ID completo
        return String.format("%s-%s-%04d", prefix, currentDate, sequenceNumber);
    }
}
