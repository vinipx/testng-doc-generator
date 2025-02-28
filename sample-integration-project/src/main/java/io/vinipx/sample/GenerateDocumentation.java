package io.vinipx.sample;

import io.vinipx.testngdoc.SimpleTestNGDocGenerator;

/**
 * Sample class demonstrating how to use the TestNG Documentation Generator library.
 */
public class GenerateDocumentation {
    
    public static void main(String[] args) {
        // Path to the test classes
        String testClassesPath = "src/test/java/io/vinipx/sample/tests";
        
        // Generate documentation
        System.out.println("Generating TestNG documentation for: " + testClassesPath);
        SimpleTestNGDocGenerator.main(new String[]{testClassesPath});
        System.out.println("Documentation generation complete!");
    }
}
