package it.unisa.medical_docs_to_cda.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The DocumentIdGenerator class provides a method to generate unique document IDs.
 * 
 * The generated ID consists of a specified prefix, the current date in 'yyyyMMdd' format,
 * and a sequence number that increments with each call.
 * 
 * This class uses an AtomicInteger to ensure thread-safe incrementation of the sequence number.
 */
public class DocumentIdGenerator {

    private static final AtomicInteger counter = new AtomicInteger(1);

    /**
     * Generates unique document IDs with a specified prefix, current date, and a
     * sequence number.
     * 
     * <p>
     * The ID format is: {prefix}-{currentDate}-{sequenceNumber}, where:
     * <ul>
     * <li>{prefix} is the provided prefix string.</li>
     * <li>{currentDate} is the current date in 'yyyyMMdd' format.</li>
     * <li>{sequenceNumber} is a zero-padded, incrementing integer starting from
     * 1.</li>
     * </ul>
     * 
     * @param prefix the prefix to be used in the document ID
     * @return a unique document ID string
     */
    public static String generateDocumentId(String prefix) {
        String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        int sequenceNumber = counter.getAndIncrement();

        return String.format("%s-%s-%04d", prefix, currentDate, sequenceNumber);
    }
}
