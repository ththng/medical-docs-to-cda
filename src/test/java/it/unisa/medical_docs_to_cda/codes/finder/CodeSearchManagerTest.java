package it.unisa.medical_docs_to_cda.codes.finder;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CodeSearchManagerTest {


    // Singleton instance is created correctly on first getInstance() call
    @Test
    public void test_singleton_instance_creation() {
        CodeSearchManager instance1 = CodeSearchManager.getInstance();
        assertNotNull(instance1);
    
        CodeSearchManager instance2 = CodeSearchManager.getInstance();
        assertSame(instance1, instance2);
    }

    // Search for LOINC code related to body weight using searchLoincByName method
    @Test
    public void test_search_loinc_by_name_for_body_weight() {
        CodeSearchManager manager = CodeSearchManager.getInstance();
        String result =manager.searchLoincByName("Body weight");
        // Since the method prints results, we would need to capture the output stream to assert the results.
        // This is a placeholder for actual assertions based on expected output.
        System.out.println(result);
    }
    @Test
    public void test_search_icd9_by_name() {
        CodeSearchManager manager = CodeSearchManager.getInstance();
        String result = manager.searchICD9ByTerm("presenile dementia");
        // Since the method prints results, we would need to capture the output stream to assert the results.
        System.out.println(result);

    }

}