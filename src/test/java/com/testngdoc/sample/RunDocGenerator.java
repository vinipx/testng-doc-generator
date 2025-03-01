package com.testngdoc.sample;

import io.vinipx.testngdoc.TestNGDocGenerator;

/**
 * Simple class to run the TestNGDocGenerator
 */
public class RunDocGenerator {
    
    public static void main(String[] args) {
        try {
            TestNGDocGenerator generator = new TestNGDocGenerator();
            
            // Generate documentation for all test classes
            generator.generateDocumentation(new Class<?>[] {
                TCPrefixTests.class,
                UnderscoreTests.class,
                GherkinStyleTests.class
            }, "testng-docs");
            
            System.out.println("Documentation generated successfully!");
        } catch (Exception e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
